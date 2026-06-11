package org.ssafy.ssarain.domain.topic.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.ssafy.ssarain.common.response.BaseResponse;
import org.ssafy.ssarain.common.response.SuccessCode;
import org.ssafy.ssarain.common.security.model.CustomUserDetails;
import org.ssafy.ssarain.common.security.service.BrainAuthService;
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
    private final BrainAuthService brainAuthService;
    private final TopicService topicService;
    
    @GetMapping
    @Operation(summary = "T01: 전체 Topic 조회")
    public ResponseEntity<BaseResponse<List<TopicInfoDto>>> getAllTopics(
            @RequestParam(name = "brain", required = false) Integer bid) {
        return BaseResponse.success(SuccessCode.TOPIC_INFO_SUCCESS, topicService.getAllTopicInfo(bid));
    }
    
    @GetMapping("/{tid}/child")
    @Operation(summary = "T02: 특정 Topic의 자식 Topic 조회")
    public ResponseEntity<BaseResponse<List<TopicInfoDto>>> getChildTopic(
            @PathVariable int tid,
            @RequestParam(name = "brain", required = false) Integer bid) {
        return BaseResponse.success(SuccessCode.TOPIC_INFO_SUCCESS, topicService.getChildTopic(tid, bid));
    }
    
    @PostMapping({"", "/{pid}"})
    @Operation(summary = "T04: Topic 생성", description = "pid == null일 경우 루트 주제를 생성합니다.")
    public ResponseEntity<BaseResponse<TopicDetailDto>> createTopic(
            @PathVariable(required = false) Integer pid,
            @RequestBody TopicCreateDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        brainAuthService.authorizeAnyBrainAdmin(userDetails);
        return BaseResponse.success(SuccessCode.TOPIC_CREATE_SUCCESS, topicService.createTopic(pid, dto));
    }
}
