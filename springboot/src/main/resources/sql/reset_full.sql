-- 一键重置脚本（MySQL）
-- 用途：
-- 1) 删除所有业务表
-- 2) 重建所有表结构
-- 3) 重新插入演示数据

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS intervention_record;
DROP TABLE IF EXISTS warning_record;
DROP TABLE IF EXISTS risk_prediction;
DROP TABLE IF EXISTS student_feature;
DROP TABLE IF EXISTS student_academic;
DROP TABLE IF EXISTS exam_record;
DROP TABLE IF EXISTS exam_qualification;
DROP TABLE IF EXISTS exam;
DROP TABLE IF EXISTS homework_submission;
DROP TABLE IF EXISTS homework;
DROP TABLE IF EXISTS student_attendance;
DROP TABLE IF EXISTS student_course;
DROP TABLE IF EXISTS course;
DROP TABLE IF EXISTS sys_user;

SET FOREIGN_KEY_CHECKS = 1;

-- 重新建表 + 初始化数据
source springboot/src/main/resources/sql/schema.sql;
source springboot/src/main/resources/sql/data.sql;
