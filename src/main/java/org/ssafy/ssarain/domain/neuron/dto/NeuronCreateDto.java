package org.ssafy.ssarain.domain.neuron.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NeuronCreateDto(

        @Schema(requiredMode = RequiredMode.REQUIRED, example = "뉴런 제목 1")
        @Size(max = 100)
        @NotBlank
        String title,

        @Schema(requiredMode = RequiredMode.REQUIRED, example = "뉴런 내용 1")
        @NotBlank
        String content,

        @Schema(requiredMode = RequiredMode.REQUIRED, example = "1")
        @NotNull
        Integer btid
) {
}
