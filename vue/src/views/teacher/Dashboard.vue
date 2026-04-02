<template>
  <div>
    <div class="card">高风险 {{data.highRiskCount}} / 中风险 {{data.mediumRiskCount}} / 低风险 {{data.lowRiskCount}}</div>
    <div style="display:flex; gap:10px; margin-top:10px">
      <div class="card" style="flex:1"><base-pie-chart title="风险分布" :data="riskPie"/></div>
      <div class="card" style="flex:1"><base-pie-chart title="GPA颜色分布" :data="gpaPie"/></div>
    </div>
    <div class="card" style="margin-top:10px"><base-line-chart title="风险变化（百分比，越高越危险）" :x-data="trendX" :y-data="trendY" unit="%"/></div>
    <div class="card" style="margin-top:10px">
      <h4>高风险学生列表</h4>
      <el-table :data="data.highRiskStudents || []">
        <el-table-column prop="studentId" label="学生ID"/>
        <el-table-column prop="riskProbability" label="风险概率"/>
        <el-table-column prop="mainReason" label="主要原因"/>
      </el-table>
    </div>
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
const trendX=computed(()=> (data.riskTrend||[]).map((_,i)=>`第${i+1}次预警`).reverse())
const trendY=computed(()=> (data.riskTrend||[]).map(i=> Number(((i.riskProbability || 0) * 100).toFixed(2))).reverse())
onMounted(async()=>{const r=await request.get('/api/dashboard/teacher'); Object.assign(data,r.data||{})})
</script>
