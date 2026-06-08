package com.nyamnyam.coach.auth.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private static final String REFRESH_TOKEN_KEY_PREFIX = "refresh:";
    private static final String USER_REFRESH_TOKENS_KEY_PREFIX = "user-refresh-tokens:";

    private final StringRedisTemplate redisTemplate;

    public void save(Long userId, String tokenHash, Duration ttl) {
        String refreshTokenKey = refreshTokenKey(tokenHash);
        String userRefreshTokensKey = userRefreshTokensKey(userId);

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        SetOperations<String, String> setOperations = redisTemplate.opsForSet();

        valueOperations.set(refreshTokenKey, String.valueOf(userId), ttl);
        setOperations.add(userRefreshTokensKey, tokenHash);
        redisTemplate.expire(userRefreshTokensKey, ttl);
    }

    public Optional<Long> findUserIdByTokenHash(String tokenHash) {
        String userId = redisTemplate.opsForValue().get(refreshTokenKey(tokenHash));

        if (userId == null) {
            return Optional.empty();
        }

        return Optional.of(Long.valueOf(userId));
    }

    public int revokeByTokenHash(String tokenHash) {
        Optional<Long> userId = findUserIdByTokenHash(tokenHash);
        Boolean deleted = redisTemplate.delete(refreshTokenKey(tokenHash));

        userId.ifPresent(id -> redisTemplate.opsForSet().remove(userRefreshTokensKey(id), tokenHash));

        return Boolean.TRUE.equals(deleted) ? 1 : 0;
    }

    public int revokeAllByUserId(Long userId) {
        String userRefreshTokensKey = userRefreshTokensKey(userId);
        Set<String> tokenHashes = redisTemplate.opsForSet().members(userRefreshTokensKey);

        if (tokenHashes == null || tokenHashes.isEmpty()) {
            Boolean deleted = redisTemplate.delete(userRefreshTokensKey);
            return Boolean.TRUE.equals(deleted) ? 1 : 0;
        }

        List<String> keysToDelete = new ArrayList<>();
        for (String tokenHash : tokenHashes) {
            keysToDelete.add(refreshTokenKey(tokenHash));
        }
        keysToDelete.add(userRefreshTokensKey);

        Long deletedCount = redisTemplate.delete(keysToDelete);
        return deletedCount == null ? 0 : deletedCount.intValue();
    }

    private String refreshTokenKey(String tokenHash) {
        return REFRESH_TOKEN_KEY_PREFIX + tokenHash;
    }

    private String userRefreshTokensKey(Long userId) {
        return USER_REFRESH_TOKENS_KEY_PREFIX + userId;
    }
}
