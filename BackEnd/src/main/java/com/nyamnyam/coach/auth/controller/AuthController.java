package com.nyamnyam.coach.auth.controller;

import com.nyamnyam.coach.auth.dto.request.LoginRequest;
import com.nyamnyam.coach.auth.dto.request.LogoutRequest;
import com.nyamnyam.coach.auth.dto.request.SignupRequest;
import com.nyamnyam.coach.auth.dto.request.TokenRefreshRequest;
import com.nyamnyam.coach.auth.dto.response.LoginResponse;
import com.nyamnyam.coach.auth.dto.response.LogoutResponse;
import com.nyamnyam.coach.auth.dto.response.SignupResponse;
import com.nyamnyam.coach.auth.dto.response.TokenRefreshResponse;
import com.nyamnyam.coach.auth.service.AuthService;
import com.nyamnyam.coach.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthController implements AuthApiDocs {

    private final AuthService authService;

    @Override
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(
            @Valid @RequestBody SignupRequest request
    ) {
        SignupResponse response = authService.signup(request);
        return ResponseEntity.ok(ApiResponse.success(response, "회원가입이 완료되었습니다."));
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "로그인에 성공했습니다."));
    }

    @Override
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refresh(
            @Valid @RequestBody TokenRefreshRequest request
    ) {
        TokenRefreshResponse response = authService.refresh(request);
        return ResponseEntity.ok(ApiResponse.success(response, "토큰이 재발급되었습니다."));
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<LogoutResponse>> logout(
            @Valid @RequestBody LogoutRequest request
    ) {
        LogoutResponse response = authService.logout(request);
        return ResponseEntity.ok(ApiResponse.success(response, "로그아웃되었습니다."));
    }
}
