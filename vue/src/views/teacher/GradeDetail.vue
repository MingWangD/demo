<template>
  <div>
    <h3 style="margin:0 0 10px 0;color:#303133">作答详情与主观题判分</h3>
    <div class="card" v-if="type==='homework'">
      <h4>{{data.homework?.title}} - {{data.student?.name}}</h4>
      <div style="margin:8px 0;color:#606266">客观题自动得分：{{data.objectiveScore || 0}}</div>
      <el-table :data="objectiveRows" style="margin-bottom:10px">
        <el-table-column prop="index" label="#" width="60"/>
        <el-table-column prop="question" label="客观题"/>
        <el-table-column prop="correctAnswer" label="正确答案" width="120"/>
        <el-table-column label="学生答案" width="180">
          <template #default="scope">
            <span>{{ scope.row.answer }}</span>
            <span style="color:#f56c6c;margin-left:8px;font-weight:700">{{ scope.row.isCorrect ? '✓' : '✗' }}</span>
          </template>
        </el-table-column>
      </el-table>
      <el-table :data="subjectiveRows">
        <el-table-column prop="index" label="#" width="60"/>
        <el-table-column prop="question" label="主观题"/>
        <el-table-column prop="answer" label="学生作答"/>
        <el-table-column label="评分" width="160">
          <template #default="scope">
            <el-input v-model="scores[scope.$index]" placeholder="0-10"/>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top:10px;display:flex;gap:8px">
        <el-input v-model="comment" placeholder="评语（可选）"/>
        <el-button type="primary" @click="submitHomework">提交判分</el-button>
      </div>
    </div>

    <div class="card" v-if="type==='exam'">
      <h4>{{data.exam?.examName}} - {{data.student?.name}}</h4>
      <div style="margin:8px 0;color:#606266">客观题自动得分：{{data.objectiveScore || 0}}</div>
      <el-table :data="objectiveRows" style="margin-bottom:10px">
        <el-table-column prop="index" label="#" width="60"/>
        <el-table-column prop="question" label="客观题"/>
        <el-table-column prop="correctAnswer" label="正确答案" width="120"/>
        <el-table-column label="学生答案" width="180">
          <template #default="scope">
            <span>{{ scope.row.answer }}</span>
            <span style="color:#f56c6c;margin-left:8px;font-weight:700">{{ scope.row.isCorrect ? '✓' : '✗' }}</span>
          </template>
        </el-table-column>
      </el-table>
      <el-table :data="subjectiveRows">
        <el-table-column prop="index" label="#" width="60"/>
        <el-table-column prop="question" label="主观题"/>
        <el-table-column prop="answer" label="学生作答"/>
        <el-table-column label="评分" width="160">
          <template #default="scope">
            <el-input v-model="scores[scope.$index]" placeholder="0-10"/>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top:10px;display:flex;gap:8px">
        <el-input v-model="comment" placeholder="评语（可选）"/>
        <el-button type="primary" @click="submitExam">提交判分</el-button>
      </div>
    </div>
  </div>
</template>
<script setup>
import {computed, onMounted, ref} from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const type = ref(route.query.type || 'homework')
const submissionId = ref(route.query.submissionId || '')
const recordId = ref(route.query.recordId || '')
const data = ref({})
const scores = ref([])
const comment = ref('')

const objectiveRows = computed(() => {
  const q = data.value.objectiveQuestions || []
  const a = data.value.objectiveAnswers || []
  const c = data.value.objectiveCorrectAnswers || []
  return q.map((question, i) => {
    const answer = String(a[i] || '-')
    const correctAnswer = String(c[i] || '-')
    return {
      index: i + 1,
      question,
      answer,
      correctAnswer,
      isCorrect: answer !== '-' && correctAnswer !== '-' && answer.toUpperCase() === correctAnswer.toUpperCase()
    }
  })
})

const subjectiveRows = computed(() => {
  const q = data.value.subjectiveQuestions || []
  const a = data.value.subjectiveAnswers || []
  return q.map((question, i) => ({ index: i + 1, question, answer: a[i] || '-' }))
})

const load = async() => {
  if (type.value === 'homework' && submissionId.value) {
    const r = await request.get('/api/teacher/homework-submission-detail', { params: { submissionId: submissionId.value } })
    data.value = r.data || {}
  } else if (type.value === 'exam' && recordId.value) {
    const r = await request.get('/api/teacher/exam-record-detail', { params: { recordId: recordId.value } })
    data.value = r.data || {}
  }
  const existing = data.value.subjectiveScores || []
  scores.value = (data.value.subjectiveQuestions || []).map((_, i) => existing[i] ?? '')
}

const submitHomework = async() => {
  await request.post('/api/teacher/homework-grade-detail', null, {
    params: { submissionId: submissionId.value, questionScores: JSON.stringify(scores.value), comment: comment.value }
  })
  ElMessage.success('判分成功')
  router.back()
}

const submitExam = async() => {
  await request.post('/api/teacher/exam-grade-detail', null, {
    params: { recordId: recordId.value, questionScores: JSON.stringify(scores.value), comment: comment.value }
  })
  ElMessage.success('判分成功')
  router.back()
}

onMounted(load)
</script>
