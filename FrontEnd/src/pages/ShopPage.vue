<template>
  <section v-if="isLoading" class="loading-state">
    <AppCard>상점 정보를 불러오는 중...</AppCard>
  </section>

  <section v-else class="shop-page">
    <div v-if="errorMessage" class="api-message error">{{ errorMessage }}</div>
    <div v-if="successMessage" class="api-message success">{{ successMessage }}</div>

    <div class="shop-layout">
      <aside class="shop-left">
        <AppCard :padding="14" class="coin-card">
          <div class="coin-row"><span>냠냠코인</span><strong>🪙 {{ coins.toLocaleString() }}</strong></div>
        </AppCard>
        <AppCard :padding="32" class="preview" :class="{ 'has-background': Boolean(displayBackgroundImage) }" :style="previewBackgroundStyle">
          <div class="mono-label">{{ hovered && isOwned(hovered) ? `미리보기 · ${hovered.name}` : `장착 중 · ${equipLabel}` }}</div>
          <CharacterAvatar
            class="preview-avatar"
            :stage="characterStage"
            :size="165"
            :mood="characterMood"
            :appearance-type="previewAppearanceType"
            :head-item="displayHeadItem"
            :hand-item="displayHandItem"
            :background-item="displayBackgroundItem"
          />
          <p v-if="equippedItems.length"><strong>{{ equipLabel }}</strong></p>
          <p v-else class="empty">장착된 아이템이 없어요</p>
        </AppCard>
      </aside>

      <main class="shop-main">
        <div class="shop-header">
          <div>
            <div class="shop-title">{{ shopTitle }}</div>
            <p>{{ shopDescription }}</p>
          </div>
          <div class="shop-tabs">
            <button type="button" :class="{ active: activeShopTab === 'items' }" @click="activeShopTab = 'items'">아이템 상점</button>
            <button type="button" :class="{ active: activeShopTab === 'characters' }" @click="activeShopTab = 'characters'">캐릭터 상점</button>
            <button type="button" :class="{ active: activeShopTab === 'backgrounds' }" @click="activeShopTab = 'backgrounds'">배경 상점</button>
          </div>
        </div>
        <div v-if="visibleShopItems.length === 0" class="empty-state">판매 중인 아이템이 없습니다.</div>
        <div v-else class="item-list">
          <div
            v-for="item in visibleShopItems"
            :key="item.itemId"
            class="item-row"
            :class="{ equipped: isEquipped(item), hover: hovered?.itemId === item.itemId, 'slot-hand': item.slotType === 'HAND', 'slot-head': item.slotType === 'HEAD', 'slot-character': isCharacterItem(item), 'slot-background': isBackgroundItem(item) }"
            @mouseenter="hovered = item"
            @mouseleave="hovered = null"
          >
            <div class="item-preview" :class="{ head: item.slotType === 'HEAD' }">
              <NyamnyamCharacter v-if="isCharacterItem(item)" stage="egg" mood="normal" :size="82" :appearance-type="appearanceFromItem(item)" />
              <img v-else-if="isBackgroundItem(item) && resolveBackgroundAsset(item)" class="background-asset-image" :src="resolveBackgroundAsset(item) || ''" :alt="item.name">
              <img v-else-if="equipmentImageUrl(item) && !failedItemImages.has(item.itemId)" :class="['item-asset-image', `item-asset-${equipmentIconId(item) || 'unknown'}`]" :src="equipmentImageUrl(item) || ''" :alt="item.name" @error="markItemImageFailed(item.itemId)">
              <WeaponIcon v-else-if="equipmentIconId(item)" :id="equipmentIconId(item) || undefined" />
              <span v-else class="no-image">이미지 없음</span>
            </div>
            <div class="grow">
              <div class="item-head"><strong>{{ item.name }}</strong><AppPill size="sm">{{ itemTypeLabel(item.itemType) }}</AppPill><AppPill v-if="shouldShowSlotPill(item)" size="sm">{{ slotLabel(item.slotType) }}</AppPill></div>
              <p>{{ item.description || '아이템 설명이 없습니다.' }}</p>
              <AppPill v-if="item.defaultItem" size="sm">무료 기본 아이템</AppPill>
              <b v-else>🪙 {{ item.price.toLocaleString() }} 코인</b>
            </div>
            <div class="actions">
              <template v-if="isEquipped(item)">
                <AppPill tone="accent" size="sm">장착 중 ✓</AppPill>
                <AppButton variant="secondary" :disabled="isPending" @click="unequip(item.slotType)">{{ pendingActionId === `unequip-${item.slotType}` ? '해제 중...' : '해제' }}</AppButton>
              </template>
              <AppButton v-else-if="isOwned(item)" variant="secondary" :disabled="isPending" @click="equip(item)">{{ pendingActionId === `equip-${item.itemId}` ? '선택 중...' : equipButtonLabel(item) }}</AppButton>
              <AppButton v-else :disabled="isPending || !item.purchasable" @click="purchase(item)">{{ purchaseButtonLabel(item) }}</AppButton>
            </div>
          </div>
        </div>
      </main>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import AppButton from '../components/common/AppButton.vue'
