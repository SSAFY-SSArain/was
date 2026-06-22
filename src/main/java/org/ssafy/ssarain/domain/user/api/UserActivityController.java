package org.ssafy.ssarain.domain.user.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ssafy.ssarain.common.response.BaseResponse;
import org.ssafy.ssarain.common.response.SuccessCode;
import org.ssafy.ssarain.common.security.model.CustomUserDetails;
import org.ssafy.ssarain.domain.user.dto.UserActivityCommentDto;
import org.ssafy.ssarain.domain.user.dto.UserActivityNeuronDto;
import org.ssafy.ssarain.domain.user.dto.UserActivityPageDto;
import org.ssafy.ssarain.domain.user.service.UserActivityService;

@RestController
@RequestMapping("/api/v1/user/activities")
@RequiredArgsConstructor
public class UserActivityController {

    private final UserActivityService userActivityService;

    @GetMapping("/neurons")
    @Operation(summary = "U04: 내가 작성한 뉴런 조회")
    public ResponseEntity<BaseResponse<UserActivityPageDto<UserActivityNeuronDto>>> getWrittenNeurons(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable
    ) {

        UserActivityPageDto<UserActivityNeuronDto> writtenNeurons =
                userActivityService.getWrittenNeurons(userDetails.getUserId(), pageable);

        return BaseResponse.success(SuccessCode.USER_ACTIVITY_INFO_SUCCESS, writtenNeurons);
    }

    @GetMapping("/comments")
    @Operation(summary = "U05: 내가 작성한 댓글 조회")
    public ResponseEntity<BaseResponse<UserActivityPageDto<UserActivityCommentDto>>> getWrittenComments(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable
    ) {

        UserActivityPageDto<UserActivityCommentDto> writtenComments =
                userActivityService.getWrittenComments(userDetails.getUserId(), pageable);

        return BaseResponse.success(SuccessCode.USER_ACTIVITY_INFO_SUCCESS, writtenComments);
    }

    @GetMapping("/liked-neurons")
    @Operation(summary = "U06: 내가 좋아요한 뉴런 조회")
    public ResponseEntity<BaseResponse<UserActivityPageDto<UserActivityNeuronDto>>> getRecommendedNeurons(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable
    ) {

        UserActivityPageDto<UserActivityNeuronDto> recommendedNeurons =
                userActivityService.getRecommendedNeurons(userDetails.getUserId(), pageable);

        return BaseResponse.success(SuccessCode.USER_ACTIVITY_INFO_SUCCESS, recommendedNeurons);
    }
}
