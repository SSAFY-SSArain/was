package org.ssafy.ssarain.common.security.service;

import org.springframework.stereotype.Service;
import org.ssafy.ssarain.common.error.GlobalException;
import org.ssafy.ssarain.common.response.ErrorCode;
import org.ssafy.ssarain.common.security.model.CustomUserDetails;
import org.ssafy.ssarain.domain.brain.service.BrainAdminService;
import org.ssafy.ssarain.domain.brain.service.BrainMemberService;
import org.ssafy.ssarain.domain.user.model.UserRole;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BrainAuthService {

    private final BrainAdminService  brainAdminService;
    private final BrainMemberService brainMemberService;
    
    public void authorizeAnyBrainAdmin(CustomUserDetails userDetails) {
        // ADMIN 권한은 항상 허용
        if (isAdmin(userDetails)) {
            return;
        }

        // 1개 이상의 Brain Admin인지 확인
        boolean isAnyBrainAdmin = brainAdminService.isAnyBrainAdmin(userDetails.getUserId());
        if (isAnyBrainAdmin) {
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
        boolean isBrainAdminOf = brainAdminService.isBrainAdminOf(userDetails.getUserId(), bid);
        if (isBrainAdminOf) {
            return;
        }
        
        throw new GlobalException(ErrorCode.ACCESS_DENIED);
    }

    public void authorizeBrainMember(CustomUserDetails userDetails, int bid) {

        // ADMIN 권한은 항상 허용
        if (isAdmin(userDetails)) {
            return;
        }

        // 특정 Brain의 Member인지 확인
        if (brainMemberService.isBrainMember(userDetails.getUserId(), bid)) {
            return;
        }

        // 특정 Brain의 Admin인지 확인
        if(brainAdminService.isBrainAdminOf(userDetails.getUserId(), bid)) {
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
}
