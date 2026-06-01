package org.ssafy.ssarain.common.security.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ssafy.ssarain.common.response.BaseResponse;
import org.ssafy.ssarain.common.response.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.ssafy.ssarain.common.response.ErrorCode.USER_AUTH_ACCESS_TOKEN_EXPIRED;
import static org.ssafy.ssarain.common.response.ErrorCode.USER_AUTH_INVALID_ACCESS_TOKEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명", e);
            sendErrorResponse(response, USER_AUTH_INVALID_ACCESS_TOKEN);
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT", e);
            sendErrorResponse(response, USER_AUTH_ACCESS_TOKEN_EXPIRED);
        } catch (UnsupportedJwtException e) {
            log.error("지원하지 않는 JWT 형식", e);
            sendErrorResponse(response, USER_AUTH_INVALID_ACCESS_TOKEN);
        } catch (JwtException e) {
            log.error("알 수 없는 JWT 에러", e);
            sendErrorResponse(response, USER_AUTH_INVALID_ACCESS_TOKEN);
        }

    }

    private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        BaseResponse<Void> baseResponse = BaseResponse.of(errorCode);

        response.setStatus(Integer.parseInt(baseResponse.code()));
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(UTF_8.name());

        objectMapper.writeValue(response.getWriter(), baseResponse);
    }

}
