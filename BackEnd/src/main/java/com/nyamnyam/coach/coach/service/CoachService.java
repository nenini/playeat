package com.nyamnyam.coach.coach.service;

import com.nyamnyam.coach.ai.entity.AiFeedback;
import com.nyamnyam.coach.ai.service.AiFeedbackService;
import com.nyamnyam.coach.ai.service.AiTextGenerator;
import com.nyamnyam.coach.ai.service.parser.AiJsonResponseParser;
import com.nyamnyam.coach.ai.service.prompt.CoachFeedbackPrompt;
import com.nyamnyam.coach.coach.dto.response.CoachFeedbackResponse;
import com.nyamnyam.coach.coach.dto.response.CoachListResponse;
import com.nyamnyam.coach.coach.dto.response.CoachResponse;
import com.nyamnyam.coach.coach.entity.Coach;
import com.nyamnyam.coach.coach.repository.CoachRepository;
import com.nyamnyam.coach.diet.entity.Diet;
import com.nyamnyam.coach.diet.repository.DietRepository;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.CoachErrorCode;
import com.nyamnyam.coach.global.exception.errorcode.DietErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CoachService {

    private final CoachRepository coachRepository;
    private final DietRepository dietRepository;
    private final AiFeedbackService aiFeedbackService;
    private final AiTextGenerator aiTextGenerator;
    private final AiJsonResponseParser aiJsonResponseParser;

    public CoachService(
            CoachRepository coachRepository,
            DietRepository dietRepository,
            AiFeedbackService aiFeedbackService,
            AiTextGenerator aiTextGenerator,
            AiJsonResponseParser aiJsonResponseParser
    ) {
        this.coachRepository = coachRepository;
        this.dietRepository = dietRepository;
        this.aiFeedbackService = aiFeedbackService;
        this.aiTextGenerator = aiTextGenerator;
        this.aiJsonResponseParser = aiJsonResponseParser;
    }

    @Transactional(readOnly = true)
    public CoachListResponse getCoaches(Long userId) {
        Long selectedCoachId = coachRepository.findSelectedByUserId(userId)
                .map(Coach::getCoachId)
                .orElse(null);

        return new CoachListResponse(
                coachRepository.findActiveCoaches().stream()
                        .map(coach -> CoachResponse.from(coach, coach.getCoachId().equals(selectedCoachId)))
                        .toList()
        );
    }

    @Transactional
    public CoachResponse selectCoach(Long userId, Long coachId) {
        Coach coach = coachRepository.findById(coachId)
                .filter(item -> Boolean.TRUE.equals(item.getActive()))
                .orElseThrow(() -> new BusinessException(CoachErrorCode.COACH_NOT_FOUND));
        coachRepository.updateSelectedCoach(userId, coachId);
        return CoachResponse.from(coach, true);
    }

    @Transactional
    public CoachFeedbackResponse createDietFeedback(Long userId, Long dietId) {
        Diet diet = dietRepository.findByIdAndUserId(dietId, userId)
                .orElseThrow(() -> new BusinessException(DietErrorCode.DIET_NOT_FOUND));
        Coach coach = resolveCoach(userId);

        String rawFeedback = aiTextGenerator.generateCoachFeedback(new CoachFeedbackPrompt(
                coach.getName(),
                coach.getToneDescription(),
                diet.getTotalCalories(),
                diet.getTotalProteinG(),
                diet.getTotalCarbsG(),
                diet.getTotalFatG()
        ));
        String message = aiJsonResponseParser.parseCoachFeedback(rawFeedback).message();
        AiFeedback feedback = aiFeedbackService.save(userId, dietId, coach.getCoachId(), message, aiTextGenerator.modelName());
        return toFeedbackResponse(feedback, coach);
    }

    @Transactional(readOnly = true)
    public CoachFeedbackResponse getDietFeedback(Long userId, Long dietId) {
        dietRepository.findByIdAndUserId(dietId, userId)
                .orElseThrow(() -> new BusinessException(DietErrorCode.DIET_NOT_FOUND));
        AiFeedback feedback = aiFeedbackService.getLatest(userId, dietId);
        Coach coach = feedback.getCoachId() == null
                ? resolveCoach(userId)
                : coachRepository.findById(feedback.getCoachId()).orElse(resolveCoach(userId));
        return toFeedbackResponse(feedback, coach);
    }

    private Coach resolveCoach(Long userId) {
        return coachRepository.findSelectedByUserId(userId)
                .or(() -> coachRepository.findDefaultCoach())
                .orElseThrow(() -> new BusinessException(CoachErrorCode.COACH_NOT_FOUND));
    }

    private CoachFeedbackResponse toFeedbackResponse(AiFeedback feedback, Coach coach) {
        return new CoachFeedbackResponse(
                feedback.getFeedbackId(),
                feedback.getDietId(),
                feedback.getCoachId(),
                coach.getName(),
                feedback.getMessage(),
                feedback.getModelName(),
                feedback.getCreatedAt()
        );
    }
}
