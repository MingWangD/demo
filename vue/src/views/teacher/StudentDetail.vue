<template>
  <div>
    <h3 style="margin:0 0 10px 0;color:#303133">学生学情详情</h3>
    <div class="card">
      <el-input v-model="studentId" placeholder="学生ID" style="width:120px;margin-right:8px"/>
      <el-input v-model="courseId" placeholder="课程ID(可选)" style="width:140px;margin-right:8px"/>
      <el-button type="primary" @click="load">查询</el-button>
    </div>
    <div class="card" style="margin-top:10px" v-if="data.student">
      <p>学生：{{data.student.name}}</p>
      <p>出勤：{{data.attendanceCount}}，作业提交率：{{data.homeworkSubmitRate}}，作业均分：{{data.homeworkAvgScore}}</p>
      <p>考试均分：{{data.examAvgScore}}</p>
      <p>GPA：{{data.academic?.gpa}} ({{data.academic?.gpaColor}})</p>
      <p>最新风险：{{data.latestRisk?.riskProbability}} / {{data.latestRisk?.riskLevel}} / {{data.latestRisk?.mainReason}}</p>
    </div>
  </div>
</template>
<script setup>
import {ref,reactive} from 'vue'
import request from '@/utils/request'
const studentId=ref('101'); const courseId=ref('201'); const data=reactive({})
const load=async()=>{const r=await request.get('/api/teacher/student-detail',{params:{studentId:studentId.value,courseId:courseId.value||undefined}}); Object.assign(data,r.data||{})}
</script>
