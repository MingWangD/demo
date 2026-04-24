<template>
  <div>
    <h3 style="margin: 0 0 10px 0; color: #303133">学生学业详情</h3>

    <div class="card">
      <el-select v-model="courseId" placeholder="选择课程" style="width: 220px; margin-right: 8px" @change="loadStudentOptions">
        <el-option
          v-for="course in courses"
          :key="course.id"
          :label="`${course.courseName}（课程编号：${course.id}）`"
          :value="String(course.id)"
        />
      </el-select>

      <el-select v-model="studentId" placeholder="选择学生" style="width: 220px; margin-right: 8px" @change="loadDetail">
        <el-option
          v-for="student in students"
          :key="student.studentId"
          :label="`${student.studentName}（学生编号：${student.studentId}）`"
          :value="String(student.studentId)"
        />
      </el-select>

      <el-button type="primary" @click="loadDetail">查询</el-button>
    </div>

    <div class="card" style="margin-top: 10px" v-if="data.student">
      <div style="display: flex; justify-content: space-between; align-items: center">
        <h4 style="margin: 0">{{ data.student.name }} 的学业画像</h4>
        <el-tag type="danger">风险：{{ riskLevelLabel(data.latestRisk?.riskLevel || "LOW") }}</el-tag>
      </div>

      <el-row :gutter="12" style="margin-top: 10px">
        <el-col :span="6"><div class="metric">出勤次数<br><b>{{ data.attendanceCount || 0 }}</b></div></el-col>
        <el-col :span="6"><div class="metric">作业提交率<br><b>{{ Number((data.homeworkSubmitRate || 0) * 100).toFixed(2) }}%</b></div></el-col>
        <el-col :span="6"><div class="metric">作业均分<br><b>{{ data.homeworkAvgScore || 0 }}</b></div></el-col>
        <el-col :span="6"><div class="metric">课程最终成绩<br><b>{{ data.examAvgScore || 0 }}</b></div></el-col>
      </el-row>

      <div class="metric" style="margin-top: 10px">
        GPA：<b>{{ data.academic?.gpa || 0 }}</b>
        <el-tag style="margin-left: 8px">{{ gpaColorLabel(data.academic?.gpaColor || "GREEN") }}</el-tag>
      </div>

      <div class="metric" style="margin-top: 10px">
        <div style="font-weight: 600; margin-bottom: 6px">最新风险原因</div>
        <div style="color: #606266">{{ data.latestRisk?.mainReason || "当前学习状态稳定" }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import request from "@/utils/request";
import { gpaColorLabel, riskLevelLabel } from "@/utils/display";

const studentId = ref("");
const courseId = ref("");
const courses = ref([]);
const students = ref([]);
const data = reactive({});

const loadCourses = async () => {
  const response = await request.get("/api/teacher/courses");
  courses.value = response.data || [];
  if (courses.value.length && !courseId.value) {
    courseId.value = String(courses.value[0].id);
  }
};

const loadStudentOptions = async () => {
  if (!courseId.value) return;
  const response = await request.get("/api/teacher/course-students", { params: { courseId: courseId.value } });
  students.value = response.data || [];
  if (!studentId.value && students.value.length) {
    studentId.value = String(students.value[0].studentId);
  }
  await loadDetail();
};

const loadDetail = async () => {
  if (!studentId.value) return;
  const response = await request.get("/api/teacher/student-detail", {
    params: { studentId: studentId.value, courseId: courseId.value || undefined }
  });
  Object.assign(data, response.data || {});
};

loadCourses().then(loadStudentOptions);
</script>

<style scoped>
.card {
  background: #fff;
  border-radius: 8px;
  padding: 14px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.metric {
  background: #fff;
  border-radius: 8px;
  padding: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}
</style>
