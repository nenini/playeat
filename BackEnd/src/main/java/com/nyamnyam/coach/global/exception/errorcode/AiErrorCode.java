package com.nyamnyam.coach.global.exception.errorcode;

import org.springframework.http.HttpStatus;

public enum AiErrorCode implements ErrorCode {

    AI_FEEDBACK_NOT_FOUND(HttpStatus.NOT_FOUND, "AI 피드백을 찾을 수 없습니다."),
    AI_RESPONSE_FAILED(HttpStatus.BAD_GATEWAY, "AI 응답 생성에 실패했습니다.");

    private final HttpStatus status;
    private final String message;

    AiErrorCode(HttpStatus status, String message) {
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
