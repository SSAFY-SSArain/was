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

        int likeCount,

        boolean liked,

        LocalDateTime createdAt,

        List<CommentDetailDto> comments
) {

    public static NeuronDetailDto from(
            Neuron neuron
    ) {
        return new NeuronDetailDto(
                neuron.getNid(),
                neuron.getTitle(),
                neuron.getUser().getName(),
                neuron.getContent(),
                0,
                false,
                neuron.getCreatedAt(),
                List.of()
        );
    }

    public static NeuronDetailDto from(
            Neuron neuron,
            int likeCount,
            boolean liked,
            List<CommentDetailDto> comments
    ) {
        return new NeuronDetailDto(
                neuron.getNid(),
                neuron.getTitle(),
                neuron.getUser().getName(),
                neuron.getContent(),
                likeCount,
                liked,
                neuron.getCreatedAt(),
                comments
        );
    }
}
