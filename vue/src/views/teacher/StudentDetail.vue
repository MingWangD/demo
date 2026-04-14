<template>
  <div>
    <h3 style="margin:0 0 10px 0;color:#303133">学生学业详情</h3>
    <div class="card">
      <el-select v-model="courseId" placeholder="选择课程" style="width:220px;margin-right:8px" @change="loadStudentOptions">
        <el-option v-for="c in courses" :key="c.id" :label="`${c.courseName}(ID:${c.id})`" :value="String(c.id)" />
      </el-select>
      <el-select v-model="studentId" placeholder="选择学生" style="width:220px;margin-right:8px" @change="loadAll">
        <el-option v-for="s in students" :key="s.studentId" :label="`${s.studentName}(ID:${s.studentId})`" :value="String(s.studentId)" />
      </el-select>
      <el-button type="primary" @click="loadAll">查询</el-button>
    </div>

    <div class="card" style="margin-top:10px" v-if="data.student">
      <div style="display:flex;justify-content:space-between;align-items:center">
        <h4 style="margin:0">{{ data.student.name }} 的学业画像</h4>
        <el-tag type="danger">风险：{{ data.latestRisk?.riskLevel || 'LOW' }}</el-tag>
      </div>
      <el-row :gutter="12" style="margin-top:10px">
        <el-col :span="6"><div class="card">出勤次数<br><b>{{ data.attendanceCount || 0 }}</b></div></el-col>
        <el-col :span="6"><div class="card">作业提交率<br><b>{{ Number((data.homeworkSubmitRate || 0) * 100).toFixed(2) }}%</b></div></el-col>
        <el-col :span="6"><div class="card">作业均分<br><b>{{ data.homeworkAvgScore || 0 }}</b></div></el-col>
        <el-col :span="6"><div class="card">考试均分<br><b>{{ data.examAvgScore || 0 }}</b></div></el-col>
      </el-row>
      <div class="card" style="margin-top:10px">
        GPA：<b>{{ data.academic?.gpa || 0 }}</b>
        <el-tag style="margin-left:8px">{{ data.academic?.gpaColor || 'GREEN' }}</el-tag>
      </div>
      <div class="card" style="margin-top:10px">
        <div style="font-weight:600;margin-bottom:6px">最新风险原因</div>
        <div style="color:#606266">{{ data.latestRisk?.mainReason || '学习状态稳定' }}</div>
      </div>
    </div>

    <div class="card" style="margin-top:10px" v-if="courseId && studentId">
      <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:10px">
        <h4 style="margin:0">教师维护出勤</h4>
        <el-button type="primary" @click="openCreate">新增出勤</el-button>
      </div>
      <el-table :data="attendanceList">
        <el-table-column prop="attendanceTime" label="出勤时间" min-width="180" />
        <el-table-column prop="weekNo" label="周次" width="90" />
        <el-table-column prop="attendanceType" label="记录方式" width="120" />
        <el-table-column prop="remark" label="备注" min-width="180" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button link type="primary" @click="openEdit(scope.row)">编辑</el-button>
            <el-button link type="danger" @click="removeAttendance(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="attendanceDialogVisible" title="维护出勤记录" width="520px">
      <el-form :model="attendanceForm" label-width="90px">
        <el-form-item label="出勤时间">
          <el-date-picker
            v-model="attendanceForm.attendanceTime"
            type="datetime"
            placeholder="选择出勤时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width:100%"
          />
        </el-form-item>
        <el-form-item label="周次">
          <el-input-number v-model="attendanceForm.weekNo" :min="1" :max="30" style="width:100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="attendanceForm.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="attendanceDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAttendance">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import request from "@/utils/request";
import { ElMessage, ElMessageBox } from "element-plus";

const studentId = ref("");
const courseId = ref("");
const courses = ref([]);
const students = ref([]);
const attendanceList = ref([]);
const attendanceDialogVisible = ref(false);
const data = reactive({});
const attendanceForm = reactive({
  id: null,
  attendanceTime: "",
  weekNo: 1,
  remark: "",
  attendanceType: "MANUAL"
});

const resetAttendanceForm = () => {
  attendanceForm.id = null;
  attendanceForm.attendanceTime = "";
  attendanceForm.weekNo = 1;
  attendanceForm.remark = "";
  attendanceForm.attendanceType = "MANUAL";
};

const loadCourses = async () => {
  const r = await request.get("/api/teacher/courses");
  courses.value = r.data || [];
  if (courses.value.length && !courseId.value) courseId.value = String(courses.value[0].id);
};

const loadStudentOptions = async () => {
  if (!courseId.value) return;
  const r = await request.get("/api/teacher/course-students", { params: { courseId: courseId.value } });
  students.value = r.data || [];
  if (!studentId.value && students.value.length) studentId.value = String(students.value[0].studentId);
  await loadAll();
};

const loadDetail = async () => {
  if (!studentId.value) return;
  const r = await request.get("/api/teacher/student-detail", {
    params: { studentId: studentId.value, courseId: courseId.value || undefined }
  });
  Object.assign(data, r.data || {});
};

const loadAttendance = async () => {
  if (!courseId.value || !studentId.value) return;
  const r = await request.get("/api/teacher/attendance", {
    params: { courseId: courseId.value, studentId: studentId.value }
  });
  attendanceList.value = r.data || [];
};

const loadAll = async () => {
  await Promise.all([loadDetail(), loadAttendance()]);
};

const openCreate = () => {
  resetAttendanceForm();
  attendanceDialogVisible.value = true;
};

const openEdit = (row) => {
  attendanceForm.id = row.id;
  attendanceForm.attendanceTime = row.attendanceTime;
  attendanceForm.weekNo = row.weekNo || 1;
  attendanceForm.remark = row.remark || "";
  attendanceForm.attendanceType = row.attendanceType || "MANUAL";
  attendanceDialogVisible.value = true;
};

const saveAttendance = async () => {
  if (!courseId.value || !studentId.value) {
    ElMessage.warning("请先选择课程和学生");
    return;
  }
  const payload = {
    id: attendanceForm.id || undefined,
    studentId: Number(studentId.value),
    courseId: Number(courseId.value),
    attendanceType: "MANUAL",
    attendanceTime: attendanceForm.attendanceTime,
    weekNo: attendanceForm.weekNo,
    remark: attendanceForm.remark
  };
  await request.post("/api/teacher/attendance/save", payload);
  ElMessage.success("出勤记录已保存");
  attendanceDialogVisible.value = false;
  await loadAll();
};

const removeAttendance = async (id) => {
  await ElMessageBox.confirm("确认删除这条出勤记录吗？", "删除确认", { type: "warning" });
  await request.delete(`/api/teacher/attendance/${id}`);
  ElMessage.success("出勤记录已删除");
  await loadAll();
};

loadCourses().then(loadStudentOptions);
</script>
