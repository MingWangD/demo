<template>
  <div>
    <h3 style="margin: 0 0 10px; color: #303133">学业总览</h3>
    <el-row :gutter="12">
      <el-col :span="8">
        <div :class="['card', 'metric', metricClassName]">
          <div>绩点</div>
          <h2>{{ data.gpa || 0 }}</h2>
          <el-tag :type="gpaTagType">{{ gpaColorLabel(data.gpaColor || "GREEN") }}</el-tag>
        </div>
      </el-col>
      <el-col :span="8">
        <div :class="['card', 'metric', metricClassName]">
          <div>已获学分</div>
          <h2>{{ data.earnedCredit || 0 }}</h2>
          <div>按最终成绩是否及格计算</div>
        </div>
      </el-col>
      <el-col :span="8">
        <div :class="['card', 'metric', metricClassName]">
          <div>课程最终成绩均分</div>
          <h2>{{ data.finalScoreAvg || data.examAvgScore || 0 }}</h2>
          <div>期中 30% + 期末 70%</div>
        </div>
      </el-col>
    </el-row>

    <div class="card" style="margin-top: 10px">
      <div style="font-weight: 600; margin-bottom: 6px">当前预警原因</div>
      <div style="color: #606266">{{ displayReason }}</div>
    </div>

    <div class="card" style="margin-top: 10px">
      <div style="font-weight: 600; margin-bottom: 6px">平时过程指标</div>
      <el-progress :percentage="Number(((data.homeworkSubmitRate || 0) * 100).toFixed(2))" text-inside :stroke-width="16" />
      <div style="margin-top: 8px; color: #909399">作业提交率 {{ Number(((data.homeworkSubmitRate || 0) * 100).toFixed(2)) }}%</div>
      <div style="margin-top: 6px; color: #909399">出勤次数 {{ data.attendanceCount || 0 }}</div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive } from "vue";
import request from "@/utils/request";
import { gpaColorLabel } from "@/utils/display";

const user = JSON.parse(localStorage.getItem("system-user") || "{}");
const data = reactive({});

const gpaTagType = computed(() => {
  if ((data.gpaColor || "").toUpperCase() === "RED") return "danger";
  if ((data.gpaColor || "").toUpperCase() === "ORANGE") return "warning";
  if ((data.gpaColor || "").toUpperCase() === "YELLOW") return "warning";
  return "success";
});

const metricClassName = computed(() => `metric-${(data.gpaColor || "GREEN").toLowerCase()}`);

const displayReason = computed(() => {
  if (!data.mainReason) return "当前学习状态稳定";
  return data.mainReason;
});

onMounted(async () => {
  const response = await request.get("/api/student/overview", { params: { studentId: user.userId || user.id } });
  Object.assign(data, response.data || {});
});
</script>

<style scoped>
.metric h2 { margin: 8px 0; color: #303133; }
.metric { color: #fff; border-radius: 12px; }
.metric-green { background: linear-gradient(135deg, #43e97b, #38f9d7); }
.metric-yellow { background: linear-gradient(135deg, #ffd86f, #fc6262); }
.metric-orange { background: linear-gradient(135deg, #ff9a44, #fc6076); }
.metric-red { background: linear-gradient(135deg, #f85032, #e73827); }
</style>
