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
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "C006", "잘못된 형식의 요청입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "C007", "요청한 리소스를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C008", "해당 메서드는 지원되지 않습니다."),
    NOT_ACCEPTABLE(HttpStatus.NOT_ACCEPTABLE, "C009", "응답할 수 없는 형식을 요청했습니다."),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "C010", "지원되지 않는 media-type입니다."),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "C011", "현재 해당 요청을 처리할 수 없습니다."),
    RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "C012", "요청 횟수를 초과했습니다. 잠시 후 다시 시도해주세요."),

    // Email
    EMAIL_VERIFY_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "EM01", "인증 코드가 만료되었거나 존재하지 않습니다."),
    EMAIL_VERIFY_CODE_INCORRECT(HttpStatus.BAD_REQUEST, "EM02", "유효하지 않은 인증 코드입니다."),
    EMAIL_IS_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "EM03", "인증되지 않은 이메일입니다."),
    
    // ErrorCode
    NOT_ERROR_STATUS(HttpStatus.INTERNAL_SERVER_ERROR, "EC001","에러 코드가 아닌 status를 에러 코드로 변환하려 시도했습니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "US001","해당 유저를 찾을 수 없습니다."),
    USER_EMAIL_DUPLICATED(HttpStatus.BAD_REQUEST, "US002", "이미 사용 중인 이메일 주소입니다."),
    USER_NAME_DUPLICATED(HttpStatus.BAD_REQUEST, "US003", "이미 사용 중인 이름입니다."),
    USER_ROLE_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "US004", "지원하지 않는 권한입니다."),
    USER_AUTH_INFO_INCORRECT(HttpStatus.BAD_REQUEST, "US005", "잘못된 이메일 혹은 비밀번호입니다."),
    USER_AUTH_INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "US006", "잘못된 AccessToken입니다."),
    USER_AUTH_ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "US007", "만료된 AccessToken입니다."),
    USER_AUTH_INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "US008", "잘못된 RefreshToken입니다."),
    USER_AUTH_REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "US009", "만료된 RefreshToken입니다."),

    // Brain
    BRAIN_NOT_FOUND(HttpStatus.NOT_FOUND, "B001", "해당 브레인을 찾을 수 없습니다."),
    BRAIN_NAME_DUPLICATED(HttpStatus.CONFLICT, "B002", "브레인 이름이 이미 존재합니다."),
    BRAIN_OR_TOPIC_NOT_FOUND(HttpStatus.NOT_FOUND, "B003", "브레인 또는 주제를 찾을 수 없습니다."),
    BRAIN_TOPIC_NOT_FOUND(HttpStatus.NOT_FOUND, "B004", "브레인에 속한 주제를 찾을 수 없습니다."),
    BRAIN_MEMBER_DUPLICATED(HttpStatus.CONFLICT, "B005", "이미 브레인에 회원으로 소속되어 있습니다."),
    BRAIN_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "B006", "브레인 회원 정보를 찾을 수 없습니다."),
    BRAIN_WAITING_NOT_FOUND(HttpStatus.NOT_FOUND, "B007", "브레인 가입 신청 내역이 없습니다."),
    BRAIN_MEMBER_CANNOT_DELETE_SELF(HttpStatus.UNPROCESSABLE_ENTITY, "B008", "브레인 관리자 자신을 제외시킬 수 없습니다."),
    BRAIN_WAITING_ALREADY_EXISTS(HttpStatus.CONFLICT, "B009", "이미 해당 브레인에 가입 신청을 했습니다."),
    BRAIN_MEMBER_DELETION_DENIED(HttpStatus.FORBIDDEN, "B010", "해당 사용자를 Brain에서 삭제할 권한이 부족합니다."),
    
    // Topic
    TOPIC_NOT_FOUND(HttpStatus.NOT_FOUND, "T001", "해당 주제를 찾을 수 없습니다."),
    TOPIC_NAME_DUPLICATED(HttpStatus.CONFLICT, "T002", "이미 존재하는 주제입니다."),

    // Quiz
    QUIZ_SOURCE_NEURON_NOT_FOUND(HttpStatus.NOT_FOUND, "Q001", "퀴즈 생성에 사용할 뉴런이 없습니다."),
    QUIZ_GENERATION_LIMIT_EXCEEDED(HttpStatus.CONFLICT, "Q002", "해당 BrainTopic은 퀴즈를 더 이상 생성할 수 없습니다."),

    // Neuron
    NEURON_NOT_FOUND(HttpStatus.NOT_FOUND, "N001", "해당 뉴런을 찾을 수 없습니다."),

    // Comment
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "CM001", "해당 댓글/답글를 찾을 수 없습니다."),

    // Gemini
    GEMINI_API_KEY_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "G001", "Gemini API 키가 설정되지 않았습니다."),
    GEMINI_REQUEST_FAILED(HttpStatus.BAD_GATEWAY, "G002", "Gemini API 요청에 실패했습니다."),
    GEMINI_INVALID_RESPONSE(HttpStatus.BAD_GATEWAY, "G003", "Gemini API 응답 형식이 올바르지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
    
    public static ErrorCode getCommonErrorCode(HttpStatus status) {
        if (status == null) {
            return NOT_ERROR_STATUS;
        }
        
        if (status.is4xxClientError()) {
            return switch (status) {
            case BAD_REQUEST -> BAD_REQUEST;
            case NOT_FOUND -> NOT_FOUND;
            case METHOD_NOT_ALLOWED -> METHOD_NOT_ALLOWED;
            case NOT_ACCEPTABLE -> NOT_ACCEPTABLE;
            case UNSUPPORTED_MEDIA_TYPE -> UNSUPPORTED_MEDIA_TYPE;
            default -> BAD_REQUEST;
            };
        }
        
        if (status.is5xxServerError()) {
            return switch (status) {
            case SERVICE_UNAVAILABLE -> SERVICE_UNAVAILABLE;
            default -> INTERNAL_SERVER_ERROR;
            };
        }
        
        // Not Error
        return NOT_ERROR_STATUS;
    }
}
