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
(104, 's_zhao', '123456', '赵峰', 'STUDENT', '20260004', NULL, '计算机学院', '软件工程', '软工2班', 'ACTIVE', NULL),
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

INSERT INTO student_attendance (student_id, course_id, attendance_type, attendance_time, week_no, remark)
VALUES
(101, 201, 'LOGIN', '2026-03-01 08:10:00', 1, '系统登录'),
(101, 201, 'LOGIN', '2026-03-08 08:11:00', 2, '系统登录'),
(101, 201, 'LOGIN', '2026-03-15 08:09:00', 3, '系统登录'),
(101, 201, 'LOGIN', '2026-03-22 08:15:00', 4, '系统登录'),
(101, 201, 'LOGIN', '2026-03-29 08:16:00', 5, '系统登录'),
(102, 201, 'LOGIN', '2026-03-01 08:17:00', 1, '系统登录'),
(102, 201, 'LOGIN', '2026-03-08 08:20:00', 2, '系统登录'),
(103, 201, 'LOGIN', '2026-03-01 08:05:00', 1, '系统登录'),
(103, 201, 'LOGIN', '2026-03-08 08:05:00', 2, '系统登录'),
(103, 201, 'LOGIN', '2026-03-15 08:05:00', 3, '系统登录'),
(103, 201, 'LOGIN', '2026-03-22 08:05:00', 4, '系统登录'),
(103, 201, 'LOGIN', '2026-03-29 08:05:00', 5, '系统登录'),
(104, 202, 'LOGIN', '2026-03-01 10:05:00', 1, '系统登录'),
(104, 202, 'LOGIN', '2026-03-08 10:05:00', 2, '系统登录'),
(105, 201, 'LOGIN', '2026-03-01 08:40:00', 1, '系统登录'),
(105, 201, 'LOGIN', '2026-03-08 08:40:00', 2, '系统登录'),
(105, 201, 'LOGIN', '2026-03-15 08:40:00', 3, '系统登录'),
(106, 201, 'LOGIN', '2026-03-01 08:50:00', 1, '系统登录');

INSERT INTO homework (id, course_id, teacher_id, title, content, deadline, total_score)
VALUES
(301, 201, 1, '链表与栈练习', '完成链表逆置与括号匹配题', '2026-03-20 23:59:59', 100),
(302, 202, 1, 'SQL多表查询', '完成三张表联查与聚合统计', '2026-03-22 23:59:59', 100),
(303, 203, 2, '逻辑回归推导', '提交逻辑回归损失函数推导报告', '2026-03-25 23:59:59', 100);

INSERT INTO homework_submission (homework_id, student_id, submit_content, submit_time, score, is_late, status, teacher_comment)
VALUES
(301, 101, '已提交代码与报告', '2026-03-19 20:00:00', 92, 0, 'GRADED', '完成度高'),
(301, 102, '已提交代码', '2026-03-21 10:00:00', 76, 1, 'GRADED', '有迟交'),
(301, 103, '完整报告', '2026-03-20 18:00:00', 88, 0, 'GRADED', '较好'),
(301, 105, NULL, NULL, NULL, 0, 'MISSING', '缺交'),
(302, 101, 'SQL脚本', '2026-03-21 19:00:00', 90, 0, 'GRADED', '语句规范'),
(302, 103, 'SQL脚本+截图', '2026-03-22 20:00:00', 85, 0, 'GRADED', '不错'),
(302, 104, 'SQL脚本', '2026-03-23 11:00:00', 70, 1, 'GRADED', '迟交'),
(303, 102, '推导笔记', '2026-03-24 21:00:00', 80, 0, 'GRADED', '思路清晰'),
(303, 103, '推导报告', '2026-03-25 20:00:00', 95, 0, 'GRADED', '优秀'),
(303, 106, NULL, NULL, NULL, 0, 'MISSING', '缺交');

INSERT INTO exam (id, course_id, teacher_id, exam_name, exam_time, total_score, duration_minutes, description)
VALUES
(401, 201, 1, '数据结构期中', '2026-04-10 09:00:00', 100, 120, '闭卷考试'),
(402, 202, 1, '数据库阶段测试', '2026-04-12 14:00:00', 100, 90, '机考'),
(403, 203, 2, '机器学习小测', '2026-04-15 10:00:00', 100, 60, '课堂测验');

INSERT INTO exam_qualification (exam_id, student_id, attendance_count, required_count, qualification_rate, is_qualified, reason)
VALUES
(401, 101, 5, 10, 0.50, 0, '出勤次数不足，暂不具备考试资格'),
(401, 102, 2, 10, 0.20, 0, '出勤严重不足'),
(401, 103, 5, 10, 0.50, 0, '出勤次数不足，建议补足'),
(401, 105, 3, 10, 0.30, 0, '出勤不足'),
(402, 101, 4, 10, 0.40, 0, '出勤不足'),
(402, 104, 2, 10, 0.20, 0, '出勤不足'),
(403, 102, 2, 9, 0.22, 0, '出勤不足'),
(403, 103, 4, 9, 0.44, 0, '出勤不足'),
(403, 106, 1, 9, 0.11, 0, '出勤不足');

