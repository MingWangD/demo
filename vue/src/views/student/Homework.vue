<template>
  <div>
    <el-select v-model="courseId" placeholder="选择课程" style="width:240px" @change="load">
      <el-option v-for="c in courses" :key="c.id" :label="c.courseName" :value="c.id" />
    </el-select>
    <el-table :data="list" style="margin-top:10px">
      <el-table-column prop="homework.title" label="作业"/>
      <el-table-column prop="submission.status" label="状态"/>
      <el-table-column label="提交">
        <template #default="scope"><el-button @click="submit(scope.row.homework.id)">提交</el-button></template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script setup>
import {onMounted,ref} from 'vue'
import request from '@/utils/request'
const user = JSON.parse(localStorage.getItem('system-user')||'{}')
const courses = ref([]); const courseId = ref(); const list = ref([])
const loadCourses = async()=>{const r=await request.get('/api/student/courses',{params:{studentId:user.userId||user.id}}); courses.value=r.data||[]; if(courses.value.length){courseId.value=courses.value[0].id; load()}}
const load = async()=>{ const r=await request.get('/api/student/homework',{params:{studentId:user.userId||user.id,courseId:courseId.value}}); list.value=r.data||[] }
const submit = async(homeworkId)=>{ await request.post('/api/homework/submit',{homeworkId,studentId:user.userId||user.id,submitContent:'在线提交内容'}); load() }
onMounted(loadCourses)
</script>
