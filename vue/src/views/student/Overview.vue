<template>
  <div>
    <div class="card">学情总览：GPA {{data.gpa}}（{{data.gpaColor}}），风险 {{data.riskProbability}} / {{data.riskLevel}}</div>
    <div class="card" style="margin-top:10px">预警原因：{{data.mainReason}}</div>
    <div class="card" style="margin-top:10px">出勤 {{data.attendanceCount}}，作业提交率 {{data.homeworkSubmitRate}}，考试均分 {{data.examAvgScore}}</div>
  </div>
</template>
<script setup>
import {reactive,onMounted} from 'vue'
import request from '@/utils/request'
const user = JSON.parse(localStorage.getItem('system-user')||'{}')
const data = reactive({})
onMounted(async()=>{ const res=await request.get('/api/student/overview',{params:{studentId:user.userId||user.id}}); Object.assign(data,res.data||{}) })
</script>
