<template>
  <section class="analyze-layout">
    <AppCard :padding="0">
      <div class="tabs">
        <button :class="{ active: tab === 'daily' }" @click="tab = 'daily'"><strong>일간 리포트</strong><small>오늘 · 05-15</small></button>
        <button :class="{ active: tab === 'weekly' }" @click="tab = 'weekly'"><strong>주간 리포트</strong><small>이번 주</small></button>
      </div>
      <div v-if="tab === 'daily'" class="report-body">
        <div class="score-card">
          <ScoreRing :value="score" :size="120" />
          <div><div class="mono-label">HEALTH SCORE</div><h2>오늘 {{ score }}점</h2><p>{{ score >= 80 ? '최상위 수준이에요!' : score >= 65 ? '또래 평균 71점보다 살짝 위에요.' : '한 끼만 더 챙겨도 점수가 크게 올라요.' }}</p></div>
        </div>
        <div><h3>끼니 기록 · {{ logs.length }}/4</h3><div class="meal-status"><div v-for="kind in mealKinds" :key="kind.id" :class="{ done: byKind[kind.id]?.length }"><span>{{ kind.emoji }}</span><strong>{{ kind.label }}</strong><small>{{ byKind[kind.id]?.length ? '✓' : '—' }}</small></div></div></div>
        <div><h3>영양소 평균 · 오늘</h3><div class="goal-list"><GoalRow v-for="goal in goals" :key="goal.name" :goal="goal" /></div></div>
      </div>
      <div v-else class="report-body">
        <div class="week-stats"><div v-for="stat in weekStats" :key="stat[0]"><small>{{ stat[0] }}</small><strong>{{ stat[1] }}</strong><span>{{ stat[2] }}</span></div></div>
        <div><h3>일별 건강 점수 추이</h3><div class="week-chart"><div v-for="(v, i) in week" :key="i"><span>{{ v }}</span><b :style="{ height: `${v}%` }" :class="{ today: i === week.length - 1 }"></b><small>{{ ['월','화','수','목','금','토','일'][i] }}</small></div></div></div>
        <AppCard :padding="14" class="ai-report"><div class="ai-head"><strong>🤖 주간 AI 리포트</strong><AppPill tone="ok" size="sm"><AppIcon name="sparkle" :size="10" /> RAG</AppPill></div><p>이번 주 단백질 평균이 <strong>61g</strong>으로 목표(90g)의 68%에 그쳤어요. 매끼 단백질원을 한 가지씩 추가하면 냠냠이의 근력 수치가 올라가요. 채소 섭취는 또래 평균과 비슷한 수준이에요.</p><small>📚 근거: 식품의약품안전처 영양성분 DB · 질병관리청 2023 국민건강통계</small></AppCard>
        <AppCard :padding="14" class="strategy"><div class="section-title-main">다음 주 AI 전략</div><p>"그대, 단백질이 꾸준히 부족하구나. 다음 주엔 매끼 단백질원을 1가지씩 추가하라. 보스 격파 후 잠시 회복기, 무리하지 말지어다."</p></AppCard>
      </div>
    </AppCard>

    <aside class="right-col">
      <AppCard>
        <div class="section-title"><div><div class="section-title-main">① 코치 한마디</div><div class="section-title-sub">{{ coach?.tagline }}</div></div><AppPill tone="accent" size="sm"><AppIcon name="sparkle" :size="10" /> AI 코치</AppPill></div>
        <div class="coach-row"><div class="npc" :style="{ background: coach?.color }">{{ coach?.glyph }}</div><div class="speech-bubble"><strong>{{ coach?.name }}</strong><p>{{ coachSpeak(coachId, logs) }}</p></div></div>
        <div class="coach-picker"><div class="mono-label">코치 변경 ↓</div><button v-for="npc in npcCoaches" :key="npc.id" :class="{ active: coachId === npc.id }" @click="coachId = npc.id"><span :style="{ background: npc.color }">{{ npc.glyph }}</span>{{ npc.name }}</button></div>
      </AppCard>
      <AppCard><div class="section-title"><div><div class="section-title-main">② 오늘의 목표 달성</div><div class="section-title-sub">5개 영양소 · 목표 대비</div></div></div><div class="goal-list"><GoalRow v-for="goal in goals" :key="goal.name" :goal="goal" /></div></AppCard>
      <AppCard><div class="section-title"><div><div class="section-title-main">③ 또래 비교 인사이트</div><div class="section-title-sub">질병관리청 2023 국민건강통계 기반</div></div></div><div class="peer-list"><PeerRow name="단백질" :mine="`${Math.round(totals.p)}g`" peer="74g" :state="totals.p < 74 ? 'low' : 'ok'" /><PeerRow name="나트륨" mine="2,380mg" peer="2,100mg" state="over" /><PeerRow name="채소" :mine="`${veggieCount(logs)}종`" peer="2.3종" :state="veggieCount(logs) < 2 ? 'low' : 'ok'" /></div></AppCard>
    </aside>
  </section>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import AppCard from '../components/common/AppCard.vue'
import AppIcon from '../components/common/AppIcon.vue'
import AppPill from '../components/common/AppPill.vue'
import GoalRow from './parts/GoalRow.vue'
import PeerRow from './parts/PeerRow.vue'
import ScoreRing from './parts/ScoreRing.vue'
import { coachSpeak, goalDefaults, healthScore, mealKinds, npcCoaches, recordsByKind, totalsFor, veggieCount, type MealLog } from '../services/mock/nyamnyamMock'

