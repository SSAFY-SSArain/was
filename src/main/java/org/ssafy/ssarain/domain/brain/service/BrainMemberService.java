package org.ssafy.ssarain.domain.brain.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ssafy.ssarain.common.error.GlobalException;
import org.ssafy.ssarain.common.response.ErrorCode;
import org.ssafy.ssarain.domain.brain.dao.BrainMemberRepository;
import org.ssafy.ssarain.domain.brain.dao.BrainRepository;
import org.ssafy.ssarain.domain.brain.dao.BrainWaitingRepository;
import org.ssafy.ssarain.domain.brain.dto.request.BrainJoinManageDto;
import org.ssafy.ssarain.domain.brain.dto.request.BrainMemberDeleteDto;
import org.ssafy.ssarain.domain.brain.dto.response.BrainUserInfoDto;
import org.ssafy.ssarain.domain.brain.dto.response.BrainUserListDto;
import org.ssafy.ssarain.domain.brain.model.Brain;
import org.ssafy.ssarain.domain.brain.model.BrainMember;
import org.ssafy.ssarain.domain.brain.model.BrainWaiting;
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
        } else {
            log.error("JoinPolicy.{}에 대한 가입 요청 처리 로직이 없습니다.", brain.getJoinPolicy());
            throw new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void deleteMembers(int bid, UUID uid, BrainMemberDeleteDto dto) {
        validateBrainExists(bid);
        validateDeleteList(uid, dto.users());

        List<BrainMember.BrainMemberId> ids = getBrainMemberIdsOf(bid, dto.users());
        brainMemberRepository.deleteAllByIdInBatch(ids);
    }

    @Transactional(readOnly = true)
    public BrainUserListDto searchAvailableUsers(int bid, String search) {
        validateBrainExists(bid);

        String keyword = search == null ? "" : search.trim();
        List<BrainUserInfoDto> users = userRepository.searchUsersAvailableForBrain(bid, keyword).stream()
                .map(BrainUserInfoDto::from)
                .toList();

        return BrainUserListDto.from(users);
    }

    @Transactional(readOnly = true)
    public BrainUserListDto getJoinRequests(int bid) {
        validateBrainExists(bid);

        List<BrainUserInfoDto> users = brainWaitingRepository.findByBmidBid(bid).stream()
                .map(brainWaiting -> BrainUserInfoDto.from(brainWaiting.getUser()))
                .toList();

        return BrainUserListDto.from(users);
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

    public boolean isBrainMemberByBtid(UUID uid, int bid) {
        return brainMemberRepository.existsByUidAndBtid(uid, bid);
    }
    
    /*
        Util Method
     */

    private Brain getBrain(int bid) {
        return brainRepository.findById(bid)
                .orElseThrow(() -> new GlobalException(ErrorCode.BRAIN_NOT_FOUND));
    }
    
    private User getUser(UUID uid) {
        return userRepository.findByUid(uid)
                .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
    }
    
    private BrainWaiting getBrainWaiting(int bid, UUID uid) {
        return brainWaitingRepository.findByBmidBidAndBmidUid(bid, uid)
                .orElseThrow(() -> new GlobalException(ErrorCode.BRAIN_WAITING_NOT_FOUND));
    }
    
    private List<BrainMember.BrainMemberId> getBrainMemberIdsOf(int bid, List<UUID> uids) {
        List<BrainMember.BrainMemberId> ids = uids.stream()
                .distinct()
                .map(uid -> new BrainMember.BrainMemberId(bid, uid))
                .collect(Collectors.toList());

        if (brainMemberRepository.countAllByBmidIn(ids) != ids.size()) {
            throw new GlobalException(ErrorCode.BRAIN_MEMBER_NOT_FOUND);
        }
        
        return ids;
    }
    
    private void validateDeleteList(UUID requester, List<UUID> deleteList) {
        if (deleteList.contains(requester)) {
            throw new GlobalException(ErrorCode.BRAIN_MEMBER_CANNOT_DELETE_SELF);
        }
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
