<template>
  <div>
    <h3 style="margin: 0 0 10px 0; color: #303133">高风险学生筛选</h3>
    <div class="card">
      <el-select v-model="filters.courseId" placeholder="选择课程（可选）" style="width: 220px; margin-right: 8px">
        <el-option label="全部课程" value="" />
        <el-option v-for="course in courses" :key="course.id" :label="`${course.courseName}（课程编号：${course.id}）`" :value="String(course.id)" />
      </el-select>
      <el-select v-model="filters.riskLevel" placeholder="风险等级" style="width: 140px; margin-right: 8px">
        <el-option label="全部" value="" />
        <el-option label="高风险" value="HIGH" />
        <el-option label="中风险" value="MEDIUM" />
        <el-option label="低风险" value="LOW" />
      </el-select>
      <el-button type="primary" @click="onFilter">筛选</el-button>
    </div>

    <el-table :data="list" empty-text="暂无数据，请调整筛选条件" style="margin-top: 10px">
      <el-table-column label="学生姓名" prop="student.name" />
      <el-table-column label="课程">
        <template #default="scope">
          {{ scope.row.courseName || `课程编号：${scope.row.risk?.courseId ?? "-"}` }}
        </template>
      </el-table-column>
      <el-table-column label="风险概率" prop="risk.riskProbability" />
      <el-table-column label="风险等级">
        <template #default="scope">{{ riskLevelLabel(scope.row.risk?.riskLevel) }}</template>
      </el-table-column>
      <el-table-column label="GPA">
        <template #default="scope">{{ scope.row.academic?.gpa ?? 0 }}</template>
      </el-table-column>
      <el-table-column label="原因" prop="risk.mainReason" />
    </el-table>

    <div style="display: flex; justify-content: flex-end; margin-top: 12px">
      <el-pagination
        background
        layout="total, prev, pager, next, sizes"
        :current-page="page.pageNum"
        :page-size="page.pageSize"
        :page-sizes="[5, 10, 20, 50]"
        :total="page.total"
        @current-change="onPageChange"
        @size-change="onSizeChange"
      />
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import request from "@/utils/request";
import { riskLevelLabel } from "@/utils/display";

const list = ref([]);
const courses = ref([]);
const filters = reactive({ courseId: "", riskLevel: "HIGH" });
const page = reactive({ pageNum: 1, pageSize: 10, total: 0 });

const onFilter = () => {
  page.pageNum = 1;
  load();
};

const load = async () => {
  const response = await request.get("/api/teacher/high-risk", { params: { ...filters, ...page } });
  const data = response.data || {};
  list.value = data.list || [];
  page.total = data.total || 0;
};

const onPageChange = (value) => {
  page.pageNum = value;
  load();
};

const onSizeChange = (value) => {
  page.pageSize = value;
  page.pageNum = 1;
  load();
};

const loadCourses = async () => {
  const response = await request.get("/api/teacher/courses");
  courses.value = response.data || [];
};

onMounted(async () => {
  await Promise.all([loadCourses(), load()]);
});
</script>
