package com.nyamnyam.coach.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.GlobalExceptionHandler;
import com.nyamnyam.coach.global.exception.errorcode.AuthErrorCode;
import com.nyamnyam.coach.global.exception.errorcode.CommonErrorCode;
import com.nyamnyam.coach.global.exception.errorcode.UserErrorCode;
import com.nyamnyam.coach.user.dto.request.DeactivateUserRequest;
import com.nyamnyam.coach.user.dto.request.HealthProfileRequest;
import com.nyamnyam.coach.user.dto.request.OnboardingRequest;
import com.nyamnyam.coach.user.dto.request.UpdateUserRequest;
import com.nyamnyam.coach.user.dto.response.DeactivateUserResponse;
import com.nyamnyam.coach.user.dto.response.HealthProfileResponse;
import com.nyamnyam.coach.user.dto.response.OnboardingResponse;
import com.nyamnyam.coach.user.dto.response.ProfileImageResponse;
import com.nyamnyam.coach.user.dto.response.UpdateUserResponse;
import com.nyamnyam.coach.user.dto.response.UserMeResponse;
import com.nyamnyam.coach.user.service.UserService;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    void getMe() throws Exception {
        when(userService.getMe(1L))
                .thenReturn(new UserMeResponse(
                        1L,
                        "user@example.com",
                        "nyam",
                        "/uploads/profile-images/profile.png",
                        1L,
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
                .andExpect(jsonPath("$.data.profileImageUrl").value("/uploads/profile-images/profile.png"))
                .andExpect(jsonPath("$.data.selectedCoachId").value(1))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));
    }

    @Test
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
                .andExpect(jsonPath("$.data.nickname").value("newnyam"));
    }

    @Test
    void updateProfileImage() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "profile.png",
                MediaType.IMAGE_PNG_VALUE,
                "image-content".getBytes()
        );

        when(userService.updateProfileImage(any(Long.class), any()))
                .thenReturn(new ProfileImageResponse(1L, "/uploads/profile-images/profile.png"));

        mockMvc.perform(multipart("/v1/users/me/profile-image")
                        .file(image)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .principal(authentication()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.profileImageUrl").value("/uploads/profile-images/profile.png"));
    }

    @Test
    void deleteProfileImage() throws Exception {
        when(userService.deleteProfileImage(any(Long.class)))
                .thenReturn(new ProfileImageResponse(1L, null));

        mockMvc.perform(delete("/v1/users/me/profile-image").principal(authentication()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.profileImageUrl").doesNotExist());
    }

    @Test
    void getHealthProfile() throws Exception {
        when(userService.getHealthProfile(1L)).thenReturn(healthProfileResponse());

        mockMvc.perform(get("/v1/users/me/health-profile").principal(authentication()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.healthProfileId").value(10))
                .andExpect(jsonPath("$.data.healthGoal").value("LOSE_WEIGHT"));
    }

    @Test
    void updateHealthProfile() throws Exception {
        when(userService.updateHealthProfile(any(Long.class), any(HealthProfileRequest.class)))
                .thenReturn(healthProfileResponse());

        mockMvc.perform(patch("/v1/users/me/health-profile")
                        .principal(authentication())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(healthProfileRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.healthProfileId").value(10));
    }

    @Test
    void completeOnboarding() throws Exception {
        when(userService.completeOnboarding(any(Long.class), any(OnboardingRequest.class)))
                .thenReturn(new OnboardingResponse(1L, true, 1L, 10L));

        mockMvc.perform(post("/v1/users/me/onboarding")
                        .principal(authentication())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new OnboardingRequest(1L, healthProfileRequest()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.onboardingCompleted").value(true))
                .andExpect(jsonPath("$.data.healthProfileId").value(10));
    }

    @Test
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
                .andExpect(jsonPath("$.data.status").value("INACTIVE"));
    }

    @Test
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

    private HealthProfileRequest healthProfileRequest() {
        return new HealthProfileRequest(
                new BigDecimal("162"),
                new BigDecimal("54"),
                new BigDecimal("50"),
                LocalDate.of(2001, 3, 15),
                "FEMALE",
                "LOSE_WEIGHT",
                "LIGHT",
                List.of("BALANCED"),
                List.of("CAFFEINE"),
                List.of("PEANUT"),
                new BigDecimal("2000"),
                new BigDecimal("90"),
                new BigDecimal("260"),
                new BigDecimal("65"),
                new BigDecimal("2300")
        );
    }

    private HealthProfileResponse healthProfileResponse() {
        return new HealthProfileResponse(
                10L,
                new BigDecimal("162"),
                new BigDecimal("54"),
                new BigDecimal("50"),
                LocalDate.of(2001, 3, 15),
                "FEMALE",
                "LOSE_WEIGHT",
                "LIGHT",
                List.of("BALANCED"),
                List.of("CAFFEINE"),
                List.of("PEANUT"),
                new BigDecimal("2000"),
                new BigDecimal("90"),
                new BigDecimal("260"),
                new BigDecimal("65"),
                new BigDecimal("2300"),
                LocalDateTime.of(2026, 6, 9, 12, 0)
        );
    }
}
