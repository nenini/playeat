<template>
  <section>
    <div class="page-title-row">
      <div>
        <div class="mono-label">ANALYZE · 영양 분석</div>
        <div class="title-lg">식단 리포트</div>
      </div>
      <div class="date-box">
        <AppButton variant="ghost" size="sm" @click="shiftDay(-1)">
          <AppIcon name="chev-l" :size="14" />
        </AppButton>
        <button class="date-open" type="button" @click="showCalendar = !showCalendar">{{ selectedDateLabel }}</button>
        <input
          v-if="showCalendar"
          v-model="selectedDate"
          class="calendar-pop"
          type="date"
          :max="todayDate"
          @change="syncDateFromCalendar"
        >
        <AppButton variant="ghost" size="sm" :disabled="!canGoForward" @click="shiftDay(1)">
          <AppIcon name="chev-r" :size="14" />
        </AppButton>
      </div>
    </div>

    <div class="analyze-layout">
      <AppCard :padding="0">
        <div class="tabs">
          <button :class="{ active: tab === 'daily' }" @click="tab = 'daily'">
            <strong>일간 리포트</strong>
            <small>{{ selectedDateLabel }}</small>
          </button>
          <button :class="{ active: tab === 'weekly' }" @click="tab = 'weekly'">
            <strong>주간 리포트</strong>
            <small>{{ weekRangeLabel }}</small>
          </button>
        </div>

        <div v-if="tab === 'daily'" class="report-body">
          <div class="score-card">
            <ScoreRing v-if="dailyAnalysis" :value="score" :size="120" />
            <div v-else class="empty-score">{{ dailyLoading ? '...' : '-' }}</div>
            <div>
              <div class="mono-label">DAILY REPORT · 선택 날짜 기준</div>
              <h2>{{ selectedDateLabel }} 리포트</h2>
              <div class="report-sections">
                <div v-for="section in dailyReportSections" :key="section.title" class="report-section">
                  <strong>{{ section.title }}</strong>
                  <p>{{ section.text }}</p>
                </div>
              </div>
            </div>
          </div>

          <div>
            <h3>끼니 기록 · {{ recordedMealCount }}/4</h3>
            <div class="meal-status">
              <div v-for="kind in mealKinds" :key="kind.id" :class="{ done: mealRecorded[kind.id] }">
                <span>{{ kind.emoji }}</span>
                <strong>{{ kind.label }}</strong>
                <small>{{ mealRecorded[kind.id] ? '기록' : '미기록' }}</small>
              </div>
            </div>
          </div>

          <div>
            <h3>영양소 그래프 · 선택 날짜</h3>
            <div v-if="goals.length" class="goal-list">
              <GoalRow v-for="goal in goals" :key="goal.name" :goal="goal" />
            </div>
            <p v-else class="empty-text">{{ dailyLoading ? '영양 분석을 불러오는 중입니다.' : '영양 분석 데이터가 없습니다.' }}</p>
          </div>
        </div>

        <div v-else class="report-body">
          <div class="week-stats">
            <div v-for="stat in weekStats" :key="stat[0]">
              <small>{{ stat[0] }}</small>
              <strong>{{ stat[1] }}</strong>
              <span>{{ stat[2] }}</span>
            </div>
          </div>

          <div>
            <h3>일별 건강 점수 추이</h3>
            <div v-if="week.length" class="week-chart">
              <div v-for="(v, i) in week" :key="i">
                <span>{{ v }}</span>
                <b :style="{ height: `${v}%` }" :class="{ today: i === week.length - 1 }"></b>
                <small>{{ weeklyDayLabels[i] }}</small>
              </div>
            </div>
            <p v-else class="empty-text">{{ weeklyLoading ? '주간 분석을 불러오는 중입니다.' : '주간 분석 데이터가 없습니다.' }}</p>
          </div>

          <AppCard :padding="14" class="ai-report">
            <div class="ai-head">
              <strong>주간 AI 리포트</strong>
              <AppPill tone="ok" size="sm"><AppIcon name="sparkle" :size="10" /> AI</AppPill>
            </div>
            <div class="report-sections">
              <div v-for="section in weeklyReportSections" :key="section.title" class="report-section">
                <strong>{{ section.title }}</strong>
                <p>{{ section.text }}</p>
              </div>
            </div>
          </AppCard>

          <AppCard :padding="14" class="strategy">
            <div class="section-title-main">다음 주 식단 전략</div>
            <p>{{ nextWeekStrategy }}</p>
          </AppCard>
        </div>
      </AppCard>

      <aside class="right-col">
        <AppCard>
          <div class="section-title">
            <div>
              <div class="section-title-main">코치 한마디</div>
              <div class="section-title-sub">{{ coach?.tagline }}</div>
            </div>
            <AppPill tone="accent" size="sm"><AppIcon name="sparkle" :size="10" /> AI 코치</AppPill>
          </div>
          <div class="coach-row">
            <div class="npc" :style="{ background: coach?.color }">{{ coach?.glyph }}</div>
            <div class="speech-bubble">
              <strong>{{ apiCoachName }}</strong>
              <p>{{ coachMessage }}</p>
            </div>
          </div>
          <div class="coach-picker">
            <div class="mono-label">코치 변경</div>
            <button v-for="npc in npcCoaches" :key="npc.id" :class="{ active: coachId === npc.id }" @click="selectCoach(npc.id)">
              <span :style="{ background: npc.color }">{{ npc.glyph }}</span>{{ npc.name }}
            </button>
          </div>
        </AppCard>

        <AppCard>
          <div class="section-title">
            <div>
              <div class="section-title-main">오늘의 목표 달성</div>
              <div class="section-title-sub">공식 기준 기반 목표 대비</div>
            </div>
          </div>
          <div v-if="goals.length" class="goal-list">
            <GoalRow v-for="goal in goals" :key="goal.name" :goal="goal" />
          </div>
          <p v-else class="empty-text">{{ dailyLoading ? '목표 달성률을 불러오는 중입니다.' : '목표 달성 데이터가 없습니다.' }}</p>
        </AppCard>

        <AppCard>
          <div class="section-title">
            <div>
              <div class="section-title-main">또래 영양 비교</div>
              <div class="section-title-sub">동성·나이대 기준 비교</div>
            </div>
          </div>
          <div v-if="standardInsightRows.length" class="peer-list">
            <PeerRow
              v-for="insight in standardInsightRows"
              :key="insight.name"
              :name="insight.name"
              :mine="insight.mine"
              :peer="insight.standard"
              :state="insight.state"
            />
          </div>
          <p v-else class="empty-text">{{ dailyLoading ? '인사이트를 불러오는 중입니다.' : '기준 대비 인사이트가 없습니다.' }}</p>
        </AppCard>
      </aside>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import AppCard from '../components/common/AppCard.vue'
