# 基于逻辑回归算法的学情智能预警系统

## 1. 项目简介
本项目是面向高校教学场景的“学生端 + 教师端”学情智能预警系统。
核心闭环：
登录记出勤 -> 作业/考试行为 -> 特征提取 -> 逻辑回归预测 -> 风险分层 -> 预警落库 -> 教师干预。

## 2. 数据库初始化
```sql
source springboot/src/main/resources/sql/schema.sql;
source springboot/src/main/resources/sql/data.sql;
```

## 3. 启动方式
### 后端
1. 配置 `springboot/src/main/resources/application.yml` 的 MySQL 连接
2. 启动 `SpringbootApplication`

### 前端
```bash
cd vue
npm install
npm run dev
```

> 说明：若环境无法下载 npm 包，可直接使用现有 lock/deps；图表通过 CDN ECharts 挂载到 `window.echarts`。

## 4. 默认测试账号
- 教师：`t_zhang / 123456`、`t_li / 123456`
- 学生：`s_wang / 123456`、`s_liu / 123456`、`s_chen / 123456`

## 5. 核心接口说明
### 认证
- `POST /api/auth/login`

### 学生端
- `GET /api/student/overview`
- `GET /api/student/courses`
- `GET /api/student/homework`
- `POST /api/homework/submit`
- `GET /api/student/exams`
- `POST /api/exam/submit`
- `GET /api/student/risk-detail`
- `GET /api/student/trend`
- `POST /api/student/update-academic`

### 教师端
- `GET /api/dashboard/teacher`
- `GET /api/teacher/courses`
- `POST /api/homework/create`
- `GET /api/teacher/homework-manage`
- `POST /api/teacher/homework-grade`
- `POST /api/exam/create`
- `GET /api/teacher/exam-manage`
- `GET /api/teacher/student-detail`
- `GET /api/teacher/high-risk`
- `POST /api/intervention/add`
- `GET /api/intervention/list`

### 风险计算
- `POST /api/risk/predict`
- `POST /api/risk/predict-all`

## 6. 页面路由说明
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

## 7. 逻辑回归预警流程说明
1. `FeatureExtractor` 从出勤、作业、考试、GPA提取特征并存 `student_feature`。
2. `LogisticRegression` 计算 `p = sigmoid(b + Σ(w*x))`。
3. `RiskPredictor` 输出概率、风险等级（HIGH/MEDIUM/LOW）、颜色（RED/ORANGE/GREEN）、主原因。
4. 结果落库到 `risk_prediction`，同时写入 `warning_record`。
5. 教师在 `intervention_record` 记录干预信息，形成预警闭环。

## 8. 自动触发机制
- 学生登录（写出勤并触发重算）
- 作业提交后触发
- 考试提交后触发
- GPA更新后触发
- 定时任务：每日 2:00 全量重算

## 9. 推荐答辩演示流程
1. 学生登录 -> 生成登录出勤
2. 学生提交作业 -> 风险变化
3. 学生查看考试资格（动态出勤计算）-> 提交考试
4. 学生查看风险详情/趋势/GPA趋势
5. 教师登录 -> 看板查看分布与高风险
6. 教师进入高风险筛选页 -> 查看学生学情详情
7. 教师录入干预记录 -> 形成闭环
