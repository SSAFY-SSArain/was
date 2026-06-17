package org.ssafy.ssarain.domain.user.dto;

public record UserActivityDto(
        int nodeCount,
        int commentCount,
        // TODO: 추후 확장을 위한 데이터이며, 현재는 0이 전송됨 
        int likeCount) {

}
