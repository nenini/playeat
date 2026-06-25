package com.nyamnyam.coach.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nyamnyam.coach.global.exception.errorcode.ErrorCode;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErrorResponse(
        boolean success,
        String code,
        String message,
        List<FieldErrorDetail> errors
) {

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(
                false,
                errorCode.getCode(),
                errorCode.getMessage(),
                null
        );
    }

    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return new ErrorResponse(
                false,
                errorCode.getCode(),
                message,
                null
        );
    }

    public static ErrorResponse validation(
            ErrorCode errorCode,
            List<FieldErrorDetail> errors
    ) {
        return new ErrorResponse(
                false,
                errorCode.getCode(),
                errorCode.getMessage(),
                errors
        );
    }
}
