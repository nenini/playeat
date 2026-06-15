package com.nyamnyam.coach.quest.repository.row;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DietVerificationRow {

    private Long dietId;
    private Long userId;
    private LocalDate summaryDate;
    private BigDecimal totalSugarG;

    public Long getDietId() {
        return dietId;
    }

    public void setDietId(Long dietId) {
        this.dietId = dietId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getSummaryDate() {
        return summaryDate;
    }

    public void setSummaryDate(LocalDate summaryDate) {
        this.summaryDate = summaryDate;
    }

    public BigDecimal getTotalSugarG() {
        return totalSugarG;
    }

    public void setTotalSugarG(BigDecimal totalSugarG) {
        this.totalSugarG = totalSugarG;
    }
}
