package com.nyamnyam.coach.food.repository.row;

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
public class FrequentFoodRow {

    private Long foodId;
    private String name;
    private Long recordCount;
    private LocalDateTime lastRecordedAt;
}
