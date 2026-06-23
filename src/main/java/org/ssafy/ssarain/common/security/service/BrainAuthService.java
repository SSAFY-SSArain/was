package org.ssafy.ssarain.common.security.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.ssafy.ssarain.common.error.GlobalException;
import org.ssafy.ssarain.common.response.ErrorCode;
import org.ssafy.ssarain.common.security.model.CustomUserDetails;
import org.ssafy.ssarain.domain.brain.dao.BrainMemberRepository;
import org.ssafy.ssarain.domain.brain.model.BrainMemberRole;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BrainAuthService {
    
    public static final List<BrainMemberRole> BRAIN_MANAGER = List.of(BrainMemberRole.ADMIN, BrainMemberRole.MANAGER);
    public static final List<BrainMemberRole> BRAIN_ADMIN = List.of(BrainMemberRole.ADMIN);

    private final BrainMemberRepository brainMemberRepository;
    private final AuthService           authService;
    
    public void authorizeAnyBrainRole(CustomUserDetails userDetails, List<BrainMemberRole> roles) {
        // ADMIN 권한은 항상 허용
        if (authService.isAdmin(userDetails)) {
            return;
        }

        // 1개 이상의 Brain Role을 가지는지 확인
        if (hasAnyBrainRole(userDetails.getUserId(), roles)) {
            return;
        }
        
        throw new GlobalException(ErrorCode.ACCESS_DENIED);
    }
    
    public void authorizeBrainRoleOf(CustomUserDetails userDetails, int bid, List<BrainMemberRole> roles) {
        // ADMIN 권한은 항상 허용
        if (authService.isAdmin(userDetails)) {
            return;
        }

        // 특정 Brain에서 Role을 가지는지 확인
        if (hasBrainRoleOf(userDetails.getUserId(), bid, roles)) {
            return;
        }
        
        throw new GlobalException(ErrorCode.ACCESS_DENIED);
    }

    public void authorizeBrainTopicRoleOf(CustomUserDetails userDetails, int btid, List<BrainMemberRole> roles) {
        // ADMIN 권한은 항상 허용
        if (authService.isAdmin(userDetails)) {
            return;
        }

        // BrainTopic이 속한 Brain에서 Role을 가지는지 확인
        if (hasBrainTopicRoleOf(userDetails.getUserId(), btid, roles)) {
            return;
        }
        
        throw new GlobalException(ErrorCode.ACCESS_DENIED);
    }

    public void authorizeBrainMemberByBtid(CustomUserDetails userDetails, int btid) {

        // ADMIN 권한은 항상 허용
        if (authService.isAdmin(userDetails)) {
            return;
        }

        // 특정 Brain의 Member인지 확인
        if (isBrainMemberByBtid(userDetails.getUserId(), btid)) {
            return;
        }

        throw new GlobalException(ErrorCode.ACCESS_DENIED);
    }

    /*
        Util Mehtod
     */


    private boolean hasAnyBrainRole(UUID uid, List<BrainMemberRole> roles) {
        return brainMemberRepository.existsByBmidUidAndRoleIn(uid, roles);
    }
    
    private boolean hasBrainTopicRoleOf(UUID uid, int btid, List<BrainMemberRole> roles) {
        return brainMemberRepository.existsByUidAndBtidAndRoleIn(uid, btid, roles);
    }
    
    private boolean hasBrainRoleOf(UUID uid, int bid, List<BrainMemberRole> roles) {
        return brainMemberRepository.existsByBmidUidAndBmidBidAndRoleIn(uid, bid, roles);
    }

    private boolean isBrainMemberByBtid(UUID uid, int bid) {
        return brainMemberRepository.existsByUidAndBtid(uid, bid);
    }
}
