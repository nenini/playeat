package com.nyamnyam.coach.character.service;

import com.nyamnyam.coach.ai.service.AiTextGenerator;
import com.nyamnyam.coach.ai.service.parser.AiJsonResponseParser;
import com.nyamnyam.coach.ai.service.parser.CharacterMoodContent;
import com.nyamnyam.coach.ai.service.prompt.CharacterMoodPrompt;
import com.nyamnyam.coach.character.entity.CharacterMood;
import com.nyamnyam.coach.character.repository.CharacterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class CharacterMoodService {

    private final AiTextGenerator aiTextGenerator;
    private final AiJsonResponseParser aiJsonResponseParser;
    private final CharacterRepository characterRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CharacterMood updateMoodFromDailyReport(
            Long userId,
            LocalDate date,
            Integer healthScore,
            String summary,
            List<String> strengths,
            List<String> warnings,
            String nextAction
    ) {
        CharacterMood mood = selectMood(userId, date, healthScore, summary, strengths, warnings, nextAction);
        characterRepository.updateMood(userId, mood.name(), randomMessage(mood));
        return mood;
    }

    private CharacterMood selectMood(
            Long userId,
            LocalDate date,
            Integer healthScore,
            String summary,
            List<String> strengths,
            List<String> warnings,
            String nextAction
    ) {
        try {
            CharacterMoodPrompt prompt = new CharacterMoodPrompt(
                    userId,
                    date,
                    healthScore,
                    summary,
                    emptyIfNull(strengths),
                    emptyIfNull(warnings),
                    nextAction
            );
            CharacterMoodContent content = aiJsonResponseParser.parseCharacterMood(
                    aiTextGenerator.selectCharacterMood(prompt)
            );
            return normalizeMood(content.mood());
        } catch (Exception exception) {
            log.warn("Failed to select character mood. fallback=NORMAL userId={} date={}", userId, date, exception);
            return CharacterMood.NORMAL;
        }
    }

    private CharacterMood normalizeMood(String mood) {
        if (mood == null || mood.isBlank()) {
            return CharacterMood.NORMAL;
        }
        try {
            return CharacterMood.valueOf(mood.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return CharacterMood.NORMAL;
        }
    }

    private List<String> emptyIfNull(List<String> values) {
        return values == null ? List.of() : values;
    }

    private String randomMessage(CharacterMood mood) {
        List<String> messages = messagesFor(mood);
        return messages.get(ThreadLocalRandom.current().nextInt(messages.size()));
    }

    private List<String> messagesFor(CharacterMood mood) {
        return switch (mood) {
            case HUNGRY -> List.of(
                    "배고파요, 밥 주세요ㅜㅜ",
                    "꼬르륵… 냠냠이 배고파요.",
                    "오늘은 조금 더 먹고 싶어요.",
                    "든든한 한 끼가 필요해요!",
                    "냠냠이가 힘이 빠졌어요.",
                    "밥 냄새가 그리워요…",
                    "조금만 더 챙겨주세요!",
                    "냠냠, 에너지가 부족해요.",
                    "배가 텅 빈 느낌이에요.",
                    "다음 끼니는 든든하게요!"
            );
            case CHUBBY -> List.of(
                    "배가 빵빵해졌어요!",
                    "오늘은 조금 많이 먹었나 봐요.",
                    "냠냠이가 동글동글해졌어요.",
                    "다음 끼니는 가볍게 가요!",
                    "배부르지만 행복했어요…",
                    "냠냠, 잠깐 쉬어갈까요?",
                    "오늘은 산책이 필요해요!",
                    "조금만 덜 냠냠해볼까요?",
                    "배가 톡 튀어나왔어요.",
                    "내일은 더 가뿐하게요!"
            );
            case MUSCLE -> List.of(
                    "냠냠이가 튼튼해졌어요!",
                    "단백질 힘이 차올라요!",
                    "오늘은 힘이 불끈해요!",
                    "냠냠 근육 충전 완료!",
                    "튼튼한 한 끼였어요!",
                    "팔에 힘이 들어가요!",
                    "냠냠이가 강해지는 중이에요.",
                    "오늘 식단, 아주 든든해요!",
                    "힘찬 냠냠이 출동!",
                    "건강 에너지가 반짝여요!"
            );
            case NORMAL -> List.of(
                    "오늘도 냠냠 기록해요!",
                    "무난한 하루예요, 좋아요!",
                    "냠냠이는 잘 따라가고 있어요.",
                    "이 리듬 그대로 가봐요!",
                    "오늘 식단도 차근차근이에요.",
                    "냠냠, 괜찮은 흐름이에요!",
                    "지금처럼만 해도 좋아요.",
                    "오늘은 평온한 냠냠이에요.",
                    "기록하는 모습 멋져요!",
                    "다음 끼니도 같이 골라요!"
            );
        };
    }
}
