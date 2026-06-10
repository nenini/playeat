package com.nyamnyam.coach.guild.controller;

import com.nyamnyam.coach.global.response.ApiResponse;
import com.nyamnyam.coach.guild.dto.request.GuildCreateRequest;
import com.nyamnyam.coach.guild.dto.request.GuildNoticeCreateRequest;
import com.nyamnyam.coach.guild.dto.request.GuildNoticeUpdateRequest;
import com.nyamnyam.coach.guild.dto.request.GuildUpdateRequest;
import com.nyamnyam.coach.guild.dto.request.JoinRequestCreateByCodeRequest;
import com.nyamnyam.coach.guild.dto.request.JoinRequestCreateRequest;
import com.nyamnyam.coach.guild.dto.response.GuildJoinRequestListResponse;
import com.nyamnyam.coach.guild.dto.response.GuildCreateResponse;
import com.nyamnyam.coach.guild.dto.response.GuildDeleteResponse;
import com.nyamnyam.coach.guild.dto.response.GuildDetailResponse;
import com.nyamnyam.coach.guild.dto.response.GuildKickResponse;
import com.nyamnyam.coach.guild.dto.response.GuildLeaveResponse;
import com.nyamnyam.coach.guild.dto.response.GuildListResponse;
import com.nyamnyam.coach.guild.dto.response.GuildMemberDetailResponse;
import com.nyamnyam.coach.guild.dto.response.GuildMemberListResponse;
import com.nyamnyam.coach.guild.dto.response.GuildNoticeCreateResponse;
import com.nyamnyam.coach.guild.dto.response.GuildNoticeDeleteResponse;
import com.nyamnyam.coach.guild.dto.response.GuildNoticeListResponse;
import com.nyamnyam.coach.guild.dto.response.GuildNoticeResponse;
import com.nyamnyam.coach.guild.dto.response.GuildNoticeUpdateResponse;
import com.nyamnyam.coach.guild.dto.response.GuildUpdateResponse;
import com.nyamnyam.coach.guild.dto.response.JoinRequestApproveResponse;
import com.nyamnyam.coach.guild.dto.response.JoinRequestCancelResponse;
import com.nyamnyam.coach.guild.dto.response.JoinRequestCreateResponse;
import com.nyamnyam.coach.guild.dto.response.JoinRequestRejectResponse;
import com.nyamnyam.coach.guild.dto.response.MyJoinRequestListResponse;
import com.nyamnyam.coach.guild.dto.response.MyGuildListResponse;
import com.nyamnyam.coach.guild.dto.response.MyGuildStatusResponse;
import com.nyamnyam.coach.guild.service.GuildService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/guilds")
public class GuildController implements GuildApiDocs {

