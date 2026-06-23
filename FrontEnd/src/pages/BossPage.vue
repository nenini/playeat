<template>
  <section v-if="isLoading" class="state-screen"><AppCard>보스전 정보를 불러오는 중...</AppCard></section>

  <section v-else-if="screen === 'no-guild'" class="state-screen">
    <div v-if="errorMessage" class="api-message">{{ errorMessage }}</div>
    <AppCard><div class="title-xl">길드 보스전</div><p>길드에 가입해야 보스전에 참여할 수 있습니다.</p></AppCard>
  </section>

  <section v-else-if="screen === 'lobby'">
    <div v-if="errorMessage" class="api-message">{{ errorMessage }}</div>
    <div class="page-title-row">
      <div><div class="pill-row"><AppPill tone="bad" size="sm">BOSS LOBBY</AppPill><AppPill size="sm">{{ seasonName || '현재 시즌' }}</AppPill><AppPill size="sm">{{ guildName }}</AppPill></div><div class="title-xl">{{ selectedBoss?.name || '현재 보스' }} <span>입장방</span></div><p>보스를 선택하고 길드원과 함께 전투를 시작하세요</p></div>
      <div class="start-box"><AppButton size="lg" :disabled="!isLeader || !selectedBoss || creatingBattle" @click="startBattle">⚔️ {{ creationStepMessage || '전투 시작!' }}</AppButton><div v-if="!isLeader" class="leader-help">길드장만 전투를 시작할 수 있어요</div><div v-else-if="creationStepMessage" class="creation-step">{{ creationStepMessage }}</div></div>
    </div>
    <div class="boss-lobby">
      <div class="boss-art"><BossMonster :size="220" :hp="100" /><strong>{{ selectedBoss?.name || '보스 데이터 없음' }}</strong><p>{{ selectedBoss?.description || '현재 시즌에 등록된 보스가 없습니다.' }}</p></div>
      <div><div class="mono-label diff-label">보스 선택</div><div v-if="bosses.length" class="diff-grid"><button v-for="item in bosses" :key="item.bossId" :class="[item.difficulty.toLowerCase(), { active: selectedBossId === item.bossId }]" @click="selectedBossId = item.bossId"><strong>{{ difficultyLabel(item.difficulty) }}</strong><p>{{ item.name }}</p><AppPill :tone="difficultyTone(item.difficulty)" size="sm">{{ item.rewardExp.toLocaleString() }} XP</AppPill></button></div><div v-else class="empty-state">현재 시즌 보스가 없습니다.</div></div>
      <AppCard v-if="selectedBoss" :padding="14" class="diff-summary"><div><div><small>BOSS HP</small><strong>{{ selectedBoss.maxHp.toLocaleString() }}</strong></div><div><small>보상 코인</small><strong>{{ selectedBoss.rewardCoin.toLocaleString() }}</strong></div><div><small>격파 XP</small><strong>{{ selectedBoss.rewardExp.toLocaleString() }}</strong></div></div></AppCard>
    </div>
  </section>

  <section v-else>
    <div v-if="errorMessage" class="api-message">{{ errorMessage }}</div>
    <div v-if="successMessage" class="api-message success">{{ successMessage }}</div>
    <div class="page-title-row">
      <div><div class="pill-row"><AppPill tone="bad" size="sm">GUILD BOSS · {{ battle?.difficulty || '-' }}</AppPill><AppPill size="sm">{{ statusLabel(battle?.status) }}</AppPill><AppPill size="sm">{{ battle?.guildName || guildName }}</AppPill></div><div class="title-xl">{{ battle?.bossName || '보스전' }}</div><p>시작 {{ formatDate(battle?.startedAt) }} · 종료 {{ formatDate(battle?.endsAt) }} · 길드원 {{ battle?.participantCount ?? '-' }}명 참전</p></div>
      <div class="battle-actions"><AppButton @click="$emit('navigate', 'meals')"><AppIcon name="plus" color="#fff" />식단 기록 · 데미지!</AppButton></div>
    </div>
    <div class="battle-grid">
      <main class="battle-left">
        <div v-if="isBattleCleared" class="clear-banner">
          <div class="clear-badge">BOSS<br>CLEAR</div>
          <div>
            <h2>보스 격파 성공!</h2>
            <p>길드원들과 함께 보스를 쓰러뜨렸어요. 보상을 수령해 주세요.</p>
          </div>
        </div>
        <div class="arena"><div class="grid-bg"></div><BossMonster :size="300" :hp="hpPercent" /></div>
        <AppCard :padding="16"><div class="hp-row"><span>BOSS HP</span><ProgressBar :value="hp?.currentHp ?? 0" :max="hp?.maxHp || 1" :tone="hpPercent < 30 ? 'accent' : 'bad'" :height="18" /><b>{{ hp?.currentHp?.toLocaleString() ?? '-' }} / {{ hp?.maxHp?.toLocaleString() ?? '-' }}</b></div><div class="damage-row"><span>누적 데미지: <strong>−{{ hp?.totalDamage?.toLocaleString() ?? '-' }}</strong> HP</span><span>퀘스트 완료: <strong>{{ dashboard ? `${dashboard.questCompletedCount}/${dashboard.questTotalCount}` : '-' }}</strong></span></div></AppCard>
        <nav class="game-tabs battle-tabs" aria-label="보스전 메뉴"><button type="button" :class="{ active: battleTab === 'quests' }" @click="battleTab = 'quests'">📜 개인 퀘스트</button><button type="button" :class="{ active: battleTab === 'conditions' }" @click="battleTab = 'conditions'">⚔️ 격파 조건</button><button type="button" :class="{ active: battleTab === 'rewards' }" @click="battleTab = 'rewards'">🎁 레이드 보상</button></nav>
        <div class="condition-grid">
          <AppCard v-if="battleTab === 'conditions'" :padding="20"><div class="condition-head"><div class="section-title-main">길드 공통 격파 조건</div><AppPill :tone="isBattleCleared ? 'ok' : 'accent'" size="sm">{{ isBattleCleared ? '완료' : '진행 중' }}</AppPill></div><div v-if="battle?.commonConditions?.length" class="checks"><p v-for="condition in battle.commonConditions" :key="condition.battleConditionId" :class="{ completed: condition.completed }"><b>{{ condition.completed ? '✓' : '□' }}</b><span>{{ condition.title }}</span><strong>{{ condition.currentValue ?? '-' }}/{{ condition.targetValue ?? '-' }} {{ condition.unit || '' }}</strong></p></div><p v-else>격파 조건 데이터가 없습니다.</p></AppCard>
          <AppCard v-if="battleTab === 'rewards'" :padding="22" class="reward"><div class="section-title-main">레이드 클리어 보상</div><div class="reward-loot"><span>✨<small>경험치</small><strong>+{{ bossDetail?.rewardExp?.toLocaleString() ?? '-' }} XP</strong></span><span>🪙<small>코인</small><strong>+{{ bossDetail?.rewardCoin?.toLocaleString() ?? '-' }}</strong></span></div><AppPill v-if="isBattleCleared && battle?.rewardClaimed" tone="ok" size="sm">보상 수령 완료</AppPill><AppButton v-else-if="isBattleCleared" full size="lg" :disabled="pendingActionId === 'battle-reward'" @click="claimBattleReward">보스 보상 받기</AppButton><p v-else class="reward-lock">보스를 격파하면 보상 상자가 열립니다.</p></AppCard>
        </div>
      </main>
      <aside v-if="battleTab === 'quests'" class="members">
        <div class="member-head"><strong>길드원 개인 퀘스트 · {{ displayQuests.length }}명</strong><span>{{ dashboard ? `완료 ${dashboard.questCompletedCount} · 전체 ${dashboard.questTotalCount}` : '-' }}</span></div>
        <div v-if="displayQuests.length === 0" class="quest-empty"><p>생성된 퀘스트가 없습니다.</p><AppButton v-if="isLeader" size="sm" :disabled="pendingActionId === 'generate-quests'" @click="generateQuests">퀘스트 생성</AppButton></div>
        <div v-for="quest in displayQuests" :key="quest.questId" class="quest-entry">
          <MemberQuest :member="questMember(quest)" />
          <div v-if="quest.isMe" class="quest-actions"><AppButton v-if="quest.status === 'COMPLETED'" size="sm" :disabled="pendingActionId === `reward-${quest.questId}`" @click="claimQuestReward(quest.questId)">보상 수령</AppButton><AppPill v-if="quest.status === 'REWARDED'" tone="ok" size="sm">보상 수령 완료</AppPill></div>
        </div>
        <AppCard :padding="12" class="tip">💡 길드원이 자기 퀘스트를 깰 때마다 보스 HP가 감소해요.</AppCard>
      </aside>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import AppButton from '../components/common/AppButton.vue'
