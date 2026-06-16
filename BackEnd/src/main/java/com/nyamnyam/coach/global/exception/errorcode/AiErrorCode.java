package com.nyamnyam.coach.global.exception.errorcode;

import org.springframework.http.HttpStatus;

public enum AiErrorCode implements ErrorCode {

    AI_FEEDBACK_NOT_FOUND(HttpStatus.NOT_FOUND, "AI feedback was not found."),
    AI_REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "AI report was not found."),
    AI_REPORT_PERIOD_INVALID(HttpStatus.BAD_REQUEST, "Weekly report period must start on Monday and span exactly 7 days."),
    AI_RESPONSE_FAILED(HttpStatus.BAD_GATEWAY, "AI response generation failed."),
    AI_RESPONSE_PARSE_FAILED(HttpStatus.BAD_GATEWAY, "AI response format is invalid."),
    AI_PROVIDER_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "AI provider request timed out."),
    AI_PROVIDER_UNAVAILABLE(HttpStatus.BAD_GATEWAY, "AI provider is unavailable.");

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
