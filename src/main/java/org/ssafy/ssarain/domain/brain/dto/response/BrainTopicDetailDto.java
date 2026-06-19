package org.ssafy.ssarain.domain.brain.dto.response;

import java.util.List;

import org.ssafy.ssarain.domain.brain.model.BrainTopic;
import org.ssafy.ssarain.domain.neuron.dto.NeuronInfoDto;
import org.ssafy.ssarain.domain.topic.model.Topic;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

public record BrainTopicDetailDto(
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "15")
        int btid,
        
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "11")
        int tid,

        @Schema(requiredMode = RequiredMode.NOT_REQUIRED, nullable = true, example = "3")
        Integer pid,

        @Schema(requiredMode = RequiredMode.REQUIRED, example = "최단거리")
        String name,

        @Schema(requiredMode = RequiredMode.REQUIRED)
        List<NeuronInfoDto> neurons
) {

    public static BrainTopicDetailDto from(BrainTopic brainTopic, List<NeuronInfoDto> neurons) {
        if (neurons == null) {
            neurons = List.of();
        }
        Topic topic = brainTopic.getTopic();
        return new BrainTopicDetailDto(brainTopic.getBtid(), topic.getTid(), topic.getPid(), topic.getName(), neurons);
    }
}
