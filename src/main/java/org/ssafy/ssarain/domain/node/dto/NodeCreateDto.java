package org.ssafy.ssarain.domain.node.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import org.ssafy.ssarain.domain.node.model.Node;

import java.util.UUID;

public record NodeCreateDto(
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "노드 제목 1")
        String title,

        @Schema(requiredMode = RequiredMode.REQUIRED, example = "노드 내용 1")
        String content,

        @Schema(requiredMode = RequiredMode.REQUIRED, example = "1")
        Integer btid
) {

    public Node toEntity(UUID uid) {
        return Node.of(btid,uid,title,content);
    }
}
