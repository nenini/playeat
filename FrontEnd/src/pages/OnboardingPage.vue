<template>
  <section class="onboarding-page">
    <header class="onboarding-top">
      <button class="brand" type="button" @click="$emit('cancel')"><span>냠</span><strong>냠냠코치</strong></button>
      <div class="progress-label">{{ currentIndex + 1 }} / {{ onboardingSteps.length }}</div>
    </header>

    <main class="onboarding-shell">
      <aside class="step-list">
        <button v-for="(step, index) in onboardingSteps" :key="step.id" type="button" :class="{ active: currentIndex === index, done: index < currentIndex }" @click="currentIndex = index">
          <b>{{ index + 1 }}</b>
          <span>{{ step.title }}</span>
        </button>
      </aside>

      <form class="step-card" @submit.prevent="next">
        <div class="bar"><span :style="{ width: `${((currentIndex + 1) / onboardingSteps.length) * 100}%` }"></span></div>
        <div class="step-head">
          <p>{{ currentStep.eyebrow }}</p>
          <h1>{{ currentStep.title }}</h1>
          <span>{{ currentStep.description }}</span>
        </div>

        <div class="question-list">
          <div v-for="question in currentStep.questions" :key="question.id" class="question">
            <label>{{ question.label }}</label>
            <input v-if="question.type === 'date'" v-model="form[question.id]" type="date">
            <input v-else-if="question.type === 'number'" v-model="form[question.id]" type="number" :placeholder="question.placeholder">
            <input v-else-if="question.type === 'time'" v-model="form[question.id]" type="time">
            <div v-else-if="question.type === 'single'" class="choice-grid">
              <button v-for="option in question.options" :key="option" type="button" :class="{ selected: form[question.id] === option }" @click="form[question.id] = option">{{ option }}</button>
            </div>
            <div v-else class="choice-grid multi">
              <button v-for="option in question.options" :key="option" type="button" :class="{ selected: multiValue(question.id).includes(option) }" @click="toggleMulti(question.id, option)">{{ option }}</button>
              <div v-if="question.placeholder" class="custom-input">
                <input v-model="drafts[question.id]" :placeholder="question.placeholder" @keydown.enter.prevent="addCustom(question.id)">
                <button type="button" @click="addCustom(question.id)">추가</button>
              </div>
              <div v-if="customValues(question.id).length" class="custom-tags">
                <button v-for="item in customValues(question.id)" :key="item" type="button" @click="removeCustom(question.id, item)">{{ item }} ×</button>
              </div>
            </div>
            <small v-if="question.unit">{{ question.unit }}</small>
          </div>
        </div>

        <div class="step-actions">
          <button type="button" class="ghost" :disabled="currentIndex === 0" @click="prev">이전</button>
          <button type="submit" class="primary">{{ currentIndex === onboardingSteps.length - 1 ? '완료하고 시작하기' : '다음' }}</button>
        </div>
      </form>
    </main>
  </section>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { onboardingSteps } from '../services/mock/nyamnyamMock'

const emit = defineEmits<{ done: [payload: Record<string, string | string[]>], cancel: [] }>()
const currentIndex = ref(0)
const drafts = reactive<Record<string, string>>({})
const form = reactive<Record<string, string | string[]>>({
  birthday: '2001-03-15',
  gender: '여성',
  height: '162',
  currentWeight: '54',
  targetWeight: '50',
  activityLevel: '가벼운 활동이 있어요',
  exerciseFrequency: '주 1~2회',
  mainGoal: '감량',
  improveTarget: '식습관 개선',
  healthFocus: ['없음'],
  recordFrequency: '매일',
  calorieTrackingExperience: '처음이에요'
})

const currentStep = computed(() => onboardingSteps[currentIndex.value])

function multiValue(id: string) {
  const value = form[id]
  return Array.isArray(value) ? value : []
}

function toggleMulti(id: string, option: string) {
  const values = multiValue(id)
  if (option === '없음') {
    form[id] = values.includes(option) ? [] : [option]
    return
  }
  const next = values.includes(option) ? values.filter((item) => item !== option) : [...values.filter((item) => item !== '없음'), option]
  form[id] = next
}

function customValues(id: string) {
  return multiValue(id).filter((item) => !onboardingSteps.flatMap((step) => step.questions).find((question) => question.id === id)?.options?.includes(item))
}

function addCustom(id: string) {
  const value = (drafts[id] || '').trim()
  if (!value) return
  const values = multiValue(id).filter((item) => item !== '없음')
  if (!values.includes(value)) form[id] = [...values, value]
  drafts[id] = ''
}

function removeCustom(id: string, item: string) {
  form[id] = multiValue(id).filter((value) => value !== item)
}

function prev() {
  if (currentIndex.value > 0) currentIndex.value -= 1
}

