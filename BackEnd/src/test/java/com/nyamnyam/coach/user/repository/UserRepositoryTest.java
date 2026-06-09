package com.nyamnyam.coach.user.repository;

import com.nyamnyam.coach.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@ActiveProfiles("test")
@Sql(scripts = "/test-auth-schema.sql")
class UserRepositoryTest {

    private final UserRepository userRepository;

    @Autowired
    UserRepositoryTest(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Test
    @DisplayName("사용자를 저장하고 이메일과 ID로 조회한다")
    void saveAndFindUser() {
        User user = User.builder()
                .email("user@example.com")
                .passwordHash("encoded-password")
                .nickname("nyam")
                .status("ACTIVE")
                .build();

        userRepository.save(user);

        assertThat(user.getUserId()).isNotNull();

        Optional<User> byEmail = userRepository.findByEmail("user@example.com");
        Optional<User> byId = userRepository.findById(user.getUserId());

        assertThat(byEmail).isPresent();
        assertThat(byEmail.get().getNickname()).isEqualTo("nyam");
        assertThat(byId).isPresent();
        assertThat(byId.get().getEmail()).isEqualTo("user@example.com");
    }

    @Test
    @DisplayName("이메일과 닉네임 중복 여부를 조회한다")
    void existsByEmailAndNickname() {
        User user = User.builder()
                .email("duplicate@example.com")
                .passwordHash("encoded-password")
                .nickname("duplicate")
                .status("ACTIVE")
                .build();

        userRepository.save(user);

        assertThat(userRepository.existsByEmail("duplicate@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("missing@example.com")).isFalse();
        assertThat(userRepository.existsByNickname("duplicate")).isTrue();
        assertThat(userRepository.existsByNickname("missing")).isFalse();
    }

    @Test
    @DisplayName("닉네임을 수정하고 updatedAt을 갱신한다")
    void updateProfile() {
        User user = User.builder()
                .email("update@example.com")
                .passwordHash("encoded-password")
                .nickname("before")
                .status("ACTIVE")
                .build();
        userRepository.save(user);

        int updatedCount = userRepository.updateProfile(user.getUserId(), "after");

        User updatedUser = userRepository.findById(user.getUserId()).orElseThrow();
        assertThat(updatedCount).isEqualTo(1);
        assertThat(updatedUser.getNickname()).isEqualTo("after");
        assertThat(updatedUser.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("회원을 비활성화하고 탈퇴 시각을 기록한다")
    void deactivate() {
        User user = User.builder()
                .email("deactivate@example.com")
                .passwordHash("encoded-password")
                .nickname("deactivate")
                .status("ACTIVE")
                .build();
        userRepository.save(user);

        int updatedCount = userRepository.deactivate(user.getUserId());

        User deactivatedUser = userRepository.findById(user.getUserId()).orElseThrow();
        assertThat(updatedCount).isEqualTo(1);
        assertThat(deactivatedUser.getStatus()).isEqualTo("INACTIVE");
        assertThat(deactivatedUser.getDeactivatedAt()).isNotNull();
    }

    @Test
    @DisplayName("탈퇴 사용자를 같은 이메일로 재활성화한다")
    void reactivateInactiveUser() {
        User inactiveUser = User.builder()
                .email("reactivate@example.com")
                .passwordHash("old-password")
                .nickname("old-nickname")
                .status("INACTIVE")
                .onboardingCompleted(true)
                .deactivatedAt(LocalDateTime.of(2026, 5, 26, 10, 0))
                .build();

        userRepository.save(inactiveUser);

        inactiveUser.setPasswordHash("new-password");
        inactiveUser.setNickname("new-nickname");
        inactiveUser.setSelectedCoachId(null);
        inactiveUser.setStatus("ACTIVE");
        inactiveUser.setOnboardingCompleted(false);
        inactiveUser.setDeactivatedAt(null);

        userRepository.reactivate(inactiveUser);

        User reactivatedUser = userRepository.findByEmail("reactivate@example.com").orElseThrow();

        assertThat(reactivatedUser.getUserId()).isEqualTo(inactiveUser.getUserId());
        assertThat(reactivatedUser.getPasswordHash()).isEqualTo("new-password");
        assertThat(reactivatedUser.getNickname()).isEqualTo("new-nickname");
        assertThat(reactivatedUser.getStatus()).isEqualTo("ACTIVE");
        assertThat(reactivatedUser.getOnboardingCompleted()).isFalse();
        assertThat(reactivatedUser.getDeactivatedAt()).isNull();
    }

    @Test
    @DisplayName("닉네임 중복 검사에서 같은 사용자는 제외할 수 있다")
    void existsByNicknameExcludingUserId() {
        User user = User.builder()
                .email("nickname-owner@example.com")
                .passwordHash("encoded-password")
                .nickname("owned-nickname")
                .status("INACTIVE")
                .build();

        userRepository.save(user);

        assertThat(userRepository.existsByNicknameExcludingUserId("owned-nickname", user.getUserId())).isFalse();
        assertThat(userRepository.existsByNicknameExcludingUserId("owned-nickname", user.getUserId() + 1)).isTrue();
    }
}
