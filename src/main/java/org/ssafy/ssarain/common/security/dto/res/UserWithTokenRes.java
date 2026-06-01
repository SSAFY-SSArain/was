package org.ssafy.ssarain.common.security.dto.res;

public record UserWithTokenRes(TokenRes tokenRes, UserInfoRes userInfo
) {


}
