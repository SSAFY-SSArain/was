package org.ssafy.ssarain.domain.topic.dto;

import org.ssafy.ssarain.domain.topic.model.Topic;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

public record TopicInfoDto(
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "1")
        int tid,
        
        @Schema(requiredMode = RequiredMode.NOT_REQUIRED, nullable = true, example = "null")
        Integer pid,
        
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "Java 개발")
        String name
        ) {
    
    public static TopicInfoDto from(Topic topic) {
        return new TopicInfoDto(topic.getTid(), topic.getPid(), topic.getName());
    }
}
