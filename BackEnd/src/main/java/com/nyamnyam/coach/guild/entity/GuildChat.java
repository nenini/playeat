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
public class GuildChat {

    private Long chatId;
    private Long guildId;
    private Long senderUserId;
    private String messageType;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
}
