<template>
  <section v-if="isLoading" class="loading-state">
    <AppCard>길드 정보를 불러오는 중...</AppCard>
  </section>

  <section v-else-if="!joined && joinMode === 'list'">
    <div v-if="errorMessage" class="api-message error">{{ errorMessage }}</div>
    <div v-if="successMessage" class="api-message success">{{ successMessage }}</div>
    <div class="pre-head">
      <div><div class="title-lg">길드 참여하기</div><p>마음에 드는 길드에 참여하거나, 코드로 직접 입장하세요</p></div>
      <AppButton @click="joinMode = 'create'">+ 길드 생성</AppButton>
    </div>
    <div class="pre-grid">
      <main class="guild-list">
        <div class="section-title"><div><div class="section-title-main">길드 목록</div><div class="section-title-sub">현재 페이지 {{ guilds.length }}개 길드</div></div></div>
        <div v-if="guilds.length === 0" class="empty-state">표시할 길드가 없습니다.</div>
        <div v-for="item in guilds" :key="item.guildId" class="guild-row">
          <div class="guild-logo">길</div>
          <div class="grow">
            <h3>{{ item.name }}</h3>
            <p>👥 {{ item.memberCount }}/{{ item.maxMembers }}명 · 🏆 {{ item.guildPoint.toLocaleString() }} pt · {{ item.description || '소개가 없습니다.' }}</p>
          </div>
          <AppButton
            v-if="requestForGuild(item.guildId)"
            size="sm"
            variant="secondary"
            :disabled="pendingActionId === `cancel-${item.guildId}`"
            @click="cancelJoinRequest(item.guildId)"
          >참여 요청 취소</AppButton>
          <AppButton
            v-else
            size="sm"
            :disabled="pendingActionId === `request-${item.guildId}` || item.alreadyJoinedAnyGuild"
            @click="requestJoinGuild(item.guildId)"
          >참여 요청</AppButton>
        </div>
        <div v-if="pages.length > 1" class="pagination">
          <button v-for="page in pages" :key="page" type="button" :class="{ active: currentPage === page }" @click="changePage(page)">{{ page }}</button>
        </div>
      </main>
      <AppCard class="code-card">
        <div class="section-title-main">코드로 참여</div>
        <p>길드 코드를 알고 있다면 직접 입력해 참여를 요청할 수 있어요.</p>
        <div class="mono-label">길드 코드</div>
        <label class="app-input"><input v-model="code" placeholder="예: NYAM-2840"></label>
        <AppButton full class="join-btn" :disabled="!code.trim() || pendingActionId === 'invite'" @click="requestByInviteCode">코드로 참여 요청</AppButton>
        <small>💡 길드 코드는 길드장에게 받을 수 있어요.<br>영문 + 숫자 4~12자리 형식입니다.</small>
      </AppCard>
    </div>
  </section>

  <section v-else-if="!joined && joinMode === 'create'">
    <div v-if="errorMessage" class="api-message error">{{ errorMessage }}</div>
    <div class="pre-head"><div><div class="title-lg">길드 생성</div><p>새 길드를 만들고 길드원을 초대해 함께 건강 목표를 달성하세요.</p></div><AppButton variant="secondary" @click="joinMode = 'list'">목록으로</AppButton></div>
    <AppCard class="create-card">
      <div class="form-grid">
        <label>길드 이름<input v-model="createForm.name" placeholder="예: 잘먹잘싸"></label>
        <label>최대 인원<input v-model.number="createForm.max" type="number" min="1" max="30"></label>
        <div class="generated-code"><small>자동 생성 길드 코드</small><strong>생성 후 자동 발급</strong></div>
      </div>
      <label class="wide">길드 소개<textarea v-model="createForm.description" placeholder="우리 길드의 식단 목표를 적어주세요."></textarea></label>
      <div class="create-actions"><AppButton variant="secondary" @click="joinMode = 'list'">취소</AppButton><AppButton :disabled="!createForm.name.trim() || pendingActionId === 'create'" @click="createGuild">길드 생성하기</AppButton></div>
    </AppCard>
  </section>

  <section v-else>
    <div v-if="errorMessage" class="api-message error">{{ errorMessage }}</div>
    <div v-if="successMessage" class="api-message success">{{ successMessage }}</div>

    <div v-if="selectedMember" class="modal-backdrop" @click="selectedMember = null">
      <div class="member-modal" @click.stop>
        <button class="modal-close" @click="selectedMember = null">×</button>
        <div class="member-detail-head">
          <div class="member-character"><NyamnyamCharacter :stage="characterStage(selectedMember.characterLevel)" :size="96" /></div>
          <div><h2>{{ selectedMember.nickname }}</h2><p>LV.{{ selectedMember.characterLevel ?? '-' }} · {{ roleLabel(selectedMember.role) }}</p></div>
        </div>
        <div class="modal-info">
          <span><b>이번 주 기록률</b><strong>{{ valueOrDash(selectedMember.weeklyRecordRate, '%') }}</strong></span>
          <span><b>보스 기여도</b><strong>{{ valueOrDash(selectedMember.bossContribution, ' HP') }}</strong></span>
          <span><b>완료 퀘스트</b><strong>{{ valueOrDash(selectedMember.completedQuestCount, '개') }}</strong></span>
        </div>
      </div>
    </div>

    <div v-if="showRequests" class="modal-backdrop" @click="showRequests = false">
      <div class="request-modal" @click.stop>
        <button class="modal-close" @click="showRequests = false">×</button>
        <h2>참여 요청 관리</h2><p>길드장만 승인·거절할 수 있어요.</p>
        <div v-if="joinRequests.length === 0" class="empty-state">대기 중인 요청이 없습니다.</div>
        <div v-for="request in joinRequests" :key="request.requestId" class="request-row">
          <div><strong>{{ request.nickname || '이름 없음' }}</strong><span>LV.{{ request.characterLevel ?? '-' }} · {{ formatDate(request.createdAt) }}</span></div>
          <div><AppButton size="sm" :disabled="pendingActionId === `approve-${request.requestId}`" @click="approveRequest(request.requestId)">승인</AppButton><AppButton size="sm" variant="danger" :disabled="pendingActionId === `reject-${request.requestId}`" @click="rejectRequest(request.requestId)">거절</AppButton></div>
        </div>
      </div>
    </div>

    <div v-if="showSettings" class="modal-backdrop" @click="showSettings = false">
      <div class="request-modal" @click.stop>
        <button class="modal-close" @click="showSettings = false">×</button>
        <h2>길드 설정</h2><p>길드장 전용 설정 화면입니다.</p>
        <label>길드 이름<input v-model="settingsForm.name"></label>
        <label>길드 소개<textarea v-model="settingsForm.description"></textarea></label>
        <label>최대 인원<input v-model.number="settingsForm.maxMembers" type="number" min="1" max="30"></label>
        <div class="setting-members">
          <strong>길드원 관리</strong>
          <div v-for="member in kickableMembers" :key="member.memberId"><span>{{ member.nickname }} · LV.{{ member.characterLevel ?? '-' }}</span><AppButton size="sm" variant="danger" :disabled="pendingActionId === `kick-${member.memberId}`" @click="kickMember(member)">추방</AppButton></div>
        </div>
        <AppButton full variant="danger" :disabled="pendingActionId === 'delete-guild'" @click="removeGuild">길드 삭제</AppButton>
        <AppButton full :disabled="pendingActionId === 'update-guild'" @click="updateGuild">저장</AppButton>
      </div>
    </div>

    <AppCard :padding="20" class="guild-header">
      <div class="grow">
        <div class="pill-row"><AppPill tone="accent" size="sm">길드 코드 {{ guild?.inviteCode || '-' }}</AppPill><AppPill v-if="dashboard?.myRank" tone="ok" size="sm">전체 {{ dashboard.myRank }}위</AppPill><AppPill :tone="isLeader ? 'dark' : 'neutral'" size="sm">{{ isLeader ? '길드장' : '길드원' }}</AppPill></div>
        <h1>{{ guild?.name || '내 길드' }}</h1>
        <p>{{ guild?.memberCount ?? '-' }}명 · 주간 점수 <strong>{{ dashboard ? `${dashboard.weeklyScore.toLocaleString()} pt` : '-' }}</strong> · 이번 주 기록률 {{ dashboard ? `${dashboard.recordRate}%` : '-' }}</p>
      </div>
      <div class="header-actions"><AppButton v-if="isLeader" variant="secondary" size="sm" @click="showRequests = true">참여 요청 <AppPill tone="bad" size="sm">{{ joinRequests.length }}</AppPill></AppButton><AppButton v-if="isLeader" variant="secondary" size="sm" @click="openSettings">길드 설정</AppButton><AppButton v-if="!isLeader" variant="danger" size="sm" :disabled="pendingActionId === 'leave-guild'" @click="leaveGuild">{{ pendingActionId === 'leave-guild' ? '탈퇴 중...' : '길드 탈퇴' }}</AppButton></div>
    </AppCard>

    <div class="board">
      <AppCard :padding="0" class="chat-card">
        <div class="chat-head"><span></span><strong>길드 채팅</strong></div>
        <div ref="chatMessagesRef" class="feed"><div v-if="displayChats.length === 0" class="empty-state">아직 채팅이 없습니다.</div><ChatBubble v-for="chatItem in displayChats" :key="chatItem.id" :msg="chatItem" /></div>
        <form class="chat-input" @submit.prevent="sendChatMessage"><label><AppIcon name="plus" color="var(--ink-3)" /><input v-model="message" placeholder="메시지 보내기…" @keydown.enter="guardComposingEnter"><button class="chat-submit" type="submit" :disabled="!message.trim() || sendingChat"><AppIcon name="send" color="#fff" />보내기</button></label></form>
      </AppCard>

      <AppCard class="notice">
        <div class="notice-head"><div class="section-title-main">📢 공지사항</div><AppButton v-if="isLeader" size="sm" variant="secondary" @click="startAddNotice">공지 추가</AppButton></div>
        <div v-if="notices.length === 0 && editingNoticeId !== 'new'" class="empty-state">등록된 공지가 없습니다.</div>
        <div v-if="editingNoticeId === 'new'" class="notice-box"><label>제목<input v-model="noticeDraft.title" placeholder="공지 제목"></label><label>내용<textarea v-model="noticeDraft.body" placeholder="길드원에게 알릴 내용을 입력하세요."></textarea></label><div class="notice-actions"><AppButton size="sm" variant="ghost" @click="cancelNoticeEdit">취소</AppButton><AppButton size="sm" :disabled="pendingActionId === 'notice-save'" @click="saveNotice">저장</AppButton></div></div>
        <div v-for="noticeItem in notices" :key="noticeItem.noticeId" class="notice-box">
          <template v-if="editingNoticeId === noticeItem.noticeId"><label>제목<input v-model="noticeDraft.title"></label><label>내용<textarea v-model="noticeDraft.body"></textarea></label><div class="notice-actions"><AppButton size="sm" variant="ghost" @click="cancelNoticeEdit">취소</AppButton><AppButton size="sm" :disabled="pendingActionId === 'notice-save'" @click="saveNotice">저장</AppButton></div></template>
          <template v-else><strong>{{ noticeItem.title }}</strong><small>{{ formatDate(noticeItem.updatedAt || noticeItem.createdAt) }}</small><p>{{ noticeItem.content }}</p><div v-if="isLeader" class="notice-actions"><AppButton size="sm" variant="secondary" @click="startEditNotice(noticeItem)">수정</AppButton><AppButton size="sm" variant="danger" :disabled="pendingActionId === `notice-delete-${noticeItem.noticeId}`" @click="deleteNotice(noticeItem.noticeId)">삭제</AppButton></div></template>
        </div>
      </AppCard>

      <AppCard class="ranking"><div class="section-title"><div><div class="section-title-main">🏆 길드 순위</div><div class="section-title-sub">이번 주 길드 순위</div></div><AppPill v-if="dashboard?.myRank" tone="ok" size="sm">우리 {{ dashboard.myRank }}위</AppPill></div><div v-if="displayRankings.length === 0" class="empty-state">랭킹 데이터가 없습니다.</div><RankRow v-for="rank in displayRankings" :key="String(rank[0])" :row="rank" /></AppCard>

      <AppCard class="members"><div class="section-title-main">👥 길드원</div><div v-if="members.length === 0" class="empty-state">길드원 정보가 없습니다.</div><div class="member-grid"><button v-for="member in members" :key="member.memberId" @click="openMember(member)"><span>{{ member.nickname[0] || '?' }}</span><strong>{{ member.nickname }} <small>LV.{{ member.characterLevel ?? '-' }}</small></strong><em>{{ roleLabel(member.role) }}</em></button></div></AppCard>

      <AppCard class="stats"><div class="section-title-main">📊 길드 통계</div><div class="stat-grid"><StatBlock label="기록률" :value="dashboard ? `${dashboard.recordRate}%` : '-'" /><StatBlock label="보스 데미지" :value="dashboard ? `${dashboard.bossDamage} HP` : '-'" /><StatBlock label="주간 점수" :value="dashboard ? `${dashboard.weeklyScore.toLocaleString()} pt` : '-'" accent /><StatBlock label="퀘스트 완료" :value="dashboard ? `${dashboard.questCompletedCount}/${dashboard.questTotalCount}` : '-'" /></div><div v-if="chartStats.length" class="guild-chart"><div v-for="(stat, index) in chartStats" :key="stat.date"><b :style="{ height: stat.heightPercent > 0 ? `${stat.heightPercent}%` : '6px' }" :class="{ zero: stat.value === 0, today: index === chartStats.length - 1 }"></b><small>{{ dayLabel(stat.dayOfWeek) }}</small></div></div><p v-else>아직 주간 리포트가 없습니다.</p></AppCard>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, reactive, ref } from 'vue'
