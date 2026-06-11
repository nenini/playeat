package com.nyamnyam.coach.character.service;

import com.nyamnyam.coach.character.entity.CharacterAppearanceType;
import com.nyamnyam.coach.character.entity.CharacterEntity;
import com.nyamnyam.coach.character.entity.CharacterMood;
import com.nyamnyam.coach.character.entity.CharacterStage;
import com.nyamnyam.coach.character.entity.XpHistory;
import com.nyamnyam.coach.character.entity.XpSourceType;
import com.nyamnyam.coach.character.repository.CharacterRepository;
import com.nyamnyam.coach.character.repository.XpHistoryRepository;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.CharacterErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CharacterGrowthService {

    private static final String DEFAULT_CHARACTER_NAME = "냠냠이";

    private final CharacterRepository characterRepository;
    private final XpHistoryRepository xpHistoryRepository;

    @Transactional
    public void createDefaultCharacterIfMissing(Long userId, String nickname) {
        if (characterRepository.existsByUserId(userId)) {
            return;
        }

        CharacterEntity character = CharacterEntity.builder()
                .userId(userId)
                .name(DEFAULT_CHARACTER_NAME)
                .level(1)
                .xp(0)
                .stage(CharacterStage.EGG.name())
                .mood(CharacterMood.NORMAL.name())
                .appearanceType(CharacterAppearanceType.NORMAL.name())
                .streakDays(0)
                .bestStreakDays(0)
                .build();

        characterRepository.save(character);
    }

    @Transactional
    public void addXp(Long userId, XpSourceType sourceType, Long sourceId, int xpAmount, String reason) {
        if (sourceType == null || sourceId == null || xpAmount <= 0) {
            throw new BusinessException(CharacterErrorCode.INVALID_CHARACTER_STATE);
        }

        CharacterEntity character = characterRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(CharacterErrorCode.CHARACTER_NOT_FOUND));

        try {
            xpHistoryRepository.save(XpHistory.builder()
                    .userId(userId)
                    .characterId(character.getCharacterId())
                    .sourceType(sourceType.name())
                    .sourceId(sourceId)
                    .xpAmount(xpAmount)
                    .reason(reason)
                    .build());
        } catch (DuplicateKeyException exception) {
            log.warn("XP already granted: sourceType={}, sourceId={}", sourceType, sourceId);
            return;
        }

        int level = character.getLevel();
        int xp = character.getXp() + xpAmount;
        while (xp >= requiredXp(level)) {
            xp -= requiredXp(level);
            level += 1;
        }

        characterRepository.updateGrowth(
                character.getCharacterId(),
                level,
                xp,
                stageFor(level),
                CharacterMood.NORMAL.name(),
                character.getAppearanceType(),
                character.getStreakDays(),
                Math.max(character.getBestStreakDays(), character.getStreakDays())
        );
    }

    private int requiredXp(int level) {
        return level * 150 + 150;
    }

    private String stageFor(int level) {
        if (level >= 30) {
            return CharacterStage.ADULT.name();
        }
        if (level >= 20) {
            return CharacterStage.CHILD.name();
        }
        if (level >= 10) {
            return CharacterStage.BABY.name();
        }
        return CharacterStage.EGG.name();
    }
}
