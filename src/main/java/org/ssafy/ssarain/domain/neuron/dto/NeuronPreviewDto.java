package org.ssafy.ssarain.domain.neuron.dto;

import org.ssafy.ssarain.domain.neuron.model.Neuron;

public record NeuronPreviewDto(
        Integer nid,

        String title,

        String content
) {

    public static NeuronPreviewDto from (Neuron neuron) {
        return new NeuronPreviewDto(neuron.getNid(), neuron.getTitle(), neuron.getContent());
    }
}
