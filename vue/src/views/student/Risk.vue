<template>
  <div>
    <div class="card">当前风险：{{detail?.risk?.riskProbability}} / {{detail?.risk?.riskLevel}} / {{detail?.risk?.warningColor}}</div>
    <div class="card" style="margin-top:10px">原因：{{detail?.risk?.mainReason}}</div>
    <div class="card" style="margin-top:10px"><base-line-chart title="风险趋势" :x-data="riskX" :y-data="riskY"/></div>
    <div class="card" style="margin-top:10px"><base-line-chart title="GPA变化" :x-data="gpaX" :y-data="gpaY"/></div>
    <div class="card" style="margin-top:10px"><base-line-chart title="作业成绩趋势" :x-data="hwX" :y-data="hwY"/></div>
    <div class="card" style="margin-top:10px"><base-line-chart title="考试成绩趋势" :x-data="examX" :y-data="examY"/></div>
  </div>
</template>
<script setup>
import {onMounted,ref} from 'vue'
import request from '@/utils/request'
import BaseLineChart from '@/components/charts/BaseLineChart.vue'
const user = JSON.parse(localStorage.getItem('system-user')||'{}')
const detail = ref({})
const riskX=ref([]), riskY=ref([]), gpaX=ref([]), gpaY=ref([]), hwX=ref([]), hwY=ref([]), examX=ref([]), examY=ref([])
onMounted(async()=>{
  const d=await request.get('/api/student/risk-detail',{params:{studentId:user.userId||user.id}}); detail.value=d.data||{}
  const t=await request.get('/api/student/trend',{params:{studentId:user.userId||user.id}})
  const risk=t.data?.risk||[]
  riskX.value=risk.map((_,i)=>`R${i+1}`).reverse(); riskY.value=risk.map(r=>r.riskProbability).reverse()
  const gpa=t.data?.gpa||[]
  gpaX.value=gpa.map((_,i)=>`G${i+1}`); gpaY.value=gpa
  const hw=t.data?.homeworkScores||[]
  hwX.value=hw.map((_,i)=>`H${i+1}`); hwY.value=hw
  const ex=t.data?.examScores||[]
  examX.value=ex.map((_,i)=>`E${i+1}`); examY.value=ex
})
</script>
