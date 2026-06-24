package org.ssafy.ssarain.domain.brain.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ssafy.ssarain.common.error.GlobalException;
import org.ssafy.ssarain.common.response.ErrorCode;
import org.ssafy.ssarain.domain.brain.dao.BrainMemberRepository;
import org.ssafy.ssarain.domain.brain.dao.BrainRepository;
import org.ssafy.ssarain.domain.brain.dto.request.BrainCreateDto;
import org.ssafy.ssarain.domain.brain.dto.request.BrainSearchDto;
import org.ssafy.ssarain.domain.brain.dto.request.BrainUpdateDto;
import org.ssafy.ssarain.domain.brain.dto.response.BrainDetailDto;
import org.ssafy.ssarain.domain.brain.dto.response.BrainFoundDto;
import org.ssafy.ssarain.domain.brain.dto.response.BrainInfoDto;
import org.ssafy.ssarain.domain.brain.dto.response.BrainListDto;
import org.ssafy.ssarain.domain.brain.dto.response.BrainNameVaildationDto;
import org.ssafy.ssarain.domain.brain.dto.response.BrainPageDto;
import org.ssafy.ssarain.domain.brain.model.Brain;
import org.ssafy.ssarain.domain.brain.model.BrainMember;
import org.ssafy.ssarain.domain.brain.model.BrainMemberRole;
import org.ssafy.ssarain.domain.brain.model.JoinStatus;
import org.ssafy.ssarain.domain.user.dao.UserRepository;
import org.ssafy.ssarain.domain.user.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BrainService {
    
    private final UserRepository userRepository;
    private final BrainRepository brainRepository;
    private final BrainMemberRepository brainMemberRepository;
    private final BrainMemberService brainMemberService;
    
    @Transactional(readOnly = true)
    public BrainListDto<BrainInfoDto> getBrainInfos(UUID uid) {
        List<BrainInfoDto> brains = brainMemberRepository.findByBmid_Uid(uid).stream()
                .map(BrainInfoDto::from)
                .toList();
        return BrainListDto.from(brains);
    }
    
    @Transactional(readOnly = true)
    public BrainPageDto<BrainFoundDto> searchBrain(BrainSearchDto dto, UUID uid) {
        Page<Brain> brains = findBrains(dto, uid);
        Page<BrainFoundDto> brainDtos = brains.map(brain -> toBrainFoundDto(brain, uid));
        
        return BrainPageDto.from(brainDtos);
    }
    
    @Transactional
    public BrainDetailDto createBrain(BrainCreateDto dto, UUID uid) {
        validateDuplicateName(dto.name());
        
        Brain brain = createAndSaveBrain(dto);
        User brainAdmin = userRepository.getReferenceById(uid);
        brainMemberRepository.save(BrainMember.adminOf(brain, brainAdmin));
        
        return BrainDetailDto.from(brain);
    }

    @Transactional
    public BrainInfoDto updateBrain(int bid, BrainUpdateDto dto) {
        Brain brain = findBrain(bid);
        if (dto.name() != null) {
            validateDuplicateName(dto.name());
        }

        brain.update(dto);

    	return BrainInfoDto.from(brain);
    }

    @Transactional(readOnly = true)
    public BrainInfoDto getBrainInfo(int bid) {
    	Brain brain = findBrain(bid);
    	return BrainInfoDto.from(brain);
    }

    @Transactional
    public void deleteBrain(int bid) {
        validateBrainExists(bid);
        brainRepository.deleteById(bid);
    }

    @Transactional(readOnly = true)
    public BrainNameVaildationDto checkBrainName(String name) {
        return new BrainNameVaildationDto(brainRepository.existsByName(name));
    }
    
    /*
        Util Method
     */
    
    private void validateDuplicateName(String name) {
        if (brainRepository.existsByName(name)) {
            throw new GlobalException(ErrorCode.BRAIN_NAME_DUPLICATED);
        }
    }

    private void validateBrainExists(int bid) {
        if (!brainRepository.existsById(bid)) {
            throw new GlobalException(ErrorCode.BRAIN_NOT_FOUND);
        }
    }

    private Brain createAndSaveBrain(BrainCreateDto dto) {
        Brain newBrain = Brain.of(dto.name(), dto.description(), dto.joinPolicy());
        return brainRepository.save(newBrain);
    }
    
    private Brain findBrain(int bid) {
    	return brainRepository.findById(bid)
    			.orElseThrow(() -> new GlobalException(ErrorCode.BRAIN_NOT_FOUND));
    }

    private Page<Brain> findBrains(BrainSearchDto dto, UUID uid) {
        String name = dto.name() == null ? null : dto.name().trim();
        return brainRepository.searchBrains(name, uid, dto.shouldIncludeJoined(), dto.pageable());
    }
    
    private BrainFoundDto toBrainFoundDto(Brain brain, UUID uid) {
        List<BrainMember> members = brain.getBrainMembers();
        String adminName = members.stream()
                .filter(member -> member.getRole().equals(BrainMemberRole.ADMIN)
                        || member.getRole().equals(BrainMemberRole.MANAGER))
                .sorted((m1, m2) -> (m1.getRole() == BrainMemberRole.ADMIN) ? 1 : 0)
                .findFirst()
                .map(member -> member.getUser().getName())
                .orElse("");
        List<String> memberNames = members.stream()
                .map(brainMember -> brainMember.getUser().getName())
                .toList();
        JoinStatus joinStatus = brainMemberService.getJoinStatus(brain.getBid(), uid);
        
        return BrainFoundDto.from(brain, adminName, joinStatus, memberNames);
    }
}
