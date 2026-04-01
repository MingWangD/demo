<template>
  <div>
    <div style="font-weight:bold; margin-bottom:8px">{{ title }}</div>
    <el-table :data="data || []" size="small" border>
      <el-table-column prop="name" label="分类"/>
      <el-table-column prop="value" label="数量"/>
    </el-table>
  </div>
</template>
<script setup>
defineProps({ title: String, data: Array })
</script>
