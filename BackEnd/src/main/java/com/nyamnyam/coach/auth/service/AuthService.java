package com.nyamnyam.coach.auth.service;

import com.nyamnyam.coach.auth.dto.request.LoginRequest;
import com.nyamnyam.coach.auth.dto.request.GoogleOAuthLoginRequest;
import com.nyamnyam.coach.auth.dto.request.LogoutRequest;
import com.nyamnyam.coach.auth.dto.request.SignupRequest;
import com.nyamnyam.coach.auth.dto.request.TokenRefreshRequest;
import com.nyamnyam.coach.auth.dto.response.AuthUserResponse;
import com.nyamnyam.coach.auth.dto.response.LoginResponse;
import com.nyamnyam.coach.auth.dto.response.LogoutResponse;
import com.nyamnyam.coach.auth.dto.response.SignupResponse;
import com.nyamnyam.coach.auth.dto.response.TokenRefreshResponse;
import com.nyamnyam.coach.auth.jwt.JwtToken;
import com.nyamnyam.coach.auth.jwt.JwtTokenProvider;
import com.nyamnyam.coach.auth.repository.RefreshTokenRepository;
import com.nyamnyam.coach.character.service.CharacterGrowthService;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.AuthErrorCode;
import com.nyamnyam.coach.global.exception.errorcode.UserErrorCode;
import com.nyamnyam.coach.user.entity.User;
import com.nyamnyam.coach.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String ACTIVE_STATUS = "ACTIVE";
    private static final String LOCAL_PROVIDER = "LOCAL";
    private static final String GOOGLE_PROVIDER = "GOOGLE";
    private static final String ROLE_USER = "ROLE_USER";

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final CharacterGrowthService characterGrowthService;
    private final GoogleOAuthService googleOAuthService;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        User existingUser = userRepository.findByEmail(request.email()).orElse(null);
        if (existingUser != null && ACTIVE_STATUS.equals(existingUser.getStatus())) {
            throw new BusinessException(AuthErrorCode.EMAIL_ALREADY_EXISTS);
        }
        if (existingUser != null) {
            userRepository.releaseInactiveEmail(existingUser.getUserId());
        }

        User user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .provider(LOCAL_PROVIDER)
                .status(ACTIVE_STATUS)
                .onboardingCompleted(false)
                .build();

        userRepository.save(user);
        User savedUser = userRepository.findById(user.getUserId()).orElse(user);
        characterGrowthService.createDefaultCharacterIfMissing(savedUser.getUserId(), savedUser.getNickname());

        return new SignupResponse(
                savedUser.getUserId(),
                savedUser.getEmail(),
                savedUser.getNickname(),
                savedUser.getOnboardingCompleted(),
                savedUser.getCreatedAt()
        );
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(AuthErrorCode.INVALID_CREDENTIALS));

        if (!isLocalUser(user)
                || user.getPasswordHash() == null
                || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessException(AuthErrorCode.INVALID_CREDENTIALS);
        }
        validateActiveUser(user);

        return issueLoginResponse(user);
    }

    @Transactional
    public LoginResponse loginWithGoogle(GoogleOAuthLoginRequest request) {
        GoogleOAuthUserInfo googleUser = googleOAuthService.authenticate(request);
        User user = userRepository.findByProviderAndProviderId(GOOGLE_PROVIDER, googleUser.providerId())
                .orElse(null);

        if (user == null) {
            if (userRepository.findByEmail(googleUser.email()).isPresent()) {
                throw new BusinessException(AuthErrorCode.OAUTH_EMAIL_ALREADY_EXISTS);
            }
            user = createGoogleUser(googleUser);
        }

        validateActiveUser(user);
        return issueLoginResponse(user);
    }

    private User createGoogleUser(GoogleOAuthUserInfo googleUser) {
        String nickname = googleUser.name() == null
                ? googleUser.email().substring(0, googleUser.email().indexOf('@'))
                : googleUser.name();
        if (nickname.length() > 50) {
            nickname = nickname.substring(0, 50);
        }

        User user = User.builder()
                .email(googleUser.email())
                .passwordHash(passwordEncoder.encode(UUID.randomUUID().toString()))
                .nickname(nickname)
                .profileImageUrl(googleUser.picture())
                .provider(GOOGLE_PROVIDER)
                .providerId(googleUser.providerId())
                .status(ACTIVE_STATUS)
                .onboardingCompleted(false)
                .build();

        userRepository.save(user);
        User savedUser = userRepository.findById(user.getUserId()).orElse(user);
        characterGrowthService.createDefaultCharacterIfMissing(savedUser.getUserId(), savedUser.getNickname());
        return savedUser;
    }

    private boolean isLocalUser(User user) {
        return user.getProvider() == null || LOCAL_PROVIDER.equals(user.getProvider());
    }

    private LoginResponse issueLoginResponse(User user) {

        JwtToken jwtToken = issueToken(user);
        saveRefreshToken(user.getUserId(), jwtToken.getRefreshToken());

        return new LoginResponse(
                jwtToken.getAccessToken(),
                jwtToken.getRefreshToken(),
                jwtToken.getGrantType(),
                jwtTokenProvider.getAccessTokenExpiresInSeconds(),
                toAuthUserResponse(user)
        );
    }

    @Transactional
    public TokenRefreshResponse refresh(TokenRefreshRequest request) {
        String refreshToken = request.refreshToken();
        jwtTokenProvider.validateRefreshTokenForRefresh(refreshToken);

        String tokenHash = hashToken(refreshToken);
        Long storedUserId = refreshTokenRepository.findUserIdByTokenHash(tokenHash)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.INVALID_TOKEN));

        Long tokenUserId = extractUserId(refreshToken);
        if (!storedUserId.equals(tokenUserId)) {
            throw new BusinessException(AuthErrorCode.INVALID_TOKEN);
        }

        User user = userRepository.findById(storedUserId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
        validateActiveUser(user);

        JwtToken jwtToken = issueToken(user);
        refreshTokenRepository.revokeByTokenHash(tokenHash);
        saveRefreshToken(user.getUserId(), jwtToken.getRefreshToken());

        return new TokenRefreshResponse(
                jwtToken.getAccessToken(),
                jwtToken.getRefreshToken(),
                jwtToken.getGrantType(),
                jwtTokenProvider.getAccessTokenExpiresInSeconds()
        );
    }

    @Transactional
    public LogoutResponse logout(LogoutRequest request) {
        String refreshToken = request.refreshToken();
        jwtTokenProvider.validateRefreshTokenSignatureAllowExpired(refreshToken);

        String tokenHash = hashToken(refreshToken);
        refreshTokenRepository.findUserIdByTokenHash(tokenHash)
                .ifPresent(userId -> refreshTokenRepository.revokeByTokenHash(tokenHash));

        return new LogoutResponse(LocalDateTime.now());
    }

    private JwtToken issueToken(User user) {
        return jwtTokenProvider.generateToken(authenticationFor(user));
    }

    private void saveRefreshToken(Long userId, String refreshToken) {
        refreshTokenRepository.save(
                userId,
                hashToken(refreshToken),
                jwtTokenProvider.getRefreshTokenExpirationDuration()
        );
    }

    private Authentication authenticationFor(User user) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(ROLE_USER));
        return new UsernamePasswordAuthenticationToken(
                String.valueOf(user.getUserId()),
                "",
                authorities
        );
    }

    private Long extractUserId(String token) {
        try {
            Claims claims = jwtTokenProvider.parseClaims(token);
            return Long.valueOf(claims.getSubject());
        } catch (RuntimeException exception) {
            throw new BusinessException(AuthErrorCode.INVALID_TOKEN);
        }
    }

    private void validateActiveUser(User user) {
        if (!ACTIVE_STATUS.equals(user.getStatus())) {
            throw new BusinessException(AuthErrorCode.USER_INACTIVE);
        }
    }

    private AuthUserResponse toAuthUserResponse(User user) {
        return new AuthUserResponse(
                user.getUserId(),
                user.getEmail(),
                user.getNickname(),
                user.getOnboardingCompleted()
        );
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 algorithm is not available.", exception);
        }
    }
}
