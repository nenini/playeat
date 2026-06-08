<template>
  <div class="rank" :class="{ mine: row[4] }">
    <span>{{ row[0] }}</span>
    <strong>{{ row[1] }}<small v-if="row[2]"> · {{ row[2] }}</small></strong>
    <b>{{ Number(row[3]).toLocaleString() }}<i>pt</i></b>
  </div>
</template>

<script setup lang="ts">
defineProps<{ row: Array<string | number | boolean> }>()
</script>
<style scoped>.rank{display:flex;align-items:center;gap:10px;padding:8px 10px;border-radius:8px;border:1.5px dashed var(--border);margin-top:6px}.rank.mine{border-style:solid;border-color:var(--accent);background:var(--accent-soft)}span{width:24px;height:24px;border-radius:12px;background:var(--surface-alt);display:flex;align-items:center;justify-content:center;font-family:var(--mono);font-size:11px;font-weight:800}.mine span{background:var(--accent);color:#fff}strong{flex:1;font-size:13px}small{color:var(--accent);font-size:10px}b{font-family:var(--mono);font-size:12px}i{color:var(--ink-3);font-style:normal;margin-left:2px;font-weight:400}</style>
