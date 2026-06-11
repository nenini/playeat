package com.nyamnyam.coach.character.controller;

import com.nyamnyam.coach.character.dto.request.UpdateCharacterNameRequest;
import com.nyamnyam.coach.character.dto.response.CharacterResponse;
import com.nyamnyam.coach.character.dto.response.UpdateCharacterNameResponse;
import com.nyamnyam.coach.character.dto.response.XpHistoryListResponse;
import com.nyamnyam.coach.character.service.CharacterService;
import com.nyamnyam.coach.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/characters")
public class CharacterController implements CharacterApiDocs {

    private final CharacterService characterService;

    @Override
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CharacterResponse>> getMyCharacter(Authentication authentication) {
        CharacterResponse response = characterService.getMyCharacter(authenticatedUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success(response, "캐릭터 조회에 성공했습니다."));
    }

    @Override
    @PatchMapping("/me/name")
    public ResponseEntity<ApiResponse<UpdateCharacterNameResponse>> updateName(
            Authentication authentication,
            @Valid @RequestBody UpdateCharacterNameRequest request
    ) {
        UpdateCharacterNameResponse response = characterService.updateName(authenticatedUserId(authentication), request);
        return ResponseEntity.ok(ApiResponse.success(response, "캐릭터 이름이 수정되었습니다."));
    }

    @Override
    @GetMapping("/me/xp-history")
    public ResponseEntity<ApiResponse<XpHistoryListResponse>> getXpHistory(
            Authentication authentication,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sourceType
    ) {
        XpHistoryListResponse response = characterService.getXpHistory(
                authenticatedUserId(authentication),
                page,
                size,
                sourceType
        );
        return ResponseEntity.ok(ApiResponse.success(response, "경험치 이력 조회에 성공했습니다."));
    }

    private Long authenticatedUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}
