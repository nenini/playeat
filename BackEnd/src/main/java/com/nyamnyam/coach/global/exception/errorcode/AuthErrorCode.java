package com.nyamnyam.coach.global.exception.errorcode;

import org.springframework.http.HttpStatus;

public enum AuthErrorCode implements ErrorCode {

    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),
    NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),
    USER_INACTIVE(HttpStatus.FORBIDDEN, "비활성화된 사용자입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    OAUTH_GOOGLE_FAILED(HttpStatus.BAD_GATEWAY, "Google 로그인 처리에 실패했습니다."),
    OAUTH_EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 이메일로 가입된 계정입니다. 일반 로그인을 이용해주세요."),
    OAUTH_INVALID_RESPONSE(HttpStatus.BAD_GATEWAY, "Google 사용자 정보를 확인할 수 없습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Refresh token이 만료되었습니다. 다시 로그인해주세요.");

    private final HttpStatus status;
    private final String message;

    AuthErrorCode(HttpStatus status, String message) {
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
