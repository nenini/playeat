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
      <div class="boss-art" :style="{ backgroundImage: `url(${selectedBossAssets.background})` }"><BossMonster :size="275" :hp="100" :boss-type="selectedBossType" :boss-name="selectedBoss?.name || '보스'" /><strong>{{ selectedBoss?.name || '보스 데이터 없음' }}</strong></div>
      <div><div class="mono-label diff-label">보스 선택</div><p class="diff-help">난이도를 선택해 길드원과 함께 전투를 시작하세요</p><div v-if="bosses.length" class="diff-grid"><button v-for="item in bosses" :key="item.bossId" :class="[item.difficulty.toLowerCase(), { active: selectedBossId === item.bossId }]" @click="selectedBossId = item.bossId"><strong>{{ difficultyLabel(item.difficulty) }}</strong><p>{{ item.name }}</p><AppPill :tone="difficultyTone(item.difficulty)" size="sm">{{ item.rewardExp.toLocaleString() }} XP</AppPill></button></div><div v-else class="empty-state">현재 시즌 보스가 없습니다.</div></div>
      <AppCard v-if="selectedBoss" :padding="14" class="diff-summary"><div><div><small>BOSS HP</small><strong>{{ selectedBoss.maxHp.toLocaleString() }}</strong></div><div><small>보상 코인</small><strong>{{ selectedBoss.rewardCoin.toLocaleString() }}</strong></div><div><small>격파 XP</small><strong>{{ selectedBoss.rewardExp.toLocaleString() }}</strong></div></div></AppCard>
    </div>
  </section>

  <section v-else class="boss-battle-screen" :style="{ backgroundImage: `url(${battleBossAssets.background})` }">
    <div v-if="errorMessage" class="api-message">{{ errorMessage }}</div>
    <div v-if="successMessage" class="api-message success">{{ successMessage }}</div>
    <div class="page-title-row">
      <div><div class="pill-row"><AppPill tone="bad" size="sm">GUILD BOSS · {{ battle?.difficulty || '-' }}</AppPill><AppPill size="sm">{{ statusLabel(battle?.status) }}</AppPill><AppPill size="sm">{{ battle?.guildName || guildName }}</AppPill></div><div class="title-xl">{{ battle?.bossName || '보스전' }}</div><p>시작 {{ formatDate(battle?.startedAt) }} · 종료 {{ formatDate(battle?.endsAt) }} · 길드원 {{ battle?.participantCount ?? '-' }}명 참전</p></div>
      <div class="battle-actions">
        <button type="button" class="sound-toggle" :class="{ off: !attackSoundEnabled }" @click="toggleSound">
          {{ attackSoundEnabled ? '효과음 ON' : '효과음 OFF' }}
        </button>
        <AppButton @click="$emit('navigate', 'meals')"><AppIcon name="plus" color="#fff" />식단 기록 · 데미지!</AppButton>
      </div>
    </div>
    <div class="battle-grid">
      <main class="battle-left">
        <div class="arena" :class="{ cleared: isBattleCleared, 'is-hit': isBossHit }">
          <BossMonster :size="420" :hp="hpPercent" :boss-type="battleBossType" :boss-name="battle?.bossName || bossDetail?.name || '보스'" :cleared="isBattleCleared" />
          <BossAttackEffect
            v-if="attackDamage > 0"
            :play-key="attackSequence"
            :effect-type="attackEffectType"
            :damage="attackDamage"
            :attacker-name="attackAttackerName"
          />
          <div v-if="isBattleCleared" class="boss-clear-overlay">
            <span class="sparkle sparkle-a">✦</span>
            <span class="sparkle sparkle-b">✧</span>
            <span class="sparkle sparkle-c">✦</span>
            <div class="clear-kicker">BOSS CLEAR!</div>
            <h2>{{ battle?.bossName || '당분 드래곤' }} 격파 성공</h2>
            <p>길드원들과 함께 보스를 쓰러뜨렸어요. 보상을 수령해 주세요.</p>
            <AppButton v-if="!battle?.rewardClaimed" size="lg" :disabled="pendingActionId === 'battle-reward'" @click="claimBattleReward">보상 수령하기</AppButton>
            <AppPill v-else tone="ok" size="sm">보상 수령 완료</AppPill>
          </div>
        </div>
        <AppCard :padding="12" class="hp-card"><div class="hp-row"><span>BOSS HP</span><ProgressBar :value="hp?.currentHp ?? 0" :max="hp?.maxHp || 1" :tone="hpPercent < 30 ? 'accent' : 'bad'" :height="12" /><b>{{ hp?.currentHp?.toLocaleString() ?? '-' }} / {{ hp?.maxHp?.toLocaleString() ?? '-' }}</b></div><div class="damage-row"><span>누적 데미지: <strong>−{{ hp?.totalDamage?.toLocaleString() ?? '-' }}</strong> HP</span><span>퀘스트 완료: <strong>{{ dashboard ? `${dashboard.questCompletedCount}/${dashboard.questTotalCount}` : '-' }}</strong></span></div></AppCard>
        <button v-if="recentAttack" type="button" class="recent-attack-card" @click="replayRecentAttack">
          <span>최근 공격</span>
          <strong>{{ recentAttack.attackerName }}님이 {{ recentAttack.damage.toLocaleString() }} HP 피해!</strong>
          <small>클릭하면 공격 연출을 다시 볼 수 있어요</small>
        </button>
        <div class="condition-grid">
          <AppCard :padding="10" class="condition-card"><div class="condition-head"><div class="section-title-main">길드 공통 격파 조건</div><AppPill :tone="isBattleCleared ? 'ok' : 'accent'" size="sm">{{ isBattleCleared ? '완료' : '진행 중' }}</AppPill></div><div v-if="battle?.commonConditions?.length" class="checks"><p v-for="condition in battle.commonConditions" :key="condition.battleConditionId" :class="{ completed: condition.completed }"><b>{{ condition.completed ? '✓' : '□' }}</b><span>{{ condition.title }}</span><strong>{{ condition.currentValue ?? '-' }}/{{ condition.targetValue ?? '-' }} {{ condition.unit || '' }}</strong></p></div><p v-else>격파 조건 데이터가 없습니다.</p></AppCard>
          <AppCard :padding="10" class="reward"><div class="section-title-main">레이드 클리어 보상</div><div class="reward-loot"><span>✨<small>경험치</small><strong>+{{ bossDetail?.rewardExp?.toLocaleString() ?? '-' }} XP</strong></span><span>🪙<small>코인</small><strong>+{{ bossDetail?.rewardCoin?.toLocaleString() ?? '-' }}</strong></span></div><AppPill v-if="isBattleCleared && battle?.rewardClaimed" tone="ok" size="sm">보상 수령 완료</AppPill><p v-else-if="isBattleCleared" class="reward-lock">상단 클리어 화면에서 보상을 수령할 수 있어요.</p><p v-else class="reward-lock">보스를 격파하면 보상 상자가 열립니다.</p></AppCard>
        </div>
      </main>
      <aside class="members">
        <div class="member-head">
          <strong>길드원 개인 퀘스트 · {{ displayQuests.length }}명</strong>
          <span>{{ dashboard ? `완료 ${dashboard.questCompletedCount} · 전체 ${dashboard.questTotalCount}` : '-' }}</span>
        </div>
        <div v-if="displayQuests.length === 0" class="quest-empty"><p>생성된 퀘스트가 없습니다.</p><AppButton v-if="isLeader" size="sm" :disabled="pendingActionId === 'generate-quests'" @click="generateQuests">퀘스트 생성</AppButton></div>
        <div v-else class="quest-board">
          <section class="my-quest-panel">
            <div class="quest-panel-title">본인 퀘스트</div>
            <template v-if="myDisplayQuest">
              <MemberQuest :member="questMember(myDisplayQuest)" />
              <div class="quest-actions"><AppButton v-if="myDisplayQuest.status === 'COMPLETED'" size="sm" :disabled="pendingActionId === `reward-${myDisplayQuest.questId}`" @click="claimQuestReward(myDisplayQuest.questId)">보상 수령</AppButton><AppPill v-if="myDisplayQuest.status === 'REWARDED'" tone="ok" size="sm">보상 수령 완료</AppPill></div>
            </template>
            <p v-else class="quest-missing">내 퀘스트가 아직 없습니다.</p>
          </section>
          <section class="guild-quest-panel">
            <div class="quest-slider-head">
              <div class="quest-panel-title">길드원 퀘스트</div>
              <div class="quest-slider-controls">
                <button type="button" :disabled="guildQuestPage <= 0" aria-label="이전 길드원 퀘스트" @click="prevGuildQuestPage">‹</button>
                <span>{{ guildQuestPageCount ? guildQuestCurrentPage + 1 : 0 }} / {{ guildQuestPageCount }}</span>
                <button type="button" :disabled="guildQuestPage >= guildQuestPageCount - 1" aria-label="다음 길드원 퀘스트" @click="nextGuildQuestPage">›</button>
              </div>
            </div>
            <div v-if="visibleGuildQuests.length" class="guild-quest-track">
              <div v-for="quest in visibleGuildQuests" :key="quest.questId" class="quest-entry">
                <MemberQuest :member="questMember(quest)" />
              </div>
            </div>
            <p v-else class="quest-missing">다른 길드원 퀘스트가 없습니다.</p>
          </section>
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
import BossAttackEffect from '../components/boss/BossAttackEffect.vue'
import BossMonster from '../components/nyamnyam/BossMonster.vue'
import { bossAssetsFor, resolveBossType } from '../utils/bossAssets'
import { attackEffectLabel, resolveAttackEffectType, type AttackEffectType } from '../utils/attackEffect'
import { getAttackSoundEnabled, playAttackSound, toggleAttackSound } from '../utils/attackSound'
import { bossApi } from '../services/api/bossApi'
import { bossBattleApi } from '../services/api/bossBattleApi'
import { ApiError } from '../services/api/client'
import { characterEquipmentApi } from '../services/api/characterEquipmentApi'
import { guildApi } from '../services/api/guildApi'
import { questApi } from '../services/api/questApi'
import type { BossDetail, BossDifficulty, BossSummary } from '../types/boss'
import type { BossBattleDamageLog, BossBattleDashboard, BossBattleDetail, BossBattleHp } from '../types/bossBattle'
import type { CharacterEquipment } from '../types/characterEquipment'
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
const guildQuestPage = ref(0)
const handEquipment = ref<CharacterEquipment | null>(null)
const attackSequence = ref(0)
const attackDamage = ref(0)
const attackEffectType = ref<AttackEffectType>('DEFAULT')
const attackAttackerName = ref('')
const attackSoundEnabled = ref(getAttackSoundEnabled())
const isBossHit = ref(false)
const recentAttack = ref<{ damage: number; effectType: AttackEffectType; attackerName: string; label: string } | null>(null)
let hitTimer: ReturnType<typeof setTimeout> | null = null
let recentLogReplayGuard = false

