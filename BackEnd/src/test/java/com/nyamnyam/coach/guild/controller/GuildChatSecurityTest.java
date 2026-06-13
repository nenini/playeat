package com.nyamnyam.coach.guild.controller;

import com.nyamnyam.coach.auth.jwt.JwtTokenProvider;
import com.nyamnyam.coach.global.config.SecurityConfig;
import com.nyamnyam.coach.global.exception.errorcode.CommonErrorCode;
import com.nyamnyam.coach.global.security.CustomAccessDeniedHandler;
import com.nyamnyam.coach.global.security.CustomAuthenticationEntryPoint;
import com.nyamnyam.coach.guild.service.GuildChatService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GuildChatController.class)
@AutoConfigureMockMvc
@Import({
        SecurityConfig.class,
        JwtTokenProvider.class,
        CustomAuthenticationEntryPoint.class,
        CustomAccessDeniedHandler.class
})
@TestPropertySource(properties = {
        "jwt.secret=dGVzdC1zZWNyZXQta2V5LW11c3QtYmUtYXQtbGVhc3QtMjU2LWJpdHMtbG9uZy10ZXN0",
        "jwt.access-token-expiration=3600000",
        "jwt.refresh-token-expiration=604800000"
})
class GuildChatSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GuildChatService guildChatService;

    @Test
    @DisplayName("인증 없이 길드 채팅 조회 요청 시 401을 반환한다")
    void getGuildChatsWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/v1/guilds/1/chats"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(CommonErrorCode.UNAUTHORIZED.getCode()));
    }
}
