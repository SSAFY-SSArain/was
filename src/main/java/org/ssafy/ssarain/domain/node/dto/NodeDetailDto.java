package org.ssafy.ssarain.domain.node.dto;

import org.ssafy.ssarain.domain.comment.dto.CommentDetailDto;
import org.ssafy.ssarain.domain.node.model.Node;

import java.time.LocalDateTime;
import java.util.List;

public record NodeDetailDto(
        Integer nid,

        String title,

        String writer,

        String content,

        LocalDateTime createdAt,

        List<CommentDetailDto> comments
) {

    public static NodeDetailDto from(Node node) {
        return new NodeDetailDto(
                node.getNid(),
                node.getTitle(),
                node.getUser().getName(),
                node.getContent(),
                node.getCreatedAt(),
                List.of()
        );
    }

    public static NodeDetailDto from(Node node, List<CommentDetailDto> comments) {
        return new NodeDetailDto(
                node.getNid(),
                node.getTitle(),
                node.getUser().getName(),
                node.getContent(),
                node.getCreatedAt(),
                comments
        );
    }
}
