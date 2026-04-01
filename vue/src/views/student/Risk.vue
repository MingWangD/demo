<template>
  <div>
    <div class="card">当前风险：{{detail?.risk?.riskProbability}} / {{detail?.risk?.riskLevel}} / {{detail?.risk?.warningColor}}</div>
    <div class="card" style="margin-top:10px">原因：{{detail?.risk?.mainReason}}</div>
    <base-line-chart title="风险趋势" :x-data="x" :y-data="y" style="margin-top:10px"/>
  </div>
</template>
<script setup>
import {onMounted,ref} from 'vue'
import request from '@/utils/request'
import BaseLineChart from '@/components/charts/BaseLineChart.vue'
const user = JSON.parse(localStorage.getItem('system-user')||'{}')
const detail = ref({}); const x = ref([]); const y = ref([])
onMounted(async()=>{
  const d=await request.get('/api/student/risk-detail',{params:{studentId:user.userId||user.id}}); detail.value=d.data||{}
  const t=await request.get('/api/student/trend',{params:{studentId:user.userId||user.id}})
  const risk=t.data?.risk||[]; x.value=risk.map((_,i)=>`P${i+1}`).reverse(); y.value=risk.map(r=>r.riskProbability).reverse()
})
</script>
