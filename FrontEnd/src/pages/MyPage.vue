<template>
  <section class="mypage-layout">
    <div v-if="passwordOpen || deleteOpen" class="modal-backdrop" @click="closeModals">
      <form v-if="passwordOpen" class="account-modal" @click.stop @submit.prevent="passwordOpen = false">
        <button type="button" class="modal-close" @click="passwordOpen = false">×</button>
        <h2>비밀번호 재설정</h2>
        <label>기존 비밀번호<input v-model="passwordForm.current" type="password"></label>
        <label>새 비밀번호<input v-model="passwordForm.next" type="password"></label>
        <label>새 비밀번호 확인<input v-model="passwordForm.confirm" type="password"></label>
        <p v-if="passwordMismatch" class="error">새 비밀번호가 일치하지 않아요.</p>
        <AppButton full :disabled="!canResetPassword">비밀번호 변경</AppButton>
      </form>
      <div v-else class="account-modal" @click.stop>
        <button type="button" class="modal-close" @click="deleteOpen = false">×</button>
        <h2>회원 탈퇴 확인</h2>
        <p>정말 회원 탈퇴를 진행할까요? 이 작업은 되돌릴 수 없어요.</p>
        <div class="modal-actions"><AppButton variant="secondary" @click="deleteOpen = false">취소</AppButton><AppButton variant="danger" @click="deleteOpen = false">탈퇴하기</AppButton></div>
      </div>
    </div>
    <main class="left">
      <AppCard>
        <div class="section-title"><div class="section-title-main">회원 정보</div><div class="edit-actions"><AppButton v-if="editing" variant="ghost" size="sm" @click="editing = false">취소</AppButton><AppButton v-if="editing" size="sm" @click="editing = false">저장</AppButton><AppButton v-else variant="secondary" size="sm" @click="editing = true">수정</AppButton></div></div>
        <div class="profile-card">
          <div class="avatar-wrap">
            <img v-if="profile.photoUrl" :src="profile.photoUrl" alt="프로필 사진">
            <span v-else>{{ profile.nickname[0] }}</span>
          </div>
          <div class="profile-meta"><strong>{{ profile.nickname }}</strong><small>{{ profile.email }}</small></div>
          <div v-if="editing" class="photo-actions">
            <input ref="photoInput" class="hidden-file" type="file" accept="image/*" @change="uploadPhoto">
            <AppButton size="sm" variant="secondary" @click="photoInput?.click()">{{ profile.photoUrl ? '사진 수정' : '사진 업로드' }}</AppButton>
            <AppButton v-if="profile.photoUrl" size="sm" variant="danger" @click="removePhoto">사진 삭제</AppButton>
          </div>
        </div>
        <div v-if="editing" class="edit-form">
          <label>닉네임<input v-model="profile.nickname"></label>
          <label>이메일<input :value="profile.email" disabled></label>
          <label>키<input v-model="profile.height"></label>
          <label>몸무게<input v-model="profile.weight"></label>
          <label>건강 목표<input v-model="profile.goal"></label>
        </div>
        <div v-else class="info-grid"><div v-for="item in info" :key="item[0]"><small>{{ item[0].toUpperCase() }}</small><strong>{{ item[1] }}</strong></div></div>
      </AppCard>
      <AppCard>
        <div class="section-title"><div><div class="section-title-main">뱃지 컬렉션</div><div class="section-title-sub">{{ badges.filter(b => b.earned).length }}/{{ badges.length }} 획득</div></div></div>
        <div class="badge-grid"><div v-for="badge in badges" :key="badge.id" :class="{ locked: !badge.earned }"><span>{{ badge.emoji }}</span><strong>{{ badge.name }}</strong><small>{{ badge.desc }}</small><em v-if="badge.earned">{{ badge.date }}</em></div></div>
      </AppCard>
    </main>
    <aside class="right">
      <AppCard>
        <div class="section-title-main">계정 설정</div>
        <div class="setting-list"><button @click="passwordOpen = true"><span><strong>비밀번호 재설정</strong><small>현재 비밀번호 확인 후 변경 가능</small></span><AppIcon name="chev-r" color="var(--ink-3)" /></button><button @click="$emit('restartOnboarding')"><span><strong>온보딩 다시하기</strong><small>건강 목표·기본 정보 재설정</small></span><AppIcon name="chev-r" color="var(--ink-3)" /></button></div>
      </AppCard>
      <AppCard><div class="section-title-main">계정</div><div class="account-buttons"><AppButton variant="secondary" full>로그아웃</AppButton><AppButton variant="danger" full @click="deleteOpen = true">회원 탈퇴</AppButton></div></AppCard>
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
import { computed, reactive, ref } from 'vue'

