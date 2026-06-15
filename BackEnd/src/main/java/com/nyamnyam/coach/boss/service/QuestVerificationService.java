package com.nyamnyam.coach.boss.service;

import com.nyamnyam.coach.boss.dto.response.QuestVerifyResponse;
import com.nyamnyam.coach.boss.entity.BossBattleParticipantStatus;
import com.nyamnyam.coach.boss.entity.BossBattleStatus;
import com.nyamnyam.coach.boss.entity.DamageSourceType;
import com.nyamnyam.coach.boss.entity.Quest;
import com.nyamnyam.coach.boss.entity.QuestStatus;
import com.nyamnyam.coach.boss.entity.QuestType;
import com.nyamnyam.coach.boss.entity.QuestVerification;
import com.nyamnyam.coach.boss.repository.BossBattleParticipantRepository;
import com.nyamnyam.coach.boss.repository.QuestRepository;
import com.nyamnyam.coach.boss.repository.row.BattleConditionStateRow;
import com.nyamnyam.coach.boss.repository.row.BattleStateRow;
import com.nyamnyam.coach.boss.repository.row.BossBattleParticipantRow;
import com.nyamnyam.coach.boss.repository.row.DietVerificationRow;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.BossErrorCode;
import com.nyamnyam.coach.global.exception.errorcode.QuestErrorCode;
import com.nyamnyam.coach.ranking.entity.GuildScoreSourceType;
import com.nyamnyam.coach.ranking.repository.GuildScoreRepository;
import com.nyamnyam.coach.ranking.service.GuildScoreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class QuestVerificationService {

    private static final String SUGAR_UNDER_LIMIT = "SUGAR_UNDER_LIMIT";

    private final QuestRepository questRepository;
    private final BossBattleParticipantRepository bossBattleParticipantRepository;
    private final GuildScoreService guildScoreService;
    private final GuildScoreRepository guildScoreRepository;

    public QuestVerificationService(
            QuestRepository questRepository,
            BossBattleParticipantRepository bossBattleParticipantRepository,
            GuildScoreService guildScoreService,
            GuildScoreRepository guildScoreRepository
    ) {
        this.questRepository = questRepository;
        this.bossBattleParticipantRepository = bossBattleParticipantRepository;
        this.guildScoreService = guildScoreService;
        this.guildScoreRepository = guildScoreRepository;
    }

    @Transactional
    public QuestVerifyResponse verifyQuest(Long questId, Long userId) {
        Quest quest = questRepository.findQuestForUpdate(questId)
                .orElseThrow(() -> new BusinessException(QuestErrorCode.QUEST_NOT_FOUND));
        validateQuestOwner(quest, userId);
        validateQuestInProgress(quest);
        if (questRepository.existsQuestVerificationByQuestId(questId)) {
            throw new BusinessException(QuestErrorCode.QUEST_VERIFICATION_ALREADY_EXISTS);
        }

        BattleStateRow battle = findBattleForUpdate(quest.getBattleId());
        validateBattleInProgress(battle);
        validateActiveParticipant(quest.getBattleId(), userId);

        LocalDate today = LocalDate.now();
        DietVerificationRow diet = verifyQuestType(quest, userId, today);
        int currentValue = defaultValue(quest.getTargetValue());
        int updated = questRepository.updateQuestCompleted(questId, currentValue);
        if (updated == 0) {
            throw new BusinessException(QuestErrorCode.QUEST_NOT_IN_PROGRESS);
        }

        insertVerification(quest, userId, diet, today);
        applyDamage(
                quest.getBattleId(),
                userId,
                defaultValue(quest.getDamage()),
                DamageSourceType.PERSONAL_QUEST,
                quest.getQuestId(),
                "개인 퀘스트 완료"
        );
        addScoreLog(
                quest.getGuildId(),
                userId,
                quest.getBattleId(),
                GuildScoreSourceType.QUEST_COMPLETE,
                quest.getQuestId(),
                defaultValue(quest.getDamage()),
                today,
                "개인 퀘스트 완료"
        );

        refreshCommonConditions(battle, userId, today);
        tryDefeatBattle(quest.getBattleId(), quest.getGuildId(), userId, today);

        BattleStateRow updatedBattle = findBattleForUpdate(quest.getBattleId());
        return new QuestVerifyResponse(
                quest.getQuestId(),
                quest.getBattleId(),
                quest.getGuildId(),
                QuestStatus.COMPLETED.name(),
                true,
                defaultValue(quest.getDamage()),
                defaultValue(updatedBattle.getCurrentHp()),
                defaultValue(updatedBattle.getTotalDamage()),
                updatedBattle.getStatus(),
                "퀘스트 검증에 성공했습니다."
        );
    }

    private void validateQuestOwner(Quest quest, Long userId) {
        if (!Objects.equals(quest.getUserId(), userId)) {
            throw new BusinessException(QuestErrorCode.QUEST_NOT_OWNER);
        }
    }

    private void validateQuestInProgress(Quest quest) {
        if (QuestStatus.COMPLETED.name().equals(quest.getStatus())
                || QuestStatus.REWARDED.name().equals(quest.getStatus())) {
            throw new BusinessException(QuestErrorCode.QUEST_ALREADY_COMPLETED);
        }
        if (!QuestStatus.IN_PROGRESS.name().equals(quest.getStatus())) {
            throw new BusinessException(QuestErrorCode.QUEST_NOT_IN_PROGRESS);
        }
    }

    private BattleStateRow findBattleForUpdate(Long battleId) {
        return questRepository.findBattleStateForUpdate(battleId)
                .orElseThrow(() -> new BusinessException(BossErrorCode.BOSS_BATTLE_NOT_FOUND));
    }

    private void validateBattleInProgress(BattleStateRow battle) {
        if (!BossBattleStatus.IN_PROGRESS.name().equals(battle.getStatus())) {
            throw new BusinessException(BossErrorCode.BOSS_BATTLE_NOT_IN_PROGRESS);
        }
    }

    private void validateActiveParticipant(Long battleId, Long userId) {
        BossBattleParticipantRow participant = bossBattleParticipantRepository.findParticipantByBattleIdAndUserId(
                        battleId,
                        userId
                )
                .orElseThrow(() -> new BusinessException(BossErrorCode.BOSS_BATTLE_PARTICIPANT_NOT_FOUND));
        if (!BossBattleParticipantStatus.ACTIVE.name().equals(participant.getStatus())) {
            throw new BusinessException(BossErrorCode.BOSS_BATTLE_PARTICIPANT_INACTIVE);
        }
    }

    private DietVerificationRow verifyQuestType(Quest quest, Long userId, LocalDate today) {
        if (!QuestType.RECORD_DIET.name().equals(quest.getQuestType())) {
            throw new BusinessException(QuestErrorCode.QUEST_UNSUPPORTED_TYPE);
        }
        LocalDateTime startAt = today.atStartOfDay();
        LocalDateTime endAt = today.plusDays(1).atStartOfDay();
        return questRepository.findTodayDietForVerification(userId, startAt, endAt)
                .orElseThrow(() -> new BusinessException(QuestErrorCode.QUEST_VERIFY_FAILED));
    }

    private void insertVerification(
            Quest quest,
            Long userId,
            DietVerificationRow diet,
            LocalDate verifiedDate
    ) {
        QuestVerification verification = new QuestVerification();
        verification.setQuestId(quest.getQuestId());
        verification.setUserId(userId);
        verification.setBattleId(quest.getBattleId());
        verification.setDietId(diet.getDietId());
        verification.setQuestType(quest.getQuestType());
        verification.setVerified(true);
        verification.setDamageAmount(defaultValue(quest.getDamage()));
        verification.setMessage("퀘스트 검증에 성공했습니다.");
        verification.setVerifiedDate(verifiedDate);
        questRepository.insertQuestVerification(verification);
    }

    private void refreshCommonConditions(BattleStateRow battle, Long userId, LocalDate today) {
        List<BattleConditionStateRow> conditions = questRepository.findBattleConditionsForUpdate(battle.getBattleId());
        for (BattleConditionStateRow condition : conditions) {
            if (Boolean.TRUE.equals(condition.getCompleted())) {
                continue;
            }
            if (!SUGAR_UNDER_LIMIT.equals(condition.getTargetType())) {
                continue;
            }
            BigDecimal thresholdValue = condition.getThresholdValue();
            if (thresholdValue == null || condition.getTargetValue() == null) {
                continue;
            }
            LocalDate startDate = battle.getStartedAt() == null ? today : battle.getStartedAt().toLocalDate();
            int currentValue = questRepository.countSugarUnderLimitMemberDates(
                    battle.getGuildId(),
                    startDate,
                    today,
                    thresholdValue
            );
            questRepository.updateBattleConditionProgressValue(condition.getBattleConditionId(), currentValue);
            if (currentValue >= condition.getTargetValue()) {
                int completed = questRepository.completeBattleCondition(condition.getBattleConditionId(), currentValue);
                if (completed > 0) {
                    int damage = defaultValue(condition.getDamage());
                    applyDamage(
                            battle.getBattleId(),
                            userId,
                            damage,
                            DamageSourceType.COMMON_CONDITION,
                            condition.getBattleConditionId(),
                            "공통 격파 조건 완료: " + condition.getTitle()
                    );
                    addScoreLog(
                            battle.getGuildId(),
                            userId,
                            battle.getBattleId(),
                            GuildScoreSourceType.COMMON_CONDITION_COMPLETE,
                            condition.getBattleConditionId(),
                            damage,
                            today,
                            "공통 격파 조건 완료: " + condition.getTitle()
                    );
                }
            }
        }
    }

    private void applyDamage(
            Long battleId,
            Long userId,
            int damage,
            DamageSourceType sourceType,
            Long sourceId,
            String description
    ) {
        if (damage <= 0) {
            return;
        }
        int updated = questRepository.updateBattleDamage(battleId, damage);
        if (updated == 0) {
            throw new BusinessException(BossErrorCode.BOSS_BATTLE_NOT_IN_PROGRESS);
        }
        questRepository.insertBossBattleDamageLog(
                battleId,
                userId,
                damage,
                sourceType.name(),
                sourceId,
                description
        );
    }

    private void tryDefeatBattle(Long battleId, Long guildId, Long userId, LocalDate today) {
        BattleStateRow battle = findBattleForUpdate(battleId);
        if (!BossBattleStatus.IN_PROGRESS.name().equals(battle.getStatus())) {
            return;
        }
        if (defaultValue(battle.getCurrentHp()) > 0) {
            return;
        }
        if (questRepository.countIncompleteBattleConditions(battleId) > 0) {
            return;
        }
        int updated = questRepository.updateBattleDefeated(battleId);
        if (updated == 0) {
            return;
        }
        if (!guildScoreRepository.existsScoreLog(GuildScoreSourceType.BOSS_CLEAR.name(), battleId)) {
            int clearBonus = guildScoreService.calculateClearBonus(
                    battle.getDifficulty(),
                    BossBattleStatus.DEFEATED.name()
            );
            addScoreLog(
                    guildId,
                    userId,
                    battleId,
                    GuildScoreSourceType.BOSS_CLEAR,
                    battleId,
                    clearBonus,
                    today,
                    "보스전 클리어"
            );
        }
    }

    private void addScoreLog(
            Long guildId,
            Long userId,
            Long battleId,
            GuildScoreSourceType sourceType,
            Long sourceId,
            int score,
            LocalDate scoreDate,
            String description
    ) {
        if (score <= 0) {
            return;
        }
        guildScoreService.addScoreLog(guildId, userId, battleId, sourceType, sourceId, score, scoreDate, description);
    }

    private int defaultValue(Integer value) {
        return value == null ? 0 : value;
    }
}
