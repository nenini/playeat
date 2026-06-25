package com.nyamnyam.coach.ai.service.parser;

public record AiQuestContent(
        Long selectedTemplateId,
        String selectionReason,
        String customTitle,
        String customDescription
) {
}
