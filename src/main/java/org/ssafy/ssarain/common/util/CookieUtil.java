package org.ssafy.ssarain.common.util;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    public void addCookie(HttpServletResponse response, String name, String value, Long maxAge) {
        addCookie(response, name, value, maxAge, "/");
    }

    public void addCookie(HttpServletResponse response, String name, String value, Long maxAge, String path) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .path(path)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .maxAge(maxAge)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void clearCookies(HttpServletResponse response, String name) {
        clearCookies(response, name, "/");
    }

    public void clearCookies(HttpServletResponse response, String name, String path) {
        addCookie(response, name, "", 0L, path);
    }
}
