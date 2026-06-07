package org.ssafy.ssarain.common.security.config.authorize;

import java.util.function.Supplier;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.ssafy.ssarain.common.security.model.CustomUserDetails;
import org.ssafy.ssarain.domain.user.model.UserRole;
import org.ssafy.ssarain.domain.brain.service.BrainAdminService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AnyBrainAdminAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final BrainAdminService brainAdminService;
    
    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
        Authentication auth = authentication.get();
        
        // 인증되지 않은 사용자 거부
        if (auth == null || !auth.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }
        
        // ADMIN 권한은 항상 허용
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(authority -> UserRole.ADMIN.getAuthority().equals(authority.getAuthority()));
        if (isAdmin) {
            return new AuthorizationDecision(true);
        }

        // 1개 이상의 Brain Admin인지 확인
        CustomUserDetails userDetails = (CustomUserDetails)auth.getPrincipal();
        boolean isAnyBrainAdmin = brainAdminService.isAnyBrainAdmin(userDetails.getUserId());
        return new AuthorizationDecision(isAnyBrainAdmin);
    }
    
}
