package com.nyamnyam.coach.character.service;

import com.nyamnyam.coach.character.dto.request.UpdateCharacterNameRequest;
import com.nyamnyam.coach.character.dto.response.CharacterResponse;
import com.nyamnyam.coach.character.dto.response.UpdateCharacterNameResponse;
import com.nyamnyam.coach.character.dto.response.XpHistoryListResponse;
import com.nyamnyam.coach.character.entity.CharacterEntity;
import com.nyamnyam.coach.character.entity.XpHistory;
import com.nyamnyam.coach.character.repository.CharacterRepository;
import com.nyamnyam.coach.character.repository.XpHistoryRepository;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.CharacterErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CharacterServiceTest {

    @Mock
    private CharacterRepository characterRepository;

    @Mock
    private XpHistoryRepository xpHistoryRepository;

    private CharacterService characterService;

    @BeforeEach
    void setUp() {
        characterService = new CharacterService(characterRepository, xpHistoryRepository);
    }

    @Test
    void getMyCharacter() {
        when(characterRepository.findByUserId(1L)).thenReturn(Optional.of(character()));

        CharacterResponse response = characterService.getMyCharacter(1L);

        assertThat(response.requiredXp()).isEqualTo(1200);
        assertThat(response.xpProgressRate()).isEqualTo(79.2);
        assertThat(response.bestStreakDays()).isEqualTo(21);
    }

    @Test
    void getMyCharacterThrowsWhenMissing() {
        when(characterRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> characterService.getMyCharacter(1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CharacterErrorCode.CHARACTER_NOT_FOUND);
    }

    @Test
    void updateNameTrimsName() {
        CharacterEntity before = character();
        CharacterEntity after = character();
        after.setName("newnyam");

        when(characterRepository.findByUserId(1L))
                .thenReturn(Optional.of(before))
                .thenReturn(Optional.of(after));

        UpdateCharacterNameResponse response = characterService.updateName(
                1L,
                new UpdateCharacterNameRequest("  newnyam  ")
        );

        verify(characterRepository).updateName(1L, "newnyam");
        assertThat(response.name()).isEqualTo("newnyam");
    }

    @Test
    void getXpHistory() {
        when(characterRepository.findByUserId(1L)).thenReturn(Optional.of(character()));
        when(xpHistoryRepository.findByUserId(1L, "DIET", 20, 0))
                .thenReturn(List.of(XpHistory.builder()
                        .xpHistoryId(1L)
                        .userId(1L)
                        .characterId(1L)
                        .sourceType("DIET")
                        .sourceId(31L)
                        .xpAmount(120)
                        .reason("recorded meal")
                        .createdAt(LocalDateTime.of(2026, 6, 11, 8, 30))
                        .build()));
        when(xpHistoryRepository.countByUserId(1L, "DIET")).thenReturn(1L);

        XpHistoryListResponse response = characterService.getXpHistory(1L, 0, 20, "diet");

        assertThat(response.content()).hasSize(1);
        assertThat(response.content().get(0).sourceType()).isEqualTo("DIET");
        assertThat(response.hasNext()).isFalse();
    }

    private CharacterEntity character() {
        return CharacterEntity.builder()
                .characterId(1L)
                .userId(1L)
                .name("nyam")
                .level(7)
                .xp(950)
                .stage("EGG")
                .mood("NORMAL")
                .appearanceType("NORMAL")
                .streakDays(15)
                .bestStreakDays(21)
                .createdAt(LocalDateTime.of(2026, 6, 11, 9, 0))
                .updatedAt(LocalDateTime.of(2026, 6, 11, 9, 10))
                .build();
    }
}
