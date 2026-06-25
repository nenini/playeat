package com.nyamnyam.coach.diet.controller;

import com.nyamnyam.coach.diet.dto.request.DietCreateRequest;
import com.nyamnyam.coach.diet.dto.request.DietUpdateRequest;
import com.nyamnyam.coach.diet.dto.response.DietDayResponse;
import com.nyamnyam.coach.diet.dto.response.DietDeleteResponse;
import com.nyamnyam.coach.diet.dto.response.DietDetailResponse;
import com.nyamnyam.coach.diet.service.DietService;
import com.nyamnyam.coach.global.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/v1/diets")
public class DietController implements DietApiDocs {

    private final DietService dietService;

    public DietController(DietService dietService) {
        this.dietService = dietService;
    }

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<DietDayResponse>> getDietsByDate(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        DietDayResponse response = dietService.getDietsByDate(authenticatedUserId(authentication), date);
        return ResponseEntity.ok(ApiResponse.success(response, "날짜별 식단 조회에 성공했습니다."));
    }

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<DietDetailResponse>> createDiet(
            Authentication authentication,
            @Valid @RequestBody DietCreateRequest request
    ) {
        DietDetailResponse response = dietService.createDiet(authenticatedUserId(authentication), request);
        return ResponseEntity.ok(ApiResponse.success(response, "식단 기록 생성에 성공했습니다."));
    }

    @Override
    @GetMapping("/{dietId}")
    public ResponseEntity<ApiResponse<DietDetailResponse>> getDietDetail(
            Authentication authentication,
            @PathVariable Long dietId
    ) {
        DietDetailResponse response = dietService.getDietDetail(authenticatedUserId(authentication), dietId);
        return ResponseEntity.ok(ApiResponse.success(response, "식단 기록 상세 조회에 성공했습니다."));
    }

    @Override
    @PatchMapping("/{dietId}")
    public ResponseEntity<ApiResponse<DietDetailResponse>> updateDiet(
            Authentication authentication,
            @PathVariable Long dietId,
            @Valid @RequestBody DietUpdateRequest request
    ) {
        DietDetailResponse response = dietService.updateDiet(authenticatedUserId(authentication), dietId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "식단 기록 수정에 성공했습니다."));
    }

    @Override
    @DeleteMapping("/{dietId}")
    public ResponseEntity<ApiResponse<DietDeleteResponse>> deleteDiet(
            Authentication authentication,
            @PathVariable Long dietId
    ) {
        DietDeleteResponse response = dietService.deleteDiet(authenticatedUserId(authentication), dietId);
        return ResponseEntity.ok(ApiResponse.success(response, "식단 기록 삭제에 성공했습니다."));
    }

    private Long authenticatedUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}
