package org.ssafy.ssarain.domain.node.dto;

import org.ssafy.ssarain.domain.node.model.Node;

public record NodePreviewDto(
        Integer nid,

        String title,

        String content
) {

    public static NodePreviewDto from (Node node) {
        return new NodePreviewDto(node.getId(), node.getTitle(), node.getContent());
    }
}
