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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User", description = "회원 계정 API")
@SecurityRequirement(name = "BearerAuth")
public interface UserApiDocs {

    @Operation(summary = "내 회원 정보 조회", description = "현재 로그인한 회원의 기본 정보를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "내 회원 정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = UserMeResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    ResponseEntity<ApiResponse<UserMeResponse>> getMe(@Parameter(hidden = true) Authentication authentication);

    @Operation(summary = "내 회원 정보 수정", description = "현재 로그인한 회원의 닉네임을 수정합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "내 회원 정보 수정 성공",
                    content = @Content(schema = @Schema(implementation = UpdateUserResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 검증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    ResponseEntity<ApiResponse<UpdateUserResponse>> updateMe(
            @Parameter(hidden = true) Authentication authentication,
            UpdateUserRequest request
    );

    @Operation(summary = "내 비밀번호 변경", description = "현재 비밀번호를 확인한 뒤 새 비밀번호로 변경하고 기존 refresh token을 무효화합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "비밀번호 변경 성공",
                    content = @Content(schema = @Schema(implementation = ChangePasswordResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 검증 실패 또는 새 비밀번호 확인 불일치"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패 또는 현재 비밀번호 불일치"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    ResponseEntity<ApiResponse<ChangePasswordResponse>> changePassword(
            @Parameter(hidden = true) Authentication authentication,
            ChangePasswordRequest request
    );

    @Operation(summary = "프로필 이미지 업로드/수정", description = "현재 로그인한 회원의 프로필 이미지 파일을 로컬 파일시스템에 저장하고 접근 경로를 저장합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "프로필 이미지 수정 성공",
                    content = @Content(schema = @Schema(implementation = ProfileImageResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 검증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    ResponseEntity<ApiResponse<ProfileImageResponse>> updateProfileImage(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "Profile image file") MultipartFile image
    );

    @Operation(summary = "프로필 이미지 삭제", description = "현재 로그인한 회원의 프로필 이미지 경로와 로컬 파일을 삭제합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "프로필 이미지 삭제 성공",
                    content = @Content(schema = @Schema(implementation = ProfileImageResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    ResponseEntity<ApiResponse<ProfileImageResponse>> deleteProfileImage(
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "내 건강 프로필 조회", description = "현재 로그인한 회원의 건강 프로필을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "건강 프로필 조회 성공",
                    content = @Content(schema = @Schema(implementation = HealthProfileResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "건강 프로필을 찾을 수 없음")
    })
    ResponseEntity<ApiResponse<HealthProfileResponse>> getHealthProfile(
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "내 건강 프로필 수정", description = "현재 로그인한 회원의 건강 프로필을 수정합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "건강 프로필 수정 성공",
                    content = @Content(schema = @Schema(implementation = HealthProfileResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 검증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "건강 프로필을 찾을 수 없음")
    })
    ResponseEntity<ApiResponse<HealthProfileResponse>> updateHealthProfile(
            @Parameter(hidden = true) Authentication authentication,
            HealthProfileRequest request
    );

    @Operation(summary = "온보딩 정보 등록", description = "건강 프로필을 저장하고 회원 온보딩을 완료 처리합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "온보딩 완료 성공",
                    content = @Content(schema = @Schema(implementation = OnboardingResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 검증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 온보딩 완료")
    })
    ResponseEntity<ApiResponse<OnboardingResponse>> completeOnboarding(
            @Parameter(hidden = true) Authentication authentication,
            OnboardingRequest request
    );

    @Operation(summary = "내 회원 탈퇴", description = "현재 비밀번호를 확인한 뒤 회원을 비활성화합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원 탈퇴 성공",
                    content = @Content(schema = @Schema(implementation = DeactivateUserResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 검증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패 또는 비밀번호 불일치"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    ResponseEntity<ApiResponse<DeactivateUserResponse>> deactivateMe(
            @Parameter(hidden = true) Authentication authentication,
            DeactivateUserRequest request
    );
}
