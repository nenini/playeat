package com.nyamnyam.coach.diet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nyamnyam.coach.diet.dto.request.DietCreateRequest;
import com.nyamnyam.coach.diet.dto.request.DietItemRequest;
import com.nyamnyam.coach.diet.dto.response.DailyNutritionSummaryResponse;
import com.nyamnyam.coach.diet.dto.response.DietDayResponse;
import com.nyamnyam.coach.diet.dto.response.DietDeleteResponse;
import com.nyamnyam.coach.diet.dto.response.DietDetailResponse;
import com.nyamnyam.coach.diet.dto.response.DietItemResponse;
import com.nyamnyam.coach.diet.dto.response.DietMealResponse;
import com.nyamnyam.coach.diet.entity.MealType;
import com.nyamnyam.coach.diet.service.DietService;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.GlobalExceptionHandler;
import com.nyamnyam.coach.global.exception.errorcode.DietErrorCode;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DietController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class DietControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DietService dietService;

    @Test
    void getDietsByDate() throws Exception {
        LocalDate date = LocalDate.of(2026, 5, 15);
        when(dietService.getDietsByDate(1L, date)).thenReturn(dayResponse(date));

        mockMvc.perform(get("/v1/diets")
                        .principal(authentication())
                        .param("date", "2026-05-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.date").value("2026-05-15"))
                .andExpect(jsonPath("$.data.meals[0].mealType").value("BREAKFAST"))
                .andExpect(jsonPath("$.data.meals[0].recorded").value(true))
                .andExpect(jsonPath("$.data.dailySummary.totalCalories").value(304))
                .andExpect(jsonPath("$.message").value("날짜별 식단 조회에 성공했습니다."));

        verify(dietService).getDietsByDate(1L, date);
    }

    @Test
    void createDiet() throws Exception {
        DietCreateRequest request = new DietCreateRequest(
                MealType.BREAKFAST,
                LocalDateTime.of(2026, 5, 15, 8, 10),
                null,
                List.of(new DietItemRequest(1L, new BigDecimal("80"), "g"))
        );
        when(dietService.createDiet(eq(1L), eq(request))).thenReturn(detailResponse());

        mockMvc.perform(post("/v1/diets")
                        .principal(authentication())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.dietId").value(1))
                .andExpect(jsonPath("$.data.items[0].foodName").value("오트밀"));
    }

    @Test
    void getDietDetailNotFound() throws Exception {
        when(dietService.getDietDetail(1L, 999L))
                .thenThrow(new BusinessException(DietErrorCode.DIET_NOT_FOUND));

        mockMvc.perform(get("/v1/diets/999")
                        .principal(authentication()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("DIET_NOT_FOUND"));
    }

    @Test
    void updateDiet() throws Exception {
        DietCreateRequest request = new DietCreateRequest(
                MealType.BREAKFAST,
                LocalDateTime.of(2026, 5, 15, 8, 20),
                "운동 전 식사",
                List.of(new DietItemRequest(1L, new BigDecimal("90"), "g"))
        );
        when(dietService.updateDiet(eq(1L), eq(1L), org.mockito.ArgumentMatchers.any()))
                .thenReturn(detailResponse());

        mockMvc.perform(patch("/v1/diets/1")
                        .principal(authentication())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.dietId").value(1));
    }

    @Test
    void deleteDiet() throws Exception {
        LocalDateTime deletedAt = LocalDateTime.of(2026, 5, 15, 9, 0);
        when(dietService.deleteDiet(1L, 1L))
                .thenReturn(new DietDeleteResponse(1L, true, deletedAt));

        mockMvc.perform(delete("/v1/diets/1")
                        .principal(authentication()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.dietId").value(1))
                .andExpect(jsonPath("$.data.deleted").value(true));
    }

    private DietDayResponse dayResponse(LocalDate date) {
        return new DietDayResponse(
                date,
                List.of(new DietMealResponse(
                        MealType.BREAKFAST,
                        "아침",
                        "06-10시",
                        true,
                        1L,
                        LocalDateTime.of(2026, 5, 15, 8, 10),
                        null,
                        new BigDecimal("304.00"),
                        new BigDecimal("10.40"),
                        new BigDecimal("52.80"),
                        new BigDecimal("5.60"),
                        List.of(itemResponse())
                )),
                new DailyNutritionSummaryResponse(
                        new BigDecimal("304.00"),
                        new BigDecimal("2000.00"),
                        15,
                        new BigDecimal("10.40"),
                        new BigDecimal("90.00"),
                        12,
                        new BigDecimal("52.80"),
                        new BigDecimal("280.00"),
                        19,
                        new BigDecimal("5.60"),
                        new BigDecimal("65.00"),
                        9,
                        0,
                        2,
                        0
                )
        );
    }

    private DietDetailResponse detailResponse() {
        return new DietDetailResponse(
                1L,
                MealType.BREAKFAST,
                LocalDateTime.of(2026, 5, 15, 8, 10),
                null,
                new BigDecimal("304.00"),
                new BigDecimal("10.40"),
                new BigDecimal("52.80"),
                new BigDecimal("5.60"),
                new BigDecimal("0.20"),
                new BigDecimal("62.00"),
                List.of(itemResponse()),
                LocalDateTime.of(2026, 5, 15, 8, 11),
                LocalDateTime.of(2026, 5, 15, 8, 11)
        );
    }

    private DietItemResponse itemResponse() {
        return new DietItemResponse(
                10L,
                1L,
                "오트밀",
                null,
                new BigDecimal("80.00"),
                "g",
                new BigDecimal("80.00"),
                null,
                new BigDecimal("304.00"),
                new BigDecimal("10.40"),
                new BigDecimal("52.80"),
                new BigDecimal("5.60"),
                new BigDecimal("0.20"),
                new BigDecimal("62.00")
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
