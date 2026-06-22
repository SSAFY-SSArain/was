package org.ssafy.ssarain.domain.user.dto;

import java.time.LocalDateTime;

public record UserActivityNeuronDto(
        int bid,
        int tid,
        Integer nid,
        String title,
        LocalDateTime createdAt
) {
}
