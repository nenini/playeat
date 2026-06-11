package com.nyamnyam.coach.character.controller;

import com.nyamnyam.coach.character.dto.request.UpdateCharacterNameRequest;
import com.nyamnyam.coach.character.dto.response.CharacterResponse;
import com.nyamnyam.coach.character.dto.response.UpdateCharacterNameResponse;
import com.nyamnyam.coach.character.dto.response.XpHistoryListResponse;
import com.nyamnyam.coach.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

@Tag(name = "Character", description = "캐릭터 API")
@SecurityRequirement(name = "BearerAuth")
public interface CharacterApiDocs {

    @Operation(summary = "내 캐릭터 조회", description = "현재 로그인한 사용자의 캐릭터 기본 정보를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "캐릭터 조회 성공",
                    content = @Content(schema = @Schema(implementation = CharacterResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "캐릭터를 찾을 수 없음")
    })
    ResponseEntity<ApiResponse<CharacterResponse>> getMyCharacter(
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "캐릭터 이름 수정", description = "현재 로그인한 사용자의 캐릭터 이름을 수정합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "캐릭터 이름 수정 성공",
                    content = @Content(schema = @Schema(implementation = UpdateCharacterNameResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 검증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "캐릭터를 찾을 수 없음")
    })
    ResponseEntity<ApiResponse<UpdateCharacterNameResponse>> updateName(
            @Parameter(hidden = true) Authentication authentication,
            UpdateCharacterNameRequest request
    );

    @Operation(summary = "캐릭터 경험치 이력 조회", description = "현재 로그인한 사용자의 캐릭터 경험치 이력을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "경험치 이력 조회 성공",
                    content = @Content(schema = @Schema(implementation = XpHistoryListResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 query parameter"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "캐릭터를 찾을 수 없음")
    })
    ResponseEntity<ApiResponse<XpHistoryListResponse>> getXpHistory(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "Page number", example = "0") Integer page,
            @Parameter(description = "Page size", example = "20") Integer size,
            @Parameter(description = "XP source type: DIET, BOSS, ADMIN", example = "DIET") String sourceType
    );
}
