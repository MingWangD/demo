-- 模拟数据（适配当前新功能：自动判分/撤销/干预下拉去重/详情展示）
-- 使用方式：
-- 1) 先执行 schema.sql 建表
-- 2) 再执行本文件

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE intervention_record;
TRUNCATE TABLE warning_record;
TRUNCATE TABLE risk_prediction;
TRUNCATE TABLE student_feature;
TRUNCATE TABLE student_academic;
TRUNCATE TABLE exam_record;
TRUNCATE TABLE exam_qualification;
TRUNCATE TABLE exam;
TRUNCATE TABLE homework_submission;
TRUNCATE TABLE homework;
TRUNCATE TABLE student_attendance;
TRUNCATE TABLE student_course;
TRUNCATE TABLE course;
TRUNCATE TABLE sys_user;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO sys_user (id, username, password, name, role, student_no, teacher_no, college, major, class_name, status, avatar)
VALUES
(1, 't_zhang', '123456', '张老师', 'TEACHER', NULL, 'T2026001', '计算机学院', NULL, NULL, 'ACTIVE', NULL),
(2, 't_li', '123456', '李老师', 'TEACHER', NULL, 'T2026002', '信息工程学院', NULL, NULL, 'ACTIVE', NULL),
(101, 's_wang', '123456', '王明', 'STUDENT', '20260001', NULL, '计算机学院', '软件工程', '软工1班', 'ACTIVE', NULL),
(102, 's_liu', '123456', '刘洋', 'STUDENT', '20260002', NULL, '计算机学院', '软件工程', '软工1班', 'ACTIVE', NULL),
(103, 's_chen', '123456', '陈静', 'STUDENT', '20260003', NULL, '计算机学院', '软件工程', '软工1班', 'ACTIVE', NULL),
(104, 's_zhao', '123456', '赵峰', 'STUDENT', '20260004', NULL, '计算机学院', '网络工程', '网工1班', 'ACTIVE', NULL),
(105, 's_sun', '123456', '孙丽', 'STUDENT', '20260005', NULL, '计算机学院', '网络工程', '网工1班', 'ACTIVE', NULL),
(106, 's_hu', '123456', '胡涛', 'STUDENT', '20260006', NULL, '计算机学院', '网络工程', '网工1班', 'ACTIVE', NULL);

INSERT INTO course (id, course_name, course_code, teacher_id, credit, semester, total_weeks, attendance_required_count, exam_qualification_rate, description)
VALUES
(201, '数据结构', 'CS2026-DS', 1, 4.0, '2026-Spring', 16, 10, 0.67, '核心专业基础课'),
(202, '数据库原理', 'CS2026-DB', 1, 3.0, '2026-Spring', 16, 10, 0.67, '数据库系统设计与SQL'),
(203, '机器学习导论', 'CS2026-ML', 2, 2.0, '2026-Spring', 16, 9, 0.60, '含逻辑回归基础内容');

INSERT INTO student_course (student_id, course_id, status)
VALUES
(101, 201, 'SELECTED'), (101, 202, 'SELECTED'),
(102, 201, 'SELECTED'), (102, 203, 'SELECTED'),
(103, 201, 'SELECTED'), (103, 202, 'SELECTED'), (103, 203, 'SELECTED'),
(104, 202, 'SELECTED'), (104, 203, 'SELECTED'),
(105, 201, 'SELECTED'), (105, 202, 'SELECTED'),
(106, 201, 'SELECTED'), (106, 203, 'SELECTED');

-- 登录出勤（同一课程同一天只保留一条）
INSERT INTO student_attendance (student_id, course_id, attendance_type, attendance_time, week_no, remark)
VALUES
(101, 201, 'LOGIN', '2026-04-01 08:10:00', 1, '登录自动记录'),
(101, 202, 'LOGIN', '2026-04-01 10:10:00', 1, '登录自动记录'),
(101, 201, 'LOGIN', '2026-04-02 08:10:00', 2, '登录自动记录'),
(102, 201, 'LOGIN', '2026-04-01 08:20:00', 1, '登录自动记录'),
(103, 201, 'LOGIN', '2026-04-01 08:30:00', 1, '登录自动记录'),
(104, 202, 'LOGIN', '2026-04-01 09:20:00', 1, '登录自动记录'),
(105, 201, 'LOGIN', '2026-04-01 09:40:00', 1, '登录自动记录'),
(106, 203, 'LOGIN', '2026-04-01 10:00:00', 1, '登录自动记录');

