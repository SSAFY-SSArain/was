package org.ssafy.ssarain.domain.brain.dto.request;

import org.ssafy.ssarain.domain.brain.model.BrainMemberRole;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;

public record BrainMemberRoleUpdateDto(
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "MANAGER")
        @NotNull
        BrainMemberRole role
) {
}
