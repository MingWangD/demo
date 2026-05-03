<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-badge">Academic Early Warning</div>
      <div class="login-title">学业预警系统</div>
      <div class="login-subtitle">围绕学习过程、风险分层和教师干预的教学支持平台</div>
      <el-form ref="formRef" :model="data.form" :rules="data.rules">
        <el-form-item prop="username">
          <el-input
            v-model="data.form.username"
            :prefix-icon="User"
            size="large"
            placeholder="请输入账号"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="data.form.password"
            :prefix-icon="Lock"
            size="large"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item prop="role">
          <el-select v-model="data.form.role" size="large" placeholder="请选择登录身份" style="width: 100%">
            <el-option label="学生端登录" value="STUDENT" />
            <el-option label="教师端登录" value="TEACHER" />
            <el-option label="管理员登录" value="ADMIN" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button size="large" type="primary" class="login-button" @click="login">登录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import { User, Lock } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import request from "@/utils/request";
import router from "@/router";

const data = reactive({
  form: {
    role: "STUDENT",
    username: "",
    password: ""
  },
  rules: {
    role: [{ required: true, message: "请选择登录身份", trigger: "change" }],
    username: [{ required: true, message: "请输入账号", trigger: "blur" }],
    password: [{ required: true, message: "请输入密码", trigger: "blur" }]
  }
});

const formRef = ref();

const login = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return;
    const res = await request.post("/api/auth/login", data.form);
    if (res.code !== "200") {
      ElMessage.error(res.msg);
      return;
    }
    ElMessage.success("登录成功");
    localStorage.setItem("system-user", JSON.stringify(res.data));
    const role = res.data.role;
    if (role === "STUDENT") router.push("/manager/student-overview");
    else if (role === "TEACHER") router.push("/manager/teacher-dashboard");
    else if (role === "ADMIN") router.push("/manager/admin-students");
    else router.push("/manager/home");
  });
};
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 24px;
  background:
    radial-gradient(circle at top left, rgba(217, 164, 65, 0.18), transparent 32%),
    radial-gradient(circle at bottom right, rgba(15, 118, 110, 0.28), transparent 36%),
    linear-gradient(135deg, #f3eee3 0%, #e7f0ee 48%, #d5e4e1 100%);
}

.login-box {
  width: 420px;
  max-width: 100%;
  padding: 42px 34px 34px;
  border-radius: 24px;
  border: 1px solid rgba(19, 59, 68, 0.08);
  box-shadow: 0 28px 60px rgba(19, 59, 68, 0.16);
  background: rgba(255, 253, 248, 0.92);
  backdrop-filter: blur(10px);
}

.login-badge {
  display: inline-flex;
  align-items: center;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(15, 118, 110, 0.1);
  color: #0f766e;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.login-title {
  margin-top: 18px;
  font-size: 32px;
  font-weight: 800;
  color: #133b44;
}

.login-subtitle {
  margin: 10px 0 26px;
  color: #5f6f6d;
  line-height: 1.7;
}

.login-button {
  width: 100%;
  height: 46px;
  font-weight: 700;
}
</style>
