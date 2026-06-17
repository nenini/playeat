<template>
  <section>
  <div class="page-title-row">
    <div><div class="mono-label">ANALYZE · 영양 분석</div><div class="title-lg">식단 리포트</div></div>
    <div class="date-box">
      <AppButton variant="ghost" size="sm" @click="shiftDay(-1)"><AppIcon name="chev-l" :size="14" /></AppButton>
      <button class="date-open" type="button" @click="showCalendar = !showCalendar">{{ selectedDateLabel }}</button>
      <input v-if="showCalendar" v-model="selectedDate" class="calendar-pop" type="date" :max="todayDate" @change="syncDateFromCalendar">
      <AppButton variant="ghost" size="sm" :disabled="!canGoForward" @click="shiftDay(1)"><AppIcon name="chev-r" :size="14" /></AppButton>
    </div>
  </div>
  <div class="analyze-layout">
    <AppCard :padding="0">
      <div class="tabs">
        <button :class="{ active: tab === 'daily' }" @click="tab = 'daily'"><strong>일간 리포트</strong><small>{{ yesterdayLabel }}</small></button>
        <button :class="{ active: tab === 'weekly' }" @click="tab = 'weekly'"><strong>주간 리포트</strong><small>{{ previousWeekRange }}</small></button>
      </div>
      <div v-if="tab === 'daily'" class="report-body">
        <div class="score-card">
          <ScoreRing :value="score" :size="120" />
          <div><div class="mono-label">DAILY REPORT · 어제 식단 기반</div><h2>{{ yesterdayLabel }} 리포트</h2><div class="report-sections"><div v-for="section in dailyReportSections" :key="section.title" class="report-section"><strong>{{ section.title }}</strong><p>{{ section.text }}</p></div></div></div>
        </div>
        <div><h3>끼니 기록 · {{ logs.length }}/4</h3><div class="meal-status"><div v-for="kind in mealKinds" :key="kind.id" :class="{ done: byKind[kind.id]?.length }"><span>{{ kind.emoji }}</span><strong>{{ kind.label }}</strong><small>{{ byKind[kind.id]?.length ? '✓' : '—' }}</small></div></div></div>
        <div><h3>영양소 그래프 · 어제</h3><div class="goal-list"><GoalRow v-for="goal in goals" :key="goal.name" :goal="goal" /></div></div>
      </div>
      <div v-else class="report-body">
        <div class="week-stats"><div v-for="stat in weekStats" :key="stat[0]"><small>{{ stat[0] }}</small><strong>{{ stat[1] }}</strong><span>{{ stat[2] }}</span></div></div>
        <div><h3>일별 건강 점수 추이</h3><div class="week-chart"><div v-for="(v, i) in week" :key="i"><span>{{ v }}</span><b :style="{ height: `${v}%` }" :class="{ today: i === week.length - 1 }"></b><small>{{ ['월','화','수','목','금','토','일'][i] }}</small></div></div></div>
        <AppCard :padding="14" class="ai-report"><div class="ai-head"><strong>🤖 주간 AI 리포트</strong><AppPill tone="ok" size="sm"><AppIcon name="sparkle" :size="10" /> RAG</AppPill></div><div class="report-sections"><div v-for="section in weeklyReportSections" :key="section.title" class="report-section"><strong>{{ section.title }}</strong><p>{{ section.text }}</p></div></div></AppCard>
        <AppCard :padding="14" class="strategy"><div class="section-title-main">다음 주 식단 전략</div><p>다음 주에는 매끼 단백질원을 1가지씩 추가하고, 찌개·라면처럼 나트륨이 높은 메뉴는 주 2회 이하로 줄여보세요. 부족한 날엔 닭가슴살, 두부, 그릭 요거트로 보충하는 전략이 좋아요.</p></AppCard>
      </div>
    </AppCard>

    <aside class="right-col">
      <AppCard>
        <div class="section-title"><div><div class="section-title-main">① 코치 한마디</div><div class="section-title-sub">{{ coach?.tagline }}</div></div><AppPill tone="accent" size="sm"><AppIcon name="sparkle" :size="10" /> AI 코치</AppPill></div>
        <div class="coach-row"><div class="npc" :style="{ background: coach?.color }">{{ coach?.glyph }}</div><div class="speech-bubble"><strong>{{ coach?.name }}</strong><p>{{ coachSpeak(coachId, logs) }}</p></div></div>
        <div class="coach-picker"><div class="mono-label">코치 변경 ↓</div><button v-for="npc in npcCoaches" :key="npc.id" :class="{ active: coachId === npc.id }" @click="coachId = npc.id"><span :style="{ background: npc.color }">{{ npc.glyph }}</span>{{ npc.name }}</button></div>
      </AppCard>
      <AppCard><div class="section-title"><div><div class="section-title-main">② 오늘의 목표 달성</div><div class="section-title-sub">4개 영양소 · 목표 대비</div></div></div><div class="goal-list"><GoalRow v-for="goal in goals" :key="goal.name" :goal="goal" /></div></AppCard>
      <AppCard><div class="section-title"><div><div class="section-title-main">③ 또래 비교 인사이트</div><div class="section-title-sub">질병관리청 2023 국민건강통계 기반</div></div></div><div class="peer-list"><PeerRow name="단백질" :mine="`${Math.round(totals.p)}g`" peer="74g" :state="totals.p < 74 ? 'low' : 'ok'" /><PeerRow name="나트륨" mine="2,380mg" peer="2,100mg" state="over" /></div></AppCard>
    </aside>
  </div>
  </section>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import AppCard from '../components/common/AppCard.vue'
