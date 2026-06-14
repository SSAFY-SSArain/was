package org.ssafy.ssarain.domain.node.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.ssafy.ssarain.domain.node.model.Node;

import java.util.UUID;

public record NodeCreateDto(

        @Schema(requiredMode = RequiredMode.REQUIRED, example = "노드 제목 1")
        @NotBlank
        String title,

        @Schema(requiredMode = RequiredMode.REQUIRED, example = "노드 내용 1")
        @NotBlank
        String content,

        @Schema(requiredMode = RequiredMode.REQUIRED, example = "1")
        @NotNull
        Integer btid
) {

    public Node toEntity(UUID uid) {
        return Node.of(btid,uid,title,content);
    }
}
