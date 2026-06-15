package com.nyamnyam.coach.guild.service;

import com.nyamnyam.coach.boss.repository.BossBattleParticipantRepository;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.GuildErrorCode;
import com.nyamnyam.coach.guild.dto.request.GuildCreateRequest;
import com.nyamnyam.coach.guild.dto.request.GuildNoticeCreateRequest;
import com.nyamnyam.coach.guild.dto.request.GuildNoticeUpdateRequest;
import com.nyamnyam.coach.guild.dto.request.GuildUpdateRequest;
import com.nyamnyam.coach.guild.dto.request.JoinRequestCreateByCodeRequest;
import com.nyamnyam.coach.guild.dto.request.JoinRequestCreateRequest;
import com.nyamnyam.coach.guild.dto.response.GuildJoinRequestListResponse;
import com.nyamnyam.coach.guild.dto.response.GuildCreateResponse;
import com.nyamnyam.coach.guild.dto.response.GuildDeleteResponse;
import com.nyamnyam.coach.guild.dto.response.GuildDetailResponse;
import com.nyamnyam.coach.guild.dto.response.GuildKickResponse;
import com.nyamnyam.coach.guild.dto.response.GuildLeaveResponse;
import com.nyamnyam.coach.guild.dto.response.GuildListResponse;
import com.nyamnyam.coach.guild.dto.response.GuildMemberDetailResponse;
import com.nyamnyam.coach.guild.dto.response.GuildMemberListResponse;
import com.nyamnyam.coach.guild.dto.response.GuildNoticeCreateResponse;
import com.nyamnyam.coach.guild.dto.response.GuildNoticeListResponse;
import com.nyamnyam.coach.guild.dto.response.GuildNoticeResponse;
import com.nyamnyam.coach.guild.dto.response.GuildNoticeUpdateResponse;
import com.nyamnyam.coach.guild.dto.response.GuildUpdateResponse;
import com.nyamnyam.coach.guild.dto.response.JoinRequestApproveResponse;
import com.nyamnyam.coach.guild.dto.response.JoinRequestCancelResponse;
import com.nyamnyam.coach.guild.dto.response.JoinRequestCreateResponse;
import com.nyamnyam.coach.guild.dto.response.JoinRequestRejectResponse;
import com.nyamnyam.coach.guild.dto.response.MyJoinRequestListResponse;
import com.nyamnyam.coach.guild.dto.response.MyGuildStatusResponse;
import com.nyamnyam.coach.guild.entity.Guild;
import com.nyamnyam.coach.guild.entity.GuildJoinRequest;
import com.nyamnyam.coach.guild.entity.GuildMember;
import com.nyamnyam.coach.guild.entity.MyGuildJoinStatus;
import com.nyamnyam.coach.guild.repository.GuildRepository;
import com.nyamnyam.coach.guild.repository.row.GuildDetailRow;
import com.nyamnyam.coach.guild.repository.row.GuildMemberRow;
import com.nyamnyam.coach.guild.repository.row.GuildNoticeRow;
import com.nyamnyam.coach.guild.repository.row.GuildStatusRow;
import com.nyamnyam.coach.guild.repository.row.GuildSummaryRow;
import com.nyamnyam.coach.guild.repository.row.JoinRequestRow;
import com.nyamnyam.coach.user.entity.User;
import com.nyamnyam.coach.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GuildServiceTest {

    @Mock
    private GuildRepository guildRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BossBattleParticipantRepository bossBattleParticipantRepository;

    private GuildService guildService;

    @BeforeEach
    void setUp() {
        GuildValidator guildValidator = new GuildValidator(guildRepository);
        guildService = new GuildService(
                guildRepository,
                bossBattleParticipantRepository,
                userRepository,
                guildValidator
        );
    }

    @Test
    @DisplayName("길드 생성 시 PRIVATE ACTIVE 길드와 OWNER 멤버를 함께 저장한다")
    void createGuild() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser()));
        when(guildRepository.existsActiveMembershipByUserId(1L)).thenReturn(false);
        when(guildRepository.existsPendingJoinRequestByUserId(1L)).thenReturn(false);
        when(guildRepository.existsByName("잘먹잘싸")).thenReturn(false);
        when(guildRepository.existsByInviteCode(anyString())).thenReturn(false);
        doAnswer(invocation -> {
            Guild guild = invocation.getArgument(0);
            guild.setGuildId(10L);
            return null;
        }).when(guildRepository).save(any(Guild.class));
        when(guildRepository.findById(10L)).thenReturn(Optional.of(savedGuild()));
        when(guildRepository.countActiveMembers(10L)).thenReturn(1);

        GuildCreateResponse response = guildService.createGuild(
                1L,
                new GuildCreateRequest("잘먹잘싸", "건강하게 먹는 길드", 30)
        );

        ArgumentCaptor<Guild> guildCaptor = ArgumentCaptor.forClass(Guild.class);
        ArgumentCaptor<GuildMember> memberCaptor = ArgumentCaptor.forClass(GuildMember.class);
        verify(guildRepository).save(guildCaptor.capture());
        verify(guildRepository).saveMember(memberCaptor.capture());

        Guild guildToSave = guildCaptor.getValue();
        assertThat(guildToSave.getInviteCode()).startsWith("NYAM-");
        assertThat(guildToSave.getInviteCode().substring(5)).doesNotContain("O", "0", "I", "1");
        assertThat(guildToSave.getVisibility()).isEqualTo("PRIVATE");
        assertThat(guildToSave.getStatus()).isEqualTo("ACTIVE");
        assertThat(guildToSave.getGuildPoint()).isZero();

        GuildMember owner = memberCaptor.getValue();
        assertThat(owner.getGuildId()).isEqualTo(10L);
        assertThat(owner.getUserId()).isEqualTo(1L);
        assertThat(owner.getRole()).isEqualTo("OWNER");

        assertThat(response.guildId()).isEqualTo(10L);
        assertThat(response.myRole()).isEqualTo("OWNER");
        assertThat(response.memberCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("이미 가입한 길드가 있으면 길드를 생성할 수 없다")
    void createGuildWithJoinedGuild() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser()));
        when(guildRepository.existsActiveMembershipByUserId(1L)).thenReturn(true);

        assertThatThrownBy(() -> guildService.createGuild(
                1L,
                new GuildCreateRequest("잘먹잘싸", null, 30)
        ))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(GuildErrorCode.USER_ALREADY_JOINED_GUILD);

        verify(guildRepository, never()).save(any(Guild.class));
    }

    @Test
    @DisplayName("PENDING 참여 요청이 있으면 길드를 생성할 수 없다")
    void createGuildWithPendingRequest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser()));
        when(guildRepository.existsActiveMembershipByUserId(1L)).thenReturn(false);
        when(guildRepository.existsPendingJoinRequestByUserId(1L)).thenReturn(true);

        assertThatThrownBy(() -> guildService.createGuild(
                1L,
                new GuildCreateRequest("잘먹잘싸", null, 30)
        ))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(GuildErrorCode.USER_HAS_PENDING_GUILD_REQUEST);
    }

    @Test
    @DisplayName("길드명 중복이면 GUILD_NAME_DUPLICATED 예외를 던진다")
    void createGuildWithDuplicatedName() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser()));
        when(guildRepository.existsActiveMembershipByUserId(1L)).thenReturn(false);
        when(guildRepository.existsPendingJoinRequestByUserId(1L)).thenReturn(false);
        when(guildRepository.existsByName("잘먹잘싸")).thenReturn(true);

        assertThatThrownBy(() -> guildService.createGuild(
                1L,
                new GuildCreateRequest("잘먹잘싸", null, 30)
        ))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(GuildErrorCode.GUILD_NAME_DUPLICATED);
    }

    @Test
    @DisplayName("길드 목록은 내 참여 상태와 alreadyJoinedAnyGuild를 함께 반환한다")
    void getGuilds() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser()));
        when(guildRepository.existsActiveMembershipByUserId(1L)).thenReturn(true);
        when(guildRepository.findActiveGuildSummaries(1L, null, "guildPoint", 11, 0))
                .thenReturn(List.of(
                        guildSummaryRow(1L, "내길드", 1L, null),
                        guildSummaryRow(2L, "대기길드", null, 12L),
                        guildSummaryRow(3L, "다른길드", null, null)
                ));

        GuildListResponse response = guildService.getGuilds(1L, 0, 10, null, "guildPoint");

        assertThat(response.guilds()).hasSize(3);
        assertThat(response.guilds().get(0).myJoinStatus()).isEqualTo(MyGuildJoinStatus.JOINED);
        assertThat(response.guilds().get(1).myJoinStatus()).isEqualTo(MyGuildJoinStatus.PENDING);
        assertThat(response.guilds().get(1).joinRequestId()).isEqualTo(12L);
        assertThat(response.guilds().get(2).myJoinStatus()).isEqualTo(MyGuildJoinStatus.NONE);
        assertThat(response.guilds()).allMatch(guild -> guild.alreadyJoinedAnyGuild());
    }

    @Test
    @DisplayName("내 길드 상태는 JOINED를 PENDING보다 우선 반환한다")
    void getMyGuildStatusJoined() {
        GuildStatusRow row = new GuildStatusRow();
        row.setGuildId(1L);
        row.setName("잘먹잘싸");
        row.setInviteCode("NYAM-A7K3");
        row.setRole("OWNER");

        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser()));
        when(guildRepository.findJoinedStatus(1L)).thenReturn(Optional.of(row));

        MyGuildStatusResponse response = guildService.getMyGuildStatus(1L);

        assertThat(response.status()).isEqualTo(MyGuildJoinStatus.JOINED);
        assertThat(response.guild().role()).isEqualTo("OWNER");
        assertThat(response.joinRequest()).isNull();
    }

    @Test
    @DisplayName("가입 길드가 없고 PENDING 요청이 있으면 PENDING 상태를 반환한다")
    void getMyGuildStatusPending() {
        GuildStatusRow row = new GuildStatusRow();
        row.setGuildId(1L);
        row.setName("잘먹잘싸");
        row.setInviteCode("NYAM-A7K3");
        row.setRequestId(12L);
        row.setRequestStatus("PENDING");
        row.setRequestCreatedAt(LocalDateTime.of(2026, 6, 9, 10, 30));

        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser()));
        when(guildRepository.findJoinedStatus(1L)).thenReturn(Optional.empty());
        when(guildRepository.findPendingStatus(1L)).thenReturn(Optional.of(row));

        MyGuildStatusResponse response = guildService.getMyGuildStatus(1L);

        assertThat(response.status()).isEqualTo(MyGuildJoinStatus.PENDING);
        assertThat(response.guild().role()).isNull();
        assertThat(response.joinRequest().requestId()).isEqualTo(12L);
    }

    @Test
    @DisplayName("길드 상세 조회는 길드 멤버만 가능하다")
    void getGuildDetail() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser()));
        when(guildRepository.findById(10L)).thenReturn(Optional.of(savedGuild()));
        when(guildRepository.findActiveMemberRole(10L, 1L)).thenReturn(Optional.of("OWNER"));
        when(guildRepository.findGuildDetail(10L, 1L)).thenReturn(Optional.of(guildDetailRow()));

        GuildDetailResponse response = guildService.getGuildDetail(10L, 1L);

        assertThat(response.guildId()).isEqualTo(10L);
        assertThat(response.myRole()).isEqualTo("OWNER");
        assertThat(response.visibility()).isEqualTo("PRIVATE");
    }

    @Test
    @DisplayName("길드원이 아니면 길드원 목록을 조회할 수 없다")
    void getGuildMembersDenied() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser()));
        when(guildRepository.findById(10L)).thenReturn(Optional.of(savedGuild()));
        when(guildRepository.findActiveMemberRole(10L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> guildService.getGuildMembers(10L, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(GuildErrorCode.GUILD_ACCESS_DENIED);
    }

    @Test
    @DisplayName("길드원 목록은 캐릭터 정보와 isMe를 포함한다")
    void getGuildMembers() {
        GuildMemberRow row = new GuildMemberRow();
        row.setMemberId(100L);
        row.setUserId(1L);
        row.setNickname("예린");
        row.setProfileImageUrl("https://example.com/profile.png");
        row.setCharacterId(200L);
        row.setCharacterName("냠냠이");
        row.setCharacterLevel(7);
        row.setCharacterStage("BABY");
        row.setCharacterMood("HAPPY");
        row.setCharacterAppearanceType("NORMAL");
        row.setRole("OWNER");
        row.setJoinedAt(LocalDateTime.of(2026, 6, 9, 10, 30));

        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser()));
        when(guildRepository.findById(10L)).thenReturn(Optional.of(savedGuild()));
        when(guildRepository.findActiveMemberRole(10L, 1L)).thenReturn(Optional.of("OWNER"));
        when(guildRepository.findActiveMembers(10L)).thenReturn(List.of(row));

        GuildMemberListResponse response = guildService.getGuildMembers(10L, 1L);

        assertThat(response.members()).hasSize(1);
        assertThat(response.members().get(0).isMe()).isTrue();
        assertThat(response.members().get(0).characterLevel()).isEqualTo(7);
    }

    @Test
    @DisplayName("길드장은 길드 정보를 수정할 수 있다")
    void updateGuildByOwner() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser()));
        when(guildRepository.findById(10L))
                .thenReturn(Optional.of(savedGuild()), Optional.of(savedGuild()), Optional.of(updatedGuild()));
        when(guildRepository.findActiveMemberRole(10L, 1L)).thenReturn(Optional.of("OWNER"));
        when(guildRepository.existsByNameExceptGuildId("단백질 원정대", 10L)).thenReturn(false);
        when(guildRepository.countActiveMembers(10L)).thenReturn(2);

        GuildUpdateResponse response = guildService.updateGuild(
                10L,
                1L,
                new GuildUpdateRequest("단백질 원정대", "함께 보스 잡는 길드", 20)
        );

        verify(guildRepository).updateGuild(10L, "단백질 원정대", "함께 보스 잡는 길드", 20);
        assertThat(response.name()).isEqualTo("단백질 원정대");
        assertThat(response.memberCount()).isEqualTo(2);
        assertThat(response.maxMembers()).isEqualTo(20);
    }

    @Test
    @DisplayName("일반 멤버는 길드 정보를 수정할 수 없다")
    void updateGuildByMemberDenied() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(activeUser(2L)));
        when(guildRepository.findById(10L)).thenReturn(Optional.of(savedGuild()), Optional.of(savedGuild()));
        when(guildRepository.findActiveMemberRole(10L, 2L)).thenReturn(Optional.of("MEMBER"));

        assertThatThrownBy(() -> guildService.updateGuild(
                10L,
                2L,
                new GuildUpdateRequest("단백질 원정대", null, 30)
        ))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(GuildErrorCode.GUILD_OWNER_ONLY);

        verify(guildRepository, never()).updateGuild(any(), anyString(), any(), anyInt());
    }

    @Test
    @DisplayName("현재 멤버 수보다 작은 maxMembers로 수정할 수 없다")
    void updateGuildMaxMembersLessThanCurrentMembers() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser()));
        when(guildRepository.findById(10L)).thenReturn(Optional.of(savedGuild()), Optional.of(savedGuild()));
        when(guildRepository.findActiveMemberRole(10L, 1L)).thenReturn(Optional.of("OWNER"));
        when(guildRepository.existsByNameExceptGuildId("잘먹잘싸", 10L)).thenReturn(false);
        when(guildRepository.countActiveMembers(10L)).thenReturn(5);

        assertThatThrownBy(() -> guildService.updateGuild(
                10L,
                1L,
                new GuildUpdateRequest("잘먹잘싸", null, 4)
        ))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(GuildErrorCode.GUILD_MAX_MEMBERS_LESS_THAN_CURRENT_MEMBERS);
    }

    @Test
    @DisplayName("길드장은 길드를 소프트 삭제하고 active 멤버를 모두 탈퇴 처리한다")
    void deleteGuildByOwner() {
        Guild inactiveGuild = inactiveGuild();
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser()));
        when(guildRepository.findById(10L))
                .thenReturn(Optional.of(savedGuild()), Optional.of(savedGuild()), Optional.of(inactiveGuild));
        when(guildRepository.findActiveMemberRole(10L, 1L)).thenReturn(Optional.of("OWNER"));

        GuildDeleteResponse response = guildService.deleteGuild(10L, 1L);

        verify(guildRepository).softDeleteGuild(10L);
        verify(guildRepository).markAllMembersLeft(10L);
        assertThat(response.status()).isEqualTo("INACTIVE");
    }

    @Test
    @DisplayName("일반 멤버는 길드를 삭제할 수 없다")
    void deleteGuildByMemberDenied() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(activeUser(2L)));
        when(guildRepository.findById(10L)).thenReturn(Optional.of(savedGuild()), Optional.of(savedGuild()));
        when(guildRepository.findActiveMemberRole(10L, 2L)).thenReturn(Optional.of("MEMBER"));

        assertThatThrownBy(() -> guildService.deleteGuild(10L, 2L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(GuildErrorCode.GUILD_OWNER_ONLY);

        verify(guildRepository, never()).softDeleteGuild(any());
    }

    @Test
    @DisplayName("일반 멤버는 길드를 탈퇴할 수 있다")
    void leaveGuildByMember() {
        GuildMemberRow member = guildMemberRow(101L, 2L, "MEMBER", null);
        GuildMemberRow leftMember = guildMemberRow(101L, 2L, "MEMBER", LocalDateTime.of(2026, 6, 10, 10, 30));

        when(userRepository.findById(2L)).thenReturn(Optional.of(activeUser(2L)));
        when(guildRepository.findById(10L)).thenReturn(Optional.of(savedGuild()));
        when(guildRepository.findActiveMemberByGuildIdAndUserId(10L, 2L)).thenReturn(Optional.of(member));
        when(guildRepository.findMemberByGuildIdAndUserId(10L, 2L)).thenReturn(Optional.of(leftMember));

        GuildLeaveResponse response = guildService.leaveGuild(10L, 2L);

        verify(guildRepository).leaveGuild(10L, 2L);
        verify(bossBattleParticipantRepository).markParticipantLeftByGuildAndUser(10L, 2L);
        assertThat(response.leftAt()).isEqualTo(LocalDateTime.of(2026, 6, 10, 10, 30));
    }

    @Test
    @DisplayName("길드장은 탈퇴할 수 없다")
    void ownerCannotLeaveGuild() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser()));
        when(guildRepository.findById(10L)).thenReturn(Optional.of(savedGuild()));
        when(guildRepository.findActiveMemberByGuildIdAndUserId(10L, 1L))
                .thenReturn(Optional.of(guildMemberRow(100L, 1L, "OWNER", null)));

        assertThatThrownBy(() -> guildService.leaveGuild(10L, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(GuildErrorCode.GUILD_OWNER_CANNOT_LEAVE);
    }

    @Test
    @DisplayName("길드원 상세 조회는 캐릭터 정보와 임시 통계 0을 반환한다")
    void getGuildMemberDetail() {
        GuildMemberRow row = guildMemberDetailRow(100L, 1L, "OWNER");

        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser()));
        when(guildRepository.findById(10L)).thenReturn(Optional.of(savedGuild()));
        when(guildRepository.findActiveMemberRole(10L, 1L)).thenReturn(Optional.of("OWNER"));
        when(guildRepository.findMemberDetail(10L, 100L, 1L)).thenReturn(Optional.of(row));

        GuildMemberDetailResponse response = guildService.getGuildMemberDetail(10L, 100L, 1L);

        assertThat(response.nickname()).isEqualTo("예린");
        assertThat(response.streakDays()).isEqualTo(5);
        assertThat(response.weeklyRecordRate()).isZero();
        assertThat(response.bossContribution()).isZero();
        assertThat(response.completedQuestCount()).isZero();
    }

    @Test
    @DisplayName("길드장은 멤버를 추방할 수 있다")
    void kickGuildMemberByOwner() {
        GuildMemberRow member = guildMemberRow(101L, 2L, "MEMBER", null);
        GuildMemberRow kickedMember = guildMemberRow(101L, 2L, "MEMBER", LocalDateTime.of(2026, 6, 10, 10, 30));

        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser()));
        when(guildRepository.findById(10L)).thenReturn(Optional.of(savedGuild()));
        when(guildRepository.findActiveMemberRole(10L, 1L)).thenReturn(Optional.of("OWNER"));
        when(guildRepository.findMemberByMemberId(10L, 101L))
                .thenReturn(Optional.of(member), Optional.of(kickedMember));

        GuildKickResponse response = guildService.kickGuildMember(10L, 101L, 1L);

        verify(guildRepository).kickGuildMember(10L, 101L);
        verify(bossBattleParticipantRepository).markParticipantKickedByGuildAndUser(10L, 2L);
        assertThat(response.userId()).isEqualTo(2L);
        assertThat(response.kickedAt()).isEqualTo(LocalDateTime.of(2026, 6, 10, 10, 30));
    }

    @Test
    @DisplayName("길드장은 자기 자신을 추방할 수 없다")
    void ownerCannotKickOwner() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser()));
        when(guildRepository.findById(10L)).thenReturn(Optional.of(savedGuild()));
        when(guildRepository.findActiveMemberRole(10L, 1L)).thenReturn(Optional.of("OWNER"));
        when(guildRepository.findMemberByMemberId(10L, 100L))
                .thenReturn(Optional.of(guildMemberRow(100L, 1L, "OWNER", null)));

        assertThatThrownBy(() -> guildService.kickGuildMember(10L, 100L, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(GuildErrorCode.GUILD_CANNOT_KICK_OWNER);
    }

    @Test
    @DisplayName("이미 탈퇴한 멤버는 추방할 수 없다")
    void cannotKickAlreadyLeftMember() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser()));
        when(guildRepository.findById(10L)).thenReturn(Optional.of(savedGuild()));
        when(guildRepository.findActiveMemberRole(10L, 1L)).thenReturn(Optional.of("OWNER"));
        when(guildRepository.findMemberByMemberId(10L, 101L))
                .thenReturn(Optional.of(guildMemberRow(101L, 2L, "MEMBER", LocalDateTime.of(2026, 6, 10, 10, 30))));

        assertThatThrownBy(() -> guildService.kickGuildMember(10L, 101L, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(GuildErrorCode.GUILD_MEMBER_ALREADY_LEFT);
    }

    @Test
    @DisplayName("길드원은 공지 목록을 조회할 수 있고 길드장은 editable true를 받는다")
    void getGuildNotices() {
        GuildNoticeRow notice = guildNoticeRow(50L, "이번 주 보스전 안내", "당류 줄이기");

        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser()));
        when(guildRepository.findById(10L)).thenReturn(Optional.of(savedGuild()));
        when(guildRepository.findActiveMemberRole(10L, 1L)).thenReturn(Optional.of("OWNER"));
        when(guildRepository.findGuildNotices(10L)).thenReturn(List.of(notice));

        GuildNoticeListResponse response = guildService.getGuildNotices(10L, 1L);

        assertThat(response.notices()).hasSize(1);
        assertThat(response.notices().get(0).noticeId()).isEqualTo(50L);
        assertThat(response.notices().get(0).editable()).isTrue();
    }

    @Test
    @DisplayName("길드원은 공지 상세를 조회할 수 있다")
    void getGuildNotice() {
        GuildNoticeRow notice = guildNoticeRow(50L, "이번 주 보스전 안내", "당류 줄이기");

        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser()));
        when(guildRepository.findById(10L)).thenReturn(Optional.of(savedGuild()));
        when(guildRepository.findActiveMemberRole(10L, 1L)).thenReturn(Optional.of("OWNER"));
        when(guildRepository.findGuildNoticeById(10L, 50L)).thenReturn(Optional.of(notice));

        GuildNoticeResponse response = guildService.getGuildNotice(10L, 50L, 1L);

        assertThat(response.title()).isEqualTo("이번 주 보스전 안내");
        assertThat(response.writerNickname()).isEqualTo("예린");
        assertThat(response.editable()).isTrue();
    }

    @Test
    @DisplayName("길드장은 공지를 등록할 수 있다")
    void createGuildNotice() {
        GuildNoticeRow notice = guildNoticeRow(50L, "이번 주 보스전 안내", "당류 줄이기");

        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser()));
        when(guildRepository.findById(10L)).thenReturn(Optional.of(savedGuild()));
        when(guildRepository.findActiveMemberRole(10L, 1L)).thenReturn(Optional.of("OWNER"));
        doAnswer(invocation -> {
            GuildNoticeRow noticeToSave = invocation.getArgument(0);
            noticeToSave.setNoticeId(50L);
            return null;
        }).when(guildRepository).saveGuildNotice(any(GuildNoticeRow.class));
        when(guildRepository.findGuildNoticeById(10L, 50L)).thenReturn(Optional.of(notice));

        GuildNoticeCreateResponse response = guildService.createGuildNotice(
                10L,
                1L,
                new GuildNoticeCreateRequest(" 이번 주 보스전 안내 ", " 당류 줄이기 ")
        );

        ArgumentCaptor<GuildNoticeRow> noticeCaptor = ArgumentCaptor.forClass(GuildNoticeRow.class);
        verify(guildRepository).saveGuildNotice(noticeCaptor.capture());
        assertThat(noticeCaptor.getValue().getGuildId()).isEqualTo(10L);
        assertThat(noticeCaptor.getValue().getWriterUserId()).isEqualTo(1L);
        assertThat(noticeCaptor.getValue().getTitle()).isEqualTo("이번 주 보스전 안내");
        assertThat(noticeCaptor.getValue().getContent()).isEqualTo("당류 줄이기");
        assertThat(response.noticeId()).isEqualTo(50L);
        assertThat(response.title()).isEqualTo("이번 주 보스전 안내");
    }

    @Test
    @DisplayName("길드장은 공지를 수정할 수 있다")
    void updateGuildNotice() {
        GuildNoticeRow before = guildNoticeRow(50L, "기존 공지", "기존 내용");
        GuildNoticeRow after = guildNoticeRow(50L, "이번 주 보스전 안내", "당류 줄이기");

        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser()));
        when(guildRepository.findById(10L)).thenReturn(Optional.of(savedGuild()));
        when(guildRepository.findActiveMemberRole(10L, 1L)).thenReturn(Optional.of("OWNER"));
        when(guildRepository.findGuildNoticeById(10L, 50L))
                .thenReturn(Optional.of(before), Optional.of(after));

        GuildNoticeUpdateResponse response = guildService.updateGuildNotice(
                10L,
                50L,
                1L,
                new GuildNoticeUpdateRequest(" 이번 주 보스전 안내 ", " 당류 줄이기 ")
        );

        verify(guildRepository).updateGuildNotice(10L, 50L, "이번 주 보스전 안내", "당류 줄이기");
        assertThat(response.title()).isEqualTo("이번 주 보스전 안내");
    }

    @Test
    @DisplayName("일반 멤버는 공지를 등록할 수 없다")
    void createGuildNoticeByMemberDenied() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(activeUser(2L)));
        when(guildRepository.findById(10L)).thenReturn(Optional.of(savedGuild()));
        when(guildRepository.findActiveMemberRole(10L, 2L)).thenReturn(Optional.of("MEMBER"));

        assertThatThrownBy(() -> guildService.createGuildNotice(
                10L,
                2L,
                new GuildNoticeCreateRequest("공지", "내용")
        ))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(GuildErrorCode.GUILD_OWNER_ONLY);
    }

    @Test
    @DisplayName("길드장은 공지를 삭제할 수 있다")
    void deleteGuildNotice() {
        GuildNoticeRow notice = guildNoticeRow(50L, "이번 주 보스전 안내", "당류 줄이기");

        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser()));
        when(guildRepository.findById(10L)).thenReturn(Optional.of(savedGuild()));
        when(guildRepository.findActiveMemberRole(10L, 1L)).thenReturn(Optional.of("OWNER"));
        when(guildRepository.findGuildNoticeById(10L, 50L)).thenReturn(Optional.of(notice));

        assertThat(guildService.deleteGuildNotice(10L, 50L, 1L).deleted()).isTrue();

        verify(guildRepository).deleteGuildNotice(10L, 50L);
    }

    @Test
    @DisplayName("초대 코드로 길드 참여 요청을 생성한다")
    void createJoinRequestByInviteCode() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(activeUser(2L)));
        when(guildRepository.findActiveGuildByInviteCode("NYAM-A7K3")).thenReturn(Optional.of(savedGuild()));
        when(guildRepository.existsActiveMembershipByUserId(2L)).thenReturn(false);
        when(guildRepository.existsPendingJoinRequestByUserId(2L)).thenReturn(false);
        when(guildRepository.findById(10L)).thenReturn(Optional.of(savedGuild()));
        when(guildRepository.countActiveMembers(10L)).thenReturn(3);
        doAnswer(invocation -> {
            GuildJoinRequest joinRequest = invocation.getArgument(0);
            joinRequest.setRequestId(12L);
            return null;
        }).when(guildRepository).insertJoinRequest(any(GuildJoinRequest.class));
        when(guildRepository.findJoinRequestByGuildIdAndRequestId(10L, 12L))
                .thenReturn(Optional.of(joinRequestRow(12L, 10L, 2L, "PENDING")));

        JoinRequestCreateResponse response = guildService.createJoinRequestByInviteCode(
                2L,
                new JoinRequestCreateByCodeRequest("NYAM-A7K3")
        );

        ArgumentCaptor<GuildJoinRequest> captor = ArgumentCaptor.forClass(GuildJoinRequest.class);
        verify(guildRepository).insertJoinRequest(captor.capture());
        assertThat(captor.getValue().getGuildId()).isEqualTo(10L);
        assertThat(response.requestId()).isEqualTo(12L);
        assertThat(response.status()).isEqualTo("PENDING");
    }

    @Test
    @DisplayName("길드 ID로 길드 참여 요청을 생성한다")
    void createJoinRequestByGuildId() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(activeUser(2L)));
        when(guildRepository.findById(10L)).thenReturn(Optional.of(savedGuild()), Optional.of(savedGuild()));
        when(guildRepository.existsActiveMembershipByUserId(2L)).thenReturn(false);
        when(guildRepository.existsPendingJoinRequestByUserId(2L)).thenReturn(false);
        when(guildRepository.countActiveMembers(10L)).thenReturn(3);
        doAnswer(invocation -> {
            GuildJoinRequest joinRequest = invocation.getArgument(0);
            joinRequest.setRequestId(12L);
            return null;
        }).when(guildRepository).insertJoinRequest(any(GuildJoinRequest.class));
        when(guildRepository.findJoinRequestByGuildIdAndRequestId(10L, 12L))
                .thenReturn(Optional.of(joinRequestRow(12L, 10L, 2L, "PENDING")));

        JoinRequestCreateResponse response = guildService.createJoinRequest(
                10L,
                2L,
                new JoinRequestCreateRequest()
        );

        assertThat(response.guildId()).isEqualTo(10L);
        assertThat(response.status()).isEqualTo("PENDING");
    }

    @Test
    @DisplayName("이미 가입한 사용자는 참여 요청을 생성할 수 없다")
    void createJoinRequestAlreadyJoined() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(activeUser(2L)));
        when(guildRepository.findActiveGuildByInviteCode("NYAM-A7K3")).thenReturn(Optional.of(savedGuild()));
        when(guildRepository.existsActiveMembershipByUserId(2L)).thenReturn(true);

        assertThatThrownBy(() -> guildService.createJoinRequestByInviteCode(
                2L,
                new JoinRequestCreateByCodeRequest("NYAM-A7K3")
        ))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(GuildErrorCode.USER_ALREADY_JOINED_GUILD);
    }

    @Test
    @DisplayName("PENDING 요청이 있으면 중복 참여 요청을 생성할 수 없다")
    void createJoinRequestAlreadyPending() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(activeUser(2L)));
        when(guildRepository.findActiveGuildByInviteCode("NYAM-A7K3")).thenReturn(Optional.of(savedGuild()));
        when(guildRepository.existsActiveMembershipByUserId(2L)).thenReturn(false);
        when(guildRepository.existsPendingJoinRequestByUserId(2L)).thenReturn(true);

        assertThatThrownBy(() -> guildService.createJoinRequestByInviteCode(
                2L,
                new JoinRequestCreateByCodeRequest("NYAM-A7K3")
        ))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(GuildErrorCode.USER_HAS_PENDING_GUILD_REQUEST);
    }

    @Test
    @DisplayName("잘못된 초대 코드는 예외를 던진다")
    void createJoinRequestInvalidInviteCode() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(activeUser(2L)));
        when(guildRepository.findActiveGuildByInviteCode("BAD-CODE")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> guildService.createJoinRequestByInviteCode(
                2L,
                new JoinRequestCreateByCodeRequest("BAD-CODE")
        ))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(GuildErrorCode.INVALID_INVITE_CODE);
    }

    @Test
    @DisplayName("내 참여 요청 목록을 조회한다")
    void getMyJoinRequests() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(activeUser(2L)));
        when(guildRepository.findMyJoinRequests(2L, "PENDING", 11, 0))
                .thenReturn(List.of(joinRequestRow(12L, 10L, 2L, "PENDING")));

        MyJoinRequestListResponse response = guildService.getMyJoinRequests(2L, "pending", 0, 10);

        assertThat(response.requests()).hasSize(1);
        assertThat(response.requests().get(0).requestId()).isEqualTo(12L);
        assertThat(response.requests().get(0).guildName()).isEqualTo("잘먹잘싸");
    }

    @Test
    @DisplayName("길드장은 자기 길드의 참여 요청 목록을 조회할 수 있다")
    void getGuildJoinRequestsByOwner() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser()));
        when(guildRepository.findById(10L)).thenReturn(Optional.of(savedGuild()));
        when(guildRepository.findActiveMemberRole(10L, 1L)).thenReturn(Optional.of("OWNER"));
        when(guildRepository.findGuildJoinRequests(10L, "PENDING", 11, 0))
                .thenReturn(List.of(joinRequestRow(12L, 10L, 2L, "PENDING")));

        GuildJoinRequestListResponse response = guildService.getGuildJoinRequests(10L, 1L, null, 0, 10);

        assertThat(response.requests()).hasSize(1);
        assertThat(response.requests().get(0).nickname()).isEqualTo("민수");
    }

    @Test
    @DisplayName("일반 멤버는 참여 요청 목록을 조회할 수 없다")
    void getGuildJoinRequestsByMemberDenied() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(activeUser(2L)));
        when(guildRepository.findById(10L)).thenReturn(Optional.of(savedGuild()));
        when(guildRepository.findActiveMemberRole(10L, 2L)).thenReturn(Optional.of("MEMBER"));

        assertThatThrownBy(() -> guildService.getGuildJoinRequests(10L, 2L, null, 0, 10))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(GuildErrorCode.GUILD_OWNER_ONLY);
    }

    @Test
    @DisplayName("길드장은 참여 요청을 승인하고 MEMBER로 등록한다")
    void approveJoinRequest() {
        JoinRequestRow pending = joinRequestRow(12L, 10L, 2L, "PENDING");
        JoinRequestRow approved = joinRequestRow(12L, 10L, 2L, "APPROVED");
        approved.setHandledBy(1L);
        approved.setHandledAt(LocalDateTime.of(2026, 6, 10, 10, 30));

        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser()));
        when(guildRepository.findById(10L)).thenReturn(Optional.of(savedGuild()), Optional.of(savedGuild()));
        when(guildRepository.findActiveMemberRole(10L, 1L)).thenReturn(Optional.of("OWNER"));
        when(guildRepository.findJoinRequestByGuildIdAndRequestId(10L, 12L))
                .thenReturn(Optional.of(pending), Optional.of(approved));
        when(guildRepository.existsActiveMembershipByUserId(2L)).thenReturn(false);
        when(guildRepository.countActiveMembers(10L)).thenReturn(3);
        when(guildRepository.findMemberByGuildIdAndUserId(10L, 2L)).thenReturn(Optional.empty());
        doAnswer(invocation -> {
            GuildMember member = invocation.getArgument(0);
            member.setGuildMemberId(8L);
            return null;
        }).when(guildRepository).saveMember(any(GuildMember.class));
        when(guildRepository.approveJoinRequest(10L, 12L, 1L)).thenReturn(1);

        JoinRequestApproveResponse response = guildService.approveJoinRequest(10L, 12L, 1L);

        verify(guildRepository).approveJoinRequest(10L, 12L, 1L);
        verify(guildRepository).cancelOtherPendingRequests(2L, 12L, 1L);
        assertThat(response.status()).isEqualTo("APPROVED");
        assertThat(response.memberId()).isEqualTo(8L);
    }

    @Test
    @DisplayName("길드장은 참여 요청을 거절할 수 있다")
    void rejectJoinRequest() {
        JoinRequestRow pending = joinRequestRow(12L, 10L, 2L, "PENDING");
        JoinRequestRow rejected = joinRequestRow(12L, 10L, 2L, "REJECTED");
        rejected.setHandledBy(1L);
        rejected.setHandledAt(LocalDateTime.of(2026, 6, 10, 10, 30));

        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser()));
        when(guildRepository.findById(10L)).thenReturn(Optional.of(savedGuild()));
        when(guildRepository.findActiveMemberRole(10L, 1L)).thenReturn(Optional.of("OWNER"));
        when(guildRepository.findJoinRequestByGuildIdAndRequestId(10L, 12L))
                .thenReturn(Optional.of(pending), Optional.of(rejected));
        when(guildRepository.rejectJoinRequest(10L, 12L, 1L)).thenReturn(1);

        JoinRequestRejectResponse response = guildService.rejectJoinRequest(10L, 12L, 1L);

        verify(guildRepository).rejectJoinRequest(10L, 12L, 1L);
        assertThat(response.status()).isEqualTo("REJECTED");
    }

    @Test
    @DisplayName("요청자는 본인 참여 요청을 취소할 수 있다")
    void cancelJoinRequest() {
        JoinRequestRow pending = joinRequestRow(12L, 10L, 2L, "PENDING");
        JoinRequestRow canceled = joinRequestRow(12L, 10L, 2L, "CANCELED");
        canceled.setHandledAt(LocalDateTime.of(2026, 6, 10, 10, 30));

        when(userRepository.findById(2L)).thenReturn(Optional.of(activeUser(2L)));
        when(guildRepository.findJoinRequestByGuildIdAndRequestId(10L, 12L))
                .thenReturn(Optional.of(pending), Optional.of(canceled));
        when(guildRepository.cancelJoinRequest(10L, 12L, 2L)).thenReturn(1);

        JoinRequestCancelResponse response = guildService.cancelJoinRequest(10L, 12L, 2L);

        verify(guildRepository).cancelJoinRequest(10L, 12L, 2L);
        assertThat(response.status()).isEqualTo("CANCELED");
        assertThat(response.canceledAt()).isEqualTo(LocalDateTime.of(2026, 6, 10, 10, 30));
    }

    @Test
    @DisplayName("다른 사용자의 참여 요청은 취소할 수 없다")
    void cancelJoinRequestAccessDenied() {
        when(userRepository.findById(3L)).thenReturn(Optional.of(activeUser(3L)));
        when(guildRepository.findJoinRequestByGuildIdAndRequestId(10L, 12L))
                .thenReturn(Optional.of(joinRequestRow(12L, 10L, 2L, "PENDING")));

        assertThatThrownBy(() -> guildService.cancelJoinRequest(10L, 12L, 3L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(GuildErrorCode.JOIN_REQUEST_ACCESS_DENIED);
    }

    @Test
    @DisplayName("PENDING이 아닌 참여 요청은 승인할 수 없다")
    void approveJoinRequestNotPending() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser()));
        when(guildRepository.findById(10L)).thenReturn(Optional.of(savedGuild()));
        when(guildRepository.findActiveMemberRole(10L, 1L)).thenReturn(Optional.of("OWNER"));
        when(guildRepository.findJoinRequestByGuildIdAndRequestId(10L, 12L))
                .thenReturn(Optional.of(joinRequestRow(12L, 10L, 2L, "APPROVED")));

        assertThatThrownBy(() -> guildService.approveJoinRequest(10L, 12L, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(GuildErrorCode.JOIN_REQUEST_NOT_PENDING);
    }

    private User activeUser() {
        return activeUser(1L);
    }

    private User activeUser(Long userId) {
        return User.builder()
                .userId(userId)
                .email("user" + userId + "@example.com")
                .passwordHash("encoded-password")
                .nickname("예린")
                .status("ACTIVE")
                .onboardingCompleted(true)
                .build();
    }

    private Guild savedGuild() {
        return Guild.builder()
                .guildId(10L)
                .name("잘먹잘싸")
                .description("건강하게 먹는 길드")
                .inviteCode("NYAM-A7K3")
                .ownerUserId(1L)
                .maxMembers(30)
                .guildPoint(0)
                .visibility("PRIVATE")
                .status("ACTIVE")
                .createdAt(LocalDateTime.of(2026, 6, 9, 10, 30))
                .updatedAt(LocalDateTime.of(2026, 6, 9, 10, 30))
                .build();
    }

    private Guild updatedGuild() {
        return Guild.builder()
                .guildId(10L)
                .name("단백질 원정대")
                .description("함께 보스 잡는 길드")
                .inviteCode("NYAM-A7K3")
                .ownerUserId(1L)
                .maxMembers(20)
                .guildPoint(0)
                .visibility("PRIVATE")
                .status("ACTIVE")
                .createdAt(LocalDateTime.of(2026, 6, 9, 10, 30))
                .updatedAt(LocalDateTime.of(2026, 6, 10, 10, 30))
                .build();
    }

    private Guild inactiveGuild() {
        return Guild.builder()
                .guildId(10L)
                .name("잘먹잘싸")
                .description("건강하게 먹는 길드")
                .inviteCode("NYAM-A7K3")
                .ownerUserId(1L)
                .maxMembers(30)
                .guildPoint(0)
                .visibility("PRIVATE")
                .status("INACTIVE")
                .createdAt(LocalDateTime.of(2026, 6, 9, 10, 30))
                .updatedAt(LocalDateTime.of(2026, 6, 10, 10, 30))
                .build();
    }

    private GuildMemberRow guildMemberRow(Long memberId, Long userId, String role, LocalDateTime leftAt) {
        GuildMemberRow row = new GuildMemberRow();
        row.setMemberId(memberId);
        row.setUserId(userId);
        row.setRole(role);
        row.setJoinedAt(LocalDateTime.of(2026, 6, 9, 10, 30));
        row.setLeftAt(leftAt);
        return row;
    }

    private GuildMemberRow guildMemberDetailRow(Long memberId, Long userId, String role) {
        GuildMemberRow row = guildMemberRow(memberId, userId, role, null);
        row.setNickname("예린");
        row.setProfileImageUrl("https://example.com/profile.png");
        row.setCharacterId(200L);
        row.setCharacterName("냠냠이");
        row.setCharacterLevel(7);
        row.setCharacterStage("BABY");
        row.setCharacterMood("HAPPY");
        row.setCharacterAppearanceType("NORMAL");
        row.setStreakDays(5);
        return row;
    }

    private GuildNoticeRow guildNoticeRow(Long noticeId, String title, String content) {
        GuildNoticeRow row = new GuildNoticeRow();
        row.setNoticeId(noticeId);
        row.setGuildId(10L);
        row.setWriterUserId(1L);
        row.setWriterNickname("예린");
        row.setTitle(title);
        row.setContent(content);
        row.setCreatedAt(LocalDateTime.of(2026, 6, 10, 10, 0));
        row.setUpdatedAt(LocalDateTime.of(2026, 6, 10, 10, 30));
        return row;
    }

    private JoinRequestRow joinRequestRow(Long requestId, Long guildId, Long userId, String status) {
        JoinRequestRow row = new JoinRequestRow();
        row.setRequestId(requestId);
        row.setGuildId(guildId);
        row.setGuildName("잘먹잘싸");
        row.setInviteCode("NYAM-A7K3");
        row.setGuildDescription("건강하게 먹는 길드");
        row.setUserId(userId);
        row.setNickname("민수");
        row.setProfileImageUrl("https://example.com/profile.png");
        row.setCharacterId(300L);
        row.setCharacterName("냠냠이");
        row.setCharacterLevel(4);
        row.setStatus(status);
        row.setCreatedAt(LocalDateTime.of(2026, 6, 10, 10, 0));
        return row;
    }

    private GuildSummaryRow guildSummaryRow(
            Long guildId,
            String name,
            Long joinedGuildId,
            Long pendingRequestId
    ) {
        GuildSummaryRow row = new GuildSummaryRow();
        row.setGuildId(guildId);
        row.setName(name);
        row.setDescription("설명");
        row.setInviteCode("NYAM-A7K3");
        row.setMemberCount(3);
        row.setMaxMembers(30);
        row.setGuildPoint(100);
        row.setOwnerNickname("예린");
        row.setJoinedGuildId(joinedGuildId);
        row.setPendingRequestId(pendingRequestId);
        return row;
    }

    private GuildDetailRow guildDetailRow() {
        GuildDetailRow row = new GuildDetailRow();
        row.setGuildId(10L);
        row.setName("잘먹잘싸");
        row.setDescription("건강하게 먹는 길드");
        row.setInviteCode("NYAM-A7K3");
        row.setOwnerUserId(1L);
        row.setOwnerNickname("예린");
        row.setMemberCount(1);
        row.setMaxMembers(30);
        row.setGuildPoint(0);
        row.setVisibility("PRIVATE");
        row.setStatus("ACTIVE");
        row.setMyRole("OWNER");
        row.setCreatedAt(LocalDateTime.of(2026, 6, 9, 10, 30));
        row.setUpdatedAt(LocalDateTime.of(2026, 6, 9, 10, 30));
        return row;
    }
}
