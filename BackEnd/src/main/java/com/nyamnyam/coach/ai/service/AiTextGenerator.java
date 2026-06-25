package com.nyamnyam.coach.ai.service;

import com.nyamnyam.coach.ai.service.prompt.AiQuestPrompt;
import com.nyamnyam.coach.ai.service.prompt.CharacterMoodPrompt;
import com.nyamnyam.coach.ai.service.prompt.CoachFeedbackPrompt;
import com.nyamnyam.coach.ai.service.prompt.DailyReportPrompt;
import com.nyamnyam.coach.ai.service.prompt.WeeklyReportPrompt;

public interface AiTextGenerator {

    String generateCoachFeedback(CoachFeedbackPrompt prompt);

    String generateDailyReport(DailyReportPrompt prompt);

    String generateWeeklyReport(WeeklyReportPrompt prompt);

    String generateDailyQuest(AiQuestPrompt prompt);

    String selectCharacterMood(CharacterMoodPrompt prompt);

    String modelName();
}
