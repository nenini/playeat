package com.nyamnyam.coach.global.response;

public record FieldErrorDetail(
        String field,
        String reason
) {
}
