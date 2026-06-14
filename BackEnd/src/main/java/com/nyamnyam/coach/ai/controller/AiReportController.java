package com.nyamnyam.coach.ai.controller;

import com.nyamnyam.coach.ai.dto.request.DailyAiReportRequest;
import com.nyamnyam.coach.ai.dto.request.WeeklyAiReportRequest;
import com.nyamnyam.coach.ai.dto.response.AiReportResponse;
import com.nyamnyam.coach.ai.service.AiReportService;
import com.nyamnyam.coach.global.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/v1/ai/reports")
public class AiReportController implements AiReportApiDocs {

    private final AiReportService aiReportService;

    public AiReportController(AiReportService aiReportService) {
        this.aiReportService = aiReportService;
    }

    @Override
    @PostMapping("/daily")
    public ResponseEntity<ApiResponse<AiReportResponse>> createDailyReport(
            Authentication authentication,
            @Valid @RequestBody DailyAiReportRequest request
    ) {
        AiReportResponse response = aiReportService.createDailyReport(authenticatedUserId(authentication), request.date());
        return ResponseEntity.ok(ApiResponse.success(response, "일간 AI 리포트 생성에 성공했습니다."));
    }

    @Override
    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<AiReportResponse>> getDailyReport(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        AiReportResponse response = aiReportService.getDailyReport(authenticatedUserId(authentication), date);
        return ResponseEntity.ok(ApiResponse.success(response, "일간 AI 리포트 조회에 성공했습니다."));
    }

    @Override
    @PostMapping("/weekly")
    public ResponseEntity<ApiResponse<AiReportResponse>> createWeeklyReport(
            Authentication authentication,
            @Valid @RequestBody WeeklyAiReportRequest request
    ) {
        AiReportResponse response = aiReportService.createWeeklyReport(
                authenticatedUserId(authentication),
                request.startDate(),
                request.endDate()
        );
        return ResponseEntity.ok(ApiResponse.success(response, "주간 AI 리포트 생성에 성공했습니다."));
    }

    @Override
    @GetMapping("/weekly")
    public ResponseEntity<ApiResponse<AiReportResponse>> getWeeklyReport(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        AiReportResponse response = aiReportService.getWeeklyReport(authenticatedUserId(authentication), startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(response, "주간 AI 리포트 조회에 성공했습니다."));
    }

    private Long authenticatedUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}
