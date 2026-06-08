package com.nyamnyam.coach.auth.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Collection;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenRepositoryTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private SetOperations<String, String> setOperations;

    private RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        lenient().when(redisTemplate.opsForSet()).thenReturn(setOperations);
        refreshTokenRepository = new RefreshTokenRepository(redisTemplate);
    }

    @Test
    @DisplayName("refresh token hash를 userId와 TTL로 Redis에 저장한다")
    void saveRefreshToken() {
        Duration ttl = Duration.ofDays(7);

        refreshTokenRepository.save(1L, "token-hash-1", ttl);

        verify(valueOperations).set("refresh:token-hash-1", "1", ttl);
        verify(setOperations).add("user-refresh-tokens:1", "token-hash-1");
        verify(redisTemplate).expire("user-refresh-tokens:1", ttl);
    }

    @Test
    @DisplayName("refresh token hash로 userId를 조회한다")
    void findUserIdByTokenHash() {
        when(valueOperations.get("refresh:token-hash-1")).thenReturn("1");

        assertThat(refreshTokenRepository.findUserIdByTokenHash("token-hash-1"))
                .contains(1L);
    }

    @Test
    @DisplayName("Redis에 token hash가 없으면 빈 Optional을 반환한다")
    void findUserIdByMissingTokenHash() {
        when(valueOperations.get("refresh:missing-token")).thenReturn(null);

        assertThat(refreshTokenRepository.findUserIdByTokenHash("missing-token"))
                .isEmpty();
    }

    @Test
    @DisplayName("refresh token 하나를 삭제하고 사용자 token 목록에서도 제거한다")
    void revokeByTokenHash() {
        when(valueOperations.get("refresh:token-hash-1")).thenReturn("1");
        when(redisTemplate.delete("refresh:token-hash-1")).thenReturn(true);

        int deletedCount = refreshTokenRepository.revokeByTokenHash("token-hash-1");

        assertThat(deletedCount).isEqualTo(1);
        verify(redisTemplate).delete("refresh:token-hash-1");
        verify(setOperations).remove("user-refresh-tokens:1", "token-hash-1");
    }

    @Test
    @DisplayName("사용자에게 발급된 모든 refresh token을 삭제한다")
    void revokeAllByUserId() {
        when(setOperations.members("user-refresh-tokens:1"))
                .thenReturn(Set.of("token-a", "token-b"));
        when(redisTemplate.delete(any(Collection.class))).thenReturn(3L);

        int deletedCount = refreshTokenRepository.revokeAllByUserId(1L);

        assertThat(deletedCount).isEqualTo(3);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Collection<String>> keysCaptor = ArgumentCaptor.forClass(Collection.class);
        verify(redisTemplate).delete(keysCaptor.capture());
        assertThat(keysCaptor.getValue())
                .containsExactlyInAnyOrder(
                        "refresh:token-a",
                        "refresh:token-b",
                        "user-refresh-tokens:1"
                );
    }
}
