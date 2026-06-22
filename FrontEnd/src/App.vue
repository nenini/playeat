<template>
  <StartPage v-if="view === 'start'" @start="showSignup" @login="showLogin" />
  <LoginPage v-else-if="view === 'login'" :api-error="authError" @done="login" @google-login="startGoogleLogin" @signup="showSignup" @back="showStart" />
  <SignupPage v-else-if="view === 'signup'" :api-error="authError" @onboarding="signup" @login="showLogin" @back="showStart" />
  <OnboardingPage v-else-if="view === 'onboarding'" :mode="onboardingMode" :api-error="onboardingError" @done="completeOnboarding" @cancel="enterApp" />
  <OAuthCallbackPage v-else-if="view === 'oauthCallback'" />
  <AppShell v-else :active-page="activePage" :logs-count="0" :streak="streak" :profile-image-url="currentUser?.profileImageUrl || currentUser?.profileImagePath" :profile-name="currentUser?.nickname" @navigate="go">
    <HomePage v-if="activePage === 'home'" :stage="stage" :equipped-weapon="equippedWeapon" @navigate="go" />
    <MealsPage v-else-if="activePage === 'meals'" :logs="[]" @diet-changed="refreshCharacter" />
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
import OAuthCallbackPage from './pages/OAuthCallbackPage.vue'
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

type ViewMode = 'start' | 'login' | 'signup' | 'onboarding' | 'oauthCallback' | 'app'

const GOOGLE_CALLBACK_PATH = '/oauth/google/callback'
const GOOGLE_OAUTH_STATE_KEY = 'nyamnyam.googleOAuthState'

function initialView(): ViewMode {
  const path = window.location.pathname
  if (path === '/') return 'start'
  if (path === '/login') return 'login'
  if (path === '/signup') return 'signup'
  if (path === GOOGLE_CALLBACK_PATH) return 'oauthCallback'
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
const streakDays = ref(0)
const onboardingData = ref<Record<string, string | string[]> | null>(null)
const onboardingMode = ref<'signup' | 'edit'>('signup')
const onboardingError = ref('')
const currentUser = ref<UserMeResponse | null>(null)
const authError = ref('')
const isGuildLeader = ref(new URLSearchParams(window.location.search).get('role') === 'leader')
const currentDate = ref(toDateInputValue(new Date()))

const streak = computed(() => streakDays.value)

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

function showLogin(replace = false, message = '') {
  authError.value = message
  view.value = 'login'
  if (replace) window.history.replaceState({}, '', '/login')
  else window.history.pushState({}, '', '/login')
}

function showSignup() {
  authError.value = ''
  view.value = 'signup'
  window.history.pushState({}, '', '/signup')
}

function showOnboarding(mode: 'signup' | 'edit' = 'edit', replace = false) {
  if (!tokenStorage.getAccessToken()) {
    showLogin(true)
    return
  }
  onboardingMode.value = mode
  onboardingError.value = ''
  view.value = 'onboarding'
  if (replace) window.history.replaceState({}, '', '/onboarding')
  else window.history.pushState({}, '', '/onboarding')
}

function enterApp(replace = false) {
  if (!tokenStorage.getAccessToken()) {
    showLogin(true)
    return
  }
  view.value = 'app'
  activePage.value = 'home'
  if (replace) window.history.replaceState({}, '', '/home')
  else window.history.pushState({}, '', '/home')
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
    streakDays.value = 0
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
  if (view.value === 'oauthCallback') {
    void handleGoogleCallback()
    return
  }
  if (view.value === 'app') activePage.value = pageFromPath(window.location.pathname)
})

async function login(payload: { email: string, password: string }) {
  authError.value = ''
  try {
    const response = await authApi.login({ email: payload.email, password: payload.password })
    routeAfterAuthentication(response.user.onboardingCompleted)
  } catch (error) {
    authError.value = error instanceof Error ? error.message : '로그인에 실패했습니다.'
  }
}

