<template>
  <StartPage v-if="view === 'start'" @start="showSignup" @login="showLogin" />
  <LoginPage v-else-if="view === 'login'" :api-error="authError" @done="login" @signup="showSignup" @back="showStart" />
  <SignupPage v-else-if="view === 'signup'" :api-error="authError" @onboarding="signup" @login="showLogin" @back="showStart" />
  <OnboardingPage v-else-if="view === 'onboarding'" :mode="onboardingMode" :api-error="onboardingError" @done="completeOnboarding" @cancel="enterApp" />
  <AppShell v-else :active-page="activePage" :logs-count="0" :streak="streak" :profile-image-url="currentUser?.profileImageUrl || currentUser?.profileImagePath" :profile-name="currentUser?.nickname" @navigate="go">
    <HomePage v-if="activePage === 'home'" :stage="stage" :equipped-weapon="equippedWeapon" @navigate="go" />
    <MealsPage v-else-if="activePage === 'meals'" :logs="[]" />
    <AnalyzePage v-else-if="activePage === 'analyze'" :logs="logs" />
    <BossPage v-else-if="activePage === 'boss'" :logs="logs" :is-leader="isGuildLeader" @navigate="go" />
    <GuildPage v-else-if="activePage === 'guild'" :is-leader="isGuildLeader" />
    <ShopPage v-else-if="activePage === 'shop'" :stage="stage" :equipped-weapon="equippedWeapon" :equipped-hat="equippedHat" @equip="equip" @unequip="unequip" />
    <MyPage v-else :stage="stage" @restart-onboarding="showOnboarding('edit')" @profile-updated="handleProfileUpdated" @logout="handleLogout" />
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
import { type MealLog, type PageId, type Stage } from './services/mock/nyamnyamMock'
import { authApi } from './services/api/authApi'
import { characterApi, stageFromBackend } from './services/characterApi'
import { userApi, type UserMeResponse } from './services/userApi'
import { tokenStorage } from './services/api'

type ViewMode = 'start' | 'login' | 'signup' | 'onboarding' | 'app'

function initialView(): ViewMode {
  const path = window.location.pathname
  if (path === '/') return 'start'
  if (path === '/login') return 'login'
  if (path === '/signup') return 'signup'
  if (!tokenStorage.getAccessToken()) return 'login'
  if (path === '/onboarding') return 'onboarding'
  return 'app'
}

const view = ref<ViewMode>(initialView())
const activePage = ref<PageId>(pageFromPath(window.location.pathname))
const logs = ref<MealLog[]>([])
const stage = ref<Stage>('chick')
const equippedWeapon = ref('stick')
const equippedHat = ref<string | null>(null)
const onboardingData = ref<Record<string, string | string[]> | null>(null)
const onboardingMode = ref<'signup' | 'edit'>('signup')
const onboardingError = ref('')
const currentUser = ref<UserMeResponse | null>(null)
const authError = ref('')
const isGuildLeader = ref(new URLSearchParams(window.location.search).get('role') === 'leader')
const currentDate = ref(toDateInputValue(new Date()))

const streak = computed(() => 0)

function go(page: PageId) {
  if (!tokenStorage.getAccessToken()) {
    showLogin(true)
    return
  }
  view.value = 'app'
  activePage.value = page
  window.history.pushState({}, '', page === 'home' ? '/home' : pathFromPage(page))
}

function showStart() {
  authError.value = ''
  view.value = 'start'
  window.history.pushState({}, '', '/')
}

function showLogin(replace = false) {
  authError.value = ''
  view.value = 'login'
  if (replace) window.history.replaceState({}, '', '/login')
  else window.history.pushState({}, '', '/login')
}

function showSignup() {
  authError.value = ''
  view.value = 'signup'
  window.history.pushState({}, '', '/signup')
}

function showOnboarding(mode: 'signup' | 'edit' = 'edit') {
  if (!tokenStorage.getAccessToken()) {
    showLogin(true)
    return
  }
  onboardingMode.value = mode
  onboardingError.value = ''
  view.value = 'onboarding'
  window.history.pushState({}, '', '/onboarding')
}

function enterApp() {
  if (!tokenStorage.getAccessToken()) {
    showLogin(true)
    return
  }
  view.value = 'app'
  activePage.value = 'home'
  window.history.pushState({}, '', '/home')
  void hydrateApp()
}

async function completeOnboarding(payload: Record<string, string | string[]>) {
  onboardingData.value = payload
  onboardingError.value = ''
  try {
    if (onboardingMode.value === 'edit') {
      await userApi.updateHealthProfileFromOnboarding(payload)
      view.value = 'app'
      activePage.value = 'mypage'
      window.history.pushState({}, '', '/mypage')
      void hydrateApp()
      return
    }
    await userApi.completeOnboarding(payload)
  } catch (error) {
    console.warn('Onboarding API failed', error)
    onboardingError.value = error instanceof Error ? error.message : '온보딩 저장에 실패했습니다.'
    return
  }
  enterApp()
}

async function handleLogout() {
  try {
    await authApi.logout()
  } catch (error) {
    console.warn('Logout API failed', error)
  } finally {
    tokenStorage.clear()
    currentUser.value = null
  }
  showStart()
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
  if (isProtectedPath(window.location.pathname) && !tokenStorage.getAccessToken()) {
    showLogin(true)
    return
  }
  view.value = initialView()
  if (view.value === 'app') activePage.value = pageFromPath(window.location.pathname)
})

async function login(payload: { email: string, password: string }) {
  authError.value = ''
  try {
    await authApi.login({ email: payload.email, password: payload.password })
    enterApp()
  } catch (error) {
    authError.value = error instanceof Error ? error.message : '로그인에 실패했습니다.'
  }
}

async function signup(payload: { name: string, email: string, password: string }) {
  authError.value = ''
  try {
    await authApi.signup({ email: payload.email, password: payload.password, nickname: payload.name })
    await authApi.login({ email: payload.email, password: payload.password })
    showOnboarding('signup')
  } catch (error) {
    authError.value = error instanceof Error ? error.message : '회원가입에 실패했습니다.'
  }
}

async function hydrateApp() {
  if (!tokenStorage.getAccessToken()) return
  const [characterResult, userResult] = await Promise.allSettled([hydrateCharacter(), userApi.getMe()])
  if (userResult.status === 'fulfilled') currentUser.value = userResult.value
  if (characterResult.status === 'rejected' && userResult.status === 'rejected' && !tokenStorage.getAccessToken()) showLogin(true)
}

function handleProfileUpdated(profile: { nickname: string; profileImageUrl: string }) {
  if (!currentUser.value) return
  currentUser.value = { ...currentUser.value, nickname: profile.nickname, profileImageUrl: profile.profileImageUrl }
}

function isProtectedPath(path: string) {
  return !['/', '/login', '/signup'].includes(path)
}

async function hydrateCharacter() {
  const character = await characterApi.getMe()
  stage.value = stageFromBackend(character.stage)
}

function toDateInputValue(date: Date) {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

onMounted(() => {
  if (isProtectedPath(window.location.pathname) && !tokenStorage.getAccessToken()) {
    showLogin(true)
    return
  }
  if (tokenStorage.getAccessToken()) void hydrateApp()
})
</script>
