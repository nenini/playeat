package com.nyamnyam.coach.global.exception.errorcode;

import org.springframework.http.HttpStatus;

public enum QuestErrorCode implements ErrorCode {

    QUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "퀘스트를 찾을 수 없습니다."),
    QUEST_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 생성된 퀘스트가 있습니다."),
    QUEST_GENERATION_FAILED(HttpStatus.CONFLICT, "퀘스트 생성에 실패했습니다."),
    QUEST_ACCESS_DENIED(HttpStatus.FORBIDDEN, "퀘스트 접근 권한이 없습니다."),
    QUEST_NOT_OWNER(HttpStatus.FORBIDDEN, "퀘스트 담당자만 처리할 수 있습니다."),
    QUEST_NOT_IN_PROGRESS(HttpStatus.CONFLICT, "진행 중인 퀘스트가 아닙니다."),
    QUEST_ALREADY_COMPLETED(HttpStatus.CONFLICT, "이미 완료된 퀘스트입니다."),
    QUEST_VERIFY_FAILED(HttpStatus.BAD_REQUEST, "퀘스트 조건을 달성하지 못했습니다."),
    QUEST_VERIFICATION_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 검증된 퀘스트입니다."),
    QUEST_UNSUPPORTED_TYPE(HttpStatus.BAD_REQUEST, "아직 지원하지 않는 퀘스트 타입입니다."),
    QUEST_REWARD_NOT_CLAIMABLE(HttpStatus.CONFLICT, "퀘스트 보상을 수령할 수 없습니다."),
    ACTIVE_GUILD_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "활성 길드원을 찾을 수 없습니다."),
    QUEST_NOT_COMPLETED(HttpStatus.CONFLICT, "아직 완료하지 않은 퀘스트입니다."),
    QUEST_REWARD_ALREADY_CLAIMED(HttpStatus.CONFLICT, "이미 퀘스트 보상을 수령했습니다."),
    QUEST_TEMPLATE_NOT_FOUND(HttpStatus.NOT_FOUND, "사용 가능한 퀘스트 템플릿을 찾을 수 없습니다.");

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
