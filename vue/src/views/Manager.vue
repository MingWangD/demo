<template>
  <div class="manager-shell">
    <div class="topbar">
      <div class="brand">学业预警系统</div>
      <div class="user-info">{{ data.user.name || "-" }}（{{ roleLabel(data.user.role) }}）</div>
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
            <el-menu-item index="/manager/teacher-attendance">统计出勤</el-menu-item>
            <el-menu-item index="/manager/teacher-homework">作业管理</el-menu-item>
            <el-menu-item index="/manager/teacher-exam">考试管理</el-menu-item>
            <el-menu-item index="/manager/teacher-high-risk">高风险筛选</el-menu-item>
            <el-menu-item index="/manager/teacher-student-detail">学生学业详情</el-menu-item>
            <el-menu-item index="/manager/teacher-intervention">干预记录</el-menu-item>
          </template>
          <template v-else-if="data.user.role === 'ADMIN'">
            <el-menu-item index="/manager/home">系统首页</el-menu-item>
            <el-menu-item index="/manager/admin-students">学生管理</el-menu-item>
            <el-menu-item index="/manager/admin-teachers">教师管理</el-menu-item>
          </template>
          <template v-else>
            <el-menu-item index="/manager/home">系统首页</el-menu-item>
          </template>
          <el-menu-item @click="logout">退出系统</el-menu-item>
        </el-menu>
      </div>
      <div class="content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import router from "@/router";
import request from "@/utils/request";
import { roleLabel } from "@/utils/display";

const data = reactive({
  user: JSON.parse(localStorage.getItem("system-user") || "{}")
});

if (!data.user?.token) {
  ElMessage.error("请先登录");
  router.push("/login");
}

const logout = () => {
  localStorage.removeItem("system-user");
  router.push("/login");
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
    await ElMessageBox.alert(latest.interventionContent, "教师干预提醒", {
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
.topbar {
  height: 64px;
  background: linear-gradient(90deg, #133b44 0%, #1d5661 100%);
  display: flex;
  align-items: center;
  border-bottom: 1px solid rgba(19, 59, 68, 0.08);
  box-shadow: 0 10px 30px rgba(19, 59, 68, 0.08);
}

.brand {
  flex: 1;
  padding-left: 24px;
  color: #fff;
  font-size: 24px;
  font-weight: 800;
  letter-spacing: 0.04em;
}

.user-info {
  padding-right: 18px;
  color: rgba(255, 250, 240, 0.92);
  font-weight: 500;
}

.layout {
  display: flex;
  min-height: calc(100vh - 64px);
  background: linear-gradient(180deg, #f6f3ee 0%, #eef3f2 100%);
}

.sider {
  width: 240px;
  padding: 14px 12px;
  border-right: 1px solid rgba(19, 59, 68, 0.08);
  min-height: calc(100vh - 64px);
  background: rgba(255, 253, 248, 0.9);
  backdrop-filter: blur(6px);
}

.content {
  flex: 1;
  width: 0;
  padding: 16px;
}

:deep(.el-menu) {
  background: transparent;
}

:deep(.el-menu-item) {
  margin-bottom: 6px;
  border-radius: 12px;
  color: #506462;
}

:deep(.el-menu-item:hover) {
  background: rgba(15, 118, 110, 0.08);
  color: #133b44;
}

:deep(.el-menu-item.is-active) {
  background: linear-gradient(90deg, rgba(15, 118, 110, 0.12), rgba(217, 164, 65, 0.16));
  color: #133b44;
  font-weight: 700;
}
</style>
