<template>
  <div>
    <div style="height: 60px; background-color: #2e3143; display: flex; align-items: center; border-bottom: 1px solid #ddd">
      <div style="flex: 1; padding-left: 20px; color:#fff; font-size:24px; font-weight:bold">学情智能预警系统</div>
      <div style="padding-right: 10px; color:#fff;">{{ data.user.name || '-' }} ({{data.user.role || '-'}})</div>
    </div>
    <div style="display: flex">
      <div style="width: 240px; border-right: 1px solid #ddd; min-height: calc(100vh - 60px)">
        <el-menu router style="border: none" :default-active="router.currentRoute.value.path">
          <template v-if="data.user.role === 'STUDENT'">
            <el-menu-item index="/manager/student-overview">学情总览</el-menu-item>
            <el-menu-item index="/manager/student-homework">作业</el-menu-item>
            <el-menu-item index="/manager/student-exam">考试</el-menu-item>
            <el-menu-item index="/manager/student-risk">风险趋势</el-menu-item>
          </template>
          <template v-else-if="data.user.role === 'TEACHER'">
            <el-menu-item index="/manager/teacher-dashboard">教师看板</el-menu-item>
            <el-menu-item index="/manager/teacher-homework">作业管理</el-menu-item>
            <el-menu-item index="/manager/teacher-exam">考试管理</el-menu-item>
            <el-menu-item index="/manager/teacher-intervention">干预记录</el-menu-item>
          </template>
          <template v-else>
            <el-menu-item index="/manager/home">系统首页</el-menu-item>
            <el-menu-item index="/manager/admin">管理员信息</el-menu-item>
          </template>
          <el-menu-item @click="logout">退出系统</el-menu-item>
        </el-menu>
      </div>
      <div style="flex: 1; width: 0; background-color: #f8f8ff; padding: 10px"><router-view /></div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from "vue";
import router from "@/router";
import {ElMessage} from "element-plus";
const data = reactive({ user: JSON.parse(localStorage.getItem('system-user') || '{}') })
if (!data.user?.token) { ElMessage.error('请登录！'); router.push('/login') }
const logout = () => { router.push('/login'); localStorage.removeItem('system-user') }
</script>
