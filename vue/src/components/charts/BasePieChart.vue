<template><div ref="el" style="height:300px;width:100%"></div></template>
<script setup>
import * as echarts from 'echarts'
import {onMounted, onBeforeUnmount, ref, watch} from 'vue'
const props = defineProps({ title: String, data: Array })
const el = ref(); let chart
const render = () => {
  if (!el.value) return
  if (!chart) chart = echarts.init(el.value)
  chart.setOption({
    title: { text: props.title || '' },
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [{ type: 'pie', radius: ['40%','70%'], data: props.data || [] }]
  })
}
onMounted(render)
watch(() => props.data, render)
onBeforeUnmount(() => chart && chart.dispose())
</script>
