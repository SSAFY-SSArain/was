package org.ssafy.ssarain.common.security.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ssafy.ssarain.common.response.ErrorCode;
import org.ssafy.ssarain.common.response.SuccessCode;
import org.ssafy.ssarain.common.security.dto.req.LoginReq;
import org.ssafy.ssarain.common.security.dto.req.SignupReq;
import org.ssafy.ssarain.domain.user.dao.UserRepository;
import org.ssafy.ssarain.domain.user.model.User;
import org.ssafy.ssarain.infra.mail.service.EmailVerificationService;
import org.ssafy.ssarain.infra.redis.dao.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.ssafy.ssarain.common.security.constant.SecurityConst.ACCESS_TOKEN_COOKIE_NAME;
import static org.ssafy.ssarain.common.security.constant.SecurityConst.REFRESH_TOKEN_COOKIE_NAME;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private RedisRepository redisRepository;

    @MockitoBean
    private EmailVerificationService emailVerificationService;

    @BeforeEach
    void setUp() {

        given(redisRepository.getValue(anyString(), eq(String.class))).willReturn(Optional.empty());
    }

    @Test
    void 회원가입하면_유저를_생성하고_토큰_쿠키를_설정한다() throws Exception {

        String email    = "signup-auth-test@example.com";
        String name     = "signup-auth-test";
        String password = "test1234!";
        given(emailVerificationService.isEmailVerified(email)).willReturn(true);

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SignupReq(email, name, password))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(SuccessCode.USER_SIGN_UP_SUCCESS.getStatus().value()))
                .andExpect(jsonPath("$.message").value(SuccessCode.USER_SIGN_UP_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.email").value(email))
                .andExpect(jsonPath("$.data.name").value(name))
                .andExpect(cookie().exists(ACCESS_TOKEN_COOKIE_NAME))
                .andExpect(cookie().exists(REFRESH_TOKEN_COOKIE_NAME));

        User savedUser = userRepository.findByEmail(email).orElseThrow();

        assertThat(savedUser.getName()).isEqualTo(name);
        assertThat(passwordEncoder.matches(password, savedUser.getPassword())).isTrue();
    }

    @Test
    void 로그인_성공시_토큰_쿠키를_설정한다() throws Exception {

        String email    = "login-auth-test@example.com";
        String name     = "login-auth-test";
        String password = "test1234!";
        saveUser(email, name, password);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginReq(email, password))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(SuccessCode.USER_LOGIN_SUCCESS.getStatus().value()))
                .andExpect(jsonPath("$.message").value(SuccessCode.USER_LOGIN_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.email").value(email))
                .andExpect(jsonPath("$.data.name").value(name))
                .andExpect(cookie().exists(ACCESS_TOKEN_COOKIE_NAME))
                .andExpect(cookie().exists(REFRESH_TOKEN_COOKIE_NAME));
    }

    @Test
    void 비밀번호가_다르면_로그인에_실패한다() throws Exception {

        String email = "login-fail-auth-test@example.com";
        saveUser(email, "login-fail-auth-test", "test1234!");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginReq(email, "wrong-password"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_AUTH_INFO_INCORRECT.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_AUTH_INFO_INCORRECT.getMessage()))
                .andExpect(cookie().doesNotExist(ACCESS_TOKEN_COOKIE_NAME))
                .andExpect(cookie().doesNotExist(REFRESH_TOKEN_COOKIE_NAME));
    }

    @Test
    void 유효한_RT가_있다면_토큰을_재발급한다() throws Exception {

        String email    = "refresh-auth-test@example.com";
        String name     = "refresh-auth-test";
        String password = "test1234!";
        saveUser(email, name, password);

        Cookie refreshToken = loginAndGetCookie(email, password, REFRESH_TOKEN_COOKIE_NAME);

        // 저장된 RT는 존재하고, grace token은 아직 없다는 전제
        given(redisRepository.getValue(anyString(), eq(String.class))).willAnswer(invocation -> {
            String key = invocation.getArgument(0);
            if (key.startsWith("jwt:refresh:grace:")) {
                return Optional.empty();
            }
            return Optional.of(refreshToken.getValue());
        });

        mockMvc.perform(get("/api/v1/auth/refresh").cookie(refreshToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(SuccessCode.USER_TOKEN_REFRESH_SUCCESS.getStatus().value()))
                .andExpect(jsonPath("$.message").value(SuccessCode.USER_TOKEN_REFRESH_SUCCESS.getMessage()))
                .andExpect(cookie().exists(ACCESS_TOKEN_COOKIE_NAME))
                .andExpect(cookie().exists(REFRESH_TOKEN_COOKIE_NAME));
    }

    @Test
    void 로그아웃_하면_토큰이_삭제된다() throws Exception {

        String email    = "logout-auth-test@example.com";
        String name     = "logout-auth-test";
        String password = "test1234!";
        saveUser(email, name, password);

        Cookie accessToken = loginAndGetCookie(email, password, ACCESS_TOKEN_COOKIE_NAME);

        mockMvc.perform(post("/api/v1/auth/logout").cookie(accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(SuccessCode.USER_LOGOUT_SUCCESS.getStatus().value()))
                .andExpect(jsonPath("$.message").value(SuccessCode.USER_LOGOUT_SUCCESS.getMessage()))
                .andExpect(cookie().maxAge(ACCESS_TOKEN_COOKIE_NAME, 0))
                .andExpect(cookie().maxAge(REFRESH_TOKEN_COOKIE_NAME, 0));
    }

    private Cookie loginAndGetCookie(String email, String password, String cookieName) throws Exception {
        return mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginReq(email, password))))
                .andReturn()
                .getResponse()
                .getCookie(cookieName);
    }

    private void saveUser(String email, String name, String password) {
        userRepository.save(User.of(email, name, passwordEncoder.encode(password)));
    }
}
