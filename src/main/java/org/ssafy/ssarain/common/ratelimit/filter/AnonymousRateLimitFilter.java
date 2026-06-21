package org.ssafy.ssarain.common.ratelimit.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.ssafy.ssarain.common.ratelimit.properties.RateLimitProperties;
import org.ssafy.ssarain.common.ratelimit.properties.RateLimitProperties.RateLimitScope;

import static org.ssafy.ssarain.common.ratelimit.constant.RateLimitConst.RATE_LIMIT_ANONYMOUS_PREFIX;

@Component
public class AnonymousRateLimitFilter extends AbstractRateLimitFilter {

    public AnonymousRateLimitFilter(
            RateLimitProperties rateLimitProperties,
            ProxyManager<String> rateLimitProxyManager,
            ObjectMapper objectMapper
    ) {
        super(rateLimitProperties, rateLimitProxyManager, objectMapper);
    }

    @Override
    protected RateLimitScope getScope() {
        return RateLimitScope.ANONYMOUS;
    }

    @Override
    protected String getKeyPrefix() {
        return RATE_LIMIT_ANONYMOUS_PREFIX;
    }

    @Override
    protected String resolveSubject(HttpServletRequest request) {
        return getClientIp(request);
    }

    private String getClientIp(HttpServletRequest request) {

        String forwardedFor = request.getHeader("X-Forwarded-For");
        if(forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        String realIp = request.getHeader("X-Real-IP");
        if(realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }

        return request.getRemoteAddr();
    }
}
