package com.nyamnyam.coach.coach.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Coach {

    private Long coachId;
    private String name;
    private String role;
    private String toneDescription;
    private String sampleMessage;
    private Boolean active;
    private LocalDateTime createdAt;
}
