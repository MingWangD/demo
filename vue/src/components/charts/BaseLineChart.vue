<template><div ref="el" style="height:300px;width:100%"></div></template>
<script setup>
import * as echarts from 'echarts'
import {onMounted, onBeforeUnmount, ref, watch} from 'vue'
const props = defineProps({ title: String, xData: Array, yData: Array })
const el = ref(); let chart
const render = () => {
  if (!el.value) return
  if (!chart) chart = echarts.init(el.value)
  chart.setOption({
    title: { text: props.title || '' },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: props.xData || [] },
    yAxis: { type: 'value' },
    series: [{ type: 'line', data: props.yData || [], smooth: true, areaStyle: {} }]
  })
}
onMounted(render)
watch(() => [props.xData, props.yData], render)
onBeforeUnmount(() => chart && chart.dispose())
</script>
