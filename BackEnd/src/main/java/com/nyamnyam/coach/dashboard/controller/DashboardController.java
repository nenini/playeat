package com.nyamnyam.coach.dashboard.controller;

import com.nyamnyam.coach.dashboard.dto.response.BossBattleDashboardResponse;
import com.nyamnyam.coach.dashboard.dto.response.GuildDashboardResponse;
import com.nyamnyam.coach.dashboard.dto.response.GuildWeeklyReportResponse;
import com.nyamnyam.coach.dashboard.service.DashboardService;
import com.nyamnyam.coach.global.response.ApiResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/v1")
public class DashboardController implements DashboardApiDocs {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Override
    @GetMapping("/guilds/{guildId}/dashboard")
    public ResponseEntity<ApiResponse<GuildDashboardResponse>> getGuildDashboard(
            Authentication authentication,
            @PathVariable Long guildId
    ) {
        GuildDashboardResponse response = dashboardService.getGuildDashboard(
                authenticatedUserId(authentication),
                guildId,
                null,
                null
        );
        return ResponseEntity.ok(ApiResponse.success(response, "길드 대시보드 조회에 성공했습니다."));
    }

    @Override
    @GetMapping("/boss-battles/{battleId}/dashboard")
    public ResponseEntity<ApiResponse<BossBattleDashboardResponse>> getBossBattleDashboard(
            Authentication authentication,
            @PathVariable Long battleId
    ) {
        BossBattleDashboardResponse response = dashboardService.getBossBattleDashboard(
                authenticatedUserId(authentication),
                battleId
        );
        return ResponseEntity.ok(ApiResponse.success(response, "보스전 대시보드 조회에 성공했습니다."));
    }

    @Override
    @GetMapping("/guilds/{guildId}/reports/weekly")
    public ResponseEntity<ApiResponse<GuildWeeklyReportResponse>> getGuildWeeklyReport(
            Authentication authentication,
            @PathVariable Long guildId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStartDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekEndDate
    ) {
        GuildWeeklyReportResponse response = dashboardService.getGuildWeeklyReport(
                authenticatedUserId(authentication),
                guildId,
                weekStartDate,
                weekEndDate
        );
        return ResponseEntity.ok(ApiResponse.success(response, "길드 주간 리포트 조회에 성공했습니다."));
    }

    private Long authenticatedUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}
