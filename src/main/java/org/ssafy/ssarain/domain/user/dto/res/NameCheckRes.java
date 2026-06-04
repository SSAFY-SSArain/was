package org.ssafy.ssarain.domain.user.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;

public record NameCheckRes(
        @Schema(
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "true"
        )
        boolean isDuplicate
) {

    public static NameCheckRes of(boolean isDuplicate) {
        return new NameCheckRes(isDuplicate);
    }
}
