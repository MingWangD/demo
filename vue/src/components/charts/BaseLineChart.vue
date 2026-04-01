<template>
  <div>
    <div style="font-weight:bold; margin-bottom:8px">{{ title }}</div>
    <div style="display:flex; align-items:flex-end; gap:8px; height:180px; border-bottom:1px solid #ddd; padding:8px 0">
      <div v-for="(v,i) in yData" :key="i" style="flex:1; text-align:center">
        <div :style="{height: `${Math.max(6, Number(v||0)*120)}px`, background:'#409eff', borderRadius:'4px 4px 0 0'}"></div>
        <div style="font-size:12px; margin-top:4px">{{ xData?.[i] }}</div>
      </div>
    </div>
  </div>
</template>
<script setup>
defineProps({ title: String, xData: Array, yData: Array })
</script>
