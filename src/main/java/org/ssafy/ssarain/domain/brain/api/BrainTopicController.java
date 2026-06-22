package org.ssafy.ssarain.domain.brain.api;

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
import org.ssafy.ssarain.domain.brain.dto.request.TopicIdListDto;
import org.ssafy.ssarain.domain.brain.dto.response.BrainDetailDto;
import org.ssafy.ssarain.domain.brain.dto.response.BrainTopicDetailDto;
import org.ssafy.ssarain.domain.brain.service.BrainTopicService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/brains")
public class BrainTopicController {
    private final BrainAuthService authService;
    private final BrainTopicService brainTopicService;

    @PostMapping("/{bid}/topics")
    @Operation(summary = "B09: 특정 Brain에 Topic 등록")
    public ResponseEntity<BaseResponse<Void>> registerBrainTopic(
            @PathVariable int bid,
            @Valid @RequestBody TopicIdListDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ) {
        authService.authorizeBrainRoleOf(userDetails, bid, BrainAuthService.BRAIN_MANAGER);
        brainTopicService.registerTopic(bid, dto);
        return BaseResponse.success(SuccessCode.BRAIN_TOPIC_REGISTER_SUCCESS);
    }

    @GetMapping("/{bid}/topics")
    @Operation(summary = "B10: 특정 Brain의 Topic 조회")
    public ResponseEntity<BaseResponse<BrainDetailDto>> getBrainTopics(
            @PathVariable int bid,
            @RequestParam(required = false) Integer tid,
            @RequestParam(required = false, defaultValue = "3") @Min(1) @Max(5) int depth) {
        return BaseResponse.success(SuccessCode.BRAIN_TOPIC_INFO_SUCCESS, brainTopicService.getBrainTopics(bid, tid, depth));
    }

    @GetMapping("/{bid}/topics/{tid}")
    @Operation(summary = "B16: 특정 Brain의 Topic 상세 조회")
    public ResponseEntity<BaseResponse<BrainTopicDetailDto>> getBrainTopicDetail(
            @PathVariable int bid,
            @PathVariable int tid) {
        return BaseResponse.success(SuccessCode.BRAIN_TOPIC_INFO_SUCCESS, brainTopicService.getBrainTopicDetail(bid, tid));
    }
}
