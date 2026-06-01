package org.ssafy.ssarain.domain.user.api;

import lombok.RequiredArgsConstructor;
import org.ssafy.ssarain.common.response.BaseResponse;
import org.ssafy.ssarain.common.response.SuccessCode;
import org.ssafy.ssarain.common.security.model.CustomUserDetails;
import org.ssafy.ssarain.domain.user.dto.UserInfoDto;
import org.ssafy.ssarain.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
