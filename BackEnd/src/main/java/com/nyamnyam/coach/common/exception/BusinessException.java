package com.nyamnyam.coach.common.exception;

import lombok.Getter;

/**
 * 서비스 레이어에서 발생하는 비즈니스 예외.
 * ErrorCode에 정의된 상태코드와 메시지를 함께 담아 GlobalExceptionHandler에서 처리된다.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
