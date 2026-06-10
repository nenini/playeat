package com.nyamnyam.coach.guild.repository.row;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class JoinRequestRow {

    private Long requestId;
    private Long guildId;
    private String guildName;
    private String inviteCode;
    private String guildDescription;
    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private Long characterId;
    private String characterName;
    private Integer characterLevel;
    private String status;
    private Long handledBy;
    private String handledByNickname;
    private LocalDateTime createdAt;
    private LocalDateTime handledAt;
}
