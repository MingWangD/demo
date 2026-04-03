-- 固定演示数据（答辩版）
-- 目标：
-- 1) 教师“张老师”名下固定 3 门课程，学生详情页课程下拉稳定显示三门
-- 2) 每门课程都有学生，避免“选择学生=无数据”
-- 3) 风险分布包含 HIGH/MEDIUM/LOW，便于完整演示筛选与干预

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
(2, 't_li', '123456', '李老师', 'TEACHER', NULL, 'T2026002', '计算机学院', NULL, NULL, 'ACTIVE', NULL),
(101, 's_wang', '123456', '王明', 'STUDENT', '20260001', NULL, '计算机学院', '软件工程', '软工1班', 'ACTIVE', NULL),
(102, 's_liu', '123456', '刘洋', 'STUDENT', '20260002', NULL, '计算机学院', '软件工程', '软工1班', 'ACTIVE', NULL),
(103, 's_chen', '123456', '陈静', 'STUDENT', '20260003', NULL, '计算机学院', '软件工程', '软工1班', 'ACTIVE', NULL),
(104, 's_zhao', '123456', '赵峰', 'STUDENT', '20260004', NULL, '计算机学院', '网络工程', '网工1班', 'ACTIVE', NULL),
(105, 's_sun', '123456', '孙丽', 'STUDENT', '20260005', NULL, '计算机学院', '网络工程', '网工1班', 'ACTIVE', NULL),
(106, 's_hu', '123456', '胡涛', 'STUDENT', '20260006', NULL, '计算机学院', '网络工程', '网工1班', 'ACTIVE', NULL);

-- 张老师固定三门课，便于“学生详情”与“高风险筛选”演示
INSERT INTO course (id, course_name, course_code, teacher_id, credit, semester, total_weeks, attendance_required_count, exam_qualification_rate, description)
VALUES
(201, '数据结构', 'CS2026-DS', 1, 4.0, '2026-Spring', 16, 10, 0.67, '核心专业基础课'),
(202, '数据库原理', 'CS2026-DB', 1, 3.0, '2026-Spring', 16, 10, 0.67, '数据库系统与SQL实践'),
(203, '机器学习导论', 'CS2026-ML', 1, 2.0, '2026-Spring', 16, 9, 0.60, '逻辑回归/分类模型基础'),
(204, '计算机网络', 'CS2026-NET', 2, 3.0, '2026-Spring', 16, 9, 0.60, '另一位教师课程');

INSERT INTO student_course (student_id, course_id, status)
VALUES
(101, 201, 'SELECTED'), (101, 202, 'SELECTED'), (101, 203, 'SELECTED'),
(102, 201, 'SELECTED'), (102, 202, 'SELECTED'),
(103, 201, 'SELECTED'), (103, 202, 'SELECTED'), (103, 203, 'SELECTED'),
(104, 202, 'SELECTED'), (104, 203, 'SELECTED'),
(105, 201, 'SELECTED'), (105, 203, 'SELECTED'),
(106, 201, 'SELECTED'), (106, 202, 'SELECTED'), (106, 203, 'SELECTED');

INSERT INTO student_attendance (student_id, course_id, attendance_type, attendance_time, week_no, remark)
VALUES
(101,201,'LOGIN','2026-03-03 08:10:00',1,'登录自动记录'), (101,202,'LOGIN','2026-03-04 10:10:00',1,'登录自动记录'), (101,203,'LOGIN','2026-03-05 14:10:00',1,'登录自动记录'),
(102,201,'LOGIN','2026-03-03 08:20:00',1,'登录自动记录'), (102,202,'LOGIN','2026-03-04 10:20:00',1,'登录自动记录'),
(103,201,'LOGIN','2026-03-03 08:30:00',1,'登录自动记录'), (103,202,'LOGIN','2026-03-04 10:30:00',1,'登录自动记录'), (103,203,'LOGIN','2026-03-05 14:30:00',1,'登录自动记录'),
(104,202,'LOGIN','2026-03-04 10:40:00',1,'登录自动记录'), (104,203,'LOGIN','2026-03-05 14:40:00',1,'登录自动记录'),
(105,201,'LOGIN','2026-03-03 08:50:00',1,'登录自动记录'), (105,203,'LOGIN','2026-03-05 14:50:00',1,'登录自动记录'),
(106,201,'LOGIN','2026-03-03 09:00:00',1,'登录自动记录'), (106,202,'LOGIN','2026-03-04 11:00:00',1,'登录自动记录'), (106,203,'LOGIN','2026-03-05 15:00:00',1,'登录自动记录');