function next() {
  if (currentIndex.value < onboardingSteps.length - 1) {
    currentIndex.value += 1
    return
  }
  emit('done', { ...form })
}
</script>

<style scoped>
.onboarding-page { min-height: 100vh; background: linear-gradient(180deg,#fff1e8 0%,#faf7f0 50%,#fff 100%); color: var(--ink); }
.onboarding-top { height: 70px; display: flex; align-items: center; justify-content: space-between; padding: 0 42px; border-bottom: 1px solid var(--border); background: rgba(255,255,255,.86); backdrop-filter: blur(12px); }
.brand { display: inline-flex; align-items: center; gap: 10px; border: 0; background: transparent; cursor: pointer; } .brand span { width: 34px; height: 34px; border-radius: 9px; background: var(--accent); color: #fff; display: flex; align-items: center; justify-content: center; font-weight: 900; } .brand strong { font-size: 20px; }
.progress-label { font-family: var(--mono); color: var(--ink-2); font-weight: 800; }
.onboarding-shell { max-width: 1180px; margin: 0 auto; padding: 42px; display: grid; grid-template-columns: 300px 1fr; gap: 24px; }
.step-list { display: flex; flex-direction: column; gap: 8px; position: sticky; top: 92px; align-self: start; }
.step-list button { display: flex; align-items: center; gap: 10px; padding: 12px 14px; border-radius: 12px; border: 1px solid var(--border); background: #fff; color: var(--ink-2); cursor: pointer; text-align: left; box-shadow: var(--shadow); }
.step-list b { width: 28px; height: 28px; border-radius: 14px; background: var(--surface-alt); display: flex; align-items: center; justify-content: center; font-family: var(--mono); }
.step-list .active { border-color: var(--accent); background: var(--accent-soft); color: var(--ink); } .step-list .active b { background: var(--accent); color: #fff; } .step-list .done b { background: var(--ok); color: #fff; }
.step-card { background: #fff; border: 1px solid var(--border); border-radius: 18px; box-shadow: var(--shadow-lg); overflow: hidden; }
.bar { height: 7px; background: var(--surface-alt); } .bar span { display: block; height: 100%; background: var(--accent); transition: width .2s; }
.step-head { padding: 32px 34px 18px; } .step-head p { margin: 0 0 8px; color: var(--accent-dark); font-family: var(--mono); font-size: 11px; letter-spacing: 1.2px; font-weight: 800; } .step-head h1 { margin: 0; font-size: 30px; letter-spacing: -0.6px; } .step-head span { display: block; margin-top: 8px; color: var(--ink-2); font-size: 14px; }
.question-list { padding: 8px 34px 28px; display: flex; flex-direction: column; gap: 18px; }
.question { position: relative; padding: 18px; border: 1px solid var(--border); border-radius: 14px; background: #fffdf8; } .question label { display: block; font-size: 14px; font-weight: 900; margin-bottom: 12px; } .question > small { position: absolute; right: 32px; bottom: 31px; color: var(--ink-3); font-family: var(--mono); font-size: 12px; }
.question input { width: 100%; height: 44px; border: 1.5px solid var(--border-strong); border-radius: 10px; padding: 0 14px; outline: 0; font-size: 14px; background: #fff; } .question input:focus { border-color: var(--accent); }
.choice-grid { display: flex; flex-wrap: wrap; gap: 8px; } .choice-grid button { padding: 10px 14px; border-radius: 999px; border: 1.5px solid var(--border); background: #fff; color: var(--ink-2); cursor: pointer; font-weight: 700; } .choice-grid button.selected { border-color: var(--accent); background: var(--accent); color: #fff; }
.custom-input { display: inline-flex; gap: 6px; align-items: center; width: 260px; max-width: 100%; } .custom-input input { flex: 1; min-width: 0; } .custom-input button { border-radius: 10px; background: var(--surface-alt); }
.custom-tags { width: 100%; display: flex; gap: 6px; flex-wrap: wrap; margin-top: 2px; } .custom-tags button { background: var(--accent-soft); border-color: var(--accent); color: var(--accent-dark); }
.step-actions { border-top: 1px solid var(--border); padding: 18px 34px; display: flex; justify-content: space-between; gap: 12px; background: #fff; } .step-actions button { height: 44px; padding: 0 24px; border-radius: 10px; font-weight: 900; cursor: pointer; } .step-actions .ghost { border: 1px solid var(--border-strong); background: #fff; color: var(--ink-2); } .step-actions .ghost:disabled { opacity: .4; cursor: not-allowed; } .step-actions .primary { border: 0; background: var(--accent); color: #fff; min-width: 180px; }
@media (max-width: 860px) { .onboarding-shell { grid-template-columns: 1fr; padding: 24px 18px; } .step-list { position: static; display: grid; grid-template-columns: repeat(2, 1fr); } .onboarding-top { padding: 0 20px; } }
</style>
