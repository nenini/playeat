package com.nyamnyam.coach.food.controller;

import com.nyamnyam.coach.food.dto.response.FoodDetailResponse;
import com.nyamnyam.coach.food.dto.response.FoodSearchPageResponse;
import com.nyamnyam.coach.food.dto.response.FrequentFoodListResponse;
import com.nyamnyam.coach.food.service.FoodService;
import com.nyamnyam.coach.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/foods")
public class FoodController implements FoodApiDocs {

    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<FoodSearchPageResponse>> searchFoods(
            @RequestParam String keyword,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        FoodSearchPageResponse response = foodService.searchFoods(keyword, page, size);
        return ResponseEntity.ok(ApiResponse.success(response, "음식 검색에 성공했습니다."));
    }

    @Override
    @GetMapping("/{foodId}")
    public ResponseEntity<ApiResponse<FoodDetailResponse>> getFoodDetail(@PathVariable Long foodId) {
        FoodDetailResponse response = foodService.getFoodDetail(foodId);
        return ResponseEntity.ok(ApiResponse.success(response, "음식 상세 조회에 성공했습니다."));
    }

    @Override
    @GetMapping("/frequent")
    public ResponseEntity<ApiResponse<FrequentFoodListResponse>> getFrequentFoods(
            Authentication authentication,
            @RequestParam(required = false) Integer limit
    ) {
        FrequentFoodListResponse response = foodService.getFrequentFoods(authenticatedUserId(authentication), limit);
        return ResponseEntity.ok(ApiResponse.success(response, "자주 먹은 음식 조회에 성공했습니다."));
    }

    private Long authenticatedUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}
