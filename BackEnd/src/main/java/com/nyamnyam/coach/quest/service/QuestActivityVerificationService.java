package com.nyamnyam.coach.quest.service;

import com.nyamnyam.coach.quest.entity.QuestStatus;
import com.nyamnyam.coach.quest.repository.QuestRepository;
import com.nyamnyam.coach.boss.repository.row.BattleStateRow;
import com.nyamnyam.coach.quest.entity.Quest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class QuestActivityVerificationService {

    private final QuestRepository questRepository;
    private final QuestVerificationService questVerificationService;

    public QuestActivityVerificationService(
            QuestRepository questRepository,
            QuestVerificationService questVerificationService
    ) {
        this.questRepository = questRepository;
        this.questVerificationService = questVerificationService;
    }

    public void verifyDietActivity(Long userId, LocalDate activityDate) {
        List<BattleStateRow> battles = questRepository.findInProgressBattlesByActiveParticipant(userId);
        for (BattleStateRow battle : battles) {
            questRepository.findQuestByBattleIdAndUserId(battle.getBattleId(), userId)
                    .filter(quest -> QuestStatus.IN_PROGRESS.name().equals(quest.getStatus()))
                    .map(Quest::getQuestId)
                    .ifPresent(questId -> {
                        try {
                            questVerificationService.tryVerifyQuestForActivity(questId, userId, activityDate);
                        } catch (RuntimeException ignored) {
                            // Activity verification is opportunistic; manual verification still reports exact errors.
                        }
                    });
            questVerificationService.tryRefreshCommonConditionsForActivity(
                    battle.getBattleId(),
                    userId,
                    activityDate
            );
        }
    }
}
