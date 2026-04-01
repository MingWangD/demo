<template>
  <div>
    <h3 style="margin:0 0 10px 0;color:#303133">考试管理</h3>
    <div class="card">
      <el-input v-model="form.courseId" placeholder="课程ID" style="width:120px;margin-right:10px"/>
      <el-input v-model="form.examName" placeholder="考试名称" style="width:200px;margin-right:10px"/>
      <el-button type="primary" @click="create">发布考试</el-button>
      <el-button @click="load" style="margin-left:8px">刷新管理列表</el-button>
    </div>
    <div class="card" style="margin-top:10px" v-for="item in list" :key="item.exam.id">
      <h4>{{item.exam.examName}} (ID:{{item.exam.id}})</h4>
      <el-table :data="item.details || []" empty-text="暂无考试明细">
        <el-table-column label="学生ID" prop="qualification.studentId"/>
        <el-table-column label="资格" prop="qualification.isQualified"/>
        <el-table-column label="资格说明" prop="qualification.reason"/>
        <el-table-column label="成绩" prop="record.score"/>
      </el-table>
    </div>
  </div>
</template>
<script setup>
import {reactive, ref} from 'vue'
import request from '@/utils/request'
const user = JSON.parse(localStorage.getItem('system-user')||'{}')
const list=ref([])
const form=reactive({courseId:'201',examName:'阶段考试',examTime:'2026-12-31T10:00:00',totalScore:100,durationMinutes:120,description:'描述'})
const create=async()=>{await request.post('/api/exam/create',{...form}); load()}
const load=async()=>{const r=await request.get('/api/teacher/exam-manage',{params:{courseId:form.courseId}}); list.value=r.data||[]}
load()
</script>
