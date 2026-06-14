package org.ssafy.ssarain.domain.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentCreateDto(
        @NotNull
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "2")
        Integer nid,

        @Schema(requiredMode = RequiredMode.NOT_REQUIRED, example = "1")
        Integer pid,

        @NotBlank
        @Size(max = 255)
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "이것은 댓글입니다.")
        String content
) {
}
