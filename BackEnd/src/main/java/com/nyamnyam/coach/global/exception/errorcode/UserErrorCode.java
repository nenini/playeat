package com.nyamnyam.coach.global.exception.errorcode;

import org.springframework.http.HttpStatus;

public enum UserErrorCode implements ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    ONBOARDING_ALREADY_COMPLETED(HttpStatus.CONFLICT, "이미 온보딩을 완료했습니다."),
    HEALTH_PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "건강 프로필을 찾을 수 없습니다."),
    INVALID_PROFILE_IMAGE(HttpStatus.BAD_REQUEST, "올바른 프로필 이미지 파일이 아닙니다."),
    PROFILE_IMAGE_STORAGE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "프로필 이미지 저장에 실패했습니다.");

    private final HttpStatus status;
    private final String message;

    UserErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
