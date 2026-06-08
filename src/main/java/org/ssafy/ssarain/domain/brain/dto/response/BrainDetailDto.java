package org.ssafy.ssarain.domain.brain.dto.response;

import java.util.List;

import org.ssafy.ssarain.domain.brain.model.Brain;
import org.ssafy.ssarain.domain.topic.dto.TopicInfoDto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

public record BrainDetailDto(
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "1")
        int id,
        
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "구미 3반")
        String name,
        
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "구미 캠퍼스 3반입니다.")
        String description,
        
        // DTO가 Topic 도메인에 대한 의존성이 과할 경우, Brain 내에 별도의 Topic DTO를 두는 방법을 고려할 것.
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "[]")
        List<TopicInfoDto> topics
        ) {

    public static BrainDetailDto from(Brain brain) {
        return new BrainDetailDto(brain.getBid(), brain.getName(), brain.getDescription(), List.of());
    }

    public static BrainDetailDto from(Brain brain, List<TopicInfoDto> topics) {
        if (topics == null) {
            topics = List.of();
        }
        return new BrainDetailDto(brain.getBid(), brain.getName(), brain.getDescription(), topics);
    }
}
