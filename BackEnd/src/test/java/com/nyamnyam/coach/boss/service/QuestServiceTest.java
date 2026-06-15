package com.nyamnyam.coach.boss.service;

import com.nyamnyam.coach.quest.dto.response.BossBattleQuestListResponse;
import com.nyamnyam.coach.quest.dto.response.MyQuestResponse;
import com.nyamnyam.coach.quest.dto.response.QuestContributionListResponse;
import com.nyamnyam.coach.quest.dto.response.QuestDetailResponse;
import com.nyamnyam.coach.quest.dto.response.QuestGenerateResponse;
import com.nyamnyam.coach.quest.entity.Quest;
import com.nyamnyam.coach.quest.entity.QuestTemplate;
import com.nyamnyam.coach.boss.repository.BossBattleParticipantRepository;
import com.nyamnyam.coach.quest.repository.QuestRepository;
import com.nyamnyam.coach.quest.repository.QuestTemplateRepository;
import com.nyamnyam.coach.boss.repository.row.BossBattleParticipantRow;
import com.nyamnyam.coach.quest.repository.row.QuestBattleRow;
import com.nyamnyam.coach.quest.repository.row.QuestContributionRow;
import com.nyamnyam.coach.quest.repository.row.QuestRow;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.BossErrorCode;
import com.nyamnyam.coach.global.exception.errorcode.GuildErrorCode;
import com.nyamnyam.coach.quest.service.PlaceholderQuestGenerator;
import com.nyamnyam.coach.quest.service.QuestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuestServiceTest {

    @Mock
    private QuestRepository questRepository;

    @Mock
    private BossBattleParticipantRepository bossBattleParticipantRepository;

    @Mock
    private QuestTemplateRepository questTemplateRepository;

    private QuestService questService;

    @BeforeEach
    void setUp() {
        questService = new QuestService(
                questRepository,
                bossBattleParticipantRepository,
                new PlaceholderQuestGenerator(questTemplateRepository)
        );
    }

    @Test
    @DisplayName("길드장은 보스전 길드원별 퀘스트를 생성할 수 있다")
    void generateQuests() {
        when(questRepository.findBattleById(500L)).thenReturn(Optional.of(battle("IN_PROGRESS")));
        when(questTemplateRepository.findDefaultTemplate()).thenReturn(Optional.of(defaultTemplate()));
        when(questRepository.existsActiveGuildMember(100L, 1L)).thenReturn(true);
        when(questRepository.findGuildRole(100L, 1L)).thenReturn(Optional.of("OWNER"));
        when(bossBattleParticipantRepository.findActiveParticipantsByBattleId(500L))
                .thenReturn(List.of(participant(1L, "예린"), participant(2L, "민수")));
        when(questRepository.existsQuestByBattleIdAndUserId(500L, 1L)).thenReturn(false);
        when(questRepository.existsQuestByBattleIdAndUserId(500L, 2L)).thenReturn(false);
        doAnswer(invocation -> {
            Quest quest = invocation.getArgument(0);
            quest.setQuestId(quest.getUserId() + 1000);
            return null;
        }).when(questRepository).insertQuest(any(Quest.class));

        QuestGenerateResponse response = questService.generateQuests(500L, 1L);

        assertThat(response.createdCount()).isEqualTo(2);
        assertThat(response.skippedCount()).isZero();
        assertThat(response.quests()).extracting("title").containsOnly("오늘 식단 1회 이상 기록하기");
        assertThat(response.quests()).extracting("damage").containsExactly(100, 100);
        verify(questRepository, times(2)).insertQuest(any(Quest.class));
    }

    @Test
    @DisplayName("난이도별 개인 퀘스트 damage는 personalDamageRatio 기준으로 분배된다")
    void generateQuestsDamageByDifficulty() {
        assertQuestDamage("EASY", 1000, List.of(100, 100, 100));
        assertQuestDamage("NORMAL", 1000, List.of(100, 100, 100));
        assertQuestDamage("HARD", 1000, List.of(100, 100, 100));
    }

    @Test
    @DisplayName("일반 멤버는 퀘스트를 생성할 수 없다")
    void generateQuestsOwnerOnly() {
        when(questRepository.findBattleById(500L)).thenReturn(Optional.of(battle("IN_PROGRESS")));
        when(questRepository.existsActiveGuildMember(100L, 2L)).thenReturn(true);
        when(questRepository.findGuildRole(100L, 2L)).thenReturn(Optional.of("MEMBER"));

        assertThatThrownBy(() -> questService.generateQuests(500L, 2L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(GuildErrorCode.GUILD_OWNER_ONLY);
    }

    @Test
    @DisplayName("진행 중이 아닌 보스전에는 퀘스트를 생성할 수 없다")
    void generateQuestsNotInProgress() {
        when(questRepository.findBattleById(500L)).thenReturn(Optional.of(battle("DEFEATED")));

        assertThatThrownBy(() -> questService.generateQuests(500L, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(BossErrorCode.BOSS_BATTLE_NOT_IN_PROGRESS);
    }

    @Test
    @DisplayName("이미 퀘스트가 있는 회원은 중복 생성하지 않는다")
    void generateQuestsSkipExisting() {
        when(questRepository.findBattleById(500L)).thenReturn(Optional.of(battle("IN_PROGRESS")));
        when(questTemplateRepository.findDefaultTemplate()).thenReturn(Optional.of(defaultTemplate()));
        when(questRepository.existsActiveGuildMember(100L, 1L)).thenReturn(true);
        when(questRepository.findGuildRole(100L, 1L)).thenReturn(Optional.of("OWNER"));
        when(bossBattleParticipantRepository.findActiveParticipantsByBattleId(500L))
                .thenReturn(List.of(participant(1L, "예린"), participant(2L, "민수")));
        when(questRepository.existsQuestByBattleIdAndUserId(500L, 1L)).thenReturn(true);
        when(questRepository.existsQuestByBattleIdAndUserId(500L, 2L)).thenReturn(false);
        doAnswer(invocation -> {
            Quest quest = invocation.getArgument(0);
            quest.setQuestId(1002L);
            return null;
        }).when(questRepository).insertQuest(any(Quest.class));

        QuestGenerateResponse response = questService.generateQuests(500L, 1L);

        assertThat(response.createdCount()).isEqualTo(1);
        assertThat(response.skippedCount()).isEqualTo(1);
        assertThat(response.quests().get(0).userId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("보스전의 전체 퀘스트와 내 퀘스트를 조회할 수 있다")
    void getQuestsAndMyQuest() {
        when(questRepository.findBattleById(500L)).thenReturn(Optional.of(battle("IN_PROGRESS")));
        when(questRepository.existsActiveGuildMember(100L, 1L)).thenReturn(true);
        when(questRepository.findQuestsByBattleId(500L, 1L)).thenReturn(List.of(questRow(1L, true)));
        when(questRepository.findMyQuestByBattleId(500L, 1L)).thenReturn(Optional.of(questRow(1L, true)));

        BossBattleQuestListResponse listResponse = questService.getBattleQuests(500L, 1L);
        MyQuestResponse myQuestResponse = questService.getMyQuest(500L, 1L);

        assertThat(listResponse.quests()).hasSize(1);
        assertThat(myQuestResponse.quest()).isNotNull();
        assertThat(myQuestResponse.quest().isMe()).isTrue();
    }

    @Test
    @DisplayName("퀘스트가 없으면 내 퀘스트 조회에서 null을 반환한다")
    void getMyQuestNull() {
        when(questRepository.findBattleById(500L)).thenReturn(Optional.of(battle("IN_PROGRESS")));
        when(questRepository.existsActiveGuildMember(100L, 1L)).thenReturn(true);
        when(questRepository.findMyQuestByBattleId(500L, 1L)).thenReturn(Optional.empty());

        MyQuestResponse response = questService.getMyQuest(500L, 1L);

        assertThat(response.quest()).isNull();
    }

    @Test
    @DisplayName("퀘스트 상세를 조회할 수 있다")
    void getQuestDetail() {
        when(questRepository.findQuestDetailById(1001L, 1L)).thenReturn(Optional.of(questRow(1L, true)));
        when(questRepository.existsActiveGuildMember(100L, 1L)).thenReturn(true);

        QuestDetailResponse response = questService.getQuestDetail(1001L, 1L);

        assertThat(response.questId()).isEqualTo(1001L);
        assertThat(response.title()).isEqualTo("오늘 식단 기록하기");
    }

    @Test
    @DisplayName("길드원이 아닌 사용자는 퀘스트를 조회할 수 없다")
    void getQuestAccessDenied() {
        when(questRepository.findBattleById(500L)).thenReturn(Optional.of(battle("IN_PROGRESS")));
        when(questRepository.existsActiveGuildMember(100L, 9L)).thenReturn(false);

        assertThatThrownBy(() -> questService.getBattleQuests(500L, 9L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(BossErrorCode.BOSS_BATTLE_ACCESS_DENIED);
    }

    @Test
    @DisplayName("길드원별 퀘스트 기여도를 조회할 수 있다")
    void getQuestContributions() {
        when(questRepository.findBattleById(500L)).thenReturn(Optional.of(battle("IN_PROGRESS")));
        when(questRepository.existsActiveGuildMember(100L, 1L)).thenReturn(true);
        when(questRepository.findQuestContributionsByBattleId(500L, 1L)).thenReturn(List.of(contribution()));

        QuestContributionListResponse response = questService.getQuestContributions(500L, 1L);

        assertThat(response.contributions()).hasSize(1);
        assertThat(response.contributions().get(0).expectedDamage()).isEqualTo(100);
    }

    private QuestBattleRow battle(String status) {
        return battle(status, "EASY", 1000);
    }

    private QuestBattleRow battle(String status, String difficulty, int maxHp) {
        QuestBattleRow row = new QuestBattleRow();
        row.setBattleId(500L);
        row.setGuildId(100L);
        row.setStatus(status);
        row.setMaxHp(maxHp);
        row.setDifficulty(difficulty);
        return row;
    }

    private BossBattleParticipantRow participant(Long userId, String nickname) {
        BossBattleParticipantRow row = new BossBattleParticipantRow();
        row.setParticipantId(userId + 10);
        row.setBattleId(500L);
        row.setGuildId(100L);
        row.setUserId(userId);
        row.setGuildMemberId(userId + 20);
        row.setRoleAtStart(userId == 1L ? "OWNER" : "MEMBER");
        row.setStatus("ACTIVE");
        row.setSnapshotNickname(nickname);
        row.setSnapshotProfileImageUrl("https://example.com/profile.png");
        row.setSnapshotCharacterId(userId + 100);
        row.setSnapshotCharacterName("냠냠이");
        row.setSnapshotCharacterLevel(7);
        return row;
    }

    private QuestRow questRow(Long userId, Boolean isMe) {
        QuestRow row = new QuestRow();
        row.setQuestId(1001L);
        row.setBattleId(500L);
        row.setGuildId(100L);
        row.setUserId(userId);
        row.setNickname("예린");
        row.setCharacterId(101L);
        row.setCharacterName("냠냠이");
        row.setCharacterLevel(7);
        row.setTitle("오늘 식단 기록하기");
        row.setDescription("오늘 하루 식단을 1회 이상 기록하세요.");
        row.setQuestType("RECORD_DIET");
        row.setTargetValue(1);
        row.setCurrentValue(0);
        row.setUnit("회");
        row.setDamage(100);
        row.setRewardExp(30);
        row.setRewardCoin(10);
        row.setStatus("IN_PROGRESS");
        row.setSourceType("PLACEHOLDER");
        row.setIsMe(isMe);
        row.setCreatedAt(LocalDateTime.of(2026, 6, 10, 10, 0));
        return row;
    }

    private QuestContributionRow contribution() {
        QuestContributionRow row = new QuestContributionRow();
        row.setUserId(1L);
        row.setNickname("예린");
        row.setCharacterName("냠냠이");
        row.setCharacterLevel(7);
        row.setTotalQuestCount(1);
        row.setCompletedQuestCount(0);
        row.setTotalDamage(0);
        row.setExpectedDamage(100);
        row.setIsMe(true);
        return row;
    }

    private void assertQuestDamage(String difficulty, int maxHp, List<Integer> expectedDamages) {
        QuestService localQuestService = new QuestService(
                questRepository,
                bossBattleParticipantRepository,
                new PlaceholderQuestGenerator(questTemplateRepository)
        );
        when(questRepository.findBattleById(500L)).thenReturn(Optional.of(battle("IN_PROGRESS", difficulty, maxHp)));
        when(questTemplateRepository.findDefaultTemplate()).thenReturn(Optional.of(defaultTemplate()));
        when(questRepository.existsActiveGuildMember(100L, 1L)).thenReturn(true);
        when(questRepository.findGuildRole(100L, 1L)).thenReturn(Optional.of("OWNER"));
        when(bossBattleParticipantRepository.findActiveParticipantsByBattleId(500L)).thenReturn(List.of(
                participant(1L, "예린"),
                participant(2L, "민수"),
                participant(3L, "지민")
        ));
        when(questRepository.existsQuestByBattleIdAndUserId(500L, 1L)).thenReturn(false);
        when(questRepository.existsQuestByBattleIdAndUserId(500L, 2L)).thenReturn(false);
        when(questRepository.existsQuestByBattleIdAndUserId(500L, 3L)).thenReturn(false);
        doAnswer(invocation -> {
            Quest quest = invocation.getArgument(0);
            quest.setQuestId(quest.getUserId() + 1000);
            return null;
        }).when(questRepository).insertQuest(any(Quest.class));

        QuestGenerateResponse response = localQuestService.generateQuests(500L, 1L);

        assertThat(response.quests()).extracting("damage").containsExactlyElementsOf(expectedDamages);
        int totalDamage = response.quests().stream()
                .mapToInt(generatedQuest -> generatedQuest.damage())
                .sum();
        assertThat(totalDamage).isEqualTo(expectedDamages.stream().mapToInt(Integer::intValue).sum());
    }

    private QuestTemplate defaultTemplate() {
        QuestTemplate template = new QuestTemplate();
        template.setTemplateId(1L);
        template.setTitle("오늘 식단 1회 이상 기록하기");
        template.setDescription("오늘 하루 식단을 1회 이상 기록하세요.");
        template.setQuestType("RECORD_DIET");
        template.setConditionCategory("DIET_RECORD");
        template.setMetricType("DIET_RECORD_COUNT");
        template.setComparisonType("GREATER_THAN_OR_EQUAL");
        template.setAggregationType("DAILY_COUNT");
        template.setEvaluationScope("USER_DAILY");
        template.setThresholdValue(BigDecimal.ONE);
        template.setThresholdUnit("COUNT");
        template.setTargetValue(1);
        template.setUnit("DAY");
        template.setDamage(100);
        template.setRewardExp(30);
        template.setRewardCoin(10);
        return template;
    }
}
