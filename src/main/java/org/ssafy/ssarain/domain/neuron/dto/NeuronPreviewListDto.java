package org.ssafy.ssarain.domain.neuron.dto;

import java.util.List;

public record NeuronPreviewListDto(
        List<NeuronPreviewDto> neuronPreviewList
) {
    public static NeuronPreviewListDto from(List<NeuronPreviewDto> neuronPreviewList) {
        return new NeuronPreviewListDto(neuronPreviewList);
    }
}