defineProps<{ stage: Stage }>()
defineEmits<{ restartOnboarding: [] }>()
const editing = ref(false)
const passwordOpen = ref(false)
const deleteOpen = ref(false)
const photoInput = ref<HTMLInputElement | null>(null)
const profile = reactive({ nickname: '지은', email: 'jieun@example.com', height: '162 cm', weight: '54 kg', gender: '여성', goal: '다이어트', photoUrl: '' })
const passwordForm = reactive({ current: '', next: '', confirm: '' })
const passwordMismatch = computed(() => !!passwordForm.next && !!passwordForm.confirm && passwordForm.next !== passwordForm.confirm)
const canResetPassword = computed(() => !!passwordForm.current && !!passwordForm.next && !!passwordForm.confirm && !passwordMismatch.value)
const info = computed(() => [['키', profile.height], ['몸무게', profile.weight], ['성별', profile.gender], ['건강 목표', profile.goal], ['가입일', '2026·04·20'], ['생년월일', '2001·03·15']])
function closeModals() { passwordOpen.value = false; deleteOpen.value = false }
function uploadPhoto(event: Event) {
  const file = (event.target as HTMLInputElement).files?.[0]
  if (!file) return
  if (profile.photoUrl.startsWith('blob:')) URL.revokeObjectURL(profile.photoUrl)
  profile.photoUrl = URL.createObjectURL(file)
}
function removePhoto() {
  if (profile.photoUrl.startsWith('blob:')) URL.revokeObjectURL(profile.photoUrl)
  profile.photoUrl = ''
  if (photoInput.value) photoInput.value.value = ''
}
</script>

<style scoped>
.mypage-layout { display: grid; grid-template-columns: 1.1fr 1fr; gap: 20px; }
.left, .right { display: flex; flex-direction: column; gap: 16px; }
.profile-card { display: flex; gap: 14px; align-items: center; padding: 14px 16px; background: var(--surface-alt); border-radius: 12px; margin-bottom: 16px; }
.avatar-wrap { width: 58px; height: 58px; border-radius: 29px; background: var(--accent); color: #fff; display: flex; align-items: center; justify-content: center; font-size: 20px; font-weight: 800; overflow: hidden; flex: 0 0 58px; }
.avatar-wrap img { width: 100%; height: 100%; object-fit: cover; display: block; }
.profile-meta { flex: 1; display: flex; flex-direction: column; min-width: 0; } .profile-card strong { font-size: 18px; } .profile-card small { font-size: 12px; color: var(--ink-2); margin-top: 1px; }
.photo-actions { display: flex; gap: 6px; flex-wrap: wrap; justify-content: flex-end; }
.hidden-file { display: none; }
.info-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 8px; } .info-grid div { padding: 10px 12px; border: 1px solid var(--border); border-radius: 10px; } .info-grid small { font-family: var(--mono); color: var(--ink-3); font-size: 10px; } .info-grid strong { display: block; font-size: 13px; margin-top: 3px; }
.edit-actions { display: flex; gap: 6px; }
.edit-form { display: grid; grid-template-columns: repeat(2, 1fr); gap: 10px; }
.edit-form label, .account-modal label { display: flex; flex-direction: column; gap: 6px; font-size: 12px; font-weight: 800; }
.edit-form input, .account-modal input { height: 40px; border: 1.5px solid var(--border-strong); border-radius: 10px; padding: 0 12px; outline: 0; }
.edit-form input:disabled { background: var(--surface-alt); color: var(--ink-3); cursor: not-allowed; }
.badge-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 8px; } .badge-grid div { padding: 14px 8px; text-align: center; border: 1.5px solid var(--border-strong); background: var(--surface); border-radius: 12px; } .badge-grid .locked { opacity: .4; background: var(--surface-alt); border-color: var(--border); } .badge-grid span { font-size: 26px; display: block; margin-bottom: 5px; } .locked span { filter: grayscale(1); } .badge-grid strong { display: block; font-size: 11px; line-height: 1.3; } .badge-grid small { display: block; font-size: 10px; color: var(--ink-3); margin-top: 3px; line-height: 1.4; } .badge-grid em { display: block; font-family: var(--mono); color: var(--accent); font-style: normal; font-size: 9px; margin-top: 4px; }
.setting-list, .account-buttons { display: flex; flex-direction: column; gap: 8px; margin-top: 10px; } .setting-list button { display: flex; align-items: center; justify-content: space-between; padding: 14px 16px; border: 1px solid var(--border); border-radius: 12px; background: var(--surface); cursor: pointer; text-align: left; } .setting-list span { display: flex; flex-direction: column; } .setting-list strong { font-size: 14px; } .setting-list small { font-size: 11px; color: var(--ink-3); margin-top: 2px; }
.pet-card { background: linear-gradient(180deg,#fffaf0 0%,#fff 100%); } .pet-stage { display: flex; justify-content: center; padding-top: 14px; } .pet-card p { text-align: center; color: var(--ink-2); font-size: 12px; margin: 6px 0 0; }
.modal-backdrop { position: fixed; inset: 0; z-index: 200; background: rgba(31,28,23,.32); display: flex; align-items: center; justify-content: center; padding: 24px; }
.account-modal { width: min(420px, 100%); background: var(--surface); border-radius: 18px; box-shadow: var(--shadow-lg); padding: 24px; position: relative; display: flex; flex-direction: column; gap: 12px; }
.account-modal h2 { margin: 0 0 6px; font-size: 20px; }
.modal-close { position: absolute; top: 12px; right: 12px; width: 30px; height: 30px; border: 0; border-radius: 15px; background: var(--surface-alt); cursor: pointer; color: var(--ink-2); font-size: 18px; }
.error { margin: -2px 0 0; color: var(--bad); font-size: 12px; }
.modal-actions { display: flex; justify-content: flex-end; gap: 8px; margin-top: 8px; }
</style>
