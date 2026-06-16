package org.ssafy.ssarain.domain.brain.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

public record BrainUserPageDto(
        @Schema(requiredMode = RequiredMode.REQUIRED)
        List<BrainUserInfoDto> users,
        
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

    public static BrainUserPageDto from(Page<BrainUserInfoDto> users) {
        if (users == null) {
            return new BrainUserPageDto(
                    List.of(),
                    0, 0, 0, 0, false);
        }
        return new BrainUserPageDto(
                users.getContent(),
                users.getNumber(),
                users.getSize(),
                users.getTotalElements(),
                users.getTotalPages(),
                users.hasNext());
    }
}
