package com.nyamnyam.coach.character.repository;

import com.nyamnyam.coach.character.entity.XpHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface XpHistoryRepository {

    void save(XpHistory xpHistory);

    List<XpHistory> findByUserId(
            @Param("userId") Long userId,
            @Param("sourceType") String sourceType,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    long countByUserId(
            @Param("userId") Long userId,
            @Param("sourceType") String sourceType
    );
}
