package com.nyamnyam.coach.user.repository;

import com.nyamnyam.coach.user.entity.User;
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
    void saveAndFindUser() {
        User user = user("user@example.com", "nyam");

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
    void duplicateNicknameIsAllowed() {
        User first = user("first@example.com", "duplicate");
        User second = user("second@example.com", "duplicate");

        userRepository.save(first);
        userRepository.save(second);

        assertThat(first.getUserId()).isNotNull();
        assertThat(second.getUserId()).isNotNull();
        assertThat(userRepository.existsByEmail("first@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("missing@example.com")).isFalse();
    }

    @Test
    void updateProfile() {
        User user = user("update@example.com", "before");
        userRepository.save(user);

        int updatedCount = userRepository.updateProfile(user.getUserId(), "after");

        User updatedUser = userRepository.findById(user.getUserId()).orElseThrow();
        assertThat(updatedCount).isEqualTo(1);
        assertThat(updatedUser.getNickname()).isEqualTo("after");
        assertThat(updatedUser.getUpdatedAt()).isNotNull();
    }

    @Test
    void updatePassword() {
        User user = user("password@example.com", "password");
        userRepository.save(user);

        int updatedCount = userRepository.updatePassword(user.getUserId(), "new-encoded-password");

        User updatedUser = userRepository.findById(user.getUserId()).orElseThrow();
        assertThat(updatedCount).isEqualTo(1);
        assertThat(updatedUser.getPasswordHash()).isEqualTo("new-encoded-password");
        assertThat(updatedUser.getUpdatedAt()).isNotNull();
    }

    @Test
    void updateAndDeleteProfileImage() {
        User user = user("profile@example.com", "profile");
        userRepository.save(user);

        int updateCount = userRepository.updateProfileImage(user.getUserId(), "/uploads/profile-images/profile.png");
        User imageUpdatedUser = userRepository.findById(user.getUserId()).orElseThrow();

        assertThat(updateCount).isEqualTo(1);
        assertThat(imageUpdatedUser.getProfileImageUrl()).isEqualTo("/uploads/profile-images/profile.png");

        int deleteCount = userRepository.deleteProfileImage(user.getUserId());
        User imageDeletedUser = userRepository.findById(user.getUserId()).orElseThrow();

        assertThat(deleteCount).isEqualTo(1);
        assertThat(imageDeletedUser.getProfileImageUrl()).isNull();
    }

    @Test
    void completeOnboarding() {
        User user = user("onboarding@example.com", "onboarding");
        userRepository.save(user);

        int updatedCount = userRepository.completeOnboarding(user.getUserId(), 1L);
        User completedUser = userRepository.findById(user.getUserId()).orElseThrow();

        assertThat(updatedCount).isEqualTo(1);
        assertThat(completedUser.getOnboardingCompleted()).isTrue();
        assertThat(completedUser.getSelectedCoachId()).isEqualTo(1L);
    }

    @Test
    void releaseInactiveEmail() {
        User inactiveUser = User.builder()
                .email("reactivate@example.com")
                .passwordHash("old-password")
                .nickname("old-nickname")
                .status("INACTIVE")
                .onboardingCompleted(true)
                .deactivatedAt(LocalDateTime.of(2026, 5, 26, 10, 0))
                .build();
        userRepository.save(inactiveUser);

        int updatedCount = userRepository.releaseInactiveEmail(inactiveUser.getUserId());

        User releasedUser = userRepository.findById(inactiveUser.getUserId()).orElseThrow();
        assertThat(updatedCount).isEqualTo(1);
        assertThat(userRepository.findByEmail("reactivate@example.com")).isEmpty();
        assertThat(releasedUser.getEmail()).startsWith("deleted-" + inactiveUser.getUserId() + "-");
        assertThat(releasedUser.getStatus()).isEqualTo("INACTIVE");
    }

    @Test
    void deactivate() {
        User user = user("deactivate@example.com", "deactivate");
        userRepository.save(user);

        int updatedCount = userRepository.deactivate(user.getUserId());

        User deactivatedUser = userRepository.findById(user.getUserId()).orElseThrow();
        assertThat(updatedCount).isEqualTo(1);
        assertThat(deactivatedUser.getStatus()).isEqualTo("INACTIVE");
        assertThat(deactivatedUser.getDeactivatedAt()).isNotNull();
        assertThat(userRepository.findByEmail("deactivate@example.com")).isEmpty();
        assertThat(deactivatedUser.getEmail()).startsWith("deleted-" + user.getUserId() + "-");
    }

    private User user(String email, String nickname) {
        return User.builder()
                .email(email)
                .passwordHash("encoded-password")
                .nickname(nickname)
                .status("ACTIVE")
                .build();
    }
}
