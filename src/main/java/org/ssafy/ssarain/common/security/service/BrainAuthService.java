package org.ssafy.ssarain.common.security.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.ssafy.ssarain.common.error.GlobalException;
import org.ssafy.ssarain.common.response.ErrorCode;
import org.ssafy.ssarain.common.security.model.CustomUserDetails;
import org.ssafy.ssarain.domain.brain.dao.BrainMemberRepository;
import org.ssafy.ssarain.domain.user.model.UserRole;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BrainAuthService {

    private final BrainMemberRepository brainMemberRepository;
    
    public void authorizeAnyBrainAdmin(CustomUserDetails userDetails) {
        // ADMIN 권한은 항상 허용
        if (isAdmin(userDetails)) {
            return;
        }

        // 1개 이상의 Brain Admin인지 확인
        if (isAnyBrainAdmin(userDetails.getUserId())) {
            return;
        }
        
        throw new GlobalException(ErrorCode.ACCESS_DENIED);
    }
    
    public void authorizeBrainAdminOf(CustomUserDetails userDetails, int bid) {
        // ADMIN 권한은 항상 허용
        if (isAdmin(userDetails)) {
            return;
        }

        // 특정 Brain의 Admin인지 확인
        if (isBrainAdminOf(userDetails.getUserId(), bid)) {
            return;
        }
        
        throw new GlobalException(ErrorCode.ACCESS_DENIED);
    }

    public void authorizeBrainMemberByBtid(CustomUserDetails userDetails, int btid) {

        // ADMIN 권한은 항상 허용
        if (isAdmin(userDetails)) {
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
    
    private boolean isAdmin(CustomUserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .anyMatch(authority -> UserRole.ADMIN.getAuthority().equals(authority.getAuthority()));
    }

    private boolean isAnyBrainAdmin(UUID uid) {
        return brainMemberRepository.existsByBmidUidAndRoleIn(uid, roles);
    }
    
    private boolean isBrainAdminOf(UUID uid, int bid) {
        return brainMemberRepository.existsByBmidUidAndBmidBidAndRoleIn(uid, bid, roles);
    }

    private boolean isBrainMemberByBtid(UUID uid, int bid) {
        return brainMemberRepository.existsByUidAndBtid(uid, bid);
    }
}
