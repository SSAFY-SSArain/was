package org.ssafy.ssarain.domain.neuron.dto;

import org.ssafy.ssarain.domain.comment.dto.CommentDetailDto;
import org.ssafy.ssarain.domain.neuron.model.Neuron;

import java.time.LocalDateTime;
import java.util.List;

public record NeuronDetailDto(
        Integer nid,

        String title,

        String writer,

        String content,

        LocalDateTime createdAt,

        List<CommentDetailDto> comments
) {

    public static NeuronDetailDto from(Neuron neuron) {
        return new NeuronDetailDto(
                neuron.getNid(),
                neuron.getTitle(),
                neuron.getUser().getName(),
                neuron.getContent(),
                neuron.getCreatedAt(),
                List.of()
        );
    }

    public static NeuronDetailDto from(Neuron neuron, List<CommentDetailDto> comments) {
        return new NeuronDetailDto(
                neuron.getNid(),
                neuron.getTitle(),
                neuron.getUser().getName(),
                neuron.getContent(),
                neuron.getCreatedAt(),
                comments
        );
    }
}
