package com.nyamnyam.coach.guild.repository;

import com.nyamnyam.coach.guild.entity.GuildChat;
import com.nyamnyam.coach.guild.repository.row.GuildChatRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface GuildChatRepository {

    void insertGuildChat(GuildChat chat);

    List<GuildChatRow> findChatsByGuildId(
            @Param("guildId") Long guildId,
            @Param("limit") int limit,
            @Param("offset") int offset,
            @Param("userId") Long userId
    );

    int countChatsByGuildId(@Param("guildId") Long guildId);

    Optional<GuildChatRow> findChatById(
            @Param("chatId") Long chatId,
            @Param("userId") Long userId
    );
}
