package com.nyamnyam.coach.analysis.controller;

import com.nyamnyam.coach.analysis.dto.response.AnalysisDailyResponse;
import com.nyamnyam.coach.analysis.dto.response.AnalysisWeeklyResponse;
import com.nyamnyam.coach.analysis.service.AnalysisService;
import com.nyamnyam.coach.global.response.ApiResponse;
import com.nyamnyam.coach.nutrition.dto.response.PeerComparisonListResponse;
import com.nyamnyam.coach.nutrition.service.PeerComparisonService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/v1/analysis")
public class AnalysisController implements AnalysisApiDocs {

    private final AnalysisService analysisService;
    private final PeerComparisonService peerComparisonService;

    public AnalysisController(AnalysisService analysisService, PeerComparisonService peerComparisonService) {
        this.analysisService = analysisService;
        this.peerComparisonService = peerComparisonService;
    }

    @Override
    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<AnalysisDailyResponse>> getDailyAnalysis(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        AnalysisDailyResponse response = analysisService.getDailyAnalysis(authenticatedUserId(authentication), date);
        return ResponseEntity.ok(ApiResponse.success(response, "일별 분석 조회에 성공했습니다."));
    }

    @Override
    @GetMapping("/weekly")
    public ResponseEntity<ApiResponse<AnalysisWeeklyResponse>> getWeeklyAnalysis(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        AnalysisWeeklyResponse response = analysisService.getWeeklyAnalysis(
                authenticatedUserId(authentication),
                startDate,
                endDate
        );
        return ResponseEntity.ok(ApiResponse.success(response, "주간 분석 조회에 성공했습니다."));
    }

    @Override
    @GetMapping("/peer-comparison")
    public ResponseEntity<ApiResponse<PeerComparisonListResponse>> getPeerComparison(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        PeerComparisonListResponse response = peerComparisonService.getPeerComparison(authenticatedUserId(authentication), date);
        return ResponseEntity.ok(ApiResponse.success(response, "또래 비교 조회에 성공했습니다."));
    }

    private Long authenticatedUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}
