package com.nyamnyam.coach.character.repository;

import com.nyamnyam.coach.character.entity.CharacterEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Mapper
public interface CharacterRepository {

    Optional<CharacterEntity> findByUserId(@Param("userId") Long userId);

    Optional<CharacterEntity> findByIdAndUserId(
            @Param("characterId") Long characterId,
            @Param("userId") Long userId
    );

    void save(CharacterEntity character);

    int updateName(
            @Param("userId") Long userId,
            @Param("name") String name
    );

    int updateGrowth(
            @Param("characterId") Long characterId,
            @Param("level") int level,
            @Param("xp") int xp,
            @Param("stage") String stage,
            @Param("mood") String mood,
            @Param("appearanceType") String appearanceType,
            @Param("streakDays") int streakDays,
            @Param("bestStreakDays") int bestStreakDays
    );

    boolean existsByUserId(@Param("userId") Long userId);

    List<LocalDate> findRecordedDatesByUserId(@Param("userId") Long userId);

    int updateStreak(
            @Param("userId") Long userId,
            @Param("streakDays") int streakDays
    );
}
