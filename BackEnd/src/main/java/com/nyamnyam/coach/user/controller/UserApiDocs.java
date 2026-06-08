package com.nyamnyam.coach.user.controller;

import com.nyamnyam.coach.global.response.ApiResponse;
import com.nyamnyam.coach.user.dto.request.DeactivateUserRequest;
import com.nyamnyam.coach.user.dto.request.UpdateHealthProfileRequest;
import com.nyamnyam.coach.user.dto.request.UpdateUserRequest;
import com.nyamnyam.coach.user.dto.response.DeactivateUserResponse;
import com.nyamnyam.coach.user.dto.response.HealthProfileResponse;
import com.nyamnyam.coach.user.dto.response.UpdateUserResponse;
import com.nyamnyam.coach.user.dto.response.UserMeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

@Tag(name = "User", description = "회원 계정 및 건강 프로필 API")
public interface UserApiDocs {

    @Operation(summary = "내 회원 정보 조회")
    ResponseEntity<ApiResponse<UserMeResponse>> getMe(@Parameter(hidden = true) Authentication authentication);

    @Operation(summary = "내 회원 정보 수정")
    ResponseEntity<ApiResponse<UpdateUserResponse>> updateMe(
            @Parameter(hidden = true) Authentication authentication,
            UpdateUserRequest request
    );

    @Operation(summary = "내 회원 탈퇴")
    ResponseEntity<ApiResponse<DeactivateUserResponse>> deactivateMe(
            @Parameter(hidden = true) Authentication authentication,
            DeactivateUserRequest request
    );

    @Operation(summary = "내 건강 프로필 조회")
    ResponseEntity<ApiResponse<HealthProfileResponse>> getMyHealthProfile(
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "내 건강 프로필 수정")
    ResponseEntity<ApiResponse<HealthProfileResponse>> updateMyHealthProfile(
            @Parameter(hidden = true) Authentication authentication,
            UpdateHealthProfileRequest request
    );
}
