package com.nyamnyam.coach.guild.repository.row;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class GuildDetailRow {

    private Long guildId;
    private String name;
    private String description;
    private String inviteCode;
    private Long ownerUserId;
    private String ownerNickname;
    private Integer memberCount;
    private Integer maxMembers;
    private Integer guildPoint;
    private String visibility;
    private String status;
    private String myRole;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
