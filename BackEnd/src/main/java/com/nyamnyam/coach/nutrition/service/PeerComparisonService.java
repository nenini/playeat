package com.nyamnyam.coach.nutrition.service;

import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.UserErrorCode;
import com.nyamnyam.coach.nutrition.dto.response.PeerComparisonListResponse;
import com.nyamnyam.coach.nutrition.dto.response.PeerComparisonResponse;
import com.nyamnyam.coach.nutrition.repository.NutritionRepository;
import com.nyamnyam.coach.nutrition.repository.PeerNutritionStatRepository;
import com.nyamnyam.coach.nutrition.repository.row.DailyNutritionAggregateRow;
import com.nyamnyam.coach.nutrition.repository.row.PeerNutritionStatRow;
import com.nyamnyam.coach.user.entity.HealthProfile;
import com.nyamnyam.coach.user.repository.HealthProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

@Service
public class PeerComparisonService {

    private static final String FALLBACK_SOURCE = "2022 국민건강영양조사 (질병관리청)";
    private static final BigDecimal LOW_THRESHOLD  = BigDecimal.valueOf(0.85);
    private static final BigDecimal HIGH_THRESHOLD = BigDecimal.valueOf(1.15);

    private final HealthProfileRepository healthProfileRepository;
    private final NutritionRepository nutritionRepository;
    private final PeerNutritionStatRepository peerNutritionStatRepository;

    public PeerComparisonService(
            HealthProfileRepository healthProfileRepository,
            NutritionRepository nutritionRepository,
            PeerNutritionStatRepository peerNutritionStatRepository
    ) {
        this.healthProfileRepository = healthProfileRepository;
        this.nutritionRepository = nutritionRepository;
        this.peerNutritionStatRepository = peerNutritionStatRepository;
    }

    @Transactional(readOnly = true)
    public PeerComparisonListResponse getPeerComparison(Long userId, LocalDate date) {
        HealthProfile profile = healthProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.HEALTH_PROFILE_NOT_FOUND));

        int age = age(profile.getBirthDate());
        String gender = normalizeGender(profile.getGender());

        PeerNutritionStatRow stat = peerNutritionStatRepository.findStat(gender, age).orElse(null);

        DailyNutritionAggregateRow daily = nutritionRepository.findDailyAggregate(
                userId, startOf(date), endOf(date));
        if (daily == null) {
            daily = new DailyNutritionAggregateRow();
        }

        if (stat == null) {
            return new PeerComparisonListResponse(FALLBACK_SOURCE, List.of());
        }

        List<PeerComparisonResponse> items = List.of(
                compare("protein", "단백질", "g",  daily.getTotalProteinG(), stat.getAvgProteinG(), false),
                compare("sodium",  "나트륨",  "mg", daily.getTotalSodiumMg(), stat.getAvgSodiumMg(), true),
                compare("fiber",   "식이섬유", "g",  daily.getTotalFiberG(),   stat.getAvgFiberG(),   false)
        );

        return new PeerComparisonListResponse(stat.getSourceName(), items);
    }

    private PeerComparisonResponse compare(
            String code,
            String label,
            String unit,
            BigDecimal userRaw,
            BigDecimal peerAvg,
            boolean lowerIsBetter
    ) {
        BigDecimal userValue = userRaw == null ? BigDecimal.ZERO : userRaw.setScale(2, RoundingMode.HALF_UP);
        BigDecimal peer      = peerAvg == null ? BigDecimal.ZERO : peerAvg.setScale(2, RoundingMode.HALF_UP);

        String status  = status(userValue, peer, lowerIsBetter);
        String message = message(label, unit, userValue, peer, status, lowerIsBetter);

        return new PeerComparisonResponse(code, label, unit, userValue, peer, status, message);
    }

    private String status(BigDecimal user, BigDecimal peer, boolean lowerIsBetter) {
        if (peer.compareTo(BigDecimal.ZERO) == 0) {
            return "OK";
        }
        if (lowerIsBetter) {
            return user.compareTo(peer.multiply(HIGH_THRESHOLD)) > 0 ? "HIGH" : "OK";
        }
        if (user.compareTo(peer.multiply(LOW_THRESHOLD)) < 0) {
            return "LOW";
        }
        if (user.compareTo(peer.multiply(HIGH_THRESHOLD)) > 0) {
            return "HIGH";
        }
        return "OK";
    }

    private String message(String label, String unit, BigDecimal user, BigDecimal peer, String status, boolean lowerIsBetter) {
        BigDecimal diff = user.subtract(peer).abs().setScale(0, RoundingMode.HALF_UP);
        return switch (status) {
            case "LOW"  -> label + " 섭취가 또래 평균보다 " + diff + unit + " 부족해요";
            case "HIGH" -> lowerIsBetter
                    ? label + " 섭취가 또래 평균보다 " + diff + unit + " 많아요"
                    : label + " 섭취가 또래 평균보다 " + diff + unit + " 많아요";
            default     -> "또래 평균과 비슷한 수준이에요";
        };
    }

    private int age(LocalDate birthDate) {
        if (birthDate == null) {
            return 30;
        }
        return Math.max(1, Period.between(birthDate, LocalDate.now()).getYears());
    }

    private String normalizeGender(String gender) {
        if (gender == null || gender.isBlank()) {
            return "ALL";
        }
        String normalized = gender.toUpperCase();
        return "MALE".equals(normalized) || "FEMALE".equals(normalized) ? normalized : "ALL";
    }

    private LocalDateTime startOf(LocalDate date) {
        return date.atStartOfDay();
    }

    private LocalDateTime endOf(LocalDate date) {
        return date.plusDays(1).atStartOfDay();
    }
}
