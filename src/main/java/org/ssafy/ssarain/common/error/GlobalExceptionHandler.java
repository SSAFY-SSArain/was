package org.ssafy.ssarain.common.error;

import lombok.extern.slf4j.Slf4j;
import org.ssafy.ssarain.common.response.BaseResponse;
import org.ssafy.ssarain.common.response.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(GlobalException.class)
    protected ResponseEntity<BaseResponse<Void>> handleGlobalException(GlobalException e) {

        log.error(e.getMessage(), e);
        return BaseResponse.error(e.getErrorCode());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<BaseResponse<Void>> handleException(Exception e) {

        log.error(e.getMessage(), e);
        return BaseResponse.error(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    protected ResponseEntity<BaseResponse<Void>> handleCookieException(MissingRequestCookieException e) {

        log.warn("MissingRequestCookieException: {}", e.getMessage(), e);
        return BaseResponse.error(ErrorCode.COOKIE_NOT_EXISTS);
    }
}