import AppCard from '../components/common/AppCard.vue'
import AppPill from '../components/common/AppPill.vue'
import CharacterAvatar from '../components/nyamnyam/CharacterAvatar.vue'
import NyamnyamCharacter from '../components/nyamnyam/NyamnyamCharacter.vue'
import WeaponIcon from '../components/nyamnyam/WeaponIcon.vue'
import { ApiError } from '../services/api/client'
import { characterEquipmentApi, equipmentIconId, equipmentImageUrl } from '../services/api/characterEquipmentApi'
import { shopApi } from '../services/api/shopApi'
import { characterApi, moodFromBackend, stageFromBackend, type CharacterResponse } from '../services/characterApi'
import { resolveBackgroundAsset } from '../utils/backgroundAssets'
import type { NyamnyamMood, Stage } from '../services/mock/nyamnyamMock'
import type { CharacterEquipment } from '../types/characterEquipment'
import type { ShopItem } from '../types/shop'

defineProps<{ stage: Stage; equippedWeapon?: string; equippedHat?: string | null }>()

const coins = ref(0)
const shopItems = ref<ShopItem[]>([])
const activeShopTab = ref<'items' | 'characters' | 'backgrounds'>('items')
const equipments = ref<CharacterEquipment[]>([])
const character = ref<CharacterResponse | null>(null)
const hovered = ref<ShopItem | null>(null)
const isLoading = ref(true)
const errorMessage = ref('')
const successMessage = ref('')
const pendingActionId = ref<string | null>(null)
const failedItemImages = ref(new Set<number>())

