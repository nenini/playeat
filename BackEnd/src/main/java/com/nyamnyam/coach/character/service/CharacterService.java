package com.nyamnyam.coach.character.service;

import com.nyamnyam.coach.character.dto.request.UpdateCharacterNameRequest;
import com.nyamnyam.coach.character.dto.response.CharacterResponse;
import com.nyamnyam.coach.character.dto.response.UpdateCharacterNameResponse;
import com.nyamnyam.coach.character.dto.response.XpHistoryListResponse;
import com.nyamnyam.coach.character.dto.response.XpHistoryResponse;
import com.nyamnyam.coach.character.entity.CharacterEntity;
import com.nyamnyam.coach.character.entity.XpHistory;
import com.nyamnyam.coach.character.entity.XpSourceType;
import com.nyamnyam.coach.character.repository.CharacterRepository;
import com.nyamnyam.coach.character.repository.XpHistoryRepository;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.CharacterErrorCode;
import com.nyamnyam.coach.global.exception.errorcode.CommonErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CharacterService {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    private final CharacterRepository characterRepository;
    private final XpHistoryRepository xpHistoryRepository;

    @Transactional(readOnly = true)
    public CharacterResponse getMyCharacter(Long userId) {
        CharacterEntity character = findByUserId(userId);
        return toCharacterResponse(character);
    }

    @Transactional
    public UpdateCharacterNameResponse updateName(Long userId, UpdateCharacterNameRequest request) {
        CharacterEntity character = findByUserId(userId);
        String name = request.name().trim();

        characterRepository.updateName(userId, name);
        CharacterEntity updatedCharacter = findByUserId(userId);

        return new UpdateCharacterNameResponse(
                updatedCharacter.getCharacterId(),
                updatedCharacter.getName(),
                updatedCharacter.getUpdatedAt()
        );
    }

    @Transactional(readOnly = true)
    public XpHistoryListResponse getXpHistory(Long userId, Integer page, Integer size, String sourceType) {
        findByUserId(userId);

        int normalizedPage = normalizePage(page);
        int normalizedSize = normalizeSize(size);
        String normalizedSourceType = normalizeSourceType(sourceType);
        int offset = normalizedPage * normalizedSize;

        List<XpHistoryResponse> content = xpHistoryRepository.findByUserId(
                        userId,
                        normalizedSourceType,
                        normalizedSize,
                        offset
                )
                .stream()
                .map(this::toXpHistoryResponse)
                .toList();
        long totalElements = xpHistoryRepository.countByUserId(userId, normalizedSourceType);

        return new XpHistoryListResponse(
                content,
                normalizedPage,
                normalizedSize,
                totalElements,
                offset + content.size() < totalElements
        );
    }

    private CharacterEntity findByUserId(Long userId) {
        return characterRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(CharacterErrorCode.CHARACTER_NOT_FOUND));
    }

    private CharacterResponse toCharacterResponse(CharacterEntity character) {
        int requiredXp = requiredXp(character.getLevel());
        return new CharacterResponse(
                character.getCharacterId(),
                character.getUserId(),
                character.getName(),
                character.getLevel(),
                character.getXp(),
                requiredXp,
                xpProgressRate(character.getXp(), requiredXp),
                character.getStage(),
                character.getMood(),
                character.getMoodMessage(),
                character.getAppearanceType(),
                character.getStreakDays(),
                character.getBestStreakDays(),
                character.getCreatedAt(),
                character.getUpdatedAt()
        );
    }

    private XpHistoryResponse toXpHistoryResponse(XpHistory xpHistory) {
        return new XpHistoryResponse(
                xpHistory.getXpHistoryId(),
                xpHistory.getSourceType(),
                xpHistory.getSourceId(),
                xpHistory.getXpAmount(),
                xpHistory.getReason(),
                xpHistory.getCreatedAt()
        );
    }

    private int requiredXp(Integer level) {
        int safeLevel = level == null || level < 1 ? 1 : level;
        return safeLevel * 150 + 150;
    }

    private double xpProgressRate(Integer xp, int requiredXp) {
        if (requiredXp <= 0) {
            return 0.0;
        }
        double rate = (xp == null ? 0 : xp) * 100.0 / requiredXp;
        return BigDecimal.valueOf(rate)
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private int normalizePage(Integer page) {
        if (page == null) {
            return DEFAULT_PAGE;
        }
        if (page < 0) {
            throw new BusinessException(CommonErrorCode.INVALID_REQUEST);
        }
        return page;
    }

    private int normalizeSize(Integer size) {
        if (size == null) {
            return DEFAULT_SIZE;
        }
        if (size < 1 || size > MAX_SIZE) {
            throw new BusinessException(CommonErrorCode.INVALID_REQUEST);
        }
        return size;
    }

    private String normalizeSourceType(String sourceType) {
        if (sourceType == null || sourceType.isBlank()) {
            return null;
        }
        String normalizedSourceType = sourceType.trim().toUpperCase();
        if (!XpSourceType.isValid(normalizedSourceType)) {
            throw new BusinessException(CommonErrorCode.INVALID_REQUEST);
        }
        return normalizedSourceType;
    }
}
