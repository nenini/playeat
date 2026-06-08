<template>
  <section v-if="phase === 'lobby'">
    <div class="page-title-row">
      <div><div class="pill-row"><AppPill tone="bad" size="sm">BOSS LOBBY</AppPill><AppPill size="sm">D-{{ boss.dDay }}</AppPill><AppPill size="sm">잘먹잘싸 길드</AppPill></div><div class="title-xl">{{ boss.name }} <span>입장방</span></div><p>난이도를 선택하고 길드원과 함께 전투를 시작하세요</p></div>
      <div class="start-box">
        <AppButton size="lg" :disabled="!isLeader" @click="startBattle">⚔️ 전투 시작!</AppButton>
        <div v-if="!isLeader" class="leader-help">길드장만 전투를 시작할 수 있어요</div>
      </div>
    </div>
    <div class="boss-lobby">
      <div class="boss-art"><BossMonster :size="220" :hp="60" /><strong>{{ boss.name }}</strong><p>{{ boss.description }}</p></div>
      <div><div class="mono-label diff-label">난이도 선택</div><div class="diff-grid"><button v-for="diff in bossDiffs" :key="diff.id" :class="[diff.id, { active: difficulty === diff.id }]" @click="difficulty = diff.id"><strong>{{ diff.label }}</strong><p>{{ diff.desc }}</p><AppPill :tone="diff.tone" size="sm">{{ diff.reward }}</AppPill></button></div></div>
      <AppCard :padding="14" class="diff-summary"><div v-for="item in diffSummary" :key="item[0]"><small>{{ item[0] }}</small><strong>{{ item[1] }}</strong></div></AppCard>
    </div>
  </section>
  <section v-else>
    <div class="page-title-row">
      <div><div class="pill-row"><AppPill tone="bad" size="sm">GUILD BOSS · {{ difficulty.toUpperCase() }}</AppPill><AppPill size="sm">D-{{ boss.dDay }}</AppPill><AppPill size="sm">잘먹잘싸 길드</AppPill></div><div class="title-xl">{{ boss.name }}</div><p>출현 2026·05·15 · 격파 마감까지 D-{{ boss.dDay }} · 길드원 6명 참전 중</p></div>
      <div class="battle-actions"><AppButton @click="$emit('navigate', 'meals')"><AppIcon name="plus" color="#fff" />식단 기록 · 데미지!</AppButton></div>
    </div>
    <div class="battle-grid">
      <main class="battle-left">
        <div class="arena"><div class="grid-bg"></div><BossMonster :size="300" :hp="hp" /></div>
        <AppCard :padding="16"><div class="hp-row"><span>BOSS HP</span><ProgressBar :value="hp" :max="100" :tone="hp < 30 ? 'accent' : 'bad'" :height="18" /><b>{{ hp }} / 100</b></div><div class="damage-row"><span>이번 주 데미지: <strong>−{{ damage + 108 }}</strong> HP</span><span>마지막 데미지: <strong>−8</strong> (지영 · 채소 3종)</span></div></AppCard>
        <div class="condition-grid">
          <AppCard :padding="16"><div class="section-title-main">격파 조건</div><div class="checks"><p>✓ 당류 ≤ 50g/일 — 4일 연속 (3/4 완료)</p><p>□ 가공음료 0회 — 4일 (1일 남음)</p><p>□ 채소 일 2종 이상 — 5일 (3일 남음)</p></div></AppCard>
          <AppCard :padding="16" class="reward"><div class="section-title-main">격파 보상</div><div><small>+ 길드원 전원 XP</small><strong>800</strong><small>+ 길드 칭호</small><strong>당분 사냥꾼</strong><small>+ 냠냠이 의상</small><strong>1종</strong></div></AppCard>
        </div>
      </main>
      <aside class="members"><div class="member-head"><strong>길드원 개인 퀘스트 · 6명</strong><span>✓ 2 · 진행 3 · 대기 1</span></div><MemberQuest v-for="member in members" :key="member.id" :member="member" /><AppCard :padding="12" class="tip">💡 길드원이 자기 퀘스트를 깰 때마다 보스 HP가 감소해요.</AppCard></aside>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import AppButton from '../components/common/AppButton.vue'
import AppCard from '../components/common/AppCard.vue'
import AppIcon from '../components/common/AppIcon.vue'
import AppPill from '../components/common/AppPill.vue'
import ProgressBar from '../components/common/ProgressBar.vue'
import BossMonster from '../components/nyamnyam/BossMonster.vue'
import MemberQuest from './parts/MemberQuest.vue'
import { boss, bossDiffs, totalsFor, type MealLog, type PageId } from '../services/mock/nyamnyamMock'

