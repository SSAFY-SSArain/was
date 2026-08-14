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
import org.ssafy.ssarain.domain.user.model.UserRole;
import org.ssafy.ssarain.domain.user.service.UserService;
import org.ssafy.ssarain.infra.mail.service.EmailVerificationService;
import org.ssafy.ssarain.infra.redis.dao.RedisRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
  
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static org.ssafy.ssarain.infra.redis.constant.RedisConst.JWT_REFRESH_TOKEN_PREFIX;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private static final long     GRACE_PERIOD_SECONDS = 5L;

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

    public UserWithTokenRes refresh(String refreshToken) {

        validateRefreshTokenStructure(refreshToken);

        UUID userId = jwtProvider.getUserIdFromRefreshToken(refreshToken);

        String graceRedisKey = getGraceTokenKey(refreshToken);
        Optional<String> graceToken = redisRepository.getValue(graceRedisKey, String.class);

        if(graceToken.isPresent()) {
            String latestRefreshToken = graceToken.get();

            validateRefreshTokenMatch(latestRefreshToken, getRefreshTokenRedisKey(userId));

            return reissueWithGracePeriod(userId, latestRefreshToken);
        }

        validateRefreshTokenMatch(refreshToken, getRefreshTokenRedisKey(userId));

        return reissueWithNewToken(userId, graceRedisKey);

    }

    public void logout(String accessToken) {

        UUID userId = jwtProvider.getUserIdFromAccessToken(accessToken);
        deleteRefreshToken(userId);
    }
    
    public void authorizeAdmin(CustomUserDetails userDetails) {
        if (!isAdmin(userDetails)) {
            throw new GlobalException(ErrorCode.ACCESS_DENIED);
        }
    }

    public boolean isAdmin(CustomUserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .anyMatch(authority -> UserRole.ADMIN.getAuthority().equals(authority.getAuthority()));
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

        TokenRes tokenRes = new TokenRes(accessToken, accessTokenExpiresIn, refreshToken, refreshTokenExpiresIn);

        return createUserWithTokenRes(tokenRes, user);
    }
    
    private UserWithTokenRes createUserWithTokenRes(TokenRes prebuilTokenRes, User user) {
        UserInfoRes userInfoRes = new UserInfoRes(user.getEmail(), user.getName(), user.getRole());
        return new UserWithTokenRes(prebuilTokenRes, userInfoRes);
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

    private UserWithTokenRes reissueWithGracePeriod(UUID userId, String graceToken) {

        User user = userService.getUserByUserId(userId);

        String accessToken = jwtProvider.generateAccessToken(user.getUid(),
                                                            user.getEmail(),
                                                            user.getRole());

        TokenRes tokenRes = new TokenRes(
                accessToken,
                jwtProvider.getAccessTokenExpirationSeconds(),
                graceToken,
                jwtProvider.getRefreshTokenExpirationSeconds()
        );
        return createUserWithTokenRes(tokenRes, user);
    }

    private UserWithTokenRes reissueWithNewToken(UUID userId, String graceRedisKey) {

        User user = userService.getUserByUserId(userId);

        String newAccessToken = jwtProvider.generateAccessToken(user.getUid(), user.getEmail(), user.getRole());
        String newRefreshToken = jwtProvider.generateRefreshToken(user.getUid(), user.getEmail(), user.getRole());

        long accessTokenExpiresIn  = jwtProvider.getAccessTokenExpirationSeconds();
        long refreshTokenExpiresIn = jwtProvider.getRefreshTokenExpirationSeconds();

        // Grace period 설정 (5 초)
        redisRepository.setValue(graceRedisKey, newRefreshToken, Duration.ofSeconds(GRACE_PERIOD_SECONDS));
        // RefreshToken 갱신
        saveRefreshToken(userId, newRefreshToken, refreshTokenExpiresIn);

        TokenRes tokenRes = new TokenRes(
                newAccessToken, accessTokenExpiresIn, newRefreshToken, refreshTokenExpiresIn
        );
        return createUserWithTokenRes(tokenRes, user);
    }
}
