<template>
  <div>
    <el-input v-model="form.studentId" placeholder="学生ID" style="width:100px;margin-right:10px"/>
    <el-input v-model="form.warningId" placeholder="预警ID" style="width:100px;margin-right:10px"/>
    <el-button type="primary" @click="add">新增干预</el-button>
    <el-table :data="list" style="margin-top:10px">
      <el-table-column prop="studentId" label="学生"/>
      <el-table-column prop="interventionType" label="类型"/>
      <el-table-column prop="interventionContent" label="内容"/>
    </el-table>
  </div>
</template>
<script setup>
import {onMounted,reactive,ref} from 'vue'
import request from '@/utils/request'
const user = JSON.parse(localStorage.getItem('system-user')||'{}')
const list = ref([])
const form=reactive({studentId:'',teacherId:user.userId||user.id,courseId:null,warningId:'',interventionType:'TALK',interventionContent:'关注学习状态',interventionResult:'待观察'})
const load=async()=>{const r=await request.get('/api/intervention/list'); list.value=r.data||[]}
const add=async()=>{await request.post('/api/intervention/add',form); load()}
onMounted(load)
</script>