const equippedItems = computed(() => equipments.value.filter((equipment) => equipment.equipped && equipment.itemId !== null))
const nyamnyamCharacterItem = computed<ShopItem>(() => ({
  itemId: -1,
  name: '냠냠이 캐릭터',
  description: '기본으로 함께하는 든든한 냠냠이 모험가',
  itemType: 'CHARACTER',
  slotType: 'CHARACTER',
  price: 0,
  imageUrl: 'NYAMNYAM',
  effectValue: 'NYAMNYAM',
  defaultItem: true,
  purchasable: false,
  owned: true,
  equipped: normalizeAppearance(character.value?.appearanceType) === 'NYAMNYAM',
  userItemId: null
}))
const itemShopItems = computed(() => shopItems.value.filter((item) => !isCharacterItem(item) && !isBackgroundItem(item)))
const backgroundShopItems = computed(() => shopItems.value.filter(isBackgroundItem))
const characterShopItems = computed(() => {
  const skinItems = shopItems.value
    .filter(isCharacterItem)
    .filter((item) => appearanceFromItem(item) !== 'NYAMNYAM')
    .sort((a, b) => characterOrder(appearanceFromItem(a)) - characterOrder(appearanceFromItem(b)))
  return [nyamnyamCharacterItem.value, ...skinItems]
})
const visibleShopItems = computed(() => {
  if (activeShopTab.value === 'characters') return characterShopItems.value
  if (activeShopTab.value === 'backgrounds') return backgroundShopItems.value
  return itemShopItems.value
})
const shopTitle = computed(() => {
  if (activeShopTab.value === 'characters') return '냠냠 캐릭터 상점'
  if (activeShopTab.value === 'backgrounds') return '냠냠 배경 상점'
  return '냠냠 아이템 상점'
})
const shopDescription = computed(() => {
  if (activeShopTab.value === 'characters') return '냠냠이·강아지·펭귄 캐릭터를 선택해 모험 분위기를 바꿔보세요'
  if (activeShopTab.value === 'backgrounds') return '메인 캐릭터 카드의 모험 배경을 구매하고 적용해보세요'
  return '냠냠코인으로 무기·머리 장식을 구매하고 냠냠이에게 입혀보세요'
})
const equipLabel = computed(() => equippedItems.value.map((equipment) => equipment.name).filter(Boolean).join(' + ') || '장착 안 함')
const previewItem = computed(() => hovered.value && (isCharacterItem(hovered.value) || isOwned(hovered.value)) ? hovered.value : null)
const characterStage = computed(() => stageFromBackend(character.value?.stage))
const characterMood = computed<NyamnyamMood>(() => moodFromBackend(character.value?.mood))
const displayHandItem = computed(() => {
  if (previewItem.value?.slotType === 'HAND') return previewItem.value
  return equippedItems.value.find((item) => item.slotType === 'HAND') ?? null
})
const displayHeadItem = computed(() => {
  if (previewItem.value?.slotType === 'HEAD') return previewItem.value
  return equippedItems.value.find((item) => item.slotType === 'HEAD') ?? null
})
const previewAppearanceType = computed(() => previewItem.value && isCharacterItem(previewItem.value) ? appearanceFromItem(previewItem.value) : character.value?.appearanceType || 'NYAMNYAM')
const displayBackgroundItem = computed(() => {
  if (previewItem.value && isBackgroundItem(previewItem.value)) return previewItem.value
  return equippedItems.value.find((item) => item.slotType === 'BACKGROUND') ?? null
})
const displayBackgroundImage = computed(() => resolveBackgroundAsset(displayBackgroundItem.value))
const previewBackgroundStyle = computed(() => displayBackgroundImage.value ? { backgroundImage: `url(${displayBackgroundImage.value})` } : undefined)
const isPending = computed(() => pendingActionId.value !== null)

function markItemImageFailed(itemId: number) {
  failedItemImages.value = new Set([...failedItemImages.value, itemId])
}

function setError(error: unknown) {
  errorMessage.value = error instanceof ApiError ? error.message : '상점 요청을 처리하지 못했습니다. 잠시 후 다시 시도해주세요.'
}

function clearFeedback() {
  errorMessage.value = ''
  successMessage.value = ''
}

async function loadShop() {
  isLoading.value = true
  clearFeedback()
  try {
    const [shop, characterData] = await Promise.all([
      shopApi.getShop(),
      characterApi.getMyCharacter()
    ])
    applyShopResponse(shop)
    character.value = characterData
  } catch (error) {
    setError(error)
  } finally {
    isLoading.value = false
  }
}

async function refreshShop() {
  const [shop, characterData] = await Promise.all([shopApi.getShop(), characterApi.getMyCharacter()])
  applyShopResponse(shop)
  character.value = characterData
}

function applyShopResponse(shop: Awaited<ReturnType<typeof shopApi.getShop>>) {
  coins.value = Number(shop.balance || 0)
  shopItems.value = shop.items ?? []
  equipments.value = shop.equippedItems ?? []
}

async function purchase(item: ShopItem) {
  if (pendingActionId.value || item.owned || !item.purchasable) return
  clearFeedback()
  pendingActionId.value = `purchase-${item.itemId}`
  try {
    await shopApi.purchaseItem(item.itemId)
    await refreshShop()
    successMessage.value = `${item.name} 구매를 완료했습니다.`
  } catch (error) {
    setError(error)
  } finally {
    pendingActionId.value = null
  }
}

