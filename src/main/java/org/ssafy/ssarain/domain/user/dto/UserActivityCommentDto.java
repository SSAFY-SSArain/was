package org.ssafy.ssarain.domain.user.dto;

import java.time.LocalDateTime;

public record UserActivityCommentDto(
        int bid,
        int tid,
        Integer nid,
        Integer cid,
        String content,
        LocalDateTime createdAt
) {
}