function startGoogleLogin() {
  authError.value = ''
  const clientId = String(import.meta.env.VITE_GOOGLE_CLIENT_ID || '').trim()
  if (!clientId) {
    authError.value = 'Google 로그인 환경변수가 설정되지 않았습니다.'
    return
  }

  const redirectUri = `${window.location.origin}${GOOGLE_CALLBACK_PATH}`
  const state = crypto.randomUUID()
  sessionStorage.setItem(GOOGLE_OAUTH_STATE_KEY, state)

  const authorizationUrl = new URL('https://accounts.google.com/o/oauth2/v2/auth')
  authorizationUrl.search = new URLSearchParams({
    client_id: clientId,
    redirect_uri: redirectUri,
    response_type: 'code',
    scope: 'openid email profile',
    prompt: 'select_account',
    state
  }).toString()
  window.location.assign(authorizationUrl.toString())
}

let googleCallbackProcessing = false

async function handleGoogleCallback() {
  if (googleCallbackProcessing) return
  googleCallbackProcessing = true
  view.value = 'oauthCallback'

  const params = new URLSearchParams(window.location.search)
  const oauthError = params.get('error')
  const code = params.get('code')
  const state = params.get('state')
  const expectedState = sessionStorage.getItem(GOOGLE_OAUTH_STATE_KEY)
  sessionStorage.removeItem(GOOGLE_OAUTH_STATE_KEY)

  try {
    if (oauthError) {
      showLogin(true, params.get('error_description') || 'Google 로그인이 취소되었습니다.')
      return
    }
    if (!code) {
      showLogin(true, 'Google 인증 코드를 확인할 수 없습니다.')
      return
    }
    if (!state || !expectedState || state !== expectedState) {
      showLogin(true, 'Google 로그인 요청을 확인할 수 없습니다. 다시 시도해주세요.')
      return
    }

    const response = await authApi.loginWithGoogle({
      code,
      redirectUri: `${window.location.origin}${GOOGLE_CALLBACK_PATH}`
    })
    routeAfterAuthentication(response.user.onboardingCompleted, true)
  } catch (error) {
    tokenStorage.clear()
    showLogin(true, error instanceof Error ? error.message : 'Google 로그인에 실패했습니다.')
  } finally {
    googleCallbackProcessing = false
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
  if (userResult.status === 'fulfilled') {
    currentUser.value = userResult.value
    if (!userResult.value.onboardingCompleted && view.value !== 'onboarding') {
      showOnboarding('signup', true)
    }
  }
  if (characterResult.status === 'rejected' && userResult.status === 'rejected' && !tokenStorage.getAccessToken()) showLogin(true)
}

function routeAfterAuthentication(onboardingCompleted: boolean, replace = false) {
  if (onboardingCompleted) {
    enterApp(replace)
    return
  }
  showOnboarding('signup', replace)
}

function handleProfileUpdated(profile: { nickname: string; profileImageUrl: string }) {
  if (!currentUser.value) return
  currentUser.value = { ...currentUser.value, nickname: profile.nickname, profileImageUrl: profile.profileImageUrl }
}

function isProtectedPath(path: string) {
  return !['/', '/login', '/signup', GOOGLE_CALLBACK_PATH].includes(path)
}

async function hydrateCharacter() {
  const character = await characterApi.getMe()
  stage.value = stageFromBackend(character.stage)
  streakDays.value = Number(character.streakDays || 0)
}

async function refreshCharacter() {
  try {
    await hydrateCharacter()
  } catch (error) {
    console.warn('Character refresh API failed', error)
  }
}

function toDateInputValue(date: Date) {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

onMounted(() => {
  if (window.location.pathname === GOOGLE_CALLBACK_PATH) {
    void handleGoogleCallback()
    return
  }
  if (isProtectedPath(window.location.pathname) && !tokenStorage.getAccessToken()) {
    showLogin(true)
    return
  }
  if (tokenStorage.getAccessToken()) void hydrateApp()
})
</script>
