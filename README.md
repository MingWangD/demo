# 基于逻辑回归算法的学情智能预警系统

## 1. 项目简介
本项目是面向高校教学场景的“学生端 + 教师端”学情智能预警系统。核心闭环：

登录记出勤 -> 作业/考试行为 -> 特征提取 -> 逻辑回归预测 -> 风险分层 -> 预警落库 -> 教师干预。

## 2. 项目结构
- `springboot/`：Spring Boot 后端服务，负责登录鉴权、课程/作业/考试/预警/干预接口、逻辑回归风险计算、MyBatis 数据访问和定时重算任务。
- `vue/`：Vue3 前端应用，负责学生端/教师端页面、路由守卫、接口请求、预警图表和交互展示。
- `files/`：本地上传/下载文件目录。
- `docs/`：项目分析、测试清单和答辩材料。

## 3. 环境要求
- JDK 21
- Maven 3.9+
- Node.js 24+
- npm 11+
- MySQL 8+

## 4. 数据库初始化
先创建数据库 `study_warning`，再按演示场景导入 SQL。

### 4.1 基础演示数据
```sql
source springboot/src/main/resources/sql/schema.sql;
source springboot/src/main/resources/sql/data.sql;
```
默认账号：
- 教师：`t_zhang / 123456`、`t_li / 123456`
- 学生：`s_wang / 123456`、`s_liu / 123456`、`s_chen / 123456`

### 4.2 固定答辩演示数据
```sql
source springboot/src/main/resources/sql/schema.sql;
source springboot/src/main/resources/sql/demo_fixed_showcase.sql;
```
默认账号与 4.1 相同。

### 4.3 20 名学生扩展演示数据
```sql
source springboot/src/main/resources/sql/schema.sql;
source springboot/src/main/resources/sql/demo_fixed_20_students_0315_0401.sql;
```
默认账号：
- 教师：`t_zhang / 123456`、`t_li / 123456`
- 学生：`s_101 / 123456` 到 `s_120 / 123456`

如果你当前数据库导入的是 4.3 这份脚本，那么 `s_wang / 123456` 这类账号会登录失败，这是数据集不同导致的正常现象。

## 5. 启动方式
### 5.1 后端
```bash
cd springboot
mvn spring-boot:run
```
后端默认端口：`http://localhost:9090`

如遇 Maven 仓库访问问题，可改用：
```bash
cd springboot
mvn -s mvn-settings.xml spring-boot:run
```

### 5.2 前端
```bash
cd vue
npm install
npm run dev
```
前端开发服务默认会从 `.env.development` 读取后端地址。

### 5.3 构建校验
```bash
cd springboot
mvn -DskipTests package
```

```bash
cd vue
npm run build
```

## 6. 核心权限规则
- 登录成功返回 JWT token，前端统一写入 `Authorization: Bearer <token>`。
- `/api/student/**` 仅允许学生角色访问。
- `/api/teacher/**` 仅允许教师角色访问。
- 教师只能操作自己名下课程的数据，包括作业、考试、高风险筛选和学生详情。

## 7. 核心接口示例
### 7.1 登录
`POST /api/auth/login`

请求：
```json
{
  "username": "s_101",
  "password": "123456",
  "role": "STUDENT"
}
```

响应：
```json
{
  "code": "200",
  "msg": "请求成功",
  "data": {
    "token": "eyJ...",
    "userId": 101,
    "name": "王明",
    "role": "STUDENT",
    "avatar": null
  }
}
```

### 7.2 学生总览
`GET /api/student/overview?studentId=101`

### 7.3 学生作业提交
`POST /api/homework/submit`
```json
{
  "homeworkId": 305,
  "submitContent": "{\"objectiveAnswered\":2,\"subjectiveAnswers\":[\"答案内容\"]}"
}
```

### 7.4 学生考试提交
`POST /api/exam/submit`
```json
{
  "examId": 401,
  "answerContent": "{\"objectiveAnswered\":2,\"subjectiveAnswers\":[\"答案内容\"]}"
}
```

### 7.5 教师高风险筛选
`GET /api/teacher/high-risk?courseId=201&riskLevel=HIGH&gpaColor=RED&pageNum=1&pageSize=10`

### 7.6 教师干预记录
- 新增干预：`POST /api/intervention/add`
- 查询干预：`GET /api/intervention/list?studentId=105&courseId=201`

## 8. 业务规则
1. 学生登录成功后自动记录登录出勤，并触发风险重算。
2. 考试资格根据课程出勤要求动态计算，不满足资格时后端拒绝提交考试。
3. 作业提交、考试提交、GPA 更新和定时任务都会触发风险重算。
4. 模型风险分层：`>= 0.8 HIGH`，`>= 0.5 MEDIUM`，`< 0.5 LOW`。
5. GPA 颜色分层：`< 1.5 RED`，`< 2.0 ORANGE`，`< 2.5 YELLOW`，`>= 2.5 GREEN`。
6. 预警闭环链路：`risk_prediction -> warning_record -> intervention_record`。

## 9. 逻辑回归说明
当前系统使用逻辑回归作为风险预测核心，预测公式为：

`p = sigmoid(b + Σ(w_i * x_i))`

特征包括出勤率、作业提交率、作业均分、考试均分、GPA、缺交次数、缺考次数等；输出包括风险概率、风险等级、预警颜色和主原因说明。

当前实现是“固定权重演示版”，保留了后续训练和参数替换的扩展空间。

## 10. 页面路由
### 学生端
- `/manager/student-overview`
- `/manager/student-homework`
- `/manager/student-exam`
- `/manager/student-risk`

### 教师端
- `/manager/teacher-dashboard`
- `/manager/teacher-homework`
- `/manager/teacher-exam`
- `/manager/teacher-high-risk`
- `/manager/teacher-student-detail`
- `/manager/teacher-intervention`