const isLeader = computed(() => guildRole.value === 'OWNER')
const selectedBoss = computed(() => bosses.value.find((item) => item.bossId === selectedBossId.value) ?? bosses.value[0] ?? null)
const selectedBossType = computed(() => resolveBossType(selectedBoss.value?.imageUrl, selectedBoss.value?.difficulty, selectedBoss.value?.name))
const selectedBossAssets = computed(() => bossAssetsFor(selectedBoss.value?.imageUrl, selectedBoss.value?.difficulty, selectedBoss.value?.name))
const battleBossType = computed(() => resolveBossType(battle.value?.bossImageUrl || battle.value?.imageUrl || bossDetail.value?.imageUrl, battle.value?.difficulty || bossDetail.value?.difficulty, battle.value?.bossName || bossDetail.value?.name))
const battleBossAssets = computed(() => bossAssetsFor(battle.value?.bossImageUrl || battle.value?.imageUrl || bossDetail.value?.imageUrl, battle.value?.difficulty || bossDetail.value?.difficulty, battle.value?.bossName || bossDetail.value?.name))
const hpPercent = computed(() => hp.value?.maxHp ? hp.value.currentHp / hp.value.maxHp * 100 : 0)
const isBattleCleared = computed(() => ['DEFEATED', 'CLEARED', 'COMPLETED'].includes(String(battle.value?.status || '')) || Number(hp.value?.currentHp ?? 1) <= 0)
const currentAttackEffectType = computed(() => resolveAttackEffectType(handEquipment.value))
const attackerName = computed(() => myQuest.value?.nickname || '내')
const displayQuests = computed<QuestSummary[]>(() => {
  if (!myQuest.value) return quests.value

  const myQuestIndex = quests.value.findIndex((quest) => quest.questId === myQuest.value?.questId)
  const mine: QuestSummary = { ...myQuest.value, isMe: true }
  if (myQuestIndex < 0) return [mine, ...quests.value]

  return quests.value.map((quest, index) => index === myQuestIndex ? mine : quest)
})
const myDisplayQuest = computed(() => displayQuests.value.find((quest) => quest.isMe) ?? null)
const guildDisplayQuests = computed(() => displayQuests.value.filter((quest) => !quest.isMe))
const guildQuestPageCount = computed(() => Math.ceil(guildDisplayQuests.value.length / 4))
const guildQuestCurrentPage = computed(() => guildQuestPageCount.value ? Math.min(guildQuestPage.value, guildQuestPageCount.value - 1) : 0)
const visibleGuildQuests = computed(() => {
  const start = guildQuestCurrentPage.value * 4
  return guildDisplayQuests.value.slice(start, start + 4)
})

