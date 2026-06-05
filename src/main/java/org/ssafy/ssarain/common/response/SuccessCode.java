package org.ssafy.ssarain.common.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SuccessCode {

    // Common
    REQUEST_SUCCESS(HttpStatus.OK, "요청이 성공적으로 처리되었습니다."),
    CREATE_SUCCESS(HttpStatus.CREATED, "성공적으로 생성되었습니다."),
    UPDATE_SUCCESS(HttpStatus.OK, "성공적으로 수정되었습니다."),
    DELETE_SUCCESS(HttpStatus.OK, "성공적으로 삭제되었습니다."),

    // Email
    EMAIL_VERIFICATION_CODE_SEND_SUCCESS(HttpStatus.OK, "인증 코드가 발송되었습니다."),
    EMAIL_VERIFICATION_CODE_VERIFY_SUCCESS(HttpStatus.OK, "이메일 인증이 완료되었습니다."),

    // User
    USER_SIGN_UP_SUCCESS(HttpStatus.CREATED, "회원가입이 완료되었습니다."),
    USER_LOGIN_SUCCESS(HttpStatus.OK, "로그인 되었습니다."),
    USER_LOGOUT_SUCCESS(HttpStatus.OK, "로그아웃 되었습니다."),
    USER_INFO_SUCCESS(HttpStatus.OK, "유저 정보가 조회되었습니다."),
    USER_TOKEN_REFRESH_SUCCESS(HttpStatus.OK, "토큰 재발급이 완료되었습니다."),
    USER_NAME_CHECK_SUCCESS(HttpStatus.OK, "유저 닉네임 중복 여부가 조회되었습니다."),
    
    // Brain
    BRAIN_INFO_SUCCESS(HttpStatus.OK, "브레인 정보가 조회되었습니다."),
    BRAIN_CREATED_SUCCESS(HttpStatus.CREATED, "브레인이 생성되었습니다."),

    // Topic
    TOPIC_INFO_SUCCESS(HttpStatus.OK, "주제 정보가 조회되었습니다."),
    TOPIC_CREATE_SUCCESS(HttpStatus.CREATED, "주제가 생성되었습니다.");

    private final HttpStatus status;
    private final String message;
}
