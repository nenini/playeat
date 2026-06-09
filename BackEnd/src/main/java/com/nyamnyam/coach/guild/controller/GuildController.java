package com.nyamnyam.coach.guild.controller;

import com.nyamnyam.coach.global.response.ApiResponse;
import com.nyamnyam.coach.guild.dto.request.GuildCreateRequest;
import com.nyamnyam.coach.guild.dto.response.GuildCreateResponse;
import com.nyamnyam.coach.guild.dto.response.GuildDetailResponse;
import com.nyamnyam.coach.guild.dto.response.GuildListResponse;
import com.nyamnyam.coach.guild.dto.response.GuildMemberListResponse;
import com.nyamnyam.coach.guild.dto.response.MyGuildListResponse;
import com.nyamnyam.coach.guild.dto.response.MyGuildStatusResponse;
import com.nyamnyam.coach.guild.service.GuildService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@RequestMapping("/v1/guilds")
public class GuildController implements GuildApiDocs {

    private final GuildService guildService;

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<GuildListResponse>> getGuilds(
            Authentication authentication,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "guildPoint") String sort
    ) {
        GuildListResponse response = guildService.getGuilds(
                authenticatedUserId(authentication),
                page,
                size,
                keyword,
                sort
        );
        return ResponseEntity.ok(ApiResponse.success(response, "길드 목록 조회에 성공했습니다."));
    }

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<GuildCreateResponse>> createGuild(
            Authentication authentication,
            @Valid @RequestBody GuildCreateRequest request
    ) {
        GuildCreateResponse response = guildService.createGuild(authenticatedUserId(authentication), request);
        return ResponseEntity.ok(ApiResponse.success(response, "길드가 생성되었습니다."));
    }

    @Override
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MyGuildListResponse>> getMyGuilds(Authentication authentication) {
        MyGuildListResponse response = guildService.getMyGuilds(authenticatedUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success(response, "내 길드 목록 조회에 성공했습니다."));
    }

    @Override
    @GetMapping("/me/status")
    public ResponseEntity<ApiResponse<MyGuildStatusResponse>> getMyGuildStatus(Authentication authentication) {
        MyGuildStatusResponse response = guildService.getMyGuildStatus(authenticatedUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success(response, "내 길드 상태 조회에 성공했습니다."));
    }

    @Override
    @GetMapping("/{guildId}")
    public ResponseEntity<ApiResponse<GuildDetailResponse>> getGuildDetail(
            Authentication authentication,
            @PathVariable Long guildId
    ) {
        GuildDetailResponse response = guildService.getGuildDetail(guildId, authenticatedUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success(response, "길드 상세 조회에 성공했습니다."));
    }

    @Override
    @GetMapping("/{guildId}/members")
    public ResponseEntity<ApiResponse<GuildMemberListResponse>> getGuildMembers(
            Authentication authentication,
            @PathVariable Long guildId
    ) {
        GuildMemberListResponse response = guildService.getGuildMembers(guildId, authenticatedUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success(response, "길드원 목록 조회에 성공했습니다."));
    }

    private Long authenticatedUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}