import AppButton from '../components/common/AppButton.vue'
import AppIcon from '../components/common/AppIcon.vue'
import AppPill from '../components/common/AppPill.vue'
import GoalRow from './parts/GoalRow.vue'
import PeerRow from './parts/PeerRow.vue'
import ScoreRing from './parts/ScoreRing.vue'
import { mealKinds, npcCoaches, type MealKindId, type MealLog } from '../services/mock/nyamnyamMock'
import { analysisApi, type AiReportResponse, type AnalysisDailyResponse, type AnalysisWeeklyResponse } from '../services/analysisApi'
import { coachApi, type CoachResponse } from '../services/coachApi'

defineProps<{ logs: MealLog[] }>()

const tab = ref<'daily' | 'weekly'>('daily')
const coachId = ref('knight')
const apiCoaches = ref<CoachResponse[]>([])
const selectedDate = ref(toDateInputValue(new Date()))
const showCalendar = ref(false)
const dailyAnalysis = ref<AnalysisDailyResponse | null>(null)
const weeklyAnalysis = ref<AnalysisWeeklyResponse | null>(null)
const dailyLoading = ref(false)
const weeklyLoading = ref(false)
const dailyError = ref('')
const weeklyError = ref('')
let dailyRequestId = 0
let weeklyRequestId = 0

