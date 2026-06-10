package com.nyamnyam.coach.guild.repository.row;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class GuildNoticeRow {

    private Long noticeId;
    private Long guildId;
    private Long writerUserId;
    private String writerNickname;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
