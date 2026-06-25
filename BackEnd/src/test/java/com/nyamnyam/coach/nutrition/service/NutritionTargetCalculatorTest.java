package com.nyamnyam.coach.nutrition.service;

import com.nyamnyam.coach.nutrition.repository.NutritionReferenceRepository;
import com.nyamnyam.coach.nutrition.repository.row.NutritionReferenceStandardRow;
import com.nyamnyam.coach.user.dto.request.HealthProfileRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NutritionTargetCalculatorTest {

    private final NutritionReferenceRepository nutritionReferenceRepository = mock(NutritionReferenceRepository.class);
    private final NutritionTargetCalculator calculator = new NutritionTargetCalculator(nutritionReferenceRepository);

    @Test
    void calculatesTargetsWithKdriEerAndRepresentativeEnergyRatios() {
        NutritionReferenceStandardRow standard = new NutritionReferenceStandardRow();
        standard.setStandardVersion("KDRI_2020");
        standard.setSodiumMg(new BigDecimal("2000"));
        standard.setFiberG(new BigDecimal("25"));
        when(nutritionReferenceRepository.findStandard("FEMALE", 25)).thenReturn(Optional.of(standard));

        NutritionTargetValues targets = calculator.calculate(new HealthProfileRequest(
                new BigDecimal("162"),
                new BigDecimal("54"),
                new BigDecimal("50"),
                LocalDate.now().minusYears(25),
                "FEMALE",
                "LOSE_WEIGHT",
                "LIGHT",
                List.of("BALANCED"),
                List.of(),
                List.of()
        ));

        assertThat(targets.calories()).isEqualByComparingTo("1755.00");
        assertThat(targets.proteinG()).isEqualByComparingTo("66.00");
        assertThat(targets.carbsG()).isEqualByComparingTo("263.00");
        assertThat(targets.fatG()).isEqualByComparingTo("49.00");
        assertThat(targets.sodiumMg()).isEqualByComparingTo("2000.00");
        assertThat(targets.fiberG()).isEqualByComparingTo("25.00");
        assertThat(targets.standardVersion()).isEqualTo("KDRI_2020");
    }

    @Test
    void usesKdriLabeledFallbacksWhenReferenceStandardIsMissing() {
        when(nutritionReferenceRepository.findStandard("MALE", 30)).thenReturn(Optional.empty());

        NutritionTargetValues targets = calculator.calculate(new HealthProfileRequest(
                null,
                new BigDecimal("70"),
                null,
                LocalDate.now().minusYears(30),
                "MALE",
                "MAINTAIN",
                "MODERATE",
                List.of(),
                List.of(),
                List.of()
        ));

        assertThat(targets.calories()).isEqualByComparingTo("2000.00");
        assertThat(targets.proteinG()).isEqualByComparingTo("75.00");
        assertThat(targets.carbsG()).isEqualByComparingTo("300.00");
        assertThat(targets.fatG()).isEqualByComparingTo("56.00");
        assertThat(targets.sodiumMg()).isEqualByComparingTo("2000.00");
        assertThat(targets.fiberG()).isEqualByComparingTo("25.00");
        assertThat(targets.standardVersion()).isEqualTo("KDRI_2020");
    }
}
