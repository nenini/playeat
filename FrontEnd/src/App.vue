<template>
  <AppShell :active-page="activePage" :logs-count="logs.length" :streak="streak" @navigate="go">
    <HomePage v-if="activePage === 'home'" :logs="logs" :stage="stage" :equipped-weapon="equippedWeapon" @navigate="go" />
    <MealsPage v-else-if="activePage === 'meals'" :logs="logs" @add-log="addLog" @remove-log="removeLog" />
    <AnalyzePage v-else-if="activePage === 'analyze'" :logs="logs" />
    <BossPage v-else-if="activePage === 'boss'" :logs="logs" @navigate="go" />
    <GuildPage v-else-if="activePage === 'guild'" />
    <ShopPage v-else-if="activePage === 'shop'" :stage="stage" :equipped-weapon="equippedWeapon" :equipped-hat="equippedHat" @equip="equip" @unequip="unequip" />
    <MyPage v-else :stage="stage" />
  </AppShell>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import AppShell from './components/layout/AppShell.vue'
import HomePage from './pages/HomePage.vue'
import MealsPage from './pages/MealsPage.vue'
import AnalyzePage from './pages/AnalyzePage.vue'
import BossPage from './pages/BossPage.vue'
import GuildPage from './pages/GuildPage.vue'
import ShopPage from './pages/ShopPage.vue'
import MyPage from './pages/MyPage.vue'
import { pageFromPath, pathFromPage } from './router/routes'
import { seedLogs, type MealKindId, type MealLog, type PageId, type Stage } from './services/mock/nyamnyamMock'

const activePage = ref<PageId>(pageFromPath(window.location.pathname))
const logs = ref<MealLog[]>([...seedLogs])
const stage = ref<Stage>('chick')
const equippedWeapon = ref('stick')
const equippedHat = ref<string | null>(null)

const streak = computed(() => Math.max(12, logs.value.length + 10))

function go(page: PageId) {
  activePage.value = page
  window.history.pushState({}, '', pathFromPage(page))
}

function addLog(payload: { foodId: string, mealKind: MealKindId, qty: number }) {
  logs.value.push({ id: `log-${Date.now()}`, ...payload })
}

function removeLog(id: string) {
  logs.value = logs.value.filter((log) => log.id !== id)
}

function equip(item: { id: string, slot: string }) {
  if (item.slot === 'head') equippedHat.value = item.id
  else equippedWeapon.value = item.id
}

function unequip(slot: 'head' | 'hand') {
  if (slot === 'head') equippedHat.value = null
  else equippedWeapon.value = ''
}

window.addEventListener('popstate', () => {
  activePage.value = pageFromPath(window.location.pathname)
})
</script>