import AppButton from '../components/common/AppButton.vue'
import AppCard from '../components/common/AppCard.vue'
import AppIcon from '../components/common/AppIcon.vue'
import AppPill from '../components/common/AppPill.vue'
import NyamnyamCharacter from '../components/nyamnyam/NyamnyamCharacter.vue'
import { ApiError } from '../services/api/client'
import { guildApi } from '../services/api/guildApi'
import { guildChatApi } from '../services/api/guildChatApi'
import { rankingApi } from '../services/api/rankingApi'
import type { GuildDailyStat, GuildDashboard, GuildDetail, GuildJoinRequest, GuildMember, GuildMemberDetail, GuildNotice, GuildSummary, GuildWeeklyReport } from '../types/guild'
import type { GuildChatMessage } from '../types/guildChat'
import type { GuildRanking } from '../types/ranking'
import ChatBubble from './parts/ChatBubble.vue'
import RankRow from './parts/RankRow.vue'
import StatBlock from './parts/StatBlock.vue'

const joined = ref(false)
const joinMode = ref<'list' | 'create'>('list')
const isLoading = ref(true)
const errorMessage = ref('')
const successMessage = ref('')
const pendingActionId = ref<string | null>(null)
const sendingChat = ref(false)
const guildId = ref<number | null>(null)
const guild = ref<GuildDetail | null>(null)
const guilds = ref<GuildSummary[]>([])
const myJoinRequests = ref<GuildJoinRequest[]>([])
const joinRequests = ref<GuildJoinRequest[]>([])
const members = ref<GuildMember[]>([])
const notices = ref<GuildNotice[]>([])
const chats = ref<GuildChatMessage[]>([])
const rankings = ref<GuildRanking[]>([])
const dashboard = ref<GuildDashboard | null>(null)
const weeklyReport = ref<GuildWeeklyReport | null>(null)
const currentPage = ref(1)
const lastKnownPage = ref(1)
const hasNextPage = ref(false)
const pageSize = 10
const code = ref('')
const message = ref('')
const chatMessagesRef = ref<HTMLElement | null>(null)
const selectedMember = ref<GuildMemberDetail | null>(null)
const showRequests = ref(false)
const showSettings = ref(false)
const createForm = reactive({ name: '', max: 30, description: '' })
const settingsForm = reactive({ name: '', description: '', maxMembers: 30 })
const editingNoticeId = ref<number | 'new' | null>(null)
const noticeDraft = reactive({ title: '', body: '' })

