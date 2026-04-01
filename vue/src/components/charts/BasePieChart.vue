<template><div ref="el" style="height:300px;width:100%"></div></template>
<script setup>
import {onMounted, ref, watch} from 'vue'
const props = defineProps({ title: String, data: Array })
const el = ref(); let chart
const render = () => {
  if (!el.value || !window.echarts) return
  if (!chart) chart = window.echarts.init(el.value)
  chart.setOption({
    title: { text: props.title || '' },
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [{ type: 'pie', radius: ['40%','70%'], data: props.data || [] }]
  })
}
onMounted(render)
watch(() => props.data, render)
</script>
