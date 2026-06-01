package org.ssafy.ssarain.common.security.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class SecurityConst {

    public static final String JWT_ACCESS_TOKEN_PREFIX = "Bearer ";
    public static final String JWT_ACCESS_TOKEN_HEADER = "Authorization";

    public static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    public static final String JWT_USERNAME_KEY    = "username";
    public static final String JWT_AUTHORITIES_KEY = "authorities";

    public static final String REFRESH_TOKEN_PATH = "/api/v1/auth/refresh";

}
