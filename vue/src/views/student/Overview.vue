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
          <div style="display: flex; align-items: center; justify-content: space-between; gap: 8px">
            <span>课程最终成绩</span>
            <el-select
              v-model="selectedCourseId"
              placeholder="选择课程"
              size="small"
              style="width: 180px"
            >
              <el-option
                v-for="course in courseFinalScores"
                :key="course.courseId"
                :label="course.courseName"
                :value="String(course.courseId)"
              />
            </el-select>
          </div>
          <h2>{{ selectedCourseScore }}</h2>
          <div>{{ selectedCourseHint }}</div>
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
import { computed, onMounted, reactive, ref, watch } from "vue";
import request from "@/utils/request";
import { gpaColorLabel } from "@/utils/display";

const user = JSON.parse(localStorage.getItem("system-user") || "{}");
const data = reactive({});
const selectedCourseId = ref("");

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

const courseFinalScores = computed(() => Array.isArray(data.courseFinalScores) ? data.courseFinalScores : []);

const selectedCourse = computed(() => {
  if (!courseFinalScores.value.length) return null;
  return courseFinalScores.value.find((item) => String(item.courseId) === String(selectedCourseId.value)) || courseFinalScores.value[0];
});

const selectedCourseScore = computed(() => {
  if (!selectedCourse.value) return 0;
  return selectedCourse.value.finalScore ?? 0;
});

const selectedCourseHint = computed(() => {
  if (!selectedCourse.value) return "暂无课程成绩";
  if (selectedCourse.value.hasFinalScore === false) return "该课程暂无可展示的最终成绩";
  return "期中 30% + 期末 70%";
});

watch(courseFinalScores, (list) => {
  if (!list.length) {
    selectedCourseId.value = "";
    return;
  }
  const exists = list.some((item) => String(item.courseId) === String(selectedCourseId.value));
  if (!exists) {
    selectedCourseId.value = String(list[0].courseId);
  }
}, { immediate: true });

onMounted(async () => {
  const response = await request.get("/api/student/overview", { params: { studentId: user.userId || user.id } });
  Object.assign(data, response.data || {});
});
</script>

<style scoped>
.metric h2 { margin: 8px 0; color: #303133; }
.metric { color: #fff; border-radius: 12px; }
.metric :deep(.el-select .el-input__wrapper) {
  background: rgba(255, 255, 255, 0.95);
}
.metric-green { background: linear-gradient(135deg, #43e97b, #38f9d7); }
.metric-yellow { background: linear-gradient(135deg, #ffd86f, #fc6262); }
.metric-orange { background: linear-gradient(135deg, #ff9a44, #fc6076); }
.metric-red { background: linear-gradient(135deg, #f85032, #e73827); }
</style>
