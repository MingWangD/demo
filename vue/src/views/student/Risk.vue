<template>
  <div>
    <h3 style="margin: 0 0 10px 0; color: #303133">风险与趋势详情</h3>
    <div class="card">当前风险：{{ riskLevelLabel(detail?.risk?.riskLevel) }} / {{ gpaColorLabel(detail?.risk?.warningColor) }}</div>
    <div class="card" style="margin-top: 10px">原因：{{ displayReason }}</div>
    <div class="card" style="margin-top: 10px">
      <div style="font-weight: 600; margin-bottom: 6px">教师干预提醒</div>
      <div style="color: #606266">{{ interventionContent }}</div>
    </div>
    <div class="card" style="margin-top: 10px">
      <base-line-chart title="作业成绩趋势" :x-data="hwX" :y-data="hwY" x-name="第几次作业" y-name="成绩" />
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import request from "@/utils/request";
import BaseLineChart from "@/components/charts/BaseLineChart.vue";
import { gpaColorLabel, riskLevelLabel } from "@/utils/display";

const user = JSON.parse(localStorage.getItem("system-user") || "{}");
const detail = ref({});
const hwX = ref([]);
const hwY = ref([]);

const isGreenGpa = computed(() => (detail.value?.academic?.gpaColor || "").toUpperCase() === "GREEN");
const displayReason = computed(() => {
  if (isGreenGpa.value) return "当前学习状态稳定";
  return detail.value?.risk?.mainReason || "当前学习状态稳定";
});
const interventionContent = computed(() => detail.value?.latestIntervention?.interventionContent || "暂无教师干预记录");

onMounted(async () => {
  const detailResp = await request.get("/api/student/risk-detail", { params: { studentId: user.userId || user.id } });
  detail.value = detailResp.data || {};

  const trendResp = await request.get("/api/student/trend", { params: { studentId: user.userId || user.id } });
  const homeworkScores = trendResp.data?.homeworkScores || [];
  hwX.value = homeworkScores.map((_, i) => `第${i + 1}次`);
  hwY.value = homeworkScores;
});
</script>
