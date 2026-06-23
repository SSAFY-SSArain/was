package org.ssafy.ssarain.domain.topic.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ssafy.ssarain.domain.topic.dao.TopicRepository;
import org.ssafy.ssarain.domain.topic.dao.dto.TopicPathQueryDto;
import org.ssafy.ssarain.domain.topic.dto.TopicPathSearchDto;

@ExtendWith(MockitoExtension.class)
class TopicServiceTest {

    @Mock
    private TopicRepository topicRepository;

    @InjectMocks
    private TopicService topicService;

    @Test
    void topic을_검색하면_검색된_topic별로_root까지의_부모를_함께_반환한다() {
        when(topicRepository.findPathsByNameContaining("index", 1))
                .thenReturn(List.of(
                        new TopicPathQueryDto(7, 4, null, "language", 1, 3),
                        new TopicPathQueryDto(7, 5, 4, "c++", 1, 2),
                        new TopicPathQueryDto(7, 6, 5, "array", 0, 1),
                        new TopicPathQueryDto(7, 7, 6, "index", 0, 0),
                        new TopicPathQueryDto(20, 14, null, "database", 1, 3),
                        new TopicPathQueryDto(20, 15, 14, "mysql", 0, 2),
                        new TopicPathQueryDto(20, 16, 15, "sql", 0, 1),
                        new TopicPathQueryDto(20, 20, 16, "index", 0, 0)
                ));

        TopicPathSearchDto result = topicService.searchTopicPaths(" index ", 1);

        assertThat(result.topics()).hasSize(2);
        assertThat(result.topics().get(0))
                .extracting("name")
                .containsExactly("language", "c++", "array", "index");
        assertThat(result.topics().get(1))
                .extracting("name")
                .containsExactly("database", "mysql", "sql", "index");
    }

    @Test
    void 검색된_topic들이_같은_부모를_공유해도_각각의_경로로_반환한다() {
        when(topicRepository.findPathsByNameContaining("array", 1))
                .thenReturn(List.of(
                        new TopicPathQueryDto(6, 4, null, "language", 1, 2),
                        new TopicPathQueryDto(6, 5, 4, "c++", 1, 1),
                        new TopicPathQueryDto(6, 6, 5, "array", 0, 0),
                        new TopicPathQueryDto(8, 4, null, "language", 1, 2),
                        new TopicPathQueryDto(8, 5, 4, "c++", 1, 1),
                        new TopicPathQueryDto(8, 8, 5, "array-list", 0, 0)
                ));

        TopicPathSearchDto result = topicService.searchTopicPaths("array", 1);

        assertThat(result.topics()).hasSize(2);
        assertThat(result.topics().get(0))
                .extracting("tid")
                .containsExactly(4, 5, 6);
        assertThat(result.topics().get(1))
                .extracting("tid")
                .containsExactly(4, 5, 8);
    }

}
