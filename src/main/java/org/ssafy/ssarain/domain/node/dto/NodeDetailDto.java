package org.ssafy.ssarain.domain.node.dto;

import org.ssafy.ssarain.domain.node.model.Node;

public record NodeDetailDto(
        Integer nid,

        String title,

        String writer,

        String content

        // TODO: Comments 추가 필요
        // List<Comments> comments
) {

    public static NodeDetailDto from(Node node, String name) {
        return new NodeDetailDto(
                node.getId(),
                node.getTitle(),
                name,
                node.getContent()
        );
    }
}