import AppCard from '../components/common/AppCard.vue'
import AppIcon from '../components/common/AppIcon.vue'
import AppPill from '../components/common/AppPill.vue'
import ProgressBar from '../components/common/ProgressBar.vue'
import BossMonster from '../components/nyamnyam/BossMonster.vue'
import { bossApi } from '../services/api/bossApi'
import { bossBattleApi } from '../services/api/bossBattleApi'
import { ApiError } from '../services/api/client'
import { guildApi } from '../services/api/guildApi'
import { questApi } from '../services/api/questApi'
import type { BossDetail, BossDifficulty, BossSummary } from '../types/boss'
import type { BossBattleDashboard, BossBattleDetail, BossBattleHp } from '../types/bossBattle'
import type { QuestContribution, QuestDetail, QuestSummary } from '../types/quest'
import MemberQuest from './parts/MemberQuest.vue'

type PageId = 'home' | 'meals' | 'analyze' | 'boss' | 'guild' | 'shop' | 'mypage'

defineEmits<{ navigate: [page: PageId] }>()

const screen = ref<'no-guild' | 'lobby' | 'battle'>('no-guild')
const isLoading = ref(true)
const errorMessage = ref('')
const successMessage = ref('')
const pendingActionId = ref<string | null>(null)
const creatingBattle = ref(false)
const creationStepMessage = ref('')
const guildId = ref<number | null>(null)
const guildName = ref('내 길드')
const guildRole = ref<'OWNER' | 'MEMBER' | null>(null)
const seasonName = ref('')
const bosses = ref<BossSummary[]>([])
const selectedBossId = ref<number | null>(null)
const battleId = ref<number | null>(null)
const battle = ref<BossBattleDetail | null>(null)
const bossDetail = ref<BossDetail | null>(null)
const hp = ref<BossBattleHp | null>(null)
const dashboard = ref<BossBattleDashboard | null>(null)
const quests = ref<QuestSummary[]>([])
const myQuest = ref<QuestDetail | null>(null)
const contributions = ref<QuestContribution[]>([])
const battleTab = ref<'quests' | 'conditions' | 'rewards'>('quests')

