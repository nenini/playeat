<template>
  <div class="app-shell">
    <header class="topbar">
      <button class="brand" type="button" @click="$emit('navigate', 'home')">
        <NyamnyamCharacter stage="baby" :size="36" />
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
        <button class="profile" :class="{ active: activePage === 'mypage' }" type="button" title="마이페이지" @click="$emit('navigate', 'mypage')">
          <img v-if="profileImage && !profileImageFailed" :src="profileImage" :alt="`${profileName || '사용자'} 프로필`" @error="profileImageFailed = true">
          <span v-else>{{ profileInitial }}</span>
        </button>
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
import { computed, ref, watch } from 'vue'
import { resolveImageUrl } from '../../utils/imageUrl'

const props = defineProps<{ activePage: PageId, logsCount: number, streak: number, profileImageUrl?: string, profileName?: string }>()
defineEmits<{ navigate: [page: PageId] }>()

const profileImageFailed = ref(false)
const profileImage = computed(() => resolveImageUrl(props.profileImageUrl))
const profileInitial = computed(() => props.profileName?.trim().slice(0, 1) || '냠')

watch(() => props.profileImageUrl, () => { profileImageFailed.value = false })
</script>

<script lang="ts">
export default { name: 'AppShell' }
</script>

<style scoped>
.app-shell {
  min-height: 100vh;
  background: transparent;
  color: var(--ink);
  display: flex;
  flex-direction: column;
}
.topbar {
  height: 72px;
  flex: 0 0 72px;
  background: rgba(255,255,255,.88);
  border-bottom: 1px solid var(--border);
  display: flex;
  align-items: center;
  padding: 0 34px;
  gap: 28px;
  position: sticky;
  top: 0;
  z-index: 50;
  backdrop-filter: blur(18px);
  box-shadow: 0 4px 18px rgba(100,61,35,.06);
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
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 500;
  color: var(--ink-2);
  position: relative;
}
.nav-tab:hover { background: var(--surface-alt); transform: translateY(-1px); }
.nav-tab.active { font-weight: 900; color: var(--ink); background: #f3fdd8; }
.active-line {
  position: absolute;
  bottom: -8px;
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
  border-radius: 50%;
  padding: 0;
  background: var(--yolk);
  border: 1px solid var(--border);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  cursor: pointer;
  overflow: hidden;
  flex: 0 0 36px;
  box-shadow: none;
}
.profile.active { border-color: var(--accent); }
.profile img { width: 100%; height: 100%; object-fit: cover; display: block; border-radius: 50%; }
.profile span { display: flex; align-items: center; justify-content: center; width: 100%; height: 100%; font-weight: 800; background: var(--yolk); color: var(--ink); }
.profile.active span { background: var(--accent); color: #fff; }
.page-container {
  flex: 1;
  padding: 30px clamp(16px, 2.5vw, 48px) 56px;
  max-width: none;
  width: 80%;
  margin: 0 auto;
}
</style>