INSERT INTO homework (id, course_id, teacher_id, title, content, deadline, total_score)
VALUES
(301, 201, 1, '链表与栈练习', '完成链表逆置与括号匹配', '2026-04-20 23:59:59', 100),
(302, 202, 1, 'SQL多表查询', '完成多表联查统计', '2026-04-22 23:59:59', 100),
(303, 203, 1, '逻辑回归推导', '提交推导报告', '2026-04-25 23:59:59', 100);

INSERT INTO homework_submission (homework_id, student_id, submit_content, submit_time, score, is_late, status, teacher_comment)
VALUES
(301, 101, '已提交', '2026-04-19 20:00:00', 92, 0, 'GRADED', '完成度高'),
(301, 102, '已提交', '2026-04-20 22:00:00', 78, 0, 'GRADED', '基础一般'),
(301, 103, '已提交', '2026-04-20 19:00:00', 88, 0, 'GRADED', '较好'),
(301, 105, NULL, NULL, NULL, 0, 'MISSING', '缺交'),
(302, 101, '已提交', '2026-04-21 20:00:00', 90, 0, 'GRADED', '规范'),
(302, 103, '已提交', '2026-04-22 21:00:00', 86, 0, 'GRADED', '不错'),
(302, 104, '已提交', '2026-04-23 11:00:00', 70, 1, 'GRADED', '迟交'),
(302, 106, NULL, NULL, NULL, 0, 'MISSING', '缺交'),
(303, 101, '已提交', '2026-04-24 20:00:00', 89, 0, 'GRADED', '理解到位'),
(303, 103, '已提交', '2026-04-24 21:00:00', 94, 0, 'GRADED', '优秀'),
(303, 104, '已提交', '2026-04-25 20:00:00', 74, 0, 'GRADED', '需加强'),
(303, 105, NULL, NULL, NULL, 0, 'MISSING', '缺交');

INSERT INTO exam (id, course_id, teacher_id, exam_name, exam_time, total_score, duration_minutes, description)
VALUES
(401, 201, 1, '数据结构阶段测试', '2026-05-10 09:00:00', 100, 120, '闭卷'),
(402, 202, 1, '数据库阶段测试', '2026-05-12 14:00:00', 100, 90, '机考'),
(403, 203, 1, '机器学习小测', '2026-05-15 10:00:00', 100, 60, '课堂测验');

INSERT INTO exam_qualification (exam_id, student_id, attendance_count, required_count, qualification_rate, is_qualified, reason)
VALUES
(401,101,8,10,0.80,1,'出勤达标'), (401,102,6,10,0.60,0,'出勤不足'), (401,103,9,10,0.90,1,'出勤达标'), (401,105,3,10,0.30,0,'出勤不足'),
(402,101,7,10,0.70,1,'出勤达标'), (402,103,8,10,0.80,1,'出勤达标'), (402,104,5,10,0.50,0,'出勤不足'), (402,106,4,10,0.40,0,'出勤不足'),
(403,101,7,9,0.78,1,'出勤达标'), (403,103,8,9,0.89,1,'出勤达标'), (403,104,5,9,0.56,0,'出勤不足'), (403,105,3,9,0.33,0,'出勤不足'), (403,106,4,9,0.44,0,'出勤不足');

