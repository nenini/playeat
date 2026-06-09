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
public class Guild {

    private Long guildId;
    private String name;
    private String description;
    private String inviteCode;
    private Long ownerUserId;
    private Integer maxMembers;
    private Integer guildPoint;
    private String visibility;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
