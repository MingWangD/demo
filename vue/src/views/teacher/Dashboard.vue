<template>
  <div>
    <div class="card">高风险 {{data.highRiskCount}} / 中风险 {{data.mediumRiskCount}} / 低风险 {{data.lowRiskCount}}</div>
    <div style="display:flex; gap:10px; margin-top:10px">
      <div class="card" style="flex:1"><base-pie-chart title="风险分布" :data="riskPie"/></div>
      <div class="card" style="flex:1"><base-pie-chart title="GPA颜色分布" :data="gpaPie"/></div>
    </div>
    <div class="card" style="margin-top:10px"><base-line-chart title="全班同学的整体风险趋势（百分比，越高越危险）" :x-data="trendX" :y-data="trendY" unit="%"/></div>
    <div class="card" style="margin-top:10px">
      <h4>高风险学生列表</h4>
      <el-table :data="highRiskPaged">
        <el-table-column prop="studentId" label="学生ID"/>
        <el-table-column prop="riskProbability" label="风险概率"/>
        <el-table-column prop="mainReason" label="主要原因"/>
      </el-table>
      <div style="display:flex;justify-content:flex-end;margin-top:10px">
        <el-pagination
            background
            layout="total, prev, pager, next"
            :current-page="highRiskPage.pageNum"
            :page-size="highRiskPage.pageSize"
            :total="(data.highRiskStudents || []).length"
            @current-change="v=>highRiskPage.pageNum=v"
        />
      </div>
    </div>
  </div>
</template>
<script setup>
import {onMounted,reactive,computed} from 'vue'
import request from '@/utils/request'
import BasePieChart from '@/components/charts/BasePieChart.vue'
import BaseLineChart from '@/components/charts/BaseLineChart.vue'
const data=reactive({gpaColor:{},riskTrend:[]})
const highRiskPage=reactive({pageNum:1,pageSize:5})
const riskPie=computed(()=>[{name:'HIGH',value:data.highRiskCount||0},{name:'MEDIUM',value:data.mediumRiskCount||0},{name:'LOW',value:data.lowRiskCount||0}])
const gpaPie=computed(()=>Object.keys(data.gpaColor||{}).map(k=>({name:k,value:data.gpaColor[k]})))
const trendX=computed(()=> (data.riskTrend||[]).map(i=> i.label || '-'))
const trendY=computed(()=> (data.riskTrend||[]).map(i=> Number(((i.riskProbability || 0) * 100).toFixed(2))))
const highRiskPaged=computed(()=>{
  const all=data.highRiskStudents||[]
  const from=(highRiskPage.pageNum-1)*highRiskPage.pageSize
  return all.slice(from, from+highRiskPage.pageSize)
})
onMounted(async()=>{const r=await request.get('/api/dashboard/teacher'); Object.assign(data,r.data||{})})
</script>
