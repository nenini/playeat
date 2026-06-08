package com.nyamnyam.coach.user.service;

import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.AuthErrorCode;
import com.nyamnyam.coach.global.exception.errorcode.UserErrorCode;
import com.nyamnyam.coach.user.dto.request.DeactivateUserRequest;
import com.nyamnyam.coach.user.dto.request.UpdateUserRequest;
import com.nyamnyam.coach.user.dto.response.DeactivateUserResponse;
import com.nyamnyam.coach.user.dto.response.UpdateUserResponse;
import com.nyamnyam.coach.user.dto.response.UserMeResponse;
import com.nyamnyam.coach.user.entity.User;
import com.nyamnyam.coach.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    @DisplayName("내 회원 정보를 조회한다")
    void getMe() {
        User user = activeUser("encoded-password");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserMeResponse response = userService.getMe(1L);

        assertThat(response.userId()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo("user@example.com");
        assertThat(response.nickname()).isEqualTo("nyam");
        assertThat(response.status()).isEqualTo("ACTIVE");
        assertThat(response.onboardingCompleted()).isFalse();
    }

    @Test
    @DisplayName("닉네임을 수정한다")
    void updateMe() {
        User user = activeUser("encoded-password");
        User updatedUser = activeUser("encoded-password");
        updatedUser.setNickname("newnyam");
        updatedUser.setUpdatedAt(LocalDateTime.of(2026, 5, 26, 11, 0));

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user))
                .thenReturn(Optional.of(updatedUser));
        when(userRepository.existsByNickname("newnyam")).thenReturn(false);

        UpdateUserResponse response = userService.updateMe(1L, new UpdateUserRequest("newnyam"));

        verify(userRepository).updateProfile(1L, "newnyam");
        assertThat(response.userId()).isEqualTo(1L);
        assertThat(response.nickname()).isEqualTo("newnyam");
        assertThat(response.updatedAt()).isEqualTo(LocalDateTime.of(2026, 5, 26, 11, 0));
    }

    @Test
    @DisplayName("기존 닉네임과 같으면 중복 검사를 하지 않고 수정한다")
    void updateMeWithSameNickname() {
        User user = activeUser("encoded-password");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user), Optional.of(user));

        userService.updateMe(1L, new UpdateUserRequest("nyam"));

        verify(userRepository, never()).existsByNickname("nyam");
        verify(userRepository).updateProfile(1L, "nyam");
    }

    @Test
    @DisplayName("다른 사용자의 닉네임이면 NICKNAME_ALREADY_EXISTS 예외를 던진다")
    void updateMeWithDuplicateNickname() {
        User user = activeUser("encoded-password");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByNickname("taken")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateMe(1L, new UpdateUserRequest("taken")))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.NICKNAME_ALREADY_EXISTS);

        verify(userRepository, never()).updateProfile(1L, "taken");
    }

    @Test
    @DisplayName("비밀번호 확인 후 회원을 비활성화한다")
    void deactivateMe() {
        String passwordHash = passwordEncoder.encode("password123!");
        User user = activeUser(passwordHash);
        User deactivatedUser = activeUser(passwordHash);
        deactivatedUser.setStatus("INACTIVE");
        deactivatedUser.setDeactivatedAt(LocalDateTime.of(2026, 5, 26, 11, 30));

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user))
                .thenReturn(Optional.of(deactivatedUser));

        DeactivateUserResponse response = userService.deactivateMe(
                1L,
                new DeactivateUserRequest("password123!")
        );

        verify(userRepository).deactivate(1L);
        assertThat(response.userId()).isEqualTo(1L);
        assertThat(response.status()).isEqualTo("INACTIVE");
        assertThat(response.deactivatedAt()).isEqualTo(LocalDateTime.of(2026, 5, 26, 11, 30));
    }

    @Test
    @DisplayName("비밀번호가 틀리면 INVALID_CREDENTIALS 예외를 던진다")
    void deactivateMeWithWrongPassword() {
        User user = activeUser(passwordEncoder.encode("password123!"));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.deactivateMe(1L, new DeactivateUserRequest("wrong-password")))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.INVALID_CREDENTIALS);

        verify(userRepository, never()).deactivate(1L);
    }

    @Test
    @DisplayName("회원을 찾지 못하면 USER_NOT_FOUND 예외를 던진다")
    void getMeWithMissingUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getMe(1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(UserErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("비활성 회원이면 USER_INACTIVE 예외를 던진다")
    void getMeWithInactiveUser() {
        User user = activeUser("encoded-password");
        user.setStatus("INACTIVE");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.getMe(1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.USER_INACTIVE);
    }

    private User activeUser(String passwordHash) {
        return User.builder()
                .userId(1L)
                .email("user@example.com")
                .passwordHash(passwordHash)
                .nickname("nyam")
                .status("ACTIVE")
                .onboardingCompleted(false)
                .createdAt(LocalDateTime.of(2026, 5, 26, 10, 0))
                .updatedAt(LocalDateTime.of(2026, 5, 26, 10, 0))
                .build();
    }
}
