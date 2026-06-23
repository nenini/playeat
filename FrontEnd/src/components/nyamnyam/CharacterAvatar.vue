<template>
  <div class="character-avatar" :style="avatarStyle">
    <div v-if="showBackground && backgroundAsset" class="avatar-background" :style="backgroundStyle" />
    <div class="avatar-orb" :class="{ 'has-background': showBackground && Boolean(backgroundAsset) }" />
    <div class="avatar-character-shell">
      <NyamnyamCharacter
        :stage="normalizedStage"
        :mood="normalizedMood"
        :size="characterSize"
        :appearance-type="appearanceType"
      />
    </div>

    <img
      v-if="showItems && headAsset && !headFailed"
      :class="['avatar-item', 'avatar-head', `item-asset-${headKey || 'unknown'}`]"
      :src="headAsset"
      :alt="headItem?.name || '머리 장비'"
      @error="headFailed = true"
    >
    <WeaponIcon
      v-else-if="showItems && headKey"
      :id="headKey"
      class="avatar-item avatar-head avatar-icon"
    />

    <img
      v-if="showItems && handAsset && !handFailed"
      :class="['avatar-item', 'avatar-hand', `item-asset-${handKey || 'unknown'}`]"
      :src="handAsset"
      :alt="handItem?.name || '손 장비'"
      @error="handFailed = true"
    >
    <WeaponIcon
      v-else-if="showItems && handKey"
      :id="handKey"
      class="avatar-item avatar-hand avatar-icon"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import NyamnyamCharacter from './NyamnyamCharacter.vue'
import WeaponIcon from './WeaponIcon.vue'
import { normalizeItemKey, resolveItemAsset } from '../../utils/itemAssets'
import { resolveBackgroundAsset } from '../../utils/backgroundAssets'
import type { CharacterEquipment } from '../../types/characterEquipment'
import type { NyamnyamMood, Stage } from '../../services/mock/nyamnyamMock'

type EquipmentLike = {
  slotType?: string | null
  equipped?: boolean
  itemId?: number | null
  name?: string | null
  imageUrl?: string | null
  effectValue?: string | null
}

const props = withDefaults(defineProps<{
  appearanceType?: string
  stage?: Stage | string
  mood?: NyamnyamMood | string
  size?: number | string
  equipments?: EquipmentLike[]
  headItem?: EquipmentLike | null
  handItem?: EquipmentLike | null
  backgroundItem?: EquipmentLike | null
  showBackground?: boolean
  showItems?: boolean
}>(), {
  appearanceType: 'DEFAULT',
  stage: 'baby',
  mood: 'normal',
  size: 160,
  equipments: () => [],
  headItem: null,
  handItem: null,
  backgroundItem: null,
  showBackground: true,
  showItems: true
})

const headFailed = ref(false)
const handFailed = ref(false)

const resolvedSize = computed(() => typeof props.size === 'number' ? `${props.size}px` : props.size)
const numericSize = computed(() => typeof props.size === 'number' ? props.size : Number.parseFloat(props.size) || 160)
const characterSize = computed(() => Math.round(numericSize.value * 0.48))
const avatarStyle = computed(() => ({ '--avatar-size': resolvedSize.value }))
const normalizedStage = computed<Stage>(() => normalizeOption(props.stage, ['egg', 'baby', 'child', 'adult'], 'baby'))
const normalizedMood = computed<NyamnyamMood>(() => normalizeOption(props.mood, ['normal', 'hungry', 'chubby', 'muscle'], 'normal'))

const equippedItems = computed(() => props.equipments.filter((item) => item.equipped && item.itemId !== null))
const headItem = computed(() => props.headItem ?? equippedItems.value.find((item) => item.slotType === 'HEAD') ?? null)
const handItem = computed(() => props.handItem ?? equippedItems.value.find((item) => item.slotType === 'HAND') ?? null)
const backgroundItem = computed(() => props.backgroundItem ?? equippedItems.value.find((item) => item.slotType === 'BACKGROUND') ?? null)

