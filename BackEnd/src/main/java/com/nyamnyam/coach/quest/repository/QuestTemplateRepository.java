package com.nyamnyam.coach.quest.repository;

import com.nyamnyam.coach.quest.entity.QuestTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface QuestTemplateRepository {

    List<QuestTemplate> findActiveTemplates();

    List<QuestTemplate> findActiveTemplatesByDifficulty(@Param("difficulty") String difficulty);

    Optional<QuestTemplate> findTemplateById(@Param("templateId") Long templateId);

    Optional<QuestTemplate> findDefaultTemplate();
}