import AppButton from '../components/common/AppButton.vue'
import AppIcon from '../components/common/AppIcon.vue'
import AppPill from '../components/common/AppPill.vue'
import GoalRow from './parts/GoalRow.vue'
import PeerRow from './parts/PeerRow.vue'
import ScoreRing from './parts/ScoreRing.vue'
import { coachSpeak, goalDefaults, healthScore, mealKinds, npcCoaches, recordsByKind, totalsFor, type MealLog } from '../services/mock/nyamnyamMock'

const props = defineProps<{ logs: MealLog[] }>()
const tab = ref<'daily' | 'weekly'>('daily')
const coachId = ref('knight')
const selectedDate = ref('2026-05-15')
const showCalendar = ref(false)
const totals = computed(() => totalsFor(props.logs))
const score = computed(() => healthScore(totals.value))
const byKind = computed(() => recordsByKind(props.logs))
const coach = computed(() => npcCoaches.find((npc) => npc.id === coachId.value))
const goals = computed(() => [
  { name: '칼로리', value: totals.value.kcal, max: goalDefaults.kcal, unit: 'kcal' },
  { name: '단백질', value: totals.value.p, max: goalDefaults.p, unit: 'g' },
  { name: '탄수화물', value: totals.value.c, max: goalDefaults.c, unit: 'g' },
  { name: '지방', value: totals.value.f, max: goalDefaults.f, unit: 'g' }
].map((goal) => ({ ...goal, state: goal.value / goal.max > 1.1 ? 'over' : goal.value / goal.max < .7 ? 'low' : 'ok' })))
const week = [70, 65, 78, 82, 74, 80, 78]
const weekStats = [['평균 점수', '75', '↑ +6 vs 지난주'], ['기록률', '92%', '6/7일 4끼 완수'], ['격파한 보스', '1', '나트륨 크라켄']]
const selectedDateLabel = computed(() => {
  const date = new Date(`${selectedDate.value}T00:00:00`)
  return `${date.getFullYear()}년 ${date.getMonth() + 1}월 ${date.getDate()}일`
})
const todayDate = toDateInputValue(new Date())
const canGoForward = computed(() => selectedDate.value < todayDate)
const yesterdayLabel = computed(() => {
  const date = new Date(`${selectedDate.value}T00:00:00`)
  date.setDate(date.getDate() - 1)
  return `${date.getMonth() + 1}월 ${date.getDate()}일`
})
const previousWeekRange = computed(() => {
  const current = new Date(`${selectedDate.value}T00:00:00`)
  const day = current.getDay() || 7
  const start = new Date(current)
  start.setDate(current.getDate() - day - 6)
  const end = new Date(start)
  end.setDate(start.getDate() + 6)
  return `${start.getMonth() + 1}월 ${start.getDate()}일 ~ ${end.getMonth() + 1}월 ${end.getDate()}일`
})
const dailyReportSections = computed(() => {
  if (totals.value.p < goalDefaults.p * 0.7) return [
    { title: '요약', text: '어제는 단백질이 목표보다 부족했어요. 오늘은 첫 끼부터 단백질 식품을 먼저 배치해보세요.' },
    { title: '강점', text: '끼니 기록 흐름은 유지되고 있어요. 기록을 이어가는 습관 자체가 가장 큰 자산이에요.' },
    { title: '경고', text: '단백질 부족이 이어지면 포만감이 빨리 떨어질 수 있어요. 간식도 단백질 중심으로 고르면 좋아요.' }
  ]
  if (totals.value.kcal > goalDefaults.kcal) return [
    { title: '요약', text: '어제는 총 섭취 열량이 목표를 조금 넘었어요. 오늘은 음료와 간식 양을 먼저 점검해보세요.' },
    { title: '강점', text: '기록이 충분히 쌓여 어느 시간대에 섭취가 늘어나는지 파악하기 쉬워졌어요.' },
    { title: '경고', text: '열량 초과가 반복되면 목표 달성이 늦어질 수 있어요. 한 끼는 가볍게 조절해보세요.' }
  ]
  return [
    { title: '요약', text: '어제 식단은 전반적으로 안정적이에요. 오늘도 끼니 기록을 유지하면 좋아요.' },
    { title: '강점', text: '칼로리와 주요 영양소 흐름이 큰 폭으로 흔들리지 않았어요.' },
    { title: '경고', text: '기록이 비는 끼니가 생기면 분석 정확도가 떨어져요. 간단한 메뉴라도 남겨주세요.' }
  ]
})
const weeklyReportSections = [
  { title: '요약', text: '이번 주 단백질 평균은 61g으로 목표보다 낮았고, 외식이 있던 날에는 나트륨이 높게 나타났어요.' },
  { title: '강점', text: '기록률이 높아 식사 패턴을 안정적으로 파악할 수 있었어요. 주중 후반으로 갈수록 점수도 회복됐어요.' },
  { title: '경고', text: '단백질 부족과 나트륨 초과가 반복되면 다음 주 컨디션 관리가 어려워질 수 있어요.' }
]
function shiftDay(delta: number) {
  const date = new Date(`${selectedDate.value}T00:00:00`)
  date.setDate(date.getDate() + delta)
  const nextDate = toDateInputValue(date)
  selectedDate.value = nextDate > todayDate ? todayDate : nextDate
}
function syncDateFromCalendar() {
  if (selectedDate.value > todayDate) selectedDate.value = todayDate
  showCalendar.value = false
}
function toDateInputValue(date: Date) {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}
</script>

