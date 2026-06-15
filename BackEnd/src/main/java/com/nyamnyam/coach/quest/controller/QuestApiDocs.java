package com.nyamnyam.coach.quest.controller;

import com.nyamnyam.coach.quest.dto.response.BossBattleQuestListResponse;
import com.nyamnyam.coach.quest.dto.response.MyQuestResponse;
import com.nyamnyam.coach.quest.dto.response.QuestContributionListResponse;
import com.nyamnyam.coach.quest.dto.response.QuestDetailResponse;
import com.nyamnyam.coach.quest.dto.response.QuestGenerateResponse;
import com.nyamnyam.coach.quest.dto.response.QuestVerifyResponse;
import com.nyamnyam.coach.boss.dto.response.RewardClaimResponse;
import com.nyamnyam.coach.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

@Tag(name = "Quest", description = "Boss battle quest APIs")
public interface QuestApiDocs {

    @Operation(summary = "보스전 전체 퀘스트 조회", description = "현재 보스전에 생성된 길드원별 퀘스트를 조회합니다.")
    ResponseEntity<ApiResponse<BossBattleQuestListResponse>> getBattleQuests(
            @Parameter(hidden = true) Authentication authentication,
            Long battleId
    );

    @Operation(summary = "보스전 내 퀘스트 조회", description = "현재 보스전에서 로그인 사용자의 퀘스트를 조회합니다.")
    ResponseEntity<ApiResponse<MyQuestResponse>> getMyQuest(
            @Parameter(hidden = true) Authentication authentication,
            Long battleId
    );

    @Operation(summary = "퀘스트 상세 조회", description = "퀘스트 상세 정보를 조회합니다.")
    ResponseEntity<ApiResponse<QuestDetailResponse>> getQuestDetail(
            @Parameter(hidden = true) Authentication authentication,
            Long questId
    );

    @Operation(summary = "보스전 퀘스트 생성", description = "길드장이 보스전 참여 길드원별 placeholder 퀘스트를 생성합니다.")
    ResponseEntity<ApiResponse<QuestGenerateResponse>> generateQuests(
            @Parameter(hidden = true) Authentication authentication,
            Long battleId
    );

    @Operation(summary = "보스전 퀘스트 기여도 조회", description = "현재 보스전의 길드원별 퀘스트 기여도를 조회합니다.")
    ResponseEntity<ApiResponse<QuestContributionListResponse>> getQuestContributions(
            @Parameter(hidden = true) Authentication authentication,
            Long battleId
    );

    @Operation(summary = "퀘스트 검증", description = "퀘스트 담당자가 달성 여부를 검증하고 성공 시 보스 HP와 점수를 반영합니다.")
    ResponseEntity<ApiResponse<QuestVerifyResponse>> verifyQuest(
            @Parameter(hidden = true) Authentication authentication,
            Long questId
    );

    @Operation(summary = "퀘스트 보상 수령", description = "완료된 개인 퀘스트의 XP와 코인 보상을 수령합니다.")
    ResponseEntity<ApiResponse<RewardClaimResponse>> claimQuestReward(
            @Parameter(hidden = true) Authentication authentication,
            Long questId
    );
}
