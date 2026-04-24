<template>
  <div>
    <h3 style="margin: 0 0 10px 0; color: #303133">考试管理</h3>

    <div class="card">
      <el-select v-model="form.courseId" placeholder="选择课程" style="width: 220px; margin-right: 10px" @change="handleCourseChange">
        <el-option
          v-for="course in courses"
          :key="course.id"
          :label="`${course.courseName}（课程编号：${course.id}）`"
          :value="String(course.id)"
        />
      </el-select>

      <el-select v-model="form.examType" placeholder="考试类型" style="width: 180px; margin-right: 10px" @change="clearPicked">
        <el-option label="期中考试（30%）" value="MIDTERM" />
        <el-option label="期末考试（70%）" value="FINAL" />
      </el-select>

      <el-date-picker
        v-model="form.examTime"
        type="datetime"
        value-format="YYYY-MM-DDTHH:mm:ss"
        placeholder="考试时间"
        style="width: 220px; margin-right: 10px"
      />

      <el-button @click="showBank = true">从题库选题</el-button>
      <span style="margin: 0 10px; color: #606266">当前总分：{{ totalScore }} / 100</span>
      <el-button type="primary" :disabled="!canCreateExam" @click="create">发布考试</el-button>
      <el-button @click="load" style="margin-left: 8px">刷新列表</el-button>
    </div>

    <div class="card" style="margin-top: 10px; color: #606266">
      当前规则：每门课只允许发布 1 场期中考试和 1 场期末考试，期中考试和期末考试都必须选够 100 分，最终成绩按期中 30% + 期末 70% 计算。
    </div>

    <div class="card" style="margin-top: 10px" v-for="item in pagedList" :key="item.exam.id">
      <h4 style="display: flex; align-items: center; gap: 8px; flex-wrap: wrap">
        <span>{{ item.exam.examName }}</span>
        <el-tag>{{ examTypeLabel(item.exam.examType) }}</el-tag>
        <el-tag type="warning">{{ weightText(item.exam.examType) }}</el-tag>
        <span style="color: #909399">考试编号：{{ item.exam.id }}</span>
        <span style="color: #909399">发布时间：{{ formatDateTime(item.exam.createTime) }}</span>
        <el-button size="small" type="danger" plain @click="undo(item.exam.id)">撤销发布</el-button>
      </h4>

      <el-table :data="item.details || []" empty-text="暂无考试明细">
        <el-table-column label="学生编号" prop="qualification.studentId" />
        <el-table-column label="考试资格">
          <template #default="scope">
            {{ yesNoLabel(scope.row.qualification?.isQualified) }}
          </template>
        </el-table-column>
        <el-table-column label="资格说明" prop="qualification.reason" />
        <el-table-column label="当前得分" prop="record.score" />
        <el-table-column label="判分说明" min-width="260">
          <template #default="scope">
            {{ scoreSummary(scope.row.record?.remark) }}
          </template>
        </el-table-column>
        <el-table-column label="详情" width="110">
          <template #default="scope">
            <el-button size="small" :disabled="!scope.row.record" @click="goDetail(scope.row)">查看详情</el-button>
          </template>
        </el-table-column>
        <el-table-column label="主观题批改" min-width="320">
          <template #default="scope">
            <template v-if="shouldShowGradeForm(scope.row)">
              <el-input v-model="scope.row._subjectiveScore" placeholder="主观题分数" style="width: 110px; margin-right: 6px" />
              <el-input v-model="scope.row._comment" placeholder="教师评语" style="width: 150px; margin-right: 6px" />
              <el-button
                size="small"
                type="primary"
                :disabled="!scope.row.record"
                @click="grade(scope.row)"
              >
                提交评分
              </el-button>
            </template>
            <template v-else-if="shouldShowFinishedText(scope.row)">
              <span style="color: #67c23a">已完成主观题判分，可继续查看详情</span>
            </template>
            <template v-else>
              <span></span>
            </template>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="showBank" title="题库选题（客观题 2 分，主观题 10 分）" width="760px">
      <el-table :data="questionBank" height="420">
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
        :page-sizes="[1, 5, 10, 20]"
        :total="list.length"
        @current-change="onPageChange"
        @size-change="onSizeChange"
      />
    </div>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { useRouter } from "vue-router";
import request from "@/utils/request";
import { getQuestionBank } from "@/utils/questionBank";
import { examTypeLabel, yesNoLabel } from "@/utils/display";

const router = useRouter();
const courses = ref([]);
const list = ref([]);
const showBank = ref(false);
const picked = ref([]);
const page = reactive({ pageNum: 1, pageSize: 1 });

const buildDefaultExamTime = () => {
  const date = new Date();
  date.setHours(date.getHours() + 1);
  const pad = (value) => String(value).padStart(2, "0");
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
};

const form = reactive({
  courseId: "",
  examType: "MIDTERM",
  examTime: buildDefaultExamTime(),
  totalScore: 0,
  durationMinutes: 120,
  description: ""
});

const questionBank = computed(() => getQuestionBank(form.courseId));
const totalScore = computed(() => picked.value.reduce((sum, item) => sum + item.score, 0));
const canCreateExam = computed(() => !!form.courseId && totalScore.value === 100);
const pagedList = computed(() => {
  const from = (page.pageNum - 1) * page.pageSize;
  return list.value.slice(from, from + page.pageSize);
});

const weightText = (type) => (type === "MIDTERM" ? "计入最终成绩 30%" : "计入最终成绩 70%");
const isPicked = (id) => picked.value.some((item) => item.id === id);

