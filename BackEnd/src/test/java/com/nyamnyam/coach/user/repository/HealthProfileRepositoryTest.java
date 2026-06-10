package com.nyamnyam.coach.user.repository;

import com.nyamnyam.coach.user.entity.HealthProfile;
import com.nyamnyam.coach.user.entity.User;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@ActiveProfiles("test")
@Sql(scripts = "/test-auth-schema.sql")
class HealthProfileRepositoryTest {

    private final UserRepository userRepository;
    private final HealthProfileRepository healthProfileRepository;

    @Autowired
    HealthProfileRepositoryTest(
            UserRepository userRepository,
            HealthProfileRepository healthProfileRepository
    ) {
        this.userRepository = userRepository;
        this.healthProfileRepository = healthProfileRepository;
    }

    @Test
    void saveAndFindByUserId() {
        User user = saveUser("health@example.com");
        HealthProfile healthProfile = healthProfile(user.getUserId(), "LOSE_WEIGHT");

        healthProfileRepository.save(healthProfile);

        HealthProfile foundProfile = healthProfileRepository.findByUserId(user.getUserId()).orElseThrow();
        assertThat(healthProfile.getHealthProfileId()).isNotNull();
        assertThat(foundProfile.getHealthProfileId()).isEqualTo(healthProfile.getHealthProfileId());
        assertThat(foundProfile.getHealthGoal()).isEqualTo("LOSE_WEIGHT");
        assertThat(foundProfile.getDietStylesJson()).contains("BALANCED");
        assertThat(foundProfile.getRestrictedFoodsJson()).contains("CAFFEINE");
        assertThat(foundProfile.getAllergiesJson()).contains("PEANUT");
        assertThat(healthProfileRepository.existsByUserId(user.getUserId())).isTrue();
    }

    @Test
    void updateByUserId() {
        User user = saveUser("update-health@example.com");
        healthProfileRepository.save(healthProfile(user.getUserId(), "LOSE_WEIGHT"));
        HealthProfile update = healthProfile(user.getUserId(), "MAINTAIN");
        update.setWeightKg(new BigDecimal("55"));

        int updatedCount = healthProfileRepository.updateByUserId(update);

        HealthProfile updatedProfile = healthProfileRepository.findByUserId(user.getUserId()).orElseThrow();
        assertThat(updatedCount).isEqualTo(1);
        assertThat(updatedProfile.getHealthGoal()).isEqualTo("MAINTAIN");
        assertThat(updatedProfile.getWeightKg()).isEqualByComparingTo(new BigDecimal("55"));
        assertThat(updatedProfile.getUpdatedAt()).isNotNull();
    }

    private User saveUser(String email) {
        User user = User.builder()
                .email(email)
                .passwordHash("encoded-password")
                .nickname("nyam")
                .status("ACTIVE")
                .build();
        userRepository.save(user);
        return user;
    }

    private HealthProfile healthProfile(Long userId, String healthGoal) {
        return HealthProfile.builder()
                .userId(userId)
                .heightCm(new BigDecimal("162"))
                .weightKg(new BigDecimal("54"))
                .targetWeightKg(new BigDecimal("50"))
                .birthDate(LocalDate.of(2001, 3, 15))
                .gender("FEMALE")
                .healthGoal(healthGoal)
                .activityLevel("LIGHT")
                .dietStylesJson("[\"BALANCED\"]")
                .restrictedFoodsJson("[\"CAFFEINE\"]")
                .allergiesJson("[\"PEANUT\"]")
                .targetCalories(new BigDecimal("2000"))
                .targetProteinG(new BigDecimal("90"))
                .targetCarbsG(new BigDecimal("260"))
                .targetFatG(new BigDecimal("65"))
                .targetSodiumMg(new BigDecimal("2300"))
                .build();
    }
}
