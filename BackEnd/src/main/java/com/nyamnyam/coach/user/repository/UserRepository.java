package com.nyamnyam.coach.user.repository;

import com.nyamnyam.coach.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserRepository {

    void save(User user);

    Optional<User> findById(@Param("userId") Long userId);

    Optional<User> findByEmail(@Param("email") String email);

    List<Long> findActiveUserIds();

    int releaseInactiveEmail(@Param("userId") Long userId);

    boolean existsByEmail(@Param("email") String email);

    int updateProfile(
            @Param("userId") Long userId,
            @Param("nickname") String nickname
    );

    int updatePassword(
            @Param("userId") Long userId,
            @Param("passwordHash") String passwordHash
    );

    int updateProfileImage(
            @Param("userId") Long userId,
            @Param("profileImageUrl") String profileImageUrl
    );

    int deleteProfileImage(@Param("userId") Long userId);

    int completeOnboarding(
            @Param("userId") Long userId,
            @Param("selectedCoachId") Long selectedCoachId
    );

    int deactivate(@Param("userId") Long userId);
}
