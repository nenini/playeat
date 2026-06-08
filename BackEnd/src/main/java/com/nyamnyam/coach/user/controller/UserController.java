package com.nyamnyam.coach.user.controller;

import com.nyamnyam.coach.global.response.ApiResponse;
import com.nyamnyam.coach.user.dto.request.DeactivateUserRequest;
import com.nyamnyam.coach.user.dto.request.UpdateHealthProfileRequest;
import com.nyamnyam.coach.user.dto.request.UpdateUserRequest;
import com.nyamnyam.coach.user.dto.response.DeactivateUserResponse;
import com.nyamnyam.coach.user.dto.response.HealthProfileResponse;
import com.nyamnyam.coach.user.dto.response.UpdateUserResponse;
import com.nyamnyam.coach.user.dto.response.UserMeResponse;
import com.nyamnyam.coach.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserController implements UserApiDocs {

    private final UserService userService;

    @Override
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserMeResponse>> getMe(Authentication authentication) {
        UserMeResponse response = userService.getMe(authenticatedUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success(response, "내 회원 정보 조회에 성공했습니다."));
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
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<DeactivateUserResponse>> deactivateMe(
            Authentication authentication,
            @Valid @RequestBody DeactivateUserRequest request
    ) {
        DeactivateUserResponse response = userService.deactivateMe(authenticatedUserId(authentication), request);
        return ResponseEntity.ok(ApiResponse.success(response, "회원 탈퇴가 완료되었습니다."));
    }

    @Override
    @GetMapping("/me/health-profile")
    public ResponseEntity<ApiResponse<HealthProfileResponse>> getMyHealthProfile(Authentication authentication) {
        HealthProfileResponse response = userService.getMyHealthProfile(authenticatedUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success(response, "건강 프로필 조회에 성공했습니다."));
    }

    @Override
    @PatchMapping("/me/health-profile")
    public ResponseEntity<ApiResponse<HealthProfileResponse>> updateMyHealthProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateHealthProfileRequest request
    ) {
        HealthProfileResponse response = userService.updateMyHealthProfile(authenticatedUserId(authentication), request);
        return ResponseEntity.ok(ApiResponse.success(response, "건강 프로필이 수정되었습니다."));
    }

    private Long authenticatedUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}