-- 作业题面包含“客观题/主观题”关键字，便于自动判分
INSERT INTO homework (id, course_id, teacher_id, title, content, deadline, total_score, create_time, update_time)
VALUES
(301, 201, 1, '数据结构周测作业', '1. 客观题：链表插入复杂度？（2分）\n2. 客观题：栈的应用场景？（2分）\n3. 主观题：二叉树遍历实现与分析（10分）', '2026-04-30 23:59:59', 100, NOW(), NOW()),
(302, 202, 1, '数据库课后作业', '1. 客观题：事务ACID含义？（2分）\n2. 客观题：索引作用？（2分）\n3. 主观题：设计一个选课系统ER图（10分）', '2026-04-28 23:59:59', 100, NOW(), NOW()),
(303, 203, 2, '机器学习导论作业', '1. 客观题：sigmoid函数定义（2分）\n2. 主观题：逻辑回归损失函数推导（10分）', '2026-04-27 23:59:59', 100, NOW(), NOW());

-- 作业提交：含自动判分结果与主观题待批改提示
INSERT INTO homework_submission (id, homework_id, student_id, submit_content, submit_time, score, is_late, status, teacher_comment, create_time, update_time)
VALUES
(1, 301, 101, '客观题答案已提交；主观题答案已提交', '2026-04-03 20:00:00', 4.00, 0, 'SUBMITTED', '系统已自动判客观题得分：4.00，主观题待教师批改', NOW(), NOW()),
(2, 301, 102, '客观题答案已提交；主观题答案已提交', '2026-04-03 21:00:00', 4.00, 0, 'GRADED', '主观题已批改，继续保持', NOW(), NOW()),
(3, 302, 101, '客观题答案已提交；主观题答案已提交', '2026-04-04 19:00:00', 4.00, 0, 'SUBMITTED', '系统已自动判客观题得分：4.00，主观题待教师批改', NOW(), NOW()),
(4, 303, 106, NULL, NULL, NULL, 0, 'MISSING', '未提交', NOW(), NOW());

INSERT INTO exam (id, course_id, teacher_id, exam_name, exam_time, total_score, duration_minutes, description, create_time, update_time)
VALUES
(401, 201, 1, '数据结构阶段考试', '2026-05-10 09:00:00', 100, 120, '1. 客观题：时间复杂度判断（2分）\n2. 客观题：栈和队列差异（2分）\n3. 主观题：红黑树插入过程（10分）', NOW(), NOW()),
(402, 202, 1, '数据库原理阶段考试', '2026-05-12 14:00:00', 100, 90, '1. 客观题：隔离级别（2分）\n2. 客观题：范式概念（2分）\n3. 主观题：SQL优化方案（10分）', NOW(), NOW()),
(403, 203, 2, '机器学习小测', '2026-05-15 10:00:00', 100, 60, '1. 客观题：逻辑回归输出范围（2分）\n2. 主观题：过拟合与正则化（10分）', NOW(), NOW());

INSERT INTO exam_qualification (id, exam_id, student_id, attendance_count, required_count, qualification_rate, is_qualified, reason, create_time)
VALUES
(11, 401, 101, 2, 10, 0.2000, 0, '出勤未达标，暂不具备考试资格', NOW()),
(12, 401, 102, 1, 10, 0.1000, 0, '出勤未达标，暂不具备考试资格', NOW()),
(13, 402, 101, 1, 10, 0.1000, 0, '出勤未达标，暂不具备考试资格', NOW()),
(14, 403, 106, 1, 9, 0.1111, 0, '出勤未达标，暂不具备考试资格', NOW());

