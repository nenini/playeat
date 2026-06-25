package com.nyamnyam.coach.diet.entity;

public enum MealType {

    BREAKFAST("아침", "06-10시"),
    LUNCH("점심", "11-14시"),
    SNACK("간식", "14-17시"),
    DINNER("저녁", "17-22시");

    private final String label;
    private final String timeRange;

    MealType(String label, String timeRange) {
        this.label = label;
        this.timeRange = timeRange;
    }

    public String getLabel() {
        return label;
    }

    public String getTimeRange() {
        return timeRange;
    }
}
