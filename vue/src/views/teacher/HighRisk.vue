<template>
  <div>
    <h3 style="margin:0 0 10px 0;color:#303133">高风险学生筛选</h3>
    <div class="card">
      <el-select v-model="filters.courseId" placeholder="选择课程(可选)" style="width:220px;margin-right:8px">
        <el-option label="全部课程" value=""/>
        <el-option v-for="c in courses" :key="c.id" :label="`${c.courseName}(ID:${c.id})`" :value="String(c.id)" />
      </el-select>
      <el-select v-model="filters.gpaColor" placeholder="GPA颜色" style="width:120px;margin-right:8px">
        <el-option label="全部" value=""/><el-option label="RED" value="RED"/><el-option label="ORANGE" value="ORANGE"/><el-option label="YELLOW" value="YELLOW"/><el-option label="GREEN" value="GREEN"/>
      </el-select>
      <el-select v-model="filters.riskLevel" placeholder="风险等级" style="width:120px;margin-right:8px">
        <el-option label="HIGH" value="HIGH"/>
        <el-option label="MEDIUM" value="MEDIUM"/>
        <el-option label="LOW" value="LOW"/>
        <el-option label="全部" value=""/>
      </el-select>
      <el-button type="primary" @click="onFilter">筛选</el-button>
    </div>
    <el-table :data="list" empty-text="暂无数据，请调整筛选条件" style="margin-top:10px">
      <el-table-column label="学生" prop="student.name"/>
      <el-table-column label="课程">
        <template #default="scope">
          {{ scope.row.courseName || `课程ID:${scope.row.risk?.courseId ?? '-'}` }}
        </template>
      </el-table-column>
      <el-table-column label="风险概率" prop="risk.riskProbability"/>
      <el-table-column label="风险等级" prop="risk.riskLevel"/>
      <el-table-column label="GPA颜色" prop="academic.gpaColor"/>
      <el-table-column label="原因" prop="risk.mainReason"/>
    </el-table>
    <div style="display:flex;justify-content:flex-end;margin-top:12px">
      <el-pagination
          background
          layout="total, prev, pager, next, sizes"
          :current-page="page.pageNum"
          :page-size="page.pageSize"
          :page-sizes="[5,10,20,50]"
          :total="page.total"
          @current-change="onPageChange"
          @size-change="onSizeChange"
      />
    </div>
    <div class="card" style="margin-top:12px;color:#606266;line-height:1.8">
      <div style="font-weight:600;color:#303133">风险概率计算公式（逻辑回归演示版）</div>
      <div>p = sigmoid(z), sigmoid(z) = 1 / (1 + e<sup>-z</sup>)</div>
      <div>
        z = 0.35
        - 2.2 × 出勤率
        - 1.8 × 作业提交率
        - 0.018 × 作业均分
        - 0.020 × 考试均分
        - 1.4 × GPA
        + 0.65 × 缺交作业数
        + 0.95 × 缺考数
      </div>
    </div>
  </div>
</template>
<script setup>
import {reactive,ref,onMounted} from 'vue'
import request from '@/utils/request'
const list=ref([])
const courses=ref([])
const filters=reactive({courseId:'',gpaColor:'',riskLevel:'HIGH'})
const page=reactive({pageNum:1,pageSize:10,total:0})
const onFilter=()=>{page.pageNum=1; load()}
const load=async()=>{
  const r=await request.get('/api/teacher/high-risk',{params:{...filters,...page}})
  const data=r.data||{}
  list.value=data.list||[]
  page.total=data.total||0
}
const onPageChange=(val)=>{page.pageNum=val; load()}
const onSizeChange=(val)=>{page.pageSize=val; page.pageNum=1; load()}
const loadCourses=async()=>{const r=await request.get('/api/teacher/courses'); courses.value=r.data||[]}
onMounted(async()=>{await Promise.all([loadCourses(), load()])})
</script>
