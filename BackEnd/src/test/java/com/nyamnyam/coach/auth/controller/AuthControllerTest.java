package com.nyamnyam.coach.auth.controller;

import com.nyamnyam.coach.auth.dto.request.LoginRequest;
import com.nyamnyam.coach.auth.dto.request.LogoutRequest;
import com.nyamnyam.coach.auth.dto.request.SignupRequest;
import com.nyamnyam.coach.auth.dto.request.TokenRefreshRequest;
import com.nyamnyam.coach.auth.dto.response.AuthUserResponse;
import com.nyamnyam.coach.auth.dto.response.LoginResponse;
import com.nyamnyam.coach.auth.dto.response.LogoutResponse;
import com.nyamnyam.coach.auth.dto.response.SignupResponse;
import com.nyamnyam.coach.auth.dto.response.TokenRefreshResponse;
import com.nyamnyam.coach.auth.service.AuthService;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.GlobalExceptionHandler;
import com.nyamnyam.coach.global.exception.errorcode.AuthErrorCode;
import com.nyamnyam.coach.global.exception.errorcode.CommonErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("회원가입 성공 응답을 반환한다")
    void signup() throws Exception {
        when(authService.signup(any(SignupRequest.class)))
                .thenReturn(new SignupResponse(
                        1L,
                        "user@example.com",
                        "냥냥",
                        false,
                        LocalDateTime.of(2026, 5, 26, 10, 0)
                ));

        mockMvc.perform(post("/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new SignupRequest("user@example.com", "password123!", "냥냥")
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.email").value("user@example.com"))
                .andExpect(jsonPath("$.data.nickname").value("냥냥"))
                .andExpect(jsonPath("$.message").value("회원가입이 완료되었습니다."));
    }

    @Test
    @DisplayName("회원가입 validation 실패 시 400과 errors를 반환한다")
    void signupValidationFailure() throws Exception {
        mockMvc.perform(post("/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "",
                                  "password": "",
                                  "nickname": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(CommonErrorCode.VALIDATION_FAILED.getCode()))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].field").exists())
                .andExpect(jsonPath("$.errors[0].reason").exists());
    }

    @Test
    @DisplayName("회원가입 중복 이메일 예외는 409 JSON 에러 응답을 반환한다")
    void signupDuplicateEmailError() throws Exception {
        doThrow(new BusinessException(AuthErrorCode.EMAIL_ALREADY_EXISTS))
                .when(authService).signup(any(SignupRequest.class));

        mockMvc.perform(post("/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new SignupRequest("user@example.com", "password123!", "냥냥")
                        )))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(AuthErrorCode.EMAIL_ALREADY_EXISTS.getCode()))
                .andExpect(jsonPath("$.message").value(AuthErrorCode.EMAIL_ALREADY_EXISTS.getMessage()));
    }

    @Test
    @DisplayName("로그인 성공 응답을 반환한다")
    void login() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenReturn(new LoginResponse(
                        "access-token",
                        "refresh-token",
                        "Bearer",
                        3600L,
                        new AuthUserResponse(1L, "user@example.com", "냥냥", true)
                ));

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginRequest("user@example.com", "password123!")
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.expiresIn").value(3600))
                .andExpect(jsonPath("$.data.user.userId").value(1));
    }

    @Test
    @DisplayName("로그인 validation 실패 시 400을 반환한다")
    void loginValidationFailure() throws Exception {
        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "",
                                  "password": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(CommonErrorCode.VALIDATION_FAILED.getCode()))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @DisplayName("로그인 인증 실패 예외는 401 JSON 에러 응답을 반환한다")
    void loginInvalidCredentialsError() throws Exception {
        doThrow(new BusinessException(AuthErrorCode.INVALID_CREDENTIALS))
                .when(authService).login(any(LoginRequest.class));

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginRequest("user@example.com", "wrong-password")
                        )))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(AuthErrorCode.INVALID_CREDENTIALS.getCode()))
                .andExpect(jsonPath("$.message").value(AuthErrorCode.INVALID_CREDENTIALS.getMessage()));
    }

    @Test
    @DisplayName("토큰 재발급 성공 응답을 반환한다")
    void refresh() throws Exception {
        when(authService.refresh(any(TokenRefreshRequest.class)))
                .thenReturn(new TokenRefreshResponse(
                        "new-access-token",
                        "new-refresh-token",
                        "Bearer",
                        3600L
                ));

        mockMvc.perform(post("/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new TokenRefreshRequest("refresh-token")
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("new-refresh-token"));
    }

    @Test
    @DisplayName("토큰 재발급 validation 실패 시 400을 반환한다")
    void refreshValidationFailure() throws Exception {
        mockMvc.perform(post("/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "refreshToken": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(CommonErrorCode.VALIDATION_FAILED.getCode()))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @DisplayName("로그아웃 성공 응답을 반환한다")
    void logout() throws Exception {
        when(authService.logout(any(LogoutRequest.class)))
                .thenReturn(new LogoutResponse(LocalDateTime.of(2026, 5, 26, 10, 30)));

        mockMvc.perform(post("/v1/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LogoutRequest("refresh-token")
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.loggedOutAt").exists())
                .andExpect(jsonPath("$.message").value("로그아웃되었습니다."));
    }

    @Test
    @DisplayName("로그아웃 validation 실패 시 400을 반환한다")
    void logoutValidationFailure() throws Exception {
        mockMvc.perform(post("/v1/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "refreshToken": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(CommonErrorCode.VALIDATION_FAILED.getCode()))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @DisplayName("로그아웃 유효하지 않은 토큰 예외는 401 JSON 에러 응답을 반환한다")
    void logoutInvalidTokenError() throws Exception {
        doThrow(new BusinessException(AuthErrorCode.INVALID_TOKEN))
                .when(authService).logout(any(LogoutRequest.class));

        mockMvc.perform(post("/v1/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LogoutRequest("invalid-token")
                        )))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(AuthErrorCode.INVALID_TOKEN.getCode()))
                .andExpect(jsonPath("$.message").value(AuthErrorCode.INVALID_TOKEN.getMessage()));
    }

    @Test
    @DisplayName("서비스가 BusinessException을 던지면 JSON 에러 응답을 반환한다")
    void refreshExpiredTokenError() throws Exception {
        doThrow(new BusinessException(AuthErrorCode.REFRESH_TOKEN_EXPIRED))
                .when(authService).refresh(any(TokenRefreshRequest.class));

        mockMvc.perform(post("/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new TokenRefreshRequest("expired-token")
                        )))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(AuthErrorCode.REFRESH_TOKEN_EXPIRED.getCode()))
                .andExpect(jsonPath("$.message").value(AuthErrorCode.REFRESH_TOKEN_EXPIRED.getMessage()));
    }
}
