package org.ssafy.ssarain.domain.topic.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.ssafy.ssarain.common.error.GlobalException;
import org.ssafy.ssarain.common.response.ErrorCode;
import org.ssafy.ssarain.domain.topic.dao.TopicRepository;
import org.ssafy.ssarain.domain.topic.dto.TopicCreateDto;
import org.ssafy.ssarain.domain.topic.dto.TopicDetailDto;
import org.ssafy.ssarain.domain.topic.dto.TopicInfoDto;
import org.ssafy.ssarain.domain.topic.model.Topic;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    
    public List<TopicInfoDto> getAllTopicInfo() {
        return topicRepository.findAll().stream()
                .map(TopicInfoDto::from)
                .collect(Collectors.toList());
    }
    
    public List<TopicInfoDto> getChildTopic(int tid) {
        return topicRepository.findByPid(tid).stream()
                .map(TopicInfoDto::from)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public TopicDetailDto createTopic(Integer pid, TopicCreateDto dto) {
        validatePid(pid);
        validateCreateDto(dto);
        
        // JPA 방식의 외래키 설정을 위해 프록시 껍데기 객체만 생성해 등록합니다.
        Topic newTopic = Topic.of(getParentTopicProxy(pid), dto.name());
        newTopic = topicRepository.save(newTopic);
        return TopicDetailDto.from(newTopic);
    }
    
    /*
        Util Method
     */
    
    private void validatePid(Integer pid) {
        if (pid != null && !topicRepository.existsById(pid)) {
            throw new GlobalException(ErrorCode.TOPIC_NOT_FOUND);
        }
    }
    
    private void validateCreateDto(TopicCreateDto dto) {
        if (dto == null || dto.name() == null) {
            throw new GlobalException(ErrorCode.BAD_REQUEST);
        }
        validateDuplicateName(dto.name());
    }
    
    private void validateDuplicateName(String name) {
        if (topicRepository.existsByName(name)) {
            throw new GlobalException(ErrorCode.TOPIC_NAME_DUPLICATED);
        }
    }
    
    private Topic findTopicByTid(int tid) {
        return topicRepository.findById(tid)
                .orElseThrow(() -> new GlobalException(ErrorCode.TOPIC_NOT_FOUND));
    }

    private Topic getParentTopicProxy(Integer pid) {
        if (pid == null) {
            return null;
        }
        return topicRepository.getReferenceById(pid);
    }
}
