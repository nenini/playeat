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
public class GuildJoinRequest {

    private Long requestId;
    private Long guildId;
    private Long userId;
    private String status;
    private Long handledBy;
    private LocalDateTime createdAt;
    private LocalDateTime handledAt;
}
