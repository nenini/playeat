package com.nyamnyam.coach.nutrition.controller;

import com.nyamnyam.coach.global.response.ApiResponse;
import com.nyamnyam.coach.nutrition.dto.response.DailyNutritionAnalysisResponse;
import com.nyamnyam.coach.nutrition.service.NutritionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/v1/nutrition")
public class NutritionController implements NutritionApiDocs {

    private final NutritionService nutritionService;

    public NutritionController(NutritionService nutritionService) {
        this.nutritionService = nutritionService;
    }

    @Override
    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<DailyNutritionAnalysisResponse>> getDailyAnalysis(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        DailyNutritionAnalysisResponse response = nutritionService.getDailyAnalysis(authenticatedUserId(authentication), date);
        return ResponseEntity.ok(ApiResponse.success(response, "일별 영양 분석 조회에 성공했습니다."));
    }

    private Long authenticatedUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}
