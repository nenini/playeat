package com.nyamnyam.coach.user.controller;

import com.nyamnyam.coach.global.response.ApiResponse;
import com.nyamnyam.coach.user.dto.request.DeactivateUserRequest;
import com.nyamnyam.coach.user.dto.request.UpdateUserRequest;
import com.nyamnyam.coach.user.dto.response.DeactivateUserResponse;
import com.nyamnyam.coach.user.dto.response.UpdateUserResponse;
import com.nyamnyam.coach.user.dto.response.UserMeResponse;
import com.nyamnyam.coach.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "User", description = "User account APIs")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get my account")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserMeResponse>> getMe(Authentication authentication) {
        UserMeResponse response = userService.getMe(authenticatedUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success(response, "My account was retrieved successfully."));
    }

    @Operation(summary = "Update my account")
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UpdateUserResponse>> updateMe(
            Authentication authentication,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        UpdateUserResponse response = userService.updateMe(authenticatedUserId(authentication), request);
        return ResponseEntity.ok(ApiResponse.success(response, "My account was updated successfully."));
    }

    @Operation(summary = "Deactivate my account")
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<DeactivateUserResponse>> deactivateMe(
            Authentication authentication,
            @Valid @RequestBody DeactivateUserRequest request
    ) {
        DeactivateUserResponse response = userService.deactivateMe(authenticatedUserId(authentication), request);
        return ResponseEntity.ok(ApiResponse.success(response, "My account was deactivated successfully."));
    }

    private Long authenticatedUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}
