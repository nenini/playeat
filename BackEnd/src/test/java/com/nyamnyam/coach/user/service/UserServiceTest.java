package com.nyamnyam.coach.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nyamnyam.coach.auth.repository.RefreshTokenRepository;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.AuthErrorCode;
import com.nyamnyam.coach.global.exception.errorcode.CommonErrorCode;
import com.nyamnyam.coach.global.exception.errorcode.UserErrorCode;
import com.nyamnyam.coach.user.dto.request.ChangePasswordRequest;
import com.nyamnyam.coach.user.dto.request.DeactivateUserRequest;
import com.nyamnyam.coach.user.dto.request.HealthProfileRequest;
import com.nyamnyam.coach.user.dto.request.OnboardingRequest;
import com.nyamnyam.coach.user.dto.request.UpdateUserRequest;
import com.nyamnyam.coach.user.dto.response.ChangePasswordResponse;
import com.nyamnyam.coach.user.dto.response.HealthProfileResponse;
import com.nyamnyam.coach.user.dto.response.OnboardingResponse;
import com.nyamnyam.coach.user.dto.response.ProfileImageResponse;
import com.nyamnyam.coach.user.dto.response.UpdateUserResponse;
import com.nyamnyam.coach.user.dto.response.UserMeResponse;
import com.nyamnyam.coach.user.entity.HealthProfile;
import com.nyamnyam.coach.user.entity.User;
import com.nyamnyam.coach.user.repository.HealthProfileRepository;
import com.nyamnyam.coach.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HealthProfileRepository healthProfileRepository;

    @Mock
    private ProfileImageStorageService profileImageStorageService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserService(
                userRepository,
                healthProfileRepository,
                refreshTokenRepository,
                passwordEncoder,
                new ObjectMapper(),
                profileImageStorageService
        );
    }

    @Test
    void getMe() {
        User user = activeUser("encoded-password");
        user.setProfileImageUrl("/uploads/profile-images/profile.png");
        user.setSelectedCoachId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserMeResponse response = userService.getMe(1L);

        assertThat(response.userId()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo("user@example.com");
        assertThat(response.nickname()).isEqualTo("nyam");
        assertThat(response.profileImageUrl()).isEqualTo("/uploads/profile-images/profile.png");
        assertThat(response.selectedCoachId()).isEqualTo(1L);
    }

    @Test
    void updateMeAllowsDuplicateNickname() {
        User user = activeUser("encoded-password");
        User updatedUser = activeUser("encoded-password");
        updatedUser.setNickname("same-name");
        updatedUser.setUpdatedAt(LocalDateTime.of(2026, 5, 26, 11, 0));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user), Optional.of(updatedUser));

        UpdateUserResponse response = userService.updateMe(1L, new UpdateUserRequest("same-name"));

        verify(userRepository).updateProfile(1L, "same-name");
        assertThat(response.nickname()).isEqualTo("same-name");
    }

    @Test
    void changePassword() {
        User user = activeUser(passwordEncoder.encode("oldPassword123!"));
        User updatedUser = activeUser(passwordEncoder.encode("newPassword123!"));
        updatedUser.setUpdatedAt(LocalDateTime.of(2026, 6, 10, 14, 30));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user), Optional.of(updatedUser));

        ChangePasswordResponse response = userService.changePassword(
                1L,
                new ChangePasswordRequest("oldPassword123!", "newPassword123!", "newPassword123!")
        );

        verify(userRepository).updatePassword(any(Long.class), any(String.class));
        verify(refreshTokenRepository).revokeAllByUserId(1L);
        assertThat(response.userId()).isEqualTo(1L);
        assertThat(response.changedAt()).isEqualTo(LocalDateTime.of(2026, 6, 10, 14, 30));
    }

    @Test
    void changePasswordWithWrongCurrentPassword() {
        User user = activeUser(passwordEncoder.encode("oldPassword123!"));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.changePassword(
                1L,
                new ChangePasswordRequest("wrongPassword123!", "newPassword123!", "newPassword123!")
        ))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.INVALID_CREDENTIALS);

        verify(userRepository, never()).updatePassword(any(), any());
        verify(refreshTokenRepository, never()).revokeAllByUserId(any());
    }

    @Test
    void changePasswordWithMismatchedConfirm() {
        User user = activeUser(passwordEncoder.encode("oldPassword123!"));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.changePassword(
                1L,
                new ChangePasswordRequest("oldPassword123!", "newPassword123!", "differentPassword123!")
        ))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CommonErrorCode.INVALID_REQUEST);

        verify(userRepository, never()).updatePassword(any(), any());
        verify(refreshTokenRepository, never()).revokeAllByUserId(any());
    }

    @Test
    void updateProfileImage() {
        User user = activeUser("encoded-password");
        User updatedUser = activeUser("encoded-password");
        updatedUser.setProfileImageUrl("/uploads/profile-images/profile.png");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user), Optional.of(updatedUser));
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "profile.png",
                "image/png",
                "image-content".getBytes()
        );
        when(profileImageStorageService.store(image)).thenReturn("/uploads/profile-images/profile.png");

        ProfileImageResponse response = userService.updateProfileImage(1L, image);

        verify(userRepository).updateProfileImage(1L, "/uploads/profile-images/profile.png");
        assertThat(response.profileImageUrl()).isEqualTo("/uploads/profile-images/profile.png");
    }

    @Test
    void deleteProfileImage() {
        User user = activeUser("encoded-password");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user), Optional.of(user));

        ProfileImageResponse response = userService.deleteProfileImage(1L);

        verify(userRepository).deleteProfileImage(1L);
        verify(profileImageStorageService).delete(user.getProfileImageUrl());
        assertThat(response.profileImageUrl()).isNull();
    }

    @Test
    void getHealthProfile() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser("encoded-password")));
        when(healthProfileRepository.findByUserId(1L)).thenReturn(Optional.of(healthProfile()));

        HealthProfileResponse response = userService.getHealthProfile(1L);

        assertThat(response.healthProfileId()).isEqualTo(10L);
        assertThat(response.healthGoal()).isEqualTo("LOSE_WEIGHT");
    }

    @Test
    void getHealthProfileWithMissingProfile() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser("encoded-password")));
        when(healthProfileRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getHealthProfile(1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(UserErrorCode.HEALTH_PROFILE_NOT_FOUND);
    }

    @Test
    void updateHealthProfile() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser("encoded-password")));
        when(healthProfileRepository.existsByUserId(1L)).thenReturn(true);
        when(healthProfileRepository.findByUserId(1L)).thenReturn(Optional.of(healthProfile()));

        HealthProfileResponse response = userService.updateHealthProfile(1L, healthProfileRequest());

        verify(healthProfileRepository).updateByUserId(any(HealthProfile.class));
        assertThat(response.healthProfileId()).isEqualTo(10L);
    }

    @Test
    void completeOnboarding() {
        User user = activeUser("encoded-password");
        User completedUser = activeUser("encoded-password");
        completedUser.setOnboardingCompleted(true);
        completedUser.setSelectedCoachId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user), Optional.of(completedUser));
        when(healthProfileRepository.existsByUserId(1L)).thenReturn(false);

        OnboardingResponse response = userService.completeOnboarding(
                1L,
                new OnboardingRequest(1L, healthProfileRequest())
        );

        verify(healthProfileRepository).save(any(HealthProfile.class));
        verify(userRepository).completeOnboarding(1L, 1L);
        assertThat(response.onboardingCompleted()).isTrue();
        assertThat(response.selectedCoachId()).isEqualTo(1L);
    }

    @Test
    void completeOnboardingAlreadyCompleted() {
        User user = activeUser("encoded-password");
        user.setOnboardingCompleted(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.completeOnboarding(1L, new OnboardingRequest(1L, healthProfileRequest())))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(UserErrorCode.ONBOARDING_ALREADY_COMPLETED);
        verify(healthProfileRepository, never()).save(any(HealthProfile.class));
    }

    @Test
    void deactivateMeWithWrongPassword() {
        User user = activeUser(passwordEncoder.encode("password123!"));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.deactivateMe(1L, new DeactivateUserRequest("wrong-password")))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.INVALID_CREDENTIALS);
    }

    private User activeUser(String passwordHash) {
        return User.builder()
                .userId(1L)
                .email("user@example.com")
                .passwordHash(passwordHash)
                .nickname("nyam")
                .status("ACTIVE")
                .onboardingCompleted(false)
                .createdAt(LocalDateTime.of(2026, 5, 26, 10, 0))
                .updatedAt(LocalDateTime.of(2026, 5, 26, 10, 0))
                .build();
    }

    private HealthProfile healthProfile() {
        return HealthProfile.builder()
                .healthProfileId(10L)
                .userId(1L)
                .heightCm(new BigDecimal("162"))
                .weightKg(new BigDecimal("54"))
                .targetWeightKg(new BigDecimal("50"))
                .birthDate(LocalDate.of(2001, 3, 15))
                .gender("FEMALE")
                .healthGoal("LOSE_WEIGHT")
                .activityLevel("LIGHT")
                .dietStylesJson("[\"BALANCED\"]")
                .restrictedFoodsJson("[\"CAFFEINE\"]")
                .allergiesJson("[\"PEANUT\"]")
                .targetCalories(new BigDecimal("2000"))
                .targetProteinG(new BigDecimal("90"))
                .targetCarbsG(new BigDecimal("260"))
                .targetFatG(new BigDecimal("65"))
                .targetSodiumMg(new BigDecimal("2300"))
                .updatedAt(LocalDateTime.of(2026, 6, 9, 12, 0))
                .build();
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
}
