<template>
  <section class="callback-page">
    <div class="callback-card">
      <span class="spinner" aria-hidden="true"></span>
      <h1>Google 로그인 처리 중입니다</h1>
      <p>인증 정보를 확인하고 있어요. 잠시만 기다려주세요.</p>
    </div>
  </section>
</template>

<style scoped>
.callback-page { min-height: 100vh; display: grid; place-items: center; padding: 24px; background: #fff7ed; }
.callback-card { width: min(420px,100%); padding: 40px 32px; border: 1px solid var(--border); border-radius: 18px; background: var(--surface); box-shadow: var(--shadow-lg); text-align: center; }
.spinner { display: block; width: 36px; height: 36px; margin: 0 auto 20px; border: 4px solid var(--accent-soft); border-top-color: var(--accent); border-radius: 50%; animation: spin .8s linear infinite; }
h1 { margin: 0; font-size: 22px; }
p { margin: 10px 0 0; color: var(--ink-2); font-size: 13px; }
@keyframes spin { to { transform: rotate(360deg); } }
</style>