const isLeader = computed(() => guild.value?.myRole === 'OWNER')
const pages = computed(() => Array.from({ length: Math.max(lastKnownPage.value, currentPage.value + (hasNextPage.value ? 1 : 0)) }, (_, index) => index + 1))
const kickableMembers = computed(() => members.value.filter((member) => !member.isMe && member.role !== 'OWNER'))
const displayChats = computed(() => chats.value.map((chatItem) => ({ id: chatItem.chatId, name: chatItem.nickname, time: formatDate(chatItem.createdAt), text: chatItem.message, mine: Boolean(chatItem.isMe), system: chatItem.messageType === 'SYSTEM' })))
const displayRankings = computed<Array<[number, string, string, number, boolean]>>(() => rankings.value.map((rank) => [rank.rank, rank.guildName, rank.myGuild ? '우리 길드' : '', rank.weeklyScore, rank.myGuild]))
const chartStats = computed(() => {
  const dayOrder = ['MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN']
  const dayAliases: Record<string, string> = {
    MONDAY: 'MON', TUESDAY: 'TUE', WEDNESDAY: 'WED', THURSDAY: 'THU',
    FRIDAY: 'FRI', SATURDAY: 'SAT', SUNDAY: 'SUN'
  }
  const statsByDay = new Map<string, GuildDailyStat>()

  for (const stat of weeklyReport.value?.dailyStats ?? []) {
    const day = String(stat.dayOfWeek ?? '').toUpperCase()
    statsByDay.set(dayAliases[day] ?? day, stat)
  }

  const orderedStats = dayOrder.flatMap((day) => {
    const stat = statsByDay.get(day)
    if (!stat) return []

    const score = Number(stat.score)
    const damage = Number(stat.damage)
    const hasScore = stat.score !== null && stat.score !== undefined && Number.isFinite(score)
    const value = Math.max(0, hasScore ? score : Number.isFinite(damage) ? damage : 0)
    return [{ ...stat, value }]
  })
  const maxValue = Math.max(0, ...orderedStats.map((stat) => stat.value))

  return orderedStats.map((stat) => ({
    ...stat,
    heightPercent: maxValue > 0 ? (stat.value / maxValue) * 100 : 0
  }))
})

