<template>
  <div>
    <el-select v-model="courseId" placeholder="选择课程" style="width:240px" @change="load">
      <el-option v-for="c in courses" :key="c.id" :label="c.courseName" :value="c.id" />
    </el-select>
    <el-table :data="list" style="margin-top:10px">
      <el-table-column prop="examName" label="考试"/>
      <el-table-column prop="isQualified" label="资格"/>
      <el-table-column prop="qualificationReason" label="说明"/>
      <el-table-column prop="status" label="状态"/>
      <el-table-column prop="score" label="当前得分"/>
      <el-table-column prop="autoJudgeRemark" label="判分说明"/>
      <el-table-column label="参加">
        <template #default="scope"><el-button :disabled="!scope.row.isQualified" @click="submit(scope.row.examId)">提交</el-button></template>
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
const load = async()=>{ const r=await request.get('/api/student/exams',{params:{studentId:user.userId||user.id,courseId:courseId.value}}); list.value=r.data||[] }
const submit = async(examId)=>{
  await request.post('/api/exam/submit',{
    examId,
    studentId:user.userId||user.id,
    score:80,
    answerContent:'客观题答案已提交；主观题答案已提交'
  })
  load()
}
onMounted(loadCourses)
</script>
