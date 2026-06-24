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
    <div v-if="successMessage" class="api-message success" :class="{ 'quest-toast': successMessagePlacement === 'quest' }">{{ successMessage }}</div>
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
        <div class="hp-hud">
          <span class="hp-fill" :style="{ width: `${hpPercent}%` }" />
          <strong>{{ hp?.currentHp?.toLocaleString() ?? '-' }} / {{ hp?.maxHp?.toLocaleString() ?? '-' }}</strong>
        </div>
        <button v-if="recentAttack" type="button" class="recent-attack-card" @click="replayRecentAttack">
          <span>최근 공격</span>
          <strong>{{ recentAttack.attackerName }}님이 {{ recentAttack.damage.toLocaleString() }} HP 피해!</strong>
          <small>클릭하면 공격 연출을 다시 볼 수 있어요</small>
        </button>
        <div v-if="bottomPanel === 'summary'" class="bottom-panel summary-panel">
          <div class="bottom-panel-tabs">
            <button type="button" class="active">격파 조건 · 보상</button>
            <button type="button" @click="bottomPanel = 'quests'">퀘스트</button>
          </div>
          <div class="condition-grid">
            <AppCard :padding="10" class="condition-card"><div class="condition-head"><div class="section-title-main">길드 공통 격파 조건</div><AppPill :tone="isBattleCleared ? 'ok' : 'accent'" size="sm">{{ isBattleCleared ? '완료' : '진행 중' }}</AppPill></div><div v-if="battle?.commonConditions?.length" class="checks"><p v-for="condition in battle.commonConditions" :key="condition.battleConditionId" :class="{ completed: condition.completed }"><b>{{ condition.completed ? '✓' : '□' }}</b><span>{{ condition.title }}</span><strong>{{ condition.currentValue ?? '-' }}/{{ condition.targetValue ?? '-' }}{{ conditionUnitLabel(condition.unit) }}</strong></p></div><p v-else>격파 조건 데이터가 없습니다.</p></AppCard>
            <AppCard :padding="10" class="reward"><div class="section-title-main">레이드 클리어 보상</div><div class="reward-loot"><span>✨<small>경험치</small><strong>+{{ bossDetail?.rewardExp?.toLocaleString() ?? '-' }} XP</strong></span><span>🪙<small>코인</small><strong>+{{ bossDetail?.rewardCoin?.toLocaleString() ?? '-' }}</strong></span></div><AppPill v-if="isBattleCleared && battle?.rewardClaimed" tone="ok" size="sm">보상 수령 완료</AppPill></AppCard>
          </div>
        </div>
      </main>
      <aside v-if="bottomPanel === 'quests'" class="members bottom-panel">
        <div class="bottom-panel-tabs">
          <button type="button" @click="bottomPanel = 'summary'">격파 조건 · 보상</button>
          <button type="button" class="active">퀘스트</button>
        </div>
        <div v-if="displayQuests.length === 0" class="quest-empty"><p>생성된 퀘스트가 없습니다.</p><AppButton v-if="isLeader" size="sm" :disabled="pendingActionId === 'generate-quests'" @click="generateQuests">퀘스트 생성</AppButton></div>
        <div v-else class="quest-board">
          <section class="my-quest-panel">
            <div class="quest-panel-title quest-panel-title-row">
              <strong>본인 퀘스트</strong>
            </div>
            <template v-if="myDisplayQuest">
              <div class="my-quest-card">
                <MemberQuest :member="questMember(myDisplayQuest)" />
                <AppButton v-if="myDisplayQuest.status === 'COMPLETED'" class="quest-claim-overlay" size="sm" :disabled="pendingActionId === `reward-${myDisplayQuest.questId}`" @click="claimQuestReward(myDisplayQuest.questId)">보상 수령</AppButton>
              </div>
            </template>
            <p v-else class="quest-missing">내 퀘스트가 아직 없습니다.</p>
          </section>
          <section class="guild-quest-panel">
            <div class="quest-slider-head">
              <div class="quest-panel-title">길드원 퀘스트 <span>{{ dashboard ? `(완료 ${dashboard.questCompletedCount} / 전체 ${dashboard.questTotalCount})` : '' }}</span></div>
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
const successMessagePlacement = ref<'global' | 'quest'>('global')
const pendingActionId = ref<string | null>(null)
const creatingBattle = ref(false)
const creationStepMessage = ref('')
const bottomPanel = ref<'summary' | 'quests'>('summary')
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
  successMessagePlacement.value = 'global'
}

