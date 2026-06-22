package org.ssafy.ssarain.domain.user.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.ssafy.ssarain.common.response.BaseResponse;
import org.ssafy.ssarain.common.response.SuccessCode;
import org.ssafy.ssarain.common.security.model.CustomUserDetails;
import org.ssafy.ssarain.domain.user.dto.UserInfoDto;
import org.ssafy.ssarain.domain.user.dto.UserProfileDto;
import org.ssafy.ssarain.domain.user.dto.UserUpdateDto;
import org.ssafy.ssarain.domain.user.dto.req.NameCheckReq;
import org.ssafy.ssarain.domain.user.dto.res.NameCheckRes;
import org.ssafy.ssarain.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    @Operation(summary = "U01: 내 정보 조회", description = "유저 본인의 정보(이메일, 이름, 활동 통계)를 조회합니다.")
    @GetMapping
    public ResponseEntity<BaseResponse<UserInfoDto>> getUserInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        UserInfoDto userInfo = userService.getUserInfo(userDetails.getUsername());

        return BaseResponse.success(SuccessCode.USER_INFO_SUCCESS, userInfo);
    }
    
    @Operation(summary = "U02: 비밀번호를 변경합니다.", description = "유저 본인의 비밀번호를 변경합니다.")
    @PatchMapping
    public ResponseEntity<BaseResponse<UserProfileDto>> updateUser(
    		@AuthenticationPrincipal CustomUserDetails userDetails,
    		@Valid @RequestBody UserUpdateDto dto) {
    	
    	UserProfileDto userProfile = userService.updateUser(userDetails.getUsername(), dto);
    	
    	return BaseResponse.success(SuccessCode.USER_UPDATE_SUCCESS, userProfile);
    }

    @Operation(summary = "U03: 이름 중복 검증", description = "중복되는 이름인지 확인합니다.")
    @PostMapping("name-check")
    public ResponseEntity<BaseResponse<NameCheckRes>> getUserInfo(
            @RequestBody NameCheckReq nameCheckReq
    ) {

        // TODO: 닉네임 락
        boolean isDuplicate = userService.isNameDuplicate(nameCheckReq);
        NameCheckRes nameCheckRes = NameCheckRes.of(isDuplicate);

        return BaseResponse.success(SuccessCode.USER_NAME_CHECK_SUCCESS, nameCheckRes);
    }
}
