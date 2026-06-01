package org.ssafy.ssarain.common.security.dto.res;

public record TokenRes(String accessToken,
                       Long   accessTokenExpireTime,
                       String refreshToken,
                       Long   refreshTokenExpireTime) {
}
