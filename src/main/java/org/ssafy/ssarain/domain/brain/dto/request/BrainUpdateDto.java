package org.ssafy.ssarain.domain.brain.dto.request;

import org.ssafy.ssarain.domain.brain.model.JoinPolicy;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BrainUpdateDto(
		@Schema(requiredMode = RequiredMode.NOT_REQUIRED, example = "구미 3반")
		@Pattern(regexp = ".*\\S.*")
        @Size(max = 50)
        String name,
        
        @Schema(requiredMode = RequiredMode.NOT_REQUIRED, example = "구미 3반 지식저장소")
		@Pattern(regexp = ".*\\S.*")
        @Size(max = 200)
        String description,
        
        @Schema(requiredMode = RequiredMode.NOT_REQUIRED, example = "PROTECTED")
        JoinPolicy joinPolicy) {

}