const todayDate = toDateInputValue(new Date())
const canGoForward = computed(() => selectedDate.value < todayDate)
const selectedDateLabel = computed(() => formatDateLabel(selectedDate.value))
const score = computed(() => dailyAnalysis.value?.healthScore ?? 0)
const coach = computed(() => npcCoaches.find((npc) => npc.id === coachId.value))
const mealRecorded = computed<Record<MealKindId, boolean>>(() => {
  const initial: Record<MealKindId, boolean> = { breakfast: false, lunch: false, snack: false, dinner: false }
  for (const meal of dailyAnalysis.value?.diet?.meals || []) {
    const key = mealTypeToKind(meal.mealType)
    if (key) initial[key] = Boolean(meal.recorded || meal.items?.length)
  }
  return initial
})
const recordedMealCount = computed(() => Object.values(mealRecorded.value).filter(Boolean).length)
const goals = computed(() => dailyAnalysis.value?.nutrition?.nutrients?.map((nutrient) => ({
  name: nutrient.name,
  value: Number(nutrient.current || 0),
  max: Number(nutrient.target || 1),
  unit: nutrient.unit,
  state: statusToState(nutrient.status)
})) ?? [])
const week = computed(() => weeklyAnalysis.value?.dailyScores?.map((day) => day.healthScore) ?? [])
const weeklyDayLabels = computed(() => weeklyAnalysis.value?.dailyScores?.map((day) => dayOfWeekLabel(day.dayOfWeek)) ?? [])
const weekStats = computed(() => {
  const weekly = weeklyAnalysis.value
  if (!weekly) return [['평균 점수', weeklyLoading.value ? '...' : '-', weeklyError.value || '조회 전'], ['기록률', '-', '-'], ['식이섬유 평균', '-', '-']]

  const diff = Number(weekly.scoreDiffFromPreviousWeek || 0)
  return [
    ['평균 점수', String(weekly.averageHealthScore), `${diff >= 0 ? '+' : ''}${diff} vs 지난주`],
    ['기록률', `${weekly.recordRate}%`, `${weekly.recordedDays}/${weekly.totalDays}일 기록`],
    ['식이섬유 평균', `${Math.round(Number(weekly.weeklyNutritionAverage?.fiber || 0))}g`, '주간 평균']
  ]
})
const weekRange = computed(() => weekRangeDates())
const weekRangeLabel = computed(() => `${formatDateLabel(weekRange.value.startDate)} ~ ${formatDateLabel(weekRange.value.endDate)}`)
const isSelectedWeekFinished = computed(() => weekRange.value.endDate < todayDate)
const dailyReportSections = computed(() => {
  if (dailyLoading.value) return [{ title: '조회 중', text: '선택한 날짜의 일간 분석과 AI 리포트를 불러오는 중입니다.' }]
  if (dailyError.value) return [{ title: '조회 실패', text: dailyError.value }]
  return reportToSections(dailyAnalysis.value?.dailyReport, '선택한 날짜의 리포트가 없습니다.')
})
const weeklyReportSections = computed(() => {
  if (weeklyLoading.value) return [{ title: '조회 중', text: '선택한 주간 분석과 AI 리포트를 불러오는 중입니다.' }]
  if (weeklyError.value) return [{ title: '조회 실패', text: weeklyError.value }]
  if (weeklyAnalysis.value?.weeklyReport) return reportToSections(weeklyAnalysis.value.weeklyReport, '선택한 기간의 리포트가 없습니다.')
  if (!isSelectedWeekFinished.value) return [{ title: '분석 중', text: '선택한 주가 아직 끝나지 않았습니다. 다음 월요일부터 주간 리포트를 확인할 수 있습니다.' }]
  return [{ title: '리포트 없음', text: '선택한 기간의 리포트가 없습니다.' }]
})
const standardInsightRows = computed(() => dailyAnalysis.value?.nutrition?.standardInsights?.map((insight) => ({
  name: insight.label,
  mine: `${Math.round(Number(insight.current || 0)).toLocaleString()}${insight.unit}`,
  standard: `${Math.round(Number(insight.standard || 0)).toLocaleString()}${insight.unit}`,
  state: statusToState(insight.status)
})) ?? [])
const selectedApiCoach = computed(() =>
  apiCoaches.value.find((c) => c.name === coach.value?.name) ?? apiCoaches.value.find((c) => c.selected) ?? null
)
const selectedCoachFeedback = computed(() => {
  const selectedCoach = selectedApiCoach.value
  if (!selectedCoach) return null
  return dailyAnalysis.value?.mealFeedbacks?.find((feedback) => feedback.coachId === selectedCoach.coachId) ?? null
})
const coachMessage = computed(() =>
  selectedCoachFeedback.value?.message ||
  '현재 식단이 없습니다.'
)
const apiCoachName = computed(() => selectedApiCoach.value?.name || coach.value?.name)
const nextWeekStrategy = computed(() => weeklyAnalysis.value?.weeklyReport?.nextAction || (weeklyLoading.value ? '주간 전략을 불러오는 중입니다.' : '주간 리포트가 완성되면 다음 주 식단 전략을 표시합니다.'))

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

