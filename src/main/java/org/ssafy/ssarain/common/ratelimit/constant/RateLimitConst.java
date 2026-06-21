package org.ssafy.ssarain.common.ratelimit.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RateLimitConst {

    public static final String RATE_LIMIT_ANONYMOUS_PREFIX     = "rate-limit:anonymous:";
    public static final String RATE_LIMIT_AUTHENTICATED_PREFIX = "rate-limit:authenticated:";
}