INSERT INTO exam_record (exam_id, student_id, score, submit_time, status, remark)
VALUES
(401, 101, NULL, NULL, 'NOT_JOINED', '资格未通过'),
(401, 102, NULL, NULL, 'ABSENT', '未参加'),
(401, 103, 84, '2026-04-10 10:40:00', 'FINISHED', '正常完成'),
(402, 101, 87, '2026-04-12 15:20:00', 'FINISHED', '正常完成'),
(402, 104, 66, '2026-04-12 15:30:00', 'FINISHED', '基础薄弱'),
(403, 102, 72, '2026-04-15 10:55:00', 'FINISHED', '正常完成'),
(403, 103, 93, '2026-04-15 10:50:00', 'FINISHED', '表现优秀'),
(403, 106, NULL, NULL, 'ABSENT', '缺考');

INSERT INTO student_academic (student_id, total_credit, earned_credit, gpa, gpa_color, risk_note)
VALUES
(101, 20, 14, 2.60, 'GREEN', '总体正常，注意稳定出勤'),
(102, 20, 10, 1.90, 'ORANGE', '成绩波动，作业提交率偏低'),
(103, 20, 18, 3.20, 'GREEN', '学习状态良好'),
(104, 20, 12, 2.20, 'YELLOW', '需提高数据库课程成绩'),
(105, 20, 9, 1.40, 'RED', '高风险，建议重点干预'),
(106, 20, 8, 1.30, 'RED', '高风险，存在缺考与缺交');

INSERT INTO student_feature (student_id, course_id, attendance_count, attendance_rate, homework_submit_rate, homework_avg_score, exam_avg_score, earned_credit, gpa, missing_homework_count, absent_exam_count, recent_risk_trend, feature_date)
VALUES
(101, 201, 5, 0.50, 1.00, 91.00, 87.00, 14, 2.60, 0, 0, 'DOWN', '2026-04-01'),
(102, 201, 2, 0.20, 0.50, 78.00, 72.00, 10, 1.90, 1, 0, 'UP', '2026-04-01'),
(103, 201, 5, 0.50, 1.00, 89.00, 88.50, 18, 3.20, 0, 0, 'DOWN', '2026-04-01'),
(104, 202, 2, 0.20, 1.00, 70.00, 66.00, 12, 2.20, 0, 0, 'UP', '2026-04-01'),
(105, 201, 3, 0.30, 0.00, 0.00, 0.00, 9, 1.40, 1, 1, 'UP', '2026-04-01'),
(106, 203, 1, 0.11, 0.00, 0.00, 0.00, 8, 1.30, 1, 1, 'UP', '2026-04-01');

INSERT INTO risk_prediction (id, student_id, course_id, risk_probability, risk_label, risk_level, warning_color, main_reason, model_version, predict_time)
VALUES
(501, 101, 201, 0.3200, '低风险', 'LOW', 'GREEN', '学习表现整体稳定', 'LR-1.0.0', '2026-04-01 09:00:00'),
(502, 102, 201, 0.7600, '中风险', 'MEDIUM', 'ORANGE', '出勤不足且作业提交率偏低', 'LR-1.0.0', '2026-04-01 09:00:00'),
(503, 103, 201, 0.2200, '低风险', 'LOW', 'GREEN', '作业考试表现良好', 'LR-1.0.0', '2026-04-01 09:00:00'),
(504, 104, 202, 0.5800, '中风险', 'MEDIUM', 'ORANGE', '出勤率偏低，考试分数偏低', 'LR-1.0.0', '2026-04-01 09:00:00'),
(505, 105, 201, 0.9100, '高风险', 'HIGH', 'RED', 'GPA过低并存在缺交缺考', 'LR-1.0.0', '2026-04-01 09:00:00'),
(506, 106, 203, 0.9500, '高风险', 'HIGH', 'RED', '出勤严重不足且存在缺考', 'LR-1.0.0', '2026-04-01 09:00:00');

INSERT INTO warning_record (id, student_id, course_id, prediction_id, warning_type, warning_level, warning_content, status)
VALUES
(601, 102, 201, 502, 'RISK_WARNING', 'MEDIUM', '系统识别为中风险，请尽快提升出勤和作业提交率', 'OPEN'),
(602, 104, 202, 504, 'RISK_WARNING', 'MEDIUM', '数据库课程存在中风险，建议补课与练习', 'OPEN'),
(603, 105, 201, 505, 'GPA_WARNING', 'HIGH', '当前GPA<1.5，学业预警（红色）', 'OPEN'),
(604, 105, 201, 505, 'RISK_WARNING', 'HIGH', '逻辑回归预测高风险，请教师尽快干预', 'OPEN'),
(605, 106, 203, 506, 'EXAM_WARNING', 'HIGH', '存在缺考行为，且综合风险高', 'OPEN');

INSERT INTO intervention_record (student_id, teacher_id, course_id, warning_id, intervention_type, intervention_content, intervention_result)
VALUES
(105, 1, 201, 603, 'TALK', '约谈学生并制定每周学习计划', '学生承诺按时提交作业'),
(105, 1, 201, 604, 'PARENT_CONTACT', '联系家长同步学业风险情况', '家长表示配合监督'),
(106, 2, 203, 605, 'LEARNING_SUPPORT', '安排机器学习课程一对一辅导', '已完成第一次辅导');
