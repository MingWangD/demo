<template>
  <div class="home-page">
    <div class="card hero-card">
      <div class="hero-title">学业预警系统</div>
      <div class="hero-desc">
        系统围绕学生学业过程数据、教师维护的课程出勤、作业与考试管理、逻辑回归风险预测、预警分级展示和教师干预跟踪，构建完整业务闭环。
      </div>
    </div>

    <el-row :gutter="12">
      <el-col :span="8">
        <div class="card metric-card">
          <div class="metric-title">学生端</div>
          <div class="metric-desc">查看已绑定课程、作业、考试、绩点、风险等级和趋势变化。</div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="card metric-card">
          <div class="metric-title">教师端</div>
          <div class="metric-desc">管理课程作业考试，维护学生出勤记录，筛选高风险学生并记录干预。</div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="card metric-card">
          <div class="metric-title">智能预警</div>
          <div class="metric-desc">自动提取特征，计算风险概率，生成预警记录和颜色分层。</div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.home-page {
  min-height: calc(100vh - 80px);
}

.hero-card {
  margin-bottom: 12px;
  background: linear-gradient(135deg, #409eff, #67c23a);
  color: #fff;
}

.hero-title {
  font-size: 26px;
  font-weight: 700;
  margin-bottom: 10px;
}

.hero-desc {
  font-size: 14px;
  line-height: 1.8;
}

.metric-card {
  min-height: 120px;
}

.metric-title {
  font-size: 18px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 10px;
}

.metric-desc {
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
}
</style>
