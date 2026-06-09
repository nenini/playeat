package com.nyamnyam.coach.guild.entity;

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
public class GuildMember {

    private Long guildMemberId;
    private Long guildId;
    private Long userId;
    private String role;
    private LocalDateTime joinedAt;
    private LocalDateTime leftAt;
}
