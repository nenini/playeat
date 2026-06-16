package com.nyamnyam.coach.ai.service.prompt;

import java.util.List;

public record AiQuestPrompt(
        String battleDifficulty,
        int activeMemberCount,
        int memberIndex,
        String memberNickname,
        List<QuestTemplatePrompt> availableQuestTemplates
) {
}