function formatDateLabel(value: string) {
  const date = new Date(`${value}T00:00:00`)
  return `${date.getMonth() + 1}월 ${date.getDate()}일`
}

function statusToState(status: string) {
  if (status === 'HIGH') return 'over'
  if (status === 'LOW') return 'low'
  return 'ok'
}

function mealTypeToKind(mealType: string): MealKindId | null {
  if (mealType === 'BREAKFAST') return 'breakfast'
  if (mealType === 'LUNCH') return 'lunch'
  if (mealType === 'SNACK') return 'snack'
  if (mealType === 'DINNER') return 'dinner'
  return null
}

function dayOfWeekLabel(value: string) {
  const labels: Record<string, string> = { MON: '월', TUE: '화', WED: '수', THU: '목', FRI: '금', SAT: '토', SUN: '일' }
  return labels[value] || value
}

function weekRangeDates() {
  const current = new Date(`${selectedDate.value}T00:00:00`)
  const day = current.getDay() || 7
  const start = new Date(current)
  start.setDate(current.getDate() - day + 1)
  const end = new Date(start)
  end.setDate(start.getDate() + 6)
  return { startDate: toDateInputValue(start), endDate: toDateInputValue(end) }
}

function reportToSections(report: AiReportResponse | null | undefined, emptyText: string) {
  if (!report) return [{ title: '리포트 없음', text: emptyText }]

  const sections = [{ title: '요약', text: report.summary || emptyText }]
  if (report.strengths?.length) sections.push({ title: '강점', text: report.strengths.join(' ') })
  if (report.warnings?.length) sections.push({ title: '주의', text: report.warnings.join(' ') })
  if (report.nextAction) sections.push({ title: '다음 행동', text: report.nextAction })
  return sections
}

function errorMessage(error: unknown) {
  if (error instanceof Error) return error.message
  return '요청 처리 중 오류가 발생했습니다.'
}

async function loadDailyAnalysis() {
  const requestId = ++dailyRequestId
  const date = selectedDate.value
  dailyLoading.value = true
  dailyError.value = ''
  dailyAnalysis.value = null

  try {
    const analysis = await analysisApi.daily(date)
    if (requestId === dailyRequestId) dailyAnalysis.value = analysis
  } catch (error) {
    console.warn('Daily analysis API failed', error)
    if (requestId === dailyRequestId) {
      dailyError.value = errorMessage(error)
      dailyAnalysis.value = null
    }
  } finally {
    if (requestId === dailyRequestId) dailyLoading.value = false
  }
}

