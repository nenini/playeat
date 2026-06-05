package com.nyamnyam.coach.auth.jwt;

import lombok.Builder;
import lombok.Getter;

/**
 * 로그인 성공 시 클라이언트에 발급하는 JWT 토큰 DTO.
 * accessToken  - API 요청 인증용 (단기)
 * refreshToken - accessToken 재발급용 (장기, Redis 저장)
 */
@Getter
@Builder
public class JwtToken {

    private String grantType;       // "Bearer"
    private String accessToken;
    private String refreshToken;
}
