package com.nyamnyam.coach.guild.service;

import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.GuildErrorCode;
import com.nyamnyam.coach.guild.dto.request.GuildChatCreateRequest;
import com.nyamnyam.coach.guild.dto.response.GuildChatListResponse;
import com.nyamnyam.coach.guild.dto.response.GuildChatResponse;
import com.nyamnyam.coach.guild.entity.GuildChat;
import com.nyamnyam.coach.guild.entity.GuildChatMessageType;
import com.nyamnyam.coach.guild.repository.GuildChatRepository;
import com.nyamnyam.coach.guild.repository.row.GuildChatRow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuildChatService {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 30;
    private static final int MAX_SIZE = 100;
    private static final int MAX_MESSAGE_LENGTH = 1000;

    private final GuildChatRepository guildChatRepository;
    private final GuildValidator guildValidator;

    @Transactional(readOnly = true)
    public GuildChatListResponse getGuildChats(Long guildId, Long userId, Integer page, Integer size) {
        guildValidator.validateGuildMember(guildId, userId);

        int normalizedPage = normalizePage(page);
        int normalizedSize = normalizeSize(size);
        int offset = normalizedPage * normalizedSize;

        List<GuildChatResponse> chats = guildChatRepository.findChatsByGuildId(
                        guildId,
                        normalizedSize,
                        offset,
                        userId
                )
                .stream()
                .map(this::toResponse)
                .toList();
        int totalCount = guildChatRepository.countChatsByGuildId(guildId);
        boolean hasNext = offset + normalizedSize < totalCount;

        return new GuildChatListResponse(guildId, chats, normalizedPage, normalizedSize, hasNext);
    }

    @Transactional
    public GuildChatResponse createGuildChat(Long guildId, Long userId, GuildChatCreateRequest request) {
        guildValidator.validateGuildMember(guildId, userId);
        validateMessage(request.message());

        GuildChat chat = GuildChat.builder()
                .guildId(guildId)
                .senderUserId(userId)
                .messageType(GuildChatMessageType.USER.name())
                .content(request.message().trim())
                .build();

        guildChatRepository.insertGuildChat(chat);
        return guildChatRepository.findChatById(chat.getChatId(), userId)
                .map(this::toResponse)
                .orElseThrow(() -> new BusinessException(GuildErrorCode.GUILD_CHAT_NOT_FOUND));
    }

    @Transactional
    public GuildChatResponse createSystemMessage(Long guildId, String content) {
        guildValidator.validateGuildActive(guildId);
        validateMessage(content);

        GuildChat chat = GuildChat.builder()
                .guildId(guildId)
                .senderUserId(null)
                .messageType(GuildChatMessageType.SYSTEM.name())
                .content(content.trim())
                .build();

        guildChatRepository.insertGuildChat(chat);
        return guildChatRepository.findChatById(chat.getChatId(), null)
                .map(this::toResponse)
                .orElseThrow(() -> new BusinessException(GuildErrorCode.GUILD_CHAT_NOT_FOUND));
    }

    private GuildChatResponse toResponse(GuildChatRow row) {
        return new GuildChatResponse(
                row.getChatId(),
                row.getGuildId(),
                row.getUserId(),
                row.getNickname(),
                row.getProfileImageUrl(),
                row.getCharacterId(),
                row.getCharacterName(),
                row.getCharacterLevel(),
                row.getMessageType(),
                row.getMessage(),
                row.getCreatedAt(),
                Boolean.TRUE.equals(row.getIsMe())
        );
    }

    private void validateMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new BusinessException(GuildErrorCode.GUILD_CHAT_MESSAGE_EMPTY);
        }
        if (message.length() > MAX_MESSAGE_LENGTH) {
            throw new BusinessException(GuildErrorCode.GUILD_CHAT_MESSAGE_TOO_LONG);
        }
    }

    private int normalizePage(Integer page) {
        if (page == null || page < 0) {
            return DEFAULT_PAGE;
        }
        return page;
    }

    private int normalizeSize(Integer size) {
        if (size == null || size <= 0) {
            return DEFAULT_SIZE;
        }
        return Math.min(size, MAX_SIZE);
    }
}
