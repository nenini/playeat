<template>
  <section class="auth-page">
    <button class="auth-brand" type="button" @click="$emit('back')">
      <span>냠</span><strong>냠냠코치</strong>
    </button>
    <form class="auth-card" @submit.prevent="submit">
      <div class="auth-head">
        <p>START NYAMNYAM</p>
        <h1>회원가입</h1>
        <span>기본 계정을 만든 뒤 온보딩에서 목표 데이터를 설정해요.</span>
      </div>
      <label>이름<input v-model="name" /></label>
      <label>이메일<input v-model="email" type="email" /></label>
      <label>비밀번호<input v-model="password" type="password" /></label>
      <p class="password-guide">
        8~20자, 영문·숫자·특수문자를 각각 포함해주세요.
      </p>
      <p v-if="formError || apiError" class="form-error">
        {{ formError || apiError }}
      </p>
      <button class="submit" type="submit">회원가입 후 온보딩 시작</button>
      <div class="auth-switch">
        이미 계정이 있나요?
        <button type="button" @click="$emit('login')">로그인</button>
      </div>
    </form>
  </section>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
defineProps<{ apiError?: string }>();
const emit = defineEmits<{
  onboarding: [payload: { name: string; email: string; password: string }];
  login: [];
  back: [];
}>();
const name = ref("");
const email = ref("");
const password = ref("");
const formError = ref("");
const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const passwordPattern = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[^A-Za-z0-9\s]).{8,20}$/;

function submit() {
  const normalizedName = name.value.trim();
  const normalizedEmail = email.value.trim();
  if (!normalizedName) {
    formError.value = "이름을 입력해주세요.";
    return;
  }
  if (!emailPattern.test(normalizedEmail)) {
    formError.value = "올바른 이메일 형식을 입력해주세요.";
    return;
  }
  if (!passwordPattern.test(password.value)) {
    formError.value = "비밀번호 조건을 확인해주세요.";
    return;
  }
  formError.value = "";
  emit("onboarding", {
    name: normalizedName,
    email: normalizedEmail,
    password: password.value,
  });
}

watch([name, email, password], () => {
  formError.value = "";
});
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #fff1e8 0%, #faf7f0 60%);
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 46px 20px;
}
.auth-brand {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  border: 0;
  background: transparent;
  cursor: pointer;
  margin-bottom: 44px;
}
.auth-brand span {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: #f7d34b;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 900;
}
.auth-brand strong {
  font-size: 20px;
}
.auth-card {
  width: min(440px, 100%);
  background: #fff;
  border: 1px solid var(--border);
  border-radius: 18px;
  box-shadow: var(--shadow-lg);
  padding: 34px;
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
  font-size: 28px;
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
  height: 46px;
  border: 1.5px solid var(--border-strong);
  border-radius: 10px;
  padding: 0 14px;
  font-size: 14px;
  outline: 0;
}
input:focus {
  border-color: var(--accent);
}
.submit {
  width: 100%;
  height: 48px;
  border: 0;
  border-radius: 10px;
  background: var(--accent);
  color: #fff;
  font-weight: 900;
  cursor: pointer;
  margin-top: 8px;
}
.password-guide {
  margin: -8px 0 14px;
  color: var(--ink-3);
  font-size: 11px;
  line-height: 1.5;
}
.form-error {
  margin: 0 0 10px;
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
</style>
