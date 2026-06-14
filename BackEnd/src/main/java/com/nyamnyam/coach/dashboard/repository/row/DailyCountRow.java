package com.nyamnyam.coach.dashboard.repository.row;

import java.time.LocalDate;

public class DailyCountRow {

    private LocalDate statDate;
    private Integer countValue;

    public LocalDate getStatDate() { return statDate; }
    public void setStatDate(LocalDate statDate) { this.statDate = statDate; }
    public Integer getCountValue() { return countValue; }
    public void setCountValue(Integer countValue) { this.countValue = countValue; }
}
