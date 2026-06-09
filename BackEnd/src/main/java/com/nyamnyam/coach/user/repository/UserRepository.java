package com.nyamnyam.coach.user.repository;

import com.nyamnyam.coach.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface UserRepository {

    void save(User user);

    Optional<User> findById(@Param("userId") Long userId);

    Optional<User> findByEmail(@Param("email") String email);

    void reactivate(User user);

    boolean existsByEmail(@Param("email") String email);

    boolean existsByNickname(@Param("nickname") String nickname);

    boolean existsByNicknameExcludingUserId(
            @Param("nickname") String nickname,
            @Param("userId") Long userId
    );

    int updateProfile(
            @Param("userId") Long userId,
            @Param("nickname") String nickname
    );

    int deactivate(@Param("userId") Long userId);
}
