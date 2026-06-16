package com.nyamnyam.coach.ai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nyamnyam.coach.ai.dto.response.AiReportResponse;
import com.nyamnyam.coach.ai.entity.AiReport;
import com.nyamnyam.coach.ai.repository.AiReportRepository;
import com.nyamnyam.coach.ai.service.parser.AiJsonResponseParser;
import com.nyamnyam.coach.ai.service.parser.DailyReportContent;
import com.nyamnyam.coach.ai.service.prompt.DailyReportPrompt;
import com.nyamnyam.coach.diet.dto.response.DietDayResponse;
import com.nyamnyam.coach.diet.dto.response.DietItemResponse;
import com.nyamnyam.coach.diet.dto.response.DietMealResponse;
import com.nyamnyam.coach.diet.service.DietService;
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
    private final DietService dietService;
    private final ObjectMapper objectMapper;
    private final AiJsonResponseParser aiJsonResponseParser;

    public AiReportService(
            AiReportRepository aiReportRepository,
            AiTextGenerator aiTextGenerator,
            NutritionService nutritionService,
            DietService dietService,
            ObjectMapper objectMapper,
            AiJsonResponseParser aiJsonResponseParser
    ) {
        this.aiReportRepository = aiReportRepository;
        this.aiTextGenerator = aiTextGenerator;
        this.nutritionService = nutritionService;
        this.dietService = dietService;
        this.objectMapper = objectMapper;
        this.aiJsonResponseParser = aiJsonResponseParser;
    }

    @Transactional
    public AiReportResponse createDailyReport(Long userId, LocalDate date) {
        Optional<AiReport> existingReport = aiReportRepository.findByUserIdAndPeriod(userId, DAILY, date, date);
        if (existingReport.isPresent()) {
            return toResponse(existingReport.get(), dailyScore(userId, date));
        }

        DailyNutritionAnalysisResponse analysis = nutritionService.getDailyAnalysis(userId, date);
        int healthScore = analysis.healthScore();
        List<String> mealSummaries = dailyMealSummaries(userId, date);
        DailyReportContent content = aiJsonResponseParser.parseDailyReport(
                aiTextGenerator.generateDailyReport(new DailyReportPrompt(date, healthScore, analysis.nutrients(), mealSummaries))
        );

        AiReport report = new AiReport();
        report.setUserId(userId);
        report.setReportType(DAILY);
        report.setPeriodStart(date);
        report.setPeriodEnd(date);
        report.setSummary(nullToDefault(content.summary(), "일일 리포트를 생성했습니다."));
        report.setStrengthsJson(toJson(emptyIfNull(content.strengths())));
        report.setWarningsJson(toJson(emptyIfNull(content.warnings())));
        report.setNextAction(nullToDefault(content.nextAction(), "다음 끼니 선택을 점검해보세요."));
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
        report.setSummary("주간 리포트 생성은 현재 AI 구현 범위에 포함되지 않습니다.");
        report.setStrengthsJson(toJson(List.of("주간 식단 기록 흐름을 확인할 수 있어요.")));
        report.setWarningsJson(toJson(List.of("주간 RAG 기반 참고 자료는 이후 구현 범위에서 추가할 예정입니다.")));
        report.setNextAction("이번 주에 반복적으로 부족했던 영양소를 한 가지 정해 다음 주에 보완해보세요.");
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

    private List<String> dailyMealSummaries(Long userId, LocalDate date) {
        DietDayResponse dietDay = dietService.getDietsByDate(userId, date);
        return dietDay.meals().stream()
                .filter(DietMealResponse::recorded)
                .map(this::toMealSummary)
                .toList();
    }

    private String toMealSummary(DietMealResponse meal) {
        String items = meal.items().stream()
                .map(this::toItemSummary)
                .reduce((left, right) -> left + ", " + right)
                .orElse("기록된 음식 없음");
        return "%s: %s".formatted(meal.label(), items);
    }

    private String toItemSummary(DietItemResponse item) {
        return "%s %s%s".formatted(
                item.foodName(),
                item.inputAmount(),
                item.inputUnit()
        );
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

    private List<String> emptyIfNull(List<String> values) {
        return values == null ? List.of() : values;
    }

    private String nullToDefault(String value, String defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return value;
    }
}
