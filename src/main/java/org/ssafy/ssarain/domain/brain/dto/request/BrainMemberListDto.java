package org.ssafy.ssarain.domain.brain.dto.request;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record BrainMemberListDto(
        @Schema(requiredMode = RequiredMode.REQUIRED,
                example = "[\"123e4567-e89b-12d3-a456-426614174000\", \"123e4567-e89b-12d3-a456-426614174001\"]")
        @NotEmpty
        List<@NotNull UUID> users
) {
}
