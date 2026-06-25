package com.nyamnyam.coach.global.exception.errorcode;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

    HttpStatus getStatus();

    String getMessage();

    default String getCode() {
        return ((Enum<?>) this).name();
    }
}
