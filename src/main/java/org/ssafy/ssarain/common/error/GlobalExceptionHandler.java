package org.ssafy.ssarain.common.error;

import lombok.extern.slf4j.Slf4j;
import org.ssafy.ssarain.common.response.BaseResponse;
import org.ssafy.ssarain.common.response.ErrorCode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	private static final int MAX_LOG_ELEMENT_LENGTH = 100;
	private static final int MAX_LOG_TOTAL_LENGTH = 10000;
	
    @ExceptionHandler(GlobalException.class)
    protected ResponseEntity<BaseResponse<Void>> handleGlobalException(GlobalException e) {

        HttpStatusCode statusCode = e.getErrorCode().getStatus();

        logByStatus(statusCode, e);

        return BaseResponse.error(e.getErrorCode());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<BaseResponse<Void>> handleException(Exception e) {

        log.error(toSafeLogString(e.getMessage()), e);
        return BaseResponse.error(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    protected ResponseEntity<BaseResponse<Void>> handleCookieException(MissingRequestCookieException e) {

        log.warn("MissingRequestCookieException: {}", toSafeLogString(e.getMessage()));
        return BaseResponse.error(ErrorCode.COOKIE_NOT_EXISTS);
    }
    
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception e, Object body, HttpHeaders headers,
            HttpStatusCode statusCode, WebRequest request) {

        logByStatus(statusCode, e);
        
        ErrorCode errorCode = ErrorCode.getCommonErrorCode((HttpStatus)statusCode);
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(BaseResponse.of(errorCode));
    }
    
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
    		HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    	
    	StringBuilder errorLog = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            
            Object rejectedValue = error.getRejectedValue(); 
            String valueString = toSafeElementString(rejectedValue);
            
            String log = String.format("Validation failed for field [%s]. Value: [%s]. Reason: %s", 
            		fieldName, valueString, errorMessage);

            errorLog.append(log).append('\n');
        });

        if (0 < errorLog.length()) {
        	log.warn("MethodArgumentNotValidException: {}", errorLog);
        } 
        
        return ResponseEntity
                .status(ErrorCode.BAD_REQUEST.getStatus())
                .body(BaseResponse.of(ErrorCode.BAD_REQUEST));
    }

    private String toSafeString(Object value, int limit) {
        if (value == null) return "null";
        String strValue = (value instanceof String s ? s : value.toString());
        if (strValue.length() > MAX_LOG_ELEMENT_LENGTH) {
            return strValue.substring(0, MAX_LOG_ELEMENT_LENGTH) + "... (truncated)"; 
        }
        return strValue;
    }
    
    private String toSafeElementString(Object value) {
    	return toSafeString(value, MAX_LOG_ELEMENT_LENGTH);
    }
    
    private String toSafeLogString(Object value) {
    	return toSafeString(value, MAX_LOG_TOTAL_LENGTH);
    }

    private void logByStatus(HttpStatusCode statusCode, Exception e) {

        if (statusCode.is5xxServerError()) {
            log.error(toSafeLogString(e.getMessage()), e);
            return;
        }

        log.warn("{}: [{}] {}", e.getClass().getSimpleName(), statusCode.value(), toSafeLogString(e.getMessage()));
    }
}