const props = defineProps<{ logs: MealLog[], isLeader?: boolean }>()
defineEmits<{ navigate: [page: PageId] }>()
const phase = ref<'lobby' | 'battle'>('lobby')
const difficulty = ref('normal')
const diff = computed(() => bossDiffs.find((item) => item.id === difficulty.value) || bossDiffs[1])
const diffSummary = computed(() => [['BOSS HP', diff.value.hp], ['보상 배율', diff.value.rewardMult], ['격파 XP', diff.value.id === 'hard' ? 2400 : diff.value.id === 'normal' ? 1200 : 800]])
const damage = computed(() => props.logs.length * 5)
const hp = computed(() => Math.max(0, boss.baseHP - damage.value))
const protein = computed(() => Math.round(totalsFor(props.logs).p))
const members = computed(() => [
  { id: 'me', name: '지은 (나)', role: '단백질 담당', lv: 7, quest: '단백질 60g 달성', progress: protein.value, total: 60, unit: 'g', mine: true, done: protein.value >= 60 },
  { id: 'minjun', name: '민준', role: '단백질 담당', lv: 12, quest: '단백질 90g 달성', progress: 90, total: 90, unit: 'g', done: true },
  { id: 'jiyoung', name: '지영', role: '채소 담당', lv: 14, quest: '채소 3종 이상', progress: 3, total: 3, unit: '종', done: true },
  { id: 'youngsook', name: '영숙', role: '나트륨 담당', lv: 9, quest: '나트륨 ≤ 2,000mg', progress: 2380, total: 2000, unit: 'mg', over: true },
  { id: 'yein', name: '예인', role: '기록 담당', lv: 18, quest: '4끼 모두 기록', progress: props.logs.length, total: 4, unit: '끼' },
  { id: 'taehyung', name: '태형', role: '무관', lv: 5, quest: '오늘 식단 기록', progress: 0, total: 1, unit: '끼', idle: true }
])
function startBattle() {
  if (!props.isLeader) return
  phase.value = 'battle'
}
</script>

<style scoped>
.pill-row { display: flex; gap: 8px; margin-bottom: 6px; } .title-xl span { color: var(--ink-3); } p { margin: 4px 0 0; color: var(--ink-2); font-size: 13px; }
.boss-lobby { display: flex; flex-direction: column; gap: 14px; }
.boss-art { border-radius: 18px; overflow: hidden; padding: 36px 32px; background: linear-gradient(135deg,#fff5e0 0%,#fbe5d3 50%,#f6c098 100%); border: 1.5px solid var(--border); box-shadow: var(--shadow-lg); display: flex; flex-direction: column; align-items: center; gap: 12px; }
.boss-art strong { font-size: 20px; font-weight: 900; } .boss-art p { max-width: 400px; text-align: center; line-height: 1.6; font-size: 12px; }
.diff-label { margin-bottom: 10px; } .diff-grid { display: grid; grid-template-columns: repeat(3,1fr); gap: 10px; }
.diff-grid button { padding: 18px 14px; text-align: center; border: 2px solid var(--border); background: var(--surface); border-radius: 14px; cursor: pointer; } .diff-grid strong { font-family: var(--mono); font-size: 14px; display: block; margin-bottom: 6px; } .diff-grid .easy strong { color: var(--ok); } .diff-grid .normal strong { color: var(--accent); } .diff-grid .hard strong { color: var(--bad); } .diff-grid .easy.active { background: var(--ok-soft); border-color: var(--ok); } .diff-grid .normal.active { background: var(--accent-soft); border-color: var(--accent); } .diff-grid .hard.active { background: var(--bad-soft); border-color: var(--bad); }
.diff-summary { background: var(--surface-alt); } .diff-summary :deep(> div) { display: flex; gap: 16px; } .diff-summary div div { flex: 1; text-align: center; } .diff-summary small { font-family: var(--mono); color: var(--ink-3); font-size: 10px; } .diff-summary strong { display: block; font-family: var(--mono); font-size: 22px; margin-top: 4px; }
.back { display: inline-flex; padding: 2px 8px; border: 1px solid var(--border); border-radius: 6px; background: var(--surface); font-size: 11px; color: var(--ink-2); cursor: pointer; }
.battle-actions { display: flex; gap: 8px; }
.leader-help { margin-top: 6px; font-family: var(--mono); font-size: 11px; color: var(--ink-3); text-align: right; }
.battle-grid { display: grid; grid-template-columns: 1fr 440px; gap: 20px; } .battle-left, .members { display: flex; flex-direction: column; gap: 14px; }
.arena { position: relative; height: 380px; border-radius: 18px; overflow: hidden; background: linear-gradient(135deg,#fff5e0 0%,#fbe5d3 50%,#f6c098 100%); border: 1.5px solid var(--border); box-shadow: var(--shadow-lg); display: flex; align-items: center; justify-content: center; } .grid-bg { position: absolute; inset: 0; opacity: .16; background-image: linear-gradient(var(--ink) 1px, transparent 1px), linear-gradient(90deg,var(--ink) 1px, transparent 1px); background-size: 40px 40px; }
.hp-row { display: flex; align-items: center; gap: 14px; } .hp-row span { font-family: var(--mono); font-size: 11px; color: var(--ink-3); font-weight: 700; letter-spacing: 1px; } .hp-row :deep(.bar-wrap) { flex: 1; } .hp-row b { font-family: var(--mono); font-size: 18px; color: var(--accent); min-width: 90px; text-align: right; }
.damage-row { margin-top: 10px; padding-top: 10px; border-top: 1px dashed var(--border); display: flex; justify-content: space-between; font-family: var(--mono); font-size: 11px; color: var(--ink-3); } .damage-row strong { color: var(--accent); }
.condition-grid { display: grid; grid-template-columns: 1.3fr 1fr; gap: 12px; } .checks p { font-size: 12px; color: var(--ink-2); }
.reward { background: var(--accent-soft); border-color: var(--accent); } .reward small { display: block; font-family: var(--mono); color: var(--ink-2); font-size: 10px; margin-top: 8px; } .reward strong { font-family: var(--mono); color: var(--accent-dark); font-size: 13px; }
.member-head { display: flex; justify-content: space-between; align-items: baseline; } .member-head strong { font-size: 13px; } .member-head span { font-family: var(--mono); color: var(--ink-3); font-size: 11px; } .tip { background: var(--surface-alt); color: var(--ink-2); font-size: 11px; line-height: 1.6; }
</style>
