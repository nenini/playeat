package com.nyamnyam.coach.global.exception.errorcode;

import org.springframework.http.HttpStatus;

public enum BossErrorCode implements ErrorCode {

    BOSS_NOT_FOUND(HttpStatus.NOT_FOUND, "보스를 찾을 수 없습니다."),
    BOSS_BATTLE_NOT_FOUND(HttpStatus.NOT_FOUND, "보스 전투를 찾을 수 없습니다."),
    BOSS_BATTLE_NOT_ACTIVE(HttpStatus.CONFLICT, "진행 중인 보스 전투가 아닙니다.");

    private final HttpStatus status;
    private final String message;

    BossErrorCode(HttpStatus status, String message) {
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
