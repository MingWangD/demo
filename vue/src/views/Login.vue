<template>
  <div class="login-container">
    <div class="login-box">
      <div style="font-weight: bold; font-size: 30px; text-align: center; margin-bottom: 30px; color: #1967e3">学情智能预警系统</div>
      <el-form :model="data.form"  ref="formRef" :rules="data.rules">
        <el-form-item prop="username">
          <el-input :prefix-icon="User" size="large" v-model="data.form.username" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input :prefix-icon="Lock" size="large" v-model="data.form.password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item>
          <el-button size="large" type="primary" style="width: 100%" @click="login">登 录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
  import { reactive, ref } from "vue";
  import { User, Lock } from "@element-plus/icons-vue";
  import request from "@/utils/request";
  import {ElMessage} from "element-plus";
  import router from "@/router";

  const data = reactive({
    form: {},
    rules: {
      username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
      password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
    }
  })

  const formRef = ref()
  const login = () => {
    formRef.value.validate((valid => {
      if (valid) {
        request.post('/api/auth/login', data.form).then(res => {
          if (res.code === '200') {
            ElMessage.success("登录成功")
            localStorage.setItem('system-user', JSON.stringify(res.data))
            const role = res.data.role
            if (role === 'STUDENT') router.push('/manager/student-overview')
            else if (role === 'TEACHER') router.push('/manager/teacher-dashboard')
            else router.push('/manager/home')
          } else ElMessage.error(res.msg)
        })
      }
    })).catch(console.error)
  }
</script>

<style scoped>
.login-container { height: 100vh; overflow:hidden; display: flex; justify-content: center; align-items: center; background: #2e3143; }
.login-box { width: 350px; padding: 50px 30px; border-radius: 5px; box-shadow: 0 0 10px rgba(255,255,255,0.3); background-color: #fff; }
</style>
