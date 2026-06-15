package com.nyamnyam.coach.boss.controller;

import com.nyamnyam.coach.boss.dto.request.BossBattleCreateRequest;
import com.nyamnyam.coach.boss.dto.response.BossBattleCreateResponse;
import com.nyamnyam.coach.boss.dto.response.BossBattleDetailResponse;
import com.nyamnyam.coach.boss.dto.response.BossBattleHistoryResponse;
import com.nyamnyam.coach.boss.dto.response.BossBattleHpResponse;
import com.nyamnyam.coach.boss.dto.response.CommonConditionVerifyResponse;
import com.nyamnyam.coach.boss.dto.response.CurrentBossBattleResponse;
import com.nyamnyam.coach.boss.dto.response.RewardClaimResponse;
import com.nyamnyam.coach.boss.service.BossBattleService;
import com.nyamnyam.coach.quest.service.QuestVerificationService;
import com.nyamnyam.coach.quest.service.QuestRewardService;
import com.nyamnyam.coach.global.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class BossBattleController implements BossBattleApiDocs {

    private final BossBattleService bossBattleService;
    private final QuestRewardService questRewardService;
    private final QuestVerificationService questVerificationService;

    public BossBattleController(
            BossBattleService bossBattleService,
            QuestRewardService questRewardService,
            QuestVerificationService questVerificationService
    ) {
        this.bossBattleService = bossBattleService;
        this.questRewardService = questRewardService;
        this.questVerificationService = questVerificationService;
    }

    @Override
    @PostMapping("/guilds/{guildId}/boss-battles")
    public ResponseEntity<ApiResponse<BossBattleCreateResponse>> createBossBattle(
            Authentication authentication,
            @PathVariable Long guildId,
            @Valid @RequestBody BossBattleCreateRequest request
    ) {
        BossBattleCreateResponse response = bossBattleService.createBossBattle(
                guildId,
                authenticatedUserId(authentication),
                request
        );
        return ResponseEntity.ok(ApiResponse.success(response, "길드 보스전이 생성되었습니다."));
    }

    @Override
    @GetMapping("/guilds/{guildId}/boss-battles/current")
    public ResponseEntity<ApiResponse<CurrentBossBattleResponse>> getCurrentBossBattle(
            Authentication authentication,
            @PathVariable Long guildId
    ) {
        CurrentBossBattleResponse response = bossBattleService.getCurrentBossBattle(
                guildId,
                authenticatedUserId(authentication)
        );
        return ResponseEntity.ok(ApiResponse.success(response, "현재 보스전 조회에 성공했습니다."));
    }

    @Override
    @GetMapping("/boss-battles/{battleId}")
    public ResponseEntity<ApiResponse<BossBattleDetailResponse>> getBossBattleDetail(
            Authentication authentication,
            @PathVariable Long battleId
    ) {
        BossBattleDetailResponse response = bossBattleService.getBossBattleDetail(
                battleId,
                authenticatedUserId(authentication)
        );
        return ResponseEntity.ok(ApiResponse.success(response, "보스전 상세 조회에 성공했습니다."));
    }

    @Override
    @GetMapping("/boss-battles/{battleId}/hp")
    public ResponseEntity<ApiResponse<BossBattleHpResponse>> getBossBattleHp(
            Authentication authentication,
            @PathVariable Long battleId
    ) {
        BossBattleHpResponse response = bossBattleService.getBossBattleHp(
                battleId,
                authenticatedUserId(authentication)
        );
        return ResponseEntity.ok(ApiResponse.success(response, "보스전 HP 조회에 성공했습니다."));
    }

    @Override
    @GetMapping("/guilds/{guildId}/boss-battles/history")
    public ResponseEntity<ApiResponse<BossBattleHistoryResponse>> getBossBattleHistory(
            Authentication authentication,
            @PathVariable Long guildId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        BossBattleHistoryResponse response = bossBattleService.getBossBattleHistory(
                guildId,
                authenticatedUserId(authentication),
                page,
                size
        );
        return ResponseEntity.ok(ApiResponse.success(response, "보스전 이력 조회에 성공했습니다."));
    }

    @Override
    @PostMapping("/boss-battles/{battleId}/reward")
    public ResponseEntity<ApiResponse<RewardClaimResponse>> claimBossBattleReward(
            Authentication authentication,
            @PathVariable Long battleId
    ) {
        RewardClaimResponse response = questRewardService.claimBossBattleReward(
                battleId,
                authenticatedUserId(authentication)
        );
        return ResponseEntity.ok(ApiResponse.success(response, "보스전 보상을 수령했습니다."));
    }

    @Override
    @PostMapping("/boss-battles/{battleId}/common-conditions/verify")
    public ResponseEntity<ApiResponse<CommonConditionVerifyResponse>> verifyCommonConditions(
            Authentication authentication,
            @PathVariable Long battleId
    ) {
        CommonConditionVerifyResponse response = questVerificationService.verifyCommonConditions(
                battleId,
                authenticatedUserId(authentication)
        );
        return ResponseEntity.ok(ApiResponse.success(response, "공통 격파 조건 확인에 성공했습니다."));
    }

    private Long authenticatedUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}