const isLeader = computed(() => guildRole.value === 'OWNER')
const selectedBoss = computed(() => bosses.value.find((item) => item.bossId === selectedBossId.value) ?? bosses.value[0] ?? null)
const hpPercent = computed(() => hp.value?.maxHp ? hp.value.currentHp / hp.value.maxHp * 100 : 0)
const isBattleCleared = computed(() => ['DEFEATED', 'CLEARED', 'COMPLETED'].includes(String(battle.value?.status || '')))
const displayQuests = computed<QuestSummary[]>(() => {
  if (!myQuest.value) return quests.value

  const myQuestIndex = quests.value.findIndex((quest) => quest.questId === myQuest.value?.questId)
  const mine: QuestSummary = { ...myQuest.value, isMe: true }
  if (myQuestIndex < 0) return [mine, ...quests.value]

  return quests.value.map((quest, index) => index === myQuestIndex ? mine : quest)
})

function setError(error: unknown) {
  errorMessage.value = error instanceof ApiError ? error.message : '요청을 처리하지 못했습니다. 잠시 후 다시 시도해주세요.'
}

function clearFeedback() {
  errorMessage.value = ''
  successMessage.value = ''
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

async function loadBossPage() {
  isLoading.value = true
  clearFeedback()
  try {
    const guildStatus = await guildApi.getMyGuildStatus()
    const statusData = guildStatus as typeof guildStatus & {
      guildId?: number
      guildName?: string
      role?: 'OWNER' | 'MEMBER'
      memberRole?: 'OWNER' | 'MEMBER'
    }
    const currentGuildId = guildStatus.guild?.guildId ?? statusData.guildId ?? null
    if (!currentGuildId) {
      screen.value = 'no-guild'
      return
    }
    guildId.value = currentGuildId
    guildName.value = guildStatus.guild?.name ?? statusData.guildName ?? '내 길드'
    guildRole.value = guildStatus.guild?.role ?? statusData.memberRole ?? statusData.role ?? null
    screen.value = 'lobby'
    try {
      const current = await bossBattleApi.getCurrentGuildBossBattle(currentGuildId)
      if (current.battle) {
        battleId.value = current.battle.battleId
        screen.value = 'battle'
        await loadBattleData(current.battle.battleId)
      } else {
        await loadBossLobby()
      }
    } catch (error) {
      if (error instanceof ApiError && error.status === 404) {
        await loadBossLobby()
      } else {
        throw error
      }
    }
  } catch (error) {
    setError(error)
  } finally {
    isLoading.value = false
  }
}

async function loadBossLobby() {
  const response = await bossApi.getCurrentBosses()
  seasonName.value = response.seasonName
  bosses.value = response.bosses ?? []
  selectedBossId.value = bosses.value[0]?.bossId ?? null
}

async function loadBattleData(id: number) {
  const detail = await bossBattleApi.getBossBattle(id)
  const [hpData, dashboardData, myQuestData, questList, contributionList, bossData] = await Promise.all([
    bossBattleApi.getBossBattleHp(id),
    bossBattleApi.getBossBattleDashboard(id),
    questApi.getMyBattleQuests(id),
    questApi.getBattleQuests(id),
    questApi.getBattleQuestContributions(id),
    bossApi.getBoss(detail.bossId)
  ])
  battle.value = detail
  hp.value = hpData
  dashboard.value = dashboardData
  myQuest.value = myQuestData
  quests.value = questList.quests ?? []
  contributions.value = contributionList.contributions ?? []
  bossDetail.value = bossData
}

async function reloadQuestProgress() {
  if (!battleId.value) return
  const [myQuestData, questList, contributionList, hpData, dashboardData] = await Promise.all([
    questApi.getMyBattleQuests(battleId.value),
    questApi.getBattleQuests(battleId.value),
    questApi.getBattleQuestContributions(battleId.value),
    bossBattleApi.getBossBattleHp(battleId.value),
    bossBattleApi.getBossBattleDashboard(battleId.value)
  ])
  myQuest.value = myQuestData
  quests.value = questList.quests ?? []
  contributions.value = contributionList.contributions ?? []
  hp.value = hpData
  dashboard.value = dashboardData
}

async function startBattle() {
  if (!isLeader.value || !guildId.value || !selectedBoss.value || creatingBattle.value) return
  clearFeedback()
  creatingBattle.value = true
  try {
    creationStepMessage.value = '보스전 생성 중...'
    const created = await bossBattleApi.createBossBattle(guildId.value, { bossId: selectedBoss.value.bossId })
    const createdBattleId = extractBattleId(created)
    if (!createdBattleId) throw new Error('생성된 보스전 ID를 확인할 수 없습니다.')

    creationStepMessage.value = '퀘스트 생성 중...'
    await questApi.generateBattleQuests(createdBattleId)

    creationStepMessage.value = '전투 정보를 불러오는 중...'
    const current = await bossBattleApi.getCurrentGuildBossBattle(guildId.value)
    const currentBattleId = current.battle?.battleId
    if (!currentBattleId) throw new Error('현재 보스전 정보를 확인할 수 없습니다.')
    await loadBattleData(currentBattleId)
    battleId.value = currentBattleId
    screen.value = 'battle'
    successMessage.value = '보스전과 길드원 퀘스트를 생성했습니다.'
  } catch (error) {
    setError(error)
  } finally {
    creatingBattle.value = false
    creationStepMessage.value = ''
  }
}

function extractBattleId(created: unknown): number | null {
  if (!created || typeof created !== 'object') return null
  const data = created as {
    battleId?: unknown
    bossBattleId?: unknown
    id?: unknown
    battle?: { id?: unknown; battleId?: unknown }
  }
  const value = data.battleId ?? data.bossBattleId ?? data.id ?? data.battle?.battleId ?? data.battle?.id
  return typeof value === 'number' && Number.isFinite(value) ? value : null
}

async function generateQuests() {
  if (!battleId.value) return
  await runAction('generate-quests', async () => {
    await questApi.generateBattleQuests(battleId.value!)
    await reloadQuestProgress()
  }, '길드원 퀘스트를 생성했습니다.')
}

async function verifyQuest(questId: number) {
  await runAction(`verify-${questId}`, async () => {
    await questApi.verifyQuest(questId)
    await reloadQuestProgress()
  }, '퀘스트 상태를 확인했습니다.')
}

async function claimQuestReward(questId: number) {
  await runAction(`reward-${questId}`, async () => {
    await questApi.claimQuestReward(questId)
    if (!battleId.value) return
    const [myQuestData, questList, dashboardData] = await Promise.all([
      questApi.getMyBattleQuests(battleId.value),
      questApi.getBattleQuests(battleId.value),
      bossBattleApi.getBossBattleDashboard(battleId.value)
    ])
    myQuest.value = myQuestData
    quests.value = questList.quests ?? []
    dashboard.value = dashboardData
  }, '퀘스트 보상을 수령했습니다.')
}

async function verifyConditions() {
  if (!battleId.value) return
  await runAction('verify-conditions', async () => {
    await bossBattleApi.verifyCommonConditions(battleId.value!)
    const [detail, hpData, dashboardData] = await Promise.all([
      bossBattleApi.getBossBattle(battleId.value!),
      bossBattleApi.getBossBattleHp(battleId.value!),
      bossBattleApi.getBossBattleDashboard(battleId.value!)
    ])
    battle.value = detail
    hp.value = hpData
    dashboard.value = dashboardData
  }, '공통 격파 조건을 확인했습니다.')
}

async function claimBattleReward() {
  if (!battleId.value) return
  await runAction('battle-reward', async () => {
    await bossBattleApi.claimBossBattleReward(battleId.value!)
    ;[battle.value, dashboard.value] = await Promise.all([
      bossBattleApi.getBossBattle(battleId.value!),
      bossBattleApi.getBossBattleDashboard(battleId.value!)
    ])
  }, '보스전 보상을 수령했습니다.')
}

function questMember(quest: QuestSummary) {
  const contribution = contributions.value.find((item) => item.userId === quest.userId)
  return {
    id: quest.questId,
    name: quest.isMe ? `${quest.nickname} (나)` : quest.nickname,
    role: contribution ? `${contribution.totalDamage} HP 기여` : (quest.questType || '개인 퀘스트'),
    lv: quest.characterLevel ?? '-',
    quest: quest.title,
    progress: quest.currentValue,
    total: quest.targetValue || 1,
    unit: quest.unit || '',
    mine: Boolean(quest.isMe),
    done: quest.status === 'COMPLETED' || quest.status === 'REWARDED',
    idle: quest.status === 'EXPIRED'
  }
}

function difficultyLabel(value: BossDifficulty) { return ({ EASY: '쉬움', NORMAL: '보통', HARD: '어려움' } as Record<BossDifficulty, string>)[value] }
function difficultyTone(value: BossDifficulty): 'ok' | 'accent' | 'bad' { return value === 'EASY' ? 'ok' : value === 'HARD' ? 'bad' : 'accent' }
function statusLabel(value?: string) { return ({ IN_PROGRESS: '진행 중', DEFEATED: '격파 성공', CLEARED: '격파 성공', COMPLETED: '격파 성공', EXPIRED: '기간 종료', FAILED: '실패' } as Record<string, string>)[value || ''] || '데이터 없음' }
function formatDate(value?: string) { return value ? new Intl.DateTimeFormat('ko-KR', { month: 'short', day: 'numeric' }).format(new Date(value)) : '-' }

onMounted(() => { void loadBossPage() })
</script>

<style scoped>
.state-screen { max-width: 620px; margin: 80px auto; text-align: center; }
.api-message { margin-bottom: 14px; padding: 11px 14px; border: 1px solid var(--bad); border-radius: 10px; background: var(--surface); color: var(--bad); font-size: 13px; }
.api-message.success { border-color: var(--ok); color: var(--ok); }
.empty-state,.quest-empty { padding: 22px; border: 1px dashed var(--border); border-radius: 12px; text-align: center; color: var(--ink-3); }
.quest-empty p { margin-bottom: 10px; }
.pill-row { display: flex; gap: 8px; margin-bottom: 6px; } .title-xl span { color: var(--ink-3); } p { margin: 4px 0 0; color: var(--ink-2); font-size: 13px; }
.boss-lobby { display: flex; flex-direction: column; gap: 14px; }
.boss-art { border-radius: 18px; overflow: hidden; padding: 36px 32px; background: linear-gradient(135deg,#fff5e0 0%,#fbe5d3 50%,#f6c098 100%); border: 1.5px solid var(--border); box-shadow: var(--shadow-lg); display: flex; flex-direction: column; align-items: center; gap: 12px; }
.boss-art strong { font-size: 20px; font-weight: 900; } .boss-art p { max-width: 400px; text-align: center; line-height: 1.6; font-size: 12px; }
.diff-label { margin-bottom: 10px; } .diff-grid { display: grid; grid-template-columns: repeat(3,1fr); gap: 10px; }
.diff-grid button { padding: 18px 14px; text-align: center; border: 2px solid var(--border); background: var(--surface); border-radius: 14px; cursor: pointer; } .diff-grid strong { font-family: var(--mono); font-size: 14px; display: block; margin-bottom: 6px; } .diff-grid .easy strong { color: var(--ok); } .diff-grid .normal strong { color: var(--accent); } .diff-grid .hard strong { color: var(--bad); } .diff-grid .easy.active { background: var(--ok-soft); border-color: var(--ok); } .diff-grid .normal.active { background: var(--accent-soft); border-color: var(--accent); } .diff-grid .hard.active { background: var(--bad-soft); border-color: var(--bad); }
.diff-summary { background: var(--surface-alt); } .diff-summary :deep(> div) { display: flex; gap: 16px; } .diff-summary div div { flex: 1; text-align: center; } .diff-summary small { font-family: var(--mono); color: var(--ink-3); font-size: 10px; } .diff-summary strong { display: block; font-family: var(--mono); font-size: 22px; margin-top: 4px; }
.battle-actions { display: flex; gap: 8px; } .leader-help,.creation-step { margin-top: 6px; font-family: var(--mono); font-size: 11px; color: var(--ink-3); text-align: right; }
.battle-grid { display: grid; grid-template-columns: 1fr; gap: 20px; } .battle-left, .members { display: flex; flex-direction: column; gap: 14px; }
.clear-banner { display: flex; align-items: center; justify-content: center; gap: 20px; padding: 24px; border: 2px solid var(--yolk-deep); border-radius: 18px; background: radial-gradient(circle at 50% 0%,#fff 0%,#fff5c8 36%,#ffd17d 100%); box-shadow: 0 0 0 6px rgba(243,211,111,.2),0 18px 38px rgba(232,185,67,.28); position: relative; overflow: hidden; animation: clear-pop .6s cubic-bezier(.2,1.4,.4,1); }
.clear-banner:before,.clear-banner:after { content: "✦  ✧  ✦  ✧  ✦"; position: absolute; top: -28px; color: var(--accent); font-size: 20px; letter-spacing: 22px; animation: sparkle-fall 2.5s linear infinite; } .clear-banner:before { left: 6%; } .clear-banner:after { right: 2%; animation-delay: 1.2s; }
.clear-badge { width: 86px; height: 86px; border-radius: 50%; background: linear-gradient(180deg,#514139,var(--ink)); color: #fff; display: flex; align-items: center; justify-content: center; text-align: center; font-family: var(--mono); font-weight: 900; letter-spacing: 1px; box-shadow: 0 5px 0 #160f0c,0 0 24px rgba(255,255,255,.7); flex: 0 0 86px; }
.clear-banner h2 { margin: 0; font-size: 28px; color: var(--accent-dark); }
.clear-banner p { margin-top: 4px; font-size: 14px; font-weight: 800; color: var(--ink); }
.arena { position: relative; height: 380px; border-radius: 18px; overflow: hidden; background: linear-gradient(135deg,#fff5e0 0%,#fbe5d3 50%,#f6c098 100%); border: 1.5px solid var(--border); box-shadow: var(--shadow-lg); display: flex; align-items: center; justify-content: center; } .grid-bg { position: absolute; inset: 0; opacity: .16; background-image: linear-gradient(var(--ink) 1px, transparent 1px), linear-gradient(90deg,var(--ink) 1px, transparent 1px); background-size: 40px 40px; }
.hp-row { display: flex; align-items: center; gap: 14px; } .hp-row span { font-family: var(--mono); font-size: 11px; color: var(--ink-3); font-weight: 700; letter-spacing: 1px; } .hp-row :deep(.bar-wrap) { flex: 1; } .hp-row b { font-family: var(--mono); font-size: 18px; color: var(--accent); min-width: 130px; text-align: right; }
.damage-row { margin-top: 10px; padding-top: 10px; border-top: 1px dashed var(--border); display: flex; justify-content: space-between; font-family: var(--mono); font-size: 11px; color: var(--ink-3); } .damage-row strong { color: var(--accent); }
.battle-tabs { margin: 2px 0 0; }
.condition-grid { display: grid; grid-template-columns: 1fr; gap: 12px; } .condition-head { display: flex; align-items: center; justify-content: space-between; gap: 8px; } .checks { display: grid; gap: 8px; margin-top: 14px; } .checks p { margin: 0; display: grid; grid-template-columns: 24px 1fr auto; align-items: center; gap: 10px; padding: 12px; border: 1px solid var(--border); border-radius: 12px; background: var(--surface-alt); font-size: 13px; color: var(--ink-2); } .checks p.completed { background: var(--ok-soft); border-color: var(--ok); } .checks p b { color: var(--accent); font-size: 18px; } .checks p.completed b { color: var(--ok); } .checks p strong { font-family: var(--mono); color: var(--ink); }
.reward { background: linear-gradient(135deg,#fff5c8,#ffe0ad); border-color: var(--yolk-deep); text-align: center; } .reward-loot { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; margin: 16px 0; } .reward-loot span { padding: 16px; border: 1px solid var(--yolk-deep); border-radius: 14px; background: rgba(255,255,255,.64); font-size: 26px; } .reward small { display: block; font-family: var(--mono); color: var(--ink-2); font-size: 10px; margin-top: 8px; } .reward strong { display: block; font-family: var(--mono); color: var(--accent-dark); font-size: 18px; margin-top: 4px; } .reward-lock { color: var(--ink-2); font-size: 12px; }
.member-head { display: flex; justify-content: space-between; align-items: baseline; } .member-head strong { font-size: 13px; } .member-head span { font-family: var(--mono); color: var(--ink-3); font-size: 11px; } .tip { background: var(--surface-alt); color: var(--ink-2); font-size: 11px; line-height: 1.6; }
.quest-entry { display: flex; flex-direction: column; gap: 6px; } .quest-actions { display: flex; justify-content: flex-end; align-items: center; gap: 6px; }
@keyframes clear-pop { from { opacity: 0; transform: translateY(12px) scale(.86); } to { opacity: 1; transform: translateY(0) scale(1); } }
@keyframes sparkle-fall { from { transform: translateY(0) rotate(0); } to { transform: translateY(130px) rotate(120deg); } }
@media (max-width: 960px) { .battle-grid { grid-template-columns: 1fr; } }
@media (max-width: 640px) { .page-title-row { align-items: flex-start; flex-direction: column; } .diff-grid,.condition-grid { grid-template-columns: 1fr; } .hp-row,.damage-row { align-items: flex-start; flex-direction: column; } .hp-row :deep(.bar-wrap) { width: 100%; } }
</style>
