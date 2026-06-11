package com.nyamnyam.coach.character.repository;

import com.nyamnyam.coach.character.entity.CharacterEntity;
import com.nyamnyam.coach.character.entity.XpHistory;
import com.nyamnyam.coach.user.entity.User;
import com.nyamnyam.coach.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@MybatisTest
@ActiveProfiles("test")
@Sql(scripts = "/test-auth-schema.sql")
class CharacterRepositoryTest {

    private final CharacterRepository characterRepository;
    private final XpHistoryRepository xpHistoryRepository;
    private final UserRepository userRepository;

    @Autowired
    CharacterRepositoryTest(
            CharacterRepository characterRepository,
            XpHistoryRepository xpHistoryRepository,
            UserRepository userRepository
    ) {
        this.characterRepository = characterRepository;
        this.xpHistoryRepository = xpHistoryRepository;
        this.userRepository = userRepository;
    }

    @Test
    void saveFindAndUpdateName() {
        User user = saveUser();
        CharacterEntity character = character(user.getUserId());

        characterRepository.save(character);
        int updatedCount = characterRepository.updateName(user.getUserId(), "newnyam");

        CharacterEntity foundCharacter = characterRepository.findByUserId(user.getUserId()).orElseThrow();

        assertThat(character.getCharacterId()).isNotNull();
        assertThat(updatedCount).isEqualTo(1);
        assertThat(foundCharacter.getName()).isEqualTo("newnyam");
        assertThat(foundCharacter.getBestStreakDays()).isZero();
    }

    @Test
    void saveAndFindXpHistoryWithUniqueSource() {
        User user = saveUser();
        CharacterEntity character = character(user.getUserId());
        characterRepository.save(character);
        XpHistory xpHistory = xpHistory(user.getUserId(), character.getCharacterId());

        xpHistoryRepository.save(xpHistory);

        List<XpHistory> histories = xpHistoryRepository.findByUserId(user.getUserId(), "DIET", 20, 0);

        assertThat(xpHistory.getXpHistoryId()).isNotNull();
        assertThat(histories).hasSize(1);
        assertThat(xpHistoryRepository.countByUserId(user.getUserId(), "DIET")).isEqualTo(1);
        assertThatThrownBy(() -> xpHistoryRepository.save(xpHistory(user.getUserId(), character.getCharacterId())))
                .isInstanceOf(DuplicateKeyException.class);
    }

    private User saveUser() {
        User user = User.builder()
                .email("character@example.com")
                .passwordHash("password")
                .nickname("nyam")
                .status("ACTIVE")
                .onboardingCompleted(false)
                .build();
        userRepository.save(user);
        return user;
    }

    private CharacterEntity character(Long userId) {
        return CharacterEntity.builder()
                .userId(userId)
                .name("nyam")
                .level(1)
                .xp(0)
                .stage("LEVEL_1")
                .mood("NORMAL")
                .appearanceType("NORMAL")
                .streakDays(0)
                .bestStreakDays(0)
                .build();
    }

    private XpHistory xpHistory(Long userId, Long characterId) {
        return XpHistory.builder()
                .userId(userId)
                .characterId(characterId)
                .sourceType("DIET")
                .sourceId(31L)
                .xpAmount(120)
                .reason("recorded meal")
                .build();
    }
}
