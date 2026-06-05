package com.nyamnyam.coach.global.exception.errorcode;

import org.springframework.http.HttpStatus;

public enum NutritionErrorCode implements ErrorCode {

    NUTRITION_SUMMARY_NOT_FOUND(HttpStatus.NOT_FOUND, "영양 분석 결과를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    NutritionErrorCode(HttpStatus status, String message) {
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
