package org.ssafy.ssarain.common.security.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;

public record EmailVerifyReq(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "email@email.com")
        String email
) {
}
