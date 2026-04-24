<template>
  <div>
    <div class="card">
      高风险 {{ data.highRiskCount || 0 }} / 中风险 {{ data.mediumRiskCount || 0 }} / 低风险 {{ data.lowRiskCount || 0 }}
    </div>

    <div style="display: flex; gap: 10px; margin-top: 10px">
      <div class="card" style="flex: 1">
        <base-pie-chart title="风险分布" :data="riskPie" />
      </div>
      <div class="card" style="flex: 1">
        <base-pie-chart title="GPA 分布" :data="gpaPie" />
      </div>
    </div>

    <div class="card" style="margin-top: 10px">
      <base-line-chart
        title="班级作业完成情况趋势"
        :x-data="trendX"
        :y-data="trendY"
        unit="%"
        x-name="日期"
        y-name="作业完成率"
      />
    </div>

    <div class="card" style="margin-top: 10px">
      <h4>高风险学生列表</h4>
      <el-table :data="highRiskPaged" empty-text="暂无高风险学生">
        <el-table-column prop="studentId" label="学生编号" />
        <el-table-column label="风险概率">
          <template #default="{ row }">
            {{ formatProbability(row.riskProbability) }}
          </template>
        </el-table-column>
        <el-table-column prop="mainReason" label="主要原因" min-width="280" />
      </el-table>
      <div style="display: flex; justify-content: flex-end; margin-top: 10px">
        <el-pagination
          background
          layout="total, prev, pager, next"
          :current-page="highRiskPage.pageNum"
          :page-size="highRiskPage.pageSize"
          :total="(data.highRiskStudents || []).length"
          @current-change="onHighRiskPageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive } from "vue";
import request from "@/utils/request";
import BasePieChart from "@/components/charts/BasePieChart.vue";
import BaseLineChart from "@/components/charts/BaseLineChart.vue";
import { gpaColorLabel, riskLevelLabel } from "@/utils/display";

const data = reactive({
  highRiskCount: 0,
  mediumRiskCount: 0,
  lowRiskCount: 0,
  gpaColor: {},
  highRiskStudents: [],
  homeworkTrend: []
});
const highRiskPage = reactive({ pageNum: 1, pageSize: 5 });

const riskPie = computed(() => [
  { name: riskLevelLabel("HIGH"), value: data.highRiskCount || 0 },
  { name: riskLevelLabel("MEDIUM"), value: data.mediumRiskCount || 0 },
  { name: riskLevelLabel("LOW"), value: data.lowRiskCount || 0 }
]);

const gpaPie = computed(() =>
  Object.keys(data.gpaColor || {}).map((key) => ({
    name: gpaColorLabel(key),
    value: data.gpaColor[key]
  }))
);

const trendX = computed(() =>
  (data.homeworkTrend || []).map((item) => {
    const raw = item.label || "-";
    if (!raw.includes("-")) return raw;
    const parts = raw.split("-");
    return `${Number(parts[1])}.${Number(parts[2])}`;
  })
);

const trendY = computed(() =>
  (data.homeworkTrend || []).map((item) => Number((Number(item.completionRate || 0) * 100).toFixed(2)))
);

const highRiskPaged = computed(() => {
  const all = data.highRiskStudents || [];
  const from = (highRiskPage.pageNum - 1) * highRiskPage.pageSize;
  return all.slice(from, from + highRiskPage.pageSize);
});

const onHighRiskPageChange = (value) => {
  highRiskPage.pageNum = value;
};

const formatProbability = (value) => `${(Number(value || 0) * 100).toFixed(2)}%`;

onMounted(async () => {
  const response = await request.get("/api/dashboard/teacher");
  Object.assign(data, response.data || {});
});
</script>
