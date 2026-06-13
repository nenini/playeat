package com.nyamnyam.coach.guild.repository;

import com.nyamnyam.coach.guild.entity.GuildChat;
import com.nyamnyam.coach.guild.entity.GuildChatMessageType;
import com.nyamnyam.coach.guild.repository.row.GuildChatRow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@ActiveProfiles("test")
@Sql(scripts = "/test-auth-schema.sql")
class GuildChatRepositoryTest {

    private final GuildChatRepository guildChatRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    GuildChatRepositoryTest(
            GuildChatRepository guildChatRepository,
            JdbcTemplate jdbcTemplate
    ) {
        this.guildChatRepository = guildChatRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Test
    @DisplayName("길드 채팅 메시지를 저장하고 사용자 정보와 함께 조회한다")
    void insertGuildChat() {
        Long userId = saveUser("owner@example.com", "예린");
        Long guildId = saveGuild(userId);
        saveCharacter(userId, "냠냠이", 7);

        GuildChat chat = GuildChat.builder()
                .guildId(guildId)
                .senderUserId(userId)
                .messageType(GuildChatMessageType.USER.name())
                .content("오늘 퀘스트 같이 완료해요!")
                .build();

        guildChatRepository.insertGuildChat(chat);

        assertThat(chat.getChatId()).isNotNull();
        GuildChatRow row = guildChatRepository.findChatById(chat.getChatId(), userId).orElseThrow();
        assertThat(row.getNickname()).isEqualTo("예린");
        assertThat(row.getCharacterName()).isEqualTo("냠냠이");
        assertThat(row.getMessage()).isEqualTo("오늘 퀘스트 같이 완료해요!");
        assertThat(row.getIsMe()).isTrue();
    }

    @Test
    @DisplayName("deleted_at이 있는 메시지는 목록에서 제외된다")
    void findChatsExcludesDeletedMessages() {
        Long userId = saveUser("owner@example.com", "예린");
        Long guildId = saveGuild(userId);
        insertChat(guildId, userId, "보이는 메시지", null);
        insertChat(guildId, userId, "삭제된 메시지", "CURRENT_TIMESTAMP");

        List<GuildChatRow> chats = guildChatRepository.findChatsByGuildId(guildId, 30, 0, userId);

        assertThat(chats).hasSize(1);
        assertThat(chats.get(0).getMessage()).isEqualTo("보이는 메시지");
        assertThat(guildChatRepository.countChatsByGuildId(guildId)).isEqualTo(1);
    }

    @Test
    @DisplayName("최신 메시지 기준 페이지네이션 후 응답은 시간 오름차순으로 조회한다")
    void findChatsWithLatestPaginationAndAscendingResponse() {
        Long userId = saveUser("owner@example.com", "예린");
        Long guildId = saveGuild(userId);
        insertChat(guildId, userId, "첫 번째", null);
        insertChat(guildId, userId, "두 번째", null);
        insertChat(guildId, userId, "세 번째", null);

        List<GuildChatRow> firstPage = guildChatRepository.findChatsByGuildId(guildId, 2, 0, userId);
        List<GuildChatRow> secondPage = guildChatRepository.findChatsByGuildId(guildId, 2, 2, userId);

        assertThat(firstPage).extracting(GuildChatRow::getMessage)
                .containsExactly("두 번째", "세 번째");
        assertThat(secondPage).extracting(GuildChatRow::getMessage)
                .containsExactly("첫 번째");
    }

    private Long saveUser(String email, String nickname) {
        jdbcTemplate.update(
                """
                        INSERT INTO users (email, password_hash, nickname, status)
                        VALUES (?, 'encoded-password', ?, 'ACTIVE')
                        """,
                email,
                nickname
        );
        return jdbcTemplate.queryForObject("SELECT user_id FROM users WHERE email = ?", Long.class, email);
    }

    private Long saveGuild(Long ownerUserId) {
        jdbcTemplate.update(
                """
                        INSERT INTO guilds (name, description, invite_code, owner_user_id)
                        VALUES ('잘먹잘싸', '건강하게 먹는 길드', 'NYAM-A7K3', ?)
                        """,
                ownerUserId
        );
        Long guildId = jdbcTemplate.queryForObject("SELECT guild_id FROM guilds WHERE name = '잘먹잘싸'", Long.class);
        jdbcTemplate.update(
                """
                        INSERT INTO guild_members (guild_id, user_id, role)
                        VALUES (?, ?, 'OWNER')
                        """,
                guildId,
                ownerUserId
        );
        return guildId;
    }

    private void saveCharacter(Long userId, String name, int level) {
        jdbcTemplate.update(
                """
                        INSERT INTO characters (user_id, name, level)
                        VALUES (?, ?, ?)
                        """,
                userId,
                name,
                level
        );
    }

    private void insertChat(Long guildId, Long userId, String content, String deletedAtExpression) {
        if (deletedAtExpression == null) {
            jdbcTemplate.update(
                    """
                            INSERT INTO guild_chats (guild_id, sender_user_id, message_type, content)
                            VALUES (?, ?, 'USER', ?)
                            """,
                    guildId,
                    userId,
                    content
            );
            return;
        }
        jdbcTemplate.update(
                """
                        INSERT INTO guild_chats (guild_id, sender_user_id, message_type, content, deleted_at)
                        VALUES (?, ?, 'USER', ?, CURRENT_TIMESTAMP)
                        """,
                guildId,
                userId,
                content
        );
    }
}
