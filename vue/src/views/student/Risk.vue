<template>
  <div>
    <h3 style="margin:0 0 10px 0;color:#303133">风险与趋势详情</h3>
    <div class="card">当前风险：{{detail?.risk?.riskLevel || '-'}} / {{detail?.risk?.warningColor || '-'}}</div>
    <div class="card" style="margin-top:10px">原因：{{displayReason}}</div>
    <div class="card" style="margin-top:10px">
      <div style="font-weight:600;margin-bottom:6px">老师干预提醒</div>
      <div style="color:#606266">{{interventionContent}}</div>
    </div>
    <div class="card" style="margin-top:10px"><base-line-chart title="风险趋势" :x-data="riskX" :y-data="riskY" x-name="预警记录序号" y-name="风险概率"/></div>
    <div class="card" style="margin-top:10px"><base-line-chart title="GPA变化" :x-data="gpaX" :y-data="gpaY" x-name="统计序号" y-name="GPA"/></div>
    <div class="card" style="margin-top:10px"><base-line-chart title="作业成绩趋势" :x-data="hwX" :y-data="hwY" x-name="作业序号" y-name="成绩"/></div>
    <div class="card" style="margin-top:10px"><base-line-chart title="考试成绩趋势" :x-data="examX" :y-data="examY" x-name="考试序号" y-name="成绩"/></div>
  </div>
</template>
<script setup>
import {computed,onMounted,ref} from 'vue'
import request from '@/utils/request'
import BaseLineChart from '@/components/charts/BaseLineChart.vue'
const user = JSON.parse(localStorage.getItem('system-user')||'{}')
const detail = ref({})
const riskX=ref([]), riskY=ref([]), gpaX=ref([]), gpaY=ref([]), hwX=ref([]), hwY=ref([]), examX=ref([]), examY=ref([])
const isGreenGpa = computed(() => (detail.value?.academic?.gpaColor || '').toUpperCase() === 'GREEN')
const displayReason = computed(() => {
  if (isGreenGpa.value) return '-'
  return detail.value?.risk?.mainReason || '-'
})
const interventionContent = computed(() => detail.value?.latestIntervention?.interventionContent || '-')
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
