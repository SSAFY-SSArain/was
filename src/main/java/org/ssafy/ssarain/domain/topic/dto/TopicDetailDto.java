package org.ssafy.ssarain.domain.topic.dto;

import org.ssafy.ssarain.domain.topic.model.Topic;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

public record TopicDetailDto(
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "1")
        int tid,
        
        @Schema(requiredMode = RequiredMode.NOT_REQUIRED, nullable = true, example = "null")
        Integer pid,
        
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "Java 개발")
        String name
        
        // TODO: Node 관련 정보도 추가되어야 합니다.
        ) {
    
    public static TopicDetailDto from(Topic topic) {
        return new TopicDetailDto(topic.getTid(), topic.getPid(), topic.getName());
    }
}
