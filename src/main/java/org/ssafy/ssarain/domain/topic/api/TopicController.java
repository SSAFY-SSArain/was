package org.ssafy.ssarain.domain.topic.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ssafy.ssarain.common.response.BaseResponse;
import org.ssafy.ssarain.common.response.SuccessCode;
import org.ssafy.ssarain.domain.topic.dto.TopicCreateDto;
import org.ssafy.ssarain.domain.topic.dto.TopicDetailDto;
import org.ssafy.ssarain.domain.topic.dto.TopicInfoDto;
import org.ssafy.ssarain.domain.topic.service.TopicService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/topics")
public class TopicController {
    private final TopicService topicService;
    
    @GetMapping
    @Operation(summary = "T01: 전체 Topic 조회")
    public ResponseEntity<BaseResponse<List<TopicInfoDto>>> getAllTopics() {
        return BaseResponse.success(SuccessCode.TOPIC_INFO_SUCCESS, topicService.getAllTopicInfo());
    }
    
    @GetMapping("/{tid}/child")
    @Operation(summary = "T02: 특정 Topic의 자식 Topic 조회")
    public ResponseEntity<BaseResponse<List<TopicInfoDto>>> getChildTopic(
            @PathVariable int tid) {
        return BaseResponse.success(SuccessCode.TOPIC_INFO_SUCCESS, topicService.getChildTopic(tid));
    }
    
    @GetMapping("/{tid}")
    @Operation(summary = "T03: Topic 상세 조회")
    public ResponseEntity<BaseResponse<TopicDetailDto>> getTopic(
            @PathVariable int tid) {
        return BaseResponse.success(SuccessCode.TOPIC_INFO_SUCCESS, topicService.getTopicDetail(tid));
    }
    
    @PostMapping("/{pid}")
    @Operation(summary = "T04: Topic 생성")
    public ResponseEntity<BaseResponse<TopicDetailDto>> createTopic(
            @PathVariable int pid,
            @RequestBody TopicCreateDto dto) {
        return BaseResponse.success(SuccessCode.TOPIC_CREATE_SUCCESS, topicService.createTopic(pid, dto));
    }
    
    @PostMapping
    @Operation(summary = "T04_2: 루트 Topic 생성")
    public ResponseEntity<BaseResponse<TopicDetailDto>> createTopic(
            @RequestBody TopicCreateDto dto) {
        return BaseResponse.success(SuccessCode.TOPIC_CREATE_SUCCESS, topicService.createTopic(null, dto));
    }
}
