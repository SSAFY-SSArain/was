package org.ssafy.ssarain.domain.user.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

public record UserSearchPageRes(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<UserSearchInfoRes> users,

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

    public static UserSearchPageRes from(Page<UserSearchInfoRes> users) {
        if (users == null) {
            return new UserSearchPageRes(List.of(), 0, 0, 0, 0, false);
        }

        return new UserSearchPageRes(
                users.getContent(),
                users.getNumber(),
                users.getSize(),
                users.getTotalElements(),
                users.getTotalPages(),
                users.hasNext()
        );
    }
}
