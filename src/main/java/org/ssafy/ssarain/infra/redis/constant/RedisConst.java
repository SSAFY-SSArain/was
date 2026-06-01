package org.ssafy.ssarain.infra.redis.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class RedisConst {

    public static final String JWT_REFRESH_TOKEN_PREFIX = "jwt:refresh:";

}
