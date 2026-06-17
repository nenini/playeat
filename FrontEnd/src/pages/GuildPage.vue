<template>
  <section v-if="!joined && joinMode === 'list'">
    <div class="pre-head"><div><div class="title-lg">길드 참여하기</div><p>마음에 드는 길드에 참여하거나, 코드로 직접 입장하세요</p></div><AppButton @click="joinMode = 'create'">+ 길드 생성</AppButton></div>
    <div class="pre-grid">
      <main class="guild-list">
        <div class="section-title"><div><div class="section-title-main">길드 목록</div><div class="section-title-sub">총 {{ guildList.length }}개 길드</div></div></div>
        <div v-for="guild in visibleGuilds" :key="guild.id" class="guild-row">
          <div class="guild-logo">{{ guild.emoji }}</div>
          <div class="grow"><h3>{{ guild.name }} <AppPill size="sm">#{{ guild.rank }}위</AppPill></h3><p>👥 {{ guild.members }}/{{ guild.max }}명 · 🏆 {{ guild.score.toLocaleString() }} pt · 🎯 {{ guild.focus }}</p></div>
          <AppButton v-if="requestedGuildId === guild.id" size="sm" variant="secondary" @click="cancelJoinRequest">참여 요청 취소</AppButton>
          <AppButton v-else size="sm" @click="requestJoinGuild(guild.id)">참여하기</AppButton>
        </div>
        <div v-if="!requestedGuildId && totalPages > 1" class="pagination">
          <button v-for="page in pages" :key="page" type="button" :class="{ active: currentPage === page }" @click="currentPage = page">{{ page }}</button>
        </div>
      </main>
      <AppCard class="code-card"><div class="section-title-main">코드로 참여</div><p>길드 코드를 알고 있다면 직접 입력해 바로 참여할 수 있어요.</p><div class="mono-label">길드 코드</div><label class="app-input"><input v-model="code" placeholder="예: NYAM-2840"></label><AppButton full class="join-btn" @click="joined = true">코드로 참여하기</AppButton><small>💡 길드 코드는 길드장에게 받을 수 있어요.<br>영문 + 숫자 4~12자리 형식입니다.</small></AppCard>
    </div>
  </section>
  <section v-else-if="!joined && joinMode === 'create'">
    <div class="pre-head"><div><div class="title-lg">길드 생성</div><p>새 길드를 만들고 길드원을 초대해 함께 건강 목표를 달성하세요.</p></div><AppButton variant="secondary" @click="joinMode = 'list'">목록으로</AppButton></div>
    <AppCard class="create-card">
      <div class="form-grid">
        <label>길드 이름<input v-model="createForm.name" placeholder="예: 잘먹잘싸"></label>
        <label>최대 인원<input v-model="createForm.max" type="number" min="2" max="30"></label>
        <div class="generated-code"><small>자동 생성 길드 코드</small><strong>{{ generatedCode }}</strong></div>
      </div>
      <label class="wide">길드 소개<textarea v-model="createForm.description" placeholder="우리 길드의 식단 목표를 적어주세요."></textarea></label>
      <div class="create-actions"><AppButton variant="secondary" @click="joinMode = 'list'">취소</AppButton><AppButton @click="joined = true">길드 생성하기</AppButton></div>
    </AppCard>
  </section>
  <section v-else>
    <div v-if="selectedMember" class="modal-backdrop" @click="selectedMember = null">
      <div class="member-modal" @click.stop>
        <button class="modal-close" @click="selectedMember = null">×</button>
        <div class="member-detail-head">
          <div class="member-character"><NyamnyamCharacter :stage="selectedMember.lv < 5 ? 'egg' : selectedMember.lv < 15 ? 'chick' : 'adult'" :size="96" /></div>
          <div><h2>{{ selectedMember.name }}</h2><p>LV.{{ selectedMember.lv }} · {{ selectedMember.role }}</p></div>
        </div>
        <div class="modal-info">
          <span><b>이번 주 기록률</b><strong>{{ selectedMember.id === 'me' ? '92%' : '86%' }}</strong></span>
          <span><b>보스 기여도</b><strong>{{ selectedMember.id === 'jiyoung' ? '24 HP' : '18 HP' }}</strong></span>
        </div>
      </div>
    </div>
    <div v-if="showRequests" class="modal-backdrop" @click="showRequests = false">
      <div class="request-modal" @click.stop>
        <button class="modal-close" @click="showRequests = false">×</button>
        <h2>참여 요청 관리</h2>
        <p>길드장만 승인·거절할 수 있어요.</p>
        <div class="request-row" v-for="request in joinRequests" :key="request.name"><div><strong>{{ request.name }}</strong><span>LV.{{ request.lv }} · {{ request.msg }}</span></div><div><AppButton size="sm">승인</AppButton><AppButton size="sm" variant="danger">거절</AppButton></div></div>
      </div>
    </div>
    <div v-if="showSettings" class="modal-backdrop" @click="showSettings = false">
      <div class="request-modal" @click.stop>
        <button class="modal-close" @click="showSettings = false">×</button>
        <h2>길드 설정</h2>
        <p>길드장 전용 설정 화면입니다.</p>
        <label>길드 이름<input value="잘먹잘싸"></label>
        <label>길드 소개<textarea>이번 주 보스 격파 가즈아 🐲</textarea></label>
        <div class="setting-members">
          <strong>길드원 관리</strong>
          <div v-for="member in guildMembers.filter(member => member.id !== 'me')" :key="member.id"><span>{{ member.name }} · LV.{{ member.lv }}</span><AppButton size="sm" variant="danger">추방</AppButton></div>
        </div>
        <AppButton full variant="danger">길드 삭제</AppButton>
        <AppButton full @click="showSettings = false">저장</AppButton>
      </div>
    </div>
    <AppCard :padding="20" class="guild-header">
      <div class="grow"><div class="pill-row"><AppPill tone="accent" size="sm">길드 코드 NYAM-2840</AppPill><AppPill tone="ok" size="sm">전체 1위</AppPill><AppPill :tone="isLeader ? 'dark' : 'neutral'" size="sm">{{ isLeader ? '길드장' : '길드원' }}</AppPill></div><h1>잘먹잘싸</h1><p>6명 · 주간 점수 <strong>2,840 pt</strong> · 이번 주 기록률 92%</p></div><div class="header-actions"><AppButton v-if="isLeader" variant="secondary" size="sm" @click="showRequests = true">참여 요청 <AppPill tone="bad" size="sm">{{ joinRequests.length }}</AppPill></AppButton><AppButton v-if="isLeader" variant="secondary" size="sm" @click="showSettings = true">길드 설정</AppButton></div>
    </AppCard>
    <div class="board">
      <AppCard :padding="0" class="chat-card"><div class="chat-head"><span></span><strong>길드 채팅</strong></div><div class="feed"><ChatBubble v-for="(msg, i) in allChat" :key="i" :msg="msg" /></div><div class="chat-input"><label><AppIcon name="plus" color="var(--ink-3)" /><input v-model="message" placeholder="메시지 보내기…" @keydown.enter="send"><AppPill size="sm">😀</AppPill><AppPill size="sm">🍱</AppPill><AppButton size="sm" :disabled="!message.trim()" @click="send"><AppIcon name="send" color="#fff" />보내기</AppButton></label></div></AppCard>
      <AppCard class="notice">
        <div class="notice-head"><div class="section-title-main">📢 공지사항</div><AppButton v-if="isLeader" size="sm" variant="secondary" @click="startAddNotice">공지 추가</AppButton></div>
        <div v-if="editingNoticeId === 'new'" class="notice-box">
          <label>제목<input v-model="noticeDraft.title" placeholder="공지 제목"></label>
          <label>내용<textarea v-model="noticeDraft.body" placeholder="길드원에게 알릴 내용을 입력하세요."></textarea></label>
          <div class="notice-actions"><AppButton size="sm" variant="ghost" @click="cancelNoticeEdit">취소</AppButton><AppButton size="sm" @click="saveNotice">저장</AppButton></div>
        </div>
        <div v-for="notice in notices" :key="notice.id" class="notice-box">
          <template v-if="editingNoticeId === notice.id">
            <label>제목<input v-model="noticeDraft.title"></label>
            <label>내용<textarea v-model="noticeDraft.body"></textarea></label>
            <div class="notice-actions"><AppButton size="sm" variant="ghost" @click="cancelNoticeEdit">취소</AppButton><AppButton size="sm" @click="saveNotice">저장</AppButton></div>
          </template>
          <template v-else>
            <strong>{{ notice.title }}</strong><small>{{ notice.when }}</small><p>{{ notice.body }}</p>
            <div v-if="isLeader" class="notice-actions"><AppButton size="sm" variant="secondary" @click="startEditNotice(notice)">수정</AppButton><AppButton size="sm" variant="danger" @click="deleteNotice(notice.id)">삭제</AppButton></div>
          </template>
        </div>
      </AppCard>
      <AppCard class="ranking"><div class="section-title"><div><div class="section-title-main">🏆 길드 순위</div><div class="section-title-sub">이번 주 길드 순위</div></div><AppPill tone="ok" size="sm">우리 1위 🏆</AppPill></div><RankRow v-for="rank in rankings" :key="String(rank[0])" :row="rank" /></AppCard>
      <AppCard class="members"><div class="section-title-main">👥 길드원</div><div class="member-grid"><button v-for="member in guildMembers" :key="member.id" :class="{ offline: !member.online }" @click="selectedMember = member"><span>{{ member.name[0] }}</span><strong>{{ member.name }} <small>LV.{{ member.lv }}</small></strong><em>{{ member.role }}</em></button></div></AppCard>
      <AppCard class="stats"><div class="section-title-main">📊 길드 통계</div><div class="stat-grid"><StatBlock label="기록률" value="92%" /><StatBlock label="보스 데미지" value="−108 HP" /><StatBlock label="주간 점수" value="2,840 pt" accent /><StatBlock label="퀘스트 완료" value="17/24" /></div><div class="guild-chart"><div v-for="(v, i) in [68,72,80,75,88,92,0]" :key="i"><b :style="{ height: v ? `${v}%` : '6px' }" :class="{ zero: v === 0, today: i === 5 }"></b><small>{{ ['월','화','수','목','금','토','일'][i] }}</small></div></div></AppCard>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import AppButton from '../components/common/AppButton.vue'
