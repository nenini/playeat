package com.nyamnyam.coach.character.entity;

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
public class XpHistory {

    private Long xpHistoryId;
    private Long userId;
    private Long characterId;
    private String sourceType;
    private Long sourceId;
    private Integer xpAmount;
    private String reason;
    private LocalDateTime createdAt;
}
