<template>
  <div class="nyam-character" :style="sizeStyle" :class="`appearance-${appearanceType}`">
    <img
      class="nyam-image"
      :src="imageSrc"
      :alt="`${normalizedStage}-${normalizedMood}`"
      @error="imageLoadFailed = true"
    >
    <img
      v-if="hatImageUrl && !hatImageFailed"
      class="hat-image"
      :src="hatImageUrl"
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

const nyamnyamImages = import.meta.glob('../../assets/nyamnyam/*.png', {
  eager: true,
  query: '?url',
  import: 'default'
}) as Record<string, string>

const validStages: Stage[] = ['egg', 'baby', 'child', 'adult']
const validMoods: NyamnyamMood[] = ['normal', 'hungry', 'chubby', 'muscle']
const imageLoadFailed = ref(false)
const hatImageFailed = ref(false)

const normalizedStage = computed<Stage>(() => validStages.includes(props.stage) ? props.stage : 'baby')
const normalizedMood = computed<NyamnyamMood>(() => validMoods.includes(props.mood) ? props.mood : 'normal')
const imageKey = computed(() => `${normalizedStage.value}-${normalizedMood.value}`)
const fallbackKey = 'baby-normal'
const imageSrc = computed(() => {
  const key = imageLoadFailed.value ? fallbackKey : imageKey.value
  return imageForKey(key) || imageForKey(fallbackKey) || ''
})
const sizeStyle = computed(() => ({
  width: `${props.size}px`,
  height: `${props.size}px`
}))

watch(imageKey, () => {
  imageLoadFailed.value = false
})

watch(() => props.hatImageUrl, () => {
  hatImageFailed.value = false
})

function imageForKey(key: string) {
  const entry = Object.entries(nyamnyamImages).find(([path]) => path.endsWith(`${key}.png`))
  return entry?.[1]
}
</script>

<script lang="ts">
export default { name: 'NyamnyamCharacter' }
</script>

<style scoped>
.nyam-character {
  position: relative;
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
  mix-blend-mode: multiply;
}

.hat-image {
  position: absolute;
  top: -8%;
  left: 25%;
  width: 50%;
  height: 32%;
  object-fit: contain;
  pointer-events: none;
}

.hat {
  position: absolute;
  top: -12%;
  left: 24%;
  width: 52%;
  height: 36%;
  pointer-events: none;
}
</style>
