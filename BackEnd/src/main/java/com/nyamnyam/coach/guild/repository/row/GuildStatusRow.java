package com.nyamnyam.coach.guild.repository.row;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class GuildStatusRow {

    private Long guildId;
    private String name;
    private String inviteCode;
    private String role;
    private Long requestId;
    private String requestStatus;
    private LocalDateTime requestCreatedAt;
}
