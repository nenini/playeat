package com.nyamnyam.coach.character.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nyamnyam.coach.character.dto.request.UpdateCharacterNameRequest;
import com.nyamnyam.coach.character.dto.response.CharacterResponse;
import com.nyamnyam.coach.character.dto.response.UpdateCharacterNameResponse;
import com.nyamnyam.coach.character.dto.response.XpHistoryListResponse;
import com.nyamnyam.coach.character.dto.response.XpHistoryResponse;
import com.nyamnyam.coach.character.service.CharacterService;
import com.nyamnyam.coach.global.exception.GlobalExceptionHandler;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CharacterController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class CharacterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CharacterService characterService;

    @Test
    void getMyCharacter() throws Exception {
        when(characterService.getMyCharacter(1L))
                .thenReturn(new CharacterResponse(
                        1L,
                        1L,
                        "nyam",
                        7,
                        950,
                        1200,
                        79.2,
                        "EGG",
                        "HAPPY",
                        "NORMAL",
                        15,
                        21,
                        LocalDateTime.of(2026, 6, 11, 9, 0),
                        LocalDateTime.of(2026, 6, 11, 9, 10)
                ));

        mockMvc.perform(get("/v1/characters/me").principal(authentication()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.characterId").value(1))
                .andExpect(jsonPath("$.data.requiredXp").value(1200))
                .andExpect(jsonPath("$.data.xpProgressRate").value(79.2))
                .andExpect(jsonPath("$.data.bestStreakDays").value(21));
    }

    @Test
    void updateName() throws Exception {
        when(characterService.updateName(eq(1L), any(UpdateCharacterNameRequest.class)))
                .thenReturn(new UpdateCharacterNameResponse(
                        1L,
                        "newnyam",
                        LocalDateTime.of(2026, 6, 11, 10, 0)
                ));

        mockMvc.perform(patch("/v1/characters/me/name")
                        .principal(authentication())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateCharacterNameRequest("newnyam"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("newnyam"));
    }

    @Test
    void updateNameWithBlankNameFails() throws Exception {
        mockMvc.perform(patch("/v1/characters/me/name")
                        .principal(authentication())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateCharacterNameRequest(" "))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));
    }

    @Test
    void getXpHistory() throws Exception {
        when(characterService.getXpHistory(1L, 0, 20, "DIET"))
                .thenReturn(new XpHistoryListResponse(
                        List.of(new XpHistoryResponse(
                                1L,
                                "DIET",
                                31L,
                                120,
                                "recorded meal",
                                LocalDateTime.of(2026, 6, 11, 8, 30)
                        )),
                        0,
                        20,
                        1L,
                        false
                ));

        mockMvc.perform(get("/v1/characters/me/xp-history")
                        .principal(authentication())
                        .param("page", "0")
                        .param("size", "20")
                        .param("sourceType", "DIET"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].sourceType").value("DIET"))
                .andExpect(jsonPath("$.data.hasNext").value(false));
    }

    private Authentication authentication() {
        return new UsernamePasswordAuthenticationToken(
                "1",
                "",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
