package com.nyamnyam.coach.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 서비스 전체에서 사용하는 에러 코드 정의.
 * 도메인별로 prefix를 구분하여 관리 (AUTH_, USER_, FOOD_, MEAL_, CHAR_ 등)
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ── 공통 ──────────────────────────────────────
    INVALID_INPUT(HttpStatus.BAD_REQUEST,          "INVALID_INPUT",          "잘못된 입력값입니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND,        "RESOURCE_NOT_FOUND",     "요청한 리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다."),

    // ── 인증 / 인가 ───────────────────────────────
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED,          "AUTH_001",               "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED,          "AUTH_002",               "만료된 토큰입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED,    "AUTH_003",               "이메일 또는 비밀번호가 올바르지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED,           "AUTH_004",               "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN,                 "AUTH_005",               "접근 권한이 없습니다."),

    // ── 회원 ──────────────────────────────────────
    USER_NOT_FOUND(HttpStatus.NOT_FOUND,            "USER_001",               "존재하지 않는 사용자입니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT,       "USER_002",               "이미 사용 중인 이메일입니다."),
    NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT,    "USER_003",               "이미 사용 중인 닉네임입니다."),

    // ── 음식 ──────────────────────────────────────
    FOOD_NOT_FOUND(HttpStatus.NOT_FOUND,            "FOOD_001",               "존재하지 않는 음식입니다."),

    // ── 식단 기록 ──────────────────────────────────
    MEAL_RECORD_NOT_FOUND(HttpStatus.NOT_FOUND,     "MEAL_001",               "존재하지 않는 식단 기록입니다."),
    MEAL_RECORD_ACCESS_DENIED(HttpStatus.FORBIDDEN, "MEAL_002",               "해당 식단 기록에 대한 권한이 없습니다."),

    // ── 캐릭터 ────────────────────────────────────
    CHARACTER_NOT_FOUND(HttpStatus.NOT_FOUND,       "CHAR_001",               "캐릭터 정보를 찾을 수 없습니다."),

    // ── 길드 ──────────────────────────────────────
    GUILD_NOT_FOUND(HttpStatus.NOT_FOUND,           "GUILD_001",              "존재하지 않는 길드입니다."),
    GUILD_ALREADY_JOINED(HttpStatus.CONFLICT,       "GUILD_002",              "이미 가입된 길드입니다."),
    INVALID_INVITE_CODE(HttpStatus.BAD_REQUEST,     "GUILD_003",              "유효하지 않은 초대 코드입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
