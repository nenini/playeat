package com.nyamnyam.coach.ranking.controller;

import com.nyamnyam.coach.global.response.ApiResponse;
import com.nyamnyam.coach.ranking.dto.response.BossRankingResponse;
import com.nyamnyam.coach.ranking.dto.response.GuildRankingResponse;
import com.nyamnyam.coach.ranking.service.RankingService;
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
public class RankingController implements RankingApiDocs {

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @Override
    @GetMapping("/guilds/rankings")
    public ResponseEntity<ApiResponse<GuildRankingResponse>> getGuildRankings(
            Authentication authentication,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStartDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekEndDate,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        GuildRankingResponse response = rankingService.getGuildRankings(
                authenticatedUserId(authentication),
                weekStartDate,
                weekEndDate,
                size
        );
        return ResponseEntity.ok(ApiResponse.success(response, "길드 주간 랭킹 조회에 성공했습니다."));
    }

    @Override
    @GetMapping("/bosses/{bossId}/rankings")
    public ResponseEntity<ApiResponse<BossRankingResponse>> getBossRankings(
            Authentication authentication,
            @PathVariable Long bossId
    ) {
        BossRankingResponse response = rankingService.getBossRankings(authenticatedUserId(authentication), bossId);
        return ResponseEntity.ok(ApiResponse.success(response, "보스별 길드 랭킹 조회에 성공했습니다."));
    }

    private Long authenticatedUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}
