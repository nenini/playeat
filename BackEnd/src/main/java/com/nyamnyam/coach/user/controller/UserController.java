package com.nyamnyam.coach.user.controller;

import com.nyamnyam.coach.global.response.ApiResponse;
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
import com.nyamnyam.coach.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserController implements UserApiDocs {

    private final UserService userService;

    @Override
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserMeResponse>> getMe(Authentication authentication) {
        UserMeResponse response = userService.getMe(authenticatedUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success(response, "회원 정보 조회에 성공했습니다."));
    }

    @Override
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UpdateUserResponse>> updateMe(
            Authentication authentication,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        UpdateUserResponse response = userService.updateMe(authenticatedUserId(authentication), request);
        return ResponseEntity.ok(ApiResponse.success(response, "회원 정보가 수정되었습니다."));
    }

    @Override
    @PatchMapping("/me/password")
    public ResponseEntity<ApiResponse<ChangePasswordResponse>> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        ChangePasswordResponse response = userService.changePassword(authenticatedUserId(authentication), request);
        return ResponseEntity.ok(ApiResponse.success(response, "비밀번호가 변경되었습니다. 다시 로그인해주세요."));
    }

    @Override
    @PutMapping(value = "/me/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProfileImageResponse>> updateProfileImage(
            Authentication authentication,
            @RequestPart("image") MultipartFile image
    ) {
        ProfileImageResponse response = userService.updateProfileImage(authenticatedUserId(authentication), image);
        return ResponseEntity.ok(ApiResponse.success(response, "프로필 이미지가 수정되었습니다."));
    }

    @Override
    @DeleteMapping("/me/profile-image")
    public ResponseEntity<ApiResponse<ProfileImageResponse>> deleteProfileImage(Authentication authentication) {
        ProfileImageResponse response = userService.deleteProfileImage(authenticatedUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success(response, "프로필 이미지가 삭제되었습니다."));
    }

    @Override
    @GetMapping("/me/health-profile")
    public ResponseEntity<ApiResponse<HealthProfileResponse>> getHealthProfile(Authentication authentication) {
        HealthProfileResponse response = userService.getHealthProfile(authenticatedUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success(response, "건강 프로필을 조회했습니다."));
    }

    @Override
    @PatchMapping("/me/health-profile")
    public ResponseEntity<ApiResponse<HealthProfileResponse>> updateHealthProfile(
            Authentication authentication,
            @Valid @RequestBody HealthProfileRequest request
    ) {
        HealthProfileResponse response = userService.updateHealthProfile(authenticatedUserId(authentication), request);
        return ResponseEntity.ok(ApiResponse.success(response, "건강 프로필이 수정되었습니다."));
    }

    @Override
    @PostMapping("/me/onboarding")
    public ResponseEntity<ApiResponse<OnboardingResponse>> completeOnboarding(
            Authentication authentication,
            @Valid @RequestBody OnboardingRequest request
    ) {
        OnboardingResponse response = userService.completeOnboarding(authenticatedUserId(authentication), request);
        return ResponseEntity.ok(ApiResponse.success(response, "온보딩이 완료되었습니다."));
    }

    @Override
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<DeactivateUserResponse>> deactivateMe(
            Authentication authentication,
            @Valid @RequestBody DeactivateUserRequest request
    ) {
        DeactivateUserResponse response = userService.deactivateMe(authenticatedUserId(authentication), request);
        return ResponseEntity.ok(ApiResponse.success(response, "회원 탈퇴가 완료되었습니다."));
    }

    private Long authenticatedUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}
