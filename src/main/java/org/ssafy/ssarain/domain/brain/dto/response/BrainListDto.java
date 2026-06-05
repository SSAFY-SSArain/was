package org.ssafy.ssarain.domain.brain.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

public record BrainListDto<T>(
        @Schema(requiredMode = RequiredMode.REQUIRED)
        List<T> brains
        ) {

    public static <T> BrainListDto<T> from(List<T> brainDtos) {
        if (brainDtos == null) {
            brainDtos = List.of();
        }
        return new BrainListDto<>(brainDtos);
    }
}
