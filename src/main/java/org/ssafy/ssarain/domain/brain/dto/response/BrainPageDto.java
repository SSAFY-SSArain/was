package org.ssafy.ssarain.domain.brain.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

public record BrainPageDto<T>(
        @Schema(requiredMode = RequiredMode.REQUIRED)
        List<T> brains,
        
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "0")
        int currentPage,
        
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "10")
        int pageSize,
        
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "25")
        long totalElements,
        
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "3")
        int totalPages,
        
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "true")
        boolean hasNext
        ) {

    public static <T> BrainPageDto<T> from(Page<T> brainDtos) {
        if (brainDtos == null) {
            return new BrainPageDto<>(List.of(),
                    0, 0, 0, 0, false);
        }
        return new BrainPageDto<>(brainDtos.getContent(),
                brainDtos.getNumber(),
                brainDtos.getSize(),
                brainDtos.getTotalElements(),
                brainDtos.getTotalPages(),
                brainDtos.hasNext());
    }
}