async function equip(item: ShopItem) {
  if (pendingActionId.value || !item.owned) return
  if (isDefaultCharacterItem(item)) {
    await unequip('CHARACTER')
    return
  }
  const userItemId = item.userItemId
  if (!userItemId) {
    errorMessage.value = '장착할 보유 아이템 정보를 확인할 수 없습니다.'
    return
  }

  clearFeedback()
  pendingActionId.value = `equip-${item.itemId}`
  try {
    await characterEquipmentApi.equipItem({ userItemId })
    await refreshShop()
    successMessage.value = `${item.name}을(를) 장착했습니다.`
  } catch (error) {
    setError(error)
  } finally {
    pendingActionId.value = null
  }
}

async function unequip(slotType: string | null) {
  if (pendingActionId.value || !slotType) return
  clearFeedback()
  pendingActionId.value = `unequip-${slotType}`
  try {
    await characterEquipmentApi.unequipItem(slotType)
    await refreshShop()
    successMessage.value = slotType === 'CHARACTER' ? '냠냠이 캐릭터로 변경했습니다.' : slotType === 'BACKGROUND' ? '기본 배경으로 변경했습니다.' : '아이템 장착을 해제했습니다.'
  } catch (error) {
    setError(error)
  } finally {
    pendingActionId.value = null
  }
}

function isOwned(item: ShopItem) {
  return item.owned === true
}

function isEquipped(item: ShopItem) {
  if (isCharacterItem(item)) return item.equipped === true || appearanceFromItem(item) === normalizeAppearance(character.value?.appearanceType)
  return item.equipped === true || equipments.value.some((equipment) => equipment.equipped && equipment.itemId === item.itemId)
}

function isCharacterItem(item: Pick<ShopItem, 'itemType' | 'slotType'>) {
  return item.itemType === 'CHARACTER' || item.slotType === 'CHARACTER'
}

function isBackgroundItem(item: Pick<ShopItem, 'itemType' | 'slotType'>) {
  return item.itemType === 'BACKGROUND' || item.slotType === 'BACKGROUND'
}

function isDefaultCharacterItem(item: ShopItem) {
  return isCharacterItem(item) && appearanceFromItem(item) === 'NYAMNYAM'
}

function normalizeAppearance(value: unknown) {
  const key = typeof value === 'string' ? value.trim().toUpperCase() : ''
  if (key === 'PENGUIN' || key.includes('펭귄')) return 'PENGUIN'
  if (key === 'DOG' || key.includes('강아지')) return 'DOG'
  return 'NYAMNYAM'
}

function appearanceFromItem(item: Pick<ShopItem, 'effectValue' | 'imageUrl' | 'name'>) {
  return normalizeAppearance(item.effectValue || item.imageUrl || item.name)
}

function equipButtonLabel(item: ShopItem) {
  if (isBackgroundItem(item)) return '적용하기'
  return isCharacterItem(item) ? '선택하기' : '장착하기'
}

function characterOrder(value: string) {
  return ({ NYAMNYAM: 0, DOG: 1, PENGUIN: 2 } as Record<string, number>)[value] ?? 9
}

function purchaseButtonLabel(item: ShopItem) {
  if (pendingActionId.value === `purchase-${item.itemId}`) return '구매 중...'
  return item.purchasable ? '🪙 구매하기' : '구매 불가'
}

function slotLabel(slotType: string | null) {
  if (slotType === 'CHARACTER') return '캐릭터'
  if (slotType === 'BACKGROUND') return '배경'
  if (slotType === 'HEAD') return '머리'
  if (slotType === 'HAND') return '손'
  return slotType || '장착 슬롯 없음'
}

function itemTypeLabel(itemType: string) {
  if (itemType === 'CHARACTER') return '캐릭터'
  if (itemType === 'BACKGROUND') return '배경'
  if (itemType === 'EQUIPMENT') return '장비'
  if (itemType === 'CONSUMABLE') return '소모품'
  return itemType || '유형 없음'
}

function shouldShowSlotPill(item: Pick<ShopItem, 'itemType' | 'slotType'>) {
  return itemTypeLabel(item.itemType) !== slotLabel(item.slotType)
}

