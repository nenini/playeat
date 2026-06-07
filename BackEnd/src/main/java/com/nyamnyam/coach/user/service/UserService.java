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
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final String ACTIVE_STATUS = "ACTIVE";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserMeResponse getMe(Long userId) {
        User user = findActiveUser(userId);
        return toUserMeResponse(user);
    }

    @Transactional
    public UpdateUserResponse updateMe(Long userId, UpdateUserRequest request) {
        User user = findActiveUser(userId);

        if (!user.getNickname().equals(request.nickname())
                && userRepository.existsByNickname(request.nickname())) {
            throw new BusinessException(AuthErrorCode.NICKNAME_ALREADY_EXISTS);
        }

        userRepository.updateProfile(userId, request.nickname());
        User updatedUser = findActiveUser(userId);

        return new UpdateUserResponse(
                updatedUser.getUserId(),
                updatedUser.getNickname(),
                updatedUser.getUpdatedAt()
        );
    }

    @Transactional
    public DeactivateUserResponse deactivateMe(Long userId, DeactivateUserRequest request) {
        User user = findActiveUser(userId);

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        userRepository.deactivate(userId);
        User deactivatedUser = findUser(userId);

        return new DeactivateUserResponse(
                deactivatedUser.getUserId(),
                deactivatedUser.getStatus(),
                deactivatedUser.getDeactivatedAt()
        );
    }

    private User findActiveUser(Long userId) {
        User user = findUser(userId);
        if (!ACTIVE_STATUS.equals(user.getStatus())) {
            throw new BusinessException(AuthErrorCode.USER_INACTIVE);
        }
        return user;
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }

    private UserMeResponse toUserMeResponse(User user) {
        return new UserMeResponse(
                user.getUserId(),
                user.getEmail(),
                user.getNickname(),
                user.getStatus(),
                user.getOnboardingCompleted(),
                user.getCreatedAt()
        );
    }
}
