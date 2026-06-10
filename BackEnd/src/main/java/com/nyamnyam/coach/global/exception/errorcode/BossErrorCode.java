package com.nyamnyam.coach.global.exception.errorcode;

import org.springframework.http.HttpStatus;

public enum BossErrorCode implements ErrorCode {

    CURRENT_BOSS_NOT_FOUND(HttpStatus.NOT_FOUND, "현재 시즌 보스를 찾을 수 없습니다."),
    BOSS_NOT_FOUND(HttpStatus.NOT_FOUND, "보스를 찾을 수 없습니다."),
    BOSS_INACTIVE(HttpStatus.CONFLICT, "비활성화된 보스입니다."),
    BOSS_COMMON_CONDITION_NOT_FOUND(HttpStatus.NOT_FOUND, "보스 공통 격파 조건을 찾을 수 없습니다."),
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
