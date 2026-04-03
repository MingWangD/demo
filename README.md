# 基于逻辑回归算法的学情智能预警系统（答辩加固版）

## 1. 项目简介
本项目是面向高校教学场景的“学生端 + 教师端”学情智能预警系统。核心闭环：
登录记出勤 -> 作业/考试行为 -> 特征提取 -> 逻辑回归预测 -> 风险分层 -> 预警落库 -> 教师干预。

---

## 2. 最小可运行说明（已验证）
### 2.1 数据库初始化
```sql
source springboot/src/main/resources/sql/schema.sql;
source springboot/src/main/resources/sql/data.sql;
```
> `data.sql` 已内置固定演示数据（包含高风险学生、预警记录、干预记录），建议答辩前重置一次数据库以保证演示结果稳定。
> 若需“课程=3门且各课程均有学生可选”的稳定答辩数据，请使用：`springboot/src/main/resources/sql/demo_fixed_showcase.sql`。

### 2.2 前端启动（已在当前环境验证构建通过）
```bash
cd vue
npm install
npm run dev
# 验证命令：npm run build
```

### 2.3 后端启动
```bash
cd springboot
mvn spring-boot:run
```
> 当前环境存在 Maven 远程仓库限制（403/网络不可达），后端编译验证受阻。代码已完成，建议在本机可联网环境运行。
> 已提供 `springboot/mvn-settings.xml`（阿里云镜像配置）用于规避 central 限制。

### 2.4 默认测试账号
- 教师：`t_zhang / 123456`、`t_li / 123456`
- 学生：`s_wang / 123456`、`s_liu / 123456`、`s_chen / 123456`

---

## 3. 鉴权与权限规则
- 登录成功返回 JWT token，前端统一放入 `Authorization: Bearer <token>`。
- 角色校验：`/api/student/**` 仅学生、`/api/teacher/**` 仅教师。
- 资源归属校验：教师仅允许操作自己课程（作业发布/评分、考试发布/管理、高风险按课程筛选、学生课程详情）。

---

## 4. 核心接口（含 JSON 示例）
### 4.1 登录
`POST /api/auth/login`

请求：
```json
{
  "username": "s_wang",
  "password": "123456"
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

### 4.2 学生总览
`GET /api/student/overview?studentId=101`

响应（节选）：
```json
{
  "code": "200",
  "data": {
    "gpa": 2.6,
    "gpaColor": "GREEN",
    "riskProbability": 0.32,
    "riskLevel": "LOW",
    "warningColor": "GREEN",
    "mainReason": "学习状态稳定"
  }
}
```

### 4.3 作业提交触发重算
`POST /api/homework/submit`
```json
{
  "homeworkId": 301,
  "studentId": 101,
  "submitContent": "在线提交内容"
}
```

### 4.4 考试提交（先资格校验）
`POST /api/exam/submit`
```json
{
  "examId": 401,
  "studentId": 101,
  "score": 86
}
```

### 4.5 教师高风险筛选
`GET /api/teacher/high-risk?courseId=201&riskLevel=HIGH&gpaColor=RED`

### 4.6 教师作业评分
`POST /api/teacher/homework-grade?submissionId=1&score=90&comment=继续保持`

---

## 5. 业务规则说明
1. 学生登录视为出勤，并立即触发风险重算。
2. 考试资格由出勤动态计算并落库（`exam_qualification`）。
3. 作业提交、考试提交、GPA更新、定时任务均触发风险重算。
4. 风险分层：`>=0.8 HIGH`, `>=0.5 MEDIUM`, `<0.5 LOW`。
5. GPA颜色：`<1.5 RED`, `<2.0 ORANGE`, `<2.5 YELLOW`, `>=2.5 GREEN`。
6. 预警闭环：`risk_prediction -> warning_record -> intervention_record`。

---

## 6. 逻辑回归说明（答辩可讲）
- 模型：`p = sigmoid(b + Σ(w*x))`
- 特征：出勤率、作业提交率、作业均分、考试均分、GPA、缺交数、缺考数等。
- 输出：风险概率、风险等级、预警颜色、主原因。
- 当前为“固定权重演示版”，但结构保留了后续训练与参数替换能力。

---

## 7. 页面路由
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

---

## 8. 最优答辩演示顺序
1. 教师登录 -> 教师看板（先讲系统价值）
2. 进入“高风险筛选” -> 展示高风险名单
3. 进入“学生学情详情” -> 展示风险原因、GPA颜色、成绩指标
4. 进入“作业管理/考试管理” -> 发布/评分/资格状态
5. 学生登录 -> 学情总览
6. 学生提交作业 -> 再看风险趋势变化
7. 学生查看考试资格 -> 提交考试
8. 教师录入干预 -> 解释闭环完成

---

## 9. 图表离线兜底方案
- 首选：`public/echarts.min.js` 本地文件（可替换为完整官方 dist）。
- 兜底：若本地未加载成功，自动 fallback 到 CDN。
- 答辩现场断网时，请提前放置完整本地 `echarts.min.js`。
