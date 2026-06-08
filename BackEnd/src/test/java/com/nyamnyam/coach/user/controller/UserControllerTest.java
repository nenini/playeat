package com.nyamnyam.coach.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.GlobalExceptionHandler;
import com.nyamnyam.coach.global.exception.errorcode.AuthErrorCode;
import com.nyamnyam.coach.global.exception.errorcode.CommonErrorCode;
import com.nyamnyam.coach.global.exception.errorcode.UserErrorCode;
import com.nyamnyam.coach.user.dto.request.DeactivateUserRequest;
import com.nyamnyam.coach.user.dto.request.UpdateUserRequest;
import com.nyamnyam.coach.user.dto.response.DeactivateUserResponse;
import com.nyamnyam.coach.user.dto.response.UpdateUserResponse;
import com.nyamnyam.coach.user.dto.response.UserMeResponse;
import com.nyamnyam.coach.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("내 회원 정보 조회 성공 응답을 반환한다")
    void getMe() throws Exception {
        when(userService.getMe(1L))
                .thenReturn(new UserMeResponse(
                        1L,
                        "user@example.com",
                        "nyam",
                        "ACTIVE",
                        false,
                        LocalDateTime.of(2026, 5, 26, 10, 0)
                ));

        mockMvc.perform(get("/v1/users/me").principal(authentication()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.email").value("user@example.com"))
                .andExpect(jsonPath("$.data.nickname").value("nyam"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"))
                .andExpect(jsonPath("$.data.onboardingCompleted").value(false));
    }

    @Test
    @DisplayName("내 회원 정보 수정 성공 응답을 반환한다")
    void updateMe() throws Exception {
        when(userService.updateMe(any(Long.class), any(UpdateUserRequest.class)))
                .thenReturn(new UpdateUserResponse(
                        1L,
                        "newnyam",
                        LocalDateTime.of(2026, 5, 26, 11, 0)
                ));

        mockMvc.perform(patch("/v1/users/me")
                        .principal(authentication())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateUserRequest("newnyam"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.nickname").value("newnyam"))
                .andExpect(jsonPath("$.data.updatedAt").exists());
    }

    @Test
    @DisplayName("닉네임 검증 실패 시 400과 errors를 반환한다")
    void updateMeValidationFailure() throws Exception {
        mockMvc.perform(patch("/v1/users/me")
                        .principal(authentication())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nickname": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(CommonErrorCode.VALIDATION_FAILED.getCode()))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @DisplayName("중복 닉네임이면 409 JSON 에러 응답을 반환한다")
    void updateMeDuplicateNickname() throws Exception {
        doThrow(new BusinessException(AuthErrorCode.NICKNAME_ALREADY_EXISTS))
                .when(userService).updateMe(any(Long.class), any(UpdateUserRequest.class));

        mockMvc.perform(patch("/v1/users/me")
                        .principal(authentication())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateUserRequest("taken"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(AuthErrorCode.NICKNAME_ALREADY_EXISTS.getCode()));
    }

    @Test
    @DisplayName("회원 탈퇴 성공 응답을 반환한다")
    void deactivateMe() throws Exception {
        when(userService.deactivateMe(any(Long.class), any(DeactivateUserRequest.class)))
                .thenReturn(new DeactivateUserResponse(
                        1L,
                        "INACTIVE",
                        LocalDateTime.of(2026, 5, 26, 11, 30)
                ));

        mockMvc.perform(delete("/v1/users/me")
                        .principal(authentication())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DeactivateUserRequest("password123!"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.status").value("INACTIVE"))
                .andExpect(jsonPath("$.data.deactivatedAt").exists());
    }

    @Test
    @DisplayName("회원 탈퇴 비밀번호 검증 실패 시 400과 errors를 반환한다")
    void deactivateMeValidationFailure() throws Exception {
        mockMvc.perform(delete("/v1/users/me")
                        .principal(authentication())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "password": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(CommonErrorCode.VALIDATION_FAILED.getCode()))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @DisplayName("비밀번호가 틀리면 401 JSON 에러 응답을 반환한다")
    void deactivateMeWrongPassword() throws Exception {
        doThrow(new BusinessException(AuthErrorCode.INVALID_CREDENTIALS))
                .when(userService).deactivateMe(any(Long.class), any(DeactivateUserRequest.class));

        mockMvc.perform(delete("/v1/users/me")
                        .principal(authentication())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DeactivateUserRequest("wrong-password"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(AuthErrorCode.INVALID_CREDENTIALS.getCode()));
    }

    @Test
    @DisplayName("회원이 없으면 404 JSON 에러 응답을 반환한다")
    void getMeMissingUser() throws Exception {
        doThrow(new BusinessException(UserErrorCode.USER_NOT_FOUND))
                .when(userService).getMe(1L);

        mockMvc.perform(get("/v1/users/me").principal(authentication()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(UserErrorCode.USER_NOT_FOUND.getCode()));
    }

    private Authentication authentication() {
        return new UsernamePasswordAuthenticationToken(
                "1",
                "",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
