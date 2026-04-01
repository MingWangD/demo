# 学情智能预警系统（脚手架二次开发）

## 运行说明

### 1) 数据库
```sql
source springboot/src/main/resources/sql/schema.sql;
source springboot/src/main/resources/sql/data.sql;
```

### 2) 后端
- JDK 21
- MySQL 8+
- 修改 `springboot/src/main/resources/application.yml` 数据库连接
- 启动 `SpringbootApplication`

### 3) 前端
```bash
cd vue
npm install
npm run dev
```

## 核心模块
- `algorithm/FeatureExtractor`：从业务表提取特征并写入 `student_feature`
- `algorithm/LogisticRegression`：逻辑回归 `sigmoid + 权重`
- `algorithm/RiskPredictor`：风险分层 + 原因生成 + 落库 `risk_prediction` 与 `warning_record`
- `scheduler/RiskScheduler`：每日 2 点批量重算

## 关键接口
- `POST /api/auth/login`
- `GET /api/student/overview`
- `GET /api/student/courses`
- `GET /api/student/homework`
- `POST /api/homework/submit`
- `GET /api/student/exams`
- `POST /api/exam/submit`
- `GET /api/student/risk-detail`
- `GET /api/student/trend`
- `GET /api/dashboard/teacher`
- `POST /api/homework/create`
- `POST /api/exam/create`
- `POST /api/intervention/add`
- `GET /api/intervention/list`
- `POST /api/risk/predict`
- `POST /api/risk/predict-all`

## 前端路由
- 学生：`/manager/student-overview`, `/manager/student-homework`, `/manager/student-exam`, `/manager/student-risk`
- 教师：`/manager/teacher-dashboard`, `/manager/teacher-homework`, `/manager/teacher-exam`, `/manager/teacher-intervention`
