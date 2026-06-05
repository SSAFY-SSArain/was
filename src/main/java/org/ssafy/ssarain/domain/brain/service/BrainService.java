package org.ssafy.ssarain.domain.brain.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ssafy.ssarain.common.error.GlobalException;
import org.ssafy.ssarain.common.response.ErrorCode;
import org.ssafy.ssarain.domain.brain.dao.BrainManagerRepository;
import org.ssafy.ssarain.domain.brain.dao.BrainMemberRepository;
import org.ssafy.ssarain.domain.brain.dao.BrainRepository;
import org.ssafy.ssarain.domain.brain.dto.request.BrainCreateDto;
import org.ssafy.ssarain.domain.brain.dto.request.BrainSearchDto;
import org.ssafy.ssarain.domain.brain.dto.response.BrainDetailDto;
import org.ssafy.ssarain.domain.brain.dto.response.BrainFoundDto;
import org.ssafy.ssarain.domain.brain.dto.response.BrainInfoDto;
import org.ssafy.ssarain.domain.brain.dto.response.BrainListDto;
import org.ssafy.ssarain.domain.brain.dto.response.BrainPageDto;
import org.ssafy.ssarain.domain.brain.model.Brain;
import org.ssafy.ssarain.domain.brain.model.BrainManager;
import org.ssafy.ssarain.domain.brain.model.BrainMember;
import org.ssafy.ssarain.domain.user.dao.UserRepository;
import org.ssafy.ssarain.domain.user.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BrainService {
    
    private final UserRepository userRepository;
    private final BrainRepository brainRepository;
    private final BrainManagerRepository brainManagerRepository;
    private final BrainMemberRepository brainMemberRepository;
    
    public BrainListDto<BrainInfoDto> getBrainInfos(UUID uid) {
        List<BrainInfoDto> brains = brainMemberRepository.findByBmid_Uid(uid).stream()
                .map(brainMember -> BrainInfoDto.from(brainMember.getBrain()))
                .toList();
        return BrainListDto.from(brains);
    }
    
    @Transactional(readOnly = true)
    public BrainPageDto<BrainFoundDto> searchBrain(BrainSearchDto dto) {
        Pageable pageable = dto.pageable();
        String name = dto.name();
        Page<Brain> brains = (name == null || name.isBlank())
                ? brainRepository.findAll(pageable)
                : brainRepository.findByNameContaining(name.trim(), pageable);
        
        Page<BrainFoundDto> brainDtos = brains
                .map(brain -> BrainFoundDto.from(
                        brain,
                        brain.getBrainManager().getUser().getName(),
                        brain.getBrainMembers().stream()
                                .map(brainMember -> brainMember.getUser().getName())
                                .toList()));
        
        return BrainPageDto.from(brainDtos);
    }
    
    @Transactional
    public BrainDetailDto createBrain(BrainCreateDto dto, UUID uid) {
        validateCreateDto(dto);
        
        Brain newBrain = null;
        if (dto.description() == null) {
            newBrain = Brain.of(dto.name(), "", dto.joinPolicy());
        } else {
            newBrain = Brain.of(dto.name(), dto.description(), dto.joinPolicy());
        }
        newBrain = brainRepository.save(newBrain);
        
        // 외래키 저장에만 활용하므로 프록시 User객체를 사용합니다.
        User brainAdmin = userRepository.getReferenceById(uid);
        
        BrainManager brainManager = BrainManager.of(newBrain, brainAdmin);
        brainManagerRepository.save(brainManager);
        
        BrainMember brainMember = BrainMember.of(newBrain, brainAdmin);
        brainMemberRepository.save(brainMember);
        
        return BrainDetailDto.from(newBrain);
    }
    
    /*
        Util Method
     */
    
    private void validateCreateDto(BrainCreateDto dto) {
        if (dto == null || dto.name() == null) {
            throw new GlobalException(ErrorCode.BAD_REQUEST);
        }
        validateDuplicateName(dto.name());
    }
    
    private void validateDuplicateName(String name) {
        if (brainRepository.existsByName(name)) {
            throw new GlobalException(ErrorCode.BRAIN_NAME_DUPLICATED);
        }
    }
}
