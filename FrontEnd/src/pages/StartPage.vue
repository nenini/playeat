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
          <div class="hero-badges"><b>CASUAL RPG</b><b>GUILD RAID</b><b>AI COACH</b></div>
          <h1>먹고, 기록하고,<br><span>함께 성장하는 모험</span></h1>
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
          <div class="pet-level"><small>YOUR PARTNER</small><strong>LV.1 냠냠이</strong><span>첫 식단을 기록하고 함께 성장해요</span></div>
        </div>
      </section>

      <section class="features">
        <h2>왜 냠냠코치를 선택해야 할까요?</h2>
        <p>팀의 식단 습관을 즐겁게 만드는 강력한 기능들을 만나보세요</p>
        <div class="feature-grid">
          <div v-for="(feature, index) in features" :key="feature.title" class="feature-card" :class="`feature-${index + 1}`">
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
.start-page { min-height: 100vh; background: radial-gradient(circle at 10% 5%,rgba(243,211,111,.28),transparent 24rem),linear-gradient(180deg,#fffaf3,#fff5e9); color: var(--ink); }
.start-nav { height: 76px; border-bottom: 1px solid var(--border); background: rgba(255,255,255,.88); backdrop-filter: blur(16px); display: flex; align-items: center; justify-content: space-between; padding: 0 52px; position: sticky; top: 0; z-index: 20; box-shadow: 0 4px 18px rgba(100,61,35,.06); }
.brand, .footer-brand { display: inline-flex; align-items: center; gap: 10px; border: 0; background: transparent; color: #121722; }
.brand span, .footer-brand span { width: 38px; height: 38px; border-radius: 12px; background: linear-gradient(180deg,#ff9b5f,var(--accent)); display: flex; align-items: center; justify-content: center; font-weight: 900; color: #fff; box-shadow: 0 3px 0 var(--accent-dark); }
.brand strong { font-size: 20px; } .nav-actions { display: flex; align-items: center; gap: 18px; } .nav-actions button { border: 0; background: transparent; cursor: pointer; color: var(--ink-2); font-weight: 800; } .nav-actions .start-small { background: linear-gradient(180deg,#ff995e,var(--accent)); color: #fff; padding: 11px 20px; border-radius: 12px; box-shadow: 0 3px 0 var(--accent-dark),0 8px 16px rgba(240,120,60,.2); }
.hero { min-height: 600px; display: grid; grid-template-columns: minmax(0,1.1fr) minmax(360px,.9fr); align-items: center; max-width: 1240px; margin: 0 auto; padding: 72px 52px 64px; gap: 70px; }
.hero-copy { text-align: left; } .hero-badges { display: flex; flex-wrap: wrap; gap: 7px; margin-bottom: 20px; } .hero-badges b { padding: 5px 9px; border: 1px solid var(--border); border-radius: 999px; background: rgba(255,255,255,.8); color: var(--accent-dark); font: 800 10px var(--mono); } .hero-badges b:nth-child(2) { color: var(--purple); background: var(--purple-soft); } .hero-badges b:nth-child(3) { color: var(--ok); background: var(--ok-soft); }
.hero h1 { margin: 0; font-size: 52px; line-height: 1.12; letter-spacing: -1px; } .hero h1 span { color: var(--accent); } .hero p { max-width: 650px; margin: 24px 0 0; color: var(--ink-2); font-size: 16px; line-height: 1.8; }
.hero-buttons { margin-top: 30px; display: flex; gap: 12px; } .hero-buttons button, .cta button { height: 48px; border-radius: 12px; padding: 0 30px; font-weight: 900; cursor: pointer; } .primary { border: 0; background: linear-gradient(180deg,#ff995e,var(--accent)); color: #fff; box-shadow: 0 4px 0 var(--accent-dark),0 10px 20px rgba(240,120,60,.23); } .secondary { border: 1px solid var(--border-strong); background: #fff; color: var(--ink); box-shadow: 0 3px 0 rgba(116,75,49,.12); }
.hero-pet { display: flex; flex-direction: column; align-items: center; position: relative; } .pet-orbit { width: 330px; height: 330px; border-radius: 50%; display: flex; align-items: center; justify-content: center; background: radial-gradient(circle at 50% 36%,#fff,#fff0cf 58%,#ffc89f); border: 4px solid #fff; box-shadow: 0 0 0 8px rgba(240,120,60,.12),var(--shadow-lg); animation: pet-float 4s ease-in-out infinite; } .pet-level { width: 280px; margin-top: -30px; z-index: 2; padding: 14px 18px; border: 1px solid var(--border); border-radius: 15px; background: rgba(255,255,255,.94); box-shadow: var(--shadow); text-align: center; } .pet-level small { color: var(--accent); font: 800 9px var(--mono); } .pet-level strong,.pet-level span { display: block; } .pet-level strong { margin: 4px 0; font-size: 17px; } .pet-level span { color: var(--ink-2); font-size: 11px; }
.features, .steps { text-align: center; padding: 82px 52px; background: rgba(255,255,255,.82); } .steps { background: linear-gradient(180deg,#fff2df,#fff9f1); } h2 { margin: 0; font-size: 31px; letter-spacing: -0.5px; } .features > p, .steps > p, .cta p { color: var(--ink-2); margin: 16px 0 0; }
.feature-grid { max-width: 1180px; margin: 48px auto 0; display: grid; grid-template-columns: repeat(4,1fr); gap: 18px; } .feature-card { border: 1px solid var(--border); border-radius: 18px; padding: 30px 22px; background: linear-gradient(145deg,#fff,#fff8ee); min-height: 184px; display: flex; flex-direction: column; align-items: center; gap: 14px; box-shadow: var(--shadow); } .feature-card:hover { transform: translateY(-6px); border-color: var(--accent); box-shadow: var(--shadow-lg); } .feature-card div { width: 58px; height: 58px; border-radius: 16px; display: flex; align-items: center; justify-content: center; font-size: 30px; background: var(--accent-soft); } .feature-2 div { background: var(--purple-soft); } .feature-3 div { background: var(--yolk); } .feature-4 div { background: var(--ok-soft); } .feature-card strong { font-size: 16px; } .feature-card span { color: var(--ink-2); font-size: 13px; line-height: 1.7; }
.step-grid { max-width: 1050px; margin: 48px auto 0; display: grid; grid-template-columns: repeat(3,1fr); gap: 24px; } .step-grid div { padding: 26px 20px; border: 1px solid var(--border); border-radius: 18px; background: rgba(255,255,255,.75); display: flex; flex-direction: column; align-items: center; gap: 14px; } .step-grid b { width: 54px; height: 54px; border-radius: 16px; background: linear-gradient(180deg,#ff995e,var(--accent)); color: #fff; display: flex; align-items: center; justify-content: center; font-size: 21px; box-shadow: 0 4px 0 var(--accent-dark); } .step-grid strong { font-size: 17px; } .step-grid span { color: var(--ink-2); font-size: 13px; line-height: 1.7; }
.cta { text-align: center; background: radial-gradient(circle at 50% 0%,#ffb06e,var(--accent)); padding: 72px 24px; } .cta h2,.cta p { color: #fff; } .cta button { margin-top: 28px; border: 0; background: var(--ink); color: #fff; box-shadow: 0 4px 0 #120d0b; }
footer { height: 110px; background: var(--ink); color: #fff; display: flex; align-items: center; justify-content: space-between; padding: 0 52px; } footer small { color: #c5b7ae; }
@keyframes pet-float { 0%,100% { transform: translateY(0); } 50% { transform: translateY(-8px); } }
@media (max-width: 900px) { .hero { grid-template-columns: 1fr; text-align: center; } .hero-copy { text-align: center; } .hero-badges,.hero-buttons { justify-content: center; } .hero p { margin-left: auto; margin-right: auto; } .feature-grid { grid-template-columns: repeat(2,1fr); } }
@media (max-width: 760px) { .start-nav,footer { padding: 0 20px; } .hero,.features,.steps { padding-left: 20px; padding-right: 20px; } .hero h1 { font-size: 38px; } .hero-pet { display: none; } .feature-grid,.step-grid { grid-template-columns: 1fr; gap: 14px; } footer { flex-direction: column; justify-content: center; gap: 12px; } }
</style>
