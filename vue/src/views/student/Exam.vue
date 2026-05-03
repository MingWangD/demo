<template>
  <div>
    <el-select v-model="courseId" placeholder="切换当前课程" style="width: 240px" @change="load">
      <el-option v-for="course in courses" :key="course.id" :label="course.courseName" :value="course.id" />
    </el-select>

    <div class="card" style="margin-top: 10px; color: #606266">
      当前规则：每门课只有期中考试和期末考试两类，最终成绩按期中 30% + 期末 70% 计算，最终成绩达到 60 分视为及格。
    </div>

    <el-table :data="list" style="margin-top: 10px">
      <el-table-column prop="examName" label="考试名称" />
      <el-table-column label="考试类型">
        <template #default="scope">
          {{ examTypeLabel(scope.row.examType) }}
        </template>
      </el-table-column>
      <el-table-column label="成绩权重">
        <template #default="scope">
          {{ weightText(scope.row.examType) }}
        </template>
      </el-table-column>
      <el-table-column label="考试资格">
        <template #default="scope">
          {{ yesNoLabel(scope.row.isQualified) }}
        </template>
      </el-table-column>
      <el-table-column prop="qualificationReason" label="资格说明" />
      <el-table-column label="考试状态">
        <template #default="scope">
          {{ examStatusLabel(scope.row.status) }}
        </template>
      </el-table-column>
      <el-table-column prop="score" label="当前得分" />
      <el-table-column prop="autoJudgeRemark" label="判分说明" />
      <el-table-column label="参加考试">
        <template #default="scope">
          <el-button :disabled="!scope.row.isQualified || scope.row.status === 'FINISHED'" @click="openSubmit(scope.row)">完成考试</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showDialog" title="考试作答" width="760px">
      <div v-if="current">
        <h4>{{ current.examName }}</h4>
        <div style="margin-bottom: 10px; color: #606266">
          {{ examTypeLabel(current.examType) }}，成绩权重 {{ weightText(current.examType) }}
        </div>

        <div v-for="(question, index) in objectiveQuestions" :key="index" style="margin-bottom: 12px">
          <div>{{ index + 1 }}. {{ question }}</div>
          <el-radio-group v-model="objectiveAnswers[index]">
            <el-radio
              v-for="option in objectiveOptions[index] || []"
              :key="option.key"
              :value="option.key"
            >
              {{ option.key }}. {{ option.text }}
            </el-radio>
          </el-radio-group>
        </div>

        <div style="margin-top: 10px">
          <div style="font-weight: 600">主观题作答</div>
          <div v-for="(question, index) in subjectiveQuestions" :key="`subject-${index}`" style="margin-top: 8px">
            <div>{{ index + 1 }}. {{ question }}</div>
            <el-input v-model="subjectiveAnswers[index]" type="textarea" :rows="2" placeholder="请输入主观题答案" />
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="submit">提交考试</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import request from "@/utils/request";
import { examStatusLabel, examTypeLabel, yesNoLabel } from "@/utils/display";

const user = JSON.parse(localStorage.getItem("system-user") || "{}");
const courses = ref([]);
const courseId = ref();
const list = ref([]);
const showDialog = ref(false);
const current = ref(null);
const objectiveQuestions = ref([]);
const objectiveOptions = ref([]);
const objectiveAnswers = ref([]);
const subjectiveQuestions = ref([]);
const subjectiveAnswers = ref([]);

const optionPool = [
  "时间复杂度更优",
  "空间开销更小",
  "鲁棒性更强",
  "可维护性更高",
  "便于并行处理",
  "更符合题干约束",
  "边界条件更完整",
  "结果更稳定",
  "实现成本更低",
  "可解释性更好",
  "与业务目标一致",
  "满足数据规模要求"
];

const normalize = (content = "") => String(content).replace(/\\n/g, "\n");
const weightText = (type) => (type === "MIDTERM" ? "30%" : "70%");

const pickDistinctOptions = (seed) => {
  const used = new Set();
  const options = [];
  let cursor = (seed * 7) % optionPool.length;
  while (options.length < 4) {
    const text = optionPool[cursor % optionPool.length];
    if (!used.has(text)) {
      used.add(text);
      options.push(text);
    }
    cursor += 3;
  }
  return options;
};

const buildObjectiveOptions = (questions = []) => questions.map((_, index) => {
  const texts = pickDistinctOptions(index + 1);
  return ["A", "B", "C", "D"].map((key, idx) => ({ key, text: texts[idx] }));
});

const loadCourses = async () => {
  const response = await request.get("/api/student/courses", { params: { studentId: user.userId || user.id } });
  courses.value = response.data || [];
  if (courses.value.length) {
    courseId.value = courses.value[0].id;
    await load();
  }
};

const load = async () => {
  const response = await request.get("/api/student/exams", {
    params: { studentId: user.userId || user.id, courseId: courseId.value }
  });
  list.value = response.data || [];
};

const openSubmit = (row) => {
  current.value = row;
  const lines = normalize(row.description || "").split("\n").map((item) => item.trim()).filter(Boolean);
  objectiveQuestions.value = lines.filter((item) => item.includes("（2分）") || item.includes("(2)") || item.includes("(2分)"));
  objectiveOptions.value = buildObjectiveOptions(objectiveQuestions.value);
  subjectiveQuestions.value = lines.filter((item) => item.includes("（10分）") || item.includes("(10)") || item.includes("(10分)"));
  objectiveAnswers.value = Array(objectiveQuestions.value.length).fill("");
  subjectiveAnswers.value = Array(subjectiveQuestions.value.length).fill("");
  showDialog.value = true;
};

const submit = async () => {
  await ElMessageBox.confirm("考试提交后不可修改，确定提交吗？", "提交确认", { type: "warning" });
  const objectiveAnswered = objectiveAnswers.value.filter(Boolean).length;
  const response = await request.post("/api/exam/submit", {
    examId: current.value.examId,
    studentId: user.userId || user.id,
    score: 80,
    answerContent: JSON.stringify({
      objectiveAnswered,
      objectiveDetail: objectiveAnswers.value,
      subjectiveAnswers: subjectiveAnswers.value,
      objectiveLocked: true
    })
  });
  if (response.code !== "200") {
    ElMessage.error(response.msg || "考试提交失败");
    return;
  }
  ElMessage.success("考试提交成功");
  showDialog.value = false;
  await load();
};

onMounted(loadCourses);
</script>
