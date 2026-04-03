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
        <template #default="scope"><el-button :disabled="!scope.row.isQualified || scope.row.status==='FINISHED'" @click="openSubmit(scope.row)">完成考试</el-button></template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showDialog" title="考试作答" width="760px">
      <div v-if="current">
        <h4>{{current.examName}}</h4>
        <div v-for="(q, idx) in objectiveQuestions" :key="idx" style="margin-bottom:8px">
          <div>{{idx + 1}}. {{q}}</div>
          <el-radio-group v-model="objectiveAnswers[idx]">
            <el-radio label="A">A</el-radio>
            <el-radio label="B">B</el-radio>
            <el-radio label="C">C</el-radio>
            <el-radio label="D">D</el-radio>
          </el-radio-group>
        </div>
        <div style="margin-top:10px">
          <div style="font-weight:600">主观题作答</div>
          <div v-for="(q, idx) in subjectiveQuestions" :key="`es-${idx}`" style="margin-top:8px">
            <div>{{idx + 1}}. {{q}}</div>
            <el-input v-model="subjectiveAnswers[idx]" type="textarea" :rows="2" placeholder="请输入主观题答案"/>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="showDialog=false">取消</el-button>
        <el-button type="primary" @click="submit">提交考试</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import {onMounted,ref} from 'vue'
import request from '@/utils/request'
import { ElMessageBox } from 'element-plus'
const user = JSON.parse(localStorage.getItem('system-user')||'{}')
const courses = ref([]); const courseId = ref(); const list = ref([])
const showDialog = ref(false)
const current = ref(null)
const objectiveQuestions = ref([])
const objectiveAnswers = ref([])
const subjectiveQuestions = ref([])
const subjectiveAnswers = ref([])
const normalize = (content='') => String(content).replace(/\\\\n/g, '\n')
const loadCourses = async()=>{const r=await request.get('/api/student/courses',{params:{studentId:user.userId||user.id}}); courses.value=r.data||[]; if(courses.value.length){courseId.value=courses.value[0].id; load()}}
const load = async()=>{ const r=await request.get('/api/student/exams',{params:{studentId:user.userId||user.id,courseId:courseId.value}}); list.value=r.data||[] }
const openSubmit = (row)=>{
  current.value = row
  const lines = normalize(row.description || '').split('\n').map(i => i.trim()).filter(Boolean)
  objectiveQuestions.value = lines.filter(i => /（2分）|\(2分\)|客观题/.test(i))
  subjectiveQuestions.value = lines.filter(i => /（10分）|\(10分\)|主观题/.test(i))
  objectiveAnswers.value = Array(objectiveQuestions.value.length).fill('')
  subjectiveAnswers.value = Array(subjectiveQuestions.value.length).fill('')
  showDialog.value = true
}
const submit = async()=>{
  await ElMessageBox.confirm('考试提交后客观题与主观题均不可修改，确定提交吗？', '提交确认', { type:'warning' })
  const objectiveAnswered = objectiveAnswers.value.filter(Boolean).length
  await request.post('/api/exam/submit',{
    examId:current.value.examId,
    studentId:user.userId||user.id,
    score:80,
    answerContent:JSON.stringify({ objectiveAnswered, objectiveDetail: objectiveAnswers.value, subjectiveAnswers: subjectiveAnswers.value, objectiveLocked:true })
  })
  showDialog.value = false
  load()
}
onMounted(loadCourses)
</script>
