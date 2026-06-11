package com.nyamnyam.coach.character.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterEntity {

    private Long characterId;
    private Long userId;
    private String name;
    private Integer level;
    private Integer xp;
    private String stage;
    private String mood;
    private String appearanceType;
    private Integer streakDays;
    private Integer bestStreakDays;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
