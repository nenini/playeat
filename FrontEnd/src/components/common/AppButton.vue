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
  border-radius: 10px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  transition: transform .08s ease, background .15s, box-shadow .15s;
  white-space: nowrap;
}
.app-button:active:not(:disabled) { transform: translateY(1px); }
.app-button:disabled { opacity: .5; cursor: not-allowed; }
.full { width: 100%; }
.sm { padding: 6px 12px; font-size: 12px; }
.md { padding: 9px 16px; font-size: 13px; }
.lg { padding: 12px 22px; font-size: 15px; }
.primary { background: var(--accent); color: #fff; box-shadow: 0 1px 0 var(--accent-dark); }
.secondary { background: var(--surface); color: var(--ink); border: 1px solid var(--border-strong); }
.ghost { background: transparent; color: var(--ink-2); }
.danger { background: var(--surface); color: var(--bad); border: 1px solid var(--bad); }
</style>
