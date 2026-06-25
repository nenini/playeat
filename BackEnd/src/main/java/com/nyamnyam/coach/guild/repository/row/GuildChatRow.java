package com.nyamnyam.coach.guild.repository.row;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GuildChatRow {

    private Long chatId;
    private Long guildId;
    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private Long characterId;
    private String characterName;
    private Integer characterLevel;
    private String messageType;
    private String message;
    private LocalDateTime createdAt;
    private Boolean isMe;
}
