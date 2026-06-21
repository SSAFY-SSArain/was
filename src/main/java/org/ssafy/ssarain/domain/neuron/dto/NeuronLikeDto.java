package org.ssafy.ssarain.domain.neuron.dto;

public record NeuronLikeDto(
        int likeCount,

        boolean liked
) {

    public static NeuronLikeDto from(int likeCount, boolean liked) {
        return new NeuronLikeDto(likeCount, liked);
    }
}
