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

    boolean existsByEmail(@Param("email") String email);

    boolean existsByNickname(@Param("nickname") String nickname);

    int updateProfile(
            @Param("userId") Long userId,
            @Param("nickname") String nickname
    );

    int deactivate(@Param("userId") Long userId);
}
