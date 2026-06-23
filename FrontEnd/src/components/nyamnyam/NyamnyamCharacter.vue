<template>
  <div class="nyam-character" :style="sizeStyle" :class="[`appearance-${normalizedAppearance}`, `stage-${normalizedStage}`]">
    <img
      class="nyam-image"
      :src="imageSrc"
      :alt="`${normalizedStage}-${normalizedMood}`"
      @error="imageLoadFailed = true"
    >
    <img
      v-if="resolvedHatImageUrl && !hatImageFailed"
      class="hat-image"
      :src="resolvedHatImageUrl"
      alt=""
      @error="hatImageFailed = true"
    >
    <WeaponIcon v-else-if="hatId === 'crown'" id="crown" class="hat" />
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import WeaponIcon from './WeaponIcon.vue'
import type { NyamnyamMood, Stage } from '../../services/mock/nyamnyamMock'
import { resolveItemAssetByKey } from '../../utils/itemAssets'

const props = withDefaults(defineProps<{
  stage?: Stage
  mood?: NyamnyamMood
  size?: number
  appearanceType?: string
  hatId?: string | null
  hatImageUrl?: string | null
}>(), {
  stage: 'baby',
  mood: 'normal',
  size: 120,
  appearanceType: 'DEFAULT',
  hatId: null,
  hatImageUrl: null
})

const imageModules = {
  NYAMNYAM: import.meta.glob('../../assets/nyamnyam/*.png', { eager: true, query: '?url', import: 'default' }) as Record<string, string>,
  PENGUIN: import.meta.glob('../../assets/penguin/*.png', { eager: true, query: '?url', import: 'default' }) as Record<string, string>,
  DOG: import.meta.glob('../../assets/dog/*.png', { eager: true, query: '?url', import: 'default' }) as Record<string, string>
}

type AppearanceType = keyof typeof imageModules

const validStages: Stage[] = ['egg', 'baby', 'child', 'adult']
const validMoods: NyamnyamMood[] = ['normal', 'hungry', 'chubby', 'muscle']
const imageLoadFailed = ref(false)
const hatImageFailed = ref(false)

const normalizedStage = computed<Stage>(() => normalizeOption(props.stage, validStages, 'baby'))
const normalizedMood = computed<NyamnyamMood>(() => normalizeOption(props.mood, validMoods, 'normal'))
const normalizedAppearance = computed<AppearanceType>(() => normalizeAppearance(props.appearanceType))
const imageKey = computed(() => `${normalizedStage.value}-${normalizedMood.value}`)
const fallbackKey = 'baby-normal'
const imageSrc = computed(() => {
  const key = imageLoadFailed.value ? fallbackKey : imageKey.value
  return imageForKey(key, normalizedAppearance.value) || imageForKey('egg-normal', normalizedAppearance.value) || imageForKey(fallbackKey, normalizedAppearance.value) || imageForKey(fallbackKey, 'NYAMNYAM') || ''
})
const sizeStyle = computed(() => ({
  width: `${props.size}px`,
  height: `${props.size}px`
}))
const resolvedHatImageUrl = computed(() => resolveItemAssetByKey(props.hatImageUrl) || props.hatImageUrl || null)

watch([imageKey, normalizedAppearance], () => {
  imageLoadFailed.value = false
})

watch(resolvedHatImageUrl, () => {
  hatImageFailed.value = false
})

function normalizeOption<T extends string>(value: unknown, allowed: readonly T[], fallback: T): T {
  const normalized = typeof value === 'string' ? value.toLowerCase() : ''
  return allowed.includes(normalized as T) ? normalized as T : fallback
}

function normalizeAppearance(value: unknown): AppearanceType {
  const key = typeof value === 'string' ? value.trim().toUpperCase() : ''
  if (key === 'PENGUIN' || key === 'DOG') return key
  return 'NYAMNYAM'
}

function imageForKey(key: string, appearance: AppearanceType) {
  const modules = imageModules[appearance] || imageModules.NYAMNYAM
  const underscoreKey = key.replace(/-/g, '_')
  const entry = Object.entries(modules).find(([path]) => path.endsWith(`${key}.png`) || path.endsWith(`${underscoreKey}.png`))
  return entry?.[1]
}
</script>

<script lang="ts">
export default { name: 'NyamnyamCharacter' }
</script>

<style scoped>
.nyam-character {
  position: relative;
  z-index: 2;
  display: inline-grid;
  place-items: center;
  overflow: visible;
  flex: 0 0 auto;
}

.nyam-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
  display: block;
}

.appearance-PENGUIN .nyam-image {
  transform: scale(.95);
  transform-origin: center center;
}

.hat-image {
  position: absolute;
  z-index: 3;
  top: -8%;
  left: 25%;
  width: 50%;
  height: 32%;
  object-fit: contain;
  pointer-events: none;
}

.hat {
  position: absolute;
  z-index: 3;
  top: -12%;
  left: 24%;
  width: 52%;
  height: 36%;
  pointer-events: none;
}
</style>
