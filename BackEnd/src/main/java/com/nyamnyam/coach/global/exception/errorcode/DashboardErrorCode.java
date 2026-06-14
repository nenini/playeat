package com.nyamnyam.coach.global.exception.errorcode;

import org.springframework.http.HttpStatus;

public enum DashboardErrorCode implements ErrorCode {

    DASHBOARD_PERIOD_INVALID(HttpStatus.BAD_REQUEST, "대시보드 조회 기간이 올바르지 않습니다."),
    DASHBOARD_DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "대시보드 데이터를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    DashboardErrorCode(HttpStatus status, String message) {
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
