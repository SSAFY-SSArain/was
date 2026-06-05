package org.ssafy.ssarain.common.security.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ssafy.ssarain.common.response.BaseResponse;
import org.ssafy.ssarain.common.response.ErrorCode;
import org.ssafy.ssarain.common.response.SuccessCode;
import org.ssafy.ssarain.common.security.dto.req.EmailVerifyCodeReq;
import org.ssafy.ssarain.common.security.dto.req.EmailVerifyReq;
import org.ssafy.ssarain.infra.mail.service.EmailVerificationService;

@RestController
@RequestMapping("/api/v1/auth/email")
@RequiredArgsConstructor
public class EmailAuthController {

    private final EmailVerificationService emailVerificationService;

    @Operation(summary = "A05: 이메일 인증번호 전송")
    @PostMapping("/request")
    public ResponseEntity<BaseResponse<Void>> requestVerificationCode(
            @RequestBody EmailVerifyReq emailVerifyReq
    ) {

        emailVerificationService.sendVerificationCode(emailVerifyReq);

        return BaseResponse.success(SuccessCode.EMAIL_VERIFICATION_CODE_SEND_SUCCESS);
    }

    @Operation(summary = "A06: 이메일 인증번호 검증")
    @PostMapping("/verify")
    public ResponseEntity<BaseResponse<Void>> verifyCode(
            @RequestBody EmailVerifyCodeReq emailVerifyCodeReq
    ) {

        boolean isVerified = emailVerificationService.verifyCode(emailVerifyCodeReq);

        if(isVerified) {
            return BaseResponse.success(SuccessCode.EMAIL_VERIFICATION_CODE_VERIFY_SUCCESS);
        }
        return BaseResponse.error(ErrorCode.EMAIL_VERIFY_CODE_INCORRECT);
    }
}
