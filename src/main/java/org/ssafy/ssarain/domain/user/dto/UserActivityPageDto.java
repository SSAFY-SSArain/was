package org.ssafy.ssarain.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import org.springframework.data.domain.Page;

import java.util.List;

public record UserActivityPageDto<T>(
        @Schema(requiredMode = RequiredMode.REQUIRED)
        List<T> activities,

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

    public static <T> UserActivityPageDto<T> from(Page<T> activities) {
        if (activities == null) {
            return new UserActivityPageDto<>(List.of(), 0, 0, 0, 0, false);
        }

        return new UserActivityPageDto<>(
                activities.getContent(),
                activities.getNumber(),
                activities.getSize(),
                activities.getTotalElements(),
                activities.getTotalPages(),
                activities.hasNext()
        );
    }
}
