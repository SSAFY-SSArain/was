package org.ssafy.ssarain.domain.brain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public record BrainCreateDto(
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "구미 3반")
        @NotBlank
        String name,
        
        @Schema(requiredMode = RequiredMode.NOT_REQUIRED, example = "구미 3반 지식저장소")
        String description,
        
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "false")
        boolean joinPolicy
        ) {

}
