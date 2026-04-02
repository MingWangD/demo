<template>
  <div>
    <h3 style="margin:0 0 10px;color:#303133">学情总览</h3>
    <el-row :gutter="12">
      <el-col :span="8"><div class="card metric"><div>GPA</div><h2>{{data.gpa || 0}}</h2><el-tag>{{data.gpaColor || 'GREEN'}}</el-tag></div></el-col>
      <el-col :span="8"><div class="card metric"><div>风险等级</div><h2>{{data.riskLevel || 'LOW'}}</h2><div>概率 {{data.riskProbability || 0}}</div></div></el-col>
      <el-col :span="8"><div class="card metric"><div>考试均分</div><h2>{{data.examAvgScore || 0}}</h2><div>出勤 {{data.attendanceCount || 0}}</div></div></el-col>
    </el-row>
    <div class="card" style="margin-top:10px">
      <div style="font-weight:600;margin-bottom:6px">预警原因</div>
      <div style="color:#606266">{{data.mainReason || '学习状态稳定'}}</div>
    </div>
    <div class="card" style="margin-top:10px">
      <div style="font-weight:600;margin-bottom:6px">过程指标</div>
      <el-progress :percentage="Number(((data.homeworkSubmitRate||0)*100).toFixed(2))" text-inside :stroke-width="16" />
      <div style="margin-top:8px;color:#909399">作业提交率 {{data.homeworkSubmitRate || 0}}</div>
    </div>
  </div>
</template>
<script setup>
import {reactive,onMounted} from 'vue'
import request from '@/utils/request'
const user = JSON.parse(localStorage.getItem('system-user')||'{}')
const data = reactive({})
onMounted(async()=>{ const res=await request.get('/api/student/overview',{params:{studentId:user.userId||user.id}}); Object.assign(data,res.data||{}) })
</script>
<style scoped>
.metric h2{margin:8px 0;color:#303133}
</style>
