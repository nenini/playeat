<template>
  <section class="auth-page">
    <button class="auth-brand" type="button" @click="$emit('back')">
      <span>냠</span><strong>냠냠코치</strong>
    </button>
    <div class="auth-companion"><div class="companion-ring"><NyamnyamCharacter stage="baby" :size="150" /></div><b>모험가님, 다시 만났네요!</b><span>오늘의 퀘스트가 기다리고 있어요.</span></div>
    <form class="auth-card" @submit.prevent="submit">
      <div class="auth-head">
        <p>RETURN TO ADVENTURE</p>
        <h1>모험 이어하기</h1>
        <span>식단 기록과 냠냠이 성장을 이어가세요.</span>
      </div>
      <label>이메일<input v-model="email" type="email" /></label>
      <label>비밀번호<input v-model="password" type="password" /></label>
      <p v-if="formError || apiError" class="form-error">
        {{ formError || apiError }}
      </p>
      <button class="submit" type="submit">로그인</button>
      <div class="auth-divider"><span>또는</span></div>
      <button class="google-login" type="button" @click="$emit('google-login')">
        <b aria-hidden="true">G</b>
        Google로 로그인
      </button>
      <div class="auth-switch">
        계정이 없나요?
        <button type="button" @click="$emit('signup')">회원가입</button>
      </div>
    </form>
  </section>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import NyamnyamCharacter from '../components/nyamnyam/NyamnyamCharacter.vue'
defineProps<{ apiError?: string }>();
const emit = defineEmits<{
  done: [payload: { email: string; password: string }];
  "google-login": [];
  signup: [];
  back: [];
}>();
const email = ref("");
const password = ref("");
const formError = ref("");
const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const passwordPattern = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[^A-Za-z0-9\s]).{8,20}$/;

function submit() {
  const normalizedEmail = email.value.trim();
  if (!emailPattern.test(normalizedEmail)) {
    formError.value = "올바른 이메일 형식을 입력해주세요.";
    return;
  }
  if (!passwordPattern.test(password.value)) {
    formError.value =
      "비밀번호는 8~20자이며 영문, 숫자, 특수문자를 포함해야 합니다.";
    return;
  }
  formError.value = "";
  emit("done", { email: normalizedEmail, password: password.value });
}

watch([email, password], () => {
  formError.value = "";
});
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  background: linear-gradient(180deg,#fffaf3,#fff0e2);
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 42px 20px 70px;
  position: relative;
  overflow: hidden;
}
.auth-brand {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  border: 0;
  background: transparent;
  cursor: pointer;
  margin-bottom: 34px;
  z-index: 2;
}
.auth-brand span {
  width: 36px;
  height: 36px;
  border-radius: 12px;
  background: linear-gradient(180deg,#B8DB80,var(--accent-dark));
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 900;
  box-shadow: 0 3px 0 var(--accent-dark);
}
.auth-brand strong {
  font-size: 20px;
}
.auth-card {
  width: min(440px, 100%);
  background: #fff;
  border: 1px solid var(--border);
  border-radius: 22px;
  box-shadow: var(--shadow-lg);
  padding: 38px;
  position: relative;
  z-index: 2;
  background: linear-gradient(160deg,rgba(255,255,255,.98),rgba(255,249,241,.98));
  border-color: #e5c5a8;
}
.auth-head {
  text-align: center;
  margin-bottom: 26px;
}
.auth-head p {
  margin: 0 0 8px;
  font-family: var(--mono);
  font-size: 11px;
  letter-spacing: 1.4px;
  color: var(--accent);
}
.auth-head h1 {
  margin: 0;
  font-size: 30px;
}
.auth-head span {
  display: block;
  color: var(--ink-2);
  font-size: 13px;
  margin-top: 8px;
}
label {
  display: flex;
  flex-direction: column;
  gap: 8px;
  font-size: 12px;
  font-weight: 800;
  margin-bottom: 16px;
}
input {
  height: 50px;
  border: 1.5px solid var(--border-strong);
  border-radius: 12px;
  padding: 0 14px;
  font-size: 14px;
  outline: 0;
}
input:focus {
  border-color: var(--accent);
  box-shadow: 0 0 0 3px rgba(143,207,85,.12);
}
.submit {
  width: 100%;
  height: 48px;
  border: 0;
  border-radius: 12px;
  background: linear-gradient(180deg,#B8DB80,var(--accent-dark));
  color: #fff;
  font-weight: 900;
  cursor: pointer;
  margin-top: 8px;
  box-shadow: 0 4px 0 var(--accent-dark),0 10px 20px rgba(143,207,85,.2);
}
.auth-divider { display: flex; align-items: center; gap: 12px; margin: 18px 0; color: var(--ink-3); font-size: 11px; }
.auth-divider::before,.auth-divider::after { content: ""; flex: 1; height: 1px; background: var(--border); }
.google-login { width: 100%; height: 48px; border: 1.5px solid var(--border-strong); border-radius: 12px; background: #fff; color: var(--ink); display: flex; align-items: center; justify-content: center; gap: 10px; font-weight: 800; cursor: pointer; box-shadow: 0 3px 0 rgba(116,75,49,.1); }
.google-login b { font-family: Arial,sans-serif; color: #4285f4; font-size: 18px; }
.form-error {
  margin: -4px 0 10px;
  color: var(--bad);
  font-size: 12px;
  line-height: 1.5;
}
.auth-switch {
  text-align: center;
  color: var(--ink-2);
  font-size: 13px;
  margin-top: 18px;
}
.auth-switch button {
  border: 0;
  background: transparent;
  color: var(--accent-dark);
  font-weight: 900;
  cursor: pointer;
}
.auth-companion { position: absolute; left: max(40px,calc(50% - 490px)); top: 50%; transform: translateY(-42%); width: 270px; text-align: center; display: flex; flex-direction: column; align-items: center; gap: 6px; } .companion-ring { width: 250px; height: 250px; border-radius: 50%; display: flex; align-items: center; justify-content: center; background: radial-gradient(circle,#fff,#fff0cf 60%,#ffc89f); border: 4px solid #fff; box-shadow: 0 0 0 8px rgba(240,120,60,.12),var(--shadow-lg); animation: auth-float 4s ease-in-out infinite; } .auth-companion b { margin-top: 22px; font-size: 17px; } .auth-companion span { color: var(--ink-2); font-size: 12px; }
@keyframes auth-float { 0%,100% { transform: translateY(0); } 50% { transform: translateY(-7px); } }
@media (max-width: 1040px) { .auth-companion { display: none; } }
</style>
