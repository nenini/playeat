<template>
  <img v-if="assetSrc" class="weapon weapon-image" :src="assetSrc" :alt="id || 'item'" draggable="false">
  <svg v-else-if="id === 'crown'" viewBox="0 0 60 36" width="60" height="36" class="weapon">
    <path d="M3 24 L 15 5 L 27 24 Z" fill="#f5c83a" stroke="#7a4a08" stroke-width="1.6" stroke-linejoin="round" />
    <path d="M18 24 L 30 1 L 42 24 Z" fill="#f5c83a" stroke="#7a4a08" stroke-width="1.6" stroke-linejoin="round" />
    <path d="M33 24 L 45 5 L 57 24 Z" fill="#f5c83a" stroke="#7a4a08" stroke-width="1.6" stroke-linejoin="round" />
    <rect x="2" y="22" width="56" height="11" rx="2" fill="#f5c83a" stroke="#7a4a08" stroke-width="1.6" />
    <circle cx="15" cy="7" r="2.6" fill="#e84a5a" stroke="#7a4a08" stroke-width="1" />
    <circle cx="30" cy="3" r="2.8" fill="#3a92e0" stroke="#7a4a08" stroke-width="1" />
    <circle cx="45" cy="7" r="2.6" fill="#e84a5a" stroke="#7a4a08" stroke-width="1" />
  </svg>
  <svg v-else-if="id === 'sword'" viewBox="0 0 60 200" width="60" height="200" class="weapon">
    <polygon points="30,8 35,100 30,115 25,100" fill="#d8e4ee" stroke="#8a9aaa" stroke-width="1.8" />
    <rect x="15" y="100" width="30" height="8" rx="3" fill="#d4a840" stroke="#8a6820" stroke-width="1.6" />
    <rect x="25" y="108" width="10" height="38" rx="4" fill="#7a3a10" stroke="#4a2008" stroke-width="1.6" />
    <circle cx="30" cy="150" r="7" fill="#d4a840" stroke="#8a6820" stroke-width="1.6" />
  </svg>
  <svg v-else-if="id === 'staff'" viewBox="0 0 60 200" width="60" height="200" class="weapon">
    <circle cx="30" cy="20" r="18" fill="rgba(160,100,240,0.15)" />
    <circle cx="30" cy="20" r="13" fill="#c090f0" stroke="#8040d0" stroke-width="2" />
    <rect x="26" y="30" width="8" height="148" rx="4" fill="#4a3020" stroke="#2a1808" stroke-width="1.8" />
    <rect x="24" y="55" width="12" height="5" rx="2" fill="#8040d0" />
    <rect x="24" y="90" width="12" height="5" rx="2" fill="#8040d0" />
    <polygon points="30,180 26,170 34,170" fill="#8040d0" stroke="#5020a0" stroke-width="1.5" />
  </svg>
  <svg v-else viewBox="0 0 60 200" width="60" height="200" class="weapon">
    <rect x="26" y="12" width="8" height="152" rx="4" fill="#a0785a" stroke="#6b4c30" stroke-width="2" />
    <ellipse cx="30" cy="10" rx="6" ry="4" fill="#7a5c3a" stroke="#6b4c30" stroke-width="1.5" />
    <line x1="26" y1="50" x2="20" y2="38" stroke="#6b4c30" stroke-width="1.5" stroke-linecap="round" />
    <line x1="34" y1="80" x2="42" y2="68" stroke="#6b4c30" stroke-width="1.5" stroke-linecap="round" />
  </svg>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { resolveItemAssetByKey } from '../../utils/itemAssets'

const props = withDefaults(defineProps<{ id?: string | null }>(), { id: 'stick' })

const assetSrc = computed(() => resolveItemAssetByKey(props.id))
</script>

<script lang="ts">
export default { name: 'WeaponIcon' }
</script>

<style scoped>
.weapon { overflow: visible; display: block; }
.weapon-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
}
</style>
