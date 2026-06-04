package org.ssafy.ssarain.domain.user.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.ssafy.ssarain.common.response.BaseResponse;
import org.ssafy.ssarain.common.response.SuccessCode;
import org.ssafy.ssarain.common.security.model.CustomUserDetails;
import org.ssafy.ssarain.domain.user.dto.UserInfoDto;
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

    @GetMapping
    public ResponseEntity<BaseResponse<UserInfoDto>> getUserInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        UserInfoDto userInfo = userService.getUserInfo(userDetails.getUsername());

        return BaseResponse.success(SuccessCode.USER_INFO_SUCCESS, userInfo);
    }

    @PostMapping("name-check")
    public ResponseEntity<BaseResponse<NameCheckRes>> getUserInfo(
            @RequestBody NameCheckReq nameCheckReq
    ) {

        // TODO: 닉네임 락
        boolean isDuplicate = userService.isNameDuplicate(nameCheckReq.name());
        NameCheckRes nameCheckRes = NameCheckRes.of(isDuplicate);

        return BaseResponse.success(SuccessCode.USER_NAME_CHECK_SUCCESS, nameCheckRes);
    }
}
