package org.ssafy.ssarain.domain.brain.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record BrainMergeDto(
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "SSAFY 15기 지식모임")
        @NotBlank
        String name,
        
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "SSAFY 15기 브레인의 집합체입니다.")
        @NotBlank
        String description,
        
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "[ 1, 2, 3 ]")
        @NotEmpty
        List<@NotNull Integer> brains) {

}
