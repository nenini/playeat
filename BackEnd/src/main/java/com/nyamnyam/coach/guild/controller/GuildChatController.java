package com.nyamnyam.coach.guild.controller;

import com.nyamnyam.coach.global.response.ApiResponse;
import com.nyamnyam.coach.guild.dto.request.GuildChatCreateRequest;
import com.nyamnyam.coach.guild.dto.response.GuildChatListResponse;
import com.nyamnyam.coach.guild.dto.response.GuildChatResponse;
import com.nyamnyam.coach.guild.service.GuildChatService;
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
@RequestMapping("/v1/guilds/{guildId}/chats")
public class GuildChatController implements GuildChatApiDocs {

    private final GuildChatService guildChatService;

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<GuildChatListResponse>> getGuildChats(
            Authentication authentication,
            @PathVariable Long guildId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "30") Integer size
    ) {
        GuildChatListResponse response = guildChatService.getGuildChats(
                guildId,
                authenticatedUserId(authentication),
                page,
                size
        );
        return ResponseEntity.ok(ApiResponse.success(response, "길드 채팅 목록 조회에 성공했습니다."));
    }

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<GuildChatResponse>> createGuildChat(
            Authentication authentication,
            @PathVariable Long guildId,
            @Valid @RequestBody GuildChatCreateRequest request
    ) {
        GuildChatResponse response = guildChatService.createGuildChat(
                guildId,
                authenticatedUserId(authentication),
                request
        );
        return ResponseEntity.ok(ApiResponse.success(response, "길드 채팅 메시지가 전송되었습니다."));
    }

    private Long authenticatedUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}