    private final GuildService guildService;

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<GuildListResponse>> getGuilds(
            Authentication authentication,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "guildPoint") String sort
    ) {
        GuildListResponse response = guildService.getGuilds(
                authenticatedUserId(authentication),
                page,
                size,
                keyword,
                sort
        );
        return ResponseEntity.ok(ApiResponse.success(response, "길드 목록 조회에 성공했습니다."));
    }

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<GuildCreateResponse>> createGuild(
            Authentication authentication,
            @Valid @RequestBody GuildCreateRequest request
    ) {
        GuildCreateResponse response = guildService.createGuild(authenticatedUserId(authentication), request);
        return ResponseEntity.ok(ApiResponse.success(response, "길드가 생성되었습니다."));
    }

    @Override
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MyGuildListResponse>> getMyGuilds(Authentication authentication) {
        MyGuildListResponse response = guildService.getMyGuilds(authenticatedUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success(response, "내 길드 목록 조회에 성공했습니다."));
    }

    @Override
    @GetMapping("/me/status")
    public ResponseEntity<ApiResponse<MyGuildStatusResponse>> getMyGuildStatus(Authentication authentication) {
        MyGuildStatusResponse response = guildService.getMyGuildStatus(authenticatedUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success(response, "내 길드 상태 조회에 성공했습니다."));
    }

    @Override
    @PostMapping("/join-requests")
    public ResponseEntity<ApiResponse<JoinRequestCreateResponse>> createJoinRequestByInviteCode(
            Authentication authentication,
            @Valid @RequestBody JoinRequestCreateByCodeRequest request
    ) {
        JoinRequestCreateResponse response = guildService.createJoinRequestByInviteCode(
                authenticatedUserId(authentication),
                request
        );
        return ResponseEntity.ok(ApiResponse.success(response, "길드 참여 요청이 생성되었습니다."));
    }

    @Override
    @GetMapping("/join-requests/me")
    public ResponseEntity<ApiResponse<MyJoinRequestListResponse>> getMyJoinRequests(
            Authentication authentication,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        MyJoinRequestListResponse response = guildService.getMyJoinRequests(
                authenticatedUserId(authentication),
                status,
                page,
                size
        );
        return ResponseEntity.ok(ApiResponse.success(response, "내 길드 참여 요청 목록 조회에 성공했습니다."));
    }

    @Override
    @GetMapping("/{guildId}")
    public ResponseEntity<ApiResponse<GuildDetailResponse>> getGuildDetail(
            Authentication authentication,
            @PathVariable Long guildId
    ) {
        GuildDetailResponse response = guildService.getGuildDetail(guildId, authenticatedUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success(response, "길드 상세 조회에 성공했습니다."));
    }

    @Override
    @GetMapping("/{guildId}/members")
    public ResponseEntity<ApiResponse<GuildMemberListResponse>> getGuildMembers(
            Authentication authentication,
            @PathVariable Long guildId
    ) {
        GuildMemberListResponse response = guildService.getGuildMembers(guildId, authenticatedUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success(response, "길드원 목록 조회에 성공했습니다."));
    }

    @Override
    @PatchMapping("/{guildId}")
    public ResponseEntity<ApiResponse<GuildUpdateResponse>> updateGuild(
            Authentication authentication,
            @PathVariable Long guildId,
            @Valid @RequestBody GuildUpdateRequest request
    ) {
        GuildUpdateResponse response = guildService.updateGuild(guildId, authenticatedUserId(authentication), request);
        return ResponseEntity.ok(ApiResponse.success(response, "길드 정보가 수정되었습니다."));
    }

    @Override
    @DeleteMapping("/{guildId}")
    public ResponseEntity<ApiResponse<GuildDeleteResponse>> deleteGuild(
            Authentication authentication,
            @PathVariable Long guildId
    ) {
        GuildDeleteResponse response = guildService.deleteGuild(guildId, authenticatedUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success(response, "길드가 삭제되었습니다."));
    }

    @Override
    @DeleteMapping("/{guildId}/members/me")
    public ResponseEntity<ApiResponse<GuildLeaveResponse>> leaveGuild(
            Authentication authentication,
            @PathVariable Long guildId
    ) {
        GuildLeaveResponse response = guildService.leaveGuild(guildId, authenticatedUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success(response, "길드에서 탈퇴했습니다."));
    }

    @Override
    @GetMapping("/{guildId}/members/{memberId}")
    public ResponseEntity<ApiResponse<GuildMemberDetailResponse>> getGuildMemberDetail(
            Authentication authentication,
            @PathVariable Long guildId,
            @PathVariable Long memberId
    ) {
        GuildMemberDetailResponse response = guildService.getGuildMemberDetail(
                guildId,
                memberId,
                authenticatedUserId(authentication)
        );
        return ResponseEntity.ok(ApiResponse.success(response, "길드원 상세 조회에 성공했습니다."));
    }

    @Override
    @DeleteMapping("/{guildId}/members/{memberId}")
    public ResponseEntity<ApiResponse<GuildKickResponse>> kickGuildMember(
            Authentication authentication,
            @PathVariable Long guildId,
            @PathVariable Long memberId
    ) {
        GuildKickResponse response = guildService.kickGuildMember(
                guildId,
                memberId,
                authenticatedUserId(authentication)
        );
        return ResponseEntity.ok(ApiResponse.success(response, "길드원이 추방되었습니다."));
    }

    @Override
    @GetMapping("/{guildId}/notices")
    public ResponseEntity<ApiResponse<GuildNoticeListResponse>> getGuildNotices(
            Authentication authentication,
            @PathVariable Long guildId
    ) {
        GuildNoticeListResponse response = guildService.getGuildNotices(guildId, authenticatedUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success(response, "길드 공지사항 목록 조회에 성공했습니다."));
    }

    @Override
    @GetMapping("/{guildId}/notices/{noticeId}")
    public ResponseEntity<ApiResponse<GuildNoticeResponse>> getGuildNotice(
            Authentication authentication,
            @PathVariable Long guildId,
            @PathVariable Long noticeId
    ) {
        GuildNoticeResponse response = guildService.getGuildNotice(
                guildId,
                noticeId,
                authenticatedUserId(authentication)
        );
        return ResponseEntity.ok(ApiResponse.success(response, "길드 공지사항 상세 조회에 성공했습니다."));
    }

    @Override
    @PostMapping("/{guildId}/notices")
    public ResponseEntity<ApiResponse<GuildNoticeCreateResponse>> createGuildNotice(
            Authentication authentication,
            @PathVariable Long guildId,
            @Valid @RequestBody GuildNoticeCreateRequest request
    ) {
        GuildNoticeCreateResponse response = guildService.createGuildNotice(
                guildId,
                authenticatedUserId(authentication),
                request
        );
        return ResponseEntity.ok(ApiResponse.success(response, "길드 공지사항이 등록되었습니다."));
    }

    @Override
    @PatchMapping("/{guildId}/notices/{noticeId}")
    public ResponseEntity<ApiResponse<GuildNoticeUpdateResponse>> updateGuildNotice(
            Authentication authentication,
            @PathVariable Long guildId,
            @PathVariable Long noticeId,
            @Valid @RequestBody GuildNoticeUpdateRequest request
    ) {
        GuildNoticeUpdateResponse response = guildService.updateGuildNotice(
                guildId,
                noticeId,
                authenticatedUserId(authentication),
                request
        );
        return ResponseEntity.ok(ApiResponse.success(response, "길드 공지사항이 수정되었습니다."));
    }

    @Override
    @DeleteMapping("/{guildId}/notices/{noticeId}")
    public ResponseEntity<ApiResponse<GuildNoticeDeleteResponse>> deleteGuildNotice(
            Authentication authentication,
            @PathVariable Long guildId,
            @PathVariable Long noticeId
    ) {
        GuildNoticeDeleteResponse response = guildService.deleteGuildNotice(
                guildId,
                noticeId,
                authenticatedUserId(authentication)
        );
        return ResponseEntity.ok(ApiResponse.success(response, "길드 공지사항이 삭제되었습니다."));
    }

    @Override
    @PostMapping("/{guildId}/join-requests")
    public ResponseEntity<ApiResponse<JoinRequestCreateResponse>> createJoinRequest(
            Authentication authentication,
            @PathVariable Long guildId,
            @Valid @RequestBody JoinRequestCreateRequest request
    ) {
        JoinRequestCreateResponse response = guildService.createJoinRequest(
                guildId,
                authenticatedUserId(authentication),
                request
        );
        return ResponseEntity.ok(ApiResponse.success(response, "길드 참여 요청이 생성되었습니다."));
    }

    @Override
    @GetMapping("/{guildId}/join-requests")
    public ResponseEntity<ApiResponse<GuildJoinRequestListResponse>> getGuildJoinRequests(
            Authentication authentication,
            @PathVariable Long guildId,
            @RequestParam(defaultValue = "PENDING") String status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        GuildJoinRequestListResponse response = guildService.getGuildJoinRequests(
                guildId,
                authenticatedUserId(authentication),
                status,
                page,
                size
        );
        return ResponseEntity.ok(ApiResponse.success(response, "길드 참여 요청 목록 조회에 성공했습니다."));
    }

    @Override
    @PostMapping("/{guildId}/join-requests/{requestId}/approve")
    public ResponseEntity<ApiResponse<JoinRequestApproveResponse>> approveJoinRequest(
            Authentication authentication,
            @PathVariable Long guildId,
            @PathVariable Long requestId
    ) {
        JoinRequestApproveResponse response = guildService.approveJoinRequest(
                guildId,
                requestId,
                authenticatedUserId(authentication)
        );
        return ResponseEntity.ok(ApiResponse.success(response, "길드 참여 요청이 승인되었습니다."));
    }

    @Override
    @PostMapping("/{guildId}/join-requests/{requestId}/reject")
    public ResponseEntity<ApiResponse<JoinRequestRejectResponse>> rejectJoinRequest(
            Authentication authentication,
            @PathVariable Long guildId,
            @PathVariable Long requestId
    ) {
        JoinRequestRejectResponse response = guildService.rejectJoinRequest(
                guildId,
                requestId,
                authenticatedUserId(authentication)
        );
        return ResponseEntity.ok(ApiResponse.success(response, "길드 참여 요청이 거절되었습니다."));
    }

    @Override
    @DeleteMapping("/{guildId}/join-requests/{requestId}")
    public ResponseEntity<ApiResponse<JoinRequestCancelResponse>> cancelJoinRequest(
            Authentication authentication,
            @PathVariable Long guildId,
            @PathVariable Long requestId
    ) {
        JoinRequestCancelResponse response = guildService.cancelJoinRequest(
                guildId,
                requestId,
                authenticatedUserId(authentication)
        );
        return ResponseEntity.ok(ApiResponse.success(response, "길드 참여 요청이 취소되었습니다."));
    }

    private Long authenticatedUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}
