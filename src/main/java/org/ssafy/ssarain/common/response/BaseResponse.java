package org.ssafy.ssarain.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import org.springframework.http.ResponseEntity;

@Builder(access = AccessLevel.PRIVATE)
@JsonInclude(Include.NON_NULL)
public record BaseResponse<T>(@NotNull String code, @NotNull Integer status, @NotBlank String message, T data) {

    // --- 성공 응답 처리 ---

    public static <T> ResponseEntity<BaseResponse<T>> success(final SuccessCode successCode) {
        return ResponseEntity
                .status(successCode.getStatus())
                .body(BaseResponse.<T>builder()
                        .status(successCode.getStatus().value())
                        .message(successCode.getMessage())
                        .build());
    }

    public static <T> ResponseEntity<BaseResponse<T>> success(final SuccessCode successCode, final T data) {
        return ResponseEntity
                .status(successCode.getStatus())
                .body(BaseResponse.<T>builder()
                        .status(successCode.getStatus().value())
                        .message(successCode.getMessage())
                        .data(data)
                        .build());
    }

    // --- 에러 응답 처리 ---

    public static <T> ResponseEntity<BaseResponse<T>> error(final ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(BaseResponse.<T>builder()
                        .status(errorCode.getStatus().value())
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    // 상황에 따라 메시지를 직접 입력하고 싶을 때
    public static <T> ResponseEntity<BaseResponse<T>> error(final ErrorCode errorCode, final String message) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(BaseResponse.<T>builder()
                        .status(errorCode.getStatus().value())
                        .code(errorCode.getCode())
                        .message(message)
                        .build());
    }

    public static <T> BaseResponse<T> of(final ErrorCode errorCode) {
        return BaseResponse.<T>builder()
                .status(errorCode.getStatus().value())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
    }
}