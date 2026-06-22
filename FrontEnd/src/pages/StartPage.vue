<template>
  <div class="start-page">
    <header class="start-nav">
      <button class="brand" type="button">
        <span>냠</span>
        <strong>냠냠코치</strong>
      </button>
      <div class="nav-actions">
        <button type="button" @click="$emit('login')">로그인</button>
        <button type="button" class="start-small" @click="$emit('start')">시작하기</button>
      </div>
    </header>

    <main>
      <section class="hero">
        <div class="hero-copy">
          <h1>스마트한 식단 관리의<br><span>새로운 시작</span></h1>
          <p>냠냠코치와 함께 매일의 식사를 기록하고, AI 코치의 도움으로 중요한 영양 목표를 놓치지 마세요. 즐거운 캐릭터 성장과 길드 퀘스트로 건강 관리를 경험해보세요.</p>
          <div class="hero-buttons">
            <button class="primary" type="button" @click="$emit('start')">무료로 시작하기 <span>→</span></button>
            <button class="secondary" type="button" @click="$emit('login')">로그인</button>
          </div>
        </div>
        <div class="hero-pet">
          <div class="pet-orbit">
            <NyamnyamCharacter stage="baby" :size="190" />
          </div>
        </div>
      </section>

      <section class="features">
        <h2>왜 냠냠코치를 선택해야 할까요?</h2>
        <p>팀의 식단 습관을 즐겁게 만드는 강력한 기능들을 만나보세요</p>
        <div class="feature-grid">
          <div v-for="feature in features" :key="feature.title" class="feature-card">
            <div>{{ feature.icon }}</div>
            <strong>{{ feature.title }}</strong>
            <span>{{ feature.desc }}</span>
          </div>
        </div>
      </section>

      <section class="steps">
        <h2>간단한 3단계로 시작하세요</h2>
        <p>복잡한 설정 없이 바로 사용할 수 있습니다</p>
        <div class="step-grid">
          <div v-for="step in steps" :key="step.no">
            <b>{{ step.no }}</b>
            <strong>{{ step.title }}</strong>
            <span>{{ step.desc }}</span>
          </div>
        </div>
      </section>

      <section class="cta">
        <h2>지금 바로 시작해보세요</h2>
        <p>무료로 냠냠코치를 경험하고 건강 관리 문화를 혁신하세요</p>
        <button type="button" @click="$emit('start')">무료 체험 시작하기</button>
      </section>
    </main>

    <footer>
      <div class="footer-brand"><span>냠</span><strong>냠냠코치</strong></div>
      <small>© 2026 NyamNyam Coach. All rights reserved.</small>
    </footer>
  </div>
</template>

<script setup lang="ts">
import NyamnyamCharacter from '../components/nyamnyam/NyamnyamCharacter.vue'

defineEmits<{ start: [], login: [] }>()

const features = [
  { icon: '🍱', title: '스마트 식단 기록', desc: '끼니와 영양소를 손쉽게 기록하고 오늘의 균형을 확인하세요.' },
  { icon: '✨', title: 'AI 코치 메시지', desc: '기록을 바탕으로 지금 필요한 한마디를 받아보세요.' },
  { icon: '🐥', title: '캐릭터 성장', desc: '건강한 습관이 쌓일수록 냠냠이가 함께 성장합니다.' },
  { icon: '🛡️', title: '길드 협업', desc: '길드원과 함께 퀘스트를 깨고 보스를 공략하세요.' }
]

const steps = [
  { no: 1, title: '회원가입', desc: '간단한 정보로 계정을 만들고 냠냠이를 만나보세요.' },
  { no: 2, title: '온보딩 설정', desc: '목표와 식습관을 선택해 나에게 맞는 기준을 세워요.' },
  { no: 3, title: '식단 기록 시작', desc: '매일의 식사를 기록하고 AI 분석과 캐릭터 성장을 확인해요.' }
]
</script>

