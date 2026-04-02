<template>
  <div>
    <h3 style="margin:0 0 10px 0;color:#303133">学生学情详情</h3>
    <div class="card">
      <el-select v-model="courseId" placeholder="选择课程" style="width:220px;margin-right:8px" @change="loadStudentOptions">
        <el-option v-for="c in courses" :key="c.id" :label="`${c.courseName}(ID:${c.id})`" :value="String(c.id)" />
      </el-select>
      <el-select v-model="studentId" placeholder="选择学生" style="width:220px;margin-right:8px">
        <el-option v-for="s in students" :key="s.studentId" :label="`${s.studentName}(ID:${s.studentId})`" :value="String(s.studentId)" />
      </el-select>
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
const studentId=ref('')
const courseId=ref('')
const courses=ref([])
const students=ref([])
const data=reactive({})
const loadCourses = async()=>{
  const r = await request.get('/api/teacher/courses')
  courses.value = r.data || []
  if (courses.value.length && !courseId.value) courseId.value = String(courses.value[0].id)
}
const loadStudentOptions = async()=>{
  if (!courseId.value) return
  const r = await request.get('/api/teacher/high-risk',{params:{courseId:courseId.value}})
  const map = new Map()
  ;(r.data || []).forEach(i => {
    if (i.student?.id && !map.has(i.student.id)) map.set(i.student.id, { studentId:i.student.id, studentName:i.student.name })
  })
  students.value = [...map.values()]
  if (!studentId.value && students.value.length) studentId.value = String(students.value[0].studentId)
}
const load=async()=>{
  if (!studentId.value) return
  const r=await request.get('/api/teacher/student-detail',{params:{studentId:studentId.value,courseId:courseId.value||undefined}})
  Object.assign(data,r.data||{})
}
loadCourses().then(loadStudentOptions).then(load)
</script>
