package com.nyamnyam.coach.coach.controller;

import com.nyamnyam.coach.coach.dto.request.CoachSelectRequest;
import com.nyamnyam.coach.coach.dto.response.CoachFeedbackResponse;
import com.nyamnyam.coach.coach.dto.response.CoachListResponse;
import com.nyamnyam.coach.coach.dto.response.CoachResponse;
import com.nyamnyam.coach.coach.service.CoachService;
import com.nyamnyam.coach.global.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/coaches")
public class CoachController implements CoachApiDocs {

    private final CoachService coachService;

    public CoachController(CoachService coachService) {
        this.coachService = coachService;
    }

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<CoachListResponse>> getCoaches(Authentication authentication) {
        CoachListResponse response = coachService.getCoaches(authenticatedUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success(response, "코치 목록 조회에 성공했습니다."));
    }

    @Override
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<CoachResponse>> selectCoach(
            Authentication authentication,
            @Valid @RequestBody CoachSelectRequest request
    ) {
        CoachResponse response = coachService.selectCoach(authenticatedUserId(authentication), request.coachId());
        return ResponseEntity.ok(ApiResponse.success(response, "내 코치 선택에 성공했습니다."));
    }

    @Override
    @PostMapping("/me/diets/{dietId}/feedback")
    public ResponseEntity<ApiResponse<CoachFeedbackResponse>> createDietFeedback(
            Authentication authentication,
            @PathVariable Long dietId
    ) {
        CoachFeedbackResponse response = coachService.createDietFeedback(authenticatedUserId(authentication), dietId);
        return ResponseEntity.ok(ApiResponse.success(response, "끼니 피드백 생성에 성공했습니다."));
    }

    @Override
    @GetMapping("/me/diets/{dietId}/feedback")
    public ResponseEntity<ApiResponse<CoachFeedbackResponse>> getDietFeedback(
            Authentication authentication,
            @PathVariable Long dietId
    ) {
        CoachFeedbackResponse response = coachService.getDietFeedback(authenticatedUserId(authentication), dietId);
        return ResponseEntity.ok(ApiResponse.success(response, "끼니 피드백 조회에 성공했습니다."));
    }

    private Long authenticatedUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}