async function loadWeeklyAnalysis() {
  const requestId = ++weeklyRequestId
  const { startDate, endDate } = weekRangeDates()
  weeklyLoading.value = true
  weeklyError.value = ''
  weeklyAnalysis.value = null

  try {
    const analysis = await analysisApi.weekly(startDate, endDate)
    if (requestId === weeklyRequestId) weeklyAnalysis.value = analysis
  } catch (error) {
    console.warn('Weekly analysis API failed', error)
    if (requestId === weeklyRequestId) {
      weeklyError.value = errorMessage(error)
      weeklyAnalysis.value = null
    }
  } finally {
    if (requestId === weeklyRequestId) weeklyLoading.value = false
  }
}

watch(selectedDate, () => {
  void loadDailyAnalysis()
  void loadWeeklyAnalysis()
})

watch(tab, (next) => {
  if (next === 'weekly' && !weeklyAnalysis.value) void loadWeeklyAnalysis()
})

function selectCoach(nextCoachId: string) {
  coachId.value = nextCoachId
}

async function loadCoaches() {
  try {
    const result = await coachApi.getCoaches()
    apiCoaches.value = result.coaches ?? []
    const selected = result.coaches?.find((c) => c.selected)
    if (selected) {
      const match = npcCoaches.find((npc) => npc.name === selected.name)
      if (match) coachId.value = match.id
    }
  } catch (error) {
    console.warn('Coaches API failed', error)
  }
}

onMounted(() => {
  void loadDailyAnalysis()
  void loadWeeklyAnalysis()
  void loadCoaches()
})
</script>

