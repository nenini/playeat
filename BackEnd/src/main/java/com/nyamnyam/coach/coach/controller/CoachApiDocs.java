package com.nyamnyam.coach.coach.controller;

import com.nyamnyam.coach.coach.dto.request.CoachSelectRequest;
import com.nyamnyam.coach.coach.dto.response.CoachFeedbackResponse;
import com.nyamnyam.coach.coach.dto.response.CoachListResponse;
import com.nyamnyam.coach.coach.dto.response.CoachResponse;
import com.nyamnyam.coach.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

@Tag(name = "Coach", description = "AI 코치 API")
public interface CoachApiDocs {

    @Operation(summary = "코치 목록 조회", description = "선택 가능한 코치 목록과 현재 선택 여부를 조회합니다.")
    @SecurityRequirement(name = "BearerAuth")
    ResponseEntity<ApiResponse<CoachListResponse>> getCoaches(@Parameter(hidden = true) Authentication authentication);

    @Operation(summary = "내 코치 선택", description = "현재 로그인한 사용자의 코치를 선택합니다.")
    @SecurityRequirement(name = "BearerAuth")
    ResponseEntity<ApiResponse<CoachResponse>> selectCoach(
            @Parameter(hidden = true) Authentication authentication,
            CoachSelectRequest request
    );

    @Operation(summary = "끼니 피드백 생성", description = "선택된 코치 스타일로 식단 기록에 대한 피드백을 생성합니다.")
    @SecurityRequirement(name = "BearerAuth")
    ResponseEntity<ApiResponse<CoachFeedbackResponse>> createDietFeedback(
            @Parameter(hidden = true) Authentication authentication,
            Long dietId
    );

    @Operation(summary = "코치별 끼니 피드백 생성", description = "활성화된 모든 코치 스타일로 식단 기록에 대한 피드백을 생성합니다.")
    @SecurityRequirement(name = "BearerAuth")
    ResponseEntity<ApiResponse<List<CoachFeedbackResponse>>> createDietFeedbacksForAllCoaches(
            @Parameter(hidden = true) Authentication authentication,
            Long dietId
    );

    @Operation(summary = "끼니 피드백 조회", description = "식단 기록에 대해 마지막으로 생성된 코치 피드백을 조회합니다.")
    @SecurityRequirement(name = "BearerAuth")
    ResponseEntity<ApiResponse<CoachFeedbackResponse>> getDietFeedback(
            @Parameter(hidden = true) Authentication authentication,
            Long dietId
    );
}