function prevGuildQuestPage() {
  guildQuestPage.value = Math.max(0, guildQuestPage.value - 1)
}

function nextGuildQuestPage() {
  guildQuestPage.value = Math.min(Math.max(0, guildQuestPageCount.value - 1), guildQuestPage.value + 1)
}

async function loadAttackEquipment() {
  try {
    const response = await characterEquipmentApi.getMyEquipments()
    handEquipment.value = response.equipments?.find((item) => item.slotType === 'HAND' && item.equipped && item.itemId !== null) ?? null
  } catch {
    handEquipment.value = null
  }
}

function setBossHp(nextHp: BossBattleHp, animate = true) {
  const previousHp = hp.value?.currentHp
  const nextCurrentHp = nextHp.currentHp
  if (animate && !recentLogReplayGuard && previousHp !== undefined && previousHp > nextCurrentHp) {
    playAttackEffect(previousHp - nextCurrentHp)
  }
  hp.value = nextHp
}

function effectTypeFromDamageLog(log: BossBattleDamageLog) {
  return resolveAttackEffectType({
    name: log.weaponName,
    imageUrl: log.weaponImageUrl,
    effectValue: log.weaponEffectValue
  })
}

function recentAttackFromLog(log: BossBattleDamageLog) {
  const effectType = effectTypeFromDamageLog(log)
  return {
    damage: Math.max(0, Math.round(Number(log.damage) || 0)),
    effectType,
    attackerName: log.nickname || '길드원',
    label: attackEffectLabel(effectType)
  }
}

