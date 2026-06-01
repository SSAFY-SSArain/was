package org.ssafy.ssarain.common.security.jwt.provider;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.ssafy.ssarain.common.security.jwt.provider.properties.JwtProperties;
import org.ssafy.ssarain.common.security.model.CustomUserDetails;
import org.ssafy.ssarain.domain.user.model.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

import static io.jsonwebtoken.Jwts.SIG.HS512;
import static io.jsonwebtoken.io.Decoders.BASE64;
import static org.ssafy.ssarain.common.security.constant.SecurityConst.JWT_AUTHORITIES_KEY;
import static org.ssafy.ssarain.common.security.constant.SecurityConst.JWT_USERNAME_KEY;

@Component
@Slf4j
public class JwtProvider {

    private final String    issuer;
    private final SecretKey accessTokenKey;
    private final SecretKey refreshTokenKey;
    private final long      accessTokenExpirationMillis;
    private final long      refreshTokenExpirationMillis;

    public JwtProvider(JwtProperties jwtProperties) {
        issuer = jwtProperties.getIssuer();
        accessTokenKey = Keys.hmacShaKeyFor(BASE64.decode(jwtProperties.getAccessTokenSecret()));
        refreshTokenKey = Keys.hmacShaKeyFor(BASE64.decode(jwtProperties.getRefreshTokenSecret()));
        accessTokenExpirationMillis = jwtProperties.getAccessTokenExpirationSeconds() * 1000L;
        refreshTokenExpirationMillis = jwtProperties.getRefreshTokenExpirationSeconds() * 1000L;
    }

    public String generateAccessToken(UUID userId, String email, UserRole role) {
        return generateToken(userId, email, role, false);
    }

    public String generateRefreshToken(UUID userId, String email, UserRole role) {
        return generateToken(userId, email, role, true);
    }

    private String generateToken(UUID userId, String email, UserRole role, boolean isRefreshToken) {
        Date now       = new Date(System.currentTimeMillis());
        Date expiresIn = new Date(now.getTime() + getExpirationMillis(isRefreshToken));

        return Jwts.builder()
                .issuer(issuer)
                .subject(userId.toString())
                .claim(JWT_USERNAME_KEY, email)
                .claim(JWT_AUTHORITIES_KEY, role.name())
                .issuedAt(now)
                .expiration(expiresIn)
                .signWith(getSignKey(isRefreshToken), HS512)
                .compact();
    }

    public long getAccessTokenExpirationSeconds() {
        return accessTokenExpirationMillis / 1000L;
    }

    public long getRefreshTokenExpirationSeconds() {
        return refreshTokenExpirationMillis / 1000L;
    }

    public void validateRefreshToken(final String refreshToken) {
        validateToken(refreshToken, true);
    }

    /*
        Util Method
     */

    private void validateToken(final String token, final boolean isRefreshToken) {
        Jwts.parser()
                .verifyWith(getSigningKey(isRefreshToken))
                .build()
                .parseSignedClaims(token);
    }

    private UUID getUserIdFromToken(String token, boolean isRefreshToken) {
        String subject = Jwts.parser()
                .verifyWith(getSigningKey(isRefreshToken))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();

        return UUID.fromString(subject);

    }

    public UUID getUserIdFromAccessToken(String accessToken) {
        return getUserIdFromToken(accessToken, false);
    }

    public UUID getUserIdFromRefreshToken(String refreshToken) {
        return getUserIdFromToken(refreshToken, true);
    }

    public long getRemainingSecondsFromAccessToken(String accessToken) {
        return getRemainingSeconds(accessToken, false);
    }

    public Authentication getAuthenticationFromAccessToken(String accessToken) {
        return getAuthenticationFromToken(accessToken, false);
    }

    public Authentication getAuthenticationFromRefreshToken(String refreshToken) {
        return getAuthenticationFromToken(refreshToken, true);
    }

    private Authentication getAuthenticationFromToken(String accessToken, boolean isRefreshToken) {
        Claims claims = getClaimsFromToken(accessToken, isRefreshToken);

        UUID userId = UUID.fromString(claims.getSubject());
        String  email  = claims.get(JWT_USERNAME_KEY, String.class);
        UserRole role  = UserRole.from(claims.get(JWT_AUTHORITIES_KEY, String.class));

        CustomUserDetails userDetails = CustomUserDetails.of(userId, email, role);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private long getRemainingSeconds(String token, boolean isRefreshToken) {
        Claims claims = getClaimsFromToken(token, isRefreshToken);
        return (claims.getExpiration().getTime() - System.currentTimeMillis()) / 1000L;
    }

    private SecretKey getSigningKey(boolean isRefreshToken) {
        return isRefreshToken ? refreshTokenKey : accessTokenKey;
    }

    private Claims getClaimsFromToken(String token, boolean isRefreshToken) {
        return Jwts.parser()
                .verifyWith(getSigningKey(isRefreshToken))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignKey(boolean isRefreshToken) {
        return isRefreshToken ? refreshTokenKey : accessTokenKey;
    }

    private long getExpirationMillis(boolean isRefreshToken) {
        return isRefreshToken ? refreshTokenExpirationMillis : accessTokenExpirationMillis;
    }

}
