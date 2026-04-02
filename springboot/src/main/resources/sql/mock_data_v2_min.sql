-- 最小演示数据（5分钟联调版）
-- 覆盖链路：登录 -> 发布作业/考试 -> 学生提交 -> 自动判客观题 -> 干预记录
-- 使用：先执行 schema.sql，再执行本文件

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

INSERT INTO sys_user (id, username, password, name, role, student_no, teacher_no, college, major, class_name, status)
VALUES
(1, 't_zhang', '123456', '张老师', 'TEACHER', NULL, 'T2026001', '计算机学院', NULL, NULL, 'ACTIVE'),
(101, 's_wang', '123456', '王明', 'STUDENT', '20260001', NULL, '计算机学院', '软件工程', '软工1班', 'ACTIVE'),
(102, 's_liu', '123456', '刘洋', 'STUDENT', '20260002', NULL, '计算机学院', '软件工程', '软工1班', 'ACTIVE');

INSERT INTO course (id, course_name, course_code, teacher_id, credit, semester, total_weeks, attendance_required_count, exam_qualification_rate, description)
VALUES
(201, '数据结构', 'CS2026-DS', 1, 4.0, '2026-Spring', 16, 2, 0.50, '最小演示课程');

INSERT INTO student_course (student_id, course_id, status)
VALUES (101, 201, 'SELECTED'), (102, 201, 'SELECTED');

INSERT INTO student_attendance (student_id, course_id, attendance_type, attendance_time, week_no, remark)
VALUES
(101, 201, 'LOGIN', NOW(), 1, '登录自动记录'),
(102, 201, 'LOGIN', NOW(), 1, '登录自动记录');

INSERT INTO homework (id, course_id, teacher_id, title, content, deadline, total_score, create_time, update_time)
VALUES
(301, 201, 1, '数据结构练习', '1. 客观题：链表是否支持随机访问？（2分）\n2. 客观题：栈是先进后出吗？（2分）\n3. 主观题：写出栈的典型应用并分析（10分）', DATE_ADD(NOW(), INTERVAL 7 DAY), 100, NOW(), NOW());

INSERT INTO homework_submission (id, homework_id, student_id, submit_content, submit_time, score, is_late, status, teacher_comment, create_time, update_time)
VALUES
(1, 301, 101, '客观题答案已提交；主观题答案已提交', NOW(), 4.00, 0, 'SUBMITTED', '系统已自动判客观题得分：4.00，主观题待教师批改', NOW(), NOW());

INSERT INTO exam (id, course_id, teacher_id, exam_name, exam_time, total_score, duration_minutes, description, create_time, update_time)
VALUES
(401, 201, 1, '数据结构测验', DATE_ADD(NOW(), INTERVAL 10 DAY), 100, 90, '1. 客观题：时间复杂度符号是O吗？（2分）\n2. 客观题：队列是先进先出吗？（2分）\n3. 主观题：比较栈与队列应用场景（10分）', NOW(), NOW());

INSERT INTO exam_qualification (id, exam_id, student_id, attendance_count, required_count, qualification_rate, is_qualified, reason, create_time)
VALUES
(11, 401, 101, 1, 2, 0.5000, 1, '出勤达标，可参加考试', NOW()),
(12, 401, 102, 1, 2, 0.5000, 1, '出勤达标，可参加考试', NOW());

INSERT INTO exam_record (id, exam_id, student_id, score, submit_time, status, remark, create_time, update_time)
VALUES
(21, 401, 101, 4.00, NOW(), 'FINISHED', '系统已自动判客观题得分：4.00，主观题待教师批改；学生作答：客观题答案已提交；主观题答案已提交', NOW(), NOW());

INSERT INTO student_academic (student_id, total_credit, earned_credit, gpa, gpa_color, risk_note, update_time)
VALUES
(101, 20, 12, 2.40, 'YELLOW', '持续关注过程数据', NOW()),
(102, 20, 10, 1.80, 'ORANGE', '需加强巩固', NOW());

INSERT INTO student_feature (id, student_id, course_id, attendance_count, attendance_rate, homework_submit_rate, homework_avg_score, exam_avg_score, earned_credit, gpa, missing_homework_count, absent_exam_count, recent_risk_trend, feature_date, create_time)
VALUES
(701, 101, 201, 1, 0.5000, 1.0000, 4.00, 4.00, 12.00, 2.40, 0, 0, 'UP', CURDATE(), NOW()),
(702, 102, 201, 1, 0.5000, 0.0000, 0.00, 0.00, 10.00, 1.80, 1, 0, 'UP', CURDATE(), NOW());

INSERT INTO risk_prediction (id, student_id, course_id, risk_probability, risk_label, risk_level, warning_color, main_reason, model_version, predict_time, create_time)
VALUES
(801, 101, 201, 0.5200, '中风险', 'MEDIUM', 'ORANGE', '作业均分偏低；考试均分偏低', 'LR-1.1.0', NOW(), NOW()),
(802, 102, 201, 0.8600, '高风险', 'HIGH', 'RED', '作业提交率低；GPA低于预警区间', 'LR-1.1.0', NOW(), NOW());

INSERT INTO warning_record (id, student_id, course_id, prediction_id, warning_type, warning_level, warning_content, status, create_time, update_time)
VALUES
(901, 102, 201, 802, 'RISK_WARNING', 'HIGH', '系统识别高风险，请尽快干预', 'OPEN', NOW(), NOW());

INSERT INTO intervention_record (id, student_id, teacher_id, course_id, warning_id, intervention_type, intervention_content, intervention_result, create_time, update_time)
VALUES
(1001, 102, 1, 201, 901, 'TALK', '约谈并制定每周学习计划', '学生同意按计划执行', NOW(), NOW());
