package com.nyamnyam.coach.global.exception.errorcode;

import org.springframework.http.HttpStatus;

public enum FoodErrorCode implements ErrorCode {

    INVALID_KEYWORD(HttpStatus.BAD_REQUEST, "\uAC80\uC0C9\uC5B4\uAC00 \uC62C\uBC14\uB974\uC9C0 \uC54A\uC2B5\uB2C8\uB2E4."),
    FOOD_NOT_FOUND(HttpStatus.NOT_FOUND, "\uC74C\uC2DD \uC815\uBCF4\uB97C \uCC3E\uC744 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4."),
    FAVORITE_ALREADY_EXISTS(HttpStatus.CONFLICT, "\uC774\uBBF8 \uC990\uACA8\uCC3E\uAE30\uC5D0 \uB4F1\uB85D\uB41C \uC74C\uC2DD\uC785\uB2C8\uB2E4."),
    FAVORITE_NOT_FOUND(HttpStatus.NOT_FOUND, "\uC990\uACA8\uCC3E\uAE30 \uC815\uBCF4\uB97C \uCC3E\uC744 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4.");

    private final HttpStatus status;
    private final String message;

    FoodErrorCode(HttpStatus status, String message) {
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
