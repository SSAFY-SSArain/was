package org.ssafy.ssarain.domain.user.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;

public record NameCheckReq(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "박성수")
        String name
) {
}