function requestForGuild(id: number) {
  return myJoinRequests.value.find((request) => request.guildId === id && request.status === 'PENDING')
}

function setError(error: unknown) {
  errorMessage.value = error instanceof ApiError ? error.message : '요청을 처리하지 못했습니다. 잠시 후 다시 시도해주세요.'
}

function clearFeedback() {
  errorMessage.value = ''
  successMessage.value = ''
}

async function scrollChatToBottom() {
  await nextTick()
  const element = chatMessagesRef.value
  if (element) element.scrollTop = element.scrollHeight
}

async function runAction(key: string, task: () => Promise<void>, success?: string) {
  clearFeedback()
  pendingActionId.value = key
  try {
    await task()
    if (success) successMessage.value = success
  } catch (error) {
    setError(error)
  } finally {
    pendingActionId.value = null
  }
}

async function loadPage() {
  isLoading.value = true
  clearFeedback()
  try {
    const status = await guildApi.getMyGuildStatus()
    joined.value = status.status === 'JOINED' && Boolean(status.guild?.guildId)
    guildId.value = status.guild?.guildId ?? null
    if (joined.value && guildId.value) await loadGuildData(guildId.value)
    else await loadExploreData()
  } catch (error) {
    setError(error)
    joined.value = false
  } finally {
    isLoading.value = false
    if (joined.value) await scrollChatToBottom()
  }
}

