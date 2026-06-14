package org.ssafy.ssarain.domain.node.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NodeCreateDto(

        @Schema(requiredMode = RequiredMode.REQUIRED, example = "노드 제목 1")
        @Size(max = 255)
        @NotBlank
        String title,

        @Schema(requiredMode = RequiredMode.REQUIRED, example = "노드 내용 1")
        @NotBlank
        String content,

        @Schema(requiredMode = RequiredMode.REQUIRED, example = "1")
        @NotNull
        Integer btid
) {
}
