package com.nyamnyam.coach.global.exception.errorcode;

import org.springframework.http.HttpStatus;

public enum DietErrorCode implements ErrorCode {

    INVALID_AMOUNT(HttpStatus.BAD_REQUEST, "섭취량이 올바르지 않습니다."),
    DIET_NOT_FOUND(HttpStatus.NOT_FOUND, "식단 기록을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    DietErrorCode(HttpStatus status, String message) {
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
