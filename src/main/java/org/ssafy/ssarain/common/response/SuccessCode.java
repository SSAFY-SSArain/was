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
    USER_ACTIVITY_INFO_SUCCESS(HttpStatus.OK, "내 활동 조회가 완료되었습니다."),
    USER_TOKEN_REFRESH_SUCCESS(HttpStatus.OK, "토큰 재발급이 완료되었습니다."),
    USER_NAME_CHECK_SUCCESS(HttpStatus.OK, "유저 닉네임 중복 여부가 조회되었습니다."),
    USER_SEARCH_SUCCESS(HttpStatus.OK, "유저 검색이 완료되었습니다."),
    USER_UPDATE_SUCCESS(HttpStatus.OK, "유저 정보가 수정되었습니다."),
    USER_PASSWORD_UPDATE_SUCCESS(HttpStatus.OK, "유저 비밀번호가 수정되었습니다."),

    // Brain
    BRAIN_INFO_SUCCESS(HttpStatus.OK, "브레인 정보가 조회되었습니다."),
    BRAIN_CREATED_SUCCESS(HttpStatus.CREATED, "브레인이 생성되었습니다."),
    BRAIN_DELETE_SUCCESS(HttpStatus.NO_CONTENT, "브레인이 삭제되었습니다."),
    BRAIN_UPDATE_SUCCESS(HttpStatus.OK, "브레인 정보가 수정되었습니다."),
    BRAIN_TOPIC_REGISTER_SUCCESS(HttpStatus.NO_CONTENT, "브레인에 주제가 등록되었습니다."),
    BRAIN_TOPIC_DELETE_SUCCESS(HttpStatus.NO_CONTENT, "브레인에서 주제가 제거되었습니다."),
    BRAIN_TOPIC_INFO_SUCCESS(HttpStatus.OK, "브레인의 주제 정보가 조회되었습니다."),
    BRAIN_MEMBER_REQUEST_SUCCESS(HttpStatus.NO_CONTENT, "브레인 가입 신청이 완료되었습니다."),
    BRAIN_MEMBER_DELETE_SUCCESS(HttpStatus.NO_CONTENT, "브레인 회원이 제거되었습니다."),
    BRAIN_AVAILABLE_USER_INFO_SUCCESS(HttpStatus.OK, "브레인에 등록 가능한 회원 정보가 조회되었습니다."),
    BRAIN_JOIN_REQUEST_INFO_SUCCESS(HttpStatus.OK, "브레인 가입 신청 회원 정보가 조회되었습니다."),
    BRAIN_JOIN_MANAGE_SUCCESS(HttpStatus.NO_CONTENT, "브레인 가입 신청 처리가 완료되었습니다."),
    BRAIN_MEMBER_JOIN_SUCCESS(HttpStatus.NO_CONTENT, "브레인에 사용자 추가가 완료되었습니다."),
    BRAIN_MEMBER_INFO_SUCCESS(HttpStatus.OK, "브레인 회원 정보가 조회되었습니다."),
    BRAIN_MEMBER_ROLE_UPDATE_SUCCESS(HttpStatus.NO_CONTENT, "브레인 회원 권한이 변경되었습니다."),
    BRAIN_NAME_VALIDATION_SUCCESS(HttpStatus.OK, "브레인 이름 중복 검사가 완료되었습니다."),
    BRAIN_LEAVE_SUCCESS(HttpStatus.NO_CONTENT, "브레인에서 탈퇴 처리가 완료되었습니다."),
    BRAIN_MERGE_SUCCESS(HttpStatus.NO_CONTENT, "브레인 병합이 완료되었습니다."),

    // Topic
    TOPIC_INFO_SUCCESS(HttpStatus.OK, "주제 정보가 조회되었습니다."),
    TOPIC_SEARCH_SUCCESS(HttpStatus.OK, "해당 토픽들의 검색이 완료되었습니다."),
    TOPIC_CREATE_SUCCESS(HttpStatus.CREATED, "주제가 생성되었습니다."),

    // Quiz
    QUIZ_INFO_SUCCESS(HttpStatus.OK, "퀴즈 조회가 완료되었습니다."),
    QUIZ_CREATE_SUCCESS(HttpStatus.CREATED, "퀴즈 생성이 완료되었습니다."),

    // Neuron
    NEURON_INFO_SUCCESS(HttpStatus.OK, "뉴런 정보가 조회되었습니다."),
    NEURON_CREATE_SUCCESS(HttpStatus.CREATED, "뉴런 생성이 완료되었습니다."),
    NEURON_DELETE_SUCCESS(HttpStatus.NO_CONTENT, "뉴런 삭제가 완료되었습니다."),
    NEURON_LIKE_SUCCESS(HttpStatus.OK, "뉴런 좋아요 추가/취소가 완료되었습니다."),

    // Comment
    COMMENT_CREATE_SUCCESS(HttpStatus.CREATED, "댓글 생성이 완료되었습니다."),
    COMMENT_UPDATE_SUCCESS(HttpStatus.OK, "댓글 수정이 완료되었습니다."),
    COMMENT_DELETE_SUCCESS(HttpStatus.NO_CONTENT, "댓글 삭제가 완료되었습니다.");



    private final HttpStatus status;
    private final String message;
}