async function runAction(key: string, task: () => Promise<void>, success?: string, placement: 'global' | 'quest' = 'global') {
  clearFeedback()
  pendingActionId.value = key
  try {
    await task()
    if (success) {
      successMessage.value = success
      successMessagePlacement.value = placement
    }
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
  }, '퀘스트 보상을 수령했습니다.', 'quest')
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
    rewarded: quest.status === 'REWARDED',
    idle: quest.status === 'EXPIRED'
  }
}

function difficultyLabel(value: BossDifficulty) { return ({ EASY: '쉬움', NORMAL: '보통', HARD: '어려움' } as Record<BossDifficulty, string>)[value] }
function difficultyTone(value: BossDifficulty): 'ok' | 'accent' | 'bad' { return value === 'EASY' ? 'ok' : value === 'HARD' ? 'bad' : 'accent' }
function statusLabel(value?: string) { return ({ IN_PROGRESS: '진행 중', DEFEATED: '격파 성공', CLEARED: '격파 성공', COMPLETED: '격파 성공', EXPIRED: '기간 종료', FAILED: '실패' } as Record<string, string>)[value || ''] || '데이터 없음' }
function conditionUnitLabel(value?: string) { return value && value !== 'COUNT' ? ` ${value}` : '' }
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
.boss-battle-screen { position: relative; width: 100vw; height: calc(100vh - 72px); min-height: 0; margin: -30px calc(50% - 50vw) -56px; padding: 18px clamp(16px, 4vw, 52px) 24px; overflow: hidden; display: grid; grid-template-rows: auto minmax(0, 1fr); gap: 7px; background-size: cover; background-position: 50% 50%; background-repeat: no-repeat; background-attachment: fixed; box-sizing: border-box; }
.boss-battle-screen:before { content: ""; position: absolute; inset: 0; z-index: 0; background: linear-gradient(180deg, rgba(18, 12, 8, .3), rgba(18, 12, 8, .08) 38%, rgba(18, 12, 8, .34)), radial-gradient(circle at 50% 36%, rgba(255, 232, 146, .2), transparent 36%); pointer-events: none; }
.boss-battle-screen > * { position: relative; z-index: 1; }
.boss-battle-screen .api-message { position: absolute; top: 18px; right: max(24px, 10vw); z-index: 8; width: min(420px, 42vw); margin: 0; background: rgba(255,255,255,.88); backdrop-filter: blur(12px); box-shadow: 0 18px 46px rgba(0,0,0,.16); }
.boss-battle-screen .api-message.success.quest-toast { top: auto; right: auto; left: 50%; bottom: 232px; width: auto; max-width: min(420px, 80vw); transform: translateX(-50%); padding: 9px 16px; border-color: rgba(143,207,85,.72); border-radius: 999px; background: rgba(24, 34, 19, .82); color: #f7ffe7; box-shadow: 0 12px 30px rgba(0,0,0,.24); backdrop-filter: blur(8px); text-align: center; white-space: nowrap; pointer-events: none; }
.boss-battle-screen .page-title-row { width: min(80%, 1440px); margin: 0 auto; padding: 10px 14px; border: 1px solid rgba(255,255,255,.52); border-radius: 14px; background: rgba(255,255,255,.82); backdrop-filter: blur(12px); box-shadow: 0 12px 36px rgba(0,0,0,.18); box-sizing: border-box; }
.boss-battle-screen .page-title-row > div:first-child { flex: 1 1 auto; min-width: 0; }
.boss-battle-screen .page-title-row .pill-row { flex-wrap: nowrap; min-width: 0; overflow: hidden; }
.boss-battle-screen .page-title-row p { max-width: 100%; color: rgba(50, 35, 26, .8); font-weight: 700; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.boss-battle-screen .title-xl { color: var(--ink); text-shadow: 0 1px 0 rgba(255,255,255,.7); }
.battle-grid { width: min(80%, 1440px); height: 100%; min-height: 0; margin: 0 auto; display: grid; grid-template-columns: minmax(0, 1fr); grid-template-rows: minmax(0, 1fr) 204px; grid-template-areas: "stage" "bottom"; gap: 7px; align-items: stretch; position: relative; z-index: 1; }
.battle-left { display: contents; }
.members { display: flex; flex-direction: column; gap: 8px; }
.boss-battle-screen :deep(.app-card), .quest-empty { background: rgba(255,255,255,.66); border: 1px solid rgba(255,255,255,.42); box-shadow: 0 10px 28px rgba(0,0,0,.14); backdrop-filter: blur(10px); }
.boss-battle-screen :deep(.app-card:hover) { border-color: rgba(255,255,255,.68); box-shadow: 0 14px 34px rgba(0,0,0,.18); }
.boss-battle-screen .reward { background: rgba(255,241,200,.82); border-color: rgba(255,199,64,.72); }
.boss-battle-screen .tip { background: rgba(255,255,255,.7); }
.boss-battle-screen .checks p, .boss-battle-screen .reward-loot span { background: rgba(255,255,255,.62); border-color: rgba(116,75,49,.18); }
.boss-battle-screen .checks p.completed { background: rgba(232,247,200,.74); border-color: rgba(98,167,62,.52); }
.arena { grid-area: stage; position: relative; min-height: 0; overflow: visible; background: transparent; border: 0; box-shadow: none; display: flex; align-items: center; justify-content: center; padding: 34px 16px 0; } .arena :deep(.boss-monster) { z-index: 2; width: clamp(300px, 31vw, 430px) !important; height: clamp(238px, 24.6vw, 341px) !important; filter: drop-shadow(0 28px 34px rgba(0,0,0,.42)); }
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
.hp-hud { grid-area: stage; align-self: start; justify-self: center; z-index: 5; position: relative; width: min(500px, 50vw); height: 29px; margin-top: 48px; display: flex; align-items: center; justify-content: center; padding: 0 14px; overflow: hidden; border: 1.5px solid rgba(255,238,188,.74); border-radius: 999px; background: linear-gradient(180deg, rgba(36,16,15,.9), rgba(87,24,22,.82)); box-shadow: 0 10px 22px rgba(0,0,0,.28), inset 0 2px 0 rgba(255,255,255,.22), inset 0 -3px 7px rgba(0,0,0,.32); color: #fff8d8; text-shadow: 0 2px 7px rgba(0,0,0,.9), 0 0 12px rgba(255,229,122,.32); }
.hp-hud .hp-fill { position: absolute; inset: 0 auto 0 0; z-index: 0; width: 0; background: linear-gradient(90deg, #9c1717, #e13a28 48%, #ffb24b); box-shadow: inset 0 2px 0 rgba(255,255,255,.22), inset 0 -5px 12px rgba(70,0,0,.35); transition: width .8s cubic-bezier(.2,.8,.2,1); }
.hp-hud strong { position: relative; z-index: 1; font-family: var(--sans); font-size: clamp(14px, 1.18vw, 18px); font-weight: 700; letter-spacing: .1px; font-variant-numeric: tabular-nums; white-space: nowrap; }
.recent-attack-card { grid-area: stage; align-self: end; justify-self: center; z-index: 6; width: min(400px, 46vw); margin-bottom: 6px; padding: 9px 14px; border: 1px solid rgba(255,255,255,.54); border-radius: 14px; background: linear-gradient(145deg, rgba(255,255,255,.8), rgba(255,247,224,.68)); box-shadow: 0 12px 30px rgba(0,0,0,.18), inset 0 1px 0 rgba(255,255,255,.72); backdrop-filter: blur(12px); color: var(--ink); cursor: pointer; text-align: left; transition: transform .18s ease, box-shadow .18s ease; }
.recent-attack-card:hover { transform: translateY(-2px); box-shadow: 0 20px 46px rgba(0,0,0,.24), inset 0 1px 0 rgba(255,255,255,.8); }
.recent-attack-card span { display: inline-flex; margin-bottom: 4px; padding: 3px 8px; border-radius: 999px; background: var(--accent-soft); color: var(--accent-dark); font-size: 10px; font-weight: 900; }
.recent-attack-card strong { display: block; font-size: 13px; line-height: 1.35; }
.recent-attack-card small { display: block; margin-top: 4px; color: var(--ink-3); font-family: var(--mono); font-size: 10px; }
.bottom-panel { grid-area: bottom; min-height: 0; height: 100%; box-sizing: border-box; display: grid; grid-template-rows: auto minmax(0, 1fr); gap: 8px; padding: 12px 14px 16px; border: 1px solid rgba(255,255,255,.42); border-radius: 14px; background: rgba(255,255,255,.66); box-shadow: 0 10px 28px rgba(0,0,0,.14); backdrop-filter: blur(10px); overflow: hidden; }
.summary-panel { padding: 12px 14px 18px; }
.bottom-panel-tabs { display: grid; grid-template-columns: repeat(2, 120px); width: max-content; gap: 4px; padding: 3px; border-radius: 999px; background: rgba(20,13,8,.18); }
.bottom-panel-tabs button { width: 120px; min-width: 0; min-height: 26px; padding: 0 8px; border: 0; border-radius: 999px; background: transparent; color: var(--ink-2); font-size: 12px; font-weight: 900; white-space: nowrap; cursor: pointer; }
.bottom-panel-tabs button.active { background: rgba(255,255,255,.78); color: var(--ink); box-shadow: 0 3px 10px rgba(0,0,0,.12); }
.condition-grid { min-height: 0; display: grid; grid-template-columns: minmax(0, 1fr) minmax(0, 1fr); gap: 10px; align-items: stretch; } .condition-grid :deep(.app-card) { height: 100%; min-height: 0; box-sizing: border-box; overflow: hidden; } .condition-card .section-title-main, .reward .section-title-main { font-size: 15px; font-weight: 900; } .condition-head { display: flex; align-items: center; justify-content: space-between; gap: 8px; } .checks { display: grid; gap: 3px; margin-top: 5px; } .checks p { margin: 0; display: grid; grid-template-columns: 18px 1fr auto; align-items: center; gap: 7px; padding: 4px 6px; border: 0; border-radius: 7px; background: rgba(255,255,255,.48); font-size: 13px; font-weight: 800; color: var(--ink); } .checks p.completed { background: rgba(232,247,200,.68); border-color: var(--ok); } .checks p b { color: var(--accent); font-size: 15px; } .checks p.completed b { color: var(--ok); } .checks p strong { font-family: var(--mono); color: var(--ink); font-size: 12px; }
.reward { background: rgba(255,241,200,.66); border-color: rgba(255,199,64,.58); text-align: center; display: flex; flex-direction: column; } .reward .section-title-main { font-family: var(--sans); font-size: 17px; font-weight: 850; line-height: 1.1; letter-spacing: 0; color: #4a311f; } .reward-loot { flex: 1; min-height: 0; display: grid; grid-template-columns: 1fr 1fr; gap: 8px; margin: 6px 0 0; } .reward-loot span { min-height: 0; padding: 6px 8px; border: 1px solid rgba(232,185,67,.42); border-radius: 10px; background: linear-gradient(180deg, rgba(255,255,255,.74), rgba(255,244,205,.58)); box-shadow: inset 0 1px 0 rgba(255,255,255,.62), 0 6px 14px rgba(116,75,49,.08); font-family: var(--sans); font-size: 21px; display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 2px; } .reward small { display: block; font-family: var(--sans); color: rgba(74,49,31,.72); font-size: 12px; font-weight: 800; line-height: 1; margin: 0; letter-spacing: 0; } .reward strong { display: block; font-family: var(--sans); color: var(--accent-dark); font-size: 16px; font-weight: 850; line-height: 1.05; margin-top: 2px; letter-spacing: 0; font-variant-numeric: tabular-nums; } .reward-lock { margin-top: auto; color: var(--ink-2); font-size: 12px; font-weight: 800; }
.members { grid-area: bottom; min-height: 0; }
.members :deep(.member) { box-sizing: border-box; padding: 7px 8px; gap: 4px; background: linear-gradient(180deg, rgba(255,255,255,.58), rgba(255,255,255,.34)); border-color: rgba(255,255,255,.34); border-radius: 10px; box-shadow: 0 7px 18px rgba(0,0,0,.13); backdrop-filter: blur(8px); }
.members :deep(.member.mine) { background: rgba(244,255,220,.78); border-color: rgba(143,207,85,.62); }
.members :deep(.member.done:not(.mine)) { background: rgba(232,247,200,.7); }
.members :deep(.member.rewarded) { position: relative; overflow: hidden; }
.members :deep(.member.rewarded:after) { content: "보상 수령 완료"; position: absolute; inset: 0; z-index: 3; display: flex; align-items: center; justify-content: center; border-radius: inherit; background: rgba(16, 12, 10, .42); color: #fff8d8; font-size: 14px; font-weight: 900; text-shadow: 0 2px 8px rgba(0,0,0,.72); }
.members :deep(.top) { gap: 7px; }
.members :deep(.avatar) { width: 28px; height: 28px; border-radius: 14px; }
.members :deep(.who strong) { font-size: 14px; }
.members :deep(.who small) { font-size: 10px; }
.members :deep(.quest span) { font-size: 13px; font-weight: 800; }
.members :deep(.quest small) { font-size: 11px; }
.member-head { display: flex; justify-content: space-between; align-items: baseline; } .member-head strong { font-size: 15px; } .member-head span { font-family: var(--mono); color: var(--ink-3); font-size: 12px; font-weight: 800; } .tip { padding: 6px 10px !important; background: rgba(255,255,255,.46); color: var(--ink-2); font-size: 12px; font-weight: 800; line-height: 1.2; }
.quest-board { height: 100%; min-height: 0; display: grid; grid-template-columns: minmax(230px, 290px) minmax(0, 1fr); gap: 10px; align-items: stretch; }
.my-quest-panel, .guild-quest-panel { min-width: 0; min-height: 0; box-sizing: border-box; padding: 9px; border: 1px solid rgba(255,255,255,.28); border-radius: 10px; background: rgba(255,255,255,.18); overflow: hidden; }
.guild-quest-panel { position: relative; }
.guild-quest-panel:before { content: ""; position: absolute; left: -5px; top: 8px; bottom: 8px; width: 1px; background: rgba(45,33,29,.18); box-shadow: 0 0 8px rgba(255,255,255,.28); }
.my-quest-panel { display: grid; grid-template-rows: 32px minmax(0, 1fr); gap: 5px; }
.guild-quest-panel { display: grid; grid-template-rows: 32px minmax(0, 1fr); gap: 5px; }
.quest-panel-title { font-size: 14px; font-weight: 900; color: var(--ink); }
.quest-panel-title-row { display: flex; align-items: center; justify-content: space-between; gap: 8px; }
.quest-panel-title-row strong { font-size: 14px; font-weight: 900; }
.quest-panel-title-row :deep(button) { min-height: 26px; padding: 0 10px; font-size: 11px; }
.quest-panel-title > span { margin-left: 6px; color: var(--ink-2); font-family: var(--mono); font-size: 11px; font-weight: 800; }
.quest-slider-head { min-height: 32px; display: flex; align-items: center; justify-content: space-between; gap: 10px; }
.quest-slider-controls { display: inline-flex; align-items: center; gap: 8px; }
.quest-slider-controls button { width: 28px; height: 28px; border: 1px solid rgba(116,75,49,.2); border-radius: 50%; background: rgba(255,255,255,.72); color: var(--ink); font-size: 20px; line-height: 1; font-weight: 900; cursor: pointer; }
.quest-slider-controls button:disabled { opacity: .36; cursor: not-allowed; }
.quest-slider-controls span { min-width: 46px; text-align: center; font-family: var(--mono); font-size: 10px; color: var(--ink-3); }
.guild-quest-track { min-height: 0; display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 7px; }
.my-quest-card { position: relative; height: 100%; min-height: 0; }
.my-quest-card :deep(.member) { height: 100%; min-height: 0; }
.quest-claim-overlay { position: absolute; left: 50%; top: 50%; z-index: 4; transform: translate(-50%, -50%); box-shadow: 0 10px 24px rgba(0,0,0,.22); }
.quest-claim-overlay:hover:not(:disabled) { transform: translate(-50%, -50%); }
.quest-claim-overlay:active:not(:disabled) { transform: translate(-50%, -50%) scale(.98); }
.quest-entry { height: 100%; min-height: 0; display: flex; flex-direction: column; gap: 6px; min-width: 0; } .quest-actions { display: flex; justify-content: flex-end; align-items: center; gap: 6px; }
.quest-entry :deep(.member) { height: 100%; min-height: 0; }
.quest-missing { min-height: 112px; display: flex; align-items: center; justify-content: center; margin: 0; border: 0; border-radius: 12px; background: rgba(255,255,255,.22); color: var(--ink-3); font-size: 12px; text-align: center; }
@keyframes clear-pop { from { opacity: 0; transform: translateY(12px) scale(.86); } to { opacity: 1; transform: translateY(0) scale(1); } }
@keyframes sparkle-fall { from { transform: translateY(0) rotate(0); } to { transform: translateY(130px) rotate(120deg); } }
@keyframes boss-hit-shake { 0%,100% { transform: translate3d(0,0,0) rotate(0); filter: drop-shadow(0 28px 34px rgba(0,0,0,.42)); } 15% { transform: translate3d(-6px,2px,0) rotate(-1.2deg); filter: drop-shadow(0 28px 34px rgba(0,0,0,.42)) brightness(1.16); } 32% { transform: translate3d(7px,-2px,0) rotate(1.2deg); } 48% { transform: translate3d(-4px,1px,0) rotate(-.7deg); } 66% { transform: translate3d(3px,0,0) rotate(.4deg); } }
@media (max-width: 960px) { .boss-battle-screen { padding-inline: clamp(14px, 3vw, 28px); } .battle-grid { grid-template-columns: 1fr; grid-template-areas: "stage" "bottom"; } .arena { min-height: 0; padding-top: 30px; } .hp-hud { width: min(470px, 70vw); } .quest-board { grid-template-columns: 1fr; } .guild-quest-track { grid-template-columns: repeat(2, minmax(0, 1fr)); } }
@media (max-width: 640px) { .page-title-row { align-items: flex-start; flex-direction: column; } .boss-battle-screen { margin-top: -30px; padding: 12px 10px 16px; background-attachment: scroll; background-position: 50% 50%; } .boss-battle-screen .api-message { top: 12px; left: 10px; right: 10px; width: auto; } .boss-battle-screen .api-message.success.quest-toast { top: auto; right: auto; left: 50%; bottom: 210px; width: auto; max-width: calc(100vw - 28px); } .boss-battle-screen .page-title-row, .battle-grid { width: 100%; } .boss-battle-screen .page-title-row { border-radius: 12px; padding: 8px 10px; } .battle-grid { height: 100%; grid-template-rows: minmax(0, 1fr) 190px; gap: 6px; } .battle-actions { width: 100%; flex-wrap: wrap; } .battle-actions :deep(button) { flex: 1; justify-content: center; } .sound-toggle { flex: 0 0 auto; } .arena { min-height: 0; padding: 42px 0 0; } .arena :deep(.boss-monster) { width: clamp(230px, 76vw, 320px) !important; height: clamp(182px, 60vw, 254px) !important; } .hp-hud { width: min(86vw, 350px); height: 26px; margin-top: 32px; padding: 0 10px; } .recent-attack-card { width: min(92vw, 360px); margin-bottom: 2px; } .bottom-panel { gap: 6px; padding: 8px 8px 12px; border-radius: 12px; } .summary-panel { padding: 8px 8px 12px; } .bottom-panel-tabs { grid-template-columns: repeat(2, 112px); } .bottom-panel-tabs button { width: 112px; font-size: 11px; } .diff-grid,.condition-grid,.guild-quest-track { grid-template-columns: 1fr; } .quest-slider-head { align-items: flex-start; flex-direction: column; gap: 4px; } }
</style>
