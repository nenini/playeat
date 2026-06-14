package com.nyamnyam.coach.ai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nyamnyam.coach.ai.dto.response.AiReportResponse;
import com.nyamnyam.coach.ai.entity.AiReport;
import com.nyamnyam.coach.ai.repository.AiReportRepository;
import com.nyamnyam.coach.ai.service.prompt.DailyReportPrompt;
import com.nyamnyam.coach.ai.service.prompt.WeeklyReportPrompt;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.AiErrorCode;
import com.nyamnyam.coach.nutrition.dto.response.DailyNutritionAnalysisResponse;
import com.nyamnyam.coach.nutrition.service.NutritionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AiReportService {

    private static final String DAILY = "DAILY";
    private static final String WEEKLY = "WEEKLY";

    private final AiReportRepository aiReportRepository;
    private final AiTextGenerator aiTextGenerator;
    private final NutritionService nutritionService;
    private final ObjectMapper objectMapper;

    public AiReportService(
            AiReportRepository aiReportRepository,
            AiTextGenerator aiTextGenerator,
            NutritionService nutritionService,
            ObjectMapper objectMapper
    ) {
        this.aiReportRepository = aiReportRepository;
        this.aiTextGenerator = aiTextGenerator;
        this.nutritionService = nutritionService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public AiReportResponse createDailyReport(Long userId, LocalDate date) {
        Optional<AiReport> existingReport = aiReportRepository.findByUserIdAndPeriod(userId, DAILY, date, date);
        if (existingReport.isPresent()) {
            return toResponse(existingReport.get(), dailyScore(userId, date));
        }

        int healthScore = dailyScore(userId, date);
        AiReport report = new AiReport();
        report.setUserId(userId);
        report.setReportType(DAILY);
        report.setPeriodStart(date);
        report.setPeriodEnd(date);
        report.setSummary(aiTextGenerator.generateDailyReport(new DailyReportPrompt(date, healthScore)));
        report.setStrengthsJson(toJson(List.of("식단을 기록해 분석할 수 있는 상태예요.")));
        report.setWarningsJson(toJson(List.of("부족하거나 과한 영양소를 다음 끼니에서 조정해보세요.")));
        report.setNextAction("다음 끼니에 단백질 또는 채소를 한 가지 추가해보세요.");
        aiReportRepository.insert(report);
        return toResponse(report, healthScore);
    }

    @Transactional(readOnly = true)
    public AiReportResponse getDailyReport(Long userId, LocalDate date) {
        AiReport report = aiReportRepository.findByUserIdAndPeriod(userId, DAILY, date, date)
                .orElseThrow(() -> new BusinessException(AiErrorCode.AI_REPORT_NOT_FOUND));
        return toResponse(report, dailyScore(userId, date));
    }

    @Transactional(readOnly = true)
    public AiReportResponse findDailyReportOrNull(Long userId, LocalDate date) {
        return aiReportRepository.findByUserIdAndPeriod(userId, DAILY, date, date)
                .map(report -> toResponse(report, dailyScore(userId, date)))
                .orElse(null);
    }

    @Transactional
    public AiReportResponse createWeeklyReport(Long userId, LocalDate startDate, LocalDate endDate) {
        validateWeeklyPeriod(startDate, endDate);
        Optional<AiReport> existingReport = aiReportRepository.findByUserIdAndPeriod(userId, WEEKLY, startDate, endDate);
        if (existingReport.isPresent()) {
            return toResponse(existingReport.get(), 75);
        }

        AiReport report = new AiReport();
        report.setUserId(userId);
        report.setReportType(WEEKLY);
        report.setPeriodStart(startDate);
        report.setPeriodEnd(endDate);
        report.setSummary(aiTextGenerator.generateWeeklyReport(new WeeklyReportPrompt(startDate, endDate)));
        report.setStrengthsJson(toJson(List.of("주간 식단 기록 흐름을 확인할 수 있어요.")));
        report.setWarningsJson(toJson(List.of("주간 리포트는 추후 RAG 기반 건강 자료와 함께 보강할 예정이에요.")));
        report.setNextAction("이번 주에 반복적으로 부족했던 영양소를 한 가지 정해 보완해보세요.");
        aiReportRepository.insert(report);
        return toResponse(report, 75);
    }

    @Transactional(readOnly = true)
    public AiReportResponse getWeeklyReport(Long userId, LocalDate startDate, LocalDate endDate) {
        validateWeeklyPeriod(startDate, endDate);
        AiReport report = aiReportRepository.findByUserIdAndPeriod(userId, WEEKLY, startDate, endDate)
                .orElseThrow(() -> new BusinessException(AiErrorCode.AI_REPORT_NOT_FOUND));
        return toResponse(report, 75);
    }

    private int dailyScore(Long userId, LocalDate date) {
        DailyNutritionAnalysisResponse analysis = nutritionService.getDailyAnalysis(userId, date);
        return analysis.healthScore();
    }

    private void validateWeeklyPeriod(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new BusinessException(AiErrorCode.AI_RESPONSE_FAILED);
        }
    }

    private AiReportResponse toResponse(AiReport report, int healthScore) {
        return new AiReportResponse(
                report.getReportId(),
                report.getReportType(),
                report.getPeriodStart(),
                report.getPeriodEnd(),
                healthScore,
                report.getSummary(),
                fromJson(report.getStrengthsJson()),
                fromJson(report.getWarningsJson()),
                report.getNextAction(),
                report.getCreatedAt()
        );
    }

    private String toJson(List<String> values) {
        try {
            return objectMapper.writeValueAsString(values);
        } catch (JsonProcessingException e) {
            throw new BusinessException(AiErrorCode.AI_RESPONSE_FAILED);
        }
    }

    private List<String> fromJson(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }
}