onMounted(() => { void loadShop() })
</script>

<style scoped>
.loading-state { max-width: 560px; margin: 80px auto; text-align: center; }
.api-message { margin-bottom: 14px; padding: 11px 14px; border-radius: 10px; border: 1px solid var(--border); font-size: 13px; }
.api-message.error { color: var(--bad); border-color: var(--bad); background: var(--surface); }
.api-message.success { color: var(--ok); background: var(--surface); }
.empty-state { padding: 28px; border: 1.5px dashed var(--border); border-radius: 14px; background: var(--surface); text-align: center; color: var(--ink-3); font-size: 13px; }
.shop-layout { display: grid; grid-template-columns: minmax(320px, 440px) minmax(0, 1fr); gap: 36px; align-items: start; }
.shop-left, .shop-main { min-width: 0; display: flex; flex-direction: column; gap: 12px; } .shop-main { gap: 16px; }
.coin-card { background: linear-gradient(135deg,#fff8dc 0%,#ffe57a 100%); border-color: var(--yolk-deep); } .coin-row { display: flex; justify-content: space-between; align-items: center; } .coin-row span { font-family: var(--mono); font-size: 11px; color: #7a5c00; letter-spacing: 1px; } .coin-row strong { font-family: var(--mono); font-size: 22px; color: #6b4c00; }
.preview { position: sticky; top: 88px; background: #fff6e8; background-size: cover; background-position: center; background-repeat: no-repeat; text-align: center; border-color: #e3b789; overflow: hidden; } .preview:before { content: ""; position: absolute; inset: 0; pointer-events: none; opacity: 0; background: linear-gradient(180deg, rgba(255,248,236,.16), rgba(255,248,236,.08)); z-index: 0; } .preview.has-background:before { opacity: 1; } .preview > * { position: relative; z-index: 1; } .preview:after { content: "EQUIPMENT ROOM"; position: absolute; bottom: 8px; left: 0; right: 0; color: rgba(190,78,31,.07); font: 900 24px var(--mono); z-index: 1; } .preview-avatar { margin: 20px auto 0; }
.preview p { margin: 16px 0 0; font-size: 12px; color: var(--ink-2); line-height: 1.5; } .preview .empty { color: var(--ink-3); }
.shop-header { display: flex; justify-content: space-between; align-items: flex-end; gap: 16px; flex-wrap: wrap; } .shop-title { font-size: 28px; font-weight: 900; letter-spacing: -0.4px; margin-bottom: 4px; } .shop-main > div > p { margin: 0; color: var(--ink-2); font-size: 13px; } .shop-tabs { display: inline-flex; gap: 8px; padding: 5px; border: 1px solid var(--border); border-radius: 999px; background: rgba(255,255,255,.72); box-shadow: 0 3px 0 rgba(116,75,49,.07); } .shop-tabs button { border: 0; border-radius: 999px; padding: 10px 15px; background: transparent; color: var(--ink-2); font-weight: 900; cursor: pointer; } .shop-tabs button.active { background: linear-gradient(180deg,#B8DB80,var(--accent-dark)); color: #fff; box-shadow: 0 3px 0 var(--accent-dark); }
.item-list { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 20px; width: 100%; align-items: stretch; }
.item-row { position: relative; box-sizing: border-box; overflow: hidden; isolation: isolate; min-width: 0; width: 100%; height: auto; min-height: 220px; margin: 0; z-index: 0; transform: none; display: grid; grid-template-columns: 92px minmax(0,1fr); grid-template-rows: auto 1fr auto; align-items: start; gap: 16px; padding: 20px; padding-top: 24px; border-radius: 18px; border: 1.5px solid var(--border); background: linear-gradient(145deg,#fffdf9 0%,#fff6ec 100%); box-shadow: 0 3px 0 rgba(116,75,49,.07), 0 12px 28px rgba(96,57,31,.08); cursor: default; transition: transform .16s ease, box-shadow .18s ease, border-color .18s ease; }
.item-row:before { content: "COMMON"; position: absolute; top: 12px; right: -16px; width: 76px; padding: 3px 0; transform: rotate(35deg); text-align: center; background: var(--surface-alt); color: var(--ink-3); font: 800 8px var(--mono); pointer-events: none; }
.item-row.slot-head:before { content: "HEAD"; background: var(--purple-soft); color: var(--purple); }
.item-row.slot-hand:before { content: "HAND"; background: var(--accent-soft); color: var(--accent-dark); }
.item-row.slot-character:before { content: "SKIN"; background: var(--purple-soft); color: var(--purple); }
.item-row.slot-background:before { content: "BG"; background: var(--accent-soft); color: var(--accent-dark); }
.item-row.hover, .item-row:hover { z-index: 1; border-color: var(--accent); box-shadow: var(--shadow-lg); transform: translateY(-2px); }
.item-row.equipped { background: linear-gradient(145deg,#fffdf8 0%,#f5ffdf 100%); border-color: var(--accent); box-shadow: 0 0 0 3px rgba(143,207,85,.14), 0 3px 0 rgba(116,75,49,.07), 0 12px 28px rgba(96,57,31,.08); }
.item-preview { grid-row: 1 / 2; width: 92px; height: 116px; display: flex; align-items: center; justify-content: center; background: radial-gradient(circle,#fff,#fff1df); border-radius: 16px; border: 1px solid var(--border); overflow: hidden; }
.item-preview img,
.item-asset-image { width: 100%; height: 100%; object-fit: contain; }
.item-preview :deep(svg) { max-width: 72px; max-height: 100px; width: auto; height: auto; }
.item-row.slot-hand .item-preview :deep(svg) { width: 54px; height: 100px; }
.item-row.slot-head .item-preview :deep(svg) { width: 72px; height: auto; }
.item-row.slot-hand .item-preview .item-asset-image { width: 60px; height: 104px; }
.item-row.slot-hand .item-preview .item-asset-stick { width: 52px; height: 94px; }
.item-row.slot-hand .item-preview .item-asset-sword,
.item-row.slot-hand .item-preview .item-asset-staff { width: 68px; height: 116px; }
.item-row.slot-head .item-preview .item-asset-image { width: 72px; height: 58px; }
.item-row.slot-character .item-preview { background: radial-gradient(circle,#fff,#f6ffe3); }
.item-row.slot-background .item-preview { grid-column: 1 / -1; width: 100%; height: 132px; border-radius: 16px; background: var(--surface-alt); }
.item-row.slot-background .grow,
.item-row.slot-background .actions { grid-column: 1 / -1; }
.background-asset-image { width: 100%; height: 100%; object-fit: cover; display: block; }
.no-image { padding: 4px; font-size: 9px; line-height: 1.3; color: var(--ink-3); text-align: center; }
.grow { min-width: 0; display: flex; flex-direction: column; gap: 8px; }
.item-head { display: flex; align-items: center; flex-wrap: wrap; gap: 6px; margin: 0; padding-right: 34px; }
.item-head strong { min-width: 100%; font-size: 18px; line-height: 1.25; }
.item-row p { margin: 0; font-size: 12px; color: var(--ink-2); line-height: 1.55; overflow-wrap: anywhere; }
.item-row b { display: inline-flex; align-items: center; gap: 4px; margin-top: 2px; font-family: var(--mono); font-size: 13px; color: #7a5c00; }
.actions { position: static; grid-column: 1 / -1; align-self: end; min-width: 0; margin-top: auto; display: flex; flex-direction: row; flex-wrap: wrap; align-items: center; justify-content: flex-end; gap: 10px; padding-top: 10px; }
@media (max-width: 1280px) { .item-list { grid-template-columns: 1fr; } }
@media (max-width: 860px) { .shop-layout { grid-template-columns: 1fr; } .shop-left { display: grid; grid-template-columns: 1fr 1fr; } .preview { position: static; } }
@media (max-width: 620px) { .shop-left { display: flex; } .item-row { align-items: flex-start; flex-wrap: wrap; } .actions { width: 100%; justify-content: flex-end; } }
</style>
