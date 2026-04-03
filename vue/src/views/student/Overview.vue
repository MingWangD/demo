<template>
  <div>
    <h3 style="margin:0 0 10px;color:#303133">学情总览</h3>
    <el-row :gutter="12">
      <el-col :span="8"><div :class="['card','metric',metricClassName]"><div>GPA</div><h2>{{data.gpa || 0}}</h2><el-tag :type="gpaTagType">{{data.gpaColor || 'GREEN'}}</el-tag></div></el-col>
      <el-col :span="8"><div :class="['card','metric',metricClassName]"><div>风险等级</div><h2>{{data.riskLevel || 'LOW'}}</h2></div></el-col>
      <el-col :span="8"><div :class="['card','metric',metricClassName]"><div>考试均分</div><h2>{{data.examAvgScore || 0}}</h2><div>出勤 {{data.attendanceCount || 0}}</div></div></el-col>
    </el-row>
    <div class="card" style="margin-top:10px">
      <div style="font-weight:600;margin-bottom:6px">预警原因</div>
      <div style="color:#606266">{{displayReason}}</div>
    </div>
    <div class="card" style="margin-top:10px">
      <div style="font-weight:600;margin-bottom:6px">过程指标</div>
      <el-progress :percentage="Number(((data.homeworkSubmitRate||0)*100).toFixed(2))" text-inside :stroke-width="16" />
      <div style="margin-top:8px;color:#909399">作业提交率 {{Number(((data.homeworkSubmitRate||0)*100).toFixed(2))}}%</div>
    </div>
  </div>
</template>
<script setup>
import {computed,reactive,onMounted} from 'vue'
import request from '@/utils/request'
const user = JSON.parse(localStorage.getItem('system-user')||'{}')
const data = reactive({})
const gpaTagType = computed(()=>{
  if ((data.gpaColor||'').toUpperCase() === 'RED') return 'danger'
  if ((data.gpaColor||'').toUpperCase() === 'ORANGE') return 'warning'
  if ((data.gpaColor||'').toUpperCase() === 'YELLOW') return 'warning'
  return 'success'
})
const metricClassName = computed(() => `metric-${(data.gpaColor || 'GREEN').toLowerCase()}`)
const displayReason = computed(() => {
  if ((data.gpaColor || '').toUpperCase() === 'GREEN') return '-'
  return data.mainReason || '学习状态稳定'
})
onMounted(async()=>{ const res=await request.get('/api/student/overview',{params:{studentId:user.userId||user.id}}); Object.assign(data,res.data||{}) })
</script>
<style scoped>
.metric h2{margin:8px 0;color:#303133}
.metric{color:#fff;border-radius:12px}
.metric-green{background:linear-gradient(135deg,#43e97b,#38f9d7)}
.metric-yellow{background:linear-gradient(135deg,#ffd86f,#fc6262)}
.metric-orange{background:linear-gradient(135deg,#ff9a44,#fc6076)}
.metric-red{background:linear-gradient(135deg,#f85032,#e73827)}
</style>
