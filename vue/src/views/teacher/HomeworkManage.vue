<template>
  <div>
    <el-input v-model="form.courseId" placeholder="课程ID" style="width:120px;margin-right:10px"/>
    <el-input v-model="form.title" placeholder="标题" style="width:200px;margin-right:10px"/>
    <el-button type="primary" @click="create">发布作业</el-button>
  </div>
</template>
<script setup>
import {reactive} from 'vue'
import request from '@/utils/request'
const user = JSON.parse(localStorage.getItem('system-user')||'{}')
const form=reactive({courseId:'',title:'新作业',content:'内容',deadline:'2026-12-31T23:59:59',totalScore:100})
const create=async()=>{await request.post('/api/homework/create',{...form,teacherId:user.userId||user.id});}
</script>
