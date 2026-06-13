package com.nyamnyam.coach.guild.controller;

import com.nyamnyam.coach.global.response.ApiResponse;
import com.nyamnyam.coach.guild.dto.request.GuildChatCreateRequest;
import com.nyamnyam.coach.guild.dto.response.GuildChatListResponse;
import com.nyamnyam.coach.guild.dto.response.GuildChatResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

@Tag(name = "Guild Chat", description = "Guild chat REST APIs")
public interface GuildChatApiDocs {

    @Operation(summary = "길드 채팅 메시지 목록 조회", description = "길드 멤버가 REST 방식으로 길드 채팅 메시지를 조회합니다.")
    ResponseEntity<ApiResponse<GuildChatListResponse>> getGuildChats(
            @Parameter(hidden = true) Authentication authentication,
            Long guildId,
            Integer page,
            Integer size
    );

    @Operation(summary = "길드 채팅 메시지 전송", description = "길드 멤버가 USER 타입 채팅 메시지를 전송합니다.")
    ResponseEntity<ApiResponse<GuildChatResponse>> createGuildChat(
            @Parameter(hidden = true) Authentication authentication,
            Long guildId,
            GuildChatCreateRequest request
    );
}
