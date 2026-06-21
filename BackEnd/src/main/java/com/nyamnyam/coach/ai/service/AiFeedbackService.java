package com.nyamnyam.coach.ai.service;

import com.nyamnyam.coach.ai.entity.AiFeedback;
import com.nyamnyam.coach.ai.repository.AiFeedbackRepository;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.AiErrorCode;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AiFeedbackService {

    private final AiFeedbackRepository aiFeedbackRepository;

    public AiFeedbackService(AiFeedbackRepository aiFeedbackRepository) {
        this.aiFeedbackRepository = aiFeedbackRepository;
    }

    public AiFeedback save(Long userId, Long dietId, Long coachId, String message, String modelName) {
        AiFeedback feedback = new AiFeedback();
        feedback.setUserId(userId);
        feedback.setDietId(dietId);
        feedback.setCoachId(coachId);
        feedback.setMessage(message);
        feedback.setModelName(modelName);
        aiFeedbackRepository.insert(feedback);
        return feedback;
    }

    public AiFeedback getLatest(Long userId, Long dietId) {
        return findLatest(userId, dietId)
                .orElseThrow(() -> new BusinessException(AiErrorCode.AI_FEEDBACK_NOT_FOUND));
    }

    public AiFeedback getLatest(Long userId, Long dietId, Long coachId) {
        return findLatest(userId, dietId, coachId)
                .orElseThrow(() -> new BusinessException(AiErrorCode.AI_FEEDBACK_NOT_FOUND));
    }

    public Optional<AiFeedback> findLatest(Long userId, Long dietId) {
        return aiFeedbackRepository.findLatestByUserIdAndDietId(userId, dietId);
    }

    public Optional<AiFeedback> findLatest(Long userId, Long dietId, Long coachId) {
        return aiFeedbackRepository.findLatestByUserIdAndDietIdAndCoachId(userId, dietId, coachId);
    }
}
