package com.nyamnyam.coach.guild.repository;

import com.nyamnyam.coach.guild.entity.Guild;
import com.nyamnyam.coach.guild.entity.GuildMember;
import com.nyamnyam.coach.guild.repository.row.GuildDetailRow;
import com.nyamnyam.coach.guild.repository.row.GuildMemberRow;
import com.nyamnyam.coach.guild.repository.row.GuildStatusRow;
import com.nyamnyam.coach.guild.repository.row.GuildSummaryRow;
import com.nyamnyam.coach.user.entity.User;
import com.nyamnyam.coach.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@ActiveProfiles("test")
@Sql(scripts = "/test-auth-schema.sql")
class GuildRepositoryTest {

    private final GuildRepository guildRepository;
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    GuildRepositoryTest(
            GuildRepository guildRepository,
            UserRepository userRepository,
            JdbcTemplate jdbcTemplate
    ) {
        this.guildRepository = guildRepository;
        this.userRepository = userRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Test
    @DisplayName("길드와 OWNER 멤버를 저장하고 조회한다")
    void saveGuildAndOwnerMember() {
        User user = saveUser("owner@example.com", "예린");
        Guild guild = guild(user.getUserId(), "잘먹잘싸", "NYAM-A7K3");

        guildRepository.save(guild);
        guildRepository.saveMember(ownerMember(guild.getGuildId(), user.getUserId()));

        Optional<Guild> foundGuild = guildRepository.findById(guild.getGuildId());
        Optional<String> role = guildRepository.findActiveMemberRole(guild.getGuildId(), user.getUserId());

        assertThat(guild.getGuildId()).isNotNull();
        assertThat(foundGuild).isPresent();
        assertThat(foundGuild.get().getVisibility()).isEqualTo("PRIVATE");
        assertThat(role).contains("OWNER");
        assertThat(guildRepository.countActiveMembers(guild.getGuildId())).isEqualTo(1);
    }

    @Test
    @DisplayName("길드 탐색 목록은 memberCount, ownerNickname, 내 상태 정보를 조회한다")
    void findActiveGuildSummaries() {
        User owner = saveUser("owner@example.com", "예린");
        Guild guild = saveGuild(owner.getUserId(), "잘먹잘싸", "NYAM-A7K3");
        guildRepository.saveMember(ownerMember(guild.getGuildId(), owner.getUserId()));

        List<GuildSummaryRow> rows = guildRepository.findActiveGuildSummaries(
                owner.getUserId(),
                null,
                "guildPoint",
                10,
                0
        );

        assertThat(rows).hasSize(1);
        assertThat(rows.get(0).getGuildId()).isEqualTo(guild.getGuildId());
        assertThat(rows.get(0).getMemberCount()).isEqualTo(1);
        assertThat(rows.get(0).getOwnerNickname()).isEqualTo("예린");
        assertThat(rows.get(0).getJoinedGuildId()).isEqualTo(guild.getGuildId());
    }

    @Test
    @DisplayName("내 active 길드와 JOINED 상태를 조회한다")
    void findMyGuildAndJoinedStatus() {
        User owner = saveUser("owner@example.com", "예린");
        Guild guild = saveGuild(owner.getUserId(), "잘먹잘싸", "NYAM-A7K3");
        guildRepository.saveMember(ownerMember(guild.getGuildId(), owner.getUserId()));

        assertThat(guildRepository.findMyActiveGuilds(owner.getUserId())).hasSize(1);

        Optional<GuildStatusRow> status = guildRepository.findJoinedStatus(owner.getUserId());

        assertThat(status).isPresent();
        assertThat(status.get().getGuildId()).isEqualTo(guild.getGuildId());
        assertThat(status.get().getRole()).isEqualTo("OWNER");
    }

    @Test
    @DisplayName("PENDING 참여 요청 상태를 조회한다")
    void findPendingStatus() {
        User owner = saveUser("owner@example.com", "예린");
        User requester = saveUser("requester@example.com", "민수");
        Guild guild = saveGuild(owner.getUserId(), "잘먹잘싸", "NYAM-A7K3");
        insertPendingJoinRequest(guild.getGuildId(), requester.getUserId());

        Optional<GuildStatusRow> status = guildRepository.findPendingStatus(requester.getUserId());

        assertThat(status).isPresent();
        assertThat(status.get().getGuildId()).isEqualTo(guild.getGuildId());
        assertThat(status.get().getRequestStatus()).isEqualTo("PENDING");
        assertThat(guildRepository.existsPendingJoinRequestByUserId(requester.getUserId())).isTrue();
    }

    @Test
    @DisplayName("길드 상세와 멤버 목록은 캐릭터 정보를 함께 조회한다")
    void findGuildDetailAndMembers() {
        User owner = saveUser("owner@example.com", "예린");
        Guild guild = saveGuild(owner.getUserId(), "잘먹잘싸", "NYAM-A7K3");
        guildRepository.saveMember(ownerMember(guild.getGuildId(), owner.getUserId()));
        insertCharacter(owner.getUserId());

        Optional<GuildDetailRow> detail = guildRepository.findGuildDetail(guild.getGuildId(), owner.getUserId());
        List<GuildMemberRow> members = guildRepository.findActiveMembers(guild.getGuildId());

        assertThat(detail).isPresent();
        assertThat(detail.get().getMyRole()).isEqualTo("OWNER");
        assertThat(members).hasSize(1);
        assertThat(members.get(0).getNickname()).isEqualTo("예린");
        assertThat(members.get(0).getCharacterName()).isEqualTo("냠냠이");
        assertThat(members.get(0).getCharacterLevel()).isEqualTo(7);
    }

    private User saveUser(String email, String nickname) {
        User user = User.builder()
                .email(email)
                .passwordHash("encoded-password")
                .nickname(nickname)
                .profileImageUrl("https://example.com/profile.png")
                .status("ACTIVE")
                .build();
        userRepository.save(user);
        return user;
    }

    private Guild saveGuild(Long ownerUserId, String name, String inviteCode) {
        Guild guild = guild(ownerUserId, name, inviteCode);
        guildRepository.save(guild);
        return guild;
    }

    private Guild guild(Long ownerUserId, String name, String inviteCode) {
        return Guild.builder()
                .name(name)
                .description("건강하게 먹는 길드")
                .inviteCode(inviteCode)
                .ownerUserId(ownerUserId)
                .maxMembers(30)
                .guildPoint(100)
                .visibility("PRIVATE")
                .status("ACTIVE")
                .build();
    }

    private GuildMember ownerMember(Long guildId, Long userId) {
        return GuildMember.builder()
                .guildId(guildId)
                .userId(userId)
                .role("OWNER")
                .build();
    }

    private void insertPendingJoinRequest(Long guildId, Long userId) {
        jdbcTemplate.update(
                """
                INSERT INTO guild_join_requests (
                    guild_id,
                    user_id,
                    message,
                    status
                )
                VALUES (?, ?, ?, 'PENDING')
                """,
                guildId,
                userId,
                "참여하고 싶어요"
        );
    }

    private void insertCharacter(Long userId) {
        jdbcTemplate.update(
                """
                INSERT INTO characters (
                    user_id,
                    name,
                    level,
                    stage,
                    mood,
                    appearance_type
                )
                VALUES (?, '냠냠이', 7, 'BABY', 'HAPPY', 'NORMAL')
                """,
                userId
        );
    }
}
