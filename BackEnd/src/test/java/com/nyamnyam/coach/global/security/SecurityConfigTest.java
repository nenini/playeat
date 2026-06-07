package com.nyamnyam.coach.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nyamnyam.coach.auth.dto.request.LogoutRequest;
import com.nyamnyam.coach.auth.dto.request.SignupRequest;
import com.nyamnyam.coach.auth.dto.response.LogoutResponse;
import com.nyamnyam.coach.auth.dto.response.SignupResponse;
import com.nyamnyam.coach.auth.jwt.JwtToken;
import com.nyamnyam.coach.auth.jwt.JwtTokenProvider;
import com.nyamnyam.coach.auth.service.AuthService;
import com.nyamnyam.coach.global.config.SecurityConfig;
import com.nyamnyam.coach.global.exception.errorcode.CommonErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureMockMvc
@Import({
        SecurityConfig.class,
        JwtTokenProvider.class,
        CustomAuthenticationEntryPoint.class,
        CustomAccessDeniedHandler.class
})
@TestPropertySource(properties = {
        "jwt.secret=dGVzdC1zZWNyZXQta2V5LW11c3QtYmUtYXQtbGVhc3QtMjU2LWJpdHMtbG9uZy10ZXN0",
        "jwt.access-token-expiration=3600000",
        "jwt.refresh-token-expiration=604800000"
})
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("회원가입은 인증 없이 호출할 수 있다")
    void signupIsPublic() throws Exception {
        when(authService.signup(any(SignupRequest.class)))
                .thenReturn(new SignupResponse(
                        1L,
                        "user@example.com",
                        "nyam",
                        false,
                        LocalDateTime.of(2026, 5, 26, 10, 0)
                ));

        mockMvc.perform(post("/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new SignupRequest("user@example.com", "password123!", "nyam")
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("로그아웃은 access token 없이는 호출할 수 없다")
    void logoutRequiresAuthentication() throws Exception {
        mockMvc.perform(post("/v1/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LogoutRequest("refresh-token")
                        )))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(CommonErrorCode.UNAUTHORIZED.getCode()));
    }

    @Test
    @DisplayName("로그아웃은 유효한 access token이 있으면 컨트롤러까지 도달한다")
    void logoutWithAccessTokenReachesController() throws Exception {
        when(authService.logout(any(LogoutRequest.class)))
                .thenReturn(new LogoutResponse(LocalDateTime.of(2026, 5, 26, 10, 30)));

        mockMvc.perform(post("/v1/auth/logout")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + issueAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LogoutRequest("refresh-token")
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    private String issueAccessToken() {
        JwtToken jwtToken = jwtTokenProvider.generateToken(
                new UsernamePasswordAuthenticationToken(
                        "1",
                        "",
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                )
        );
        return jwtToken.getAccessToken();
    }
}