-- 考试记录包含自动判分说明+学生作答摘要
INSERT INTO exam_record (id, exam_id, student_id, score, submit_time, status, remark, create_time, update_time)
VALUES
(21, 402, 101, 4.00, '2026-05-12 15:20:00', 'FINISHED', '系统已自动判客观题得分：4.00，主观题待教师批改；学生作答：客观题答案已提交；主观题答案已提交', NOW(), NOW()),
(22, 403, 103, 2.00, '2026-05-15 10:50:00', 'FINISHED', '系统已自动判客观题得分：2.00，主观题待教师批改；学生作答：客观题答案已提交；主观题答案已提交', NOW(), NOW()),
(23, 401, 102, NULL, NULL, 'ABSENT', '未参加考试', NOW(), NOW());

INSERT INTO student_academic (student_id, total_credit, earned_credit, gpa, gpa_color, risk_note, update_time)
VALUES
(101, 20, 14, 2.60, 'GREEN', '总体正常，注意稳定出勤', NOW()),
(102, 20, 10, 1.90, 'ORANGE', '成绩波动，作业提交率偏低', NOW()),
(103, 20, 18, 3.20, 'GREEN', '学习状态良好', NOW()),
(104, 20, 12, 2.20, 'YELLOW', '需提高数据库课程成绩', NOW()),
(105, 20, 9, 1.40, 'RED', '高风险，建议重点干预', NOW()),
(106, 20, 8, 1.30, 'RED', '高风险，存在缺考与缺交', NOW());

INSERT INTO student_feature (id, student_id, course_id, attendance_count, attendance_rate, homework_submit_rate, homework_avg_score, exam_avg_score, earned_credit, gpa, missing_homework_count, absent_exam_count, recent_risk_trend, feature_date, create_time)
VALUES
(701, 101, 201, 2, 0.2000, 1.0000, 4.00, 0.00, 14.00, 2.60, 0, 0, 'UP', '2026-04-02', NOW()),
(702, 102, 201, 1, 0.1000, 1.0000, 4.00, 0.00, 10.00, 1.90, 0, 1, 'UP', '2026-04-02', NOW()),
(703, 106, 203, 1, 0.1111, 0.0000, 0.00, 0.00, 8.00, 1.30, 1, 1, 'UP', '2026-04-02', NOW());

INSERT INTO risk_prediction (id, student_id, course_id, risk_probability, risk_label, risk_level, warning_color, main_reason, model_version, predict_time, create_time)
VALUES
(801, 101, 201, 0.4200, '低风险', 'LOW', 'GREEN', '出勤次数偏低', 'LR-1.1.0', NOW(), NOW()),
(802, 102, 201, 0.7600, '中风险', 'MEDIUM', 'ORANGE', '出勤次数偏低；GPA低于预警区间', 'LR-1.1.0', NOW(), NOW()),
(803, 105, 201, 0.9100, '高风险', 'HIGH', 'RED', '作业提交率低；GPA低于预警区间', 'LR-1.1.0', NOW(), NOW()),
(804, 106, 203, 0.9500, '高风险', 'HIGH', 'RED', '作业提交率低；考试平均分偏低；GPA低于预警区间', 'LR-1.1.0', NOW(), NOW());

INSERT INTO warning_record (id, student_id, course_id, prediction_id, warning_type, warning_level, warning_content, status, create_time, update_time)
VALUES
(901, 102, 201, 802, 'RISK_WARNING', 'MEDIUM', '系统识别为中风险，请尽快提升出勤与作业质量', 'OPEN', NOW(), NOW()),
(902, 105, 201, 803, 'GPA_WARNING', 'HIGH', '当前GPA<1.5，学业红色预警', 'OPEN', NOW(), NOW()),
(903, 106, 203, 804, 'RISK_WARNING', 'HIGH', '综合风险高，建议立即干预', 'OPEN', NOW(), NOW());

INSERT INTO intervention_record (id, student_id, teacher_id, course_id, warning_id, intervention_type, intervention_content, intervention_result, create_time, update_time)
VALUES
(1001, 105, 1, 201, 902, 'TALK', '约谈学生并制定每周学习计划', '学生承诺按时提交作业', NOW(), NOW()),
(1002, 106, 2, 203, 903, 'LEARNING_SUPPORT', '安排机器学习课程一对一辅导', '已完成第一次辅导', NOW(), NOW());