function damageLogStorageKey(id: number) {
  return `nyamnyam:last-seen-damage-log:${id}`
}

function syncRecentDamageLog(logs?: BossBattleDamageLog[]) {
  const latestLog = logs?.[0]
  if (!battleId.value || !latestLog || !latestLog.damageLogId) return

  const nextRecentAttack = recentAttackFromLog(latestLog)
  recentAttack.value = nextRecentAttack

  const key = damageLogStorageKey(battleId.value)
  const logId = String(latestLog.damageLogId)
  if (localStorage.getItem(key) === logId) return

  localStorage.setItem(key, logId)
  recentLogReplayGuard = true
  playAttackEffect(nextRecentAttack.damage, nextRecentAttack.effectType, nextRecentAttack.attackerName)
  window.setTimeout(() => {
    recentLogReplayGuard = false
  }, 1200)
}

function playAttackEffect(damage: number, effectType = currentAttackEffectType.value, name = attackerName.value) {
  const normalizedDamage = Math.max(0, Math.round(Number(damage) || 0))
  if (normalizedDamage <= 0) return

  playAttackSound(effectType)
  attackDamage.value = normalizedDamage
  attackEffectType.value = effectType
  attackAttackerName.value = name
  attackSequence.value += 1
  isBossHit.value = true
  recentAttack.value = {
    damage: normalizedDamage,
    effectType,
    attackerName: name,
    label: attackEffectLabel(effectType)
  }

  if (hitTimer) clearTimeout(hitTimer)
  hitTimer = setTimeout(() => {
    isBossHit.value = false
  }, effectType === 'STICK' ? 560 : 460)
}

function replayRecentAttack() {
  if (!recentAttack.value) return
  playAttackEffect(recentAttack.value.damage, recentAttack.value.effectType, recentAttack.value.attackerName)
}

function toggleSound() {
  attackSoundEnabled.value = toggleAttackSound()
}

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
  battleId.value = id
  battle.value = detail
  setBossHp(hpData, false)
  dashboard.value = dashboardData
  myQuest.value = myQuestData
  quests.value = questList.quests ?? []
  contributions.value = contributionList.contributions ?? []
  bossDetail.value = bossData
  syncRecentDamageLog(detail.recentDamageLogs)
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
  setBossHp(hpData)
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
    const [myQuestData, questList, hpData, dashboardData] = await Promise.all([
      questApi.getMyBattleQuests(battleId.value),
      questApi.getBattleQuests(battleId.value),
      bossBattleApi.getBossBattleHp(battleId.value),
      bossBattleApi.getBossBattleDashboard(battleId.value)
    ])
    myQuest.value = myQuestData
    quests.value = questList.quests ?? []
    setBossHp(hpData)
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
    setBossHp(hpData)
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

onMounted(() => {
  void loadAttackEquipment()
  void loadBossPage()
})
</script>

