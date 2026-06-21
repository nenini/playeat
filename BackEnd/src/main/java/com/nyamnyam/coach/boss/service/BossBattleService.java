package com.nyamnyam.coach.boss.service;

import com.nyamnyam.coach.boss.dto.request.BossBattleCreateRequest;
import com.nyamnyam.coach.boss.dto.response.BossBattleConditionResponse;
import com.nyamnyam.coach.boss.dto.response.BossBattleCreateResponse;
import com.nyamnyam.coach.boss.dto.response.BossBattleDamageLogResponse;
import com.nyamnyam.coach.boss.dto.response.BossBattleDetailResponse;
import com.nyamnyam.coach.boss.dto.response.BossBattleHistoryResponse;
import com.nyamnyam.coach.boss.dto.response.BossBattleHpResponse;
import com.nyamnyam.coach.boss.dto.response.BossBattleSummaryResponse;
import com.nyamnyam.coach.boss.dto.response.CurrentBossBattleResponse;
import com.nyamnyam.coach.boss.entity.BossBattle;
import com.nyamnyam.coach.boss.entity.BossBattleCondition;
import com.nyamnyam.coach.boss.entity.BossBattleParticipant;
import com.nyamnyam.coach.boss.entity.BossBattleParticipantStatus;
import com.nyamnyam.coach.boss.entity.BossBattleStatus;
import com.nyamnyam.coach.boss.entity.BossDifficulty;
import com.nyamnyam.coach.boss.entity.BossStatus;
import com.nyamnyam.coach.boss.repository.BossBattleParticipantRepository;
import com.nyamnyam.coach.boss.repository.BossBattleRepository;
import com.nyamnyam.coach.boss.repository.row.BossBattleConditionRow;
import com.nyamnyam.coach.boss.repository.row.BossBattleDamageLogRow;
import com.nyamnyam.coach.boss.repository.row.BossBattleParticipantCountRow;
import com.nyamnyam.coach.boss.repository.row.BossBattleParticipantRow;
import com.nyamnyam.coach.boss.repository.row.BossBattleRow;
import com.nyamnyam.coach.boss.repository.row.BossCommonConditionRow;
import com.nyamnyam.coach.boss.repository.row.BossRow;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.BossErrorCode;
import com.nyamnyam.coach.guild.service.GuildValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BossBattleService {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 50;
    private static final int RECENT_DAMAGE_LOG_LIMIT = 10;

    private final BossBattleRepository bossBattleRepository;
    private final BossBattleParticipantRepository bossBattleParticipantRepository;
    private final GuildValidator guildValidator;

    public BossBattleService(
            BossBattleRepository bossBattleRepository,
            BossBattleParticipantRepository bossBattleParticipantRepository,
            GuildValidator guildValidator
    ) {
        this.bossBattleRepository = bossBattleRepository;
        this.bossBattleParticipantRepository = bossBattleParticipantRepository;
        this.guildValidator = guildValidator;
    }

    @Transactional
    public BossBattleCreateResponse createBossBattle(
            Long guildId,
            Long userId,
            BossBattleCreateRequest request
    ) {
        guildValidator.validateGuildOwner(guildId, userId);
        BossRow boss = bossBattleRepository.findActiveBossById(request.bossId())
                .orElseThrow(() -> new BusinessException(BossErrorCode.BOSS_NOT_FOUND));
        if (!BossStatus.ACTIVE.name().equals(boss.getStatus())) {
            throw new BusinessException(BossErrorCode.BOSS_INACTIVE);
        }

        Long currentSeasonId = bossBattleRepository.findCurrentSeasonId()
                .orElseThrow(() -> new BusinessException(BossErrorCode.CURRENT_BOSS_NOT_FOUND));
        if (!currentSeasonId.equals(boss.getSeasonId())) {
            throw new BusinessException(BossErrorCode.BOSS_NOT_CURRENT_SEASON);
        }
        if (bossBattleRepository.existsInProgressBattleByGuildId(guildId)) {
            throw new BusinessException(BossErrorCode.BOSS_BATTLE_ALREADY_IN_PROGRESS);
        }
        if (bossBattleRepository.existsBattleByGuildIdAndSeasonId(guildId, boss.getSeasonId())) {
            throw new BusinessException(BossErrorCode.BOSS_BATTLE_ALREADY_EXISTS_IN_SEASON);
        }

        List<BossBattleParticipantRow> participantSnapshots =
                bossBattleParticipantRepository.findActiveGuildMembersForBattleSnapshot(guildId);
        if (participantSnapshots.isEmpty()) {
            throw new BusinessException(BossErrorCode.ACTIVE_GUILD_MEMBER_NOT_FOUND);
        }
        BossDifficulty difficulty = parseDifficulty(boss.getDifficulty());
        int actualMaxHp = difficulty.calculateMaxHp(participantSnapshots.size());

        BossBattle battle = new BossBattle();
        battle.setGuildId(guildId);
        battle.setBossId(boss.getBossId());
        battle.setSeasonId(boss.getSeasonId());
        battle.setStatus(BossBattleStatus.IN_PROGRESS.name());
        battle.setMaxHp(actualMaxHp);
        battle.setCurrentHp(actualMaxHp);
        battle.setTotalDamage(0);
        bossBattleRepository.insertBossBattle(battle);

        List<BossCommonConditionRow> conditions = bossBattleRepository.findBossCommonConditionsByBossId(boss.getBossId());
        int commonTotalDamage = difficulty.calculateCommonConditionTotalDamage(actualMaxHp);
        for (int index = 0; index < conditions.size(); index++) {
            BossCommonConditionRow condition = conditions.get(index);
            int conditionDamage = calculateDistributedDamage(commonTotalDamage, conditions.size(), index);
            bossBattleRepository.insertBossBattleCondition(toBattleCondition(
                    battle.getBattleId(),
                    condition,
                    conditionDamage
            ));
        }
        participantSnapshots.forEach(snapshot ->
                bossBattleParticipantRepository.insertBossBattleParticipant(toParticipant(
                        battle.getBattleId(),
                        snapshot
                ))
        );

        BossBattleRow savedBattle = findBattle(battle.getBattleId());
        return new BossBattleCreateResponse(
                savedBattle.getBattleId(),
                savedBattle.getGuildId(),
                savedBattle.getBossId(),
                savedBattle.getSeasonId(),
                savedBattle.getBossName(),
                savedBattle.getDifficulty(),
                savedBattle.getStatus(),
                savedBattle.getMaxHp(),
                savedBattle.getCurrentHp(),
                savedBattle.getStartedAt()
        );
    }

    @Transactional(readOnly = true)
    public CurrentBossBattleResponse getCurrentBossBattle(Long guildId, Long userId) {
        guildValidator.validateGuildMember(guildId, userId);
        return new CurrentBossBattleResponse(
                bossBattleRepository.findCurrentBattleByGuildId(guildId)
                        .map(row -> toSummaryResponse(row, userId))
                        .orElse(null)
        );
    }

    @Transactional(readOnly = true)
    public BossBattleDetailResponse getBossBattleDetail(Long battleId, Long userId) {
        BossBattleRow battle = findBattle(battleId);
        guildValidator.validateGuildMember(battle.getGuildId(), userId);
        List<BossBattleConditionResponse> conditions = bossBattleRepository.findBattleConditionsByBattleId(battleId)
                .stream()
                .map(this::toConditionResponse)
                .toList();
        List<BossBattleDamageLogResponse> damageLogs = bossBattleRepository
                .findRecentDamageLogsByBattleId(battleId, RECENT_DAMAGE_LOG_LIMIT)
                .stream()
                .map(this::toDamageLogResponse)
                .toList();

        BossBattleParticipantCountRow participantCounts =
                bossBattleParticipantRepository.countParticipantsByBattleId(battleId);
        if (participantCounts == null) {
            participantCounts = new BossBattleParticipantCountRow();
        }

        return new BossBattleDetailResponse(
                battle.getBattleId(),
                battle.getGuildId(),
                battle.getGuildName(),
                battle.getBossId(),
                battle.getBossName(),
                battle.getDifficulty(),
                battle.getBossImageUrl(),
                battle.getStatus(),
                battle.getMaxHp(),
                battle.getCurrentHp(),
                battle.getTotalDamage(),
                calculateHpRate(battle.getCurrentHp(), battle.getMaxHp()),
                battle.getStartedAt(),
                battle.getEndedAt(),
                battle.getEndsAt(),
                conditions,
                damageLogs,
                defaultValue(participantCounts.getParticipantCount()),
                defaultValue(participantCounts.getActiveParticipantCount()),
                defaultValue(participantCounts.getLeftParticipantCount()),
                bossBattleRepository.existsBossBattleRewardClaim(battleId, userId)
        );
    }

    @Transactional(readOnly = true)
    public BossBattleHpResponse getBossBattleHp(Long battleId, Long userId) {
        BossBattleRow battle = bossBattleRepository.findBattleHpById(battleId)
                .orElseThrow(() -> new BusinessException(BossErrorCode.BOSS_BATTLE_NOT_FOUND));
        guildValidator.validateGuildMember(battle.getGuildId(), userId);
        return new BossBattleHpResponse(
                battle.getBattleId(),
                battle.getStatus(),
                battle.getMaxHp(),
                battle.getCurrentHp(),
                battle.getTotalDamage(),
                calculateHpRate(battle.getCurrentHp(), battle.getMaxHp())
        );
    }

    @Transactional(readOnly = true)
    public BossBattleHistoryResponse getBossBattleHistory(
            Long guildId,
            Long userId,
            Integer page,
            Integer size
    ) {
        guildValidator.validateGuildMember(guildId, userId);
        int normalizedPage = normalizePage(page);
        int normalizedSize = normalizeSize(size);
        int limit = normalizedSize + 1;
        int offset = normalizedPage * normalizedSize;
        List<BossBattleRow> rows = bossBattleRepository.findBattleHistoryByGuildId(guildId, limit, offset);
        boolean hasNext = rows.size() > normalizedSize;
        List<BossBattleSummaryResponse> battles = rows
                .stream()
                .limit(normalizedSize)
                .map(row -> toSummaryResponse(row, userId))
                .toList();

        return new BossBattleHistoryResponse(
                battles,
                normalizedPage,
                normalizedSize,
                hasNext
        );
    }

    private BossBattleRow findBattle(Long battleId) {
        return bossBattleRepository.findBattleDetailById(battleId)
                .orElseThrow(() -> new BusinessException(BossErrorCode.BOSS_BATTLE_NOT_FOUND));
    }

    private BossBattleCondition toBattleCondition(
            Long battleId,
            BossCommonConditionRow condition,
            int damage
    ) {
        BossBattleCondition battleCondition = new BossBattleCondition();
        battleCondition.setBattleId(battleId);
        battleCondition.setConditionId(condition.getConditionId());
        battleCondition.setConditionTemplateId(condition.getConditionTemplateId());
        battleCondition.setTitle(condition.getTitle());
        battleCondition.setDescription(condition.getDescription());
        battleCondition.setTargetType(condition.getTargetType());
        battleCondition.setConditionCategory(condition.getConditionCategory());
        battleCondition.setMetricType(condition.getMetricType());
        battleCondition.setComparisonType(condition.getComparisonType());
        battleCondition.setAggregationType(condition.getAggregationType());
        battleCondition.setEvaluationScope(condition.getEvaluationScope());
        battleCondition.setThresholdValue(condition.getThresholdValue());
        battleCondition.setThresholdMinValue(condition.getThresholdMinValue());
        battleCondition.setThresholdMaxValue(condition.getThresholdMaxValue());
        battleCondition.setThresholdUnit(condition.getThresholdUnit());
        battleCondition.setTargetValue(condition.getTargetValue());
        battleCondition.setRequiredDays(condition.getRequiredDays());
        battleCondition.setCurrentValue(0);
        battleCondition.setDamage(damage);
        battleCondition.setCompleted(false);
        battleCondition.setUnit(condition.getUnit());
        battleCondition.setSortOrder(condition.getSortOrder());
        battleCondition.setRequiredForClear(condition.getRequiredForClear());
        battleCondition.setVerificationSupported(condition.getVerificationSupported());
        return battleCondition;
    }

    private BossBattleParticipant toParticipant(Long battleId, BossBattleParticipantRow row) {
        BossBattleParticipant participant = new BossBattleParticipant();
        participant.setBattleId(battleId);
        participant.setGuildId(row.getGuildId());
        participant.setUserId(row.getUserId());
        participant.setGuildMemberId(row.getGuildMemberId());
        participant.setRoleAtStart(row.getRoleAtStart());
        participant.setStatus(BossBattleParticipantStatus.ACTIVE.name());
        participant.setSnapshotNickname(row.getSnapshotNickname());
        participant.setSnapshotProfileImageUrl(row.getSnapshotProfileImageUrl());
        participant.setSnapshotCharacterId(row.getSnapshotCharacterId());
        participant.setSnapshotCharacterName(row.getSnapshotCharacterName());
        participant.setSnapshotCharacterLevel(row.getSnapshotCharacterLevel());
        return participant;
    }

    private BossBattleSummaryResponse toSummaryResponse(BossBattleRow row, Long userId) {
        return new BossBattleSummaryResponse(
                row.getBattleId(),
                row.getGuildId(),
                row.getBossId(),
                row.getBossName(),
                row.getDifficulty(),
                row.getBossImageUrl(),
                row.getStatus(),
                row.getMaxHp(),
                row.getCurrentHp(),
                row.getTotalDamage(),
                row.getStartedAt(),
                row.getEndedAt(),
                row.getEndsAt(),
                bossBattleRepository.existsBossBattleRewardClaim(row.getBattleId(), userId)
        );
    }

    private BossBattleConditionResponse toConditionResponse(BossBattleConditionRow row) {
        return new BossBattleConditionResponse(
                row.getBattleConditionId(),
                row.getTitle(),
                row.getDescription(),
                row.getTargetType(),
                row.getThresholdValue(),
                row.getThresholdUnit(),
                row.getTargetValue(),
                row.getRequiredDays(),
                row.getCurrentValue(),
                row.getDamage(),
                row.getUnit(),
                row.getCompleted(),
                row.getSortOrder()
        );
    }

    private BossBattleDamageLogResponse toDamageLogResponse(BossBattleDamageLogRow row) {
        return new BossBattleDamageLogResponse(
                row.getDamageLogId(),
                row.getUserId(),
                row.getNickname(),
                row.getDamage(),
                row.getSourceType(),
                row.getDescription(),
                row.getCreatedAt()
        );
    }

    private Double calculateHpRate(Integer currentHp, Integer maxHp) {
        if (maxHp == null || maxHp == 0 || currentHp == null) {
            return 0.0;
        }
        return Math.round((currentHp * 10000.0 / maxHp)) / 100.0;
    }

    private BossDifficulty parseDifficulty(String difficulty) {
        try {
            return BossDifficulty.from(difficulty);
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new BusinessException(BossErrorCode.BOSS_DIFFICULTY_INVALID);
        }
    }

    private int calculateDistributedDamage(int totalDamage, int count, int index) {
        if (count <= 0) {
            return 0;
        }
        int baseDamage = totalDamage / count;
        int remainder = totalDamage % count;
        return index < remainder ? baseDamage + 1 : baseDamage;
    }

    private int defaultValue(Integer value) {
        return value == null ? 0 : value;
    }

    private int normalizePage(Integer page) {
        return page == null || page < 0 ? DEFAULT_PAGE : page;
    }

    private int normalizeSize(Integer size) {
        if (size == null || size <= 0) {
            return DEFAULT_SIZE;
        }
        return Math.min(size, MAX_PAGE_SIZE);
    }
}
