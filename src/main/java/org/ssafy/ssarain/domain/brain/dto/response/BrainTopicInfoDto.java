package org.ssafy.ssarain.domain.brain.dto.response;

import org.ssafy.ssarain.domain.brain.model.BrainTopic;
import org.ssafy.ssarain.domain.topic.model.Topic;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

public record BrainTopicInfoDto(
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "1")
        int btid,
        
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "1")
        int tid,
        
        @Schema(requiredMode = RequiredMode.NOT_REQUIRED, nullable = true, example = "null")
        Integer pid,
        
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "Java 개발")
        String name
        ) {
    
    public static BrainTopicInfoDto from(BrainTopic brainTopic) {
        Topic topic = brainTopic.getTopic();
        return new BrainTopicInfoDto(
                brainTopic.getBtid(),
                topic.getTid(), 
                topic.getPid(), 
                topic.getName());
    }
}
