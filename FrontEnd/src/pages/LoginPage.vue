<template>
  <section class="auth-page">
    <button class="auth-brand" type="button" @click="$emit('back')"><span>냠</span><strong>냠냠코치</strong></button>
    <form class="auth-card" @submit.prevent="submit">
      <div class="auth-head"><p>WELCOME BACK</p><h1>로그인</h1><span>식단 기록과 냠냠이 성장을 이어가세요.</span></div>
      <label>이메일<input v-model="email" type="email" placeholder="jieun@example.com"></label>
      <label>비밀번호<input v-model="password" type="password" placeholder="비밀번호"></label>
      <button class="submit" type="submit">로그인</button>
      <div class="auth-switch">계정이 없나요? <button type="button" @click="$emit('signup')">회원가입</button></div>
    </form>
  </section>
</template>

<script setup lang="ts">
import { ref } from 'vue'
const emit = defineEmits<{ done: [payload: { email: string, password: string }], signup: [], back: [] }>()
const email = ref('jieun@example.com')
const password = ref('')
function submit() {
  emit('done', { email: email.value, password: password.value })
}
</script>

<style scoped>
.auth-page { min-height: 100vh; background: linear-gradient(180deg,#fff1e8 0%,#faf7f0 60%); display: flex; flex-direction: column; align-items: center; padding: 46px 20px; }
.auth-brand { display: inline-flex; align-items: center; gap: 10px; border: 0; background: transparent; cursor: pointer; margin-bottom: 44px; } .auth-brand span { width: 36px; height: 36px; border-radius: 10px; background: #f7d34b; color: #fff; display: flex; align-items: center; justify-content: center; font-weight: 900; } .auth-brand strong { font-size: 20px; }
.auth-card { width: min(440px, 100%); background: #fff; border: 1px solid var(--border); border-radius: 18px; box-shadow: var(--shadow-lg); padding: 34px; }
.auth-head { text-align: center; margin-bottom: 26px; } .auth-head p { margin: 0 0 8px; font-family: var(--mono); font-size: 11px; letter-spacing: 1.4px; color: var(--accent); } .auth-head h1 { margin: 0; font-size: 28px; } .auth-head span { display: block; color: var(--ink-2); font-size: 13px; margin-top: 8px; }
label { display: flex; flex-direction: column; gap: 8px; font-size: 12px; font-weight: 800; margin-bottom: 16px; } input { height: 46px; border: 1.5px solid var(--border-strong); border-radius: 10px; padding: 0 14px; font-size: 14px; outline: 0; } input:focus { border-color: var(--accent); }
.submit { width: 100%; height: 48px; border: 0; border-radius: 10px; background: var(--accent); color: #fff; font-weight: 900; cursor: pointer; margin-top: 8px; }
.auth-switch { text-align: center; color: var(--ink-2); font-size: 13px; margin-top: 18px; } .auth-switch button { border: 0; background: transparent; color: var(--accent-dark); font-weight: 900; cursor: pointer; }
</style>
