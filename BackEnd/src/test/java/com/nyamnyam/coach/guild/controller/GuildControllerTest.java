package com.nyamnyam.coach.guild.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.GlobalExceptionHandler;
import com.nyamnyam.coach.global.exception.errorcode.CommonErrorCode;
import com.nyamnyam.coach.global.exception.errorcode.GuildErrorCode;
import com.nyamnyam.coach.guild.dto.request.GuildCreateRequest;
import com.nyamnyam.coach.guild.dto.response.GuildCreateResponse;
import com.nyamnyam.coach.guild.dto.response.GuildDetailResponse;
import com.nyamnyam.coach.guild.dto.response.GuildListResponse;
import com.nyamnyam.coach.guild.dto.response.GuildMemberListResponse;
import com.nyamnyam.coach.guild.dto.response.GuildMemberResponse;
import com.nyamnyam.coach.guild.dto.response.GuildSummaryResponse;
import com.nyamnyam.coach.guild.dto.response.MyGuildListResponse;
import com.nyamnyam.coach.guild.dto.response.MyGuildResponse;
import com.nyamnyam.coach.guild.dto.response.MyGuildStatusResponse;
import com.nyamnyam.coach.guild.entity.MyGuildJoinStatus;
import com.nyamnyam.coach.guild.service.GuildService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GuildController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class GuildControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GuildService guildService;

    @Test
    @DisplayName("길드 탐색 목록 조회 성공 응답을 반환한다")
    void getGuilds() throws Exception {
        when(guildService.getGuilds(1L, 0, 10, null, "guildPoint"))
                .thenReturn(new GuildListResponse(
                        List.of(new GuildSummaryResponse(
                                1L,
                                "잘먹잘싸",
                                "건강하게 먹는 길드",
                                "NYAM-A7K3",
                                6,
                                30,
                                2840,
                                "예린",
                                MyGuildJoinStatus.PENDING,
                                12L,
                                false
                        )),
                        0,
                        10,
                        false
                ));

        mockMvc.perform(get("/v1/guilds").principal(authentication()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.guilds[0].guildId").value(1))
                .andExpect(jsonPath("$.data.guilds[0].inviteCode").value("NYAM-A7K3"))
                .andExpect(jsonPath("$.data.guilds[0].myJoinStatus").value("PENDING"))
                .andExpect(jsonPath("$.data.guilds[0].joinRequestId").value(12))
                .andExpect(jsonPath("$.data.hasNext").value(false));
    }

    @Test
    @DisplayName("길드 생성 성공 응답을 반환한다")
    void createGuild() throws Exception {
        when(guildService.createGuild(eq(1L), any(GuildCreateRequest.class)))
                .thenReturn(new GuildCreateResponse(
                        1L,
                        "잘먹잘싸",
                        "건강하게 먹는 길드",
                        "NYAM-A7K3",
                        1L,
                        "OWNER",
                        1,
                        30,
                        "PRIVATE",
                        "ACTIVE",
                        LocalDateTime.of(2026, 6, 9, 10, 30)
                ));

        mockMvc.perform(post("/v1/guilds")
                        .principal(authentication())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new GuildCreateRequest("잘먹잘싸", "건강하게 먹는 길드", 30)
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.guildId").value(1))
                .andExpect(jsonPath("$.data.myRole").value("OWNER"))
                .andExpect(jsonPath("$.data.visibility").value("PRIVATE"));
    }

    @Test
    @DisplayName("길드 생성 입력값 검증 실패 시 400과 errors를 반환한다")
    void createGuildValidationFailure() throws Exception {
        mockMvc.perform(post("/v1/guilds")
                        .principal(authentication())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "maxMembers": 31
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(CommonErrorCode.VALIDATION_FAILED.getCode()))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @DisplayName("이미 가입한 길드가 있으면 409 JSON 에러 응답을 반환한다")
    void createGuildAlreadyJoined() throws Exception {
        doThrow(new BusinessException(GuildErrorCode.USER_ALREADY_JOINED_GUILD))
                .when(guildService).createGuild(eq(1L), any(GuildCreateRequest.class));

        mockMvc.perform(post("/v1/guilds")
                        .principal(authentication())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new GuildCreateRequest("잘먹잘싸", null, 30)
                        )))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(GuildErrorCode.USER_ALREADY_JOINED_GUILD.getCode()));
    }

    @Test
    @DisplayName("내 길드 목록 조회 성공 응답을 반환한다")
    void getMyGuilds() throws Exception {
        when(guildService.getMyGuilds(1L))
                .thenReturn(new MyGuildListResponse(
                        List.of(new MyGuildResponse(
                                1L,
                                "잘먹잘싸",
                                "건강하게 먹는 길드",
                                "NYAM-A7K3",
                                6,
                                30,
                                2840,
                                "OWNER",
                                LocalDateTime.of(2026, 6, 9, 10, 30)
                        ))
                ));

        mockMvc.perform(get("/v1/guilds/me").principal(authentication()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.guilds[0].guildId").value(1))
                .andExpect(jsonPath("$.data.guilds[0].myRole").value("OWNER"));
    }

    @Test
    @DisplayName("내 길드 상태 조회 성공 응답을 반환한다")
    void getMyGuildStatus() throws Exception {
        when(guildService.getMyGuildStatus(1L))
                .thenReturn(new MyGuildStatusResponse(
                        MyGuildJoinStatus.PENDING,
                        new MyGuildStatusResponse.GuildStatusInfo(
                                1L,
                                "잘먹잘싸",
                                "NYAM-A7K3",
                                null
                        ),
                        new MyGuildStatusResponse.JoinRequestInfo(
                                12L,
                                "PENDING",
                                LocalDateTime.of(2026, 6, 9, 10, 30)
                        )
                ));

        mockMvc.perform(get("/v1/guilds/me/status").principal(authentication()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.guild.guildId").value(1))
                .andExpect(jsonPath("$.data.joinRequest.requestId").value(12));
    }

    @Test
    @DisplayName("길드 상세 조회 성공 응답을 반환한다")
    void getGuildDetail() throws Exception {
        when(guildService.getGuildDetail(1L, 1L))
                .thenReturn(new GuildDetailResponse(
                        1L,
                        "잘먹잘싸",
                        "건강하게 먹는 길드",
                        "NYAM-A7K3",
                        1L,
                        "예린",
                        6,
                        30,
                        2840,
                        "PRIVATE",
                        "ACTIVE",
                        "OWNER",
                        LocalDateTime.of(2026, 6, 9, 10, 30),
                        LocalDateTime.of(2026, 6, 9, 10, 30)
                ));

        mockMvc.perform(get("/v1/guilds/1").principal(authentication()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.guildId").value(1))
                .andExpect(jsonPath("$.data.ownerNickname").value("예린"))
                .andExpect(jsonPath("$.data.myRole").value("OWNER"));
    }

    @Test
    @DisplayName("길드원이 아니면 길드 상세 조회에서 403 JSON 에러 응답을 반환한다")
    void getGuildDetailAccessDenied() throws Exception {
        doThrow(new BusinessException(GuildErrorCode.GUILD_ACCESS_DENIED))
                .when(guildService).getGuildDetail(1L, 1L);

        mockMvc.perform(get("/v1/guilds/1").principal(authentication()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(GuildErrorCode.GUILD_ACCESS_DENIED.getCode()));
    }

    @Test
    @DisplayName("길드원 목록 조회 성공 응답을 반환한다")
    void getGuildMembers() throws Exception {
        when(guildService.getGuildMembers(1L, 1L))
                .thenReturn(new GuildMemberListResponse(
                        List.of(new GuildMemberResponse(
                                10L,
                                1L,
                                "예린",
                                "https://example.com/profile.png",
                                20L,
                                "냠냠이",
                                7,
                                "BABY",
                                "HAPPY",
                                "NORMAL",
                                "OWNER",
                                LocalDateTime.of(2026, 6, 9, 10, 30),
                                true
                        ))
                ));

        mockMvc.perform(get("/v1/guilds/1/members").principal(authentication()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.members[0].userId").value(1))
                .andExpect(jsonPath("$.data.members[0].characterLevel").value(7))
                .andExpect(jsonPath("$.data.members[0].isMe").value(true));
    }

    private Authentication authentication() {
        return new UsernamePasswordAuthenticationToken(
                "1",
                "",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
