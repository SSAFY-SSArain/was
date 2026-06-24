package org.ssafy.ssarain.domain.brain.dto.response;

import java.util.List;

import org.ssafy.ssarain.domain.brain.model.Brain;
import org.ssafy.ssarain.domain.brain.model.JoinPolicy;
import org.ssafy.ssarain.domain.brain.model.JoinStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

public record BrainFoundDto(
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "1")
        int id,
        
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "구미 3반")
        String name,
        
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "강석진")
        String adminName,
        
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "구미 캠퍼스 3반입니다.")
        String description,

        @Schema(requiredMode = RequiredMode.REQUIRED, example = "PUBLIC")
        JoinPolicy joinPolicy,

        @Schema(requiredMode = RequiredMode.REQUIRED, example = "ACTIVE")
        JoinStatus joinStatus,
        
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "[ \"박성수\", \"백성수\" ]")
        List<String> memberNames
        ) {
    
    public static BrainFoundDto from(
            Brain brain,
            String adminName,
            JoinStatus joinStatus,
            List<String> memberNames
    ) {
        return new BrainFoundDto(
                brain.getBid(),
                brain.getName(),
                adminName,
                brain.getDescription(),
                brain.getJoinPolicy(),
                joinStatus,
                memberNames
        );
    }
}
