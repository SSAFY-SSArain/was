package org.ssafy.ssarain.domain.topic.dto;

import java.util.List;

public record TopicPathSearchDto(
        List<List<TopicInfoDto>> topics
        ) {

    public static TopicPathSearchDto from(List<List<TopicInfoDto>> topics) {
        return new TopicPathSearchDto(topics);
    }
}
