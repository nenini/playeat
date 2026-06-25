package com.nyamnyam.coach.coach.service;

import com.nyamnyam.coach.ai.entity.AiFeedback;
import com.nyamnyam.coach.ai.service.AiFeedbackService;
import com.nyamnyam.coach.ai.service.AiTextGenerator;
import com.nyamnyam.coach.ai.service.parser.AiJsonResponseParser;
import com.nyamnyam.coach.ai.service.prompt.CoachFeedbackPrompt;
import com.nyamnyam.coach.coach.dto.response.CoachFeedbackResponse;
import com.nyamnyam.coach.coach.dto.response.CoachListResponse;
import com.nyamnyam.coach.coach.dto.response.CoachResponse;
import com.nyamnyam.coach.coach.entity.Coach;
import com.nyamnyam.coach.coach.repository.CoachRepository;
import com.nyamnyam.coach.diet.entity.Diet;
import com.nyamnyam.coach.diet.entity.MealType;
import com.nyamnyam.coach.diet.repository.DietRepository;
import com.nyamnyam.coach.diet.repository.row.DietItemRow;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.CoachErrorCode;
import com.nyamnyam.coach.global.exception.errorcode.DietErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CoachService {

    private final CoachRepository coachRepository;
    private final DietRepository dietRepository;
    private final AiFeedbackService aiFeedbackService;
    private final AiTextGenerator aiTextGenerator;
    private final AiJsonResponseParser aiJsonResponseParser;

    public CoachService(
            CoachRepository coachRepository,
            DietRepository dietRepository,
            AiFeedbackService aiFeedbackService,
            AiTextGenerator aiTextGenerator,
            AiJsonResponseParser aiJsonResponseParser
    ) {
        this.coachRepository = coachRepository;
        this.dietRepository = dietRepository;
        this.aiFeedbackService = aiFeedbackService;
        this.aiTextGenerator = aiTextGenerator;
        this.aiJsonResponseParser = aiJsonResponseParser;
    }

    @Transactional(readOnly = true)
    public CoachListResponse getCoaches(Long userId) {
        Long selectedCoachId = coachRepository.findSelectedByUserId(userId)
                .map(Coach::getCoachId)
                .orElse(null);

        return new CoachListResponse(
                coachRepository.findActiveCoaches().stream()
                        .map(coach -> CoachResponse.from(coach, coach.getCoachId().equals(selectedCoachId)))
                        .toList()
        );
    }

    @Transactional
    public CoachResponse selectCoach(Long userId, Long coachId) {
        Coach coach = coachRepository.findById(coachId)
                .filter(item -> Boolean.TRUE.equals(item.getActive()))
                .orElseThrow(() -> new BusinessException(CoachErrorCode.COACH_NOT_FOUND));
        coachRepository.updateSelectedCoach(userId, coachId);
        return CoachResponse.from(coach, true);
    }

    @Transactional
    public CoachFeedbackResponse createDietFeedback(Long userId, Long dietId) {
        Diet diet = dietRepository.findByIdAndUserId(dietId, userId)
                .orElseThrow(() -> new BusinessException(DietErrorCode.DIET_NOT_FOUND));
        Coach coach = resolveCoach(userId);
        return createDietFeedback(userId, diet, coach);
    }

    @Transactional
    public List<CoachFeedbackResponse> createDietFeedbacksForAllCoaches(Long userId, Long dietId) {
        Diet diet = dietRepository.findByIdAndUserId(dietId, userId)
                .orElseThrow(() -> new BusinessException(DietErrorCode.DIET_NOT_FOUND));
        return coachRepository.findActiveCoaches().stream()
                .map(coach -> findDietFeedback(userId, diet.getDietId(), coach)
                        .orElseGet(() -> createDietFeedback(userId, diet, coach)))
                .toList();
    }

    private CoachFeedbackResponse createDietFeedback(Long userId, Diet diet, Coach coach) {
        List<DietItemRow> items = dietRepository.findItemsByDietId(diet.getDietId());
        String rawFeedback = aiTextGenerator.generateCoachFeedback(new CoachFeedbackPrompt(
                coach.getName(),
                coach.getToneDescription(),
                mealTypeLabel(diet.getMealType()),
                mealItemsText(items),
                zeroIfNull(diet.getTotalCalories()),
                zeroIfNull(diet.getTotalProteinG()),
                zeroIfNull(diet.getTotalCarbsG()),
                zeroIfNull(diet.getTotalFatG()),
                zeroIfNull(diet.getTotalSodiumMg()),
                zeroIfNull(diet.getTotalFiberG()),
                cautionText(diet, items)
        ));
        String message = aiJsonResponseParser.parseCoachFeedback(rawFeedback).message();
        AiFeedback feedback = aiFeedbackService.save(userId, diet.getDietId(), coach.getCoachId(), message, aiTextGenerator.modelName());
        return toFeedbackResponse(feedback, coach);
    }

    @Transactional(readOnly = true)
    public CoachFeedbackResponse getDietFeedback(Long userId, Long dietId) {
        dietRepository.findByIdAndUserId(dietId, userId)
                .orElseThrow(() -> new BusinessException(DietErrorCode.DIET_NOT_FOUND));
        Coach coach = resolveCoach(userId);
        AiFeedback feedback = aiFeedbackService.getLatest(userId, dietId, coach.getCoachId());
        return toFeedbackResponse(feedback, coach);
    }

    @Transactional(readOnly = true)
    public Optional<CoachFeedbackResponse> findDietFeedback(Long userId, Long dietId) {
        if (dietRepository.findByIdAndUserId(dietId, userId).isEmpty()) {
            return Optional.empty();
        }
        Coach coach = resolveCoach(userId);
        return findDietFeedback(userId, dietId, coach);
    }

    @Transactional(readOnly = true)
    public List<CoachFeedbackResponse> findDietFeedbacks(Long userId, Long dietId) {
        if (dietRepository.findByIdAndUserId(dietId, userId).isEmpty()) {
            return List.of();
        }
        return coachRepository.findActiveCoaches().stream()
                .map(coach -> findDietFeedback(userId, dietId, coach))
                .flatMap(Optional::stream)
                .toList();
    }

    private Optional<CoachFeedbackResponse> findDietFeedback(Long userId, Long dietId, Coach coach) {
        return aiFeedbackService.findLatest(userId, dietId, coach.getCoachId())
                .map(feedback -> toFeedbackResponse(feedback, coach));
    }

    private Coach resolveCoach(Long userId) {
        return coachRepository.findSelectedByUserId(userId)
                .or(() -> coachRepository.findDefaultCoach())
                .orElseThrow(() -> new BusinessException(CoachErrorCode.COACH_NOT_FOUND));
    }

    private CoachFeedbackResponse toFeedbackResponse(AiFeedback feedback, Coach coach) {
        return new CoachFeedbackResponse(
                feedback.getFeedbackId(),
                feedback.getDietId(),
                feedback.getCoachId(),
                coach.getName(),
                feedback.getMessage(),
                feedback.getModelName(),
                feedback.getCreatedAt()
        );
    }

    private String mealTypeLabel(MealType mealType) {
        if (mealType == null) {
            return "미지정";
        }
        return switch (mealType) {
            case BREAKFAST -> "아침";
            case LUNCH -> "점심";
            case SNACK -> "간식";
            case DINNER -> "저녁";
        };
    }

    private String mealItemsText(List<DietItemRow> items) {
        if (items == null || items.isEmpty()) {
            return "음식 상세 없음";
        }
        return String.join("\n", items.stream()
                .map(item -> "%s %s, 열량 %skcal, 나트륨 %smg, 식이섬유 %sg".formatted(
                        item.getFoodName(),
                        amountText(item),
                        zeroIfNull(item.getCalories()),
                        zeroIfNull(item.getSodiumMg()),
                        zeroIfNull(item.getFiberG())
                ))
                .toList());
    }

    private String amountText(DietItemRow item) {
        if (item.getInputAmount() != null && item.getInputUnit() != null && !item.getInputUnit().isBlank()) {
            return "%s%s".formatted(item.getInputAmount(), item.getInputUnit());
        }
        if (item.getAmountG() != null && item.getAmountG().compareTo(BigDecimal.ZERO) > 0) {
            return "%sg".formatted(item.getAmountG());
        }
        if (item.getAmountMl() != null && item.getAmountMl().compareTo(BigDecimal.ZERO) > 0) {
            return "%sml".formatted(item.getAmountMl());
        }
        return "입력량 없음";
    }

    private String cautionText(Diet diet, List<DietItemRow> items) {
        List<String> cautions = new ArrayList<>();
        if (isAtLeast(diet.getTotalCalories(), "800")) {
            cautions.add("총 열량이 800kcal 이상이라 한 끼 기준 과다 가능성을 확인하세요.");
        }
        if (isAtLeast(diet.getTotalSodiumMg(), "1500")) {
            cautions.add("총 나트륨이 1500mg 이상이라 짠 음식 섭취 가능성을 확인하세요.");
        }
        if (hasLargeAmount(items)) {
            cautions.add("입력량이 800g 또는 800ml 이상인 항목이 있어 많은 양 섭취 가능성을 언급하세요.");
        }
        return cautions.isEmpty() ? "특이 주의 신호 없음" : String.join(" ", cautions);
    }

    private boolean hasLargeAmount(List<DietItemRow> items) {
        if (items == null) {
            return false;
        }
        return items.stream().anyMatch(item ->
                isAtLeast(item.getInputAmount(), "800")
                        || isAtLeast(item.getAmountG(), "800")
                        || isAtLeast(item.getAmountMl(), "800")
        );
    }

    private boolean isAtLeast(BigDecimal value, String threshold) {
        return value != null && value.compareTo(new BigDecimal(threshold)) >= 0;
    }

    private BigDecimal zeroIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