import AppCard from '../components/common/AppCard.vue'
import AppIcon from '../components/common/AppIcon.vue'
import AppPill from '../components/common/AppPill.vue'
import ChatBubble from './parts/ChatBubble.vue'
import RankRow from './parts/RankRow.vue'
import StatBlock from './parts/StatBlock.vue'
import NyamnyamCharacter from '../components/nyamnyam/NyamnyamCharacter.vue'
import { guildList as mockGuildList, guildMembers, seedChat } from '../services/mock/nyamnyamMock'
import { guildApi } from '../services/nyamnyamApi'

defineProps<{ isLeader?: boolean }>()

const joined = ref(new URLSearchParams(window.location.search).get('guild') !== 'none')
const joinMode = ref<'list' | 'create'>('list')
const requestedGuildId = ref<number | null>(null)
const apiGuildList = ref<typeof mockGuildList>([])
const requestIds = ref<Record<number, number>>({})
const currentPage = ref(1)
const pageSize = 10
const code = ref('')
const message = ref('')
const chat = ref<any[]>([])
const selectedMember = ref<typeof guildMembers[number] | null>(null)
const showRequests = ref(false)
const showSettings = ref(false)
const createForm = reactive({ name: '', max: 30, description: '' })
const generatedCode = 'NYAM-' + Math.floor(1000 + Math.random() * 9000)
const notices = ref([{ id: 1, title: '이번 주 보스 격파 가즈아 🐲', when: '어제', body: '당분 드래곤 D-3 남았습니다. 일요일까지 다 같이 밀어봅시다!' }])
const editingNoticeId = ref<number | 'new' | null>(null)
const noticeDraft = reactive({ title: '', body: '' })
const allChat = computed(() => [...seedChat, ...chat.value])
const guildList = computed(() => apiGuildList.value.length ? apiGuildList.value : mockGuildList)
const visibleGuilds = computed(() => {
  if (requestedGuildId.value) return guildList.value.filter((guild) => guild.id === requestedGuildId.value)
  const start = (currentPage.value - 1) * pageSize
  return guildList.value.slice(start, start + pageSize)
})
const totalPages = computed(() => Math.ceil(guildList.value.length / pageSize))
const pages = computed(() => Array.from({ length: totalPages.value }, (_, index) => index + 1))
const rankings: Array<[string, string, string, number, boolean]> = [['1', '잘먹잘싸', '우리 길드', 2840, true], ['2', '단백질 부대', '', 2710, false], ['3', '아침 챔피언즈', '', 2620, false], ['4', '채소 사랑', '', 2400, false], ['5', '저염 라이프', '', 2300, false]]
const joinRequests = [{ name: '김건강', lv: 3, msg: '건강 식단에 관심 많아요!' }, { name: '박야채', lv: 6, msg: '채소 챌린지 중이에요.' }]
async function requestJoinGuild(guildId: number) {
  requestedGuildId.value = guildId
  try {
    const response = await guildApi.requestJoin(guildId) as { requestId?: number }
    if (response.requestId) requestIds.value[guildId] = response.requestId
  } catch (error) {
    console.warn('Guild join request API failed', error)
  }
}
async function cancelJoinRequest() {
  const guildId = requestedGuildId.value
  requestedGuildId.value = null
  currentPage.value = 1
  if (!guildId || !requestIds.value[guildId]) return
  try {
    await guildApi.cancelJoinRequest(guildId, requestIds.value[guildId])
    delete requestIds.value[guildId]
  } catch (error) {
    console.warn('Guild join cancel API failed', error)
  }
}
function send() { if (!message.value.trim()) return; chat.value.push({ id: 'me', name: '지은', time: '방금', text: message.value, mine: true }); message.value = '' }
function startAddNotice() { editingNoticeId.value = 'new'; noticeDraft.title = ''; noticeDraft.body = '' }
function startEditNotice(notice: typeof notices.value[number]) { editingNoticeId.value = notice.id; noticeDraft.title = notice.title; noticeDraft.body = notice.body }
function cancelNoticeEdit() { editingNoticeId.value = null; noticeDraft.title = ''; noticeDraft.body = '' }
function saveNotice() {
  if (!noticeDraft.title.trim() || !noticeDraft.body.trim()) return
  if (editingNoticeId.value === 'new') notices.value.unshift({ id: Date.now(), title: noticeDraft.title, body: noticeDraft.body, when: '방금' })
  else {
    const notice = notices.value.find((item) => item.id === editingNoticeId.value)
    if (notice) { notice.title = noticeDraft.title; notice.body = noticeDraft.body; notice.when = '수정됨' }
  }
  cancelNoticeEdit()
}
function deleteNotice(id: number) { notices.value = notices.value.filter((notice) => notice.id !== id) }
async function loadGuilds() {
  try {
    const response = await guildApi.list(0, 50)
    apiGuildList.value = response.guilds.map((guild, index) => ({
      id: guild.guildId,
      name: guild.name,
      members: guild.memberCount,
      max: guild.maxMembers,
      score: guild.guildPoint,
      rank: index + 1,
      focus: guild.description || guild.ownerNickname || '',
      emoji: '길'
    }))
    requestIds.value = response.guilds.reduce((acc, guild) => {
      if (guild.joinRequestId) acc[guild.guildId] = guild.joinRequestId
      return acc
    }, {} as Record<number, number>)
  } catch (error) {
    console.warn('Guild list API failed', error)
  }
}
onMounted(() => { void loadGuilds() })
</script>