async function loadExploreData() {
  const [list, requests] = await Promise.all([
    guildApi.getGuilds({ page: currentPage.value - 1, size: pageSize }),
    guildApi.getMyJoinRequests()
  ])
  guilds.value = list.guilds ?? []
  myJoinRequests.value = requests ?? []
  hasNextPage.value = list.hasNext
  lastKnownPage.value = Math.max(lastKnownPage.value, currentPage.value + (list.hasNext ? 1 : 0))
}

async function loadGuildData(id: number) {
  const [detail, memberList, noticeList, dashboardData, reportData, chatList, rankingList] = await Promise.all([
    guildApi.getGuild(id),
    guildApi.getGuildMembers(id),
    guildApi.getGuildNotices(id),
    guildApi.getGuildDashboard(id),
    guildApi.getGuildWeeklyReport(id),
    guildChatApi.getGuildChats(id, { page: 0, size: 30 }),
    rankingApi.getGuildRankings()
  ])
  guild.value = detail
  members.value = memberList ?? []
  notices.value = noticeList ?? []
  dashboard.value = dashboardData
  weeklyReport.value = reportData
  chats.value = chatList ?? []
  rankings.value = rankingList ?? []
  if (detail.myRole === 'OWNER') joinRequests.value = await guildApi.getGuildJoinRequests(id)
  else joinRequests.value = []
}

async function changePage(page: number) {
  if (page === currentPage.value) return
  currentPage.value = page
  await runAction('page', loadExploreData)
}

async function requestJoinGuild(id: number) {
  await runAction(`request-${id}`, async () => {
    await guildApi.createJoinRequest(id)
    await loadExploreData()
  }, '길드 참여 요청을 보냈습니다.')
}

async function cancelJoinRequest(id: number) {
  const request = requestForGuild(id)
  if (!request) return
  await runAction(`cancel-${id}`, async () => {
    await guildApi.cancelJoinRequest(id, request.requestId)
    await loadExploreData()
  }, '길드 참여 요청을 취소했습니다.')
}

async function requestByInviteCode() {
  const inviteCode = code.value.trim()
  if (!inviteCode) return
  await runAction('invite', async () => {
    await guildApi.createJoinRequestByInviteCode({ inviteCode })
    code.value = ''
    await loadExploreData()
  }, '초대 코드로 참여 요청을 보냈습니다.')
}

async function createGuild() {
  await runAction('create', async () => {
    await guildApi.createGuild({ name: createForm.name.trim(), description: createForm.description.trim(), maxMembers: createForm.max })
    joinMode.value = 'list'
    await loadPage()
  }, '길드를 생성했습니다.')
}

async function sendChatMessage() {
  const text = message.value.trim()
  if (!text || !guildId.value || sendingChat.value) return
  clearFeedback()
  sendingChat.value = true
  try {
    await guildChatApi.sendGuildChat(guildId.value, { message: text })
    message.value = ''
    chats.value = await guildChatApi.getGuildChats(guildId.value, { page: 0, size: 30 })
    await scrollChatToBottom()
  } catch (error) {
    setError(error)
  } finally {
    sendingChat.value = false
  }
}

function guardComposingEnter(event: KeyboardEvent) {
  if (event.isComposing) event.preventDefault()
}

async function openMember(member: GuildMember) {
  if (!guildId.value) return
  await runAction(`member-${member.memberId}`, async () => {
    selectedMember.value = await guildApi.getGuildMember(guildId.value!, member.memberId)
  })
}

async function approveRequest(requestId: number) {
  if (!guildId.value) return
  await runAction(`approve-${requestId}`, async () => {
    await guildApi.approveJoinRequest(guildId.value!, requestId)
    ;[joinRequests.value, members.value] = await Promise.all([guildApi.getGuildJoinRequests(guildId.value!), guildApi.getGuildMembers(guildId.value!)])
  }, '참여 요청을 승인했습니다.')
}

async function rejectRequest(requestId: number) {
  if (!guildId.value) return
  await runAction(`reject-${requestId}`, async () => {
    await guildApi.rejectJoinRequest(guildId.value!, requestId)
    joinRequests.value = await guildApi.getGuildJoinRequests(guildId.value!)
  }, '참여 요청을 거절했습니다.')
}

function openSettings() {
  settingsForm.name = guild.value?.name ?? ''
  settingsForm.description = guild.value?.description ?? ''
  settingsForm.maxMembers = guild.value?.maxMembers ?? 30
  showSettings.value = true
}

