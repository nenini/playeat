package com.nyamnyam.coach.guild.service;

import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.GuildErrorCode;
import com.nyamnyam.coach.guild.dto.request.GuildCreateRequest;
import com.nyamnyam.coach.guild.dto.response.GuildCreateResponse;
import com.nyamnyam.coach.guild.dto.response.GuildDetailResponse;
import com.nyamnyam.coach.guild.dto.response.GuildListResponse;
import com.nyamnyam.coach.guild.dto.response.GuildMemberListResponse;
import com.nyamnyam.coach.guild.dto.response.MyGuildStatusResponse;
import com.nyamnyam.coach.guild.entity.Guild;
import com.nyamnyam.coach.guild.entity.GuildMember;
import com.nyamnyam.coach.guild.entity.MyGuildJoinStatus;
import com.nyamnyam.coach.guild.repository.GuildRepository;
import com.nyamnyam.coach.guild.repository.row.GuildDetailRow;
import com.nyamnyam.coach.guild.repository.row.GuildMemberRow;
import com.nyamnyam.coach.guild.repository.row.GuildStatusRow;
import com.nyamnyam.coach.guild.repository.row.GuildSummaryRow;
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

    private GuildService guildService;

    @BeforeEach
    void setUp() {
        GuildValidator guildValidator = new GuildValidator(guildRepository);
        guildService = new GuildService(guildRepository, userRepository, guildValidator);
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

    private User activeUser() {
        return User.builder()
                .userId(1L)
                .email("user@example.com")
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