<style scoped>
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
.guild-logo { width: 50px; height: 50px; border-radius: 12px; background: var(--yolk); border: 1.5px solid var(--border-strong); display: flex; align-items: center; justify-content: center; font-size: 22px; font-weight: 900; } .guild-logo.big { width: 64px; height: 64px; font-size: 28px; }
.generated-code { padding: 10px 12px; border: 1px dashed var(--accent); border-radius: 10px; background: var(--accent-soft); display: flex; flex-direction: column; gap: 4px; }
.generated-code small { font-family: var(--mono); color: var(--accent-dark); font-size: 10px; }
.generated-code strong { font-family: var(--mono); font-size: 16px; }
.grow { flex: 1; } h3, h1 { margin: 0; } h3 { font-size: 16px; } h1 { font-size: 24px; font-weight: 900; }
.code-card { position: sticky; top: 88px; } .join-btn { margin-top: 12px; } .code-card small { display: block; margin-top: 16px; padding-top: 14px; border-top: 1px solid var(--border); color: var(--ink-3); font-size: 11px; line-height: 1.7; }
.guild-header { margin-bottom: 18px; background: linear-gradient(135deg,#fffaf0 0%,#fff 100%); display: flex; align-items: center; gap: 18px; } .guild-header strong { color: var(--accent); } .pill-row, .header-actions { display: flex; gap: 8px; }
.board { display: grid; grid-template-columns: repeat(12,1fr); grid-auto-rows: minmax(110px,auto); gap: 14px; }
.chat-card { grid-column: span 7; grid-row: span 6; display: flex; flex-direction: column; min-height: 620px; } .notice,.ranking,.members { grid-column: span 5; grid-row: span 3; } .stats { grid-column: span 7; grid-row: span 3; }
.chat-head { padding: 14px 18px; border-bottom: 1px solid var(--border); display: flex; align-items: center; gap: 10px; background: var(--surface-alt); } .chat-head span { width: 8px; height: 8px; border-radius: 4px; background: var(--ok); } .chat-head small { font-family: var(--mono); color: var(--ink-3); }
.feed { flex: 1; padding: 14px; overflow: auto; display: flex; flex-direction: column; gap: 6px; }
.chat-input { padding: 10px 14px; border-top: 1px solid var(--border); } .chat-input label { display: flex; align-items: center; gap: 8px; border: 1.5px solid var(--border-strong); border-radius: 12px; padding: 8px 12px; } .chat-input input { flex: 1; border: 0; outline: 0; font-size: 14px; }
.notice-head { display: flex; align-items: center; justify-content: space-between; gap: 10px; margin-bottom: 10px; }
.notice-box { padding: 12px; border: 1px solid var(--border); border-radius: 10px; margin-top: 8px; } .notice-box small { float: right; font-family: var(--mono); color: var(--ink-3); font-size: 10px; }
.notice-box label { display: flex; flex-direction: column; gap: 6px; margin-top: 8px; font-size: 12px; font-weight: 800; color: var(--ink); }
.notice-box input, .notice-box textarea { width: 100%; border: 1.5px solid var(--border-strong); border-radius: 10px; padding: 10px 12px; background: var(--surface); color: var(--ink); outline: 0; font-size: 13px; }
.notice-box textarea { min-height: 90px; resize: vertical; }
.notice-actions { display: flex; justify-content: flex-end; gap: 6px; margin-top: 10px; }
.locked-notice { background: var(--surface-alt); color: var(--ink-3); }
.member-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 6px; margin-top: 10px; } .member-grid button { display: grid; grid-template-columns: 28px 1fr; column-gap: 8px; padding: 7px 9px; border-radius: 8px; background: var(--surface-alt); border: 1px solid var(--border); text-align: left; cursor: pointer; } .member-grid button.offline { opacity: .55; } .member-grid span { width: 28px; height: 28px; border-radius: 14px; background: var(--yolk); display: flex; align-items: center; justify-content: center; font-weight: 800; } .member-grid strong { font-size: 11px; } .member-grid small, .member-grid em { font-family: var(--mono); font-size: 9px; color: var(--ink-3); font-style: normal; }
.stat-grid { display: grid; grid-template-columns: repeat(4,1fr); gap: 10px; margin: 10px 0 14px; } .guild-chart { display: flex; align-items: flex-end; gap: 8px; height: 90px; } .guild-chart div { flex: 1; align-self: stretch; display: flex; flex-direction: column; align-items: center; justify-content: flex-end; gap: 4px; } .guild-chart b { width: 100%; background: var(--ink); border-radius: 4px 4px 0 0; } .guild-chart b.today { background: var(--accent); } .guild-chart b.zero { background: var(--surface-alt); border: 1px dashed var(--border); } .guild-chart small { font-family: var(--mono); font-size: 10px; color: var(--ink-3); }
.modal-backdrop { position: fixed; inset: 0; z-index: 200; background: rgba(31,28,23,.32); display: flex; align-items: center; justify-content: center; padding: 24px; }
.member-modal, .request-modal { width: min(460px, 100%); background: var(--surface); border-radius: 18px; box-shadow: var(--shadow-lg); padding: 24px; position: relative; }
.modal-close { position: absolute; top: 12px; right: 12px; width: 30px; height: 30px; border: 0; border-radius: 15px; background: var(--surface-alt); cursor: pointer; color: var(--ink-2); font-size: 18px; }
.member-detail-head { display: flex; align-items: center; gap: 18px; margin-bottom: 16px; }
.member-character { width: 116px; height: 116px; border-radius: 58px; background: radial-gradient(circle at 50% 40%, #fff5e0 0%, #fbe5d3 100%); border: 2px solid var(--border); display: flex; align-items: center; justify-content: center; flex: 0 0 116px; }
.member-modal h2, .request-modal h2 { margin: 0; font-size: 22px; }
.member-modal p, .request-modal > p { margin-bottom: 16px; color: var(--ink-2); }
.modal-info { display: flex; flex-direction: column; gap: 8px; margin-top: 16px; }
.modal-info span { display: flex; justify-content: space-between; padding: 11px 0; border-bottom: 1px solid var(--border); }
.modal-info b { font-family: var(--mono); font-size: 11px; color: var(--ink-3); }
.modal-info strong { font-size: 13px; }
.modal-info .online { color: var(--ok); }
.request-row { display: flex; justify-content: space-between; align-items: center; gap: 12px; padding: 12px; border: 1px solid var(--border); border-radius: 12px; margin-top: 10px; }
.request-row div:first-child { display: flex; flex-direction: column; gap: 4px; }
.request-row span { font-size: 12px; color: var(--ink-2); }
.request-row div:last-child { display: flex; gap: 6px; }
.setting-members { border: 1px solid var(--border); border-radius: 12px; padding: 12px; margin: 12px 0; }
.setting-members > strong { display: block; font-size: 13px; margin-bottom: 8px; }
.setting-members div { display: flex; align-items: center; justify-content: space-between; padding: 8px 0; border-top: 1px dashed var(--border); }
.setting-members div:first-of-type { border-top: 0; }
.setting-members span { font-size: 12px; color: var(--ink-2); }
</style>