<style scoped>
.analyze-layout { display: grid; grid-template-columns: 1.15fr 1fr; gap: 20px; }
.date-box { position: relative; display: flex; align-items: center; gap: 4px; background: var(--surface); border: 1px solid var(--border); border-radius: 12px; padding: 4px 6px; box-shadow: var(--shadow); }
.date-open { border: 0; background: transparent; padding: 6px 14px; font-family: var(--mono); font-size: 13px; font-weight: 800; min-width: 200px; text-align: center; cursor: pointer; color: var(--ink); }
.calendar-pop { position: absolute; right: 44px; top: 44px; z-index: 10; border: 1px solid var(--border-strong); border-radius: 10px; padding: 8px; box-shadow: var(--shadow-lg); }
.tabs { padding: 14px 22px 0; border-bottom: 1px solid var(--border); display: flex; align-items: flex-end; gap: 4px; }
.tabs button { padding: 12px 20px 14px; border: 0; background: transparent; cursor: pointer; border-bottom: 3px solid transparent; margin-bottom: -1px; display: flex; flex-direction: column; gap: 2px; text-align: left; }
.tabs button.active { border-bottom-color: var(--accent); }
.tabs strong { font-size: 14px; }
.tabs small { font-family: var(--mono); color: var(--ink-3); font-size: 10px; }
.tabs .active small { color: var(--accent); }
.report-body { padding: 22px; display: flex; flex-direction: column; gap: 18px; }
.score-card { display: flex; align-items: center; gap: 22px; padding: 20px; border-radius: 14px; background: linear-gradient(135deg,#fff5e6 0%,#fff 100%); border: 1px solid var(--border); }
.empty-score { width: 120px; height: 120px; border: 10px solid var(--border); border-radius: 50%; display: flex; align-items: center; justify-content: center; font-family: var(--mono); font-size: 28px; font-weight: 800; color: var(--ink-3); flex: 0 0 120px; }
h2 { margin: 6px 0; font-size: 22px; }
h3 { font-size: 13px; margin: 0 0 10px; }
p { margin: 0; color: var(--ink-2); line-height: 1.6; font-size: 12px; }
.empty-text { padding: 12px; border: 1px dashed var(--border); border-radius: 10px; background: var(--surface-alt); }
.report-sections { display: grid; gap: 8px; margin-top: 10px; }
.report-section { padding: 10px 12px; border: 1px solid var(--border); border-radius: 10px; background: rgba(255,255,255,.72); }
.report-section strong { display: block; margin-bottom: 4px; font-size: 13px; color: var(--ink); }
.report-section p { color: var(--ink); font-size: 15px; line-height: 1.65; font-weight: 400; word-break: keep-all; }
.meal-status { display: grid; grid-template-columns: repeat(4, 1fr); gap: 8px; }
.meal-status div { padding: 12px 8px; text-align: center; border: 1.5px dashed var(--border); border-radius: 10px; background: var(--surface-alt); }
.meal-status .done { border-style: solid; border-color: var(--border-strong); background: var(--surface); }
.meal-status span { font-size: 20px; display: block; }
.meal-status strong { display: block; font-size: 11px; margin-top: 4px; }
.meal-status small { color: var(--ok); }
.right-col { display: flex; flex-direction: column; gap: 16px; }
.coach-row { display: flex; gap: 14px; align-items: flex-start; }
.npc { width: 56px; height: 56px; border-radius: 28px; border: 1.5px solid var(--border); display: flex; align-items: center; justify-content: center; font-size: 28px; flex: 0 0 56px; }
.speech-bubble { flex: 1; padding: 14px; background: var(--surface-alt); border-radius: 12px; position: relative; }
.speech-bubble:before { content: ""; position: absolute; left: -8px; top: 18px; width: 16px; height: 16px; background: var(--surface-alt); transform: rotate(45deg); }
.speech-bubble strong { font-size: 14px; display: block; margin-bottom: 4px; }
.coach-picker { margin-top: 14px; padding-top: 14px; border-top: 1px dashed var(--border); display: flex; flex-wrap: wrap; gap: 6px; }
.coach-picker .mono-label { width: 100%; margin-bottom: 2px; }
.coach-picker button { display: inline-flex; align-items: center; gap: 6px; padding: 6px 10px; border: 1.5px solid var(--border); background: var(--surface); border-radius: 999px; font-size: 12px; font-weight: 600; color: var(--ink-2); cursor: pointer; }
.coach-picker button.active { border-color: var(--accent); background: var(--accent-soft); color: var(--accent-dark); }
.coach-picker span { width: 20px; height: 20px; border-radius: 10px; display: flex; align-items: center; justify-content: center; font-size: 11px; }
.goal-list, .peer-list { display: flex; flex-direction: column; gap: 12px; }
.week-stats { display: grid; grid-template-columns: repeat(3, 1fr); gap: 10px; }
.week-stats div { padding: 14px; border: 1px solid var(--border); border-radius: 10px; }
.week-stats small { font-family: var(--mono); color: var(--ink-3); font-size: 10px; }
.week-stats strong { display: block; font-family: var(--mono); font-size: 26px; margin-top: 4px; }
.week-stats span { font-family: var(--mono); color: var(--ink-3); font-size: 10px; }
.week-chart { display: flex; align-items: flex-end; gap: 10px; height: 140px; }
.week-chart div { flex: 1; align-self: stretch; display: flex; flex-direction: column; align-items: center; gap: 6px; justify-content: flex-end; }
.week-chart span, .week-chart small { font-family: var(--mono); font-size: 10px; color: var(--ink-3); }
.week-chart b { width: 100%; background: var(--ink); border-radius: 6px 6px 0 0; }
.week-chart b.today { background: var(--accent); }
.ai-head { display: flex; justify-content: space-between; margin-bottom: 10px; }
.strategy { background: var(--accent-soft); border-color: var(--accent); }
</style>
