package org.ssafy.ssarain.domain.user.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ssafy.ssarain.common.response.ErrorCode;
import org.ssafy.ssarain.common.response.SuccessCode;
import org.ssafy.ssarain.common.security.dto.req.LoginReq;
import org.ssafy.ssarain.domain.user.dao.UserRepository;
import org.ssafy.ssarain.domain.user.dto.req.NameCheckReq;
import org.ssafy.ssarain.domain.user.model.User;
import org.ssafy.ssarain.infra.redis.dao.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.ssafy.ssarain.common.security.constant.SecurityConst.ACCESS_TOKEN_COOKIE_NAME;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerTest {

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

    @BeforeEach
    void setUp() {

        given(redisRepository.getValue(anyString(), eq(String.class))).willReturn(Optional.empty());
    }

    @Test
    void 인증되지_않은_사용자는_사용자_정보_조회에_실패한다() throws Exception {
        mockMvc.perform(get("/api/v1/user"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.UNAUTHORIZED.getMessage()));
    }

    @Test
    void 사용자_정보를_조회한다() throws Exception {
        String email    = "me-user-test@example.com";
        String name     = "me-user-test";
        String password = "test1234!";
        saveUser(email, name, password);

        Cookie accessToken = loginAndGetCookie(email, password, ACCESS_TOKEN_COOKIE_NAME);

        mockMvc.perform(get("/api/v1/user").cookie(accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(SuccessCode.USER_INFO_SUCCESS.getStatus().value()))
                .andExpect(jsonPath("$.message").value(SuccessCode.USER_INFO_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.user.email").value(email))
                .andExpect(jsonPath("$.data.user.name").value(name))
                .andExpect(jsonPath("$.data.user.role").value("USER"))
                .andExpect(jsonPath("$.data.summary.neuronCount").value(0))
                .andExpect(jsonPath("$.data.summary.commentCount").value(0))
                .andExpect(jsonPath("$.data.summary.likeCount").value(0));
    }

    @Test
    void 이름_중복_검사는_인증_없이_호출할_수_있다() throws Exception {

        String email    = "duplicate-user-test@example.com";
        String name     = "duplicate-user-test";
        String password = "test1234!";
        saveUser(email, name, password);

        mockMvc.perform(post("/api/v1/user/name-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new NameCheckReq(name))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(SuccessCode.USER_NAME_CHECK_SUCCESS.getStatus().value()))
                .andExpect(jsonPath("$.message").value(SuccessCode.USER_NAME_CHECK_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.isDuplicate").value(true));
    }

    @Test
    void 유저를_검색한다() throws Exception {
        String requesterEmail = "requester-user-search-test@example.com";
        String requesterName  = "requester-user-search-test";
        String password       = "test1234!";
        saveUser(requesterEmail, requesterName, password);
        saveUser("member-alpha-search-test@example.com", "member-alpha-search-test", password);
        saveUser("member-beta-search-test@example.com", "member-beta-search-test", password);
        saveUser("other-user-search-test@example.com", "other-user-search-test", password);

        Cookie accessToken = loginAndGetCookie(requesterEmail, password, ACCESS_TOKEN_COOKIE_NAME);

        mockMvc.perform(get("/api/v1/user/search")
                        .param("keyword", "member")
                        .param("page", "0")
                        .param("size", "10")
                        .cookie(accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(SuccessCode.USER_SEARCH_SUCCESS.getStatus().value()))
                .andExpect(jsonPath("$.message").value(SuccessCode.USER_SEARCH_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.users.length()").value(2))
                .andExpect(jsonPath("$.data.users[0].name").value("member-alpha-search-test"))
                .andExpect(jsonPath("$.data.users[1].name").value("member-beta-search-test"))
                .andExpect(jsonPath("$.data.currentPage").value(0))
                .andExpect(jsonPath("$.data.pageSize").value(10))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.hasNext").value(false));
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
