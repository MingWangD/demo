<template><div ref="el" style="height:300px;width:100%"></div></template>
<script setup>
import * as echarts from 'echarts'
import {onMounted, onBeforeUnmount, ref, watch} from 'vue'
const props = defineProps({ title: String, xData: Array, yData: Array, unit: String, xName: String, yName: String })
const el = ref(); let chart
const render = () => {
  if (!el.value) return
  if (!chart) chart = echarts.init(el.value)
  chart.setOption({
    title: { text: props.title || '' },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: props.xData || [], name: props.xName || '', nameLocation: 'middle', nameGap: 28 },
    yAxis: { type: 'value', name: props.yName || '', nameGap: 22, axisLabel: props.unit ? { formatter: `{value}${props.unit}` } : {} },
    series: [{ type: 'line', data: props.yData || [], smooth: true, areaStyle: {} }]
  })
}
onMounted(render)
watch(() => [props.xData, props.yData], render)
onBeforeUnmount(() => chart && chart.dispose())
</script>
