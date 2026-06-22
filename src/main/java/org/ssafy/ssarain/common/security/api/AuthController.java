package org.ssafy.ssarain.common.security.api;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.ssafy.ssarain.common.response.BaseResponse;
import org.ssafy.ssarain.common.response.SuccessCode;
import org.ssafy.ssarain.common.security.dto.req.LoginReq;
import org.ssafy.ssarain.common.security.dto.req.SignupReq;
import org.ssafy.ssarain.common.security.dto.res.TokenRes;
import org.ssafy.ssarain.common.security.dto.res.UserInfoRes;
import org.ssafy.ssarain.common.security.dto.res.UserWithTokenRes;
import org.ssafy.ssarain.common.security.service.AuthService;
import org.ssafy.ssarain.common.util.CookieUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.ssafy.ssarain.common.security.constant.SecurityConst.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieUtil  cookieUtil;

    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<UserInfoRes>> signup(
            @RequestBody SignupReq dto,
            HttpServletResponse response
    ) {

        UserWithTokenRes result = authService.signup(dto);
        setTokenCookies(response, result.tokenRes());

        UserInfoRes userInfo = result.userInfo();
        return BaseResponse.success(SuccessCode.USER_SIGN_UP_SUCCESS, userInfo);
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<UserInfoRes>> login(
            @RequestBody LoginReq dto,
            HttpServletResponse response
    ) {

        UserWithTokenRes result = authService.login(dto);
        setTokenCookies(response, result.tokenRes());
        UserInfoRes userInfo = result.userInfo();

        return BaseResponse.success(SuccessCode.USER_LOGIN_SUCCESS, userInfo);
    }

    @GetMapping("/refresh")
    public ResponseEntity<BaseResponse<UserInfoRes>> refresh(
            @CookieValue(value = REFRESH_TOKEN_COOKIE_NAME) String refreshToken,
            HttpServletResponse response
    ) {

        UserWithTokenRes result = authService.refresh(refreshToken);
        setTokenCookies(response, result.tokenRes());
        UserInfoRes userInfo = result.userInfo();
        
        return BaseResponse.success(SuccessCode.USER_TOKEN_REFRESH_SUCCESS, userInfo);
    }

    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<Void>> logout(
            @CookieValue(value = ACCESS_TOKEN_COOKIE_NAME, required = false) String accessToken,
            HttpServletResponse response
    ) {

        if(accessToken != null) {
            authService.logout(accessToken);
        }
        clearTokenCookies(response);

        return BaseResponse.success(SuccessCode.USER_LOGOUT_SUCCESS);
    }

    /*
        Util Method
     */

    private void setTokenCookies(HttpServletResponse response, TokenRes result) {

        cookieUtil.addCookie(response, ACCESS_TOKEN_COOKIE_NAME, result.accessToken(), result.accessTokenExpireTime());
        cookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, result.refreshToken(), result.refreshTokenExpireTime(), "/api/v1/auth/refresh");
    }

    private void clearTokenCookies(HttpServletResponse response) {

        cookieUtil.clearCookies(response, ACCESS_TOKEN_COOKIE_NAME);
        cookieUtil.clearCookies(response, REFRESH_TOKEN_COOKIE_NAME, REFRESH_TOKEN_PATH);
    }

}
