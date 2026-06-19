package org.ssafy.ssarain.domain.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentUpdateDto(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "댓글이요")
        @NotBlank
        @Size(max = 255)
        String content
) {
}
