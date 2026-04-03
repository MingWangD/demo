<template>
  <div>
    <h3 style="margin:0 0 10px 0;color:#303133">作业管理</h3>
    <div class="card">
      <el-select v-model="form.courseId" placeholder="选择课程" style="width:180px;margin-right:10px" @change="load">
        <el-option v-for="c in courses" :key="c.id" :label="`${c.courseName}(ID:${c.id})`" :value="String(c.id)" />
      </el-select>
      <el-input v-model="form.title" placeholder="标题" style="width:200px;margin-right:10px"/>
      <el-button @click="showBank=true">题库选题</el-button>
      <span style="margin:0 10px;color:#606266">当前总分：{{totalScore}} / 100</span>
      <el-button type="primary" @click="create">发布作业</el-button>
      <el-button @click="load" style="margin-left:8px">刷新管理列表</el-button>
    </div>

    <div class="card" style="margin-top:10px" v-for="item in list" :key="item.homework.id">
      <h4>{{item.homework.title}} (ID:{{item.homework.id}})
        <el-button size="small" type="danger" plain style="margin-left:10px" @click="undo(item.homework.id)">撤销发布</el-button>
      </h4>
      <el-table :data="item.submissions || []" empty-text="暂无提交记录">
        <el-table-column prop="studentId" label="学生ID"/>
        <el-table-column prop="status" label="状态"/>
        <el-table-column prop="score" label="分数"/>
        <el-table-column label="学生作答">
          <template #default="scope">
            {{ shortAnswer(scope.row.submitContent) }}
          </template>
        </el-table-column>
        <el-table-column prop="teacherComment" label="评语"/>
        <el-table-column label="详情" width="110">
          <template #default="scope">
            <el-button size="small" @click="goDetail(scope.row)">本次作业</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="showBank" title="题库选题（客观题2分，主观题10分）" width="760px">
      <el-alert type="info" :closable="false" show-icon title="每个科目题库≥50题（客观30 + 主观20）；总分达到100后将禁止继续添加。"/>
      <el-table :data="questionBank" height="420" style="margin-top:10px">
        <el-table-column prop="id" label="题号" width="150"/>
        <el-table-column prop="type" label="题型" width="120"/>
        <el-table-column prop="score" label="分值" width="80"/>
        <el-table-column prop="title" label="题目"/>
        <el-table-column label="操作" width="100">
          <template #default="scope">
            <el-button size="small" type="primary" :disabled="isPicked(scope.row.id) || totalScore + scope.row.score > 100" @click="pick(scope.row)">+</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top:10px;color:#606266">已选 {{picked.length}} 题，总分 {{totalScore}}</div>
      <template #footer>
        <el-button @click="showBank=false">完成</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import {computed, reactive, ref} from 'vue'
import request from '@/utils/request'
import { getQuestionBank } from '@/utils/questionBank'
import { useRouter } from 'vue-router'
const router = useRouter()
const courses = ref([])
const list = ref([])
const form=reactive({courseId:'',title:'新作业',content:'',deadline:'2026-12-31T23:59:59',totalScore:0})
const showBank = ref(false)
const picked = ref([])
const questionBank = computed(() => getQuestionBank(form.courseId))
const totalScore = computed(() => picked.value.reduce((s, q) => s + q.score, 0))
const isPicked = (id) => picked.value.some(i => i.id === id)
const pick = (q) => { if (!isPicked(q.id) && totalScore.value + q.score <= 100) picked.value.push(q) }
const loadCourses = async()=>{
  const r = await request.get('/api/teacher/courses')
  courses.value = r.data || []
  if (!form.courseId && courses.value.length) form.courseId = String(courses.value[0].id)
}
const create=async()=>{
  form.content = picked.value.map((q, idx) => `${idx + 1}. ${q.title}（${q.score}分）`).join('\n')
  form.totalScore = totalScore.value || 100
  await request.post('/api/homework/create',{...form, courseId:Number(form.courseId)})
  load()
}
const load=async()=>{const r=await request.get('/api/teacher/homework-manage',{params:{courseId:form.courseId}}); list.value=r.data||[]}
const undo=async(homeworkId)=>{await request.post('/api/homework/undo',null,{params:{homeworkId}}); load()}
const shortAnswer=(raw='')=>{
  try {
    const m=JSON.parse(raw||'{}')
    const oa=(m.objectiveDetail||[]).join(',')
    const sa=(m.subjectiveAnswers||[]).join(' | ')
    return `客观:${oa || '-'}；主观:${sa || '-'}`
  } catch {
    return String(raw||'').slice(0,30)
  }
}
const goDetail=(row)=>router.push({path:'/manager/teacher-grade-detail',query:{type:'homework',submissionId:row.id}})
loadCourses().then(load)
</script>
