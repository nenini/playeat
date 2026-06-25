package com.nyamnyam.coach.global.exception.errorcode;

import org.springframework.http.HttpStatus;

public enum RankingErrorCode implements ErrorCode {

    RANKING_PERIOD_INVALID(HttpStatus.BAD_REQUEST, "랭킹 조회 기간이 올바르지 않습니다."),
    RANKING_NOT_FOUND(HttpStatus.NOT_FOUND, "랭킹 정보를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    RankingErrorCode(HttpStatus status, String message) {
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
