package com.nyamnyam.coach.boss.controller;

import com.nyamnyam.coach.boss.dto.response.BossBattleQuestListResponse;
import com.nyamnyam.coach.boss.dto.response.MyQuestResponse;
import com.nyamnyam.coach.boss.dto.response.QuestContributionListResponse;
import com.nyamnyam.coach.boss.dto.response.QuestDetailResponse;
import com.nyamnyam.coach.boss.dto.response.QuestGenerateResponse;
import com.nyamnyam.coach.boss.dto.response.QuestVerifyResponse;
import com.nyamnyam.coach.boss.dto.response.RewardClaimResponse;
import com.nyamnyam.coach.boss.service.QuestRewardService;
import com.nyamnyam.coach.boss.service.QuestService;
import com.nyamnyam.coach.boss.service.QuestVerificationService;
import com.nyamnyam.coach.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class QuestController implements QuestApiDocs {

    private final QuestService questService;
    private final QuestVerificationService questVerificationService;
    private final QuestRewardService questRewardService;

    public QuestController(
            QuestService questService,
            QuestVerificationService questVerificationService,
            QuestRewardService questRewardService
    ) {
        this.questService = questService;
        this.questVerificationService = questVerificationService;
        this.questRewardService = questRewardService;
    }

    @Override
    @GetMapping("/boss-battles/{battleId}/quests")
    public ResponseEntity<ApiResponse<BossBattleQuestListResponse>> getBattleQuests(
            Authentication authentication,
            @PathVariable Long battleId
    ) {
        BossBattleQuestListResponse response = questService.getBattleQuests(
                battleId,
                authenticatedUserId(authentication)
        );
        return ResponseEntity.ok(ApiResponse.success(response, "보스전 퀘스트 목록 조회에 성공했습니다."));
    }

    @Override
    @GetMapping("/boss-battles/{battleId}/quests/me")
    public ResponseEntity<ApiResponse<MyQuestResponse>> getMyQuest(
            Authentication authentication,
            @PathVariable Long battleId
    ) {
        MyQuestResponse response = questService.getMyQuest(
                battleId,
                authenticatedUserId(authentication)
        );
        return ResponseEntity.ok(ApiResponse.success(response, "내 퀘스트 조회에 성공했습니다."));
    }

    @Override
    @GetMapping("/quests/{questId}")
    public ResponseEntity<ApiResponse<QuestDetailResponse>> getQuestDetail(
            Authentication authentication,
            @PathVariable Long questId
    ) {
        QuestDetailResponse response = questService.getQuestDetail(
                questId,
                authenticatedUserId(authentication)
        );
        return ResponseEntity.ok(ApiResponse.success(response, "퀘스트 상세 조회에 성공했습니다."));
    }

    @Override
    @PostMapping("/boss-battles/{battleId}/quests/generate")
    public ResponseEntity<ApiResponse<QuestGenerateResponse>> generateQuests(
            Authentication authentication,
            @PathVariable Long battleId
    ) {
        QuestGenerateResponse response = questService.generateQuests(
                battleId,
                authenticatedUserId(authentication)
        );
        return ResponseEntity.ok(ApiResponse.success(response, "보스전 퀘스트 생성에 성공했습니다."));
    }

    @Override
    @GetMapping("/boss-battles/{battleId}/quests/contributions")
    public ResponseEntity<ApiResponse<QuestContributionListResponse>> getQuestContributions(
            Authentication authentication,
            @PathVariable Long battleId
    ) {
        QuestContributionListResponse response = questService.getQuestContributions(
                battleId,
                authenticatedUserId(authentication)
        );
        return ResponseEntity.ok(ApiResponse.success(response, "보스전 퀘스트 기여도 조회에 성공했습니다."));
    }

    @Override
    @PostMapping("/quests/{questId}/verify")
    public ResponseEntity<ApiResponse<QuestVerifyResponse>> verifyQuest(
            Authentication authentication,
            @PathVariable Long questId
    ) {
        QuestVerifyResponse response = questVerificationService.verifyQuest(
                questId,
                authenticatedUserId(authentication)
        );
        return ResponseEntity.ok(ApiResponse.success(response, "퀘스트 검증에 성공했습니다."));
    }

    @Override
    @PostMapping("/quests/{questId}/reward")
    public ResponseEntity<ApiResponse<RewardClaimResponse>> claimQuestReward(
            Authentication authentication,
            @PathVariable Long questId
    ) {
        RewardClaimResponse response = questRewardService.claimQuestReward(
                questId,
                authenticatedUserId(authentication)
        );
        return ResponseEntity.ok(ApiResponse.success(response, "퀘스트 보상을 수령했습니다."));
    }

    private Long authenticatedUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}
