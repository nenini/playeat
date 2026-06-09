package com.nyamnyam.coach.global.exception.errorcode;

import org.springframework.http.HttpStatus;

public enum GuildErrorCode implements ErrorCode {

    GUILD_NOT_FOUND(HttpStatus.NOT_FOUND, "길드를 찾을 수 없습니다."),
    GUILD_ALREADY_INACTIVE(HttpStatus.CONFLICT, "이미 비활성화된 길드입니다."),
    GUILD_NAME_DUPLICATED(HttpStatus.CONFLICT, "이미 사용 중인 길드명입니다."),
    GUILD_ACCESS_DENIED(HttpStatus.FORBIDDEN, "길드 접근 권한이 없습니다."),
    GUILD_OWNER_ONLY(HttpStatus.FORBIDDEN, "길드장만 처리할 수 있습니다."),
    GUILD_ALREADY_JOINED(HttpStatus.CONFLICT, "이미 길드에 가입되어 있습니다."),
    GUILD_FULL(HttpStatus.CONFLICT, "길드 정원이 가득 찼습니다."),
    GUILD_CAPACITY_EXCEEDED(HttpStatus.CONFLICT, "길드 정원이 가득 찼습니다."),
    GUILD_MAX_MEMBERS_INVALID(HttpStatus.BAD_REQUEST, "길드 최대 인원은 1명 이상 30명 이하로 설정해야 합니다."),
    GUILD_MAX_MEMBERS_LESS_THAN_CURRENT_MEMBERS(HttpStatus.CONFLICT, "현재 길드원 수보다 작은 최대 인원으로 변경할 수 없습니다."),
    GUILD_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "길드원을 찾을 수 없습니다."),
    GUILD_OWNER_CANNOT_LEAVE(HttpStatus.FORBIDDEN, "길드장은 길드를 탈퇴할 수 없습니다."),
    GUILD_CANNOT_KICK_OWNER(HttpStatus.FORBIDDEN, "길드장은 추방할 수 없습니다."),
    GUILD_MEMBER_ALREADY_LEFT(HttpStatus.CONFLICT, "이미 탈퇴한 길드원입니다."),
    GUILD_NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "길드 공지사항을 찾을 수 없습니다."),
    GUILD_JOIN_REQUEST_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 길드 가입을 신청했습니다."),
    GUILD_JOIN_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "길드 가입 신청을 찾을 수 없습니다."),
    GUILD_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "길드 관리 권한이 없습니다."),
    USER_ALREADY_JOINED_GUILD(HttpStatus.CONFLICT, "이미 가입한 길드가 있습니다."),
    USER_HAS_PENDING_GUILD_REQUEST(HttpStatus.CONFLICT, "이미 처리 대기 중인 길드 참여 요청이 있습니다."),
    INVITE_CODE_DUPLICATED(HttpStatus.CONFLICT, "이미 사용 중인 길드 초대 코드입니다."),
    INVITE_CODE_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "길드 초대 코드 생성에 실패했습니다."),
    GUILD_NOTICE_INVALID(HttpStatus.BAD_REQUEST, "길드 공지사항 입력값이 올바르지 않습니다.");

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
