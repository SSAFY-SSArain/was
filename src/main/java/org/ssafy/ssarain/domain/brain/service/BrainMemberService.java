package org.ssafy.ssarain.domain.brain.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ssafy.ssarain.common.error.GlobalException;
import org.ssafy.ssarain.common.response.ErrorCode;
import org.ssafy.ssarain.common.util.BatchProcessor;
import org.ssafy.ssarain.domain.brain.dao.BrainMemberRepository;
import org.ssafy.ssarain.domain.brain.dao.BrainRepository;
import org.ssafy.ssarain.domain.brain.dao.BrainWaitingRepository;
import org.ssafy.ssarain.domain.brain.dto.request.BrainJoinManageDto;
import org.ssafy.ssarain.domain.brain.dto.request.BrainMemberListDto;
import org.ssafy.ssarain.domain.brain.dto.request.BrainMemberPageDto;
import org.ssafy.ssarain.domain.brain.dto.request.BrainMemberRoleUpdateDto;
import org.ssafy.ssarain.domain.brain.dto.request.BrainMemberSearchDto;
import org.ssafy.ssarain.domain.brain.dto.response.BrainMemberInfoDto;
import org.ssafy.ssarain.domain.brain.dto.response.BrainMemberInfoPageDto;
import org.ssafy.ssarain.domain.brain.dto.response.BrainUserInfoDto;
import org.ssafy.ssarain.domain.brain.dto.response.BrainUserPageDto;
import org.ssafy.ssarain.domain.brain.model.Brain;
import org.ssafy.ssarain.domain.brain.model.BrainMember;
import org.ssafy.ssarain.domain.brain.model.BrainMemberRole;
import org.ssafy.ssarain.domain.brain.model.BrainWaiting;
import org.ssafy.ssarain.domain.brain.model.JoinStatus;
import org.ssafy.ssarain.domain.brain.model.JoinPolicy;
import org.ssafy.ssarain.domain.user.dao.UserRepository;
import org.ssafy.ssarain.domain.user.model.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrainMemberService {

    private final BrainRepository brainRepository;
    private final BrainMemberRepository brainMemberRepository;
    private final BrainWaitingRepository brainWaitingRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public JoinStatus getJoinStatus(int bid, UUID uid) {
        if (uid == null) {
            return JoinStatus.INACTIVE;
        }
        if (brainMemberRepository.existsById(new BrainMember.BrainMemberId(bid, uid))) {
            return JoinStatus.ACTIVE;
        }
        if (brainWaitingRepository.existsByBmidBidAndBmidUid(bid, uid)) {
            return JoinStatus.PENDING;
        }
        return JoinStatus.INACTIVE;
    }

    @Transactional
    public void requestJoin(int bid, UUID uid) {
        validateNotMember(bid, uid);
        Brain brain = getBrain(bid);
        User user = getUser(uid);
        
        if (brain.getJoinPolicy() == JoinPolicy.PROTECTED) {
            if (brainWaitingRepository.existsByBmidBidAndBmidUid(bid, uid)) {
                throw new GlobalException(ErrorCode.BRAIN_WAITING_ALREADY_EXISTS);
            }
            brainWaitingRepository.save(BrainWaiting.of(brain, user));
        } else if (brain.getJoinPolicy() == JoinPolicy.PUBLIC) {
            brainMemberRepository.save(BrainMember.of(brain, user));
            brainWaitingRepository.deleteById(new BrainWaiting.BrainWaitingId(bid, uid));
        } else {
            log.error("JoinPolicy.{}에 대한 가입 요청 처리 로직이 없습니다.", brain.getJoinPolicy());
            throw new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Transactional
    public void addBrainMembers(int bid, BrainMemberListDto dto) {
        validateBrainExists(bid);
        List<BrainMember> newBrainMembers = toNewBrainMembersOf(bid, dto.users());
        
        List<BrainWaiting.BrainWaitingId> joinRequests = dto.users().stream()
                .map(uid -> new BrainWaiting.BrainWaitingId(bid, uid))
                .toList();
        brainWaitingRepository.deleteAllByIdInBatch(joinRequests);
        
        brainMemberRepository.saveAll(newBrainMembers);
    }

    @Transactional
    public void deleteMembers(int bid, UUID uid, BrainMemberListDto dto) {
        validateBrainExists(bid);
        validateSelfDeletion(dto.users(), uid);

        List<BrainMember> members = getExistBrainMembersOf(bid, dto.users());
        BrainMemberRole requesterRole = brainMemberRepository.findRoleByBmidUidAndBmidBid(uid, bid);
        validateDeleteList(requesterRole, members);
        
        brainMemberRepository.deleteAllInBatch(members);
        resignAdmin(bid, dto.users());
    }

    @Transactional(readOnly = true)
    public BrainUserPageDto searchAvailableUsers(int bid, BrainMemberSearchDto dto) {
        validateBrainExists(bid);

        String keyword = dto.keyword() == null ? "" : dto.keyword().trim();
        Page<BrainUserInfoDto> users = userRepository.searchUsersAvailableForBrain(bid, keyword, dto.pageable())
                .map(BrainUserInfoDto::from);

        return BrainUserPageDto.from(users);
    }

    @Transactional(readOnly = true)
    public BrainUserPageDto getJoinRequests(BrainMemberPageDto dto, int bid) {
        validateBrainExists(bid);

        Page<BrainUserInfoDto> users = brainWaitingRepository.findByBmidBid(bid, dto.pageable())
                .map(brainWaiting -> BrainUserInfoDto.from(brainWaiting.getUser()));

        return BrainUserPageDto.from(users);
    }

    @Transactional
    public void manageJoinRequest(int bid, BrainJoinManageDto dto) {
        validateNotMember(bid, dto.user());
        BrainWaiting waiting = getBrainWaiting(bid, dto.user());

        if (dto.isAccept()) {
            brainMemberRepository.save(BrainMember.of(waiting));
        }
        
        brainWaitingRepository.delete(waiting);
    }

    @Transactional(readOnly = true)
    public BrainMemberInfoPageDto getBrainMembers(BrainMemberPageDto dto, int bid) {
        validateBrainExists(bid);
        
        Page<BrainMemberInfoDto> memberInfoDtos = brainMemberRepository.findByBmid_Bid(bid, dto.pageable())
                .map(BrainMemberInfoDto::from);
        return BrainMemberInfoPageDto.from(memberInfoDtos);
    }

    @Transactional
    public void updateMemberRole(int bid, UUID requesterUid, UUID targetUid, BrainMemberRoleUpdateDto dto) {
        validateBrainExists(bid);
        validateSelfRoleUpdate(requesterUid, targetUid);

        BrainMember targetMember = getBrainMember(bid, targetUid);
        validateTargetRoleChangeable(targetMember);

        if (dto.role() == BrainMemberRole.ADMIN) {
            transferAdminRole(bid, targetMember);
            return;
        }

        targetMember.changeRole(dto.role());
    }
    
    @Transactional
    public void leaveFromBrain(int bid, UUID uid) {
    	 BrainMember brainMember = getBrainMember(bid, uid);
    	 
    	 // JPA 영속성 컨텍스트 관리:
    	 // Brain 삭제를 위해서는 연관된 엔티티인 brainMember가 먼저 제거되어야 합니다. 
    	 brainMemberRepository.delete(brainMember);
    	 
    	 if (brainMember.getRole() == BrainMemberRole.ADMIN) {
    		 if (!resignAdmin(bid, List.of(uid))) {
                 brainRepository.deleteById(bid);
             }
    	 }
    }
    
    /*
        Util Method
     */

    private Brain getBrain(int bid) {
        return brainRepository.findById(bid)
                .orElseThrow(() -> new GlobalException(ErrorCode.BRAIN_NOT_FOUND));
    }
    
    private User getUser(UUID uid) {
        return userRepository.findByUidAndDeletedAtIsNull(uid)
                .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
    }
    
    private BrainWaiting getBrainWaiting(int bid, UUID uid) {
        return brainWaitingRepository.findByBmidBidAndBmidUid(bid, uid)
                .orElseThrow(() -> new GlobalException(ErrorCode.BRAIN_WAITING_NOT_FOUND));
    }

    private BrainMember getBrainMember(int bid, UUID uid) {
        return brainMemberRepository.findById(new BrainMember.BrainMemberId(bid, uid))
                .orElseThrow(() -> new GlobalException(ErrorCode.BRAIN_MEMBER_NOT_FOUND));
    }
    
    private List<BrainMember> getExistBrainMembersOf(int bid, List<UUID> uids) {
        List<BrainMember.BrainMemberId> ids = uids.stream()
                .distinct()
                .map(uid -> new BrainMember.BrainMemberId(bid, uid))
                .collect(Collectors.toList());

        List<BrainMember> members = brainMemberRepository.findAllById(ids);
        if (members.size() != ids.size()) {
            throw new GlobalException(ErrorCode.BRAIN_MEMBER_NOT_FOUND);
        }
        
        return members;
    }

    private List<BrainMember> toNewBrainMembersOf(int bid, List<UUID> uids) {
        uids = uids.stream().distinct().toList();
        validateUidsInBatches(uids);
        
        List<BrainMember> members = uids.stream()
                .map(uid -> BrainMember.of(
                        brainRepository.getReferenceById(bid), 
                        userRepository.getReferenceById(uid)))
                .toList();
        
        List<BrainMember.BrainMemberId> ids = members.stream()
                .map(BrainMember::getBmid)
                .toList();
        if (0 < brainMemberRepository.countAllByBmidIn(ids)) {
            throw new GlobalException(ErrorCode.BRAIN_MEMBER_DUPLICATED);
        }
        
        return members;
    }
    
    private boolean hasDeleteAuthority(BrainMemberRole deleter, BrainMemberRole member) {
        return (deleter == BrainMemberRole.ADMIN)
                || (deleter == BrainMemberRole.MANAGER && member == BrainMemberRole.USER);
    }
    
    private void validateUidsInBatches(List<UUID> uids) {
        BatchProcessor.process(uids, batch -> {
            if (batch.size() != userRepository.countAllByUidInAndDeletedAtIsNull(batch)) {
                throw new GlobalException(ErrorCode.USER_NOT_FOUND);
            }
        });
    }
    
    private void validateDeleteList(BrainMemberRole requesterRole, List<BrainMember> deleteList) {
        long validDeletionCount = deleteList.stream()
                .filter(member -> hasDeleteAuthority(requesterRole, member.getRole()))
                .count();
        
        if (deleteList.size() != validDeletionCount) {
            throw new GlobalException(ErrorCode.BRAIN_MEMBER_DELETION_DENIED);
        }
    }
    
    private void validateSelfDeletion(List<UUID> deleteList, UUID requester) {
        if (deleteList.contains(requester)) {
            throw new GlobalException(ErrorCode.BRAIN_MEMBER_CANNOT_DELETE_SELF);
        }
    }

    private void validateSelfRoleUpdate(UUID requester, UUID target) {
        if (requester.equals(target)) {
            throw new GlobalException(ErrorCode.BRAIN_MEMBER_ROLE_UPDATE_DENIED);
        }
    }

    private void validateTargetRoleChangeable(BrainMember brainMember) {
        if (brainMember.getRole() == BrainMemberRole.ADMIN) {
            throw new GlobalException(ErrorCode.BRAIN_MEMBER_ROLE_UPDATE_DENIED);
        }
    }

    private void transferAdminRole(int bid, BrainMember targetMember) {
        BrainMember adminMember = getAdminMember(bid);

        targetMember.changeRole(BrainMemberRole.ADMIN);
        adminMember.changeRole(BrainMemberRole.MANAGER);
    }

    private boolean resignAdmin(int bid, List<UUID> resigningUids) {
        Optional<BrainMember> successor = findOldestSuccessor(bid, BrainMemberRole.MANAGER, resigningUids)
                .or(() -> findOldestSuccessor(bid, BrainMemberRole.USER, resigningUids));

        if (successor.isPresent()) {
            successor.get().changeRole(BrainMemberRole.ADMIN);
            return true;
        }

        return false;
    }

    private BrainMember getAdminMember(int bid) {
        return brainMemberRepository.findByBmidBidAndRole(bid, BrainMemberRole.ADMIN)
                .orElseThrow(() -> new GlobalException(ErrorCode.BRAIN_MEMBER_NOT_FOUND));
    }

    private Optional<BrainMember> findOldestSuccessor(int bid, BrainMemberRole role, List<UUID> excludedUids) {
        return brainMemberRepository.findFirstByBmidBidAndRoleAndBmidUidNotInOrderByCreatedAtAsc(
                bid,
                role,
                excludedUids.stream().distinct().toList()
        );
    }

    private void validateBrainExists(int bid) {
        if (!brainRepository.existsById(bid)) {
            throw new GlobalException(ErrorCode.BRAIN_NOT_FOUND);
        }
    }

    private void validateNotMember(int bid, UUID uid) {
        if (brainMemberRepository.existsById(new BrainMember.BrainMemberId(bid, uid))) {
            throw new GlobalException(ErrorCode.BRAIN_MEMBER_DUPLICATED);
        }
    }
}
