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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

@Tag(name = "User", description = "회원 계정 및 건강 프로필 API")
@SecurityRequirement(name = "bearerAuth")
public interface UserApiDocs {

    @Operation(summary = "내 회원 정보 조회", description = "현재 로그인한 회원의 기본 정보를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "내 회원 정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = UserMeResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "success": false,
                              "code": "USER_NOT_FOUND",
                              "message": "사용자를 찾을 수 없습니다."
                            }
                            """)))
    })
    ResponseEntity<ApiResponse<UserMeResponse>> getMe(@Parameter(hidden = true) Authentication authentication);

    @Operation(summary = "내 회원 정보 수정", description = "현재 로그인한 회원의 닉네임을 수정합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "내 회원 정보 수정 성공",
                    content = @Content(schema = @Schema(implementation = UpdateUserResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 검증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "닉네임 중복",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "success": false,
                              "code": "NICKNAME_ALREADY_EXISTS",
                              "message": "이미 사용 중인 닉네임입니다."
                            }
                            """)))
    })
    ResponseEntity<ApiResponse<UpdateUserResponse>> updateMe(
            @Parameter(hidden = true) Authentication authentication,
            UpdateUserRequest request
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

    @Operation(summary = "내 건강 프로필 조회", description = "현재 로그인한 회원의 건강 프로필을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "건강 프로필 조회 성공",
                    content = @Content(schema = @Schema(implementation = HealthProfileResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "건강 프로필을 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "success": false,
                              "code": "HEALTH_PROFILE_NOT_FOUND",
                              "message": "건강 프로필을 찾을 수 없습니다."
                            }
                            """)))
    })
    ResponseEntity<ApiResponse<HealthProfileResponse>> getMyHealthProfile(
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
    ResponseEntity<ApiResponse<HealthProfileResponse>> updateMyHealthProfile(
            @Parameter(hidden = true) Authentication authentication,
            UpdateHealthProfileRequest request
    );
}
