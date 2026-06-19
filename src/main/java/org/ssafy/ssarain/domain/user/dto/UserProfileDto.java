package org.ssafy.ssarain.domain.user.dto;

import org.ssafy.ssarain.domain.user.model.User;
import org.ssafy.ssarain.domain.user.model.UserRole;

public record UserProfileDto(String email, String name, UserRole role) {

    public static UserProfileDto from(User user) {
        return new UserProfileDto(user.getEmail(), user.getName(), user.getRole());
    }

}
