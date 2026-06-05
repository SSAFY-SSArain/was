package org.ssafy.ssarain.common.security.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

public record EmailVerifyCodeReq(
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "email@email.com")
        String email,

        @Schema(requiredMode = RequiredMode.REQUIRED, example = "123456")
        String code
) {
}
