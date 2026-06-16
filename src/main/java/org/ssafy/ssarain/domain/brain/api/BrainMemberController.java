package org.ssafy.ssarain.domain.brain.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import org.ssafy.ssarain.domain.brain.dto.request.BrainJoinManageDto;
import org.ssafy.ssarain.domain.brain.dto.request.BrainMemberListDto;
import org.ssafy.ssarain.domain.brain.dto.response.BrainUserListDto;
import org.ssafy.ssarain.domain.brain.service.BrainMemberService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/brains")
public class BrainMemberController {

    private final BrainMemberService brainMemberService;
    private final BrainAuthService brainAuthService;

    @PostMapping("/{bid}/join")
    @Operation(summary = "B01: Brain 가입 신청")
    public ResponseEntity<BaseResponse<Void>> requestJoin(
            @PathVariable int bid,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        brainMemberService.requestJoin(bid, userDetails.getUserId());
        return BaseResponse.success(SuccessCode.BRAIN_MEMBER_REQUEST_SUCCESS);
    }
    
    @PostMapping("/{bid}/users")
    @Operation(summary = "B02: Brain에 사용자 일괄 등록")
    public ResponseEntity<BaseResponse<Void>> addBrainMembers(
            @PathVariable int bid,
            @Valid @RequestBody BrainMemberListDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        brainAuthService.authorizeBrainRoleOf(userDetails, bid, BrainAuthService.BRAIN_MANAGER);
        brainMemberService.addBrainMembers(bid, dto);
        return BaseResponse.success(SuccessCode.BRAIN_MEMBER_JOIN_SUCCESS);
    }

    @DeleteMapping("/{bid}/users")
    @Operation(summary = "B03: Brain 소속 사용자 일괄 삭제")
    public ResponseEntity<BaseResponse<Void>> deleteMembers(
            @PathVariable int bid,
            @Valid @RequestBody BrainMemberListDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        brainAuthService.authorizeBrainRoleOf(userDetails, bid, BrainAuthService.BRAIN_MANAGER);
        brainMemberService.deleteMembers(bid, userDetails.getUserId(), dto);
        return BaseResponse.success(SuccessCode.BRAIN_MEMBER_DELETE_SUCCESS);
    }

    @GetMapping("/{bid}/available-users")
    @Operation(summary = "B12: 특정 Brain에 소속되지 않은 사용자 검색")
    public ResponseEntity<BaseResponse<BrainUserListDto>> searchAvailableUsers(
            @PathVariable int bid,
            @RequestParam(required = false, defaultValue = "") String search,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        brainAuthService.authorizeBrainRoleOf(userDetails, bid, BrainAuthService.BRAIN_MANAGER);
        return BaseResponse.success(
                SuccessCode.BRAIN_AVAILABLE_USER_INFO_SUCCESS,
                brainMemberService.searchAvailableUsers(bid, search)
        );
    }

    @GetMapping("/{bid}/join-requests")
    @Operation(summary = "B14: 특정 Brain 가입 신청자 조회")
    public ResponseEntity<BaseResponse<BrainUserListDto>> getJoinRequests(
            @PathVariable int bid,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        brainAuthService.authorizeBrainRoleOf(userDetails, bid, BrainAuthService.BRAIN_MANAGER);
        return BaseResponse.success(
                SuccessCode.BRAIN_JOIN_REQUEST_INFO_SUCCESS,
                brainMemberService.getJoinRequests(bid)
        );
    }

    @PostMapping("/{bid}/join-manage")
    @Operation(summary = "B15: 특정 Brain 가입 신청자 수락/거부")
    public ResponseEntity<BaseResponse<Void>> manageJoinRequest(
            @PathVariable int bid,
            @Valid @RequestBody BrainJoinManageDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        brainAuthService.authorizeBrainRoleOf(userDetails, bid, BrainAuthService.BRAIN_MANAGER);
        brainMemberService.manageJoinRequest(bid, dto);
        return BaseResponse.success(SuccessCode.BRAIN_JOIN_MANAGE_SUCCESS);
    }

    @GetMapping("/{bid}/users")
    @Operation(summary = "B17: Brain 소속 사용자 조회")
    public ResponseEntity<BaseResponse<BrainUserListDto>> getBrainMembers(
            @PathVariable int bid,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        brainAuthService.authorizeBrainRoleOf(userDetails, bid, BrainAuthService.BRAIN_MANAGER);
        return BaseResponse.success(
                SuccessCode.BRAIN_MEMBER_INFO_SUCCESS,
                brainMemberService.getBrainMembers(bid)
        );
    }
}
