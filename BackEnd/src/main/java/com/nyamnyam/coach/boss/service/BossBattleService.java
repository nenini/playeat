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
import com.nyamnyam.coach.boss.entity.BossBattleStatus;
import com.nyamnyam.coach.boss.entity.BossStatus;
import com.nyamnyam.coach.boss.repository.BossBattleRepository;
import com.nyamnyam.coach.boss.repository.row.BossBattleConditionRow;
import com.nyamnyam.coach.boss.repository.row.BossBattleDamageLogRow;
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
    private final GuildValidator guildValidator;

    public BossBattleService(
            BossBattleRepository bossBattleRepository,
            GuildValidator guildValidator
    ) {
        this.bossBattleRepository = bossBattleRepository;
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

        BossBattle battle = new BossBattle();
        battle.setGuildId(guildId);
        battle.setBossId(boss.getBossId());
        battle.setSeasonId(boss.getSeasonId());
        battle.setStatus(BossBattleStatus.IN_PROGRESS.name());
        battle.setMaxHp(boss.getMaxHp());
        battle.setCurrentHp(boss.getMaxHp());
        battle.setTotalDamage(0);
        bossBattleRepository.insertBossBattle(battle);

        List<BossCommonConditionRow> conditions = bossBattleRepository.findBossCommonConditionsByBossId(boss.getBossId());
        for (BossCommonConditionRow condition : conditions) {
            bossBattleRepository.insertBossBattleCondition(toBattleCondition(battle.getBattleId(), condition));
        }

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
                        .map(this::toSummaryResponse)
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
                damageLogs
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
                .map(this::toSummaryResponse)
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

    private BossBattleCondition toBattleCondition(Long battleId, BossCommonConditionRow condition) {
        BossBattleCondition battleCondition = new BossBattleCondition();
        battleCondition.setBattleId(battleId);
        battleCondition.setConditionId(condition.getConditionId());
        battleCondition.setTitle(condition.getTitle());
        battleCondition.setDescription(condition.getDescription());
        battleCondition.setTargetType(condition.getTargetType());
        battleCondition.setThresholdValue(condition.getThresholdValue());
        battleCondition.setThresholdUnit(condition.getThresholdUnit());
        battleCondition.setTargetValue(condition.getTargetValue());
        battleCondition.setRequiredDays(condition.getRequiredDays());
        battleCondition.setCurrentValue(0);
        battleCondition.setCompleted(false);
        battleCondition.setUnit(condition.getUnit());
        battleCondition.setSortOrder(condition.getSortOrder());
        return battleCondition;
    }

    private BossBattleSummaryResponse toSummaryResponse(BossBattleRow row) {
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
                row.getEndsAt()
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
