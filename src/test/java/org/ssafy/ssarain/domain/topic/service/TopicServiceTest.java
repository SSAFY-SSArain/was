package org.ssafy.ssarain.domain.topic.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ssafy.ssarain.common.error.GlobalException;
import org.ssafy.ssarain.domain.topic.dao.TopicRepository;
import org.ssafy.ssarain.domain.topic.dao.dto.TopicPathQueryDto;
import org.ssafy.ssarain.domain.topic.dto.TopicCreateDto;
import org.ssafy.ssarain.domain.topic.dto.TopicPathSearchDto;
import org.ssafy.ssarain.domain.topic.model.Topic;

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

    @Test
    void 같은_부모의_자식_topic_name은_중복될_수_없다() {
        when(topicRepository.existsById(1)).thenReturn(true);
        when(topicRepository.existsByPidAndName(1, "index")).thenReturn(true);

        assertThatThrownBy(() -> topicService.createTopic(1, new TopicCreateDto("index")))
                .isInstanceOf(GlobalException.class);

        verify(topicRepository, never()).save(any(Topic.class));
    }

    @Test
    void root_topic_name도_중복될_수_없다() {
        when(topicRepository.existsByPidAndName(null, "database")).thenReturn(true);

        assertThatThrownBy(() -> topicService.createTopic(null, new TopicCreateDto("database")))
                .isInstanceOf(GlobalException.class);

        verify(topicRepository, never()).save(any(Topic.class));
    }

    @Test
    void 부모가_다르면_같은_topic_name으로_생성할_수_있다() {
        Topic savedTopic = Topic.of(Topic.of(null, "database"), "index");

        when(topicRepository.existsById(2)).thenReturn(true);
        when(topicRepository.existsByPidAndName(2, "index")).thenReturn(false);
        when(topicRepository.getReferenceById(2)).thenReturn(Topic.of(null, "mysql"));
        when(topicRepository.save(any(Topic.class))).thenReturn(savedTopic);

        topicService.createTopic(2, new TopicCreateDto("index"));

        verify(topicRepository).save(any(Topic.class));
    }

}