<style scoped>
.analyze-layout { display: grid; grid-template-columns: 1.15fr 1fr; gap: 20px; }
.date-box { position: relative; display: flex; align-items: center; gap: 4px; background: var(--surface); border: 1px solid var(--border); border-radius: 12px; padding: 4px 6px; box-shadow: var(--shadow); }
.date-open { border: 0; background: transparent; padding: 6px 14px; font-family: var(--mono); font-size: 13px; font-weight: 800; min-width: 200px; text-align: center; cursor: pointer; color: var(--ink); }
.calendar-pop { position: absolute; right: 44px; top: 44px; z-index: 10; border: 1px solid var(--border-strong); border-radius: 10px; padding: 8px; box-shadow: var(--shadow-lg); }
.tabs { padding: 14px 22px 0; border-bottom: 1px solid var(--border); display: flex; align-items: flex-end; gap: 4px; }
.tabs button { padding: 12px 20px 14px; border: 0; background: transparent; cursor: pointer; border-bottom: 3px solid transparent; margin-bottom: -1px; display: flex; flex-direction: column; gap: 2px; text-align: left; }
.tabs button.active { border-bottom-color: var(--accent); } .tabs strong { font-size: 14px; } .tabs small { font-family: var(--mono); color: var(--ink-3); font-size: 10px; }
.tabs .active small { color: var(--accent); }
.report-body { padding: 22px; display: flex; flex-direction: column; gap: 18px; }
.score-card { display: flex; align-items: center; gap: 22px; padding: 20px; border-radius: 14px; background: linear-gradient(135deg,#fff5e6 0%,#fff 100%); border: 1px solid var(--border); }
h2 { margin: 6px 0; font-size: 22px; } h3 { font-size: 13px; margin: 0 0 10px; } p { margin: 0; color: var(--ink-2); line-height: 1.6; font-size: 12px; }
.report-sections { display: grid; gap: 8px; margin-top: 10px; }
.report-section { padding: 10px 12px; border: 1px solid var(--border); border-radius: 10px; background: rgba(255,255,255,.72); }
.report-section strong { display: block; margin-bottom: 4px; font-size: 13px; color: var(--ink); }
.report-section p { color: var(--ink); font-size: 15px; line-height: 1.65; font-weight: 700; word-break: keep-all; }
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
.ai-head { display: flex; justify-content: space-between; margin-bottom: 10px; } .strategy { background: var(--accent-soft); border-color: var(--accent); }
</style>
