package com.nyamnyam.coach.guild.controller;

import com.nyamnyam.coach.global.response.ApiResponse;
import com.nyamnyam.coach.guild.dto.request.GuildCreateRequest;
import com.nyamnyam.coach.guild.dto.response.GuildCreateResponse;
import com.nyamnyam.coach.guild.dto.response.GuildDetailResponse;
import com.nyamnyam.coach.guild.dto.response.GuildListResponse;
import com.nyamnyam.coach.guild.dto.response.GuildMemberListResponse;
import com.nyamnyam.coach.guild.dto.response.MyGuildListResponse;
import com.nyamnyam.coach.guild.dto.response.MyGuildStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

@Tag(name = "Guild", description = "길드 기본 API")
@SecurityRequirement(name = "BearerAuth")
public interface GuildApiDocs {

    @Operation(summary = "길드 탐색 목록 조회", description = "ACTIVE 상태의 PRIVATE 길드 목록을 조회합니다. 내 참여 상태도 함께 반환합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "길드 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = GuildListResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": true,
                                      "data": {
                                        "guilds": [
                                          {
                                            "guildId": 1,
                                            "name": "잘먹잘싸",
                                            "description": "건강하게 먹고 보스 잡는 길드",
                                            "inviteCode": "NYAM-2840",
                                            "memberCount": 6,
                                            "maxMembers": 30,
                                            "guildPoint": 2840,
                                            "ownerNickname": "예린",
                                            "myJoinStatus": "PENDING",
                                            "joinRequestId": 12,
                                            "alreadyJoinedAnyGuild": false
                                          }
                                        ],
                                        "page": 0,
                                        "size": 10,
                                        "hasNext": false
                                      },
                                      "message": "길드 목록 조회에 성공했습니다."
                                    }
                                    """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    ResponseEntity<ApiResponse<GuildListResponse>> getGuilds(
            @Parameter(hidden = true) Authentication authentication,
            Integer page,
            Integer size,
            String keyword,
            String sort
    );

    @Operation(summary = "길드 생성", description = "길드를 생성하고 생성자를 OWNER 길드원으로 자동 등록합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "길드 생성 성공",
                    content = @Content(schema = @Schema(implementation = GuildCreateResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 검증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "길드명 중복 또는 이미 길드/참여 요청이 있음")
    })
    ResponseEntity<ApiResponse<GuildCreateResponse>> createGuild(
            @Parameter(hidden = true) Authentication authentication,
            GuildCreateRequest request
    );

    @Operation(summary = "내 길드 목록 조회", description = "현재 로그인한 사용자가 가입한 ACTIVE 길드를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "내 길드 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = MyGuildListResponse.class)))
    })
    ResponseEntity<ApiResponse<MyGuildListResponse>> getMyGuilds(
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "내 길드 상태 조회", description = "NONE, PENDING, JOINED 중 현재 길드 상태를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "내 길드 상태 조회 성공",
                    content = @Content(schema = @Schema(implementation = MyGuildStatusResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": true,
                                      "data": {
                                        "status": "PENDING",
                                        "guild": {
                                          "guildId": 1,
                                          "name": "잘먹잘싸",
                                          "inviteCode": "NYAM-2840"
                                        },
                                        "joinRequest": {
                                          "requestId": 12,
                                          "status": "PENDING",
                                          "createdAt": "2026-06-09T10:30:00"
                                        }
                                      },
                                      "message": "내 길드 상태 조회에 성공했습니다."
                                    }
                                    """)))
    })
    ResponseEntity<ApiResponse<MyGuildStatusResponse>> getMyGuildStatus(
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "길드 상세 조회", description = "길드 멤버만 길드 상세 정보를 조회할 수 있습니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "길드 상세 조회 성공",
                    content = @Content(schema = @Schema(implementation = GuildDetailResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "길드 접근 권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "길드를 찾을 수 없음")
    })
    ResponseEntity<ApiResponse<GuildDetailResponse>> getGuildDetail(
            @Parameter(hidden = true) Authentication authentication,
            Long guildId
    );

    @Operation(summary = "길드원 목록 조회", description = "길드 멤버만 ACTIVE 길드원 목록을 조회할 수 있습니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "길드원 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = GuildMemberListResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "길드 접근 권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "길드를 찾을 수 없음")
    })
    ResponseEntity<ApiResponse<GuildMemberListResponse>> getGuildMembers(
            @Parameter(hidden = true) Authentication authentication,
            Long guildId
    );
}
