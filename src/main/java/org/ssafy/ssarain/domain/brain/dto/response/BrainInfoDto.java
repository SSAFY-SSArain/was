package org.ssafy.ssarain.domain.brain.dto.response;

import org.ssafy.ssarain.domain.brain.model.Brain;
import org.ssafy.ssarain.domain.brain.model.JoinPolicy;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

public record BrainInfoDto(
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "1")
        int id,
        
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "구미 3반")
        String name,
        
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "구미 캠퍼스 3반입니다.")
        String description,
        
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "PUBLIC")
        JoinPolicy joinPolicy
        ) {
    
    public static BrainInfoDto from(Brain brain) {
        return new BrainInfoDto(brain.getBid(), brain.getName(), brain.getDescription(), brain.getJoinPolicy());
    }
}