<style scoped>
.state-screen { max-width: 620px; margin: 80px auto; text-align: center; }
.api-message { margin-bottom: 14px; padding: 11px 14px; border: 1px solid var(--bad); border-radius: 10px; background: var(--surface); color: var(--bad); font-size: 13px; }
.api-message.success { border-color: var(--ok); color: var(--ok); }
.empty-state,.quest-empty { padding: 22px; border: 1px dashed var(--border); border-radius: 12px; text-align: center; color: var(--ink-3); }
.quest-empty p { margin-bottom: 10px; }
.pill-row { display: flex; gap: 8px; margin-bottom: 6px; } .title-xl span { color: var(--ink-3); } p { margin: 4px 0 0; color: var(--ink-2); font-size: 13px; }
.boss-lobby { display: flex; flex-direction: column; gap: 14px; }
.boss-art { border-radius: 18px; overflow: hidden; padding: 36px 32px; background-color: transparent; background-size: cover; background-position: center; background-repeat: no-repeat; border: 1.5px solid var(--border); box-shadow: var(--shadow-lg); display: flex; flex-direction: column; align-items: center; gap: 12px; position: relative; }
.boss-art:before { content: ""; position: absolute; inset: 0; background: rgba(32, 22, 14, .12); z-index: 0; pointer-events: none; } .boss-art :deep(.boss-monster), .boss-art strong, .boss-art p { position: relative; z-index: 1; } .boss-art strong { font-size: 20px; font-weight: 900; } .boss-art p { max-width: 400px; text-align: center; line-height: 1.6; font-size: 12px; }
.diff-label { display: inline-flex; align-items: center; gap: 8px; margin-bottom: 8px; padding: 7px 13px; border: 1px solid var(--accent); border-radius: 999px; background: linear-gradient(180deg, #f8ffe8, var(--accent-soft)); color: var(--accent-dark); font-family: var(--sans); font-size: 18px; font-weight: 900; letter-spacing: -0.2px; box-shadow: 0 3px 0 rgba(143,207,85,.18), 0 10px 20px rgba(143,207,85,.12); } .diff-label:before { content: "✦"; color: var(--accent); text-shadow: 0 0 10px rgba(184,219,128,.9); } .diff-help { margin: 0 0 16px; padding-left: 4px; color: var(--ink-2); font-size: 14px; font-weight: 700; line-height: 1.55; } .diff-grid { display: grid; grid-template-columns: repeat(3,1fr); gap: 10px; }
.diff-grid button { padding: 18px 14px; text-align: center; border: 2px solid var(--border); background: var(--surface); border-radius: 14px; cursor: pointer; } .diff-grid strong { font-family: var(--mono); font-size: 14px; display: block; margin-bottom: 6px; } .diff-grid .easy strong { color: var(--ok); } .diff-grid .normal strong { color: var(--accent); } .diff-grid .hard strong { color: var(--bad); } .diff-grid .easy.active { background: var(--ok-soft); border-color: var(--ok); } .diff-grid .normal.active { background: var(--accent-soft); border-color: var(--accent); } .diff-grid .hard.active { background: var(--bad-soft); border-color: var(--bad); }
.diff-summary { background: var(--surface-alt); } .diff-summary :deep(> div) { display: flex; gap: 16px; } .diff-summary div div { flex: 1; text-align: center; } .diff-summary small { font-family: var(--mono); color: var(--ink-3); font-size: 10px; } .diff-summary strong { display: block; font-family: var(--mono); font-size: 22px; margin-top: 4px; }
.battle-actions { display: flex; align-items: center; gap: 8px; } .leader-help,.creation-step { margin-top: 6px; font-family: var(--mono); font-size: 11px; color: var(--ink-3); text-align: right; }
.sound-toggle { min-height: 34px; padding: 0 12px; border: 1.5px solid rgba(143,207,85,.64); border-radius: 999px; background: linear-gradient(180deg, #f8ffe8, var(--accent-soft)); color: var(--accent-dark); font-size: 11px; font-weight: 900; box-shadow: 0 3px 0 rgba(143,207,85,.18); cursor: pointer; transition: transform .16s ease, box-shadow .16s ease, opacity .16s ease; }
.sound-toggle:hover { transform: translateY(-1px); box-shadow: 0 5px 0 rgba(143,207,85,.14); }
.sound-toggle.off { border-color: rgba(116,75,49,.24); background: rgba(255,255,255,.7); color: var(--ink-3); box-shadow: none; }
.boss-battle-screen { position: relative; width: 100vw; min-height: calc(100vh - 72px); margin: -30px calc(50% - 50vw) -56px; padding: clamp(18px, 2.5vw, 34px) clamp(16px, 4vw, 52px) 56px; overflow: hidden; background-size: cover; background-position: 50% 50%; background-repeat: no-repeat; background-attachment: fixed; box-sizing: border-box; }
.boss-battle-screen:before { content: ""; position: absolute; inset: 0; z-index: 0; background: linear-gradient(180deg, rgba(18, 12, 8, .48), rgba(18, 12, 8, .2) 35%, rgba(18, 12, 8, .56)), radial-gradient(circle at 50% 36%, rgba(255, 229, 122, .16), transparent 35%); pointer-events: none; }
.boss-battle-screen > * { position: relative; z-index: 1; }
.boss-battle-screen .api-message { width: min(80%, 1440px); margin: 0 auto 14px; background: rgba(255,255,255,.84); backdrop-filter: blur(12px); box-shadow: 0 18px 46px rgba(0,0,0,.16); }
.boss-battle-screen .page-title-row { width: min(80%, 1440px); margin: 0 auto 20px; padding: 16px 18px; border: 1px solid rgba(255,255,255,.48); border-radius: 18px; background: rgba(255,255,255,.78); backdrop-filter: blur(12px); box-shadow: 0 18px 52px rgba(0,0,0,.22); box-sizing: border-box; }
.boss-battle-screen .page-title-row p { color: rgba(50, 35, 26, .8); font-weight: 700; }
.boss-battle-screen .title-xl { color: var(--ink); text-shadow: 0 1px 0 rgba(255,255,255,.7); }
.battle-grid { width: min(80%, 1440px); margin: 0 auto; display: grid; grid-template-columns: minmax(0, 1fr); grid-template-areas: "stage" "conditions" "members"; gap: 18px; align-items: start; position: relative; z-index: 1; }
.battle-left { display: contents; }
.members { grid-area: members; display: flex; flex-direction: column; gap: 14px; }
.boss-battle-screen :deep(.app-card), .members, .quest-empty { background: rgba(255,255,255,.78); border: 1px solid rgba(255,255,255,.48); box-shadow: 0 18px 48px rgba(0,0,0,.18); backdrop-filter: blur(12px); }
.boss-battle-screen :deep(.app-card:hover) { border-color: rgba(255,255,255,.72); box-shadow: 0 22px 54px rgba(0,0,0,.22); }
.boss-battle-screen .reward { background: rgba(255,241,200,.82); border-color: rgba(255,199,64,.72); }
.boss-battle-screen .tip { background: rgba(255,255,255,.7); }
.boss-battle-screen .checks p, .boss-battle-screen .reward-loot span { background: rgba(255,255,255,.62); border-color: rgba(116,75,49,.18); }
.boss-battle-screen .checks p.completed { background: rgba(232,247,200,.74); border-color: rgba(98,167,62,.52); }
.arena { grid-area: stage; position: relative; min-height: clamp(430px, 52vh, 620px); overflow: visible; background: transparent; border: 0; box-shadow: none; display: flex; align-items: center; justify-content: center; padding: 42px 16px 10px; } .arena :deep(.boss-monster) { z-index: 2; width: clamp(320px, 32vw, 440px) !important; height: clamp(254px, 25.4vw, 349px) !important; filter: drop-shadow(0 28px 34px rgba(0,0,0,.42)); }
.arena.is-hit :deep(.boss-monster) { animation: boss-hit-shake .44s cubic-bezier(.22,.9,.3,1); }
.arena.cleared:before { content: ""; position: absolute; inset: 0; z-index: 3; background: radial-gradient(circle at 50% 44%, rgba(255, 229, 122, .2) 0%, rgba(18, 12, 8, .28) 42%, rgba(18, 12, 8, .72) 100%); pointer-events: none; }
.arena :deep(.boss-attack-effect) { z-index: 3; }
.boss-clear-overlay { position: absolute; inset: 0; z-index: 4; display: flex; flex-direction: column; align-items: center; justify-content: flex-start; gap: 10px; padding: 48px 30px 30px; text-align: center; color: #fff; animation: clear-overlay-pop .58s cubic-bezier(.2,1.35,.38,1); }
.clear-kicker { padding: 8px 16px; border: 1px solid rgba(255, 229, 122, .82); border-radius: 999px; background: rgba(31, 24, 15, .46); color: #ffe57a; font-family: var(--mono); font-size: 22px; font-weight: 900; letter-spacing: 2px; text-shadow: 0 2px 10px rgba(0,0,0,.7), 0 0 18px rgba(255,229,122,.72); box-shadow: 0 0 24px rgba(255,229,122,.35); }
.boss-clear-overlay h2 { margin: 0; color: #fff8d8; font-size: 32px; font-weight: 900; text-shadow: 0 3px 16px rgba(0,0,0,.82), 0 0 24px rgba(255,229,122,.48); }
.boss-clear-overlay p { max-width: 420px; margin: 0 0 4px; color: rgba(255,255,255,.92); font-size: 14px; font-weight: 800; line-height: 1.55; text-shadow: 0 2px 10px rgba(0,0,0,.78); }
.sparkle { position: absolute; color: #ffe57a; font-size: 24px; text-shadow: 0 0 18px rgba(255,229,122,.9); animation: clear-sparkle 1.8s ease-in-out infinite; }
.sparkle-a { left: 18%; top: 22%; }
.sparkle-b { right: 22%; top: 28%; animation-delay: .45s; }
.sparkle-c { left: 62%; bottom: 22%; animation-delay: .9s; }
@keyframes clear-overlay-pop { from { opacity: 0; transform: scale(.92); } to { opacity: 1; transform: scale(1); } }
@keyframes clear-sparkle { 0%,100% { opacity: .35; transform: translateY(0) scale(.82) rotate(0deg); } 50% { opacity: 1; transform: translateY(-8px) scale(1.18) rotate(18deg); } }
.hp-card { grid-area: stage; align-self: start; justify-self: center; z-index: 5; width: min(520px, 54vw); margin-top: 38px; }
.hp-row { display: flex; align-items: center; gap: 10px; } .hp-row span { font-family: var(--mono); font-size: 10px; color: var(--ink-3); font-weight: 700; letter-spacing: 1px; white-space: nowrap; } .hp-row :deep(.bar-wrap) { flex: 1; min-width: 110px; } .hp-row b { font-family: var(--mono); font-size: 13px; color: var(--accent); min-width: 106px; text-align: right; }
.damage-row { margin-top: 8px; padding-top: 8px; border-top: 1px dashed rgba(116,75,49,.2); display: flex; justify-content: space-between; gap: 12px; font-family: var(--mono); font-size: 10px; color: var(--ink-3); } .damage-row strong { color: var(--accent); }
.recent-attack-card { grid-area: stage; align-self: end; justify-self: center; z-index: 6; width: min(420px, 48vw); margin-bottom: 20px; padding: 12px 16px; border: 1px solid rgba(255,255,255,.54); border-radius: 16px; background: linear-gradient(145deg, rgba(255,255,255,.84), rgba(255,247,224,.76)); box-shadow: 0 16px 38px rgba(0,0,0,.2), inset 0 1px 0 rgba(255,255,255,.72); backdrop-filter: blur(12px); color: var(--ink); cursor: pointer; text-align: left; transition: transform .18s ease, box-shadow .18s ease; }
.recent-attack-card:hover { transform: translateY(-2px); box-shadow: 0 20px 46px rgba(0,0,0,.24), inset 0 1px 0 rgba(255,255,255,.8); }
.recent-attack-card span { display: inline-flex; margin-bottom: 4px; padding: 3px 8px; border-radius: 999px; background: var(--accent-soft); color: var(--accent-dark); font-size: 10px; font-weight: 900; }
.recent-attack-card strong { display: block; font-size: 13px; line-height: 1.35; }
.recent-attack-card small { display: block; margin-top: 4px; color: var(--ink-3); font-family: var(--mono); font-size: 10px; }
.condition-grid { grid-area: conditions; display: grid; grid-template-columns: minmax(0, 1fr) minmax(0, 1fr); gap: 14px; align-items: stretch; } .condition-grid :deep(.app-card) { height: 100%; } .condition-card .section-title-main, .reward .section-title-main { font-size: 13px; } .condition-head { display: flex; align-items: center; justify-content: space-between; gap: 8px; } .checks { display: grid; gap: 4px; margin-top: 7px; } .checks p { margin: 0; display: grid; grid-template-columns: 18px 1fr auto; align-items: center; gap: 7px; padding: 6px 8px; border: 1px solid var(--border); border-radius: 9px; background: var(--surface-alt); font-size: 11px; color: var(--ink-2); } .checks p.completed { background: var(--ok-soft); border-color: var(--ok); } .checks p b { color: var(--accent); font-size: 14px; } .checks p.completed b { color: var(--ok); } .checks p strong { font-family: var(--mono); color: var(--ink); font-size: 10px; }
.reward { background: #fff1c8; border-color: var(--yolk-deep); text-align: center; } .reward-loot { display: grid; grid-template-columns: 1fr 1fr; gap: 8px; margin: 8px 0; } .reward-loot span { padding: 8px; border: 1px solid var(--yolk-deep); border-radius: 10px; background: rgba(255,255,255,.64); font-size: 18px; } .reward small { display: block; font-family: var(--mono); color: var(--ink-2); font-size: 9px; margin-top: 4px; } .reward strong { display: block; font-family: var(--mono); color: var(--accent-dark); font-size: 13px; margin-top: 2px; } .reward-lock { color: var(--ink-2); font-size: 10px; }
.members { grid-area: members; padding: 16px; border-radius: 18px; }
.members :deep(.member) { background: rgba(255,255,255,.74); border-color: rgba(116,75,49,.18); box-shadow: 0 10px 26px rgba(0,0,0,.1); }
.members :deep(.member.mine) { background: rgba(244,255,220,.78); border-color: rgba(143,207,85,.62); }
.members :deep(.member.done:not(.mine)) { background: rgba(232,247,200,.7); }
.member-head { display: flex; justify-content: space-between; align-items: baseline; } .member-head strong { font-size: 13px; } .member-head span { font-family: var(--mono); color: var(--ink-3); font-size: 11px; } .tip { background: var(--surface-alt); color: var(--ink-2); font-size: 11px; line-height: 1.6; }
.quest-board { display: grid; grid-template-columns: minmax(250px, 300px) minmax(0, 1fr); gap: 14px; align-items: stretch; }
.my-quest-panel, .guild-quest-panel { min-width: 0; padding: 12px; border: 1px solid rgba(116,75,49,.16); border-radius: 14px; background: rgba(255,255,255,.44); }
.my-quest-panel { display: flex; flex-direction: column; gap: 10px; }
.guild-quest-panel { display: flex; flex-direction: column; gap: 10px; }
.quest-panel-title { font-size: 12px; font-weight: 900; color: var(--ink); }
.quest-slider-head { display: flex; align-items: center; justify-content: space-between; gap: 10px; }
.quest-slider-controls { display: inline-flex; align-items: center; gap: 8px; }
.quest-slider-controls button { width: 28px; height: 28px; border: 1px solid rgba(116,75,49,.2); border-radius: 50%; background: rgba(255,255,255,.72); color: var(--ink); font-size: 20px; line-height: 1; font-weight: 900; cursor: pointer; }
.quest-slider-controls button:disabled { opacity: .36; cursor: not-allowed; }
.quest-slider-controls span { min-width: 46px; text-align: center; font-family: var(--mono); font-size: 10px; color: var(--ink-3); }
.guild-quest-track { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 10px; }
.quest-entry { display: flex; flex-direction: column; gap: 6px; min-width: 0; } .quest-actions { display: flex; justify-content: flex-end; align-items: center; gap: 6px; }
.quest-missing { min-height: 112px; display: flex; align-items: center; justify-content: center; margin: 0; border: 1px dashed rgba(116,75,49,.2); border-radius: 12px; color: var(--ink-3); font-size: 12px; text-align: center; }
@keyframes clear-pop { from { opacity: 0; transform: translateY(12px) scale(.86); } to { opacity: 1; transform: translateY(0) scale(1); } }
@keyframes sparkle-fall { from { transform: translateY(0) rotate(0); } to { transform: translateY(130px) rotate(120deg); } }
@keyframes boss-hit-shake { 0%,100% { transform: translate3d(0,0,0) rotate(0); filter: drop-shadow(0 28px 34px rgba(0,0,0,.42)); } 15% { transform: translate3d(-6px,2px,0) rotate(-1.2deg); filter: drop-shadow(0 28px 34px rgba(0,0,0,.42)) brightness(1.16); } 32% { transform: translate3d(7px,-2px,0) rotate(1.2deg); } 48% { transform: translate3d(-4px,1px,0) rotate(-.7deg); } 66% { transform: translate3d(3px,0,0) rotate(.4deg); } }
@media (max-width: 960px) { .boss-battle-screen { padding-inline: clamp(14px, 3vw, 28px); } .battle-grid { grid-template-columns: 1fr; grid-template-areas: "stage" "conditions" "members"; } .arena { min-height: clamp(360px, 48vh, 520px); } .hp-card { width: min(500px, 72vw); } .quest-board { grid-template-columns: 1fr; } .guild-quest-track { grid-template-columns: repeat(2, minmax(0, 1fr)); } }
@media (max-width: 640px) { .page-title-row { align-items: flex-start; flex-direction: column; } .boss-battle-screen { margin-top: -30px; padding: 18px 14px 40px; background-attachment: scroll; background-position: 50% 50%; } .boss-battle-screen .api-message, .boss-battle-screen .page-title-row, .battle-grid { width: 100%; } .boss-battle-screen .page-title-row { border-radius: 14px; padding: 14px; } .battle-actions { width: 100%; flex-wrap: wrap; } .battle-actions :deep(button) { flex: 1; justify-content: center; } .sound-toggle { flex: 0 0 auto; } .arena { min-height: 320px; padding: 52px 0 4px; } .arena :deep(.boss-monster) { width: clamp(250px, 82vw, 340px) !important; height: clamp(198px, 65vw, 270px) !important; } .hp-card { width: min(94vw, 420px); margin-top: 24px; } .recent-attack-card { width: min(92vw, 360px); margin-bottom: 4px; } .members { padding: 12px; border-radius: 14px; } .diff-grid,.condition-grid,.guild-quest-track { grid-template-columns: 1fr; } .quest-slider-head { align-items: flex-start; flex-direction: column; } .hp-row,.damage-row { align-items: flex-start; flex-direction: column; } .hp-row :deep(.bar-wrap) { width: 100%; } }
</style>
