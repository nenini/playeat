package com.nyamnyam.coach.ai.service;

import com.nyamnyam.coach.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Component
public class AiReportScheduler {

    private static final Logger log = LoggerFactory.getLogger(AiReportScheduler.class);

    private final AiReportService aiReportService;
    private final UserRepository userRepository;

    public AiReportScheduler(AiReportService aiReportService, UserRepository userRepository) {
        this.aiReportService = aiReportService;
        this.userRepository = userRepository;
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void createDailyReportsForYesterday() {
        LocalDate reportDate = LocalDate.now().minusDays(1);
        for (Long userId : userRepository.findActiveUserIds()) {
            try {
                aiReportService.createDailyReport(userId, reportDate);
            } catch (Exception error) {
                log.warn("Daily AI report scheduled creation failed. userId={}, date={}", userId, reportDate, error);
            }
        }
    }

    @Scheduled(cron = "0 5 0 * * MON", zone = "Asia/Seoul")
    public void createWeeklyReportsForPreviousWeek() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusWeeks(1).with(DayOfWeek.MONDAY);
        LocalDate endDate = startDate.plusDays(6);
        for (Long userId : userRepository.findActiveUserIds()) {
            try {
                aiReportService.createWeeklyReport(userId, startDate, endDate);
            } catch (Exception error) {
                log.warn("Weekly AI report scheduled creation failed. userId={}, startDate={}, endDate={}", userId, startDate, endDate, error);
            }
        }
    }
}
