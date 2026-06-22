<template>
  <section class="mypage-layout">
    <div v-if="passwordOpen || deleteOpen" class="modal-backdrop" @click="closeModals">
      <form v-if="passwordOpen" class="account-modal" @click.stop @submit.prevent="changePassword">
        <button type="button" class="modal-close" @click="passwordOpen = false; passwordError = ''">×</button>
        <h2>비밀번호 변경</h2>
        <label>현재 비밀번호<input v-model="passwordForm.current" type="password"></label>
        <label>새 비밀번호<input v-model="passwordForm.next" type="password"></label>
        <label>새 비밀번호 확인<input v-model="passwordForm.confirm" type="password"></label>
        <p v-if="passwordMismatch" class="error">새 비밀번호가 일치하지 않습니다.</p>
        <p v-else-if="passwordError" class="error">{{ passwordError }}</p>
        <AppButton full type="submit" :disabled="!canResetPassword">비밀번호 변경</AppButton>
      </form>
      <div v-else class="account-modal" @click.stop>
        <button type="button" class="modal-close" @click="deleteOpen = false; deleteError = ''">×</button>
        <h2>회원 탈퇴 확인</h2>
        <p>회원 탈퇴를 진행하려면 비밀번호 확인이 필요합니다.</p>
        <label>현재 비밀번호<input v-model="deletePassword" type="password"></label>
        <p v-if="deleteError" class="error">{{ deleteError }}</p>
        <div class="modal-actions">
          <AppButton variant="secondary" @click="deleteOpen = false">취소</AppButton>
          <AppButton variant="danger" :disabled="!deletePassword" @click="deleteAccount">탈퇴하기</AppButton>
        </div>
      </div>
    </div>

    <main class="left">
      <AppCard>
        <div class="section-title">
          <div>
            <div class="section-title-main">회원 정보</div>
            <div v-if="loadError" class="section-title-sub">{{ loadError }}</div>
          </div>
          <div class="edit-actions">
            <AppButton v-if="editing" variant="ghost" size="sm" @click="cancelEdit">취소</AppButton>
            <AppButton v-if="editing" size="sm" @click="saveProfile">저장</AppButton>
            <AppButton v-else variant="secondary" size="sm" @click="editing = true">수정</AppButton>
          </div>
        </div>
        <div class="profile-card">
          <div class="avatar-wrap">
            <img v-if="displayPhotoUrl && !profileImageFailed" :src="displayPhotoUrl" alt="프로필 사진" @error="profileImageFailed = true">
            <span v-else>{{ initial }}</span>
          </div>
          <div class="profile-meta">
            <strong>{{ profile.nickname || '이름 없음' }}</strong>
            <small>{{ profile.email || '이메일 없음' }}</small>
          </div>
          <div v-if="editing" class="photo-actions">
            <input ref="photoInput" class="hidden-file" type="file" accept="image/*" @change="uploadPhoto">
            <AppButton size="sm" variant="secondary" @click="photoInput?.click()">{{ profile.photoUrl ? '사진 수정' : '사진 업로드' }}</AppButton>
            <AppButton v-if="profile.photoUrl" size="sm" variant="danger" @click="removePhoto">사진 삭제</AppButton>
          </div>
        </div>
        <div v-if="editing" class="edit-form">
          <label>닉네임<input v-model="profile.nickname"></label>
          <label>이메일<input :value="profile.email" disabled></label>
        </div>
        <div v-else class="info-grid">
          <div v-for="item in info" :key="item[0]">
            <small>{{ item[0].toUpperCase() }}</small>
            <strong>{{ item[1] }}</strong>
          </div>
        </div>
      </AppCard>
    </main>

    <aside class="right">
      <AppCard>
        <div class="section-title-main">계정 설정</div>
        <div class="setting-list">
          <button @click="passwordOpen = true">
            <span><strong>비밀번호 변경</strong><small>현재 비밀번호 확인 후 변경</small></span>
            <AppIcon name="chev-r" color="var(--ink-3)" />
          </button>
          <button @click="emit('restartOnboarding')">
            <span><strong>온보딩 다시하기</strong><small>건강 목표와 기본 정보 재설정</small></span>
            <AppIcon name="chev-r" color="var(--ink-3)" />
          </button>
        </div>
      </AppCard>
      <AppCard>
        <div class="section-title-main">계정</div>
        <div class="account-buttons">
          <AppButton variant="secondary" full @click="handleLogout">로그아웃</AppButton>
          <AppButton variant="danger" full @click="deleteOpen = true">회원 탈퇴</AppButton>
        </div>
      </AppCard>
      <AppCard class="pet-card">
        <div class="section-title-main">냠냠이</div>
        <div class="pet-stage"><NyamnyamCharacter :stage="stage" :size="150" /></div>
        <p>{{ characterText }}</p>
      </AppCard>
    </aside>
  </section>
</template>

