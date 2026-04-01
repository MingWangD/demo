<template>
  <div>
    <el-input v-model="form.courseId" placeholder="课程ID" style="width:120px;margin-right:10px"/>
    <el-input v-model="form.examName" placeholder="考试名称" style="width:200px;margin-right:10px"/>
    <el-button type="primary" @click="create">发布考试</el-button>
  </div>
</template>
<script setup>
import {reactive} from 'vue'
import request from '@/utils/request'
const user = JSON.parse(localStorage.getItem('system-user')||'{}')
const form=reactive({courseId:'',examName:'阶段考试',examTime:'2026-12-31T10:00:00',totalScore:100,durationMinutes:120,description:'描述'})
const create=async()=>{await request.post('/api/exam/create',{...form,teacherId:user.userId||user.id});}
</script>
