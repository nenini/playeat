package com.nyamnyam.coach.guild.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.GlobalExceptionHandler;
import com.nyamnyam.coach.global.exception.errorcode.CommonErrorCode;
import com.nyamnyam.coach.global.exception.errorcode.GuildErrorCode;
import com.nyamnyam.coach.guild.dto.request.GuildChatCreateRequest;
import com.nyamnyam.coach.guild.dto.response.GuildChatListResponse;
import com.nyamnyam.coach.guild.dto.response.GuildChatResponse;
import com.nyamnyam.coach.guild.service.GuildChatService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GuildChatController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class GuildChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GuildChatService guildChatService;

    @Test
    @DisplayName("GET /v1/guilds/{guildId}/chats 정상 조회")
    void getGuildChats() throws Exception {
        when(guildChatService.getGuildChats(1L, 1L, 0, 30))
                .thenReturn(new GuildChatListResponse(
                        1L,
                        List.of(chatResponse(10L, 1L, "USER", "오늘 퀘스트 같이 완료해요!", true)),
                        0,
                        30,
                        false
                ));

        mockMvc.perform(get("/v1/guilds/1/chats").principal(authentication()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("길드 채팅 목록 조회에 성공했습니다."))
                .andExpect(jsonPath("$.data.guildId").value(1))
                .andExpect(jsonPath("$.data.chats[0].chatId").value(10))
                .andExpect(jsonPath("$.data.chats[0].message").value("오늘 퀘스트 같이 완료해요!"))
                .andExpect(jsonPath("$.data.chats[0].isMe").value(true));
    }

    @Test
    @DisplayName("POST /v1/guilds/{guildId}/chats 정상 전송")
    void createGuildChat() throws Exception {
        when(guildChatService.createGuildChat(eq(1L), eq(1L), any(GuildChatCreateRequest.class)))
                .thenReturn(chatResponse(10L, 1L, "USER", "오늘 퀘스트 같이 완료해요!", true));

        mockMvc.perform(post("/v1/guilds/1/chats")
                        .principal(authentication())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new GuildChatCreateRequest("오늘 퀘스트 같이 완료해요!")
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("길드 채팅 메시지가 전송되었습니다."))
                .andExpect(jsonPath("$.data.messageType").value("USER"))
                .andExpect(jsonPath("$.data.message").value("오늘 퀘스트 같이 완료해요!"));
    }

    @Test
    @DisplayName("길드원이 아닌 사용자가 요청하면 403을 반환한다")
    void guildAccessDenied() throws Exception {
        doThrow(new BusinessException(GuildErrorCode.GUILD_ACCESS_DENIED))
                .when(guildChatService).getGuildChats(1L, 1L, 0, 30);

        mockMvc.perform(get("/v1/guilds/1/chats").principal(authentication()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(GuildErrorCode.GUILD_ACCESS_DENIED.getCode()));
    }

    @Test
    @DisplayName("빈 메시지는 400 validation 응답을 반환한다")
    void createGuildChatValidationFailure() throws Exception {
        mockMvc.perform(post("/v1/guilds/1/chats")
                        .principal(authentication())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "message": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(CommonErrorCode.VALIDATION_FAILED.getCode()))
                .andExpect(jsonPath("$.errors").isArray());
    }

    private GuildChatResponse chatResponse(
            Long chatId,
            Long userId,
            String messageType,
            String message,
            boolean isMe
    ) {
        return new GuildChatResponse(
                chatId,
                1L,
                userId,
                userId == null ? "SYSTEM" : "예린",
                userId == null ? null : "https://example.com/profile.png",
                userId == null ? null : 3L,
                userId == null ? null : "냠냠이",
                userId == null ? null : 7,
                messageType,
                message,
                LocalDateTime.of(2026, 6, 10, 10, 30),
                isMe
        );
    }

    private Authentication authentication() {
        return new UsernamePasswordAuthenticationToken(
                "1",
                "",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
