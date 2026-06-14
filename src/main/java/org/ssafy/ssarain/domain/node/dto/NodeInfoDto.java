package org.ssafy.ssarain.domain.node.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import org.ssafy.ssarain.domain.node.model.Node;

public record NodeInfoDto(
        @Schema(requiredMode = RequiredMode.REQUIRED, example = "1")
        int nid,

        @Schema(requiredMode = RequiredMode.REQUIRED, example = "플로이드-워셜 시간복잡도")
        String title,

        @Schema(requiredMode = RequiredMode.REQUIRED, example = "N의 3제곱이라고 한다")
        String content
) {

    public static NodeInfoDto from(Node node) {
        return new NodeInfoDto(node.getId(), node.getTitle(), node.getContent());
    }
}
