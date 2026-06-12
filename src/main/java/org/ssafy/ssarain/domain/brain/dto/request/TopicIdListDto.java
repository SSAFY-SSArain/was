package org.ssafy.ssarain.domain.brain.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;

public record TopicIdListDto(
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "[ 1, 2, 3 ]")
        @NotNull
        List<@NotNull Integer> topics
        ) {

}
