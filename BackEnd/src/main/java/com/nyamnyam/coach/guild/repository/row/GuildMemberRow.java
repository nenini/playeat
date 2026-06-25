package com.nyamnyam.coach.guild.repository.row;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class GuildMemberRow {

    private Long memberId;
    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private Long characterId;
    private String characterName;
    private Integer characterLevel;
    private String characterStage;
    private String characterMood;
    private String characterAppearanceType;
    private Integer streakDays;
    private String role;
    private LocalDateTime joinedAt;
    private LocalDateTime leftAt;
}