<script setup lang="ts">
import AppButton from '../components/common/AppButton.vue'
import AppCard from '../components/common/AppCard.vue'
import AppIcon from '../components/common/AppIcon.vue'
import NyamnyamCharacter from '../components/nyamnyam/NyamnyamCharacter.vue'
import { type Stage } from '../services/mock/nyamnyamMock'
import { computed, onMounted, reactive, ref } from 'vue'
import { myPageApi } from '../services/myPageApi'
import { userApi } from '../services/userApi'
import { resolveImageUrl } from '../utils/imageUrl'
import { genderLabel, healthGoalLabel } from '../utils/labels'

const props = defineProps<{ stage: Stage }>()
const emit = defineEmits<{ restartOnboarding: [], profileUpdated: [profile: { nickname: string; profileImageUrl: string }], logout: [] }>()

const editing = ref(false)
const passwordOpen = ref(false)
const deleteOpen = ref(false)
const photoInput = ref<HTMLInputElement | null>(null)
const loadError = ref('')
const deletePassword = ref('')
const deleteError = ref('')
const passwordError = ref('')
const profileImageFailed = ref(false)
const profile = reactive({ nickname: '', email: '', height: '', weight: '', gender: '', goal: '', birthDate: '', joinedAt: '', photoUrl: '' })
const savedProfile = reactive({ nickname: '', photoUrl: '' })
const passwordForm = reactive({ current: '', next: '', confirm: '' })
const passwordMismatch = computed(() => !!passwordForm.next && !!passwordForm.confirm && passwordForm.next !== passwordForm.confirm)
const canResetPassword = computed(() => !!passwordForm.current && !!passwordForm.next && !!passwordForm.confirm && !passwordMismatch.value)
const initial = computed(() => profile.nickname.trim().slice(0, 1) || '?')
const displayPhotoUrl = computed(() => resolveImageUrl(profile.photoUrl))
const info = computed(() => [
  ['키', profile.height || '-'],
  ['몸무게', profile.weight || '-'],
  ['성별', profile.gender || '-'],
  ['건강 목표', profile.goal || '-'],
  ['가입일', profile.joinedAt || '-'],
  ['생년월일', profile.birthDate || '-']
])
const characterText = computed(() => `${props.stage} 단계`)

function closeModals() {
  passwordOpen.value = false
  deleteOpen.value = false
  passwordError.value = ''
  deleteError.value = ''
}

function cancelEdit() {
  profile.nickname = savedProfile.nickname
  profile.photoUrl = savedProfile.photoUrl
  editing.value = false
}

function uploadPhoto(event: Event) {
  const file = (event.target as HTMLInputElement).files?.[0]
  if (!file) return
  const prevUrl = profile.photoUrl
  if (prevUrl.startsWith('blob:')) URL.revokeObjectURL(prevUrl)
  const blobUrl = URL.createObjectURL(file)
  profile.photoUrl = blobUrl
  profileImageFailed.value = false
  savedProfile.photoUrl = blobUrl
  userApi.uploadProfileImage(file)
    .then(() => loadMyPage())
    .catch((error) => {
      console.warn('Profile image upload API failed', error)
      URL.revokeObjectURL(blobUrl)
      profile.photoUrl = prevUrl
      savedProfile.photoUrl = prevUrl
    })
}

function removePhoto() {
  if (profile.photoUrl.startsWith('blob:')) URL.revokeObjectURL(profile.photoUrl)
  profile.photoUrl = ''
  profileImageFailed.value = false
  if (photoInput.value) photoInput.value.value = ''
  userApi.deleteProfileImage()
    .then(() => loadMyPage())
    .catch((error) => console.warn('Profile image delete API failed', error))
}

async function loadMyPage() {
  try {
    const overview = await myPageApi.getMyPageOverview()
    profile.nickname = overview.user.nickname || ''
    profile.email = overview.user.email || ''
    profile.photoUrl = overview.user.profileImageUrl || overview.user.profileImagePath || ''
    profileImageFailed.value = false
    profile.height = overview.healthProfile.heightCm ? `${overview.healthProfile.heightCm} cm` : ''
    profile.weight = overview.healthProfile.weightKg ? `${overview.healthProfile.weightKg} kg` : ''
    profile.gender = genderLabel(overview.healthProfile.gender)
    profile.goal = healthGoalLabel(overview.healthProfile.healthGoal)
    profile.birthDate = overview.healthProfile.birthDate || ''
    profile.joinedAt = overview.user.createdAt ? overview.user.createdAt.slice(0, 10) : ''
    savedProfile.nickname = profile.nickname
    savedProfile.photoUrl = profile.photoUrl
    emit('profileUpdated', { nickname: profile.nickname, profileImageUrl: profile.photoUrl })
    loadError.value = ''
  } catch (error) {
    console.warn('MyPage overview API failed', error)
    loadError.value = error instanceof Error ? error.message : '마이페이지 정보를 불러오지 못했습니다.'
  }
}

