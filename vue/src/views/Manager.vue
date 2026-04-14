<template>
  <div>
    <div class="topbar">
      <div class="brand">学业智能预警系统</div>
      <div class="user-info">{{ data.user.name || '-' }} ({{ data.user.role || '-' }})</div>
    </div>
    <div class="layout">
      <div class="sider">
        <el-menu router style="border: none" :default-active="router.currentRoute.value.path">
          <template v-if="data.user.role === 'STUDENT'">
            <el-menu-item index="/manager/student-overview">学业总览</el-menu-item>
            <el-menu-item index="/manager/student-homework">作业</el-menu-item>
            <el-menu-item index="/manager/student-exam">考试</el-menu-item>
            <el-menu-item index="/manager/student-risk">风险趋势</el-menu-item>
          </template>
          <template v-else-if="data.user.role === 'TEACHER'">
            <el-menu-item index="/manager/teacher-dashboard">教师看板</el-menu-item>
            <el-menu-item index="/manager/teacher-homework">作业管理</el-menu-item>
            <el-menu-item index="/manager/teacher-exam">考试管理</el-menu-item>
            <el-menu-item index="/manager/teacher-high-risk">高风险筛选</el-menu-item>
            <el-menu-item index="/manager/teacher-student-detail">学生学业详情</el-menu-item>
            <el-menu-item index="/manager/teacher-intervention">干预记录</el-menu-item>
          </template>
          <template v-else>
            <el-menu-item index="/manager/home">系统首页</el-menu-item>
            <el-menu-item index="/manager/admin">管理员信息</el-menu-item>
          </template>
          <el-menu-item @click="logout">退出系统</el-menu-item>
        </el-menu>
      </div>
      <div class="content"><router-view /></div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive } from "vue";
import router from "@/router";
import { ElMessage, ElMessageBox } from "element-plus";
import request from "@/utils/request";

const data = reactive({ user: JSON.parse(localStorage.getItem("system-user") || "{}") });
if (!data.user?.token) {
  ElMessage.error("请登录！");
  router.push("/login");
}

const logout = () => {
  router.push("/login");
  localStorage.removeItem("system-user");
};

const latestInterventionStorageKey = () => `student-last-intervention-id-${data.user.userId || data.user.id || ""}`;

const loadStudentInterventionReminder = async () => {
  try {
    if (data.user.role !== "STUDENT") return;
    const studentId = data.user.userId || data.user.id;
    if (!studentId) return;
    const res = await request.get("/api/intervention/list", { params: { studentId } });
    const interventions = res.data || [];
    const latest = interventions[0];
    if (!latest?.id || !latest?.interventionContent) return;
    const storageKey = latestInterventionStorageKey();
    const seenId = Number(localStorage.getItem(storageKey) || 0);
    if (latest.id <= seenId) return;
    await ElMessageBox.alert(latest.interventionContent, "老师干预提醒", {
      confirmButtonText: "我知道了",
      type: "warning"
    });
    localStorage.setItem(storageKey, String(latest.id));
  } catch (e) {
    // ignore reminder failure
  }
};

onMounted(async () => {
  await loadStudentInterventionReminder();
});
</script>

<style scoped>
.topbar { height: 60px; background-color: #2e3143; display: flex; align-items: center; border-bottom: 1px solid #ddd; }
.brand { flex: 1; padding-left: 20px; color: #fff; font-size: 24px; font-weight: bold; }
.user-info { padding-right: 10px; color: #fff; }
.layout { display: flex; }
.sider { width: 240px; border-right: 1px solid #ddd; min-height: calc(100vh - 60px); }
.content { flex: 1; width: 0; background-color: #f8f8ff; padding: 10px; }
</style>
