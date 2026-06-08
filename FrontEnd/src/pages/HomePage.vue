<template>
  <section>
    <div class="greeting">
      <div>
        <div class="mono-label">{{ dayLabel().toUpperCase() }}</div>
        <div class="hello">안녕, 지은님 👋</div>
        <div class="muted">오늘도 냠냠이와 함께 잘 챙겨봐요.</div>
      </div>
      <AppButton @click="$emit('navigate', 'meals')"><AppIcon name="plus" color="#fff" />식단 기록하기</AppButton>
    </div>

    <AppCard :padding="32" class="hero">
      <div class="hero-grid">
        <div class="hero-side right">
          <HeroStat label="HEALTH SCORE" :value="score" unit="점" tone="accent" :sub="score >= 75 ? '↑ 좋아요' : '조금만 더!'" />
          <HeroStat label="LEVEL" value="LV.7" sub="냠냠이 · 병아리" />
        </div>
        <div class="mascot-wrap">
          <div class="mascot-stage">
            <NyamnyamCharacter :stage="stage" :size="240" :mood="logs.length === 0 ? 'hungry' : 'happy'" />
            <div v-if="equippedWeapon" class="weapon-on-hand"><WeaponIcon :id="equippedWeapon" /></div>
            <div class="speech">{{ logs.length === 0 ? '"배고파요…"' : logs.length >= 3 ? '"오늘도 잘 먹고 있어요!"' : '"한 끼만 더 부탁해요!"' }}</div>
          </div>
          <ProgressBar :value="xp" :max="1200" label="LV.7 → LV.8" :sub="`${xp} / 1,200 XP`" tone="accent" :height="10" class="xp" />
        </div>
        <div class="hero-side">
          <HeroStat label="STREAK" :value="streak" unit="일" tone="accent" sub="개인 최고 14일" />
          <HeroStat label="TODAY KCAL" :value="Math.round(totals.kcal).toLocaleString()" unit="kcal" :sub="`목표 2,000 · ${Math.round(totals.kcal / 2000 * 100)}%`" />
        </div>
      </div>
    </AppCard>

    <AppCard class="meal-strip">
      <div class="section-title">
        <div><div class="section-title-main">오늘의 끼니</div><div class="section-title-sub">아침 / 점심 / 간식 / 저녁</div></div>
        <AppButton variant="ghost" size="sm" @click="$emit('navigate', 'meals')">전체 보기 →</AppButton>
      </div>
      <div class="meal-grid">
        <button v-for="kind in mealKinds" :key="kind.id" class="meal-tile" :class="{ done: byKind[kind.id]?.length }" @click="$emit('navigate', 'meals')">
          <div class="tile-head"><strong>{{ kind.emoji }} {{ kind.label }}</strong><span>{{ kind.window }}</span></div>
          <div class="tile-body">{{ mealNames(kind.id) || '아직 기록 안 함' }}</div>
          <div class="tile-foot"><span>{{ byKind[kind.id]?.length ? `${Math.round(totalsFor(byKind[kind.id]).kcal)} kcal` : '— kcal' }}</span><AppPill :tone="byKind[kind.id]?.length ? 'ok' : 'accent'" size="sm">{{ byKind[kind.id]?.length ? '✓ 완료' : '+ 추가' }}</AppPill></div>
        </button>
      </div>
    </AppCard>

    <div class="banners">
      <AppCard class="quest-card">
        <div class="banner-row">
          <div class="quest-emoji">🥬</div>
          <div class="grow"><AppPill tone="accent" size="sm">보스 퀘스트</AppPill><h3>채소 2종 이상 기록하기</h3><p>+ XP 120 · 채식 뱃지 · {{ veggieCount(logs) }}/2 진행 중</p></div>
          <AppButton size="sm" @click="$emit('navigate', 'meals')">이어가기 →</AppButton>
        </div>
        <ProgressBar :value="veggieCount(logs)" :max="2" :height="8" />
      </AppCard>
      <AppCard>
        <div class="banner-row">
          <BossMonster :size="80" :hp="bossHP" />
          <div class="grow"><AppPill tone="bad" size="sm">활성 보스 · D-3</AppPill><h3>당분 드래곤</h3><p>HP {{ bossHP }}/100 · 격파까지 6명 협력 중</p></div>
          <AppButton variant="secondary" size="sm" @click="$emit('navigate', 'boss')">전투장 →</AppButton>
        </div>
        <ProgressBar :value="bossHP" :max="100" tone="bad" :height="8" />
      </AppCard>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import AppButton from '../components/common/AppButton.vue'
