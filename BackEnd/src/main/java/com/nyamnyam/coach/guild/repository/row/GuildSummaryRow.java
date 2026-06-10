package com.nyamnyam.coach.guild.repository.row;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GuildSummaryRow {

    private Long guildId;
    private String name;
    private String description;
    private String inviteCode;
    private Integer memberCount;
    private Integer maxMembers;
    private Integer guildPoint;
    private String ownerNickname;
    private Long joinedGuildId;
    private Long pendingRequestId;
}