const headAsset = computed(() => resolveItemAsset(headItem.value))
const handAsset = computed(() => resolveItemAsset(handItem.value))
const backgroundAsset = computed(() => resolveBackgroundAsset(backgroundItem.value))
const backgroundStyle = computed(() => backgroundAsset.value ? { backgroundImage: `url(${backgroundAsset.value})` } : undefined)
const headKey = computed(() => normalizeItemKey(headItem.value?.imageUrl) || normalizeItemKey(headItem.value?.effectValue) || normalizeItemKey(headItem.value?.name))
const handKey = computed(() => normalizeItemKey(handItem.value?.imageUrl) || normalizeItemKey(handItem.value?.effectValue) || normalizeItemKey(handItem.value?.name))

watch(headAsset, () => { headFailed.value = false })
watch(handAsset, () => { handFailed.value = false })

function normalizeOption<T extends string>(value: unknown, allowed: readonly T[], defaultValue: T): T {
  const normalized = typeof value === 'string' ? value.toLowerCase() : ''
  return allowed.includes(normalized as T) ? normalized as T : defaultValue
}
</script>

<script lang="ts">
export default { name: 'CharacterAvatar' }
</script>

<style scoped>
.character-avatar {
  --character-width: 72%;
  --character-top: 50%;
  --head-width: 30%;
  --head-left: 50%;
  --head-top: 24%;
  --hand-width: 18%;
  --hand-height: 48%;
  --hand-left: 72%;
  --hand-top: 53%;
  position: relative;
  width: var(--avatar-size);
  aspect-ratio: 1 / 1;
  overflow: visible;
  flex: 0 0 auto;
}

.avatar-background,
.avatar-orb,
.avatar-character-shell,
.avatar-item {
  position: absolute;
  pointer-events: none;
}

.avatar-background {
  inset: 0;
  border-radius: 50%;
  background-size: cover;
  background-position: center;
  z-index: 0;
}

.avatar-background:after {
  content: "";
  position: absolute;
  inset: 0;
  border-radius: inherit;
  background: rgba(255, 248, 236, .12);
}

.avatar-orb {
  inset: 8%;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255,255,255,.78), rgba(255,242,216,.55) 56%, rgba(255,207,169,.64) 100%);
  border: 3px solid rgba(255,255,255,.9);
  box-shadow: 0 0 0 6px rgba(240,120,60,.12), inset 0 -8px 20px rgba(232,138,77,.08);
  z-index: 1;
}

.avatar-orb.has-background {
  background: radial-gradient(circle, rgba(255,255,255,.74), rgba(255,255,255,.26) 58%, transparent 74%);
  border-color: rgba(255,255,255,.72);
  box-shadow: 0 0 32px rgba(255,255,255,.52), inset 0 -8px 22px rgba(255,255,255,.18);
}

.avatar-character-shell {
  left: 50%;
  top: var(--character-top);
  width: var(--character-width);
  transform: translate(-50%, -50%);
  display: grid;
  place-items: center;
  z-index: 3;
}

.avatar-head {
  left: var(--head-left);
  top: var(--head-top);
  width: var(--head-width);
  height: auto;
  max-height: 32%;
  transform: translate(-50%, -50%);
  object-fit: contain;
  z-index: 5;
}

.avatar-hand {
  left: var(--hand-left);
  top: var(--hand-top);
  width: var(--hand-width);
  height: var(--hand-height);
  transform: translate(-50%, -50%) rotate(2deg);
  object-fit: contain;
  z-index: 4;
}

.avatar-hand.item-asset-stick {
  width: calc(var(--hand-width) * .86);
  height: calc(var(--hand-height) * .9);
}

.avatar-hand.item-asset-sword,
.avatar-hand.item-asset-staff {
  width: calc(var(--hand-width) * 1.18);
  height: calc(var(--hand-height) * 1.08);
}

.avatar-icon {
  display: block;
}
</style>
