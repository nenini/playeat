package com.nyamnyam.coach.auth.service;

public record GoogleOAuthUserInfo(
        String providerId,
        String email,
        String name,
        String picture
) {
}