<style scoped>
.start-page { min-height: 100vh; background: #fffaf4; color: #29251f; }
.start-nav { height: 70px; border-bottom: 1px solid #e7e3d6; background: #fff; display: flex; align-items: center; justify-content: space-between; padding: 0 52px; }
.brand, .footer-brand { display: inline-flex; align-items: center; gap: 10px; border: 0; background: transparent; color: #121722; }
.brand span, .footer-brand span { width: 34px; height: 34px; border-radius: 9px; background: var(--accent); display: flex; align-items: center; justify-content: center; font-weight: 900; color: #fff; }
.brand strong { font-size: 20px; } .nav-actions { display: flex; align-items: center; gap: 18px; } .nav-actions button { border: 0; background: transparent; cursor: pointer; color: var(--ink-2); font-weight: 600; } .nav-actions .start-small { background: var(--accent); color: #fff; padding: 10px 18px; border-radius: 6px; box-shadow: 0 1px 0 var(--accent-dark); }
.hero { min-height: 420px; background: linear-gradient(180deg,#fff1e8 0%,#fffaf4 72%,#fff 100%); display: grid; grid-template-columns: 1fr 280px; align-items: center; width: 100%; margin: 0 auto; padding: 70px 52px 40px; gap: 40px; }
.hero-copy { text-align: center; grid-column: 1 / -1; } .hero h1 { margin: 0; font-size: 42px; line-height: 1.15; letter-spacing: -1px; } .hero h1 span { color: var(--accent); } .hero p { max-width: 720px; margin: 28px auto 0; color: var(--ink-2); font-size: 16px; line-height: 1.7; }
.hero-buttons { margin-top: 28px; display: flex; justify-content: center; gap: 12px; } .hero-buttons button, .cta button { height: 42px; border-radius: 6px; padding: 0 30px; font-weight: 800; cursor: pointer; } .primary { border: 0; background: var(--accent); color: #fff; box-shadow: 0 1px 0 var(--accent-dark); } .secondary { border: 1px solid #d4cdba; background: #fff; color: #252b36; }
.hero-pet { display: none; }
.features, .steps { text-align: center; padding: 78px 52px; background: #fff; } .steps { background: #f6f7f9; } h2 { margin: 0; font-size: 31px; letter-spacing: -0.5px; } .features > p, .steps > p, .cta p { color: #606977; margin: 18px 0 0; }
.feature-grid { max-width: 1180px; margin: 60px auto 0; display: grid; grid-template-columns: repeat(4, 1fr); gap: 30px; } .feature-card { border: 1px solid var(--border); border-radius: 8px; padding: 34px 22px; background: #fff; min-height: 160px; display: flex; flex-direction: column; align-items: center; gap: 16px; box-shadow: var(--shadow); } .feature-card div { font-size: 28px; color: var(--accent); } .feature-card strong { font-size: 16px; } .feature-card span { color: var(--ink-2); font-size: 13px; line-height: 1.7; }
.step-grid { max-width: 1120px; margin: 60px auto 0; display: grid; grid-template-columns: repeat(3, 1fr); gap: 80px; } .step-grid div { display: flex; flex-direction: column; align-items: center; gap: 18px; } .step-grid b { width: 58px; height: 58px; border-radius: 29px; background: var(--accent); color: #fff; display: flex; align-items: center; justify-content: center; font-size: 22px; box-shadow: 0 1px 0 var(--accent-dark); } .step-grid strong { font-size: 17px; } .step-grid span { color: #6a7280; font-size: 13px; line-height: 1.7; }
.cta { text-align: center; background: var(--accent); padding: 70px 24px; } .cta h2, .cta p { color: #fff; } .cta button { margin-top: 28px; border: 0; background: #29251f; color: #fff; }
footer { height: 110px; background: #111824; color: #fff; display: flex; align-items: center; justify-content: space-between; padding: 0 52px; } footer small { color: #98a2b3; }
@media (max-width: 760px) { .start-nav, footer { padding: 0 20px; } .hero, .features, .steps { padding-left: 20px; padding-right: 20px; } .hero h1 { font-size: 34px; } .feature-grid, .step-grid { grid-template-columns: 1fr; gap: 18px; } footer { flex-direction: column; justify-content: center; gap: 12px; } }
</style>
