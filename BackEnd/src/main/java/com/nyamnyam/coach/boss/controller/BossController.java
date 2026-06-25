package com.nyamnyam.coach.boss.controller;

import com.nyamnyam.coach.boss.dto.response.BossDetailResponse;
import com.nyamnyam.coach.boss.dto.response.CurrentBossResponse;
import com.nyamnyam.coach.boss.service.BossService;
import com.nyamnyam.coach.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/bosses")
public class BossController implements BossApiDocs {

    private final BossService bossService;

    public BossController(BossService bossService) {
        this.bossService = bossService;
    }

    @Override
    @GetMapping("/current")
    public ResponseEntity<ApiResponse<CurrentBossResponse>> getCurrentBosses(Authentication authentication) {
        CurrentBossResponse response = bossService.getCurrentBosses();
        return ResponseEntity.ok(ApiResponse.success(response, "현재 시즌 보스 조회에 성공했습니다."));
    }

    @Override
    @GetMapping("/{bossId}")
    public ResponseEntity<ApiResponse<BossDetailResponse>> getBossDetail(
            Authentication authentication,
            @PathVariable Long bossId
    ) {
        BossDetailResponse response = bossService.getBossDetail(bossId);
        return ResponseEntity.ok(ApiResponse.success(response, "보스 상세 조회에 성공했습니다."));
    }
}
