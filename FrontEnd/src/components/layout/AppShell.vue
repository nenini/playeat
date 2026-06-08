<template>
  <div class="app-shell">
    <header class="topbar">
      <button class="brand" type="button" @click="$emit('navigate', 'home')">
        <NyamnyamCharacter stage="chick" :size="36" />
        <span>NYAMNYAM</span>
      </button>
      <nav class="topnav">
        <button v-for="page in pages" :key="page.id" class="nav-tab" :class="{ active: activePage === page.id }" type="button" @click="$emit('navigate', page.id)">
          <AppIcon :name="page.icon" :color="activePage === page.id ? 'var(--accent)' : 'var(--ink-2)'" />
          {{ page.label }}
          <span v-if="activePage === page.id" class="active-line" />
        </button>
      </nav>
      <div class="right-tools">
        <AppPill tone="accent" size="sm"><AppIcon name="fire" :size="11" color="var(--accent)" />{{ streak }}일 연속</AppPill>
        <AppPill tone="neutral" size="sm">기록 {{ logsCount }}/4</AppPill>
        <button class="profile" :class="{ active: activePage === 'mypage' }" type="button" title="마이페이지" @click="$emit('navigate', 'mypage')">👩</button>
      </div>
    </header>
    <main class="page-container">
      <slot />
    </main>
  </div>
</template>

<script setup lang="ts">
import AppIcon from '../common/AppIcon.vue'
import AppPill from '../common/AppPill.vue'
import NyamnyamCharacter from '../nyamnyam/NyamnyamCharacter.vue'
import { pages, type PageId } from '../../services/mock/nyamnyamMock'

defineProps<{ activePage: PageId, logsCount: number, streak: number }>()
defineEmits<{ navigate: [page: PageId] }>()
</script>

<script lang="ts">
export default { name: 'AppShell' }
</script>

<style scoped>
.app-shell {
  min-height: 100vh;
  background: var(--bg);
  color: var(--ink);
  display: flex;
  flex-direction: column;
}
.topbar {
  height: 64px;
  flex: 0 0 64px;
  background: var(--surface);
  border-bottom: 1px solid var(--border);
  display: flex;
  align-items: center;
  padding: 0 32px;
  gap: 28px;
  position: sticky;
  top: 0;
  z-index: 50;
}
.brand {
  border: 0;
  background: transparent;
  padding: 0;
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  color: var(--ink);
}
.brand span {
  font-family: var(--mono);
  font-size: 14px;
  font-weight: 800;
  letter-spacing: 1.5px;
}
.topnav {
  display: flex;
  gap: 4px;
  flex: 1;
}
.nav-tab {
  border: 0;
  cursor: pointer;
  background: transparent;
  padding: 8px 16px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 500;
  color: var(--ink-2);
  position: relative;
}
.nav-tab.active { font-weight: 800; color: var(--ink); }
.active-line {
  position: absolute;
  bottom: -10px;
  left: 16px;
  right: 16px;
  height: 3px;
  background: var(--accent);
  border-radius: 2px;
}
.right-tools {
  display: flex;
  align-items: center;
  gap: 12px;
}
.profile {
  width: 36px;
  height: 36px;
  border-radius: 18px;
  background: var(--yolk);
  border: 1.5px solid var(--border);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  cursor: pointer;
}
.profile.active {
  background: var(--accent);
  border-color: var(--accent-dark);
}
.page-container {
  flex: 1;
  padding: 24px 32px 48px;
  max-width: 1320px;
  width: 100%;
  margin: 0 auto;
}
</style>
