<template>
  <button class="app-button" :class="[variant, size, { full }]" :disabled="disabled" :type="type">
    <slot />
  </button>
</template>

<script setup lang="ts">
withDefaults(defineProps<{
  variant?: 'primary' | 'secondary' | 'ghost' | 'danger'
  size?: 'sm' | 'md' | 'lg'
  full?: boolean
  disabled?: boolean
  type?: 'button' | 'submit' | 'reset'
}>(), {
  variant: 'primary',
  size: 'md',
  full: false,
  disabled: false,
  type: 'button'
})
</script>

<script lang="ts">
export default { name: 'AppButton' }
</script>

<style scoped>
.app-button {
  border: 0;
  cursor: pointer;
  font-weight: 700;
  border-radius: 12px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  transition: transform .08s ease, background .15s, box-shadow .15s;
  white-space: nowrap;
}
.app-button:hover:not(:disabled) { transform: translateY(-2px) scale(1.02); }
.app-button:active:not(:disabled) { transform: translateY(2px) scale(.98); }
.app-button:disabled { opacity: .5; cursor: not-allowed; }
.full { width: 100%; }
.sm { padding: 6px 12px; font-size: 12px; }
.md { padding: 9px 16px; font-size: 13px; }
.lg { padding: 12px 22px; font-size: 15px; }
.primary { background: linear-gradient(180deg, #B8DB80, var(--accent-dark)); color: #fff; box-shadow: 0 3px 0 var(--accent-dark), 0 8px 16px rgba(143,207,85,.22); }
.secondary { background: linear-gradient(180deg, #fff, #fff7ed); color: var(--ink); border: 1px solid var(--border-strong); box-shadow: 0 2px 0 rgba(116,75,49,.12); }
.ghost { background: transparent; color: var(--ink-2); }
.danger { background: linear-gradient(180deg, #fff, var(--bad-soft)); color: var(--bad); border: 1px solid var(--bad); }
</style>
