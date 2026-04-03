-- 数据库性能优化脚本（2026-04-03）
-- 用途：为高频筛选、关联与时间序列查询补充索引。
-- 说明：建议在低峰期执行；若索引已存在请先删除重复语句或调整索引名。

USE `study_warning`;

-- 风险预测：按学生/课程取最新预测、按时间趋势查询
ALTER TABLE `risk_prediction`
  ADD INDEX `idx_risk_student_course_time` (`student_id`, `course_id`, `predict_time`),
  ADD INDEX `idx_risk_course_level_time` (`course_id`, `risk_level`, `predict_time`);

-- 预警记录：按状态、课程、学生读取 OPEN 记录
ALTER TABLE `warning_record`
  ADD INDEX `idx_warning_status_course_student_time` (`status`, `course_id`, `student_id`, `create_time`);

-- 干预记录：学生端提醒与教师端检索
ALTER TABLE `intervention_record`
  ADD INDEX `idx_intervention_student_course_time` (`student_id`, `course_id`, `create_time`);

-- 出勤：考试资格与特征提取高频统计
ALTER TABLE `student_attendance`
  ADD INDEX `idx_attendance_student_course_time` (`student_id`, `course_id`, `attendance_time`);

-- 作业与提交：课程维度列表、学生维度进度与评分
ALTER TABLE `homework`
  ADD INDEX `idx_homework_course_deadline` (`course_id`, `deadline`);

ALTER TABLE `homework_submission`
  ADD INDEX `idx_hwsub_student_status_time` (`student_id`, `status`, `submit_time`);

-- 考试与资格：学生资格、考试记录与状态筛选
ALTER TABLE `exam`
  ADD INDEX `idx_exam_course_time` (`course_id`, `exam_time`);

ALTER TABLE `exam_record`
  ADD INDEX `idx_exam_record_student_status_time` (`student_id`, `status`, `submit_time`);

ALTER TABLE `exam_qualification`
  ADD INDEX `idx_exam_qual_student_qualified_rate` (`student_id`, `is_qualified`, `qualification_rate`);
