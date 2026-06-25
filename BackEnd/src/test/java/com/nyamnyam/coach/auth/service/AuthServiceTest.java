package com.nyamnyam.coach.auth.service;

import com.nyamnyam.coach.auth.dto.request.LoginRequest;
import com.nyamnyam.coach.auth.dto.request.GoogleOAuthLoginRequest;
import com.nyamnyam.coach.auth.dto.request.LogoutRequest;
import com.nyamnyam.coach.auth.dto.request.SignupRequest;
import com.nyamnyam.coach.auth.dto.request.TokenRefreshRequest;
import com.nyamnyam.coach.auth.dto.response.LoginResponse;
import com.nyamnyam.coach.auth.dto.response.SignupResponse;
import com.nyamnyam.coach.auth.dto.response.TokenRefreshResponse;
import com.nyamnyam.coach.auth.jwt.JwtToken;
import com.nyamnyam.coach.auth.jwt.JwtTokenProvider;
import com.nyamnyam.coach.auth.repository.RefreshTokenRepository;
import com.nyamnyam.coach.character.service.CharacterGrowthService;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.AuthErrorCode;
import com.nyamnyam.coach.user.entity.User;
import com.nyamnyam.coach.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String TEST_SECRET = "dGVzdC1zZWNyZXQta2V5LW11c3QtYmUtYXQtbGVhc3QtMjU2LWJpdHMtbG9uZy10ZXN0";

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private CharacterGrowthService characterGrowthService;

    @Mock
    private GoogleOAuthService googleOAuthService;

    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider jwtTokenProvider;
    private JwtTokenProvider expiredJwtTokenProvider;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        jwtTokenProvider = new JwtTokenProvider(TEST_SECRET, 3_600_000L, 604_800_000L);
        expiredJwtTokenProvider = new JwtTokenProvider(TEST_SECRET, 3_600_000L, -1L);
        authService = new AuthService(
                userRepository,
                refreshTokenRepository,
                passwordEncoder,
                jwtTokenProvider,
                characterGrowthService,
                googleOAuthService
        );
    }

    @Test
    @DisplayName("회원가입 성공 시 비밀번호를 BCrypt로 암호화한다")
    void signup() {
        SignupRequest request = new SignupRequest("user@example.com", "password123!", "냥냥");
        LocalDateTime createdAt = LocalDateTime.of(2026, 5, 26, 10, 0);
        User savedUser = User.builder()
                .userId(1L)
                .email(request.email())
                .passwordHash("encoded")
                .nickname(request.nickname())
                .status("ACTIVE")
                .onboardingCompleted(false)
                .createdAt(createdAt)
                .build();

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setUserId(1L);
            return null;
        }).when(userRepository).save(any(User.class));
        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));

        SignupResponse response = authService.signup(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        verify(characterGrowthService).createDefaultCharacterIfMissing(1L, request.nickname());

        User userToSave = userCaptor.getValue();
        assertThat(userToSave.getUserId()).isEqualTo(1L);
        assertThat(userToSave.getPasswordHash()).isNotEqualTo(request.password());
        assertThat(passwordEncoder.matches(request.password(), userToSave.getPasswordHash())).isTrue();
        assertThat(response.userId()).isEqualTo(1L);
        assertThat(response.createdAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("중복 이메일이면 EMAIL_ALREADY_EXISTS 예외를 던진다")
    void signupWithDuplicateEmail() {
        SignupRequest request = new SignupRequest("user@example.com", "password123!", "냥냥");
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(activeUser("encoded")));

        assertThatThrownBy(() -> authService.signup(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.EMAIL_ALREADY_EXISTS);
    }

    @Test
    @DisplayName("탈퇴한 이메일이면 새 사용자로 가입한다")
    void signupCreatesNewUserForInactiveEmail() {
        SignupRequest request = new SignupRequest("user@example.com", "new-password123!", "냥냥");
        User inactiveUser = inactiveUser("old-password");
        LocalDateTime createdAt = LocalDateTime.of(2026, 6, 22, 10, 0);
        User newUser = User.builder()
                .userId(2L)
                .email(request.email())
                .passwordHash("encoded")
                .nickname(request.nickname())
                .status("ACTIVE")
                .onboardingCompleted(false)
                .createdAt(createdAt)
                .build();

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(inactiveUser));
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setUserId(2L);
            return null;
        }).when(userRepository).save(any(User.class));
        when(userRepository.findById(2L)).thenReturn(Optional.of(newUser));

        SignupResponse response = authService.signup(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).releaseInactiveEmail(inactiveUser.getUserId());
        verify(userRepository).save(userCaptor.capture());
        verify(characterGrowthService).createDefaultCharacterIfMissing(2L, request.nickname());

        User userToSave = userCaptor.getValue();
        assertThat(userToSave.getUserId()).isEqualTo(2L);
        assertThat(userToSave.getEmail()).isEqualTo(request.email());
        assertThat(userToSave.getStatus()).isEqualTo("ACTIVE");
        assertThat(userToSave.getOnboardingCompleted()).isFalse();
        assertThat(response.userId()).isEqualTo(2L);
        assertThat(response.createdAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("로그인 성공 시 토큰을 발급하고 refresh token hash만 저장한다")
    void login() {
        User user = activeUser(passwordEncoder.encode("password123!"));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        LoginResponse response = authService.login(new LoginRequest("user@example.com", "password123!"));

        assertThat(response.accessToken()).isNotBlank();
        assertThat(response.refreshToken()).isNotBlank();
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(3600L);
        assertThat(response.user().userId()).isEqualTo(1L);

        ArgumentCaptor<String> hashCaptor = ArgumentCaptor.forClass(String.class);
        verify(refreshTokenRepository).save(eq(1L), hashCaptor.capture(), eq(Duration.ofDays(7)));
        assertThat(hashCaptor.getValue()).isNotEqualTo(response.refreshToken());
    }

    @Test
    @DisplayName("로그인 비밀번호가 틀리면 INVALID_CREDENTIALS 예외를 던진다")
    void loginWithInvalidPassword() {
        User user = activeUser(passwordEncoder.encode("password123!"));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.login(new LoginRequest("user@example.com", "wrong-password")))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.INVALID_CREDENTIALS);
    }

    @Test
    @DisplayName("Google 신규 사용자는 계정과 기본 캐릭터를 생성하고 로그인한다")
    void loginWithGoogleCreatesUser() {
        GoogleOAuthLoginRequest request = new GoogleOAuthLoginRequest("code", "http://localhost:5173/oauth/google/callback");
        GoogleOAuthUserInfo googleUser = new GoogleOAuthUserInfo("google-sub", "google@example.com", "구글냥", "https://example.com/profile.png");
        User savedUser = User.builder()
                .userId(2L)
                .email(googleUser.email())
                .nickname(googleUser.name())
                .profileImageUrl(googleUser.picture())
                .provider("GOOGLE")
                .providerId(googleUser.providerId())
                .status("ACTIVE")
                .onboardingCompleted(false)
                .build();

        when(googleOAuthService.authenticate(request)).thenReturn(googleUser);
        when(userRepository.findByProviderAndProviderId("GOOGLE", "google-sub")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("google@example.com")).thenReturn(Optional.empty());
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setUserId(2L);
            return null;
        }).when(userRepository).save(any(User.class));
        when(userRepository.findById(2L)).thenReturn(Optional.of(savedUser));

        LoginResponse response = authService.loginWithGoogle(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        verify(characterGrowthService).createDefaultCharacterIfMissing(2L, "구글냥");
        assertThat(userCaptor.getValue().getProvider()).isEqualTo("GOOGLE");
        assertThat(userCaptor.getValue().getProviderId()).isEqualTo("google-sub");
        assertThat(passwordEncoder.matches("code", userCaptor.getValue().getPasswordHash())).isFalse();
        assertThat(response.user().userId()).isEqualTo(2L);
        assertThat(response.accessToken()).isNotBlank();
    }

    @Test
    @DisplayName("기존 Google 사용자는 새 계정을 만들지 않고 로그인한다")
    void loginWithGoogleExistingUser() {
        GoogleOAuthLoginRequest request = new GoogleOAuthLoginRequest("code", "http://localhost:5173/oauth/google/callback");
        GoogleOAuthUserInfo googleUser = new GoogleOAuthUserInfo("google-sub", "google@example.com", "구글냥", null);
        User existingUser = activeUser(passwordEncoder.encode("unused-password"));
        existingUser.setEmail(googleUser.email());
        existingUser.setProvider("GOOGLE");
        existingUser.setProviderId(googleUser.providerId());

        when(googleOAuthService.authenticate(request)).thenReturn(googleUser);
        when(userRepository.findByProviderAndProviderId("GOOGLE", "google-sub")).thenReturn(Optional.of(existingUser));

        LoginResponse response = authService.loginWithGoogle(request);

        verify(userRepository, org.mockito.Mockito.never()).save(any(User.class));
        assertThat(response.user().userId()).isEqualTo(existingUser.getUserId());
    }

    @Test
    @DisplayName("LOCAL 계정과 같은 이메일이면 Google 자동 연결을 거부한다")
    void loginWithGoogleRejectsExistingEmail() {
        GoogleOAuthLoginRequest request = new GoogleOAuthLoginRequest("code", "http://localhost:5173/oauth/google/callback");
        GoogleOAuthUserInfo googleUser = new GoogleOAuthUserInfo("google-sub", "user@example.com", "구글냥", null);

        when(googleOAuthService.authenticate(request)).thenReturn(googleUser);
        when(userRepository.findByProviderAndProviderId("GOOGLE", "google-sub")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(activeUser("encoded")));

        assertThatThrownBy(() -> authService.loginWithGoogle(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.OAUTH_EMAIL_ALREADY_EXISTS);
    }

    @Test
    @DisplayName("Google 계정은 이메일과 비밀번호로 로그인할 수 없다")
    void googleUserCannotUsePasswordLogin() {
        User user = activeUser(passwordEncoder.encode("password123!"));
        user.setProvider("GOOGLE");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.login(new LoginRequest("user@example.com", "password123!")))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.INVALID_CREDENTIALS);
    }

    @Test
    @DisplayName("비활성 사용자면 USER_INACTIVE 예외를 던진다")
    void loginWithInactiveUser() {
        User user = inactiveUser(passwordEncoder.encode("password123!"));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.login(new LoginRequest("user@example.com", "password123!")))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.USER_INACTIVE);
    }

    @Test
    @DisplayName("만료된 refresh token은 REFRESH_TOKEN_EXPIRED로 거부한다")
    void refreshWithExpiredToken() {
        User user = activeUser(passwordEncoder.encode("password123!"));
        String refreshToken = issueExpiredRefreshToken(user);

        assertThatThrownBy(() -> authService.refresh(new TokenRefreshRequest(refreshToken)))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.REFRESH_TOKEN_EXPIRED);
    }

    @Test
    @DisplayName("서명이 변조된 refresh token은 INVALID_TOKEN으로 거부한다")
    void refreshWithTamperedToken() {
        User user = activeUser(passwordEncoder.encode("password123!"));
        String refreshToken = issueRefreshToken(user);
        String tamperedToken = refreshToken.substring(0, refreshToken.length() - 1) + "a";

        assertThatThrownBy(() -> authService.refresh(new TokenRefreshRequest(tamperedToken)))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.INVALID_TOKEN);
    }

    @Test
    @DisplayName("저장소에 없는 refresh token은 INVALID_TOKEN으로 거부한다")
    void refreshWithMissingTokenHash() {
        User user = activeUser(passwordEncoder.encode("password123!"));
        String refreshToken = issueRefreshToken(user);
        when(refreshTokenRepository.findUserIdByTokenHash(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refresh(new TokenRefreshRequest(refreshToken)))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.INVALID_TOKEN);
    }

    @Test
    @DisplayName("토큰 갱신 성공 시 기존 refresh token을 revoke하고 새 refresh token을 저장한다")
    void refresh() {
        User user = activeUser(passwordEncoder.encode("password123!"));
        String refreshToken = issueRefreshToken(user);

        when(refreshTokenRepository.findUserIdByTokenHash(anyString())).thenReturn(Optional.of(1L));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        TokenRefreshResponse response = authService.refresh(new TokenRefreshRequest(refreshToken));

        assertThat(response.accessToken()).isNotBlank();
        assertThat(response.refreshToken()).isNotBlank();
        assertThat(response.tokenType()).isEqualTo("Bearer");

        var inOrder = inOrder(refreshTokenRepository);
        inOrder.verify(refreshTokenRepository).findUserIdByTokenHash(anyString());
        inOrder.verify(refreshTokenRepository).revokeByTokenHash(anyString());
        inOrder.verify(refreshTokenRepository).save(eq(1L), anyString(), eq(Duration.ofDays(7)));
    }

    @Test
    @DisplayName("로그아웃 성공 시 저장된 refresh token을 폐기한다")
    void logoutRevokesStoredToken() {
        String refreshToken = issueRefreshToken(activeUser(passwordEncoder.encode("password123!")));
        when(refreshTokenRepository.findUserIdByTokenHash(anyString())).thenReturn(Optional.of(1L));

        authService.logout(new LogoutRequest(refreshToken));

        verify(refreshTokenRepository).revokeByTokenHash(anyString());
    }

    @Test
    @DisplayName("만료된 refresh token이어도 Redis에 없으면 로그아웃 성공으로 처리한다")
    void logoutWithExpiredTokenMissingFromRedis() {
        String refreshToken = issueExpiredRefreshToken(activeUser(passwordEncoder.encode("password123!")));
        when(refreshTokenRepository.findUserIdByTokenHash(anyString())).thenReturn(Optional.empty());

        authService.logout(new LogoutRequest(refreshToken));

        verify(refreshTokenRepository).findUserIdByTokenHash(anyString());
        verify(refreshTokenRepository, org.mockito.Mockito.never()).revokeByTokenHash(anyString());
    }

    @Test
    @DisplayName("이미 revoke된 refresh token이어도 로그아웃 성공으로 처리한다")
    void logoutWithMissingTokenHash() {
        String refreshToken = issueRefreshToken(activeUser(passwordEncoder.encode("password123!")));
        when(refreshTokenRepository.findUserIdByTokenHash(anyString())).thenReturn(Optional.empty());

        authService.logout(new LogoutRequest(refreshToken));

        verify(refreshTokenRepository).findUserIdByTokenHash(anyString());
        verify(refreshTokenRepository, org.mockito.Mockito.never()).revokeByTokenHash(anyString());
    }

    private User activeUser(String passwordHash) {
        return User.builder()
                .userId(1L)
                .email("user@example.com")
                .passwordHash(passwordHash)
                .nickname("냥냥")
                .status("ACTIVE")
                .onboardingCompleted(true)
                .build();
    }

    private User inactiveUser(String passwordHash) {
        return User.builder()
                .userId(1L)
                .email("user@example.com")
                .passwordHash(passwordHash)
                .nickname("냥냥")
                .status("INACTIVE")
                .onboardingCompleted(true)
                .build();
    }

    private String issueRefreshToken(User user) {
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication(String.valueOf(user.getUserId())));
        return jwtToken.getRefreshToken();
    }

    private String issueExpiredRefreshToken(User user) {
        JwtToken jwtToken = expiredJwtTokenProvider.generateToken(authentication(String.valueOf(user.getUserId())));
        return jwtToken.getRefreshToken();
    }

    private Authentication authentication(String name) {
        return new UsernamePasswordAuthenticationToken(
                name,
                "",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
