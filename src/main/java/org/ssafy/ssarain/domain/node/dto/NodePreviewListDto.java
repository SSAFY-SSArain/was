package org.ssafy.ssarain.domain.node.dto;

import java.util.List;

public record NodePreviewListDto(
        List<NodePreviewDto> nodePreviewList
) {
    public static NodePreviewListDto from(List<NodePreviewDto> nodePreviewList) {
        return new NodePreviewListDto(nodePreviewList);
    }
}
