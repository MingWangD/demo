<template>
  <div>
    <div class="card">
      <el-input v-model="filters.courseId" placeholder="课程ID" style="width:120px;margin-right:8px"/>
      <el-select v-model="filters.riskLevel" placeholder="风险等级" style="width:120px;margin-right:8px">
        <el-option label="全部" value=""/><el-option label="HIGH" value="HIGH"/><el-option label="MEDIUM" value="MEDIUM"/><el-option label="LOW" value="LOW"/>
      </el-select>
      <el-select v-model="filters.gpaColor" placeholder="GPA颜色" style="width:120px;margin-right:8px">
        <el-option label="全部" value=""/><el-option label="RED" value="RED"/><el-option label="ORANGE" value="ORANGE"/><el-option label="YELLOW" value="YELLOW"/><el-option label="GREEN" value="GREEN"/>
      </el-select>
      <el-button type="primary" @click="load">筛选</el-button>
    </div>
    <el-table :data="list" style="margin-top:10px">
      <el-table-column label="学生" prop="student.name"/>
      <el-table-column label="风险概率" prop="risk.riskProbability"/>
      <el-table-column label="风险等级" prop="risk.riskLevel"/>
      <el-table-column label="GPA颜色" prop="academic.gpaColor"/>
      <el-table-column label="原因" prop="risk.mainReason"/>
    </el-table>
  </div>
</template>
<script setup>
import {reactive,ref,onMounted} from 'vue'
import request from '@/utils/request'
const list=ref([])
const filters=reactive({courseId:'',riskLevel:'HIGH',gpaColor:''})
const load=async()=>{const r=await request.get('/api/teacher/high-risk',{params:filters}); list.value=r.data||[]}
onMounted(load)
</script>
