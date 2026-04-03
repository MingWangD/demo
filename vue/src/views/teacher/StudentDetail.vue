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
      <div style="display:flex;justify-content:space-between;align-items:center">
        <h4 style="margin:0">{{data.student.name}} 的学情画像</h4>
        <el-tag type="danger">风险：{{data.latestRisk?.riskLevel || 'LOW'}}</el-tag>
      </div>
      <el-row :gutter="12" style="margin-top:10px">
        <el-col :span="6"><div class="card">出勤次数<br><b>{{data.attendanceCount || 0}}</b></div></el-col>
        <el-col :span="6"><div class="card">作业提交率<br><b>{{Number((data.homeworkSubmitRate||0)*100).toFixed(2)}}%</b></div></el-col>
        <el-col :span="6"><div class="card">作业均分<br><b>{{data.homeworkAvgScore || 0}}</b></div></el-col>
        <el-col :span="6"><div class="card">考试均分<br><b>{{data.examAvgScore || 0}}</b></div></el-col>
      </el-row>
      <div class="card" style="margin-top:10px">
        GPA：<b>{{data.academic?.gpa || 0}}</b>
        <el-tag style="margin-left:8px">{{data.academic?.gpaColor || 'GREEN'}}</el-tag>
      </div>
      <div class="card" style="margin-top:10px">
        <div style="font-weight:600;margin-bottom:6px">最新风险原因</div>
        <div style="color:#606266">{{data.latestRisk?.mainReason || '学习状态稳定'}}</div>
      </div>
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
  const r = await request.get('/api/teacher/course-students',{params:{courseId:courseId.value}})
  students.value = r.data || []
  if (!studentId.value && students.value.length) studentId.value = String(students.value[0].studentId)
}
const load=async()=>{
  if (!studentId.value) return
  const r=await request.get('/api/teacher/student-detail',{params:{studentId:studentId.value,courseId:courseId.value||undefined}})
  Object.assign(data,r.data||{})
}
loadCourses().then(loadStudentOptions).then(load)
</script>