async function updateGuild() {
  if (!guildId.value) return
  await runAction('update-guild', async () => {
    await guildApi.updateGuild(guildId.value!, { name: settingsForm.name.trim(), description: settingsForm.description.trim(), maxMembers: settingsForm.maxMembers })
    guild.value = await guildApi.getGuild(guildId.value!)
    showSettings.value = false
  }, '길드 정보를 수정했습니다.')
}

async function kickMember(member: GuildMember) {
  if (!guildId.value || !window.confirm(`${member.nickname}님을 길드에서 추방할까요?`)) return
  await runAction(`kick-${member.memberId}`, async () => {
    await guildApi.kickGuildMember(guildId.value!, member.memberId)
    members.value = await guildApi.getGuildMembers(guildId.value!)
  }, '길드원을 추방했습니다.')
}

async function removeGuild() {
  if (!guildId.value || !window.confirm('길드를 삭제하면 되돌릴 수 없습니다. 정말 삭제할까요?')) return
  await runAction('delete-guild', async () => {
    await guildApi.deleteGuild(guildId.value!)
    showSettings.value = false
    await loadPage()
  }, '길드를 삭제했습니다.')
}

function clearGuildData() {
  guildId.value = null
  guild.value = null
  members.value = []
  notices.value = []
  joinRequests.value = []
  chats.value = []
  rankings.value = []
  dashboard.value = null
  weeklyReport.value = null
  selectedMember.value = null
  showRequests.value = false
  showSettings.value = false
  message.value = ''
}

async function leaveGuild() {
  const currentGuildId = guildId.value
  if (!currentGuildId || !window.confirm('정말 길드에서 탈퇴하시겠습니까?')) return
  await runAction('leave-guild', async () => {
    await guildApi.leaveGuild(currentGuildId)
    clearGuildData()
    joined.value = false
    currentPage.value = 1
    lastKnownPage.value = 1
    await loadPage()
  }, '길드에서 탈퇴했습니다.')
}

function startAddNotice() { editingNoticeId.value = 'new'; noticeDraft.title = ''; noticeDraft.body = '' }
function startEditNotice(notice: GuildNotice) { editingNoticeId.value = notice.noticeId; noticeDraft.title = notice.title; noticeDraft.body = notice.content }
function cancelNoticeEdit() { editingNoticeId.value = null; noticeDraft.title = ''; noticeDraft.body = '' }

async function saveNotice() {
  if (!guildId.value || !noticeDraft.title.trim() || !noticeDraft.body.trim()) return
  await runAction('notice-save', async () => {
    const payload = { title: noticeDraft.title.trim(), content: noticeDraft.body.trim() }
    if (editingNoticeId.value === 'new') await guildApi.createGuildNotice(guildId.value!, payload)
    else if (typeof editingNoticeId.value === 'number') await guildApi.updateGuildNotice(guildId.value!, editingNoticeId.value, payload)
    notices.value = await guildApi.getGuildNotices(guildId.value!)
    cancelNoticeEdit()
  }, '공지사항을 저장했습니다.')
}

async function deleteNotice(noticeId: number) {
  if (!guildId.value) return
  await runAction(`notice-delete-${noticeId}`, async () => {
    await guildApi.deleteGuildNotice(guildId.value!, noticeId)
    notices.value = await guildApi.getGuildNotices(guildId.value!)
  }, '공지사항을 삭제했습니다.')
}

