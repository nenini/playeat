package com.nyamnyam.coach.ai.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AiFeedback {

    private Long feedbackId;
    private Long userId;
    private Long dietId;
    private Long coachId;
    private String message;
    private String modelName;
    private LocalDateTime createdAt;
}
