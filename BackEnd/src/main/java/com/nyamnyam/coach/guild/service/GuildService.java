package com.nyamnyam.coach.guild.service;

import com.nyamnyam.coach.boss.repository.BossBattleParticipantRepository;
import com.nyamnyam.coach.global.exception.BusinessException;
import com.nyamnyam.coach.global.exception.errorcode.AuthErrorCode;
import com.nyamnyam.coach.global.exception.errorcode.GuildErrorCode;
import com.nyamnyam.coach.global.exception.errorcode.UserErrorCode;
import com.nyamnyam.coach.guild.dto.request.GuildCreateRequest;
import com.nyamnyam.coach.guild.dto.request.GuildNoticeCreateRequest;
import com.nyamnyam.coach.guild.dto.request.GuildNoticeUpdateRequest;
import com.nyamnyam.coach.guild.dto.request.GuildUpdateRequest;
import com.nyamnyam.coach.guild.dto.request.JoinRequestCreateByCodeRequest;
import com.nyamnyam.coach.guild.dto.request.JoinRequestCreateRequest;
import com.nyamnyam.coach.guild.dto.response.GuildJoinRequestListResponse;
import com.nyamnyam.coach.guild.dto.response.GuildJoinRequestResponse;
import com.nyamnyam.coach.guild.dto.response.GuildCreateResponse;
import com.nyamnyam.coach.guild.dto.response.GuildDeleteResponse;
import com.nyamnyam.coach.guild.dto.response.GuildDetailResponse;
import com.nyamnyam.coach.guild.dto.response.GuildKickResponse;
import com.nyamnyam.coach.guild.dto.response.GuildLeaveResponse;
import com.nyamnyam.coach.guild.dto.response.GuildListResponse;
import com.nyamnyam.coach.guild.dto.response.GuildMemberDetailResponse;
import com.nyamnyam.coach.guild.dto.response.GuildMemberListResponse;
import com.nyamnyam.coach.guild.dto.response.GuildMemberResponse;
import com.nyamnyam.coach.guild.dto.response.GuildNoticeCreateResponse;
import com.nyamnyam.coach.guild.dto.response.GuildNoticeDeleteResponse;
import com.nyamnyam.coach.guild.dto.response.GuildNoticeListResponse;
import com.nyamnyam.coach.guild.dto.response.GuildNoticeResponse;
import com.nyamnyam.coach.guild.dto.response.GuildNoticeUpdateResponse;
import com.nyamnyam.coach.guild.dto.response.GuildSummaryResponse;
import com.nyamnyam.coach.guild.dto.response.GuildUpdateResponse;
import com.nyamnyam.coach.guild.dto.response.JoinRequestApproveResponse;
import com.nyamnyam.coach.guild.dto.response.JoinRequestCancelResponse;
import com.nyamnyam.coach.guild.dto.response.JoinRequestCreateResponse;
import com.nyamnyam.coach.guild.dto.response.JoinRequestRejectResponse;
import com.nyamnyam.coach.guild.dto.response.MyJoinRequestListResponse;
import com.nyamnyam.coach.guild.dto.response.MyJoinRequestResponse;
import com.nyamnyam.coach.guild.dto.response.MyGuildListResponse;
import com.nyamnyam.coach.guild.dto.response.MyGuildResponse;
import com.nyamnyam.coach.guild.dto.response.MyGuildStatusResponse;
import com.nyamnyam.coach.guild.entity.Guild;
import com.nyamnyam.coach.guild.entity.GuildJoinRequest;
import com.nyamnyam.coach.guild.entity.GuildMember;
import com.nyamnyam.coach.guild.entity.GuildRole;
import com.nyamnyam.coach.guild.entity.GuildStatus;
import com.nyamnyam.coach.guild.entity.GuildVisibility;
import com.nyamnyam.coach.guild.entity.JoinRequestStatus;
import com.nyamnyam.coach.guild.entity.MyGuildJoinStatus;
import com.nyamnyam.coach.guild.repository.GuildRepository;
import com.nyamnyam.coach.guild.repository.row.GuildDetailRow;
import com.nyamnyam.coach.guild.repository.row.GuildMemberRow;
import com.nyamnyam.coach.guild.repository.row.GuildNoticeRow;
import com.nyamnyam.coach.guild.repository.row.JoinRequestRow;
import com.nyamnyam.coach.guild.repository.row.GuildSummaryRow;
import com.nyamnyam.coach.guild.repository.row.MyGuildRow;
import com.nyamnyam.coach.item.dto.response.CharacterEquipmentResponse;
import com.nyamnyam.coach.item.entity.ItemSlotType;
import com.nyamnyam.coach.item.repository.CharacterEquipmentRepository;
import com.nyamnyam.coach.item.repository.row.CharacterEquipmentRow;
import com.nyamnyam.coach.ranking.service.GuildScoreService;
import com.nyamnyam.coach.user.entity.User;
import com.nyamnyam.coach.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GuildService {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 50;
    private static final int DEFAULT_MAX_MEMBERS = 30;
    private static final int MIN_MAX_MEMBERS = 1;
    private static final int MAX_MAX_MEMBERS = 30;
    private static final int INVITE_CODE_RETRY_LIMIT = 5;
    private static final String INVITE_CODE_PREFIX = "NYAM-";
    private static final char[] INVITE_CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();
    private static final String ACTIVE_STATUS = "ACTIVE";

    private final GuildRepository guildRepository;
    private final BossBattleParticipantRepository bossBattleParticipantRepository;
    private final UserRepository userRepository;
    private final GuildValidator guildValidator;
    private final GuildScoreService guildScoreService;
    private final CharacterEquipmentRepository characterEquipmentRepository;
    private final GuildChatService guildChatService;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional(readOnly = true)
    public GuildListResponse getGuilds(Long userId, Integer page, Integer size, String keyword, String sort) {
        findActiveUser(userId);

        int normalizedPage = normalizePage(page);
        int normalizedSize = normalizeSize(size);
        int limit = normalizedSize + 1;
        int offset = normalizedPage * normalizedSize;
        String normalizedKeyword = StringUtils.hasText(keyword) ? keyword.trim() : null;
        String normalizedSort = normalizeSort(sort);
        boolean alreadyJoinedAnyGuild = guildRepository.existsActiveMembershipByUserId(userId);

        List<GuildSummaryRow> rows = guildRepository.findActiveGuildSummaries(
                userId,
                normalizedKeyword,
                normalizedSort,
                limit,
                offset
        );

        boolean hasNext = rows.size() > normalizedSize;
        List<GuildSummaryResponse> guilds = rows.stream()
                .limit(normalizedSize)
                .map(row -> toGuildSummaryResponse(row, alreadyJoinedAnyGuild))
                .toList();

        return new GuildListResponse(guilds, normalizedPage, normalizedSize, hasNext);
    }

    @Transactional
    public GuildCreateResponse createGuild(Long userId, GuildCreateRequest request) {
        findActiveUser(userId);
        guildValidator.validateNotJoinedAnyGuild(userId);
        guildValidator.validateNoPendingJoinRequest(userId);
        validateGuildNameAvailable(request.name());

        int maxMembers = resolveMaxMembers(request.maxMembers());
        String inviteCode = generateInviteCode();

        Guild guild = Guild.builder()
                .name(request.name().trim())
                .description(normalizeDescription(request.description()))
                .inviteCode(inviteCode)
                .ownerUserId(userId)
                .maxMembers(maxMembers)
                .guildPoint(0)
                .visibility(GuildVisibility.PRIVATE.name())
                .status(GuildStatus.ACTIVE.name())
                .build();

        guildRepository.save(guild);

        GuildMember ownerMember = GuildMember.builder()
                .guildId(guild.getGuildId())
                .userId(userId)
                .role(GuildRole.OWNER.name())
                .build();
        guildRepository.saveMember(ownerMember);

        Guild savedGuild = guildValidator.validateGuildActive(guild.getGuildId());
        int memberCount = guildRepository.countActiveMembers(savedGuild.getGuildId());

        return new GuildCreateResponse(
                savedGuild.getGuildId(),
                savedGuild.getName(),
                savedGuild.getDescription(),
                savedGuild.getInviteCode(),
                savedGuild.getOwnerUserId(),
                GuildRole.OWNER.name(),
                memberCount,
                savedGuild.getMaxMembers(),
                savedGuild.getVisibility(),
                savedGuild.getStatus(),
                savedGuild.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public MyGuildListResponse getMyGuilds(Long userId) {
        findActiveUser(userId);

        List<MyGuildResponse> guilds = guildRepository.findMyActiveGuilds(userId)
                .stream()
                .map(this::toMyGuildResponse)
                .toList();

        return new MyGuildListResponse(guilds);
    }

    @Transactional(readOnly = true)
    public MyGuildStatusResponse getMyGuildStatus(Long userId) {
        findActiveUser(userId);
        return guildValidator.getMyGuildStatus(userId);
    }

    @Transactional(readOnly = true)
    public GuildDetailResponse getGuildDetail(Long guildId, Long userId) {
        findActiveUser(userId);
        guildValidator.validateGuildMember(guildId, userId);

        GuildDetailRow row = guildRepository.findGuildDetail(guildId, userId)
                .orElseThrow(() -> new BusinessException(GuildErrorCode.GUILD_NOT_FOUND));

        return toGuildDetailResponse(row);
    }

    @Transactional(readOnly = true)
    public GuildMemberListResponse getGuildMembers(Long guildId, Long userId) {
        findActiveUser(userId);
        guildValidator.validateGuildMember(guildId, userId);

        List<GuildMemberResponse> members = guildRepository.findActiveMembers(guildId)
                .stream()
                .map(row -> toGuildMemberResponse(row, userId))
                .toList();

        return new GuildMemberListResponse(members);
    }

    @Transactional
    public GuildUpdateResponse updateGuild(Long guildId, Long userId, GuildUpdateRequest request) {
        findActiveUser(userId);
        guildValidator.validateGuildActive(guildId);
        guildValidator.validateGuildOwner(guildId, userId);
        validateGuildNameAvailableForUpdate(request.name(), guildId);

        int maxMembers = resolveMaxMembers(request.maxMembers());
        int memberCount = guildRepository.countActiveMembers(guildId);
        if (maxMembers < memberCount) {
            throw new BusinessException(GuildErrorCode.GUILD_MAX_MEMBERS_LESS_THAN_CURRENT_MEMBERS);
        }

        guildRepository.updateGuild(
                guildId,
                request.name().trim(),
                normalizeDescription(request.description()),
                maxMembers
        );
        Guild updatedGuild = guildValidator.validateGuildActive(guildId);

        return new GuildUpdateResponse(
                updatedGuild.getGuildId(),
                updatedGuild.getName(),
                updatedGuild.getDescription(),
                updatedGuild.getInviteCode(),
                memberCount,
                updatedGuild.getMaxMembers(),
                updatedGuild.getVisibility(),
                updatedGuild.getStatus(),
                updatedGuild.getUpdatedAt()
        );
    }

    @Transactional
    public GuildDeleteResponse deleteGuild(Long guildId, Long userId) {
        findActiveUser(userId);
        Guild guild = guildValidator.validateGuildExists(guildId);
        if (!GuildStatus.ACTIVE.name().equals(guild.getStatus())) {
            throw new BusinessException(GuildErrorCode.GUILD_ALREADY_INACTIVE);
        }
        guildValidator.validateGuildOwner(guildId, userId);

        // TODO: Boss Battle PR에서 진행 중인 보스전이 있으면 삭제를 막는 검증을 추가한다.
        guildRepository.softDeleteGuild(guildId);
        guildRepository.markAllMembersLeft(guildId);
        Guild deletedGuild = guildValidator.validateGuildExists(guildId);

        return new GuildDeleteResponse(
                deletedGuild.getGuildId(),
                deletedGuild.getStatus(),
                deletedGuild.getUpdatedAt()
        );
    }

    @Transactional
    public GuildLeaveResponse leaveGuild(Long guildId, Long userId) {
        findActiveUser(userId);
        guildValidator.validateGuildActive(guildId);
        GuildMemberRow member = guildRepository.findActiveMemberByGuildIdAndUserId(guildId, userId)
                .orElseThrow(() -> new BusinessException(GuildErrorCode.GUILD_MEMBER_NOT_FOUND));

        if (GuildRole.OWNER.name().equals(member.getRole())) {
            throw new BusinessException(GuildErrorCode.GUILD_OWNER_CANNOT_LEAVE);
        }

        guildRepository.leaveGuild(guildId, userId);
        bossBattleParticipantRepository.markParticipantLeftByGuildAndUser(guildId, userId);
        guildChatService.createSystemMessage(guildId, member.getNickname() + "님이 길드를 탈퇴했습니다.");
        GuildMemberRow leftMember = guildRepository.findMemberByGuildIdAndUserId(guildId, userId)
                .orElseThrow(() -> new BusinessException(GuildErrorCode.GUILD_MEMBER_NOT_FOUND));

        return new GuildLeaveResponse(
                guildId,
                userId,
                leftMember.getLeftAt()
        );
    }

    @Transactional(readOnly = true)
    public GuildMemberDetailResponse getGuildMemberDetail(Long guildId, Long memberId, Long userId) {
        findActiveUser(userId);
        guildValidator.validateGuildMember(guildId, userId);

        GuildMemberRow member = guildRepository.findMemberDetail(guildId, memberId, userId)
                .orElseThrow(() -> new BusinessException(GuildErrorCode.GUILD_MEMBER_NOT_FOUND));

        GuildScoreService.WeekPeriod period = guildScoreService.resolveWeek(null, null);
        int elapsedDays = guildScoreService.calculateElapsedDaysInWeek(period.startDate(), period.endDate());
        int recordedDays = guildRepository.countMemberRecordedDays(
                guildId,
                member.getUserId(),
                period.startDate(),
                period.endDate()
        );
        int weeklyRecordRate = (int) Math.round(guildScoreService.safeRate(recordedDays, elapsedDays));

        Long battleId = guildRepository.findCurrentOrLatestBattleIdByGuildId(guildId).orElse(null);
        int bossContribution = battleId == null
                ? 0
                : guildRepository.sumMemberCompletedQuestDamage(battleId, member.getUserId());
        int completedQuestCount = battleId == null
                ? 0
                : guildRepository.countMemberCompletedQuests(battleId, member.getUserId());
        List<CharacterEquipmentResponse> equippedItems = getCharacterEquipments(member.getCharacterId());

        return toGuildMemberDetailResponse(
                member,
                userId,
                weeklyRecordRate,
                bossContribution,
                completedQuestCount,
                equippedItems
        );
    }

    @Transactional
    public GuildKickResponse kickGuildMember(Long guildId, Long memberId, Long userId) {
        findActiveUser(userId);
        guildValidator.validateGuildOwner(guildId, userId);

        GuildMemberRow targetMember = guildRepository.findMemberByMemberId(guildId, memberId)
                .orElseThrow(() -> new BusinessException(GuildErrorCode.GUILD_MEMBER_NOT_FOUND));

        if (targetMember.getLeftAt() != null) {
            throw new BusinessException(GuildErrorCode.GUILD_MEMBER_ALREADY_LEFT);
        }
        if (GuildRole.OWNER.name().equals(targetMember.getRole())) {
            throw new BusinessException(GuildErrorCode.GUILD_CANNOT_KICK_OWNER);
        }

        guildRepository.kickGuildMember(guildId, memberId);
        bossBattleParticipantRepository.markParticipantKickedByGuildAndUser(guildId, targetMember.getUserId());
        guildChatService.createSystemMessage(guildId, targetMember.getNickname() + "님이 길드에서 추방되었습니다.");
        GuildMemberRow kickedMember = guildRepository.findMemberByMemberId(guildId, memberId)
                .orElseThrow(() -> new BusinessException(GuildErrorCode.GUILD_MEMBER_NOT_FOUND));

        return new GuildKickResponse(
                guildId,
                memberId,
                targetMember.getUserId(),
                kickedMember.getLeftAt()
        );
    }

    @Transactional(readOnly = true)
    public GuildNoticeListResponse getGuildNotices(Long guildId, Long userId) {
        findActiveUser(userId);
        String role = guildValidator.validateGuildMember(guildId, userId);
        boolean editable = GuildRole.OWNER.name().equals(role);

        List<GuildNoticeResponse> notices = guildRepository.findGuildNotices(guildId)
                .stream()
                .map(row -> toGuildNoticeResponse(row, editable))
                .toList();

        return new GuildNoticeListResponse(notices);
    }

    @Transactional(readOnly = true)
    public GuildNoticeResponse getGuildNotice(Long guildId, Long noticeId, Long userId) {
        findActiveUser(userId);
        String role = guildValidator.validateGuildMember(guildId, userId);

        GuildNoticeRow notice = findGuildNotice(guildId, noticeId);
        return toGuildNoticeResponse(notice, GuildRole.OWNER.name().equals(role));
    }

    @Transactional
    public GuildNoticeCreateResponse createGuildNotice(
            Long guildId,
            Long userId,
            GuildNoticeCreateRequest request
    ) {
        findActiveUser(userId);
        guildValidator.validateGuildOwner(guildId, userId);

        String title = normalizeNoticeText(request.title());
        String content = normalizeNoticeText(request.content());
        validateNotice(title, content);

        GuildNoticeRow noticeToSave = new GuildNoticeRow();
        noticeToSave.setGuildId(guildId);
        noticeToSave.setWriterUserId(userId);
        noticeToSave.setTitle(title);
        noticeToSave.setContent(content);
        guildRepository.saveGuildNotice(noticeToSave);

        GuildNoticeRow notice = findGuildNotice(guildId, noticeToSave.getNoticeId());

        return toGuildNoticeCreateResponse(notice);
    }

    @Transactional
    public GuildNoticeUpdateResponse updateGuildNotice(
            Long guildId,
            Long noticeId,
            Long userId,
            GuildNoticeUpdateRequest request
    ) {
        findActiveUser(userId);
        guildValidator.validateGuildOwner(guildId, userId);
        findGuildNotice(guildId, noticeId);

        String title = normalizeNoticeText(request.title());
        String content = normalizeNoticeText(request.content());
        validateNotice(title, content);

        guildRepository.updateGuildNotice(guildId, noticeId, title, content);
        GuildNoticeRow notice = findGuildNotice(guildId, noticeId);

        return toGuildNoticeUpdateResponse(notice);
    }

    @Transactional
    public GuildNoticeDeleteResponse deleteGuildNotice(Long guildId, Long noticeId, Long userId) {
        findActiveUser(userId);
        guildValidator.validateGuildOwner(guildId, userId);
        findGuildNotice(guildId, noticeId);

        guildRepository.deleteGuildNotice(guildId, noticeId);
        return new GuildNoticeDeleteResponse(guildId, noticeId, true);
    }

    @Transactional
    public JoinRequestCreateResponse createJoinRequestByInviteCode(
            Long userId,
            JoinRequestCreateByCodeRequest request
    ) {
        findActiveUser(userId);
        Guild guild = guildRepository.findActiveGuildByInviteCode(request.inviteCode().trim())
                .orElseThrow(() -> new BusinessException(GuildErrorCode.INVALID_INVITE_CODE));

        return createJoinRequest(userId, guild);
    }

    @Transactional
    public JoinRequestCreateResponse createJoinRequest(
            Long guildId,
            Long userId,
            JoinRequestCreateRequest request
    ) {
        findActiveUser(userId);
        Guild guild = guildValidator.validateGuildActive(guildId);

        return createJoinRequest(userId, guild);
    }

    @Transactional(readOnly = true)
    public MyJoinRequestListResponse getMyJoinRequests(
            Long userId,
            String status,
            Integer page,
            Integer size
    ) {
        findActiveUser(userId);
        int normalizedPage = normalizePage(page);
        int normalizedSize = normalizeSize(size);
        int limit = normalizedSize + 1;
        int offset = normalizedPage * normalizedSize;
        String normalizedStatus = normalizeJoinRequestStatusOrNull(status);

        List<JoinRequestRow> rows = guildRepository.findMyJoinRequests(
                userId,
                normalizedStatus,
                limit,
                offset
        );
        boolean hasNext = rows.size() > normalizedSize;
        List<MyJoinRequestResponse> requests = rows.stream()
                .limit(normalizedSize)
                .map(this::toMyJoinRequestResponse)
                .toList();

        return new MyJoinRequestListResponse(requests, normalizedPage, normalizedSize, hasNext);
    }

    @Transactional(readOnly = true)
    public GuildJoinRequestListResponse getGuildJoinRequests(
            Long guildId,
            Long userId,
            String status,
            Integer page,
            Integer size
    ) {
        findActiveUser(userId);
        guildValidator.validateGuildOwner(guildId, userId);
        int normalizedPage = normalizePage(page);
        int normalizedSize = normalizeSize(size);
        int limit = normalizedSize + 1;
        int offset = normalizedPage * normalizedSize;
        String normalizedStatus = normalizeJoinRequestStatusOrDefault(status, JoinRequestStatus.PENDING.name());

        List<JoinRequestRow> rows = guildRepository.findGuildJoinRequests(
                guildId,
                normalizedStatus,
                limit,
                offset
        );
        boolean hasNext = rows.size() > normalizedSize;
        List<GuildJoinRequestResponse> requests = rows.stream()
                .limit(normalizedSize)
                .map(this::toGuildJoinRequestResponse)
                .toList();

        return new GuildJoinRequestListResponse(requests, normalizedPage, normalizedSize, hasNext);
    }

    @Transactional
    public JoinRequestApproveResponse approveJoinRequest(Long guildId, Long requestId, Long userId) {
        findActiveUser(userId);
        guildValidator.validateGuildOwner(guildId, userId);
        JoinRequestRow request = findPendingJoinRequest(guildId, requestId);
        guildValidator.validateNotJoinedAnyGuild(request.getUserId());
        guildValidator.validateGuildCapacity(guildId);

        GuildMemberRow existingMember = guildRepository.findMemberByGuildIdAndUserId(guildId, request.getUserId())
                .orElse(null);
        Long memberId;
        if (existingMember == null) {
            GuildMember guildMember = GuildMember.builder()
                    .guildId(guildId)
                    .userId(request.getUserId())
                    .role(GuildRole.MEMBER.name())
                    .build();
            guildRepository.saveMember(guildMember);
            memberId = guildMember.getGuildMemberId();
        } else {
            guildRepository.reactivateGuildMember(guildId, request.getUserId());
            memberId = existingMember.getMemberId();
        }

        if (guildRepository.approveJoinRequest(guildId, requestId, userId) == 0) {
            throw new BusinessException(GuildErrorCode.JOIN_REQUEST_ALREADY_HANDLED);
        }
        guildRepository.cancelOtherPendingRequests(request.getUserId(), requestId, userId);
        guildChatService.createSystemMessage(guildId, request.getNickname() + "님이 길드에 가입했습니다.");
        JoinRequestRow handledRequest = findJoinRequest(guildId, requestId);

        return new JoinRequestApproveResponse(
                handledRequest.getRequestId(),
                handledRequest.getGuildId(),
                handledRequest.getUserId(),
                handledRequest.getNickname(),
                handledRequest.getStatus(),
                handledRequest.getHandledBy(),
                handledRequest.getHandledAt(),
                memberId
        );
    }

    @Transactional
    public JoinRequestRejectResponse rejectJoinRequest(Long guildId, Long requestId, Long userId) {
        findActiveUser(userId);
        guildValidator.validateGuildOwner(guildId, userId);
        JoinRequestRow request = findPendingJoinRequest(guildId, requestId);

        if (guildRepository.rejectJoinRequest(guildId, requestId, userId) == 0) {
            throw new BusinessException(GuildErrorCode.JOIN_REQUEST_ALREADY_HANDLED);
        }
        JoinRequestRow handledRequest = findJoinRequest(guildId, requestId);

        return new JoinRequestRejectResponse(
                handledRequest.getRequestId(),
                handledRequest.getGuildId(),
                request.getUserId(),
                handledRequest.getStatus(),
                handledRequest.getHandledBy(),
                handledRequest.getHandledAt()
        );
    }

    @Transactional
    public JoinRequestCancelResponse cancelJoinRequest(Long guildId, Long requestId, Long userId) {
        findActiveUser(userId);
        JoinRequestRow request = findPendingJoinRequest(guildId, requestId);
        if (!request.getUserId().equals(userId)) {
            throw new BusinessException(GuildErrorCode.JOIN_REQUEST_ACCESS_DENIED);
        }

        if (guildRepository.cancelJoinRequest(guildId, requestId, userId) == 0) {
            throw new BusinessException(GuildErrorCode.JOIN_REQUEST_ALREADY_HANDLED);
        }
        JoinRequestRow canceledRequest = findJoinRequest(guildId, requestId);

        return new JoinRequestCancelResponse(
                canceledRequest.getRequestId(),
                canceledRequest.getGuildId(),
                canceledRequest.getStatus(),
                canceledRequest.getHandledAt()
        );
    }

    private JoinRequestCreateResponse createJoinRequest(Long userId, Guild guild) {
        guildValidator.validateNotJoinedAnyGuild(userId);
        guildValidator.validateNoPendingJoinRequest(userId);
        guildValidator.validateGuildCapacity(guild.getGuildId());

        GuildJoinRequest joinRequest = GuildJoinRequest.builder()
                .guildId(guild.getGuildId())
                .userId(userId)
                .status(JoinRequestStatus.PENDING.name())
                .build();
        guildRepository.insertJoinRequest(joinRequest);
        JoinRequestRow savedRequest = findJoinRequest(guild.getGuildId(), joinRequest.getRequestId());

        return new JoinRequestCreateResponse(
                savedRequest.getRequestId(),
                savedRequest.getGuildId(),
                savedRequest.getGuildName(),
                savedRequest.getInviteCode(),
                savedRequest.getStatus(),
                savedRequest.getCreatedAt()
        );
    }

    private JoinRequestRow findJoinRequest(Long guildId, Long requestId) {
        return guildRepository.findJoinRequestByGuildIdAndRequestId(guildId, requestId)
                .orElseThrow(() -> new BusinessException(GuildErrorCode.JOIN_REQUEST_NOT_FOUND));
    }

    private JoinRequestRow findPendingJoinRequest(Long guildId, Long requestId) {
        JoinRequestRow request = findJoinRequest(guildId, requestId);
        if (!JoinRequestStatus.PENDING.name().equals(request.getStatus())) {
            throw new BusinessException(GuildErrorCode.JOIN_REQUEST_NOT_PENDING);
        }
        return request;
    }

    private User findActiveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
        if (!ACTIVE_STATUS.equals(user.getStatus())) {
            throw new BusinessException(AuthErrorCode.USER_INACTIVE);
        }
        return user;
    }

    private void validateGuildNameAvailable(String name) {
        if (guildRepository.existsByName(name.trim())) {
            throw new BusinessException(GuildErrorCode.GUILD_NAME_DUPLICATED);
        }
    }

    private void validateGuildNameAvailableForUpdate(String name, Long guildId) {
        if (guildRepository.existsByNameExceptGuildId(name.trim(), guildId)) {
            throw new BusinessException(GuildErrorCode.GUILD_NAME_DUPLICATED);
        }
    }

    private int resolveMaxMembers(Integer maxMembers) {
        int resolved = maxMembers == null ? DEFAULT_MAX_MEMBERS : maxMembers;
        if (resolved < MIN_MAX_MEMBERS || resolved > MAX_MAX_MEMBERS) {
            throw new BusinessException(GuildErrorCode.GUILD_MAX_MEMBERS_INVALID);
        }
        return resolved;
    }

    private String generateInviteCode() {
        for (int attempt = 0; attempt < INVITE_CODE_RETRY_LIMIT; attempt++) {
            String inviteCode = INVITE_CODE_PREFIX + randomCodeSuffix();
            if (!guildRepository.existsByInviteCode(inviteCode)) {
                return inviteCode;
            }
        }
        throw new BusinessException(GuildErrorCode.INVITE_CODE_GENERATION_FAILED);
    }

    private String randomCodeSuffix() {
        StringBuilder builder = new StringBuilder(4);
        for (int i = 0; i < 4; i++) {
            builder.append(INVITE_CODE_CHARS[secureRandom.nextInt(INVITE_CODE_CHARS.length)]);
        }
        return builder.toString();
    }

    private int normalizePage(Integer page) {
        if (page == null || page < 0) {
            return DEFAULT_PAGE;
        }
        return page;
    }

    private int normalizeSize(Integer size) {
        if (size == null || size <= 0) {
            return DEFAULT_SIZE;
        }
        return Math.min(size, MAX_PAGE_SIZE);
    }

    private String normalizeSort(String sort) {
        if ("createdAt".equals(sort)) {
            return "createdAt";
        }
        return "guildPoint";
    }

    private String normalizeDescription(String description) {
        return StringUtils.hasText(description) ? description.trim() : null;
    }

    private String normalizeNoticeText(String text) {
        return StringUtils.hasText(text) ? text.trim() : null;
    }

    private String normalizeJoinRequestStatusOrNull(String status) {
        if (!StringUtils.hasText(status)) {
            return null;
        }
        return normalizeJoinRequestStatusOrDefault(status, null);
    }

    private String normalizeJoinRequestStatusOrDefault(String status, String defaultStatus) {
        if (!StringUtils.hasText(status)) {
            return defaultStatus;
        }
        try {
            return JoinRequestStatus.valueOf(status.trim().toUpperCase()).name();
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(GuildErrorCode.JOIN_REQUEST_NOT_FOUND);
        }
    }

    private void validateNotice(String title, String content) {
        if (!StringUtils.hasText(title) || !StringUtils.hasText(content)) {
            throw new BusinessException(GuildErrorCode.GUILD_NOTICE_INVALID);
        }
    }

    private GuildNoticeRow findGuildNotice(Long guildId, Long noticeId) {
        return guildRepository.findGuildNoticeById(guildId, noticeId)
                .orElseThrow(() -> new BusinessException(GuildErrorCode.GUILD_NOTICE_NOT_FOUND));
    }

    private GuildSummaryResponse toGuildSummaryResponse(
            GuildSummaryRow row,
            boolean alreadyJoinedAnyGuild
    ) {
        return new GuildSummaryResponse(
                row.getGuildId(),
                row.getName(),
                row.getDescription(),
                row.getInviteCode(),
                row.getMemberCount(),
                row.getMaxMembers(),
                row.getGuildPoint(),
                row.getOwnerNickname(),
                resolveJoinStatus(row),
                row.getPendingRequestId(),
                alreadyJoinedAnyGuild
        );
    }

    private MyGuildJoinStatus resolveJoinStatus(GuildSummaryRow row) {
        if (row.getJoinedGuildId() != null) {
            return MyGuildJoinStatus.JOINED;
        }
        if (row.getPendingRequestId() != null) {
            return MyGuildJoinStatus.PENDING;
        }
        return MyGuildJoinStatus.NONE;
    }

    private MyGuildResponse toMyGuildResponse(MyGuildRow row) {
        return new MyGuildResponse(
                row.getGuildId(),
                row.getName(),
                row.getDescription(),
                row.getInviteCode(),
                row.getMemberCount(),
                row.getMaxMembers(),
                row.getGuildPoint(),
                row.getMyRole(),
                row.getJoinedAt()
        );
    }

    private GuildDetailResponse toGuildDetailResponse(GuildDetailRow row) {
        return new GuildDetailResponse(
                row.getGuildId(),
                row.getName(),
                row.getDescription(),
                row.getInviteCode(),
                row.getOwnerUserId(),
                row.getOwnerNickname(),
                row.getMemberCount(),
                row.getMaxMembers(),
                row.getGuildPoint(),
                row.getVisibility(),
                row.getStatus(),
                row.getMyRole(),
                row.getCreatedAt(),
                row.getUpdatedAt()
        );
    }

    private GuildMemberResponse toGuildMemberResponse(GuildMemberRow row, Long userId) {
        return new GuildMemberResponse(
                row.getMemberId(),
                row.getUserId(),
                row.getNickname(),
                row.getProfileImageUrl(),
                row.getCharacterId(),
                row.getCharacterName(),
                row.getCharacterLevel(),
                row.getCharacterStage(),
                row.getCharacterMood(),
                row.getCharacterAppearanceType(),
                row.getRole(),
                row.getJoinedAt(),
                row.getUserId().equals(userId)
        );
    }

    private GuildMemberDetailResponse toGuildMemberDetailResponse(
            GuildMemberRow row,
            Long userId,
            int weeklyRecordRate,
            int bossContribution,
            int completedQuestCount,
            List<CharacterEquipmentResponse> equippedItems
    ) {
        return new GuildMemberDetailResponse(
                row.getMemberId(),
                row.getUserId(),
                row.getNickname(),
                row.getProfileImageUrl(),
                row.getCharacterId(),
                row.getCharacterName(),
                row.getCharacterLevel(),
                row.getCharacterStage(),
                row.getCharacterMood(),
                row.getCharacterAppearanceType(),
                row.getStreakDays(),
                row.getRole(),
                row.getJoinedAt(),
                row.getUserId().equals(userId),
                weeklyRecordRate,
                bossContribution,
                completedQuestCount,
                equippedItems
        );
    }

    private List<CharacterEquipmentResponse> getCharacterEquipments(Long characterId) {
        List<CharacterEquipmentRow> rows = characterId == null
                ? List.of()
                : characterEquipmentRepository.findByCharacterId(characterId);
        List<CharacterEquipmentResponse> responses = new ArrayList<>();

        for (ItemSlotType slotType : ItemSlotType.values()) {
            rows.stream()
                    .filter(row -> slotType.name().equals(row.getSlotType()))
                    .findFirst()
                    .map(this::toCharacterEquipmentResponse)
                    .ifPresentOrElse(
                            responses::add,
                            () -> responses.add(emptyCharacterEquipmentResponse(slotType))
                    );
        }
        responses.sort(Comparator.comparingInt(response -> {
            if (ItemSlotType.CHARACTER.name().equals(response.slotType())) return 0;
            if (ItemSlotType.HEAD.name().equals(response.slotType())) return 1;
            if (ItemSlotType.HAND.name().equals(response.slotType())) return 2;
            return ItemSlotType.BACKGROUND.name().equals(response.slotType()) ? 3 : 9;
        }));
        return responses;
    }

    private CharacterEquipmentResponse toCharacterEquipmentResponse(CharacterEquipmentRow row) {
        return new CharacterEquipmentResponse(
                row.getSlotType(),
                true,
                row.getUserItemId(),
                row.getItemId(),
                row.getName(),
                row.getDescription(),
                row.getImageUrl(),
                row.getEffectValue(),
                row.getEquippedAt()
        );
    }

    private CharacterEquipmentResponse emptyCharacterEquipmentResponse(ItemSlotType slotType) {
        return new CharacterEquipmentResponse(
                slotType.name(),
                false,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    private GuildNoticeResponse toGuildNoticeResponse(GuildNoticeRow row, boolean editable) {
        return new GuildNoticeResponse(
                row.getNoticeId(),
                row.getGuildId(),
                row.getWriterUserId(),
                row.getWriterNickname(),
                row.getTitle(),
                row.getContent(),
                row.getCreatedAt(),
                row.getUpdatedAt(),
                editable
        );
    }

    private GuildNoticeCreateResponse toGuildNoticeCreateResponse(GuildNoticeRow row) {
        return new GuildNoticeCreateResponse(
                row.getNoticeId(),
                row.getGuildId(),
                row.getWriterUserId(),
                row.getWriterNickname(),
                row.getTitle(),
                row.getContent(),
                row.getCreatedAt(),
                row.getUpdatedAt()
        );
    }

    private GuildNoticeUpdateResponse toGuildNoticeUpdateResponse(GuildNoticeRow row) {
        return new GuildNoticeUpdateResponse(
                row.getNoticeId(),
                row.getGuildId(),
                row.getWriterUserId(),
                row.getWriterNickname(),
                row.getTitle(),
                row.getContent(),
                row.getCreatedAt(),
                row.getUpdatedAt()
        );
    }

    private MyJoinRequestResponse toMyJoinRequestResponse(JoinRequestRow row) {
        return new MyJoinRequestResponse(
                row.getRequestId(),
                row.getGuildId(),
                row.getGuildName(),
                row.getInviteCode(),
                row.getGuildDescription(),
                row.getStatus(),
                row.getCreatedAt(),
                row.getHandledAt(),
                row.getHandledByNickname()
        );
    }

    private GuildJoinRequestResponse toGuildJoinRequestResponse(JoinRequestRow row) {
        return new GuildJoinRequestResponse(
                row.getRequestId(),
                row.getGuildId(),
                row.getUserId(),
                row.getNickname(),
                row.getProfileImageUrl(),
                row.getCharacterId(),
                row.getCharacterName(),
                row.getCharacterLevel(),
                row.getStatus(),
                row.getCreatedAt()
        );
    }
}
