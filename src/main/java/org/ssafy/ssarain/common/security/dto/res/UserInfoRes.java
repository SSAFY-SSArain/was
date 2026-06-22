package org.ssafy.ssarain.common.security.dto.res;

import org.ssafy.ssarain.domain.user.model.UserRole;

public record UserInfoRes(String email, String name, UserRole role) {
}
