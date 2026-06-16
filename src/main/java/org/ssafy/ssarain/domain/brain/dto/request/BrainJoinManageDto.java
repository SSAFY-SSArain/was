package org.ssafy.ssarain.domain.brain.dto.request;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;

public record BrainJoinManageDto(
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "true")
        @JsonProperty("isAccept")
        @NotNull
        Boolean isAccept,

        @Schema(requiredMode = RequiredMode.REQUIRED, example = "123e4567-e89b-12d3-a456-426614174000")
        @NotNull
        UUID user
) {
}
