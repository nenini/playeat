package com.nyamnyam.coach.dashboard.controller;

import com.nyamnyam.coach.dashboard.dto.response.BossBattleDashboardResponse;
import com.nyamnyam.coach.dashboard.dto.response.GuildDashboardResponse;
import com.nyamnyam.coach.dashboard.dto.response.GuildWeeklyReportResponse;
import com.nyamnyam.coach.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;

@Tag(name = "Dashboard", description = "Dashboard APIs")
public interface DashboardApiDocs {

    @Operation(
            summary = "길드 대시보드 조회",
            description = "길드 메인 화면용 대시보드를 조회합니다. myRank는 전체 길드 주간 포인트 랭킹 기준입니다."
    )
    ResponseEntity<ApiResponse<GuildDashboardResponse>> getGuildDashboard(
            @Parameter(hidden = true) Authentication authentication,
            Long guildId
    );

    @Operation(summary = "보스전 대시보드 조회", description = "보스전 메인 화면용 대시보드를 조회합니다.")
    ResponseEntity<ApiResponse<BossBattleDashboardResponse>> getBossBattleDashboard(
            @Parameter(hidden = true) Authentication authentication,
            Long battleId
    );

    @Operation(summary = "길드 주간 리포트 조회", description = "길드 주간 리포트를 조회합니다.")
    ResponseEntity<ApiResponse<GuildWeeklyReportResponse>> getGuildWeeklyReport(
            @Parameter(hidden = true) Authentication authentication,
            Long guildId,
            LocalDate weekStartDate,
            LocalDate weekEndDate
    );
}
