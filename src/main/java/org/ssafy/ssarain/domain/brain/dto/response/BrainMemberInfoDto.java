package org.ssafy.ssarain.domain.brain.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.ssafy.ssarain.domain.brain.model.BrainMember;
import org.ssafy.ssarain.domain.brain.model.BrainMemberRole;
import org.ssafy.ssarain.domain.user.model.User;

import java.util.UUID;

public record BrainMemberInfoDto(
        @Schema(example = "123e4567-e89b-12d3-a456-426614174000")
        @JsonProperty("UUID")
        UUID uuid,

        @Schema(example = "홍길동")
        String name,

        @Schema(example = "email@email.com")
        String email,

        @Schema(example = "MANAGER")
        BrainMemberRole brainRole
) {

    public static BrainMemberInfoDto from(BrainMember brainMember) {
        User user = brainMember.getUser();
        return new BrainMemberInfoDto(
                user.getUid(),
                user.getName(),
                user.getEmail(),
                brainMember.getRole()
        );
    }
}
