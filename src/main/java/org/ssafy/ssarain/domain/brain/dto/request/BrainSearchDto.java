package org.ssafy.ssarain.domain.brain.dto.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Size;

public record BrainSearchDto(
        @Schema(requiredMode = RequiredMode.NOT_REQUIRED, example = "구미")
        @Size(max = 50)
        String name,
        
        @Schema(requiredMode = RequiredMode.NOT_REQUIRED, example = "0")
        Integer page,
        
        @Schema(requiredMode = RequiredMode.NOT_REQUIRED, example = "5")
        Integer size
        ) {
    
    public Pageable pageable() {
        int p = (page == null || page < 0) ? 0 : page;
        int s = (size == null || size <= 0) ? 9 : size;
        return PageRequest.of(p, s, Sort.by("createdAt").descending());
    }
}
