<template>
  <div>
    <h3 style="margin: 0 0 10px; color: #303133">干预记录</h3>
    <div class="card" style="display: flex; gap: 10px; align-items: center; flex-wrap: wrap">
      <el-select v-model="selectedWarningId" placeholder="选择需要干预的学生" style="width: 360px">
        <el-option v-for="o in options" :key="o.warningId" :label="`${o.studentName}｜${warningTypeLabel(o.warningType)}｜课程编号 ${o.courseId}`" :value="o.warningId" />
      </el-select>
      <el-select v-model="form.interventionType" style="width: 160px">
        <el-option label="谈话提醒" value="TALK" />
        <el-option label="学习帮扶" value="LEARNING_SUPPORT" />
        <el-option label="联系家长" value="PARENT_CONTACT" />
      </el-select>
      <el-input v-model="form.interventionContent" placeholder="请输入干预内容" style="width: 320px" />
      <el-input v-model="form.interventionResult" placeholder="请输入阶段结果" style="width: 260px" />
      <el-button type="primary" @click="add">新增干预</el-button>
    </div>
    <div class="card" style="margin-top: 10px">
      <el-timeline>
        <el-timeline-item v-for="item in list" :key="item.id" :timestamp="item.createTime" placement="top">
          <div style="font-weight: 600; margin-bottom: 4px">学生编号：{{ item.studentId }}，干预类型：{{ interventionTypeLabel(item.interventionType) }}</div>
          <div style="color: #606266">干预内容：{{ item.interventionContent }}</div>
          <div style="color: #909399">干预结果：{{ item.interventionResult || "待观察" }}</div>
          <el-button size="small" type="danger" plain style="margin-top: 6px" @click="undo(item.id)">撤销</el-button>
        </el-timeline-item>
      </el-timeline>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import request from "@/utils/request";
import { ElMessage } from "element-plus";
import { interventionTypeLabel } from "@/utils/display";

const list = ref([]);
const options = ref([]);
const selectedWarningId = ref(null);
const form = reactive({ interventionType: "TALK", interventionContent: "关注近期学习状态变化", interventionResult: "待观察" });

const warningTypeLabel = () => "风险预警";

const load = async () => {
  const r = await request.get("/api/intervention/list");
  list.value = r.data || [];
};

const loadOptions = async () => {
  const r = await request.get("/api/intervention/options");
  options.value = r.data || [];
};

const add = async () => {
  const selected = options.value.find(i => i.warningId === selectedWarningId.value);
  if (!selected) return ElMessage.warning("请先选择需要干预的学生");
  await request.post("/api/intervention/add", {
    studentId: selected.studentId,
    warningId: selected.warningId,
    courseId: selected.courseId,
    interventionType: form.interventionType,
    interventionContent: form.interventionContent,
    interventionResult: form.interventionResult
  });
  await Promise.all([load(), loadOptions()]);
};

const undo = async (interventionId) => {
  await request.post("/api/intervention/undo", null, { params: { interventionId } });
  load();
};

onMounted(async () => {
  await Promise.all([load(), loadOptions()]);
});
</script>
