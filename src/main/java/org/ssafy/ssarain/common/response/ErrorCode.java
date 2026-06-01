package org.ssafy.ssarain.common.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "유효하지 않은 입력 값입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN,"C002", "접근 권한이 없습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "C003","인증 정보가 유효하지 않습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C004","서버 내부 오류가 발생했습니다."),
    COOKIE_NOT_EXISTS(HttpStatus.BAD_REQUEST, "C005","쿠키가 누락되었습니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "US001","해당 유저를 찾을 수 없습니다."),
    USER_EMAIL_DUPLICATED(HttpStatus.BAD_REQUEST, "US002", "이미 사용 중인 이메일 주소입니다."),
    USER_NICKNAME_DUPLICATED(HttpStatus.BAD_REQUEST, "US003", "이미 사용 중인 닉네임입니다."),
    USER_ROLE_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "US004", "지원하지 않는 권한입니다."),
    USER_AUTH_INFO_INCORRECT(HttpStatus.BAD_REQUEST, "US005", "잘못된 이메일 혹은 비밀번호입니다."),
    USER_AUTH_INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "US006", "잘못된 AccessToken입니다."),
    USER_AUTH_ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "US007", "만료된 AccessToken입니다."),
    USER_AUTH_INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "US008", "잘못된 RefreshToken입니다."),
    USER_AUTH_REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "US009", "만료된 RefreshToken입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
