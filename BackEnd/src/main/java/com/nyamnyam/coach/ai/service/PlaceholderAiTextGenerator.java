package com.nyamnyam.coach.ai.service;

import com.nyamnyam.coach.ai.service.prompt.CoachFeedbackPrompt;
import com.nyamnyam.coach.ai.service.prompt.DailyReportPrompt;
import com.nyamnyam.coach.ai.service.prompt.WeeklyReportPrompt;
import org.springframework.stereotype.Service;

@Service
public class PlaceholderAiTextGenerator implements AiTextGenerator {

    @Override
    public String generateCoachFeedback(CoachFeedbackPrompt prompt) {
        return prompt.coachName() + " 코치가 봤을 때 이번 끼니는 기록이 잘 되었어요. 다음 끼니에서는 부족한 영양소를 한 가지 보완해보세요.";
    }

    @Override
    public String generateDailyReport(DailyReportPrompt prompt) {
        return prompt.date() + " 식단은 기록 기반으로 분석되었습니다. 오늘 부족한 영양소를 다음 끼니에서 보완해보세요.";
    }

    @Override
    public String generateWeeklyReport(WeeklyReportPrompt prompt) {
        return prompt.startDate() + "부터 " + prompt.endDate() + "까지의 식단 흐름을 기준으로 주간 리포트를 만들었습니다.";
    }

    @Override
    public String modelName() {
        return "placeholder";
    }
}
