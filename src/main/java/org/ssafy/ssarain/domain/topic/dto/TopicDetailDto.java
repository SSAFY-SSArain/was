package org.ssafy.ssarain.domain.topic.dto;

import java.util.List;

import org.ssafy.ssarain.domain.topic.model.Topic;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

public record TopicDetailDto(
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "1")
        int tid,
        
        @Schema(requiredMode = RequiredMode.NOT_REQUIRED, nullable = true, example = "null")
        Integer pid,
        
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "Java 개발")
        String name,
        
        @Schema(requiredMode = RequiredMode.REQUIRED)
        List<TopicNodeInfoDto> nodes
        ) {
    
    public static TopicDetailDto from(Topic topic, List<TopicNodeInfoDto> nodeDtos) {
        return new TopicDetailDto(topic.getTid(), topic.getPid(), topic.getName(), nodeDtos);
    }
}
