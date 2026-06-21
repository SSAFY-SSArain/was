package org.ssafy.ssarain.common.ratelimit.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.ssafy.ssarain.common.ratelimit.properties.RateLimitProperties;
import org.ssafy.ssarain.common.ratelimit.properties.RateLimitProperties.RateLimitScope;
import org.ssafy.ssarain.common.security.model.CustomUserDetails;

import static org.ssafy.ssarain.common.ratelimit.constant.RateLimitConst.RATE_LIMIT_AUTHENTICATED_PREFIX;

@Component
public class AuthenticatedRateLimitFilter extends AbstractRateLimitFilter {

    public AuthenticatedRateLimitFilter(
            RateLimitProperties rateLimitProperties,
            ProxyManager<String> rateLimitProxyManager,
            ObjectMapper objectMapper
    ) {
        super(rateLimitProperties, rateLimitProxyManager, objectMapper);
    }

    @Override
    protected RateLimitScope getScope() {
        return RateLimitScope.AUTHENTICATED;
    }

    @Override
    protected String getKeyPrefix() {
        return RATE_LIMIT_AUTHENTICATED_PREFIX;
    }

    @Override
    protected String resolveSubject(HttpServletRequest request) {
        CustomUserDetails userDetails = getUserDetails();
        return userDetails == null ? null : userDetails.getUserId().toString();
    }

    private CustomUserDetails getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if(!(principal instanceof CustomUserDetails userDetails)) {
            return null;
        }

        return userDetails;
    }
}
