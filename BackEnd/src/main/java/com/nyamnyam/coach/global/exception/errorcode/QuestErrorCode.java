package com.nyamnyam.coach.global.exception.errorcode;

import org.springframework.http.HttpStatus;

public enum QuestErrorCode implements ErrorCode {

    QUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "퀘스트를 찾을 수 없습니다."),
    QUEST_NOT_COMPLETED(HttpStatus.CONFLICT, "아직 완료하지 않은 퀘스트입니다."),
    QUEST_REWARD_ALREADY_CLAIMED(HttpStatus.CONFLICT, "이미 퀘스트 보상을 수령했습니다.");

    private final HttpStatus status;
    private final String message;

    QuestErrorCode(HttpStatus status, String message) {
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
