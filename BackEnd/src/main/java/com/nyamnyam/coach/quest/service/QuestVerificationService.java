package com.nyamnyam.coach.quest.service;

import com.nyamnyam.coach.boss.dto.response.CommonConditionVerifyItemResponse;
import com.nyamnyam.coach.boss.dto.response.CommonConditionVerifyResponse;
import com.nyamnyam.coach.boss.service.ConditionEvaluationResult;
import com.nyamnyam.coach.quest.dto.response.QuestVerifyResponse;
import com.nyamnyam.coach.boss.entity.BossBattleParticipantStatus;
import com.nyamnyam.coach.boss.entity.BossBattleStatus;
import com.nyamnyam.coach.boss.entity.DamageSourceType;
import com.nyamnyam.coach.quest.entity.Quest;
import com.nyamnyam.coach.quest.entity.QuestStatus;
import com.nyamnyam.coach.quest.entity.QuestVerification;
import com.nyamnyam.coach.boss.repository.BossBattleParticipantRepository;
import com.nyamnyam.coach.quest.repository.QuestRepository;
import com.nyamnyam.coach.boss.repository.row.BattleConditionStateRow;
import com.nyamnyam.coach.boss.repository.row.BattleStateRow;
import com.nyamnyam.coach.boss.repository.row.BossBattleParticipantRow;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.BossErrorCode;
import com.nyamnyam.coach.global.exception.errorcode.QuestErrorCode;
import com.nyamnyam.coach.guild.service.GuildChatService;
import com.nyamnyam.coach.ranking.entity.GuildScoreSourceType;
import com.nyamnyam.coach.ranking.repository.GuildScoreRepository;
import com.nyamnyam.coach.ranking.service.GuildScoreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class QuestVerificationService {

    private final QuestRepository questRepository;
    private final BossBattleParticipantRepository bossBattleParticipantRepository;
    private final ConditionEvaluationService conditionEvaluationService;
    private final GuildScoreService guildScoreService;
    private final GuildScoreRepository guildScoreRepository;
    private final GuildChatService guildChatService;

    public QuestVerificationService(
            QuestRepository questRepository,
            BossBattleParticipantRepository bossBattleParticipantRepository,
            ConditionEvaluationService conditionEvaluationService,
            GuildScoreService guildScoreService,
            GuildScoreRepository guildScoreRepository,
            GuildChatService guildChatService
    ) {
        this.questRepository = questRepository;
        this.bossBattleParticipantRepository = bossBattleParticipantRepository;
        this.conditionEvaluationService = conditionEvaluationService;
        this.guildScoreService = guildScoreService;
        this.guildScoreRepository = guildScoreRepository;
        this.guildChatService = guildChatService;
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
        BossBattleParticipantRow participant = validateActiveParticipant(quest.getBattleId(), userId);

        LocalDate today = LocalDate.now();
        completeQuestIfSatisfied(quest, battle, participant, today, true);
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

    @Transactional
    public CommonConditionVerifyResponse verifyCommonConditions(Long battleId, Long userId) {
        BattleStateRow battle = findBattleForUpdate(battleId);
        validateBattleInProgress(battle);
        if (!questRepository.existsActiveGuildMember(battle.getGuildId(), userId)) {
            throw new BusinessException(BossErrorCode.BOSS_BATTLE_ACCESS_DENIED);
        }

        LocalDate today = LocalDate.now();
        List<CommonConditionVerifyItemResponse> conditions = refreshCommonConditions(battle, userId, today);
        tryDefeatBattle(battleId, battle.getGuildId(), userId, today);
        BattleStateRow updatedBattle = findBattleForUpdate(battleId);
        return new CommonConditionVerifyResponse(
                battleId,
                updatedBattle.getStatus(),
                defaultValue(updatedBattle.getCurrentHp()),
                defaultValue(updatedBattle.getTotalDamage()),
                conditions
        );
    }

    @Transactional
    public void tryVerifyQuestForActivity(Long questId, Long userId, LocalDate activityDate) {
        Quest quest = questRepository.findQuestForUpdate(questId)
                .orElseThrow(() -> new BusinessException(QuestErrorCode.QUEST_NOT_FOUND));
        if (!Objects.equals(quest.getUserId(), userId) || !QuestStatus.IN_PROGRESS.name().equals(quest.getStatus())) {
            return;
        }
        if (questRepository.existsQuestVerificationByQuestId(questId)) {
            return;
        }
        BattleStateRow battle = findBattleForUpdate(quest.getBattleId());
        if (!BossBattleStatus.IN_PROGRESS.name().equals(battle.getStatus())) {
            return;
        }
        BossBattleParticipantRow participant = validateActiveParticipant(quest.getBattleId(), userId);
        boolean completed = completeQuestIfSatisfied(quest, battle, participant, activityDate, false);
        refreshCommonConditions(battle, userId, activityDate);
        if (completed) {
            tryDefeatBattle(quest.getBattleId(), quest.getGuildId(), userId, activityDate);
        }
    }

    @Transactional
    public void tryRefreshCommonConditionsForActivity(Long battleId, Long userId, LocalDate activityDate) {
        BattleStateRow battle = findBattleForUpdate(battleId);
        if (!BossBattleStatus.IN_PROGRESS.name().equals(battle.getStatus())) {
            return;
        }
        refreshCommonConditions(battle, userId, activityDate);
        tryDefeatBattle(battleId, battle.getGuildId(), userId, activityDate);
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

    private BossBattleParticipantRow validateActiveParticipant(Long battleId, Long userId) {
        BossBattleParticipantRow participant = bossBattleParticipantRepository.findParticipantByBattleIdAndUserId(
                        battleId,
                        userId
                )
                .orElseThrow(() -> new BusinessException(BossErrorCode.BOSS_BATTLE_PARTICIPANT_NOT_FOUND));
        if (!BossBattleParticipantStatus.ACTIVE.name().equals(participant.getStatus())) {
            throw new BusinessException(BossErrorCode.BOSS_BATTLE_PARTICIPANT_INACTIVE);
        }
        return participant;
    }

    private boolean completeQuestIfSatisfied(
            Quest quest,
            BattleStateRow battle,
            BossBattleParticipantRow participant,
            LocalDate activityDate,
            boolean throwIfFailed
    ) {
        ConditionEvaluationResult result = conditionEvaluationService.evaluateQuest(quest, quest.getUserId(), activityDate);
        if (!result.satisfied()) {
            if (throwIfFailed) {
                throw new BusinessException(QuestErrorCode.QUEST_VERIFY_FAILED);
            }
            return false;
        }

        int updated = questRepository.updateQuestCompleted(quest.getQuestId(), result.currentValue());
        if (updated == 0) {
            if (throwIfFailed) {
                throw new BusinessException(QuestErrorCode.QUEST_NOT_IN_PROGRESS);
            }
            return false;
        }

        insertVerification(quest, result.dietId(), activityDate);
        int damage = defaultValue(quest.getDamage());
        applyDamage(
                quest.getBattleId(),
                quest.getUserId(),
                damage,
                DamageSourceType.PERSONAL_QUEST,
                quest.getQuestId(),
                "개인 퀘스트 완료"
        );
        addScoreLog(
                quest.getGuildId(),
                quest.getUserId(),
                quest.getBattleId(),
                GuildScoreSourceType.QUEST_COMPLETE,
                quest.getQuestId(),
                damage,
                activityDate,
                "개인 퀘스트 완료"
        );
        String nickname = participant.getSnapshotNickname() == null ? "길드원" : participant.getSnapshotNickname();
        sendSystemMessage(battle.getGuildId(), nickname + "님이 개인 퀘스트를 완료했어요: " + quest.getTitle());
        return true;
    }

    private void insertVerification(
            Quest quest,
            Long dietId,
            LocalDate verifiedDate
    ) {
        QuestVerification verification = new QuestVerification();
        verification.setQuestId(quest.getQuestId());
        verification.setUserId(quest.getUserId());
        verification.setBattleId(quest.getBattleId());
        verification.setDietId(dietId);
        verification.setQuestType(quest.getQuestType());
        verification.setVerified(true);
        verification.setDamageAmount(defaultValue(quest.getDamage()));
        verification.setMessage("퀘스트 검증에 성공했습니다.");
        verification.setVerifiedDate(verifiedDate);
        questRepository.insertQuestVerification(verification);
    }

    private List<CommonConditionVerifyItemResponse> refreshCommonConditions(BattleStateRow battle, Long userId, LocalDate today) {
        List<BattleConditionStateRow> conditions = questRepository.findBattleConditionsForUpdate(battle.getBattleId());
        List<CommonConditionVerifyItemResponse> responses = new ArrayList<>();
        for (BattleConditionStateRow condition : conditions) {
            if (!Boolean.TRUE.equals(condition.getVerificationSupported())) {
                responses.add(toConditionResponse(condition, false));
                continue;
            }
            int currentValue;
            try {
                currentValue = conditionEvaluationService.evaluateGuildBattleCondition(battle, condition, today);
            } catch (BusinessException e) {
                responses.add(toConditionResponse(condition, false));
                continue;
            }
            questRepository.updateBattleConditionProgressValue(condition.getBattleConditionId(), currentValue);
            condition.setCurrentValue(currentValue);
            boolean newlyCompleted = false;
            if (currentValue >= defaultValue(condition.getTargetValue())) {
                int completed = questRepository.completeBattleCondition(condition.getBattleConditionId(), currentValue);
                if (completed > 0) {
                    condition.setCompleted(true);
                    newlyCompleted = true;
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
                    sendSystemMessage(battle.getGuildId(), "공통 격파 조건을 달성했어요: " + condition.getTitle());
                }
            }
            responses.add(toConditionResponse(condition, newlyCompleted));
        }
        return responses;
    }

    private CommonConditionVerifyItemResponse toConditionResponse(
            BattleConditionStateRow condition,
            boolean newlyCompleted
    ) {
        return new CommonConditionVerifyItemResponse(
                condition.getBattleConditionId(),
                condition.getTitle(),
                defaultValue(condition.getCurrentValue()),
                defaultValue(condition.getTargetValue()),
                Boolean.TRUE.equals(condition.getCompleted()),
                newlyCompleted,
                defaultValue(condition.getDamage())
        );
    }

    private void sendSystemMessage(Long guildId, String content) {
        try {
            guildChatService.createSystemMessage(guildId, content);
        } catch (RuntimeException ignored) {
            // System chat must not break quest verification or boss battle state transitions.
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
            sendSystemMessage(guildId, battle.getBossName() + "이 격파되었습니다!");
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