async function saveProfile() {
  try {
    await userApi.updateMe({ nickname: profile.nickname })
    await loadMyPage()
    editing.value = false
  } catch (error) {
    console.warn('User update API failed', error)
  }
}

async function changePassword() {
  if (!canResetPassword.value) return
  passwordError.value = ''
  try {
    await userApi.changePassword({
      currentPassword: passwordForm.current,
      newPassword: passwordForm.next,
      newPasswordConfirm: passwordForm.confirm
    })
    passwordOpen.value = false
    passwordForm.current = ''
    passwordForm.next = ''
    passwordForm.confirm = ''
  } catch (error) {
    passwordError.value = error instanceof Error ? error.message : '비밀번호 변경에 실패했습니다.'
  }
}

async function deleteAccount() {
  if (!deletePassword.value) return
  deleteError.value = ''
  try {
    await userApi.deactivate({ password: deletePassword.value })
    deleteOpen.value = false
    emit('logout')
  } catch (error) {
    deleteError.value = error instanceof Error ? error.message : '회원 탈퇴에 실패했습니다.'
  }
}

function handleLogout() {
  emit('logout')
}

onMounted(() => {
  void loadMyPage()
})
</script>

<style scoped>
.mypage-layout { display: grid; grid-template-columns: 1.1fr 1fr; gap: 20px; }
.left, .right { display: flex; flex-direction: column; gap: 16px; }
.profile-card { display: flex; gap: 14px; align-items: center; padding: 14px 16px; background: var(--surface-alt); border-radius: 12px; margin-bottom: 16px; }
.avatar-wrap { width: 58px; height: 58px; border-radius: 29px; background: var(--accent); color: #fff; display: flex; align-items: center; justify-content: center; font-size: 20px; font-weight: 800; overflow: hidden; flex: 0 0 58px; }
.avatar-wrap img { width: 100%; height: 100%; object-fit: cover; display: block; }
.profile-meta { flex: 1; display: flex; flex-direction: column; min-width: 0; }
.profile-card strong { font-size: 18px; }
.profile-card small { font-size: 12px; color: var(--ink-2); margin-top: 1px; }
.photo-actions { display: flex; gap: 6px; flex-wrap: wrap; justify-content: flex-end; }
.hidden-file { display: none; }
.info-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 8px; }
.info-grid div { padding: 10px 12px; border: 1px solid var(--border); border-radius: 10px; }
.info-grid small { font-family: var(--mono); color: var(--ink-3); font-size: 10px; }
.info-grid strong { display: block; font-size: 13px; margin-top: 3px; }
.edit-actions { display: flex; gap: 6px; }
.edit-form { display: grid; grid-template-columns: repeat(2, 1fr); gap: 10px; }
.edit-form label, .account-modal label { display: flex; flex-direction: column; gap: 6px; font-size: 12px; font-weight: 800; }
.edit-form input, .account-modal input { height: 40px; border: 1.5px solid var(--border-strong); border-radius: 10px; padding: 0 12px; outline: 0; }
.edit-form input:disabled { background: var(--surface-alt); color: var(--ink-3); cursor: not-allowed; }
.setting-list, .account-buttons { display: flex; flex-direction: column; gap: 8px; margin-top: 10px; }
.setting-list button { display: flex; align-items: center; justify-content: space-between; padding: 14px 16px; border: 1px solid var(--border); border-radius: 12px; background: var(--surface); cursor: pointer; text-align: left; }
.setting-list span { display: flex; flex-direction: column; }
.setting-list strong { font-size: 14px; }
.setting-list small { font-size: 11px; color: var(--ink-3); margin-top: 2px; }
.pet-card { background: linear-gradient(180deg,#fffaf0 0%,#fff 100%); }
.pet-stage { display: flex; justify-content: center; padding-top: 14px; }
.pet-card p { text-align: center; color: var(--ink-2); font-size: 12px; margin: 6px 0 0; }
.modal-backdrop { position: fixed; inset: 0; z-index: 200; background: rgba(31,28,23,.32); display: flex; align-items: center; justify-content: center; padding: 24px; }
.account-modal { width: min(420px, 100%); background: var(--surface); border-radius: 18px; box-shadow: var(--shadow-lg); padding: 24px; position: relative; display: flex; flex-direction: column; gap: 12px; }
.account-modal h2 { margin: 0 0 6px; font-size: 20px; }
.modal-close { position: absolute; top: 12px; right: 12px; width: 30px; height: 30px; border: 0; border-radius: 15px; background: var(--surface-alt); cursor: pointer; color: var(--ink-2); font-size: 18px; }
.error { margin: -2px 0 0; color: var(--bad); font-size: 12px; }
.modal-actions { display: flex; justify-content: flex-end; gap: 8px; margin-top: 8px; }
</style>
