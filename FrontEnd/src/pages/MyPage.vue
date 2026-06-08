<template>
  <section class="mypage-layout">
    <main class="left">
      <AppCard>
        <div class="section-title"><div class="section-title-main">회원 정보</div><AppButton variant="secondary" size="sm">수정</AppButton></div>
        <div class="profile-card"><div>지</div><span><strong>지은</strong><small>jieun@example.com</small></span></div>
        <div class="info-grid"><div v-for="item in info" :key="item[0]"><small>{{ item[0].toUpperCase() }}</small><strong>{{ item[1] }}</strong></div></div>
      </AppCard>
      <AppCard>
        <div class="section-title"><div><div class="section-title-main">뱃지 컬렉션</div><div class="section-title-sub">{{ badges.filter(b => b.earned).length }}/{{ badges.length }} 획득</div></div></div>
        <div class="badge-grid"><div v-for="badge in badges" :key="badge.id" :class="{ locked: !badge.earned }"><span>{{ badge.emoji }}</span><strong>{{ badge.name }}</strong><small>{{ badge.desc }}</small><em v-if="badge.earned">{{ badge.date }}</em></div></div>
      </AppCard>
    </main>
    <aside class="right">
      <AppCard>
        <div class="section-title-main">계정 설정</div>
        <div class="setting-list"><button><span><strong>비밀번호 재설정</strong><small>현재 비밀번호 확인 후 변경 가능</small></span><AppIcon name="chev-r" color="var(--ink-3)" /></button><button><span><strong>온보딩 다시하기</strong><small>건강 목표·기본 정보 재설정</small></span><AppIcon name="chev-r" color="var(--ink-3)" /></button></div>
      </AppCard>
      <AppCard><div class="section-title-main">계정</div><div class="account-buttons"><AppButton variant="secondary" full>로그아웃</AppButton><AppButton variant="danger" full>회원 탈퇴</AppButton></div></AppCard>
      <AppCard class="pet-card"><div class="section-title-main">냠냠이</div><div class="pet-stage"><NyamnyamCharacter :stage="stage" :size="150" /></div><p>LV.7 · 병아리 단계</p></AppCard>
    </aside>
  </section>
</template>

<script setup lang="ts">
import AppButton from '../components/common/AppButton.vue'
import AppCard from '../components/common/AppCard.vue'
import AppIcon from '../components/common/AppIcon.vue'
import NyamnyamCharacter from '../components/nyamnyam/NyamnyamCharacter.vue'
import { badges, type Stage } from '../services/mock/nyamnyamMock'

defineProps<{ stage: Stage }>()
const info = [['키', '162 cm'], ['몸무게', '54 kg'], ['성별', '여성'], ['건강 목표', '다이어트'], ['가입일', '2026·04·20'], ['생년월일', '2001·03·15']]
</script>

<style scoped>
.mypage-layout { display: grid; grid-template-columns: 1.1fr 1fr; gap: 20px; }
.left, .right { display: flex; flex-direction: column; gap: 16px; }
.profile-card { display: flex; gap: 14px; align-items: center; padding: 14px 16px; background: var(--surface-alt); border-radius: 12px; margin-bottom: 16px; }
.profile-card > div { width: 50px; height: 50px; border-radius: 25px; background: var(--accent); color: #fff; display: flex; align-items: center; justify-content: center; font-size: 20px; font-weight: 800; }
.profile-card span { display: flex; flex-direction: column; } .profile-card strong { font-size: 18px; } .profile-card small { font-size: 12px; color: var(--ink-2); margin-top: 1px; }
.info-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 8px; } .info-grid div { padding: 10px 12px; border: 1px solid var(--border); border-radius: 10px; } .info-grid small { font-family: var(--mono); color: var(--ink-3); font-size: 10px; } .info-grid strong { display: block; font-size: 13px; margin-top: 3px; }
.badge-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 8px; } .badge-grid div { padding: 14px 8px; text-align: center; border: 1.5px solid var(--border-strong); background: var(--surface); border-radius: 12px; } .badge-grid .locked { opacity: .4; background: var(--surface-alt); border-color: var(--border); } .badge-grid span { font-size: 26px; display: block; margin-bottom: 5px; } .locked span { filter: grayscale(1); } .badge-grid strong { display: block; font-size: 11px; line-height: 1.3; } .badge-grid small { display: block; font-size: 10px; color: var(--ink-3); margin-top: 3px; line-height: 1.4; } .badge-grid em { display: block; font-family: var(--mono); color: var(--accent); font-style: normal; font-size: 9px; margin-top: 4px; }
.setting-list, .account-buttons { display: flex; flex-direction: column; gap: 8px; margin-top: 10px; } .setting-list button { display: flex; align-items: center; justify-content: space-between; padding: 14px 16px; border: 1px solid var(--border); border-radius: 12px; background: var(--surface); cursor: pointer; text-align: left; } .setting-list span { display: flex; flex-direction: column; } .setting-list strong { font-size: 14px; } .setting-list small { font-size: 11px; color: var(--ink-3); margin-top: 2px; }
.pet-card { background: linear-gradient(180deg,#fffaf0 0%,#fff 100%); } .pet-stage { display: flex; justify-content: center; padding-top: 14px; } .pet-card p { text-align: center; color: var(--ink-2); font-size: 12px; margin: 6px 0 0; }
</style>
