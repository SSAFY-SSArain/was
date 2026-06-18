package org.ssafy.ssarain.domain.comment.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.ssafy.ssarain.common.response.BaseResponse;
import org.ssafy.ssarain.common.response.SuccessCode;
import org.ssafy.ssarain.common.security.model.CustomUserDetails;
import org.ssafy.ssarain.common.security.service.CommentAuthService;
import org.ssafy.ssarain.domain.comment.dto.CommentCreateDto;
import org.ssafy.ssarain.domain.comment.dto.CommentDetailDto;
import org.ssafy.ssarain.domain.comment.dto.CommentUpdateDto;
import org.ssafy.ssarain.domain.comment.service.CommentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentService     commentService;
    private final CommentAuthService commentAuthService;

    @PostMapping
    @Operation(summary = "C01: Comments 생성")
    public ResponseEntity<BaseResponse<CommentDetailDto>> createComment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody @Valid CommentCreateDto commentCreateDto
    ) {

        CommentDetailDto commentDetailDto = commentService.createComment(commentCreateDto, customUserDetails.getUserId());

        return BaseResponse.success(SuccessCode.COMMENT_CREATE_SUCCESS, commentDetailDto);
    }

    @PatchMapping("/{cid}")
    @Operation(summary = "C02: Comment 수정")
    public ResponseEntity<BaseResponse<CommentDetailDto>> updateComment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable int cid,
            @RequestBody @Valid CommentUpdateDto commentUpdateDto
    ) {

        commentAuthService.authorizeCommentWriter(customUserDetails, cid);

        CommentDetailDto updatedDetailDto = commentService.updateComment(commentUpdateDto, cid);

        return BaseResponse.success(SuccessCode.COMMENT_UPDATE_SUCCESS, updatedDetailDto);
    }

    @DeleteMapping("/{cid}")
    @Operation(summary = "C03: Comment 삭제")
    public ResponseEntity<BaseResponse<Void>> updateComment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable int cid
    ) {

        commentAuthService.authorizeCommentWriterOrAdmin(customUserDetails, cid);

        commentService.deleteComment(cid);

        return BaseResponse.success(SuccessCode.COMMENT_DELETE_SUCCESS);
    }

}
