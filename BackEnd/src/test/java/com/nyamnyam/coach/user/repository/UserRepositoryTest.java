package com.nyamnyam.coach.user.repository;

import com.nyamnyam.coach.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

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
}
