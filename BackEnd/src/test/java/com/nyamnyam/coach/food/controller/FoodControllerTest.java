package com.nyamnyam.coach.food.controller;

import com.nyamnyam.coach.food.dto.response.FoodDetailResponse;
import com.nyamnyam.coach.food.dto.response.FoodSearchPageResponse;
import com.nyamnyam.coach.food.dto.response.FoodSearchResponse;
import com.nyamnyam.coach.food.dto.response.FrequentFoodListResponse;
import com.nyamnyam.coach.food.dto.response.FrequentFoodResponse;
import com.nyamnyam.coach.food.service.FoodService;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.GlobalExceptionHandler;
import com.nyamnyam.coach.global.exception.errorcode.FoodErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FoodController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class FoodControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FoodService foodService;

    @Test
    void searchFoods() throws Exception {
        FoodSearchPageResponse response = new FoodSearchPageResponse(
                List.of(new FoodSearchResponse(
                        1L,
                        "삶은 계란",
                        "테스트브랜드",
                        "난류",
                        new BigDecimal("100.00"),
                        "g",
                        new BigDecimal("60.00"),
                        "g",
                        new BigDecimal("60.0000"),
                        new BigDecimal("78.00"),
                        new BigDecimal("6.50"),
                        new BigDecimal("0.50"),
                        new BigDecimal("5.30"),
                        new BigDecimal("0.20"),
                        new BigDecimal("62.00")
                )),
                0,
                20,
                1L,
                1
        );
        when(foodService.searchFoods("계란", 0, 20)).thenReturn(response);

        mockMvc.perform(get("/v1/foods")
                        .param("keyword", "계란")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.foods[0].foodId").value(1))
                .andExpect(jsonPath("$.data.foods[0].name").value("삶은 계란"))
                .andExpect(jsonPath("$.data.foods[0].protein").value(6.50))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.message").value("음식 검색에 성공했습니다."));

        verify(foodService).searchFoods("계란", 0, 20);
    }

    @Test
    void getFoodDetail() throws Exception {
        when(foodService.getFoodDetail(1L)).thenReturn(new FoodDetailResponse(
                1L,
                "EXT-1",
                "삶은 계란",
                "테스트브랜드",
                "난류",
                new BigDecimal("100.00"),
                "g",
                new BigDecimal("60.00"),
                "g",
                new BigDecimal("60.0000"),
                new BigDecimal("78.00"),
                new BigDecimal("6.50"),
                new BigDecimal("0.50"),
                new BigDecimal("5.30"),
                new BigDecimal("0.20"),
                new BigDecimal("62.00"),
                new BigDecimal("1.20"),
                new BigDecimal("1.10"),
                new BigDecimal("95.00"),
                new BigDecimal("75.00"),
                new BigDecimal("80.00"),
                new BigDecimal("15.00"),
                new BigDecimal("70.00"),
                "TEST"
        ));

        mockMvc.perform(get("/v1/foods/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.foodId").value(1))
                .andExpect(jsonPath("$.data.externalFoodCode").value("EXT-1"))
                .andExpect(jsonPath("$.data.fiber").value(1.20))
                .andExpect(jsonPath("$.message").value("음식 상세 조회에 성공했습니다."));
    }

    @Test
    void getFoodDetailNotFound() throws Exception {
        when(foodService.getFoodDetail(999L))
                .thenThrow(new BusinessException(FoodErrorCode.FOOD_NOT_FOUND));

        mockMvc.perform(get("/v1/foods/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("FOOD_NOT_FOUND"));
    }

    @Test
    void getFrequentFoods() throws Exception {
        LocalDateTime lastRecordedAt = LocalDateTime.of(2026, 6, 12, 19, 30);
        when(foodService.getFrequentFoods(1L, 5)).thenReturn(new FrequentFoodListResponse(
                List.of(new FrequentFoodResponse(
                        1L,
                        "삶은 계란",
                        3L,
                        lastRecordedAt
                ))
        ));

        mockMvc.perform(get("/v1/foods/frequent")
                        .principal(authentication())
                        .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.foods[0].foodId").value(1))
                .andExpect(jsonPath("$.data.foods[0].recordCount").value(3))
                .andExpect(jsonPath("$.data.foods[0].lastRecordedAt").value("2026-06-12T19:30:00"))
                .andExpect(jsonPath("$.message").value("자주 먹은 음식 조회에 성공했습니다."));

        verify(foodService).getFrequentFoods(eq(1L), eq(5));
    }

    private Authentication authentication() {
        return new UsernamePasswordAuthenticationToken(
                "1",
                "",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
