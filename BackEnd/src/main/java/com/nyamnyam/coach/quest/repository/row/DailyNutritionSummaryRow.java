package com.nyamnyam.coach.quest.repository.row;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DailyNutritionSummaryRow {

    private Long summaryId;
    private Long userId;
    private LocalDate summaryDate;
    private BigDecimal totalSugarG;

    public Long getSummaryId() { return summaryId; }
    public void setSummaryId(Long summaryId) { this.summaryId = summaryId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public LocalDate getSummaryDate() { return summaryDate; }
    public void setSummaryDate(LocalDate summaryDate) { this.summaryDate = summaryDate; }
    public BigDecimal getTotalSugarG() { return totalSugarG; }
    public void setTotalSugarG(BigDecimal totalSugarG) { this.totalSugarG = totalSugarG; }
}
