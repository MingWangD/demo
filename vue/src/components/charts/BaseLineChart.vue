<template><div ref="el" style="height:340px;width:100%"></div></template>
<script setup>
import * as echarts from 'echarts'
import {onMounted, onBeforeUnmount, ref, watch} from 'vue'
const props = defineProps({ title: String, xData: Array, yData: Array, unit: String, xName: String, yName: String })
const el = ref(); let chart
const render = () => {
  if (!el.value) return
  if (!chart) chart = echarts.init(el.value)
  const dataCount = (props.xData || []).length
  chart.setOption({
    grid: { left: 78, right: 28, top: 48, bottom: 96, containLabel: true },
    title: { text: props.title || '' },
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: props.xData || [],
      name: props.xName || '',
      nameLocation: 'middle',
      nameGap: 68,
      axisLabel: { interval: 0, rotate: 45, hideOverlap: false }
    },
    yAxis: {
      type: 'value',
      name: props.yName || '',
      nameLocation: 'middle',
      nameRotate: 90,
      nameGap: 58,
      axisLabel: props.unit ? { formatter: `{value}${props.unit}` } : {}
    },
    dataZoom: dataCount > 10 ? [
      { type: 'inside', start: 0, end: 100 },
      { type: 'slider', start: 0, end: 100, height: 18, bottom: 28 }
    ] : [],
    series: [{ type: 'line', data: props.yData || [], smooth: true, areaStyle: {} }]
  })
}
onMounted(render)
watch(() => [props.xData, props.yData], render)
onBeforeUnmount(() => chart && chart.dispose())
</script>