function formatDate(value?: string) {
  if (!value) return '-'
  return new Intl.DateTimeFormat('ko-KR', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' }).format(new Date(value))
}
function valueOrDash(value: number | undefined, suffix: string) { return value === undefined ? '-' : `${value}${suffix}` }
function roleLabel(role: string) { return role === 'OWNER' ? '길드장' : '길드원' }
function characterStage(level?: number): 'egg' | 'chick' | 'adult' { return !level || level < 5 ? 'egg' : level < 15 ? 'chick' : 'adult' }
function dayLabel(day: string) { return ({ MON: '월', MONDAY: '월', TUE: '화', TUESDAY: '화', WED: '수', WEDNESDAY: '수', THU: '목', THURSDAY: '목', FRI: '금', FRIDAY: '금', SAT: '토', SATURDAY: '토', SUN: '일', SUNDAY: '일' } as Record<string, string>)[day.toUpperCase()] ?? day.slice(0, 1) }

onMounted(() => { void loadPage() })
</script>

<style scoped>
.loading-state { max-width: 560px; margin: 80px auto; text-align: center; }
.api-message { margin-bottom: 14px; padding: 11px 14px; border-radius: 10px; border: 1px solid var(--border); font-size: 13px; }
.api-message.error { color: var(--bad); border-color: var(--bad); background: var(--surface); }
.api-message.success { color: var(--ok); background: var(--surface); }
.empty-state { padding: 18px 10px; text-align: center; color: var(--ink-3); font-size: 12px; }
.pre-head { margin-bottom: 24px; display: flex; justify-content: space-between; align-items: flex-end; gap: 16px; } p { margin: 6px 0 0; font-size: 13px; color: var(--ink-2); line-height: 1.65; }
.pre-grid { display: grid; grid-template-columns: 1fr 340px; gap: 20px; align-items: start; }
.create-card { max-width: 760px; }
.form-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 12px; margin-bottom: 12px; }
.create-card label, .request-modal label { display: flex; flex-direction: column; gap: 6px; font-size: 12px; font-weight: 800; color: var(--ink); }
.create-card input, .create-card select, .create-card textarea, .request-modal input, .request-modal textarea { width: 100%; border: 1.5px solid var(--border-strong); border-radius: 10px; padding: 10px 12px; background: var(--surface); color: var(--ink); outline: 0; font-size: 13px; }
.create-card textarea, .request-modal textarea { min-height: 92px; resize: vertical; }
.wide { margin-top: 4px; }
.create-actions { display: flex; justify-content: flex-end; gap: 8px; margin-top: 14px; }
.guild-list { display: flex; flex-direction: column; gap: 10px; }
.guild-row { display: flex; align-items: center; gap: 16px; padding: 16px 20px; border-radius: 14px; border: 1.5px solid var(--border); background: var(--surface); box-shadow: var(--shadow); }
.pagination { display: flex; justify-content: center; gap: 6px; margin-top: 12px; }
.pagination button { width: 34px; height: 34px; border: 1.5px solid var(--border); border-radius: 10px; background: var(--surface); color: var(--ink-2); font-family: var(--mono); font-weight: 800; cursor: pointer; }
.pagination button.active { background: var(--accent); border-color: var(--accent); color: #fff; }
.guild-logo { width: 50px; height: 50px; border-radius: 12px; background: var(--yolk); border: 1.5px solid var(--border-strong); display: flex; align-items: center; justify-content: center; font-size: 22px; font-weight: 900; }
.generated-code { padding: 10px 12px; border: 1px dashed var(--accent); border-radius: 10px; background: var(--accent-soft); display: flex; flex-direction: column; gap: 4px; }
.generated-code small { font-family: var(--mono); color: var(--accent-dark); font-size: 10px; }
.generated-code strong { font-family: var(--mono); font-size: 16px; }
.grow { flex: 1; } h3, h1 { margin: 0; } h3 { font-size: 16px; } h1 { font-size: 24px; font-weight: 900; }
.code-card { position: sticky; top: 88px; } .join-btn { margin-top: 12px; } .code-card small { display: block; margin-top: 16px; padding-top: 14px; border-top: 1px solid var(--border); color: var(--ink-3); font-size: 11px; line-height: 1.7; }
.guild-header { margin-bottom: 18px; background: linear-gradient(135deg,#fffaf0 0%,#fff 100%); display: flex; align-items: center; gap: 18px; } .guild-header strong { color: var(--accent); } .pill-row, .header-actions { display: flex; gap: 8px; }
.board { display: grid; grid-template-columns: repeat(12,1fr); grid-auto-rows: minmax(110px,auto); gap: 14px; }
.chat-card { grid-column: span 7; grid-row: span 6; display: flex; flex-direction: column; height: 620px; min-height: 620px; overflow: hidden; } .notice,.ranking,.members { grid-column: span 5; grid-row: span 3; } .stats { grid-column: span 7; grid-row: span 3; }
.chat-head { padding: 14px 18px; border-bottom: 1px solid var(--border); display: flex; align-items: center; gap: 10px; background: var(--surface-alt); } .chat-head span { width: 8px; height: 8px; border-radius: 4px; background: var(--ok); }
.feed { flex: 1 1 auto; min-height: 0; max-height: 540px; padding: 14px; overflow-y: auto; overflow-x: hidden; display: flex; flex-direction: column; gap: 6px; }
.chat-input { flex: 0 0 auto; padding: 10px 14px; border-top: 1px solid var(--border); background: var(--surface); } .chat-input label { display: flex; align-items: center; gap: 8px; border: 1.5px solid var(--border-strong); border-radius: 12px; padding: 8px 12px; } .chat-input input { flex: 1; min-width: 0; border: 0; outline: 0; font-size: 14px; }
.chat-submit { border: 0; border-radius: 10px; padding: 6px 12px; display: inline-flex; align-items: center; justify-content: center; gap: 6px; background: var(--accent); color: #fff; box-shadow: 0 1px 0 var(--accent-dark); font-size: 12px; font-weight: 700; cursor: pointer; white-space: nowrap; }
.chat-submit:disabled { opacity: .5; cursor: not-allowed; }
.notice-head { display: flex; align-items: center; justify-content: space-between; gap: 10px; margin-bottom: 10px; }
.notice-box { padding: 12px; border: 1px solid var(--border); border-radius: 10px; margin-top: 8px; } .notice-box small { float: right; font-family: var(--mono); color: var(--ink-3); font-size: 10px; }
.notice-box label { display: flex; flex-direction: column; gap: 6px; margin-top: 8px; font-size: 12px; font-weight: 800; color: var(--ink); }
.notice-box input, .notice-box textarea { width: 100%; border: 1.5px solid var(--border-strong); border-radius: 10px; padding: 10px 12px; background: var(--surface); color: var(--ink); outline: 0; font-size: 13px; }
.notice-box textarea { min-height: 90px; resize: vertical; }
.notice-actions { display: flex; justify-content: flex-end; gap: 6px; margin-top: 10px; }
.member-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 6px; margin-top: 10px; } .member-grid button { display: grid; grid-template-columns: 28px 1fr; column-gap: 8px; padding: 7px 9px; border-radius: 8px; background: var(--surface-alt); border: 1px solid var(--border); text-align: left; cursor: pointer; } .member-grid span { width: 28px; height: 28px; border-radius: 14px; background: var(--yolk); display: flex; align-items: center; justify-content: center; font-weight: 800; } .member-grid strong { font-size: 11px; } .member-grid small, .member-grid em { font-family: var(--mono); font-size: 9px; color: var(--ink-3); font-style: normal; }
.stat-grid { display: grid; grid-template-columns: repeat(4,1fr); gap: 10px; margin: 10px 0 14px; } .guild-chart { display: flex; align-items: flex-end; gap: 8px; height: 90px; } .guild-chart div { flex: 1; align-self: stretch; display: flex; flex-direction: column; align-items: center; justify-content: flex-end; gap: 4px; } .guild-chart b { width: 100%; background: var(--ink); border-radius: 4px 4px 0 0; } .guild-chart b.today { background: var(--accent); } .guild-chart b.zero { background: var(--surface-alt); border: 1px dashed var(--border); } .guild-chart small { font-family: var(--mono); font-size: 10px; color: var(--ink-3); }
.modal-backdrop { position: fixed; inset: 0; z-index: 200; background: rgba(31,28,23,.32); display: flex; align-items: center; justify-content: center; padding: 24px; }
.member-modal, .request-modal { width: min(460px, 100%); max-height: 90vh; overflow: auto; background: var(--surface); border-radius: 18px; box-shadow: var(--shadow-lg); padding: 24px; position: relative; }
.modal-close { position: absolute; top: 12px; right: 12px; width: 30px; height: 30px; border: 0; border-radius: 15px; background: var(--surface-alt); cursor: pointer; color: var(--ink-2); font-size: 18px; }
.member-detail-head { display: flex; align-items: center; gap: 18px; margin-bottom: 16px; }
.member-character { width: 116px; height: 116px; border-radius: 58px; background: radial-gradient(circle at 50% 40%, #fff5e0 0%, #fbe5d3 100%); border: 2px solid var(--border); display: flex; align-items: center; justify-content: center; flex: 0 0 116px; }
.member-modal h2, .request-modal h2 { margin: 0; font-size: 22px; }
.member-modal p, .request-modal > p { margin-bottom: 16px; color: var(--ink-2); }
.modal-info { display: flex; flex-direction: column; gap: 8px; margin-top: 16px; }
.modal-info span { display: flex; justify-content: space-between; padding: 11px 0; border-bottom: 1px solid var(--border); }
.modal-info b { font-family: var(--mono); font-size: 11px; color: var(--ink-3); }
.modal-info strong { font-size: 13px; }
.request-row { display: flex; justify-content: space-between; align-items: center; gap: 12px; padding: 12px; border: 1px solid var(--border); border-radius: 12px; margin-top: 10px; }
.request-row div:first-child { display: flex; flex-direction: column; gap: 4px; }
.request-row span { font-size: 12px; color: var(--ink-2); }
.request-row div:last-child { display: flex; gap: 6px; }
.setting-members { border: 1px solid var(--border); border-radius: 12px; padding: 12px; margin: 12px 0; }
.setting-members > strong { display: block; font-size: 13px; margin-bottom: 8px; }
.setting-members div { display: flex; align-items: center; justify-content: space-between; padding: 8px 0; border-top: 1px dashed var(--border); }
.setting-members div:first-of-type { border-top: 0; }
.setting-members span { font-size: 12px; color: var(--ink-2); }
.request-modal > .app-button + .app-button { margin-top: 8px; }
@media (max-width: 900px) { .pre-grid { grid-template-columns: 1fr; } .code-card { position: static; } .chat-card,.notice,.ranking,.members,.stats { grid-column: 1 / -1; } }
@media (max-width: 560px) { .pre-head,.guild-header { align-items: flex-start; flex-direction: column; } .guild-row { align-items: flex-start; flex-wrap: wrap; } .guild-row .grow { min-width: calc(100% - 70px); } .form-grid,.stat-grid { grid-template-columns: 1fr 1fr; } .member-grid { grid-template-columns: 1fr; } }
</style>