const parseRemarkPayload = (raw = "") => {
  if (!raw || typeof raw !== "string" || !raw.trim().startsWith("{")) return {};
  try {
    return JSON.parse(raw);
  } catch {
    return {};
  }
};

const subjectiveScoreFromPayload = (payload) => {
  const scores = Array.isArray(payload.subjectiveScores) ? payload.subjectiveScores : [];
  return scores.reduce((sum, item) => sum + Number(item || 0), 0);
};

const hasFinishedSubjectiveGrade = (row) => {
  const payload = parseRemarkPayload(row.record?.remark);
  if (Object.prototype.hasOwnProperty.call(payload, "subjectiveScores")) return true;
  return typeof payload.message === "string" && payload.message.includes("主观题教师判分");
};

const hasSubmissionEvidence = (row) => {
  const record = row.record;
  if (!record) return false;
  if (record.status === "FINISHED") return true;
  if (record.submitTime) return true;
  if (record.score !== null && record.score !== undefined) return true;
  return typeof record.remark === "string" && record.remark.trim().startsWith("{");
};

const hasJoinedExam = (row) => {
  const status = row.record?.status;
  return !!row.record && (status !== "NOT_JOINED" && status !== "ABSENT" || hasSubmissionEvidence(row));
};

const shouldShowGradeForm = (row) => {
  if (!hasJoinedExam(row)) return false;
  return !hasFinishedSubjectiveGrade(row);
};

const shouldShowFinishedText = (row) => {
  return hasJoinedExam(row) && hasFinishedSubjectiveGrade(row);
};

const scoreSummary = (raw = "") => {
  if (!raw) return "";
  if (typeof raw === "string" && !raw.trim().startsWith("{")) {
    if (raw.includes("不能参加考试") || raw.includes("不具备考试资格") || raw.includes("成绩无效") || raw.includes("缺考")) {
      return "";
    }
    return raw;
  }
  const payload = parseRemarkPayload(raw);
  if (!payload || Object.keys(payload).length === 0) return "";
  const objectiveScore = Number(payload.objectiveScore || 0);
  const subjectiveScore = subjectiveScoreFromPayload(payload);
  const comment = payload.message || "";
  return `客观题得分：${objectiveScore}；主观题得分：${subjectiveScore}${comment ? `；${comment}` : ""}`;
};

const pick = (question) => {
  if (!isPicked(question.id) && totalScore.value + question.score <= 100) {
    picked.value.push(question);
  }
};

const loadCourses = async () => {
  const response = await request.get("/api/teacher/courses");
  courses.value = response.data || [];
  if (!form.courseId && courses.value.length) {
    form.courseId = String(courses.value[0].id);
  }
};

const create = async () => {
  if (!form.courseId) {
    ElMessage.warning("请先选择课程");
    return;
  }
  if (totalScore.value !== 100) {
    ElMessage.warning(`${examTypeLabel(form.examType)}必须选够 100 分后才能发布`);
    return;
  }
  const examName = `${selectedCourseName()}${form.examType === "MIDTERM" ? "期中考试" : "期末考试"}`;
  form.description = picked.value.map((question, index) => `${index + 1}. ${question.title}（${question.score}分）`).join("\n");
  form.totalScore = totalScore.value || 100;
  const response = await request.post("/api/exam/create", {
    courseId: Number(form.courseId),
    examType: form.examType,
    examName,
    examTime: form.examTime,
    durationMinutes: form.durationMinutes,
    totalScore: form.totalScore,
    description: form.description
  });
  if (response.code !== "200") {
    ElMessage.error(response.msg || "考试发布失败");
    return;
  }
  ElMessage.success("考试发布成功");
  const createdExamType = form.examType;
  form.examTime = buildDefaultExamTime();
  picked.value = [];
  await load();
  focusExamType(createdExamType);
};

const clearPicked = () => {
  picked.value = [];
};

const handleCourseChange = async () => {
  clearPicked();
  await load();
};

const load = async () => {
  if (!form.courseId) return;
  const response = await request.get("/api/teacher/exam-manage", { params: { courseId: form.courseId } });
  list.value = response.data || [];
  const maxPage = Math.max(1, Math.ceil(list.value.length / page.pageSize));
  if (page.pageNum > maxPage) {
    page.pageNum = maxPage;
  }
};

const focusExamType = (examType) => {
  const index = list.value.findIndex((item) => item.exam?.examType === examType);
  if (index >= 0) {
    page.pageNum = Math.floor(index / page.pageSize) + 1;
  }
};

const undo = async (examId) => {
  await ElMessageBox.confirm("撤销后会删除该考试及其记录，确定继续吗？", "撤销确认", { type: "warning" });
  await request.post("/api/exam/undo", null, { params: { examId } });
  await load();
};

const grade = async (row) => {
  await request.post("/api/teacher/exam-grade", null, {
    params: {
      recordId: row.record.id,
      subjectiveScore: row._subjectiveScore || 0,
      comment: row._comment || ""
    }
  });
  await load();
};

const goDetail = (row) => {
  router.push({ path: "/manager/teacher-grade-detail", query: { type: "exam", recordId: row.record.id } });
};

const selectedCourseName = () => {
  const course = courses.value.find((item) => String(item.id) === String(form.courseId));
  return course?.courseName || "课程";
};

const formatDateTime = (value) => {
  if (!value) return "-";
  return String(value).replace("T", " ").slice(0, 19);
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
