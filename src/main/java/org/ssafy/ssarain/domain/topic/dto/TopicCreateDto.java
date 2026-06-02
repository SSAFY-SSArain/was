package org.ssafy.ssarain.domain.topic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public record TopicCreateDto(
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "추상화란")
        @NotBlank
        String name
        ) {

}