const props = defineProps<{ logs: MealLog[] }>()
const tab = ref<'daily' | 'weekly'>('daily')
const coachId = ref('knight')
const totals = computed(() => totalsFor(props.logs))
const score = computed(() => healthScore(totals.value))
const byKind = computed(() => recordsByKind(props.logs))
const coach = computed(() => npcCoaches.find((npc) => npc.id === coachId.value))
const goals = computed(() => [
  { name: '칼로리', value: totals.value.kcal, max: goalDefaults.kcal, unit: 'kcal' },
  { name: '단백질', value: totals.value.p, max: goalDefaults.p, unit: 'g' },
  { name: '탄수화물', value: totals.value.c, max: goalDefaults.c, unit: 'g' },
  { name: '지방', value: totals.value.f, max: goalDefaults.f, unit: 'g' },
  { name: '채소', value: veggieCount(props.logs), max: goalDefaults.veggies, unit: '종' }
].map((goal) => ({ ...goal, state: goal.value / goal.max > 1.1 ? 'over' : goal.value / goal.max < .7 ? 'low' : 'ok' })))
const week = [70, 65, 78, 82, 74, 80, 78]
const weekStats = [['평균 점수', '75', '↑ +6 vs 지난주'], ['기록률', '92%', '6/7일 4끼 완수'], ['격파한 보스', '1', '나트륨 크라켄']]
</script>

<style scoped>
.analyze-layout { display: grid; grid-template-columns: 1.15fr 1fr; gap: 20px; }
.tabs { padding: 14px 22px 0; border-bottom: 1px solid var(--border); display: flex; align-items: flex-end; gap: 4px; }
.tabs button { padding: 12px 20px 14px; border: 0; background: transparent; cursor: pointer; border-bottom: 3px solid transparent; margin-bottom: -1px; display: flex; flex-direction: column; gap: 2px; text-align: left; }
.tabs button.active { border-bottom-color: var(--accent); } .tabs strong { font-size: 14px; } .tabs small { font-family: var(--mono); color: var(--ink-3); font-size: 10px; }
.tabs .active small { color: var(--accent); }
.report-body { padding: 22px; display: flex; flex-direction: column; gap: 18px; }
.score-card { display: flex; align-items: center; gap: 22px; padding: 20px; border-radius: 14px; background: linear-gradient(135deg,#fff5e6 0%,#fff 100%); border: 1px solid var(--border); }
h2 { margin: 6px 0; font-size: 22px; } h3 { font-size: 13px; margin: 0 0 10px; } p { margin: 0; color: var(--ink-2); line-height: 1.6; font-size: 12px; }
.meal-status { display: grid; grid-template-columns: repeat(4, 1fr); gap: 8px; }
.meal-status div { padding: 12px 8px; text-align: center; border: 1.5px dashed var(--border); border-radius: 10px; background: var(--surface-alt); } .meal-status .done { border-style: solid; border-color: var(--border-strong); background: var(--surface); }
.meal-status span { font-size: 20px; display: block; } .meal-status strong { display: block; font-size: 11px; margin-top: 4px; } .meal-status small { color: var(--ok); }
.right-col { display: flex; flex-direction: column; gap: 16px; }
.coach-row { display: flex; gap: 14px; align-items: flex-start; }
.npc { width: 56px; height: 56px; border-radius: 28px; border: 1.5px solid var(--border); display: flex; align-items: center; justify-content: center; font-size: 28px; flex: 0 0 56px; }
.speech-bubble { flex: 1; padding: 14px; background: var(--surface-alt); border-radius: 12px; position: relative; } .speech-bubble:before { content: ""; position: absolute; left: -8px; top: 18px; width: 16px; height: 16px; background: var(--surface-alt); transform: rotate(45deg); }
.speech-bubble strong { font-size: 14px; display: block; margin-bottom: 4px; }
.coach-picker { margin-top: 14px; padding-top: 14px; border-top: 1px dashed var(--border); display: flex; flex-wrap: wrap; gap: 6px; } .coach-picker .mono-label { width: 100%; margin-bottom: 2px; }
.coach-picker button { display: inline-flex; align-items: center; gap: 6px; padding: 6px 10px; border: 1.5px solid var(--border); background: var(--surface); border-radius: 999px; font-size: 12px; font-weight: 600; color: var(--ink-2); cursor: pointer; } .coach-picker button.active { border-color: var(--accent); background: var(--accent-soft); color: var(--accent-dark); }
.coach-picker span { width: 20px; height: 20px; border-radius: 10px; display: flex; align-items: center; justify-content: center; font-size: 11px; }
.goal-list, .peer-list { display: flex; flex-direction: column; gap: 12px; }
.week-stats { display: grid; grid-template-columns: repeat(3, 1fr); gap: 10px; } .week-stats div { padding: 14px; border: 1px solid var(--border); border-radius: 10px; } .week-stats small { font-family: var(--mono); color: var(--ink-3); font-size: 10px; } .week-stats strong { display: block; font-family: var(--mono); font-size: 26px; margin-top: 4px; } .week-stats span { font-family: var(--mono); color: var(--ink-3); font-size: 10px; }
.week-chart { display: flex; align-items: flex-end; gap: 10px; height: 140px; } .week-chart div { flex: 1; align-self: stretch; display: flex; flex-direction: column; align-items: center; gap: 6px; justify-content: flex-end; } .week-chart span, .week-chart small { font-family: var(--mono); font-size: 10px; color: var(--ink-3); } .week-chart b { width: 100%; background: var(--ink); border-radius: 6px 6px 0 0; } .week-chart b.today { background: var(--accent); }
.ai-report small { display: block; margin-top: 8px; padding: 6px 10px; background: var(--surface-alt); border-radius: 8px; font-family: var(--mono); color: var(--ink-3); }
.ai-head { display: flex; justify-content: space-between; margin-bottom: 10px; } .strategy { background: var(--accent-soft); border-color: var(--accent); }
</style>
