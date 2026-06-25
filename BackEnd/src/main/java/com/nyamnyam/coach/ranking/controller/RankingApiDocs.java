package com.nyamnyam.coach.ranking.controller;

import com.nyamnyam.coach.global.response.ApiResponse;
import com.nyamnyam.coach.ranking.dto.response.BossRankingResponse;
import com.nyamnyam.coach.ranking.dto.response.GuildRankingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;

@Tag(name = "Ranking", description = "Ranking APIs")
public interface RankingApiDocs {

    @Operation(
            summary = "길드 주간 랭킹 조회",
            description = "메인 길드 순위에 사용하는 전체 길드 주간 포인트 랭킹을 조회합니다."
    )
    ResponseEntity<ApiResponse<GuildRankingResponse>> getGuildRankings(
            @Parameter(hidden = true) Authentication authentication,
            LocalDate weekStartDate,
            LocalDate weekEndDate,
            Integer size
    );

    @Operation(
            summary = "보스별 길드 랭킹 조회",
            description = "보스 상세 화면에서 사용하는 서브 랭킹으로, 클리어 여부와 데미지 기준 길드 성과를 조회합니다."
    )
    ResponseEntity<ApiResponse<BossRankingResponse>> getBossRankings(
            @Parameter(hidden = true) Authentication authentication,
            Long bossId
    );
}
