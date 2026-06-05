package com.nyamnyam.coach.global.exception.errorcode;

import org.springframework.http.HttpStatus;

public enum GuildErrorCode implements ErrorCode {

    GUILD_NOT_FOUND(HttpStatus.NOT_FOUND, "길드를 찾을 수 없습니다."),
    GUILD_ALREADY_JOINED(HttpStatus.CONFLICT, "이미 길드에 가입되어 있습니다."),
    GUILD_FULL(HttpStatus.CONFLICT, "길드 정원이 가득 찼습니다."),
    GUILD_JOIN_REQUEST_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 길드 가입을 신청했습니다."),
    GUILD_JOIN_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "길드 가입 신청을 찾을 수 없습니다."),
    GUILD_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "길드 관리 권한이 없습니다.");

    private final HttpStatus status;
    private final String message;

    GuildErrorCode(HttpStatus status, String message) {
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
