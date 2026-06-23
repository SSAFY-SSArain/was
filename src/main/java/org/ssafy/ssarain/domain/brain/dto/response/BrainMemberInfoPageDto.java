package org.ssafy.ssarain.domain.brain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

public record BrainMemberInfoPageDto(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<BrainMemberInfoDto> users,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
        int currentPage,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
        int pageSize,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "25")
        long totalElements,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "3")
        int totalPages,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
        boolean hasNext
) {

    public static BrainMemberInfoPageDto from(Page<BrainMemberInfoDto> users) {
        if (users == null) {
            return new BrainMemberInfoPageDto(List.of(), 0, 0, 0, 0, false);
        }

        return new BrainMemberInfoPageDto(
                users.getContent(),
                users.getNumber(),
                users.getSize(),
                users.getTotalElements(),
                users.getTotalPages(),
                users.hasNext()
        );
    }
}
