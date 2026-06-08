package com.nyamnyam.coach.auth.controller;

import com.nyamnyam.coach.auth.dto.request.LoginRequest;
import com.nyamnyam.coach.auth.dto.request.LogoutRequest;
import com.nyamnyam.coach.auth.dto.request.SignupRequest;
import com.nyamnyam.coach.auth.dto.request.TokenRefreshRequest;
import com.nyamnyam.coach.auth.dto.response.LoginResponse;
import com.nyamnyam.coach.auth.dto.response.LogoutResponse;
import com.nyamnyam.coach.auth.dto.response.SignupResponse;
import com.nyamnyam.coach.auth.dto.response.TokenRefreshResponse;
import com.nyamnyam.coach.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Auth", description = "회원가입, 로그인, 토큰 재발급, 로그아웃 API")
public interface AuthApiDocs {

    @Operation(summary = "회원가입", description = "이메일, 비밀번호, 닉네임으로 회원 계정을 생성합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원가입 성공",
                    content = @Content(schema = @Schema(implementation = SignupResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 검증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이메일 또는 닉네임 중복",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "success": false,
                              "code": "EMAIL_ALREADY_EXISTS",
                              "message": "이미 가입된 이메일입니다."
                            }
                            """)))
    })
    ResponseEntity<ApiResponse<SignupResponse>> signup(SignupRequest request);

    @Operation(summary = "로그인", description = "이메일과 비밀번호를 검증하고 access token과 refresh token을 발급합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 검증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "이메일 또는 비밀번호 불일치"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "비활성화 계정")
    })
    ResponseEntity<ApiResponse<LoginResponse>> login(LoginRequest request);

    @Operation(summary = "토큰 재발급", description = "refresh token을 검증해 새 토큰을 발급하고 refresh token rotation을 적용합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "토큰 재발급 성공",
                    content = @Content(schema = @Schema(implementation = TokenRefreshResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 검증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "유효하지 않은 refresh token")
    })
    ResponseEntity<ApiResponse<TokenRefreshResponse>> refresh(TokenRefreshRequest request);

    @Operation(summary = "로그아웃", description = "refresh token을 삭제합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그아웃 성공",
                    content = @Content(schema = @Schema(implementation = LogoutResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 검증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패 또는 유효하지 않은 token")
    })
    ResponseEntity<ApiResponse<LogoutResponse>> logout(LogoutRequest request);
}
