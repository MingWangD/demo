<template>
  <div>
    <h3 style="margin: 0 0 10px 0; color: #303133">作业管理</h3>
    <div class="card">
      <el-select v-model="form.courseId" placeholder="选择课程" style="width: 180px; margin-right: 10px" @change="load">
        <el-option v-for="c in courses" :key="c.id" :label="`${c.courseName}（课程编号：${c.id}）`" :value="String(c.id)" />
      </el-select>
      <el-input v-model="form.title" placeholder="作业标题" style="width: 180px; margin-right: 10px" />
      <el-date-picker
        v-model="form.deadline"
        type="datetime"
        value-format="YYYY-MM-DDTHH:mm:ss"
        placeholder="选择截止时间"
        style="width: 220px; margin-right: 10px"
      />
      <el-button @click="showBank = true">从题库选题</el-button>
      <span style="margin: 0 10px; color: #606266">当前总分：{{ totalScore }} / 100</span>
      <el-button type="primary" @click="create">发布作业</el-button>
      <el-button @click="load" style="margin-left: 8px">刷新列表</el-button>
    </div>

    <div class="card" style="margin-top: 10px" v-for="item in pagedList" :key="item.homework.id">
      <h4>
        {{ item.homework.title }}（作业编号：{{ item.homework.id }}）
        <span style="margin-left: 10px; color: #909399">发布时间：{{ item.publishedAt || formatDateTime(item.homework.createTime) }}</span>
        <span style="margin-left: 10px; color: #909399">截止时间：{{ item.deadlineAt || formatDateTime(item.homework.deadline) }}</span>
        <el-button size="small" type="danger" plain style="margin-left: 10px" @click="undo(item.homework.id)">撤销发布</el-button>
      </h4>
      <el-table :data="item.submissions || []" empty-text="暂无提交记录">
        <el-table-column prop="studentId" label="学生编号" />
        <el-table-column label="状态">
          <template #default="scope">{{ homeworkStatusLabel(scope.row.status) }}</template>
        </el-table-column>
        <el-table-column prop="score" label="分数" />
        <el-table-column label="学生作答">
          <template #default="scope">
            {{ shortAnswer(scope.row.submitContent) }}
          </template>
        </el-table-column>
        <el-table-column prop="teacherComment" label="教师评语" />
        <el-table-column label="详情" width="110">
          <template #default="scope">
            <el-button size="small" @click="goDetail(scope.row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="showBank" title="题库选题（客观题 2 分，主观题 10 分）" width="760px">
      <el-alert
        type="info"
        :closable="false"
        show-icon
        title="每个科目题库不少于 50 题；总分达到 100 后将禁止继续添加。"
      />
      <el-table :data="questionBank" height="420" style="margin-top: 10px">
        <el-table-column prop="id" label="题号" width="150" />
        <el-table-column prop="type" label="题型" width="120" />
        <el-table-column prop="score" label="分值" width="80" />
        <el-table-column prop="title" label="题目" />
        <el-table-column label="操作" width="100">
          <template #default="scope">
            <el-button
              size="small"
              type="primary"
              :disabled="isPicked(scope.row.id) || totalScore + scope.row.score > 100"
              @click="pick(scope.row)"
            >
              加入
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 10px; color: #606266">已选 {{ picked.length }} 题，总分 {{ totalScore }}</div>
      <template #footer>
        <el-button @click="showBank = false">完成</el-button>
      </template>
    </el-dialog>

    <div style="display: flex; justify-content: flex-end; margin-top: 12px">
      <el-pagination
        background
        layout="total, prev, pager, next, sizes"
        :current-page="page.pageNum"
        :page-size="page.pageSize"
        :page-sizes="[1,5,10,20]"
        :total="list.length"
        @current-change="onPageChange"
        @size-change="onSizeChange"
      />
    </div>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from "vue";
import request from "@/utils/request";
import { getQuestionBank } from "@/utils/questionBank";
import { useRouter } from "vue-router";
import { homeworkStatusLabel } from "@/utils/display";

const router = useRouter();
const courses = ref([]);
const list = ref([]);

const buildDefaultDeadline = () => {
  const date = new Date();
  date.setDate(date.getDate() + 7);
  const pad = (value) => String(value).padStart(2, "0");
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
};

const form = reactive({
  courseId: "",
  title: "新作业",
  content: "",
  deadline: buildDefaultDeadline(),
  totalScore: 0
});
const showBank = ref(false);
const picked = ref([]);
const page = reactive({ pageNum: 1, pageSize: 1 });

const questionBank = computed(() => getQuestionBank(form.courseId));
const totalScore = computed(() => picked.value.reduce((sum, question) => sum + question.score, 0));
const pagedList = computed(() => {
  const from = (page.pageNum - 1) * page.pageSize;
  return list.value.slice(from, from + page.pageSize);
});

const isPicked = (id) => picked.value.some((item) => item.id === id);

const pick = (question) => {
  if (!isPicked(question.id) && totalScore.value + question.score <= 100) {
    picked.value.push(question);
  }
};

const loadCourses = async () => {
  const res = await request.get("/api/teacher/courses");
  courses.value = res.data || [];
  if (!form.courseId && courses.value.length) {
    form.courseId = String(courses.value[0].id);
  }
};

const create = async () => {
  form.content = picked.value.map((question, index) => `${index + 1}. ${question.title}（${question.score}分）`).join("\n");
  form.totalScore = totalScore.value || 100;
  await request.post("/api/homework/create", { ...form, courseId: Number(form.courseId) });
  form.deadline = buildDefaultDeadline();
  picked.value = [];
  await load();
};

const load = async () => {
  const res = await request.get("/api/teacher/homework-manage", { params: { courseId: form.courseId } });
  list.value = res.data || [];
  const maxPage = Math.max(1, Math.ceil(list.value.length / page.pageSize));
  if (page.pageNum > maxPage) {
    page.pageNum = maxPage;
  }
};

const undo = async (homeworkId) => {
  await request.post("/api/homework/undo", null, { params: { homeworkId } });
  await load();
};

const shortAnswer = (raw = "") => {
  try {
    const payload = JSON.parse(raw || "{}");
    const objective = (payload.objectiveDetail || []).join("、");
    const subjective = (payload.subjectiveAnswers || []).join(" | ");
    return `客观题：${objective || "-"}；主观题：${subjective || "-"}`;
  } catch {
    return String(raw || "").slice(0, 30);
  }
};

const formatDateTime = (value) => {
  if (!value) return "-";
  return String(value).replace("T", " ").slice(0, 19);
};

const goDetail = (row) => {
  router.push({ path: "/manager/teacher-grade-detail", query: { type: "homework", submissionId: row.id } });
};

const onPageChange = (value) => {
  page.pageNum = value;
};

const onSizeChange = (value) => {
  page.pageSize = value;
  page.pageNum = 1;
};

loadCourses().then(load);
</script>
