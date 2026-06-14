package com.nyamnyam.coach.ai.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class AiReport {

    private Long reportId;
    private Long userId;
    private String reportType;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private String summary;
    private String strengthsJson;
    private String warningsJson;
    private String nextAction;
    private LocalDateTime createdAt;
}
