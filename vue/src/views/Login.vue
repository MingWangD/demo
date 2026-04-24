<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-title">学业预警系统</div>
      <el-form ref="formRef" :model="data.form" :rules="data.rules">
        <el-form-item prop="role">
          <el-select v-model="data.form.role" size="large" placeholder="请选择登录身份" style="width: 100%">
            <el-option label="学生端登录" value="STUDENT" />
            <el-option label="教师端登录" value="TEACHER" />
            <el-option label="管理员登录" value="ADMIN" />
          </el-select>
        </el-form-item>
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
        <el-form-item>
          <el-button size="large" type="primary" style="width: 100%" @click="login">登录</el-button>
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
  height: 100vh;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #2e3143;
}

.login-box {
  width: 380px;
  padding: 50px 30px;
  border-radius: 6px;
  box-shadow: 0 0 10px rgba(255, 255, 255, 0.3);
  background-color: #fff;
}

.login-title {
  font-weight: bold;
  font-size: 30px;
  text-align: center;
  margin-bottom: 30px;
  color: #1967e3;
}
</style>
