package com.nyamnyam.coach.global.exception.errorcode;

import org.springframework.http.HttpStatus;

public enum CoinErrorCode implements ErrorCode {

    INSUFFICIENT_COIN(HttpStatus.CONFLICT, "코인이 부족합니다."),
    COIN_HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "코인 내역을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    CoinErrorCode(HttpStatus status, String message) {
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
