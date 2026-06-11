package com.nyamnyam.coach.user.dto.response;

import java.time.LocalDateTime;

public record ChangePasswordResponse(
        Long userId,
        LocalDateTime changedAt
) {
}
