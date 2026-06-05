package com.nyamnyam.coach.global.exception.errorcode;

import org.springframework.http.HttpStatus;

public enum ShopErrorCode implements ErrorCode {

    SHOP_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "상점 아이템을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    ShopErrorCode(HttpStatus status, String message) {
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
