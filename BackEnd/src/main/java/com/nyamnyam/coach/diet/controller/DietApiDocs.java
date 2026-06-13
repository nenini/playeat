package com.nyamnyam.coach.diet.controller;

import com.nyamnyam.coach.diet.dto.request.DietCreateRequest;
import com.nyamnyam.coach.diet.dto.request.DietUpdateRequest;
import com.nyamnyam.coach.diet.dto.response.DietDayResponse;
import com.nyamnyam.coach.diet.dto.response.DietDeleteResponse;
import com.nyamnyam.coach.diet.dto.response.DietDetailResponse;
import com.nyamnyam.coach.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;

@Tag(name = "Diet", description = "식단 기록 API")
@SecurityRequirement(name = "BearerAuth")
public interface DietApiDocs {

    @Operation(summary = "날짜별 식단 조회", description = "선택 날짜의 아침, 점심, 간식, 저녁 식단 카드와 일일 합계를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "날짜별 식단 조회 성공",
                    content = @Content(schema = @Schema(implementation = DietDayResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    ResponseEntity<ApiResponse<DietDayResponse>> getDietsByDate(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "조회 날짜", example = "2026-05-15") LocalDate date
    );

    @Operation(summary = "식단 기록 생성", description = "선택 끼니의 식단 기록을 생성합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "식단 기록 생성 성공",
                    content = @Content(schema = @Schema(implementation = DietDetailResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청값 검증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "같은 날짜와 끼니에 이미 기록 존재")
    })
    ResponseEntity<ApiResponse<DietDetailResponse>> createDiet(
            @Parameter(hidden = true) Authentication authentication,
            @Valid DietCreateRequest request
    );

    @Operation(summary = "식단 기록 상세 조회", description = "식단 기록 하나의 상세 정보와 음식 항목을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "식단 기록 상세 조회 성공",
                    content = @Content(schema = @Schema(implementation = DietDetailResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "식단 기록을 찾을 수 없음")
    })
    ResponseEntity<ApiResponse<DietDetailResponse>> getDietDetail(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "식단 기록 ID", example = "1") Long dietId
    );

    @Operation(summary = "식단 기록 수정", description = "식단 기본 정보와 음식 항목을 수정하고 영양 합계를 다시 계산합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "식단 기록 수정 성공",
                    content = @Content(schema = @Schema(implementation = DietDetailResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청값 검증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "식단 기록을 찾을 수 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "같은 날짜와 끼니에 이미 기록 존재")
    })
    ResponseEntity<ApiResponse<DietDetailResponse>> updateDiet(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "식단 기록 ID", example = "1") Long dietId,
            @Valid DietUpdateRequest request
    );

    @Operation(summary = "식단 기록 삭제", description = "식단 기록을 삭제합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "식단 기록 삭제 성공",
                    content = @Content(schema = @Schema(implementation = DietDeleteResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "식단 기록을 찾을 수 없음")
    })
    ResponseEntity<ApiResponse<DietDeleteResponse>> deleteDiet(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "식단 기록 ID", example = "1") Long dietId
    );
}
