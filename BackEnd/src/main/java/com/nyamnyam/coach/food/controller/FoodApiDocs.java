package com.nyamnyam.coach.food.controller;

import com.nyamnyam.coach.food.dto.response.FoodDetailResponse;
import com.nyamnyam.coach.food.dto.response.FoodSearchPageResponse;
import com.nyamnyam.coach.food.dto.response.FrequentFoodListResponse;
import com.nyamnyam.coach.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

@Tag(name = "Food", description = "음식 검색 API")
public interface FoodApiDocs {

    @Operation(summary = "음식 검색", description = "음식 이름으로 음식 DB를 검색합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "음식 검색 성공",
                    content = @Content(schema = @Schema(implementation = FoodSearchPageResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "검색어가 올바르지 않음")
    })
    ResponseEntity<ApiResponse<FoodSearchPageResponse>> searchFoods(
            @Parameter(description = "검색어", example = "계란") String keyword,
            @Parameter(description = "페이지 번호", example = "0") Integer page,
            @Parameter(description = "페이지 크기", example = "20") Integer size
    );

    @Operation(summary = "음식 상세 조회", description = "음식 ID로 영양성분과 입력 보조 정보를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "음식 상세 조회 성공",
                    content = @Content(schema = @Schema(implementation = FoodDetailResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "음식을 찾을 수 없음")
    })
    ResponseEntity<ApiResponse<FoodDetailResponse>> getFoodDetail(
            @Parameter(description = "음식 ID", example = "1") Long foodId
    );

    @Operation(summary = "자주 먹은 음식 조회", description = "현재 로그인한 사용자의 식단 기록 기준으로 자주 먹은 음식을 조회합니다.")
    @SecurityRequirement(name = "BearerAuth")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "자주 먹은 음식 조회 성공",
                    content = @Content(schema = @Schema(implementation = FrequentFoodListResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    ResponseEntity<ApiResponse<FrequentFoodListResponse>> getFrequentFoods(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "조회 개수", example = "10") Integer limit
    );
}
