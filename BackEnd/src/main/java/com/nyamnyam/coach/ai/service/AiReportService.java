package com.nyamnyam.coach.ai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nyamnyam.coach.ai.dto.response.AiReportResponse;
import com.nyamnyam.coach.ai.entity.AiReport;
import com.nyamnyam.coach.ai.rag.document.RagReference;
import com.nyamnyam.coach.ai.rag.service.HealthGuideRetrievalService;
import com.nyamnyam.coach.ai.repository.AiReportRepository;
import com.nyamnyam.coach.ai.service.parser.AiJsonResponseParser;
import com.nyamnyam.coach.ai.service.parser.DailyReportContent;
import com.nyamnyam.coach.ai.service.parser.WeeklyReportContent;
import com.nyamnyam.coach.ai.service.prompt.DailyReportPrompt;
import com.nyamnyam.coach.ai.service.prompt.WeeklyReportPrompt;
import com.nyamnyam.coach.character.entity.XpSourceType;
import com.nyamnyam.coach.character.service.CharacterGrowthService;
import com.nyamnyam.coach.character.service.CharacterMoodService;
import com.nyamnyam.coach.diet.dto.response.DietDayResponse;
import com.nyamnyam.coach.diet.dto.response.DietItemResponse;
import com.nyamnyam.coach.diet.dto.response.DietMealResponse;
import com.nyamnyam.coach.diet.service.DietService;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.AiErrorCode;
import com.nyamnyam.coach.nutrition.dto.response.DailyNutritionAnalysisResponse;
import com.nyamnyam.coach.nutrition.service.NutritionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
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
    private final WeeklyReportContextService weeklyReportContextService;
    private final HealthGuideRetrievalService healthGuideRetrievalService;
    private final CharacterGrowthService characterGrowthService;
    private final CharacterMoodService characterMoodService;

    public AiReportService(
            AiReportRepository aiReportRepository,
            AiTextGenerator aiTextGenerator,
            NutritionService nutritionService,
            DietService dietService,
            ObjectMapper objectMapper,
            AiJsonResponseParser aiJsonResponseParser,
            WeeklyReportContextService weeklyReportContextService,
            HealthGuideRetrievalService healthGuideRetrievalService,
            CharacterGrowthService characterGrowthService,
            CharacterMoodService characterMoodService
    ) {
        this.aiReportRepository = aiReportRepository;
        this.aiTextGenerator = aiTextGenerator;
        this.nutritionService = nutritionService;
        this.dietService = dietService;
        this.objectMapper = objectMapper;
        this.aiJsonResponseParser = aiJsonResponseParser;
        this.weeklyReportContextService = weeklyReportContextService;
        this.healthGuideRetrievalService = healthGuideRetrievalService;
        this.characterGrowthService = characterGrowthService;
        this.characterMoodService = characterMoodService;
    }

    @Transactional
    public AiReportResponse createDailyReport(Long userId, LocalDate date) {
        Optional<AiReport> existingReport = aiReportRepository.findByUserIdAndPeriod(userId, DAILY, date, date);
        if (existingReport.isPresent()) {
            return toResponse(existingReport.get());
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
        report.setHealthScore(healthScore);
        report.setSummary(nullToDefault(content.summary(), "일일 리포트를 생성했습니다."));
        report.setStrengthsJson(toJson(emptyIfNull(content.strengths())));
        report.setWarningsJson(toJson(emptyIfNull(content.warnings())));
        report.setNextAction(nullToDefault(content.nextAction(), "다음 끼니 선택을 점검해보세요."));
        aiReportRepository.insert(report);
        updateCharacterMoodFromDailyReport(userId, date, report);
        if (healthScore > 0) {
            characterGrowthService.addXp(
                    userId,
                    XpSourceType.ANALYSIS_DAILY_REPORT,
                    report.getReportId(),
                    healthScore,
                    "%s 일간 건강 점수 리포트".formatted(date)
            );
        }
        return toResponse(report);
    }

    private void updateCharacterMoodFromDailyReport(Long userId, LocalDate date, AiReport report) {
        try {
            characterMoodService.updateMoodFromDailyReport(
                    userId,
                    date,
                    report.getHealthScore(),
                    report.getSummary(),
                    fromJson(report.getStrengthsJson()),
                    fromJson(report.getWarningsJson()),
                    report.getNextAction()
            );
        } catch (Exception exception) {
            log.warn("Failed to update character mood after daily report. userId={} date={}", userId, date, exception);
        }
    }

    @Transactional(readOnly = true)
    public AiReportResponse getDailyReport(Long userId, LocalDate date) {
        AiReport report = aiReportRepository.findByUserIdAndPeriod(userId, DAILY, date, date)
                .orElseThrow(() -> new BusinessException(AiErrorCode.AI_REPORT_NOT_FOUND));
        return toResponse(report);
    }

    @Transactional(readOnly = true)
    public AiReportResponse findDailyReportOrNull(Long userId, LocalDate date) {
        return aiReportRepository.findByUserIdAndPeriod(userId, DAILY, date, date)
                .map(this::toResponse)
                .orElse(null);
    }

    public AiReportResponse createWeeklyReport(Long userId, LocalDate startDate, LocalDate endDate) {
        validateWeeklyPeriod(startDate, endDate);
        Optional<AiReport> existingReport = aiReportRepository.findByUserIdAndPeriod(userId, WEEKLY, startDate, endDate);
        if (existingReport.isPresent()) {
            return toResponse(existingReport.get());
        }

        WeeklyReportContext context = weeklyReportContextService.collect(userId, startDate, endDate);
        List<RagReference> ragReferences = healthGuideRetrievalService.retrieve(context.retrievalQuery());
        WeeklyReportContent content = aiJsonResponseParser.parseWeeklyReport(
                aiTextGenerator.generateWeeklyReport(new WeeklyReportPrompt(
                        startDate,
                        endDate,
                        context.averageHealthScore(),
                        context.healthProfileSummary(),
                        context.dailyMealSummaries(),
                        context.dailyNutritionSummaries(),
                        context.repeatedPatterns(),
                        ragReferences
                ))
        );

        AiReport report = new AiReport();
        report.setUserId(userId);
        report.setReportType(WEEKLY);
        report.setPeriodStart(startDate);
        report.setPeriodEnd(endDate);
        report.setHealthScore(context.averageHealthScore());
        report.setSummary(nullToDefault(content.summary(), "주간 리포트를 생성했습니다."));
        report.setStrengthsJson(toJson(emptyIfNull(content.strengths())));
        report.setWarningsJson(toJson(emptyIfNull(content.warnings())));
        report.setNextAction(nullToDefault(content.nextAction(), "다음 주 식단 목표를 한 가지 정해 실천해보세요."));
        aiReportRepository.insert(report);
        return toResponse(report);
    }

    @Transactional(readOnly = true)
    public AiReportResponse getWeeklyReport(Long userId, LocalDate startDate, LocalDate endDate) {
        validateWeeklyPeriod(startDate, endDate);
        AiReport report = aiReportRepository.findByUserIdAndPeriod(userId, WEEKLY, startDate, endDate)
                .orElseThrow(() -> new BusinessException(AiErrorCode.AI_REPORT_NOT_FOUND));
        return toResponse(report);
    }

    @Transactional(readOnly = true)
    public AiReportResponse findWeeklyReportOrNull(Long userId, LocalDate startDate, LocalDate endDate) {
        validateWeeklyPeriod(startDate, endDate);
        return aiReportRepository.findByUserIdAndPeriod(userId, WEEKLY, startDate, endDate)
                .map(this::toResponse)
                .orElse(null);
    }

    private List<String> dailyMealSummaries(Long userId, LocalDate date) {
        DietDayResponse dietDay = dietService.getDietsByDate(userId, date);
        return dietDay.meals().stream()
                .filter(DietMealResponse::recorded)
                .map(this::toMealSummary)
                .toList();
    }

    private String toMealSummary(DietMealResponse meal) {
        List<DietItemResponse> mealItems = meal.items() == null ? List.of() : meal.items();
        String items = mealItems.stream()
                .filter(java.util.Objects::nonNull)
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
        if (startDate == null || endDate == null
                || startDate.getDayOfWeek() != DayOfWeek.MONDAY
                || !endDate.equals(startDate.plusDays(6))) {
            throw new BusinessException(AiErrorCode.AI_REPORT_PERIOD_INVALID);
        }
    }

    private AiReportResponse toResponse(AiReport report) {
        return new AiReportResponse(
                report.getReportId(),
                report.getReportType(),
                report.getPeriodStart(),
                report.getPeriodEnd(),
                report.getHealthScore(),
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
