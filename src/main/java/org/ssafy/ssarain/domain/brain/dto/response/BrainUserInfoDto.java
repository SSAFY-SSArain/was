package org.ssafy.ssarain.domain.brain.dto.response;

import java.util.UUID;

import org.ssafy.ssarain.domain.user.model.User;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

public record BrainUserInfoDto(
        @Schema(example = "123e4567-e89b-12d3-a456-426614174000")
        @JsonProperty("UUID")
        UUID uuid,
        
        @Schema(example = "홍길동")
        String name,
        
        @Schema(example = "email@email.com")
        String email
) {

    public static BrainUserInfoDto from(User user) {
        return new BrainUserInfoDto(user.getUid(), user.getName(), user.getEmail());
    }
}
