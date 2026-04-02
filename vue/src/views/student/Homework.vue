<template>
  <div>
    <el-select v-model="courseId" placeholder="选择课程" style="width:240px" @change="load">
      <el-option v-for="c in courses" :key="c.id" :label="c.courseName" :value="c.id" />
    </el-select>
    <el-table :data="list" style="margin-top:10px">
      <el-table-column prop="homework.title" label="作业"/>
      <el-table-column label="状态">
        <template #default="scope">
          {{ statusLabel(scope.row.submission?.status) }}
        </template>
      </el-table-column>
      <el-table-column label="提交">
        <template #default="scope"><el-button @click="openSubmit(scope.row)">完成作业</el-button></template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showDialog" title="作业作答" width="760px">
      <div v-if="current">
        <h4>{{current.homework.title}}</h4>
        <div v-for="(q, idx) in objectiveQuestions" :key="idx" style="margin-bottom:12px">
          <div>{{idx + 1}}. {{q}}</div>
          <el-radio-group v-model="objectiveAnswers[idx]" :disabled="objectiveLocked">
            <el-radio v-for="opt in objectiveOptions[idx] || []" :key="opt.key" :label="opt.key">
              {{opt.key}}. {{opt.text}}
            </el-radio>
          </el-radio-group>
        </div>
        <div style="margin-top:10px">
          <div style="font-weight:600">主观题作答</div>
          <el-input v-model="subjectiveAnswer" type="textarea" :rows="4" placeholder="请输入主观题答案"/>
        </div>
      </div>
      <template #footer>
        <el-button @click="showDialog=false">取消</el-button>
        <el-button type="primary" @click="submit">提交作业</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import {onMounted,ref} from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
const user = JSON.parse(localStorage.getItem('system-user')||'{}')
const courses = ref([]); const courseId = ref(); const list = ref([])
const showDialog = ref(false)
const current = ref(null)
const objectiveQuestions = ref([])
const objectiveOptions = ref([])
const objectiveAnswers = ref([])
const subjectiveAnswer = ref('')
const objectiveLocked = ref(false)
const normalize = (content='') => String(content).replace(/\\\\n/g, '\n')
const parseLines = (content='') => normalize(content).split('\n').map(i => i.trim()).filter(Boolean)
const parseQuestions = (content='') => {
  const lines = parseLines(content)
  const objective = lines.filter(i => /客观题|选择题|单选题/.test(i))
  if (objective.length) return objective
  return lines.filter(i => !i.includes('主观题') && !i.includes('简答题'))
}
const parseSubjective = (content='') => parseLines(content).find(i => i.includes('主观题') || i.includes('简答题')) || ''
const parseSubmission = (text) => { try { return JSON.parse(text || '{}') } catch { return {} } }
const OPTION_POOL = [
  '时间复杂度更低', '空间开销最小', '鲁棒性更强', '可维护性更高', '便于并行处理', '更符合题干约束',
  '边界条件更完整', '结果更稳定', '实现成本更低', '可解释性更好', '与业务目标一致', '满足数据规模要求'
]
const pickDistinctOptions = (seed) => {
  const used = new Set()
  const options = []
  let cursor = (seed * 7) % OPTION_POOL.length
  while (options.length < 4) {
    const text = OPTION_POOL[cursor % OPTION_POOL.length]
    if (!used.has(text)) {
      used.add(text)
      options.push(text)
    }
    cursor += 3
  }
  return options
}
const buildObjectiveOptions = (questions=[]) => questions.map((_, idx) => {
  const texts = pickDistinctOptions(idx + 1)
  return ['A', 'B', 'C', 'D'].map((key, i) => ({ key, text: texts[i] }))
})
const statusLabel = (status) => {
  if (status === 'SUBMITTED') return '已提交'
  if (status === 'GRADED') return '已批改'
  return '未提交'
}
const loadCourses = async()=>{const r=await request.get('/api/student/courses',{params:{studentId:user.userId||user.id}}); courses.value=r.data||[]; if(courses.value.length){courseId.value=courses.value[0].id; load()}}
const load = async()=>{ const r=await request.get('/api/student/homework',{params:{studentId:user.userId||user.id,courseId:courseId.value}}); list.value=r.data||[] }
const openSubmit = (row) => {
  current.value = row
  objectiveQuestions.value = parseQuestions(row.homework.content || '')
  objectiveOptions.value = buildObjectiveOptions(objectiveQuestions.value)
  const existing = parseSubmission(row.submission?.submitContent)
  objectiveAnswers.value = existing.objectiveDetail || Array(objectiveQuestions.value.length).fill('')
  objectiveLocked.value = !!existing.objectiveLocked
  subjectiveAnswer.value = existing.subjectiveAnswer || parseSubjective(row.homework.content || '')
  showDialog.value = true
}
const submit = async()=>{
  await ElMessageBox.confirm('客观题提交后将锁定不可修改，主观题在教师批改前可再次提交并覆盖。确定提交吗？', '提交确认', { type:'warning' })
  const objectiveAnswered = objectiveAnswers.value.filter(Boolean).length
  const payload = {
    objectiveAnswered,
    objectiveDetail: objectiveAnswers.value,
    subjectiveAnswer: subjectiveAnswer.value,
    objectiveLocked: true
  }
  const res = await request.post('/api/homework/submit',{homeworkId:current.value.homework.id,studentId:user.userId||user.id,submitContent:JSON.stringify(payload)})
  if (res.code === '200') ElMessage.success('提交成功')
  showDialog.value = false
  load()
}
onMounted(loadCourses)
</script>
