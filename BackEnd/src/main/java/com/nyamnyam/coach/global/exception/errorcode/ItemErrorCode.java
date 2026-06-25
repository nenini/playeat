package com.nyamnyam.coach.global.exception.errorcode;

import org.springframework.http.HttpStatus;

public enum ItemErrorCode implements ErrorCode {

    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "아이템을 찾을 수 없습니다."),
    ITEM_ALREADY_OWNED(HttpStatus.CONFLICT, "이미 보유한 아이템입니다."),
    USER_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "보유 아이템을 찾을 수 없습니다."),
    USER_ITEM_ACCESS_DENIED(HttpStatus.FORBIDDEN, "보유 아이템 접근 권한이 없습니다."),
    ITEM_NOT_OWNED(HttpStatus.FORBIDDEN, "보유한 아이템만 사용할 수 있습니다."),
    ITEM_NOT_EQUIPPABLE(HttpStatus.BAD_REQUEST, "장착할 수 없는 아이템입니다."),
    EQUIPMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "장착 정보를 찾을 수 없습니다."),
    INVALID_SLOT_TYPE(HttpStatus.BAD_REQUEST, "아이템 슬롯 타입이 올바르지 않습니다.");

    private final HttpStatus status;
    private final String message;

    ItemErrorCode(HttpStatus status, String message) {
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
