<template>
  <div>
    <div class="card">高风险 {{data.highRiskCount}} / 中风险 {{data.mediumRiskCount}} / 低风险 {{data.lowRiskCount}}</div>
    <div style="display:flex; gap:10px; margin-top:10px">
      <div class="card" style="flex:1"><base-pie-chart title="风险分布" :data="riskPie"/></div>
      <div class="card" style="flex:1"><base-pie-chart title="GPA颜色分布" :data="gpaPie"/></div>
    </div>
    <div class="card" style="margin-top:10px"><base-line-chart title="风险趋势" :x-data="trendX" :y-data="trendY"/></div>
  </div>
</template>
<script setup>
import {onMounted,reactive,computed} from 'vue'
import request from '@/utils/request'
import BasePieChart from '@/components/charts/BasePieChart.vue'
import BaseLineChart from '@/components/charts/BaseLineChart.vue'
const data=reactive({gpaColor:{},riskTrend:[]})
const riskPie=computed(()=>[{name:'HIGH',value:data.highRiskCount||0},{name:'MEDIUM',value:data.mediumRiskCount||0},{name:'LOW',value:data.lowRiskCount||0}])
const gpaPie=computed(()=>Object.keys(data.gpaColor||{}).map(k=>({name:k,value:data.gpaColor[k]})))
const trendX=computed(()=> (data.riskTrend||[]).map((_,i)=>`T${i+1}`).reverse())
const trendY=computed(()=> (data.riskTrend||[]).map(i=>i.riskProbability).reverse())
onMounted(async()=>{const r=await request.get('/api/dashboard/teacher'); Object.assign(data,r.data||{})})
</script>
