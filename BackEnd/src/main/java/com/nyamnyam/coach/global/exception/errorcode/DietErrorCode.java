package com.nyamnyam.coach.global.exception.errorcode;

import org.springframework.http.HttpStatus;

public enum DietErrorCode implements ErrorCode {

    DIET_NOT_FOUND(HttpStatus.NOT_FOUND, "식단 기록을 찾을 수 없습니다."),
    DIET_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 해당 날짜와 끼니에 식단 기록이 있습니다."),
    INVALID_MEAL_TYPE(HttpStatus.BAD_REQUEST, "끼니 유형이 올바르지 않습니다."),
    INVALID_EATEN_AT(HttpStatus.BAD_REQUEST, "식사 시간이 올바르지 않습니다."),
    INVALID_DIET_ITEMS(HttpStatus.BAD_REQUEST, "식단 음식 목록이 올바르지 않습니다."),
    INVALID_AMOUNT(HttpStatus.BAD_REQUEST, "입력량이 올바르지 않습니다."),
    UNSUPPORTED_UNIT(HttpStatus.BAD_REQUEST, "지원하지 않는 입력 단위입니다."),
    FOOD_UNIT_CONVERSION_FAILED(HttpStatus.BAD_REQUEST, "음식 단위 변환에 실패했습니다.");

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
