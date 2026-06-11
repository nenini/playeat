package com.nyamnyam.coach.boss.service;

import com.nyamnyam.coach.boss.dto.response.BossBattleQuestListResponse;
import com.nyamnyam.coach.boss.dto.response.GeneratedQuestResponse;
import com.nyamnyam.coach.boss.dto.response.MyQuestResponse;
import com.nyamnyam.coach.boss.dto.response.QuestContributionListResponse;
import com.nyamnyam.coach.boss.dto.response.QuestContributionResponse;
import com.nyamnyam.coach.boss.dto.response.QuestDetailResponse;
import com.nyamnyam.coach.boss.dto.response.QuestGenerateResponse;
import com.nyamnyam.coach.boss.dto.response.QuestSummaryResponse;
import com.nyamnyam.coach.boss.entity.BossBattleStatus;
import com.nyamnyam.coach.boss.entity.Quest;
import com.nyamnyam.coach.boss.repository.QuestRepository;
import com.nyamnyam.coach.boss.repository.row.QuestBattleRow;
import com.nyamnyam.coach.boss.repository.row.QuestContributionRow;
import com.nyamnyam.coach.boss.repository.row.QuestGuildMemberRow;
import com.nyamnyam.coach.boss.repository.row.QuestRow;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.BossErrorCode;
import com.nyamnyam.coach.global.exception.errorcode.GuildErrorCode;
import com.nyamnyam.coach.global.exception.errorcode.QuestErrorCode;
import com.nyamnyam.coach.guild.entity.GuildRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestService {

    private final QuestRepository questRepository;
    private final QuestGenerator questGenerator;

    public QuestService(
            QuestRepository questRepository,
            QuestGenerator questGenerator
    ) {
        this.questRepository = questRepository;
        this.questGenerator = questGenerator;
    }

    @Transactional(readOnly = true)
    public BossBattleQuestListResponse getBattleQuests(Long battleId, Long userId) {
        QuestBattleRow battle = findBattle(battleId);
        validateBattleMember(battle.getGuildId(), userId);
        return new BossBattleQuestListResponse(
                battle.getBattleId(),
                battle.getGuildId(),
                questRepository.findQuestsByBattleId(battleId, userId)
                        .stream()
                        .map(this::toSummaryResponse)
                        .toList()
        );
    }

    @Transactional(readOnly = true)
    public MyQuestResponse getMyQuest(Long battleId, Long userId) {
        QuestBattleRow battle = findBattle(battleId);
        validateBattleMember(battle.getGuildId(), userId);
        return new MyQuestResponse(
                questRepository.findMyQuestByBattleId(battleId, userId)
                        .map(this::toDetailResponse)
                        .orElse(null)
        );
    }

    @Transactional(readOnly = true)
    public QuestDetailResponse getQuestDetail(Long questId, Long userId) {
        QuestRow quest = questRepository.findQuestDetailById(questId, userId)
                .orElseThrow(() -> new BusinessException(QuestErrorCode.QUEST_NOT_FOUND));
        validateBattleMember(quest.getGuildId(), userId);
        return toDetailResponse(quest);
    }

    @Transactional
    public QuestGenerateResponse generateQuests(Long battleId, Long userId) {
        QuestBattleRow battle = findBattle(battleId);
        if (!BossBattleStatus.IN_PROGRESS.name().equals(battle.getStatus())) {
            throw new BusinessException(BossErrorCode.BOSS_BATTLE_NOT_IN_PROGRESS);
        }
        validateBattleOwner(battle.getGuildId(), userId);

        List<QuestGuildMemberRow> members = questRepository.findActiveGuildMembers(battle.getGuildId());
        if (members.isEmpty()) {
            throw new BusinessException(QuestErrorCode.ACTIVE_GUILD_MEMBER_NOT_FOUND);
        }

        int skippedCount = 0;
        List<GeneratedQuestResponse> generatedQuests = new ArrayList<>();
        for (QuestGuildMemberRow member : members) {
            if (questRepository.existsQuestByBattleIdAndUserId(battleId, member.getUserId())) {
                skippedCount++;
                continue;
            }
            Quest quest = questGenerator.generatePersonalQuest(battle, member, members.size());
            questRepository.insertQuest(quest);
            generatedQuests.add(toGeneratedResponse(quest, member));
        }

        return new QuestGenerateResponse(
                battle.getBattleId(),
                battle.getGuildId(),
                generatedQuests.size(),
                skippedCount,
                generatedQuests
        );
    }

    @Transactional(readOnly = true)
    public QuestContributionListResponse getQuestContributions(Long battleId, Long userId) {
        QuestBattleRow battle = findBattle(battleId);
        validateBattleMember(battle.getGuildId(), userId);
        return new QuestContributionListResponse(
                battle.getBattleId(),
                battle.getGuildId(),
                questRepository.findQuestContributionsByBattleId(battleId, userId)
                        .stream()
                        .map(this::toContributionResponse)
                        .toList()
        );
    }

    private QuestBattleRow findBattle(Long battleId) {
        return questRepository.findBattleById(battleId)
                .orElseThrow(() -> new BusinessException(BossErrorCode.BOSS_BATTLE_NOT_FOUND));
    }

    private void validateBattleMember(Long guildId, Long userId) {
        if (!questRepository.existsActiveGuildMember(guildId, userId)) {
            throw new BusinessException(BossErrorCode.BOSS_BATTLE_ACCESS_DENIED);
        }
    }

    private void validateBattleOwner(Long guildId, Long userId) {
        validateBattleMember(guildId, userId);
        String role = questRepository.findGuildRole(guildId, userId)
                .orElseThrow(() -> new BusinessException(GuildErrorCode.GUILD_ACCESS_DENIED));
        if (!GuildRole.OWNER.name().equals(role)) {
            throw new BusinessException(GuildErrorCode.GUILD_OWNER_ONLY);
        }
    }

    private QuestSummaryResponse toSummaryResponse(QuestRow row) {
        return new QuestSummaryResponse(
                row.getQuestId(),
                row.getUserId(),
                row.getNickname(),
                row.getProfileImageUrl(),
                row.getCharacterId(),
                row.getCharacterName(),
                row.getCharacterLevel(),
                row.getTitle(),
                row.getDescription(),
                row.getQuestType(),
                row.getTargetValue(),
                row.getCurrentValue(),
                row.getUnit(),
                row.getDamage(),
                row.getRewardExp(),
                row.getRewardCoin(),
                row.getStatus(),
                row.getIsMe(),
                row.getCreatedAt(),
                row.getCompletedAt()
        );
    }

    private QuestDetailResponse toDetailResponse(QuestRow row) {
        return new QuestDetailResponse(
                row.getQuestId(),
                row.getBattleId(),
                row.getGuildId(),
                row.getUserId(),
                row.getNickname(),
                row.getProfileImageUrl(),
                row.getCharacterId(),
                row.getCharacterName(),
                row.getCharacterLevel(),
                row.getTitle(),
                row.getDescription(),
                row.getQuestType(),
                row.getTargetValue(),
                row.getCurrentValue(),
                row.getUnit(),
                row.getDamage(),
                row.getRewardExp(),
                row.getRewardCoin(),
                row.getStatus(),
                row.getSourceType(),
                row.getIsMe(),
                row.getCreatedAt(),
                row.getCompletedAt(),
                row.getRewardedAt()
        );
    }

    private GeneratedQuestResponse toGeneratedResponse(Quest quest, QuestGuildMemberRow member) {
        return new GeneratedQuestResponse(
                quest.getQuestId(),
                member.getUserId(),
                member.getNickname(),
                quest.getTitle(),
                quest.getQuestType(),
                quest.getTargetValue(),
                quest.getUnit(),
                quest.getDamage(),
                quest.getRewardExp(),
                quest.getRewardCoin(),
                quest.getStatus()
        );
    }

    private QuestContributionResponse toContributionResponse(QuestContributionRow row) {
        return new QuestContributionResponse(
                row.getUserId(),
                row.getNickname(),
                row.getProfileImageUrl(),
                row.getCharacterName(),
                row.getCharacterLevel(),
                row.getTotalQuestCount(),
                row.getCompletedQuestCount(),
                row.getTotalDamage(),
                row.getExpectedDamage(),
                row.getIsMe()
        );
    }
}