import AppCard from '../components/common/AppCard.vue'
import AppIcon from '../components/common/AppIcon.vue'
import AppPill from '../components/common/AppPill.vue'
import ProgressBar from '../components/common/ProgressBar.vue'
import BossMonster from '../components/nyamnyam/BossMonster.vue'
import NyamnyamCharacter from '../components/nyamnyam/NyamnyamCharacter.vue'
import WeaponIcon from '../components/nyamnyam/WeaponIcon.vue'
import { boss, dayLabel, foodDb, healthScore, mealKinds, recordsByKind, totalsFor, veggieCount, type MealKindId, type MealLog, type PageId, type Stage } from '../services/mock/nyamnyamMock'
import HeroStat from './parts/HeroStat.vue'

const props = defineProps<{ logs: MealLog[], stage: Stage, equippedWeapon: string }>()
defineEmits<{ navigate: [page: PageId] }>()
const totals = computed(() => totalsFor(props.logs))
const score = computed(() => healthScore(totals.value))
const streak = computed(() => Math.max(12, props.logs.length + 10))
const xp = computed(() => 840 + props.logs.length * 30)
const bossHP = computed(() => Math.max(0, boss.baseHP - props.logs.length * 5))
const byKind = computed(() => recordsByKind(props.logs))
function mealNames(kindId: MealKindId) {
  return (byKind.value[kindId] || []).map((entry) => foodDb.find((food) => food.id === entry.foodId)?.name).filter(Boolean).join(' · ')
}
</script>

<style scoped>
.greeting { margin-bottom: 24px; display: flex; justify-content: space-between; align-items: flex-end; }
.hello { font-size: 28px; font-weight: 800; margin-top: 4px; letter-spacing: -0.5px; }
.hero { margin-bottom: 20px; background: linear-gradient(180deg, #fffaf0 0%, #ffffff 100%); }
.hero-grid { display: grid; grid-template-columns: 1fr auto 1fr; gap: 32px; align-items: center; }
.hero-side { display: flex; flex-direction: column; gap: 14px; align-items: flex-start; }
.hero-side.right { align-items: flex-end; }
.mascot-wrap { display: flex; flex-direction: column; align-items: center; gap: 14px; }
.mascot-stage { width: 280px; height: 280px; border-radius: 50%; background: radial-gradient(circle at 50% 40%, #fff5e0 0%, #fbe5d3 100%); border: 2px solid var(--border); display: flex; align-items: center; justify-content: center; position: relative; box-shadow: inset 0 -8px 24px rgba(232,138,77,0.08); }
.speech { position: absolute; bottom: -12px; background: var(--surface); padding: 8px 18px; border: 1.5px solid var(--border); border-radius: 999px; font-size: 14px; font-weight: 700; box-shadow: var(--shadow); white-space: nowrap; }
.weapon-on-hand { position: absolute; right: -36px; top: 26%; width: 58px; height: 195px; z-index: 2; pointer-events: none; }
.weapon-on-hand :deep(svg) { width: 58px; height: 195px; }
.xp { width: 280px; margin-top: 8px; }
.meal-strip { margin-bottom: 20px; }
.meal-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; }
.meal-tile { text-align: left; cursor: pointer; border: 1.5px dashed var(--border); border-radius: 12px; padding: 14px; background: var(--surface-alt); color: var(--ink); display: flex; flex-direction: column; gap: 8px; min-height: 110px; }
.meal-tile.done { border-style: solid; border-color: var(--border-strong); background: var(--surface); }
.tile-head, .tile-foot { display: flex; justify-content: space-between; align-items: baseline; }
.tile-head strong { font-size: 14px; } .tile-head span, .tile-foot span { font-family: var(--mono); font-size: 10px; color: var(--ink-3); }
.tile-body { flex: 1; font-size: 12px; color: var(--ink-2); font-style: italic; }
.meal-tile.done .tile-body { font-style: normal; }
.banners { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.quest-card { background: linear-gradient(135deg, var(--accent-soft) 0%, var(--surface) 100%); }
.banner-row { display: flex; align-items: center; gap: 14px; margin-bottom: 14px; }
.quest-emoji { width: 60px; height: 60px; border-radius: 14px; background: var(--surface); border: 1.5px solid var(--accent); display: flex; align-items: center; justify-content: center; font-size: 30px; }
.grow { flex: 1; } h3 { margin: 6px 0 0; font-size: 15px; } p { margin: 4px 0 0; font-size: 11px; color: var(--ink-2); }
</style>
