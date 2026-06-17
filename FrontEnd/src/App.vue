<template>
  <StartPage v-if="view === 'start'" @start="showSignup" @login="showLogin" />
  <LoginPage v-else-if="view === 'login'" @done="login" @signup="showSignup" @back="showStart" />
  <SignupPage v-else-if="view === 'signup'" @onboarding="signup" @login="showLogin" @back="showStart" />
  <OnboardingPage v-else-if="view === 'onboarding'" @done="completeOnboarding" @cancel="enterApp" />
  <AppShell v-else :active-page="activePage" :logs-count="logs.length" :streak="streak" @navigate="go">
    <HomePage v-if="activePage === 'home'" :logs="logs" :stage="stage" :equipped-weapon="equippedWeapon" @navigate="go" />
    <MealsPage v-else-if="activePage === 'meals'" :logs="logs" @add-log="addLog" @remove-log="removeLog" />
    <AnalyzePage v-else-if="activePage === 'analyze'" :logs="logs" />
    <BossPage v-else-if="activePage === 'boss'" :logs="logs" :is-leader="isGuildLeader" @navigate="go" />
    <GuildPage v-else-if="activePage === 'guild'" :is-leader="isGuildLeader" />
    <ShopPage v-else-if="activePage === 'shop'" :stage="stage" :equipped-weapon="equippedWeapon" :equipped-hat="equippedHat" @equip="equip" @unequip="unequip" />
    <MyPage v-else :stage="stage" @restart-onboarding="showOnboarding" />
  </AppShell>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import AppShell from './components/layout/AppShell.vue'
import StartPage from './pages/StartPage.vue'
import LoginPage from './pages/LoginPage.vue'
import SignupPage from './pages/SignupPage.vue'
import OnboardingPage from './pages/OnboardingPage.vue'
import HomePage from './pages/HomePage.vue'
import MealsPage from './pages/MealsPage.vue'
import AnalyzePage from './pages/AnalyzePage.vue'
import BossPage from './pages/BossPage.vue'
import GuildPage from './pages/GuildPage.vue'
import ShopPage from './pages/ShopPage.vue'
import MyPage from './pages/MyPage.vue'
import { pageFromPath, pathFromPage } from './router/routes'
import { seedLogs, type MealKindId, type MealLog, type PageId, type Stage } from './services/mock/nyamnyamMock'
import { authApi, characterApi, dietApi, logsFromDietDay, stageFromBackend, userApi } from './services/nyamnyamApi'
import { tokenStorage } from './services/api'

type ViewMode = 'start' | 'login' | 'signup' | 'onboarding' | 'app'

function initialView(): ViewMode {
  if (window.location.pathname === '/') return 'start'
  if (window.location.pathname === '/login') return 'login'
  if (window.location.pathname === '/signup') return 'signup'
  if (window.location.pathname === '/onboarding') return 'onboarding'
  return 'app'
}

const view = ref<ViewMode>(initialView())
const activePage = ref<PageId>(pageFromPath(window.location.pathname))
const logs = ref<MealLog[]>([...seedLogs])
const stage = ref<Stage>('chick')
const equippedWeapon = ref('stick')
const equippedHat = ref<string | null>(null)
const onboardingData = ref<Record<string, string | string[]> | null>(null)
const isGuildLeader = ref(new URLSearchParams(window.location.search).get('role') === 'leader')
const currentDate = ref(toDateInputValue(new Date()))

const streak = computed(() => Math.max(12, logs.value.length + 10))

function go(page: PageId) {
  view.value = 'app'
  activePage.value = page
  window.history.pushState({}, '', pathFromPage(page))
}

function showStart() {
  view.value = 'start'
  window.history.pushState({}, '', '/')
}

function showLogin() {
  view.value = 'login'
  window.history.pushState({}, '', '/login')
}

function showSignup() {
  view.value = 'signup'
  window.history.pushState({}, '', '/signup')
}

function showOnboarding() {
  view.value = 'onboarding'
  window.history.pushState({}, '', '/onboarding')
}

function enterApp() {
  view.value = 'app'
  activePage.value = 'home'
  window.history.pushState({}, '', '/home')
  void hydrateApp()
}

function completeOnboarding(payload: Record<string, string | string[]>) {
  onboardingData.value = payload
  userApi.completeOnboarding(payload).catch((error) => console.warn('Onboarding API failed', error))
  enterApp()
}

function addLog(payload: { foodId: string, mealKind: MealKindId, qty: number, inputUnit?: string, date?: string }) {
  logs.value.push({ id: `log-${Date.now()}`, ...payload })
  if (payload.date) currentDate.value = payload.date
  dietApi.create({ ...payload, date: payload.date || currentDate.value }).then(() => hydrateMeals()).catch((error) => console.warn('Diet create API failed', error))
}

function removeLog(id: string) {
  logs.value = logs.value.filter((log) => log.id !== id)
  if (/^\d+$/.test(id)) dietApi.remove(id).then(() => hydrateMeals()).catch((error) => console.warn('Diet delete API failed', error))
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
  view.value = initialView()
  if (view.value === 'app') activePage.value = pageFromPath(window.location.pathname)
})

async function login(payload: { email: string, password: string }) {
  await authApi.login(payload.email, payload.password)
  enterApp()
}

async function signup(payload: { name: string, email: string, password: string }) {
  await authApi.signup(payload.email, payload.password, payload.name)
  await authApi.login(payload.email, payload.password)
  showOnboarding()
}

async function hydrateApp() {
  if (!tokenStorage.getAccessToken()) return
  await Promise.allSettled([hydrateMeals(), hydrateCharacter(), userApi.me()])
}

async function hydrateMeals() {
  const day = await dietApi.getDay(currentDate.value)
  const nextLogs = logsFromDietDay(day)
  if (nextLogs.length) logs.value = nextLogs
}

async function hydrateCharacter() {
  const character = await characterApi.me()
  stage.value = stageFromBackend(character.stage)
}

function toDateInputValue(date: Date) {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

onMounted(() => {
  if (tokenStorage.getAccessToken()) void hydrateApp()
})
</script>
