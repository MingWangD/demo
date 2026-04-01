<template>
  <div>
    <div class="card">
      <el-input v-model="form.courseId" placeholder="课程ID" style="width:120px;margin-right:10px"/>
      <el-input v-model="form.title" placeholder="标题" style="width:200px;margin-right:10px"/>
      <el-button type="primary" @click="create">发布作业</el-button>
      <el-button @click="load" style="margin-left:8px">刷新管理列表</el-button>
    </div>

    <div class="card" style="margin-top:10px" v-for="item in list" :key="item.homework.id">
      <h4>{{item.homework.title}} (ID:{{item.homework.id}})</h4>
      <el-table :data="item.submissions || []">
        <el-table-column prop="studentId" label="学生ID"/>
        <el-table-column prop="status" label="状态"/>
        <el-table-column prop="score" label="分数"/>
        <el-table-column prop="teacherComment" label="评语"/>
        <el-table-column label="评分">
          <template #default="scope">
            <el-input v-model="scope.row._score" placeholder="分数" style="width:70px;margin-right:6px"/>
            <el-input v-model="scope.row._comment" placeholder="评语" style="width:120px;margin-right:6px"/>
            <el-button size="small" type="primary" @click="grade(scope.row)">提交</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>
<script setup>
import {reactive, ref} from 'vue'
import request from '@/utils/request'
const user = JSON.parse(localStorage.getItem('system-user')||'{}')
const list = ref([])
const form=reactive({courseId:'201',title:'新作业',content:'内容',deadline:'2026-12-31T23:59:59',totalScore:100})
const create=async()=>{await request.post('/api/homework/create',{...form,teacherId:user.userId||user.id}); load()}
const load=async()=>{const r=await request.get('/api/teacher/homework-manage',{params:{courseId:form.courseId}}); list.value=r.data||[]}
const grade=async(row)=>{await request.post('/api/teacher/homework-grade',null,{params:{submissionId:row.id,score:row._score||row.score||0,comment:row._comment||''}}); load()}
load()
</script>