INSERT INTO exam_record (exam_id, student_id, score, submit_time, status, remark)
VALUES
(401,101,86,'2026-05-10 10:30:00','FINISHED','正常完成'),
(401,102,NULL,NULL,'ABSENT','未参加'),
(401,103,91,'2026-05-10 10:20:00','FINISHED','正常完成'),
(401,105,NULL,NULL,'ABSENT','缺考'),
(402,101,88,'2026-05-12 15:20:00','FINISHED','正常完成'),
(402,103,90,'2026-05-12 15:10:00','FINISHED','正常完成'),
(402,104,68,'2026-05-12 15:35:00','FINISHED','基础薄弱'),
(402,106,NULL,NULL,'ABSENT','缺考'),
(403,101,84,'2026-05-15 10:45:00','FINISHED','正常完成'),
(403,103,93,'2026-05-15 10:40:00','FINISHED','优秀'),
(403,104,72,'2026-05-15 10:50:00','FINISHED','可提升'),
(403,105,NULL,NULL,'ABSENT','缺考'),
(403,106,NULL,NULL,'ABSENT','缺考');

INSERT INTO student_academic (student_id, total_credit, earned_credit, gpa, gpa_color, risk_note)
VALUES
(101,20,16,3.10,'GREEN','稳定低风险'),
(102,20,11,2.00,'YELLOW','存在波动'),
(103,20,18,3.40,'GREEN','学习状态优'),
(104,20,12,2.20,'YELLOW','需加强薄弱课'),
(105,20,9,1.40,'RED','高风险重点干预'),
(106,20,8,1.60,'ORANGE','中高风险');

-- 风险分布：HIGH 2人、MEDIUM 2人、LOW 2人（演示更均衡）
INSERT INTO risk_prediction (id, student_id, course_id, risk_probability, risk_label, risk_level, warning_color, main_reason, model_version, predict_time)
VALUES
(501,101,201,0.2800,'低风险','LOW','GREEN','学习状态稳定','LR-1.1.0','2026-04-01 09:00:00'),
(502,102,201,0.6400,'中风险','MEDIUM','ORANGE','出勤偏低；作业均分偏低','LR-1.1.0','2026-04-01 09:00:00'),
(503,103,202,0.2100,'低风险','LOW','GREEN','学习表现良好','LR-1.1.0','2026-04-01 09:00:00'),
(504,104,203,0.5900,'中风险','MEDIUM','ORANGE','考试均分偏低','LR-1.1.0','2026-04-01 09:00:00'),
(505,105,201,0.9100,'高风险','HIGH','RED','作业提交率低；GPA低于预警区间','LR-1.1.0','2026-04-01 09:00:00'),
(506,106,203,0.8400,'高风险','HIGH','RED','缺交缺考并存；GPA偏低','LR-1.1.0','2026-04-01 09:00:00');

INSERT INTO warning_record (id, student_id, course_id, prediction_id, warning_type, warning_level, warning_content, status)
VALUES
(601,102,201,502,'RISK_WARNING','MEDIUM','系统识别为中风险，请提升出勤与作业质量','OPEN'),
(602,104,203,504,'RISK_WARNING','MEDIUM','考试表现偏弱，建议强化练习','OPEN'),
(603,105,201,505,'GPA_WARNING','HIGH','当前GPA<1.5，学业红色预警','OPEN'),
(604,106,203,506,'RISK_WARNING','HIGH','综合风险高，建议立即干预','OPEN');

INSERT INTO intervention_record (id, student_id, teacher_id, course_id, warning_id, intervention_type, intervention_content, intervention_result)
VALUES
(701,105,1,201,603,'TALK','约谈并制定每周学习计划','学生承诺按时提交作业'),
(702,106,1,203,604,'LEARNING_SUPPORT','安排每周一次专项辅导','已完成第一次辅导');
