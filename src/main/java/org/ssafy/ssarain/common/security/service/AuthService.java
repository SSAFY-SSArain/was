package org.ssafy.ssarain.common.security.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ssafy.ssarain.common.error.GlobalException;
import org.ssafy.ssarain.common.response.ErrorCode;
import org.ssafy.ssarain.common.security.dto.req.LoginReq;
import org.ssafy.ssarain.common.security.dto.req.SignupReq;
import org.ssafy.ssarain.common.security.dto.res.TokenRes;
import org.ssafy.ssarain.common.security.dto.res.UserInfoRes;
import org.ssafy.ssarain.common.security.dto.res.UserWithTokenRes;
import org.ssafy.ssarain.common.security.jwt.provider.JwtProvider;
import org.ssafy.ssarain.common.security.model.CustomUserDetails;
import org.ssafy.ssarain.domain.user.model.User;
import org.ssafy.ssarain.domain.user.service.UserService;
import org.ssafy.ssarain.infra.mail.service.EmailVerificationService;
import org.ssafy.ssarain.infra.redis.dao.RedisRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

import static org.ssafy.ssarain.infra.redis.constant.RedisConst.JWT_REFRESH_TOKEN_PREFIX;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private static final long     GRACE_PERIOD_SECONDS = 30L;

    private final UserService     userService;
    private final JwtProvider     jwtProvider;
    private final RedisRepository redisRepository;
    private final EmailVerificationService emailVerificationService;

    public UserWithTokenRes signup(SignupReq dto) {

        if(!emailVerificationService.isEmailVerified(dto.email())) {
            throw new GlobalException(ErrorCode.EMAIL_IS_NOT_VERIFIED);
        }

        User user = userService.createUser(dto);
        return createUserWithTokenRes(user);
    }

    public UserWithTokenRes login(LoginReq dto) {

        User user = userService.login(dto.email(), dto.password());
        return createUserWithTokenRes(user);
    }

    public TokenRes refresh(String refreshToken) {

        validateRefreshTokenStructure(refreshToken);

        UUID userId = jwtProvider.getUserIdFromRefreshToken(refreshToken);
        String refreshTokenRedisKey  = getRefreshTokenRedisKey(userId);

        validateRefreshTokenMatch(refreshToken, refreshTokenRedisKey);

        String graceRedisKey = getGraceTokenKey(refreshToken);

        return redisRepository.getValue(graceRedisKey, String.class)
                .map(token -> reissueWithGracePeriod(token, refreshToken))
                .orElseGet(() -> reissueWithNewToken(userId, graceRedisKey));

    }

    public void logout(String accessToken) {

        UUID userId = jwtProvider.getUserIdFromAccessToken(accessToken);
        deleteRefreshToken(userId);
    }

    /*
        Util Method
     */

    private UserWithTokenRes createUserWithTokenRes(User user) {

        String accessToken  = jwtProvider.generateAccessToken(user.getUid(), user.getEmail(), user.getRole());
        String refreshToken = jwtProvider.generateRefreshToken(user.getUid(), user.getEmail(), user.getRole());

        long accessTokenExpiresIn  = jwtProvider.getAccessTokenExpirationSeconds();
        long refreshTokenExpiresIn = jwtProvider.getRefreshTokenExpirationSeconds();

        saveRefreshToken(user.getUid(), refreshToken, refreshTokenExpiresIn);

        UserInfoRes userInfo = new UserInfoRes(user.getEmail(), user.getName());
        TokenRes    tokenRes = new TokenRes(accessToken, accessTokenExpiresIn, refreshToken, refreshTokenExpiresIn);

        return new UserWithTokenRes(tokenRes, userInfo);
    }

    private void saveRefreshToken(UUID userId, String refreshToken, long refreshTokenExpiresIn) {

        redisRepository.setValue(
                getRefreshTokenRedisKey(userId),
                refreshToken,
                Duration.ofSeconds(refreshTokenExpiresIn)
        );
    }

    private void deleteRefreshToken(UUID userId) {

        if(userId != null) redisRepository.deleteData(getRefreshTokenRedisKey(userId));
    }

    private String getRefreshTokenRedisKey(UUID userId) {

        return JWT_REFRESH_TOKEN_PREFIX + userId;
    }

    private void validateRefreshTokenMatch(String refreshToken, String refreshTokenRedisKey) {

        redisRepository.getValue(refreshTokenRedisKey, String.class)
                .filter(savedToken -> savedToken.equals(refreshToken))
                .orElseThrow(() -> {
                    log.error("RefreshToken 불일치: 저장된 토큰이 없음 또는 불일치");
                    return new GlobalException(ErrorCode.USER_AUTH_INVALID_REFRESH_TOKEN);
                });
    }

    private void validateRefreshTokenStructure(String refreshToken) {

        try {
            jwtProvider.validateRefreshToken(refreshToken);
        } catch (ExpiredJwtException e) {
            log.error("RefreshToken 만료: {}", e.getMessage());
            throw new GlobalException(ErrorCode.USER_AUTH_REFRESH_TOKEN_EXPIRED);
        } catch (SecurityException | MalformedJwtException e) {
            log.error("RefreshToken 서명 오류: {}", e.getMessage());
            throw new GlobalException(ErrorCode.USER_AUTH_INVALID_REFRESH_TOKEN);
        } catch (UnsupportedJwtException e) {
            log.error("지원하지 않는 RefreshToken 형식: {}", e.getMessage());
            throw new GlobalException(ErrorCode.USER_AUTH_INVALID_REFRESH_TOKEN);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 RefreshToken 형식: {}", e.getMessage());
            throw new GlobalException(ErrorCode.USER_AUTH_INVALID_REFRESH_TOKEN);
        } catch (Exception e) {
            log.error("예상치 못한 RefreshToken 오류: {}", e.getMessage(), e);
            throw new GlobalException(ErrorCode.USER_AUTH_INVALID_REFRESH_TOKEN);
        }
    }

    private String getGraceTokenKey(String refreshToken) {

        return "%sgrace:%s".formatted(JWT_REFRESH_TOKEN_PREFIX, refreshToken);
    }

    private TokenRes reissueWithGracePeriod(String graceToken, String refreshToken) {

        Authentication authentication = jwtProvider.getAuthenticationFromRefreshToken(refreshToken);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String accessToken = jwtProvider.generateAccessToken(userDetails.getUserId(),
                                                            userDetails.getUsername(),
                                                            userDetails.getRole());

        return new TokenRes(
                accessToken,
                jwtProvider.getAccessTokenExpirationSeconds(),
                graceToken,
                jwtProvider.getRefreshTokenExpirationSeconds()
        );
    }

    private TokenRes reissueWithNewToken(UUID userId, String graceRedisKey) {

        User user = userService.getUserByUserId(userId);

        String newAccessToken = jwtProvider.generateAccessToken(user.getUid(), user.getEmail(), user.getRole());
        String newRefreshToken = jwtProvider.generateRefreshToken(user.getUid(), user.getEmail(), user.getRole());

        long accessTokenExpiresIn  = jwtProvider.getAccessTokenExpirationSeconds();
        long refreshTokenExpiresIn = jwtProvider.getRefreshTokenExpirationSeconds();

        // Grace period 설정 (30 초)
        redisRepository.setValue(graceRedisKey, newRefreshToken, Duration.ofSeconds(GRACE_PERIOD_SECONDS));
        // RefreshToken 갱신
        saveRefreshToken(userId, newRefreshToken, refreshTokenExpiresIn);

        return new TokenRes(
                newAccessToken, accessTokenExpiresIn, newRefreshToken, refreshTokenExpiresIn
        );
    }
}
