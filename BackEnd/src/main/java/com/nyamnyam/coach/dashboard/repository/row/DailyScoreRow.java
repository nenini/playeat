package com.nyamnyam.coach.dashboard.repository.row;

import java.time.LocalDate;

public class DailyScoreRow {

    private LocalDate scoreDate;
    private Integer score;

    public LocalDate getScoreDate() { return scoreDate; }
    public void setScoreDate(LocalDate scoreDate) { this.scoreDate = scoreDate; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
}
