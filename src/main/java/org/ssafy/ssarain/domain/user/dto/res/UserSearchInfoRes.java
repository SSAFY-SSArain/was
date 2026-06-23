package org.ssafy.ssarain.domain.user.dto.res;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.ssafy.ssarain.domain.user.model.User;

import java.util.UUID;

public record UserSearchInfoRes(
        @Schema(example = "123e4567-e89b-12d3-a456-426614174000")
        @JsonProperty("UUID")
        UUID uuid,

        @Schema(example = "홍길동")
        String name,

        @Schema(example = "email@email.com")
        String email
) {

    public static UserSearchInfoRes from(User user) {
        return new UserSearchInfoRes(user.getUid(), user.getName(), user.getEmail());
    }
}
