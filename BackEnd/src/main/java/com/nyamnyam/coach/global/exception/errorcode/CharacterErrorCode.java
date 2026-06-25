package com.nyamnyam.coach.global.exception.errorcode;

import org.springframework.http.HttpStatus;

public enum CharacterErrorCode implements ErrorCode {

    CHARACTER_NOT_FOUND(HttpStatus.NOT_FOUND, "캐릭터를 찾을 수 없습니다."),
    INVALID_CHARACTER_STATE(HttpStatus.CONFLICT, "현재 캐릭터 상태에서는 처리할 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    CharacterErrorCode(HttpStatus status, String message) {
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
