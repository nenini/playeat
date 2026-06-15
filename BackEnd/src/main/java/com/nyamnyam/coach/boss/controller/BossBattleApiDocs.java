package com.nyamnyam.coach.boss.controller;

import com.nyamnyam.coach.boss.dto.request.BossBattleCreateRequest;
import com.nyamnyam.coach.boss.dto.response.BossBattleCreateResponse;
import com.nyamnyam.coach.boss.dto.response.BossBattleDetailResponse;
import com.nyamnyam.coach.boss.dto.response.BossBattleHistoryResponse;
import com.nyamnyam.coach.boss.dto.response.BossBattleHpResponse;
import com.nyamnyam.coach.boss.dto.response.CommonConditionVerifyResponse;
import com.nyamnyam.coach.boss.dto.response.CurrentBossBattleResponse;
import com.nyamnyam.coach.boss.dto.response.RewardClaimResponse;
import com.nyamnyam.coach.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

@Tag(name = "Boss Battle", description = "Boss battle APIs")
public interface BossBattleApiDocs {

    @Operation(summary = "길드 보스전 생성", description = "길드장이 현재 시즌 보스를 선택해 보스전을 시작합니다.")
    ResponseEntity<ApiResponse<BossBattleCreateResponse>> createBossBattle(
            @Parameter(hidden = true) Authentication authentication,
            Long guildId,
            BossBattleCreateRequest request
    );

    @Operation(summary = "길드 현재 보스전 조회", description = "길드의 진행 중인 보스전을 조회합니다.")
    ResponseEntity<ApiResponse<CurrentBossBattleResponse>> getCurrentBossBattle(
            @Parameter(hidden = true) Authentication authentication,
            Long guildId
    );

    @Operation(summary = "보스전 상세 조회", description = "보스전 정보, 조건, 최근 데미지 로그를 조회합니다.")
    ResponseEntity<ApiResponse<BossBattleDetailResponse>> getBossBattleDetail(
            @Parameter(hidden = true) Authentication authentication,
            Long battleId
    );

    @Operation(summary = "보스전 HP 조회", description = "보스전의 현재 HP 정보를 조회합니다.")
    ResponseEntity<ApiResponse<BossBattleHpResponse>> getBossBattleHp(
            @Parameter(hidden = true) Authentication authentication,
            Long battleId
    );

    @Operation(summary = "길드 보스전 이력 조회", description = "길드의 보스전 이력을 조회합니다.")
    ResponseEntity<ApiResponse<BossBattleHistoryResponse>> getBossBattleHistory(
            @Parameter(hidden = true) Authentication authentication,
            Long guildId,
            Integer page,
            Integer size
    );

    @Operation(summary = "보스전 보상 수령", description = "클리어된 보스전의 XP와 코인 보상을 길드원이 수령합니다.")
    ResponseEntity<ApiResponse<RewardClaimResponse>> claimBossBattleReward(
            @Parameter(hidden = true) Authentication authentication,
            Long battleId
    );

    @Operation(summary = "공통 격파 조건 확인")
    ResponseEntity<ApiResponse<CommonConditionVerifyResponse>> verifyCommonConditions(
            @Parameter(hidden = true) Authentication authentication,
            Long battleId
    );
}
