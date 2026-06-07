package com.nyamnyam.coach.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.AuthErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * JWT 토큰 생성 / 검증 / 파싱 컴포넌트 (jjwt 0.12.x API 기준)
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private static final String GRANT_TYPE      = "Bearer";

    private final SecretKey key;
    private final long accessTokenExp;
    private final long refreshTokenExp;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenExp,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExp
    ) {
        this.key            = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessTokenExp  = accessTokenExp;
        this.refreshTokenExp = refreshTokenExp;
    }

    // ─────────────────────────────────────────
    // 토큰 생성
    // ─────────────────────────────────────────

    public JwtToken generateToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = System.currentTimeMillis();

        String accessToken = Jwts.builder()
                .subject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .issuedAt(new Date(now))
                .expiration(new Date(now + accessTokenExp))
                .signWith(key)
                .compact();

        String refreshToken = Jwts.builder()
                .subject(authentication.getName())
                .issuedAt(new Date(now))
                .expiration(new Date(now + refreshTokenExp))
                .signWith(key)
                .compact();

        return JwtToken.builder()
                .grantType(GRANT_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // ─────────────────────────────────────────
    // 토큰 → Authentication 복원
    // ─────────────────────────────────────────

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new BusinessException(AuthErrorCode.INVALID_TOKEN);
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // ─────────────────────────────────────────
    // 검증
    // ─────────────────────────────────────────

    public boolean validateToken(String token) {
        try {
            getJwtParser().parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    public void validateRefreshTokenForRefresh(String token) {
        try {
            getJwtParser().parseSignedClaims(token);
        } catch (ExpiredJwtException e) {
            log.warn("Expired refresh token: {}", e.getMessage());
            throw new BusinessException(AuthErrorCode.REFRESH_TOKEN_EXPIRED);
        } catch (io.jsonwebtoken.security.SignatureException | MalformedJwtException e) {
            log.warn("Invalid refresh token signature: {}", e.getMessage());
            throw new BusinessException(AuthErrorCode.INVALID_TOKEN);
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported refresh token: {}", e.getMessage());
            throw new BusinessException(AuthErrorCode.INVALID_TOKEN);
        } catch (IllegalArgumentException e) {
            log.warn("Refresh token claims string is empty: {}", e.getMessage());
            throw new BusinessException(AuthErrorCode.INVALID_TOKEN);
        }
    }

    public void validateRefreshTokenSignatureAllowExpired(String token) {
        try {
            getJwtParser().parseSignedClaims(token);
        } catch (ExpiredJwtException e) {
            log.warn("Expired refresh token during logout: {}", e.getMessage());
        } catch (io.jsonwebtoken.security.SignatureException | MalformedJwtException e) {
            log.warn("Invalid refresh token signature: {}", e.getMessage());
            throw new BusinessException(AuthErrorCode.INVALID_TOKEN);
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported refresh token: {}", e.getMessage());
            throw new BusinessException(AuthErrorCode.INVALID_TOKEN);
        } catch (IllegalArgumentException e) {
            log.warn("Refresh token claims string is empty: {}", e.getMessage());
            throw new BusinessException(AuthErrorCode.INVALID_TOKEN);
        }
    }

    // ─────────────────────────────────────────
    // 내부 유틸
    // ─────────────────────────────────────────

    /**
     * 만료된 토큰에서도 Claims 추출 가능 (refresh 로직에서 subject 꺼낼 때 사용)
     */
    public Claims parseClaims(String token) {
        try {
            return getJwtParser().parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    private JwtParser getJwtParser() {
        return Jwts.parser()
                .verifyWith(key)
                .build();
    }

    public long getAccessTokenExpiresInSeconds() {
        return accessTokenExp / 1000;
    }

    public Duration getRefreshTokenExpirationDuration() {
        return Duration.ofMillis(refreshTokenExp);
    }
}
