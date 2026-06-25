package com.nyamnyam.coach.boss.controller;

import com.nyamnyam.coach.boss.dto.response.BossDetailResponse;
import com.nyamnyam.coach.boss.dto.response.CurrentBossResponse;
import com.nyamnyam.coach.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

@Tag(name = "Boss", description = "Boss master APIs")
public interface BossApiDocs {

    @Operation(summary = "현재 시즌 보스 조회", description = "현재 시즌의 EASY, NORMAL, HARD 보스와 공통 격파 조건을 조회합니다.")
    ResponseEntity<ApiResponse<CurrentBossResponse>> getCurrentBosses(
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "보스 상세 조회", description = "특정 ACTIVE 보스와 해당 시즌의 공통 격파 조건을 조회합니다.")
    ResponseEntity<ApiResponse<BossDetailResponse>> getBossDetail(
            @Parameter(hidden = true) Authentication authentication,
            Long bossId
    );
}
