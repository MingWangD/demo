<template>
  <div>
    <h3 style="margin:0 0 10px 0;color:#303133">考试管理</h3>
    <div class="card">
      <el-select v-model="form.courseId" placeholder="选择课程" style="width:180px;margin-right:10px" @change="load">
        <el-option v-for="c in courses" :key="c.id" :label="`${c.courseName}(ID:${c.id})`" :value="String(c.id)" />
      </el-select>
      <el-input v-model="form.examName" placeholder="考试名称" style="width:200px;margin-right:10px"/>
      <el-button @click="showBank=true">题库选题</el-button>
      <span style="margin:0 10px;color:#606266">当前总分：{{totalScore}} / 100</span>
      <el-button type="primary" @click="create">发布考试</el-button>
      <el-button @click="load" style="margin-left:8px">刷新管理列表</el-button>
    </div>
    <div class="card" style="margin-top:10px" v-for="item in list" :key="item.exam.id">
      <h4>{{item.exam.examName}} (ID:{{item.exam.id}})
        <el-button size="small" type="danger" plain style="margin-left:10px" @click="undo(item.exam.id)">撤销发布</el-button>
      </h4>
      <el-table :data="item.details || []" empty-text="暂无考试明细">
        <el-table-column label="学生ID" prop="qualification.studentId"/>
        <el-table-column label="资格" prop="qualification.isQualified"/>
        <el-table-column label="资格说明" prop="qualification.reason"/>
        <el-table-column label="成绩" prop="record.score"/>
        <el-table-column label="作答/判分说明" prop="record.remark"/>
      </el-table>
    </div>

    <el-dialog v-model="showBank" title="考试题库选题（客观2分，主观10分）" width="760px">
      <el-table :data="questionBank" height="420">
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
      <template #footer><el-button @click="showBank=false">完成</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup>
import {computed, reactive, ref} from 'vue'
import request from '@/utils/request'
import { getQuestionBank } from '@/utils/questionBank'
const courses = ref([])
const list=ref([])
const form=reactive({courseId:'',examName:'阶段考试',examTime:'2026-12-31T10:00:00',totalScore:0,durationMinutes:120,description:''})
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
  form.description = picked.value.map((q, idx) => `${idx + 1}. ${q.title}（${q.score}分）`).join('\n')
  form.totalScore = totalScore.value || 100
  await request.post('/api/exam/create',{...form, courseId:Number(form.courseId)})
  load()
}
const load=async()=>{const r=await request.get('/api/teacher/exam-manage',{params:{courseId:form.courseId}}); list.value=r.data||[]}
const undo=async(examId)=>{await request.post('/api/exam/undo',null,{params:{examId}}); load()}
loadCourses().then(load)
</script>
