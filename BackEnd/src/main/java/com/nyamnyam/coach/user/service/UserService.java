package com.nyamnyam.coach.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
import com.nyamnyam.coach.user.dto.response.DeactivateUserResponse;
import com.nyamnyam.coach.user.dto.response.HealthProfileResponse;
import com.nyamnyam.coach.user.dto.response.OnboardingResponse;
import com.nyamnyam.coach.user.dto.response.ProfileImageResponse;
import com.nyamnyam.coach.user.dto.response.UpdateUserResponse;
import com.nyamnyam.coach.user.dto.response.UserMeResponse;
import com.nyamnyam.coach.user.entity.HealthProfile;
import com.nyamnyam.coach.user.entity.User;
import com.nyamnyam.coach.user.repository.HealthProfileRepository;
import com.nyamnyam.coach.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final String ACTIVE_STATUS = "ACTIVE";
    private static final TypeReference<List<String>> STRING_LIST_TYPE = new TypeReference<>() {
    };

    private final UserRepository userRepository;
    private final HealthProfileRepository healthProfileRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final ProfileImageStorageService profileImageStorageService;

    @Transactional(readOnly = true)
    public UserMeResponse getMe(Long userId) {
        User user = findActiveUser(userId);
        return toUserMeResponse(user);
    }

    @Transactional
    public UpdateUserResponse updateMe(Long userId, UpdateUserRequest request) {
        findActiveUser(userId);

        userRepository.updateProfile(userId, request.nickname());
        User updatedUser = findActiveUser(userId);

        return new UpdateUserResponse(
                updatedUser.getUserId(),
                updatedUser.getNickname(),
                updatedUser.getUpdatedAt()
        );
    }

    @Transactional
    public ChangePasswordResponse changePassword(Long userId, ChangePasswordRequest request) {
        User user = findActiveUser(userId);

        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new BusinessException(AuthErrorCode.INVALID_CREDENTIALS);
        }
        if (!request.newPassword().equals(request.newPasswordConfirm())) {
            throw new BusinessException(CommonErrorCode.INVALID_REQUEST);
        }

        userRepository.updatePassword(userId, passwordEncoder.encode(request.newPassword()));
        refreshTokenRepository.revokeAllByUserId(userId);
        User updatedUser = findActiveUser(userId);

        return new ChangePasswordResponse(
                updatedUser.getUserId(),
                updatedUser.getUpdatedAt()
        );
    }

    @Transactional
    public ProfileImageResponse updateProfileImage(Long userId, MultipartFile image) {
        User user = findActiveUser(userId);

        String profileImagePath = profileImageStorageService.store(image);
        userRepository.updateProfileImage(userId, profileImagePath);
        profileImageStorageService.delete(user.getProfileImageUrl());
        User updatedUser = findActiveUser(userId);

        return new ProfileImageResponse(
                updatedUser.getUserId(),
                updatedUser.getProfileImageUrl()
        );
    }

    @Transactional
    public ProfileImageResponse deleteProfileImage(Long userId) {
        User user = findActiveUser(userId);

        userRepository.deleteProfileImage(userId);
        profileImageStorageService.delete(user.getProfileImageUrl());
        User updatedUser = findActiveUser(userId);

        return new ProfileImageResponse(
                updatedUser.getUserId(),
                updatedUser.getProfileImageUrl()
        );
    }

    @Transactional(readOnly = true)
    public HealthProfileResponse getHealthProfile(Long userId) {
        findActiveUser(userId);
        HealthProfile healthProfile = findHealthProfile(userId);
        return toHealthProfileResponse(healthProfile);
    }

    @Transactional
    public HealthProfileResponse updateHealthProfile(Long userId, HealthProfileRequest request) {
        findActiveUser(userId);

        if (!healthProfileRepository.existsByUserId(userId)) {
            throw new BusinessException(UserErrorCode.HEALTH_PROFILE_NOT_FOUND);
        }

        healthProfileRepository.updateByUserId(toHealthProfile(userId, request));
        HealthProfile updatedHealthProfile = findHealthProfile(userId);

        return toHealthProfileResponse(updatedHealthProfile);
    }

    @Transactional
    public OnboardingResponse completeOnboarding(Long userId, OnboardingRequest request) {
        User user = findActiveUser(userId);
        if (Boolean.TRUE.equals(user.getOnboardingCompleted())) {
            throw new BusinessException(UserErrorCode.ONBOARDING_ALREADY_COMPLETED);
        }

        HealthProfile healthProfile = toHealthProfile(userId, request.healthProfile());
        if (healthProfileRepository.existsByUserId(userId)) {
            healthProfileRepository.updateByUserId(healthProfile);
            healthProfile = findHealthProfile(userId);
        } else {
            healthProfileRepository.save(healthProfile);
        }

        userRepository.completeOnboarding(userId, request.selectedCoachId());
        User updatedUser = findActiveUser(userId);

        return new OnboardingResponse(
                updatedUser.getUserId(),
                updatedUser.getOnboardingCompleted(),
                updatedUser.getSelectedCoachId(),
                healthProfile.getHealthProfileId()
        );
    }

    @Transactional
    public DeactivateUserResponse deactivateMe(Long userId, DeactivateUserRequest request) {
        User user = findActiveUser(userId);

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        userRepository.deactivate(userId);
        User deactivatedUser = findUser(userId);

        return new DeactivateUserResponse(
                deactivatedUser.getUserId(),
                deactivatedUser.getStatus(),
                deactivatedUser.getDeactivatedAt()
        );
    }

    private User findActiveUser(Long userId) {
        User user = findUser(userId);
        if (!ACTIVE_STATUS.equals(user.getStatus())) {
            throw new BusinessException(AuthErrorCode.USER_INACTIVE);
        }
        return user;
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }

    private HealthProfile findHealthProfile(Long userId) {
        return healthProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.HEALTH_PROFILE_NOT_FOUND));
    }

    private HealthProfile toHealthProfile(Long userId, HealthProfileRequest request) {
        return HealthProfile.builder()
                .userId(userId)
                .heightCm(request.heightCm())
                .weightKg(request.weightKg())
                .targetWeightKg(request.targetWeightKg())
                .birthDate(request.birthDate())
                .gender(request.gender())
                .healthGoal(request.healthGoal())
                .activityLevel(request.activityLevel())
                .dietStylesJson(toJson(request.dietStyles()))
                .restrictedFoodsJson(toJson(request.restrictedFoods()))
                .allergiesJson(toJson(request.allergies()))
                .targetCalories(request.targetCalories())
                .targetProteinG(request.targetProteinG())
                .targetCarbsG(request.targetCarbsG())
                .targetFatG(request.targetFatG())
                .targetSodiumMg(request.targetSodiumMg())
                .build();
    }

    private UserMeResponse toUserMeResponse(User user) {
        return new UserMeResponse(
                user.getUserId(),
                user.getEmail(),
                user.getNickname(),
                user.getProfileImageUrl(),
                user.getSelectedCoachId(),
                user.getStatus(),
                user.getOnboardingCompleted(),
                user.getCreatedAt()
        );
    }

    private HealthProfileResponse toHealthProfileResponse(HealthProfile healthProfile) {
        return new HealthProfileResponse(
                healthProfile.getHealthProfileId(),
                healthProfile.getHeightCm(),
                healthProfile.getWeightKg(),
                healthProfile.getTargetWeightKg(),
                healthProfile.getBirthDate(),
                healthProfile.getGender(),
                healthProfile.getHealthGoal(),
                healthProfile.getActivityLevel(),
                fromJson(healthProfile.getDietStylesJson()),
                fromJson(healthProfile.getRestrictedFoodsJson()),
                fromJson(healthProfile.getAllergiesJson()),
                healthProfile.getTargetCalories(),
                healthProfile.getTargetProteinG(),
                healthProfile.getTargetCarbsG(),
                healthProfile.getTargetFatG(),
                healthProfile.getTargetSodiumMg(),
                healthProfile.getUpdatedAt()
        );
    }

    private String toJson(List<String> values) {
        if (values == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(values);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Health profile JSON value cannot be serialized.", exception);
        }
    }

    private List<String> fromJson(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, STRING_LIST_TYPE);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Stored health profile JSON value is invalid.", exception);
        }
    }
}
