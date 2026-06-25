package com.nyamnyam.coach.guild.service;

import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.GuildErrorCode;
import com.nyamnyam.coach.guild.dto.request.GuildChatCreateRequest;
import com.nyamnyam.coach.guild.dto.response.GuildChatListResponse;
import com.nyamnyam.coach.guild.dto.response.GuildChatResponse;
import com.nyamnyam.coach.guild.entity.Guild;
import com.nyamnyam.coach.guild.entity.GuildChat;
import com.nyamnyam.coach.guild.repository.GuildChatRepository;
import com.nyamnyam.coach.guild.repository.GuildRepository;
import com.nyamnyam.coach.guild.repository.row.GuildChatRow;
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
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GuildChatServiceTest {

    @Mock
    private GuildChatRepository guildChatRepository;

    @Mock
    private GuildRepository guildRepository;

    private GuildChatService guildChatService;

    @BeforeEach
    void setUp() {
        guildChatService = new GuildChatService(
                guildChatRepository,
                new GuildValidator(guildRepository)
        );
    }

    @Test
    @DisplayName("길드 멤버는 채팅 목록을 조회할 수 있다")
    void getGuildChats() {
        allowMember();
        when(guildChatRepository.findChatsByGuildId(1L, 30, 0, 1L))
                .thenReturn(List.of(chatRow(10L, 1L, "USER", "안녕하세요", true)));
        when(guildChatRepository.countChatsByGuildId(1L)).thenReturn(1);

        GuildChatListResponse response = guildChatService.getGuildChats(1L, 1L, 0, 30);

        assertThat(response.guildId()).isEqualTo(1L);
        assertThat(response.chats()).hasSize(1);
        assertThat(response.chats().get(0).isMe()).isTrue();
        assertThat(response.hasNext()).isFalse();
    }

    @Test
    @DisplayName("길드 멤버는 USER 채팅 메시지를 전송할 수 있다")
    void createGuildChat() {
        allowMember();
        doAnswer(invocation -> {
            GuildChat chat = invocation.getArgument(0);
            chat.setChatId(10L);
            return null;
        }).when(guildChatRepository).insertGuildChat(any(GuildChat.class));
        when(guildChatRepository.findChatById(10L, 1L))
                .thenReturn(Optional.of(chatRow(10L, 1L, "USER", "오늘 퀘스트 같이 완료해요!", true)));

        GuildChatResponse response = guildChatService.createGuildChat(
                1L,
                1L,
                new GuildChatCreateRequest("오늘 퀘스트 같이 완료해요!")
        );

        ArgumentCaptor<GuildChat> captor = ArgumentCaptor.forClass(GuildChat.class);
        verify(guildChatRepository).insertGuildChat(captor.capture());
        assertThat(captor.getValue().getMessageType()).isEqualTo("USER");
        assertThat(captor.getValue().getSenderUserId()).isEqualTo(1L);
        assertThat(response.message()).isEqualTo("오늘 퀘스트 같이 완료해요!");
    }

    @Test
    @DisplayName("길드원이 아니면 채팅 목록 조회가 불가능하다")
    void getGuildChatsDenied() {
        denyMember();

        assertThatThrownBy(() -> guildChatService.getGuildChats(1L, 99L, 0, 30))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(GuildErrorCode.GUILD_ACCESS_DENIED);

        verify(guildChatRepository, never()).findChatsByGuildId(any(), any(Integer.class), any(Integer.class), any());
    }

    @Test
    @DisplayName("길드원이 아니면 채팅 전송이 불가능하다")
    void createGuildChatDenied() {
        denyMember();

        assertThatThrownBy(() -> guildChatService.createGuildChat(
                1L,
                99L,
                new GuildChatCreateRequest("안녕하세요")
        ))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(GuildErrorCode.GUILD_ACCESS_DENIED);

        verify(guildChatRepository, never()).insertGuildChat(any());
    }

    @Test
    @DisplayName("빈 메시지는 전송할 수 없다")
    void createGuildChatEmptyMessage() {
        allowMember();

        assertThatThrownBy(() -> guildChatService.createGuildChat(1L, 1L, new GuildChatCreateRequest(" ")))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(GuildErrorCode.GUILD_CHAT_MESSAGE_EMPTY);
    }

    @Test
    @DisplayName("1000자를 초과한 메시지는 전송할 수 없다")
    void createGuildChatTooLongMessage() {
        allowMember();
        String message = "a".repeat(1001);

        assertThatThrownBy(() -> guildChatService.createGuildChat(1L, 1L, new GuildChatCreateRequest(message)))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(GuildErrorCode.GUILD_CHAT_MESSAGE_TOO_LONG);
    }

    @Test
    @DisplayName("SYSTEM 메시지를 저장할 수 있다")
    void createSystemMessage() {
        when(guildRepository.findById(1L)).thenReturn(Optional.of(activeGuild()));
        doAnswer(invocation -> {
            GuildChat chat = invocation.getArgument(0);
            chat.setChatId(20L);
            return null;
        }).when(guildChatRepository).insertGuildChat(any(GuildChat.class));
        when(guildChatRepository.findChatById(20L, null))
                .thenReturn(Optional.of(chatRow(20L, null, "SYSTEM", "보스 HP가 감소했습니다.", false)));

        GuildChatResponse response = guildChatService.createSystemMessage(1L, "보스 HP가 감소했습니다.");

        ArgumentCaptor<GuildChat> captor = ArgumentCaptor.forClass(GuildChat.class);
        verify(guildChatRepository).insertGuildChat(captor.capture());
        assertThat(captor.getValue().getMessageType()).isEqualTo("SYSTEM");
        assertThat(captor.getValue().getSenderUserId()).isNull();
        assertThat(response.nickname()).isEqualTo("SYSTEM");
        assertThat(response.isMe()).isFalse();
    }

    private void allowMember() {
        when(guildRepository.findById(1L)).thenReturn(Optional.of(activeGuild()));
        when(guildRepository.findActiveMemberRole(1L, 1L)).thenReturn(Optional.of("MEMBER"));
    }

    private void denyMember() {
        when(guildRepository.findById(1L)).thenReturn(Optional.of(activeGuild()));
        when(guildRepository.findActiveMemberRole(1L, 99L)).thenReturn(Optional.empty());
    }

    private Guild activeGuild() {
        return Guild.builder()
                .guildId(1L)
                .name("잘먹잘싸")
                .status("ACTIVE")
                .maxMembers(30)
                .build();
    }

    private GuildChatRow chatRow(Long chatId, Long userId, String messageType, String message, boolean isMe) {
        GuildChatRow row = new GuildChatRow();
        row.setChatId(chatId);
        row.setGuildId(1L);
        row.setUserId(userId);
        row.setNickname("SYSTEM".equals(messageType) ? "SYSTEM" : "예린");
        row.setProfileImageUrl(userId == null ? null : "https://example.com/profile.png");
        row.setCharacterId(userId == null ? null : 3L);
        row.setCharacterName(userId == null ? null : "냠냠이");
        row.setCharacterLevel(userId == null ? null : 7);
        row.setMessageType(messageType);
        row.setMessage(message);
        row.setCreatedAt(LocalDateTime.of(2026, 6, 10, 10, 30));
        row.setIsMe(isMe);
        return row;
    }
}
