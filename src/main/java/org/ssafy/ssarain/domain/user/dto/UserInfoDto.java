package org.ssafy.ssarain.domain.user.dto;

import org.ssafy.ssarain.domain.user.model.User;
import org.ssafy.ssarain.domain.user.model.UserRole;

public record UserInfoDto(String email, String name, UserRole role) {

    public static UserInfoDto from(User user) {
        return new UserInfoDto(user.getEmail(), user.getName(), user.getRole());
    }

}
