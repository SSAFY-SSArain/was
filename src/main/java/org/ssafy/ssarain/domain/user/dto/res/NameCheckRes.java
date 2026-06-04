package org.ssafy.ssarain.domain.user.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;

public record NameCheckRes(
        @Schema(
                requiredMode = Schema.RequiredMode.REQUIRED,
                description = "닉네임 중복 여부 (true: 중복됨, false: 사용 가능)",
                example = "true"
        )
        boolean isDuplicate
) {

    public static NameCheckRes of(boolean isDuplicate) {
        return new NameCheckRes(isDuplicate);
    }
}
