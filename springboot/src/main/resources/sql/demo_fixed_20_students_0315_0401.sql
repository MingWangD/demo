-- 固定演示数据（20名学生，时间范围 2026-03-15 至 2026-04-01）
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

INSERT INTO sys_user (id, username, password, name, role, student_no, teacher_no, college, major, class_name, status, avatar) VALUES
(1,'t_zhang','123456','张老师','TEACHER',NULL,'T2026001','计算机学院',NULL,NULL,'ACTIVE',NULL),
(2,'t_li','123456','李老师','TEACHER',NULL,'T2026002','计算机学院',NULL,NULL,'ACTIVE',NULL),
(101,'s_101','123456','王明','STUDENT','20260101',NULL,'计算机学院','软件工程','软工1班','ACTIVE',NULL),
(102,'s_102','123456','刘洋','STUDENT','20260102',NULL,'计算机学院','网络工程','软工2班','ACTIVE',NULL),
(103,'s_103','123456','陈静','STUDENT','20260103',NULL,'计算机学院','计算机科学','网工1班','ACTIVE',NULL),
(104,'s_104','123456','赵峰','STUDENT','20260104',NULL,'计算机学院','数据科学','计科1班','ACTIVE',NULL),
(105,'s_105','123456','孙丽','STUDENT','20260105',NULL,'计算机学院','软件工程','软工1班','ACTIVE',NULL),
(106,'s_106','123456','胡涛','STUDENT','20260106',NULL,'计算机学院','网络工程','软工2班','ACTIVE',NULL),
(107,'s_107','123456','李彤','STUDENT','20260107',NULL,'计算机学院','计算机科学','网工1班','ACTIVE',NULL),
(108,'s_108','123456','周凯','STUDENT','20260108',NULL,'计算机学院','数据科学','计科1班','ACTIVE',NULL),
(109,'s_109','123456','吴倩','STUDENT','20260109',NULL,'计算机学院','软件工程','软工1班','ACTIVE',NULL),
(110,'s_110','123456','郑宇','STUDENT','20260110',NULL,'计算机学院','网络工程','软工2班','ACTIVE',NULL),
(111,'s_111','123456','冯敏','STUDENT','20260111',NULL,'计算机学院','计算机科学','网工1班','ACTIVE',NULL),
(112,'s_112','123456','何磊','STUDENT','20260112',NULL,'计算机学院','数据科学','计科1班','ACTIVE',NULL),
(113,'s_113','123456','高原','STUDENT','20260113',NULL,'计算机学院','软件工程','软工1班','ACTIVE',NULL),
(114,'s_114','123456','梁雪','STUDENT','20260114',NULL,'计算机学院','网络工程','软工2班','ACTIVE',NULL),
(115,'s_115','123456','马超','STUDENT','20260115',NULL,'计算机学院','计算机科学','网工1班','ACTIVE',NULL),
(116,'s_116','123456','唐宁','STUDENT','20260116',NULL,'计算机学院','数据科学','计科1班','ACTIVE',NULL),
(117,'s_117','123456','彭涛','STUDENT','20260117',NULL,'计算机学院','软件工程','软工1班','ACTIVE',NULL),
(118,'s_118','123456','宋佳','STUDENT','20260118',NULL,'计算机学院','网络工程','软工2班','ACTIVE',NULL),
(119,'s_119','123456','谢安','STUDENT','20260119',NULL,'计算机学院','计算机科学','网工1班','ACTIVE',NULL),
(120,'s_120','123456','邹晨','STUDENT','20260120',NULL,'计算机学院','数据科学','计科1班','ACTIVE',NULL);

INSERT INTO course (id, course_name, course_code, teacher_id, credit, semester, total_weeks, attendance_required_count, exam_qualification_rate, description) VALUES
(201,'数据结构','CS2026-DS',1,4.0,'2026-Spring',16,10,0.67,'核心课'),
(202,'数据库原理','CS2026-DB',1,3.0,'2026-Spring',16,10,0.67,'核心课'),
(203,'机器学习导论','CS2026-ML',1,2.0,'2026-Spring',16,9,0.60,'专业选修'),
(204,'计算机网络','CS2026-NET',2,3.0,'2026-Spring',16,9,0.60,'对照课程');

INSERT INTO student_course (student_id, course_id, status) VALUES (101,201,'SELECTED');
INSERT INTO student_course (student_id, course_id, status) VALUES (101,202,'SELECTED');
INSERT INTO student_course (student_id, course_id, status) VALUES (102,201,'SELECTED');
INSERT INTO student_course (student_id, course_id, status) VALUES (102,203,'SELECTED');
INSERT INTO student_course (student_id, course_id, status) VALUES (103,201,'SELECTED');
INSERT INTO student_course (student_id, course_id, status) VALUES (103,202,'SELECTED');
INSERT INTO student_course (student_id, course_id, status) VALUES (104,201,'SELECTED');
INSERT INTO student_course (student_id, course_id, status) VALUES (104,203,'SELECTED');
INSERT INTO student_course (student_id, course_id, status) VALUES (105,201,'SELECTED');
INSERT INTO student_course (student_id, course_id, status) VALUES (105,202,'SELECTED');
INSERT INTO student_course (student_id, course_id, status) VALUES (106,201,'SELECTED');
INSERT INTO student_course (student_id, course_id, status) VALUES (106,203,'SELECTED');
INSERT INTO student_course (student_id, course_id, status) VALUES (107,201,'SELECTED');
INSERT INTO student_course (student_id, course_id, status) VALUES (107,202,'SELECTED');
INSERT INTO student_course (student_id, course_id, status) VALUES (108,201,'SELECTED');
INSERT INTO student_course (student_id, course_id, status) VALUES (108,203,'SELECTED');
INSERT INTO student_course (student_id, course_id, status) VALUES (109,201,'SELECTED');
INSERT INTO student_course (student_id, course_id, status) VALUES (109,202,'SELECTED');
INSERT INTO student_course (student_id, course_id, status) VALUES (110,201,'SELECTED');
INSERT INTO student_course (student_id, course_id, status) VALUES (110,203,'SELECTED');
INSERT INTO student_course (student_id, course_id, status) VALUES (111,201,'SELECTED');
INSERT INTO student_course (student_id, course_id, status) VALUES (111,202,'SELECTED');
INSERT INTO student_course (student_id, course_id, status) VALUES (112,201,'SELECTED');
INSERT INTO student_course (student_id, course_id, status) VALUES (112,203,'SELECTED');
INSERT INTO student_course (student_id, course_id, status) VALUES (113,201,'SELECTED');
INSERT INTO student_course (student_id, course_id, status) VALUES (113,202,'SELECTED');
INSERT INTO student_course (student_id, course_id, status) VALUES (114,201,'SELECTED');
INSERT INTO student_course (student_id, course_id, status) VALUES (114,203,'SELECTED');
INSERT INTO student_course (student_id, course_id, status) VALUES (115,201,'SELECTED');
INSERT INTO student_course (student_id, course_id, status) VALUES (115,202,'SELECTED');
INSERT INTO student_course (student_id, course_id, status) VALUES (116,201,'SELECTED');
INSERT INTO student_course (student_id, course_id, status) VALUES (116,203,'SELECTED');
INSERT INTO student_course (student_id, course_id, status) VALUES (117,201,'SELECTED');
INSERT INTO student_course (student_id, course_id, status) VALUES (117,202,'SELECTED');
INSERT INTO student_course (student_id, course_id, status) VALUES (118,201,'SELECTED');
INSERT INTO student_course (student_id, course_id, status) VALUES (118,203,'SELECTED');
INSERT INTO student_course (student_id, course_id, status) VALUES (119,201,'SELECTED');
INSERT INTO student_course (student_id, course_id, status) VALUES (119,202,'SELECTED');
INSERT INTO student_course (student_id, course_id, status) VALUES (120,201,'SELECTED');
INSERT INTO student_course (student_id, course_id, status) VALUES (120,203,'SELECTED');

INSERT INTO student_attendance (student_id, course_id, attendance_type, attendance_time, week_no, remark) VALUES
(101,201,'LOGIN','2026-03-15 08:10:00',1,'登录自动记录'),
(101,202,'LOGIN','2026-03-19 08:10:00',2,'登录自动记录'),
(101,201,'LOGIN','2026-03-23 08:10:00',3,'登录自动记录'),
(101,202,'LOGIN','2026-03-27 08:10:00',4,'登录自动记录'),
(101,201,'LOGIN','2026-03-31 08:10:00',5,'登录自动记录'),
(102,201,'LOGIN','2026-03-15 09:11:00',1,'登录自动记录'),
(102,203,'LOGIN','2026-03-19 09:11:00',2,'登录自动记录'),
(102,201,'LOGIN','2026-03-23 09:11:00',3,'登录自动记录'),
(102,203,'LOGIN','2026-03-27 09:11:00',4,'登录自动记录'),
(102,201,'LOGIN','2026-03-31 09:11:00',5,'登录自动记录'),
(103,201,'LOGIN','2026-03-15 10:12:00',1,'登录自动记录'),
(103,202,'LOGIN','2026-03-19 10:12:00',2,'登录自动记录'),
(103,201,'LOGIN','2026-03-23 10:12:00',3,'登录自动记录'),
(103,202,'LOGIN','2026-03-27 10:12:00',4,'登录自动记录'),
(103,201,'LOGIN','2026-03-31 10:12:00',5,'登录自动记录'),
(104,201,'LOGIN','2026-03-15 08:13:00',1,'登录自动记录'),
(104,203,'LOGIN','2026-03-19 08:13:00',2,'登录自动记录'),
(104,201,'LOGIN','2026-03-23 08:13:00',3,'登录自动记录'),
(104,203,'LOGIN','2026-03-27 08:13:00',4,'登录自动记录'),
(104,201,'LOGIN','2026-03-31 08:13:00',5,'登录自动记录'),
(105,201,'LOGIN','2026-03-15 09:14:00',1,'登录自动记录'),
(105,202,'LOGIN','2026-03-19 09:14:00',2,'登录自动记录'),
(105,201,'LOGIN','2026-03-23 09:14:00',3,'登录自动记录'),
(105,202,'LOGIN','2026-03-27 09:14:00',4,'登录自动记录'),
(105,201,'LOGIN','2026-03-31 09:14:00',5,'登录自动记录'),
(106,201,'LOGIN','2026-03-15 10:15:00',1,'登录自动记录'),
(106,203,'LOGIN','2026-03-19 10:15:00',2,'登录自动记录'),
(106,201,'LOGIN','2026-03-23 10:15:00',3,'登录自动记录'),
(106,203,'LOGIN','2026-03-27 10:15:00',4,'登录自动记录'),
(106,201,'LOGIN','2026-03-31 10:15:00',5,'登录自动记录'),
(107,201,'LOGIN','2026-03-15 08:16:00',1,'登录自动记录'),
(107,202,'LOGIN','2026-03-19 08:16:00',2,'登录自动记录'),
(107,201,'LOGIN','2026-03-23 08:16:00',3,'登录自动记录'),
(107,202,'LOGIN','2026-03-27 08:16:00',4,'登录自动记录'),
(107,201,'LOGIN','2026-03-31 08:16:00',5,'登录自动记录'),
(108,201,'LOGIN','2026-03-15 09:17:00',1,'登录自动记录'),
(108,203,'LOGIN','2026-03-19 09:17:00',2,'登录自动记录'),
(108,201,'LOGIN','2026-03-23 09:17:00',3,'登录自动记录'),
(108,203,'LOGIN','2026-03-27 09:17:00',4,'登录自动记录'),
(108,201,'LOGIN','2026-03-31 09:17:00',5,'登录自动记录'),
(109,201,'LOGIN','2026-03-15 10:18:00',1,'登录自动记录'),
(109,202,'LOGIN','2026-03-19 10:18:00',2,'登录自动记录'),
(109,201,'LOGIN','2026-03-23 10:18:00',3,'登录自动记录'),
(109,202,'LOGIN','2026-03-27 10:18:00',4,'登录自动记录'),
(109,201,'LOGIN','2026-03-31 10:18:00',5,'登录自动记录'),
(110,201,'LOGIN','2026-03-15 08:19:00',1,'登录自动记录'),
(110,203,'LOGIN','2026-03-19 08:19:00',2,'登录自动记录'),
(110,201,'LOGIN','2026-03-23 08:19:00',3,'登录自动记录'),
(110,203,'LOGIN','2026-03-27 08:19:00',4,'登录自动记录'),
(110,201,'LOGIN','2026-03-31 08:19:00',5,'登录自动记录'),
(111,201,'LOGIN','2026-03-15 09:20:00',1,'登录自动记录'),
(111,202,'LOGIN','2026-03-19 09:20:00',2,'登录自动记录'),
(111,201,'LOGIN','2026-03-23 09:20:00',3,'登录自动记录'),
(111,202,'LOGIN','2026-03-27 09:20:00',4,'登录自动记录'),
(111,201,'LOGIN','2026-03-31 09:20:00',5,'登录自动记录'),
(112,201,'LOGIN','2026-03-15 10:21:00',1,'登录自动记录'),
(112,203,'LOGIN','2026-03-19 10:21:00',2,'登录自动记录'),
(112,201,'LOGIN','2026-03-23 10:21:00',3,'登录自动记录'),
(112,203,'LOGIN','2026-03-27 10:21:00',4,'登录自动记录'),
(112,201,'LOGIN','2026-03-31 10:21:00',5,'登录自动记录'),
(113,201,'LOGIN','2026-03-15 08:22:00',1,'登录自动记录'),
(113,202,'LOGIN','2026-03-19 08:22:00',2,'登录自动记录'),
(113,201,'LOGIN','2026-03-23 08:22:00',3,'登录自动记录'),
(113,202,'LOGIN','2026-03-27 08:22:00',4,'登录自动记录'),
(113,201,'LOGIN','2026-03-31 08:22:00',5,'登录自动记录'),
(114,201,'LOGIN','2026-03-15 09:23:00',1,'登录自动记录'),
(114,203,'LOGIN','2026-03-19 09:23:00',2,'登录自动记录'),
(114,201,'LOGIN','2026-03-23 09:23:00',3,'登录自动记录'),
(114,203,'LOGIN','2026-03-27 09:23:00',4,'登录自动记录'),
(114,201,'LOGIN','2026-03-31 09:23:00',5,'登录自动记录'),
(115,201,'LOGIN','2026-03-15 10:24:00',1,'登录自动记录'),
(115,202,'LOGIN','2026-03-19 10:24:00',2,'登录自动记录'),
(115,201,'LOGIN','2026-03-23 10:24:00',3,'登录自动记录'),
(115,202,'LOGIN','2026-03-27 10:24:00',4,'登录自动记录'),
(115,201,'LOGIN','2026-03-31 10:24:00',5,'登录自动记录'),
(116,201,'LOGIN','2026-03-15 08:25:00',1,'登录自动记录'),
(116,203,'LOGIN','2026-03-19 08:25:00',2,'登录自动记录'),
(116,201,'LOGIN','2026-03-23 08:25:00',3,'登录自动记录'),
(116,203,'LOGIN','2026-03-27 08:25:00',4,'登录自动记录'),
(116,201,'LOGIN','2026-03-31 08:25:00',5,'登录自动记录'),
(117,201,'LOGIN','2026-03-15 09:26:00',1,'登录自动记录'),
(117,202,'LOGIN','2026-03-19 09:26:00',2,'登录自动记录'),
(117,201,'LOGIN','2026-03-23 09:26:00',3,'登录自动记录'),
(117,202,'LOGIN','2026-03-27 09:26:00',4,'登录自动记录'),
(117,201,'LOGIN','2026-03-31 09:26:00',5,'登录自动记录'),
(118,201,'LOGIN','2026-03-15 10:27:00',1,'登录自动记录'),
(118,203,'LOGIN','2026-03-19 10:27:00',2,'登录自动记录'),
(118,201,'LOGIN','2026-03-23 10:27:00',3,'登录自动记录'),
(118,203,'LOGIN','2026-03-27 10:27:00',4,'登录自动记录'),
(118,201,'LOGIN','2026-03-31 10:27:00',5,'登录自动记录'),
(119,201,'LOGIN','2026-03-15 08:28:00',1,'登录自动记录'),
(119,202,'LOGIN','2026-03-19 08:28:00',2,'登录自动记录'),
(119,201,'LOGIN','2026-03-23 08:28:00',3,'登录自动记录'),
(119,202,'LOGIN','2026-03-27 08:28:00',4,'登录自动记录'),
(119,201,'LOGIN','2026-03-31 08:28:00',5,'登录自动记录'),
(120,201,'LOGIN','2026-03-15 09:29:00',1,'登录自动记录'),
(120,203,'LOGIN','2026-03-19 09:29:00',2,'登录自动记录'),
(120,201,'LOGIN','2026-03-23 09:29:00',3,'登录自动记录'),
(120,203,'LOGIN','2026-03-27 09:29:00',4,'登录自动记录'),
(120,201,'LOGIN','2026-03-31 09:29:00',5,'登录自动记录');

INSERT INTO homework (id, course_id, teacher_id, title, content, deadline, total_score) VALUES
(301,201,1,'数据结构作业A','1. 单链表查找（2分）\n2. 栈应用（2分）\n3. 图遍历分析（10分）\n4. 红黑树平衡说明（10分）','2026-03-28 23:59:59',100),
(302,202,1,'数据库作业A','1. 事务隔离（2分）\n2. 索引命中（2分）\n3. SQL优化方案（10分）\n4. 范式设计说明（10分）','2026-03-30 23:59:59',100),
(303,203,1,'机器学习作业A','1. Sigmoid输出区间（2分）\n2. 阈值分类（2分）\n3. 损失函数推导（10分）\n4. 正则化比较（10分）','2026-04-01 23:59:59',100);

INSERT INTO homework_submission (homework_id, student_id, submit_content, submit_time, score, is_late, status, teacher_comment) VALUES
(301,101,'{"objectiveLocked":true,"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["A","C"],"subjectiveAnswers":["答题要点1","答题要点2"]}','2026-03-27 20:00:00',77,0,'GRADED','演示数据'),
(302,101,'{"objectiveLocked":true,"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["A","C"],"subjectiveAnswers":["答题要点1","答题要点2"]}','2026-03-29 20:00:00',78,0,'GRADED','演示数据'),
(301,102,'{"objectiveLocked":true,"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["A","C"],"subjectiveAnswers":["答题要点1","答题要点2"]}','2026-03-27 20:00:00',78,0,'GRADED','演示数据'),
(302,102,'{"objectiveLocked":true,"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["A","C"],"subjectiveAnswers":["答题要点1","答题要点2"]}','2026-03-29 20:00:00',79,0,'GRADED','演示数据'),
(301,103,'{"objectiveLocked":true,"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["A","C"],"subjectiveAnswers":["答题要点1","答题要点2"]}','2026-03-27 20:00:00',79,0,'GRADED','演示数据'),
(302,103,'{"objectiveLocked":true,"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["A","C"],"subjectiveAnswers":["答题要点1","答题要点2"]}','2026-03-29 20:00:00',80,0,'GRADED','演示数据'),
(301,104,'{"objectiveLocked":true,"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["A","C"],"subjectiveAnswers":["答题要点1","答题要点2"]}','2026-03-27 20:00:00',80,0,'GRADED','演示数据'),
(302,104,'{"objectiveLocked":true,"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["A","C"],"subjectiveAnswers":["答题要点1","答题要点2"]}','2026-03-29 20:00:00',81,0,'GRADED','演示数据'),
(301,105,'{"objectiveLocked":true,"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["A","C"],"subjectiveAnswers":["答题要点1","答题要点2"]}','2026-03-27 20:00:00',81,0,'GRADED','演示数据'),
(302,105,NULL,NULL,NULL,0,'MISSING','未提交'),
(301,106,NULL,NULL,NULL,0,'MISSING','未提交'),
(302,106,NULL,NULL,NULL,0,'MISSING','未提交'),
(301,107,NULL,NULL,NULL,0,'MISSING','未提交'),
(302,107,NULL,NULL,NULL,0,'MISSING','未提交'),
(301,108,NULL,NULL,NULL,0,'MISSING','未提交'),
(302,108,'{"objectiveLocked":true,"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["A","C"],"subjectiveAnswers":["答题要点1","答题要点2"]}','2026-03-29 20:00:00',85,0,'GRADED','演示数据'),
(301,109,'{"objectiveLocked":true,"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["A","C"],"subjectiveAnswers":["答题要点1","答题要点2"]}','2026-03-27 20:00:00',85,0,'GRADED','演示数据'),
(302,109,'{"objectiveLocked":true,"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["A","C"],"subjectiveAnswers":["答题要点1","答题要点2"]}','2026-03-29 20:00:00',86,0,'GRADED','演示数据'),
(301,110,'{"objectiveLocked":true,"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["A","C"],"subjectiveAnswers":["答题要点1","答题要点2"]}','2026-03-27 20:00:00',86,0,'GRADED','演示数据'),
(302,110,'{"objectiveLocked":true,"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["A","C"],"subjectiveAnswers":["答题要点1","答题要点2"]}','2026-03-29 20:00:00',87,0,'GRADED','演示数据'),
(301,111,'{"objectiveLocked":true,"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["A","C"],"subjectiveAnswers":["答题要点1","答题要点2"]}','2026-03-27 20:00:00',87,0,'GRADED','演示数据'),
(302,111,'{"objectiveLocked":true,"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["A","C"],"subjectiveAnswers":["答题要点1","答题要点2"]}','2026-03-29 20:00:00',88,0,'GRADED','演示数据'),
(301,112,'{"objectiveLocked":true,"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["A","C"],"subjectiveAnswers":["答题要点1","答题要点2"]}','2026-03-27 20:00:00',88,0,'GRADED','演示数据'),
(302,112,'{"objectiveLocked":true,"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["A","C"],"subjectiveAnswers":["答题要点1","答题要点2"]}','2026-03-29 20:00:00',89,0,'GRADED','演示数据'),
(301,113,'{"objectiveLocked":true,"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["A","C"],"subjectiveAnswers":["答题要点1","答题要点2"]}','2026-03-27 20:00:00',89,0,'GRADED','演示数据'),
(302,113,'{"objectiveLocked":true,"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["A","C"],"subjectiveAnswers":["答题要点1","答题要点2"]}','2026-03-29 20:00:00',90,0,'GRADED','演示数据'),
(301,114,'{"objectiveLocked":true,"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["A","C"],"subjectiveAnswers":["答题要点1","答题要点2"]}','2026-03-27 20:00:00',90,0,'GRADED','演示数据'),
(302,114,'{"objectiveLocked":true,"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["A","C"],"subjectiveAnswers":["答题要点1","答题要点2"]}','2026-03-29 20:00:00',91,0,'GRADED','演示数据'),
(301,115,'{"objectiveLocked":true,"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["A","C"],"subjectiveAnswers":["答题要点1","答题要点2"]}','2026-03-27 20:00:00',91,0,'GRADED','演示数据'),
(302,115,NULL,NULL,NULL,0,'MISSING','未提交'),
(301,116,NULL,NULL,NULL,0,'MISSING','未提交'),
(302,116,NULL,NULL,NULL,0,'MISSING','未提交'),
(301,117,NULL,NULL,NULL,0,'MISSING','未提交'),
(302,117,NULL,NULL,NULL,0,'MISSING','未提交'),
(301,118,NULL,NULL,NULL,0,'MISSING','未提交'),
(302,118,'{"objectiveLocked":true,"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["A","C"],"subjectiveAnswers":["答题要点1","答题要点2"]}','2026-03-29 20:00:00',60,0,'GRADED','演示数据'),
(301,119,'{"objectiveLocked":true,"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["A","C"],"subjectiveAnswers":["答题要点1","答题要点2"]}','2026-03-27 20:00:00',60,0,'GRADED','演示数据'),
(302,119,'{"objectiveLocked":true,"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["A","C"],"subjectiveAnswers":["答题要点1","答题要点2"]}','2026-03-29 20:00:00',61,0,'GRADED','演示数据'),
(301,120,'{"objectiveLocked":true,"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["A","C"],"subjectiveAnswers":["答题要点1","答题要点2"]}','2026-03-27 20:00:00',61,0,'GRADED','演示数据'),
(302,120,'{"objectiveLocked":true,"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["A","C"],"subjectiveAnswers":["答题要点1","答题要点2"]}','2026-03-29 20:00:00',62,0,'GRADED','演示数据');

INSERT INTO exam (id, course_id, teacher_id, exam_name, exam_time, total_score, duration_minutes, description) VALUES
(401,201,1,'数据结构阶段测','2026-04-02 09:00:00',100,120,'1. 复杂度判断（2分）\n2. 队列特性（2分）\n3. 快排分析（10分）\n4. 树结构对比（10分）'),
(402,202,1,'数据库阶段测','2026-04-03 14:00:00',100,90,'1. ACID含义（2分）\n2. JOIN理解（2分）\n3. 执行计划分析（10分）\n4. 索引策略设计（10分）'),
(403,203,1,'机器学习小测','2026-04-04 10:00:00',100,60,'1. 分类阈值（2分）\n2. 过拟合识别（2分）\n3. LR损失推导（10分）\n4. L1/L2比较（10分）');

INSERT INTO exam_qualification (exam_id, student_id, attendance_count, required_count, qualification_rate, is_qualified, reason) VALUES
(401,101,9,10,0.9000,1,'出勤达标，可参加考试'),
(402,101,10,10,1.0000,1,'出勤达标，可参加考试'),
(401,102,10,10,1.0000,1,'出勤达标，可参加考试'),
(403,102,6,9,0.6667,0,'出勤未达标，暂不具备考试资格'),
(401,103,5,10,0.5000,0,'出勤未达标，暂不具备考试资格'),
(402,103,6,10,0.6000,0,'出勤未达标，暂不具备考试资格'),
(401,104,6,10,0.6000,0,'出勤未达标，暂不具备考试资格'),
(403,104,8,9,0.8889,1,'出勤达标，可参加考试'),
(401,105,7,10,0.7000,1,'出勤达标，可参加考试'),
(402,105,8,10,0.8000,1,'出勤达标，可参加考试'),
(401,106,8,10,0.8000,1,'出勤达标，可参加考试'),
(403,106,10,9,1.1111,1,'出勤达标，可参加考试'),
(401,107,9,10,0.9000,1,'出勤达标，可参加考试'),
(402,107,10,10,1.0000,1,'出勤达标，可参加考试'),
(401,108,10,10,1.0000,1,'出勤达标，可参加考试'),
(403,108,6,9,0.6667,0,'出勤未达标，暂不具备考试资格'),
(401,109,5,10,0.5000,0,'出勤未达标，暂不具备考试资格'),
(402,109,6,10,0.6000,0,'出勤未达标，暂不具备考试资格'),
(401,110,6,10,0.6000,0,'出勤未达标，暂不具备考试资格'),
(403,110,8,9,0.8889,1,'出勤达标，可参加考试'),
(401,111,7,10,0.7000,1,'出勤达标，可参加考试'),
(402,111,8,10,0.8000,1,'出勤达标，可参加考试'),
(401,112,8,10,0.8000,1,'出勤达标，可参加考试'),
(403,112,10,9,1.1111,1,'出勤达标，可参加考试'),
(401,113,9,10,0.9000,1,'出勤达标，可参加考试'),
(402,113,10,10,1.0000,1,'出勤达标，可参加考试'),
(401,114,10,10,1.0000,1,'出勤达标，可参加考试'),
(403,114,6,9,0.6667,0,'出勤未达标，暂不具备考试资格'),
(401,115,5,10,0.5000,0,'出勤未达标，暂不具备考试资格'),
(402,115,6,10,0.6000,0,'出勤未达标，暂不具备考试资格'),
(401,116,6,10,0.6000,0,'出勤未达标，暂不具备考试资格'),
(403,116,8,9,0.8889,1,'出勤达标，可参加考试'),
(401,117,7,10,0.7000,1,'出勤达标，可参加考试'),
(402,117,8,10,0.8000,1,'出勤达标，可参加考试'),
(401,118,8,10,0.8000,1,'出勤达标，可参加考试'),
(403,118,10,9,1.1111,1,'出勤达标，可参加考试'),
(401,119,9,10,0.9000,1,'出勤达标，可参加考试'),
(402,119,10,10,1.0000,1,'出勤达标，可参加考试'),
(401,120,10,10,1.0000,1,'出勤达标，可参加考试'),
(403,120,6,9,0.6667,0,'出勤未达标，暂不具备考试资格');

INSERT INTO exam_record (exam_id, student_id, score, submit_time, status, remark) VALUES
(401,101,71,'2026-04-01 18:00:00','FINISHED','{"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["B","D"],"subjectiveAnswers":["主观答1","主观答2"],"message":"系统已自动判客观题得分：4，主观题待教师批改"}'),
(402,101,72,'2026-04-01 18:00:00','FINISHED','{"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["B","D"],"subjectiveAnswers":["主观答1","主观答2"],"message":"系统已自动判客观题得分：4，主观题待教师批改"}'),
(401,102,NULL,NULL,'NOT_JOINED','未参加或无资格'),
(403,102,NULL,NULL,'NOT_JOINED','未参加或无资格'),
(401,103,NULL,NULL,'NOT_JOINED','未参加或无资格'),
(402,103,NULL,NULL,'NOT_JOINED','未参加或无资格'),
(401,104,NULL,NULL,'NOT_JOINED','未参加或无资格'),
(403,104,76,'2026-04-01 18:00:00','FINISHED','{"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["B","D"],"subjectiveAnswers":["主观答1","主观答2"],"message":"系统已自动判客观题得分：4，主观题待教师批改"}'),
(401,105,NULL,NULL,'NOT_JOINED','未参加或无资格'),
(402,105,NULL,NULL,'NOT_JOINED','未参加或无资格'),
(401,106,76,'2026-04-01 18:00:00','FINISHED','{"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["B","D"],"subjectiveAnswers":["主观答1","主观答2"],"message":"系统已自动判客观题得分：4，主观题待教师批改"}'),
(403,106,78,'2026-04-01 18:00:00','FINISHED','{"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["B","D"],"subjectiveAnswers":["主观答1","主观答2"],"message":"系统已自动判客观题得分：4，主观题待教师批改"}'),
(401,107,77,'2026-04-01 18:00:00','FINISHED','{"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["B","D"],"subjectiveAnswers":["主观答1","主观答2"],"message":"系统已自动判客观题得分：4，主观题待教师批改"}'),
(402,107,78,'2026-04-01 18:00:00','FINISHED','{"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["B","D"],"subjectiveAnswers":["主观答1","主观答2"],"message":"系统已自动判客观题得分：4，主观题待教师批改"}'),
(401,108,NULL,NULL,'NOT_JOINED','未参加或无资格'),
(403,108,NULL,NULL,'NOT_JOINED','未参加或无资格'),
(401,109,NULL,NULL,'NOT_JOINED','未参加或无资格'),
(402,109,NULL,NULL,'NOT_JOINED','未参加或无资格'),
(401,110,NULL,NULL,'NOT_JOINED','未参加或无资格'),
(403,110,82,'2026-04-01 18:00:00','FINISHED','{"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["B","D"],"subjectiveAnswers":["主观答1","主观答2"],"message":"系统已自动判客观题得分：4，主观题待教师批改"}'),
(401,111,NULL,NULL,'NOT_JOINED','未参加或无资格'),
(402,111,NULL,NULL,'NOT_JOINED','未参加或无资格'),
(401,112,82,'2026-04-01 18:00:00','FINISHED','{"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["B","D"],"subjectiveAnswers":["主观答1","主观答2"],"message":"系统已自动判客观题得分：4，主观题待教师批改"}'),
(403,112,84,'2026-04-01 18:00:00','FINISHED','{"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["B","D"],"subjectiveAnswers":["主观答1","主观答2"],"message":"系统已自动判客观题得分：4，主观题待教师批改"}'),
(401,113,83,'2026-04-01 18:00:00','FINISHED','{"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["B","D"],"subjectiveAnswers":["主观答1","主观答2"],"message":"系统已自动判客观题得分：4，主观题待教师批改"}'),
(402,113,84,'2026-04-01 18:00:00','FINISHED','{"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["B","D"],"subjectiveAnswers":["主观答1","主观答2"],"message":"系统已自动判客观题得分：4，主观题待教师批改"}'),
(401,114,NULL,NULL,'NOT_JOINED','未参加或无资格'),
(403,114,NULL,NULL,'NOT_JOINED','未参加或无资格'),
(401,115,NULL,NULL,'NOT_JOINED','未参加或无资格'),
(402,115,NULL,NULL,'NOT_JOINED','未参加或无资格'),
(401,116,NULL,NULL,'NOT_JOINED','未参加或无资格'),
(403,116,88,'2026-04-01 18:00:00','FINISHED','{"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["B","D"],"subjectiveAnswers":["主观答1","主观答2"],"message":"系统已自动判客观题得分：4，主观题待教师批改"}'),
(401,117,NULL,NULL,'NOT_JOINED','未参加或无资格'),
(402,117,NULL,NULL,'NOT_JOINED','未参加或无资格'),
(401,118,88,'2026-04-01 18:00:00','FINISHED','{"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["B","D"],"subjectiveAnswers":["主观答1","主观答2"],"message":"系统已自动判客观题得分：4，主观题待教师批改"}'),
(403,118,90,'2026-04-01 18:00:00','FINISHED','{"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["B","D"],"subjectiveAnswers":["主观答1","主观答2"],"message":"系统已自动判客观题得分：4，主观题待教师批改"}'),
(401,119,89,'2026-04-01 18:00:00','FINISHED','{"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["B","D"],"subjectiveAnswers":["主观答1","主观答2"],"message":"系统已自动判客观题得分：4，主观题待教师批改"}'),
(402,119,90,'2026-04-01 18:00:00','FINISHED','{"objectiveAnswered":2,"objectiveScore":4,"objectiveDetail":["B","D"],"subjectiveAnswers":["主观答1","主观答2"],"message":"系统已自动判客观题得分：4，主观题待教师批改"}'),
(401,120,NULL,NULL,'NOT_JOINED','未参加或无资格'),
(403,120,NULL,NULL,'NOT_JOINED','未参加或无资格');

INSERT INTO student_academic (student_id, total_credit, earned_credit, gpa, gpa_color, risk_note) VALUES
(101,20,8,3.00,'GREEN','演示数据'),
(102,20,9,2.20,'YELLOW','演示数据'),
(103,20,10,1.60,'ORANGE','演示数据'),
(104,20,11,3.30,'GREEN','演示数据'),
(105,20,12,2.10,'YELLOW','演示数据'),
(106,20,13,1.60,'ORANGE','演示数据'),
(107,20,14,3.10,'GREEN','演示数据'),
(108,20,15,2.40,'YELLOW','演示数据'),
(109,20,16,1.60,'ORANGE','演示数据'),
(110,20,17,3.40,'GREEN','演示数据'),
(111,20,18,2.30,'YELLOW','演示数据'),
(112,20,19,1.60,'ORANGE','演示数据'),
(113,20,8,3.20,'GREEN','演示数据'),
(114,20,9,2.20,'YELLOW','演示数据'),
(115,20,10,1.60,'ORANGE','演示数据'),
(116,20,11,3.00,'GREEN','演示数据'),
(117,20,12,2.10,'YELLOW','演示数据'),
(118,20,13,1.60,'ORANGE','演示数据'),
(119,20,14,3.30,'GREEN','演示数据'),
(120,20,15,2.40,'YELLOW','演示数据');

INSERT INTO risk_prediction (id, student_id, course_id, risk_probability, risk_label, risk_level, warning_color, main_reason, model_version, predict_time) VALUES
(700,101,201,0.2500,'低风险','LOW','GREEN','学习状态稳定','LR-1.1.0','2026-04-01 09:00:00'),
(701,102,203,0.5800,'中风险','MEDIUM','ORANGE','出勤与作业表现一般','LR-1.1.0','2026-04-01 09:00:00'),
(702,103,201,0.8600,'高风险','HIGH','RED','作业提交率低；GPA偏低','LR-1.1.0','2026-04-01 09:00:00'),
(703,104,202,0.2500,'低风险','LOW','GREEN','学习状态稳定','LR-1.1.0','2026-04-01 09:00:00'),
(704,105,201,0.5800,'中风险','MEDIUM','ORANGE','出勤与作业表现一般','LR-1.1.0','2026-04-01 09:00:00'),
(705,106,203,0.8600,'高风险','HIGH','RED','作业提交率低；GPA偏低','LR-1.1.0','2026-04-01 09:00:00'),
(706,107,201,0.2500,'低风险','LOW','GREEN','学习状态稳定','LR-1.1.0','2026-04-01 09:00:00'),
(707,108,203,0.5800,'中风险','MEDIUM','ORANGE','出勤与作业表现一般','LR-1.1.0','2026-04-01 09:00:00'),
(708,109,201,0.8600,'高风险','HIGH','RED','作业提交率低；GPA偏低','LR-1.1.0','2026-04-01 09:00:00'),
(709,110,202,0.2500,'低风险','LOW','GREEN','学习状态稳定','LR-1.1.0','2026-04-01 09:00:00'),
(710,111,201,0.5800,'中风险','MEDIUM','ORANGE','出勤与作业表现一般','LR-1.1.0','2026-04-01 09:00:00'),
(711,112,203,0.8600,'高风险','HIGH','RED','作业提交率低；GPA偏低','LR-1.1.0','2026-04-01 09:00:00'),
(712,113,201,0.2500,'低风险','LOW','GREEN','学习状态稳定','LR-1.1.0','2026-04-01 09:00:00'),
(713,114,203,0.5800,'中风险','MEDIUM','ORANGE','出勤与作业表现一般','LR-1.1.0','2026-04-01 09:00:00'),
(714,115,201,0.8600,'高风险','HIGH','RED','作业提交率低；GPA偏低','LR-1.1.0','2026-04-01 09:00:00'),
(715,116,202,0.2500,'低风险','LOW','GREEN','学习状态稳定','LR-1.1.0','2026-04-01 09:00:00'),
(716,117,201,0.5800,'中风险','MEDIUM','ORANGE','出勤与作业表现一般','LR-1.1.0','2026-04-01 09:00:00'),
(717,118,203,0.8600,'高风险','HIGH','RED','作业提交率低；GPA偏低','LR-1.1.0','2026-04-01 09:00:00'),
(718,119,201,0.2500,'低风险','LOW','GREEN','学习状态稳定','LR-1.1.0','2026-04-01 09:00:00'),
(719,120,203,0.5800,'中风险','MEDIUM','ORANGE','出勤与作业表现一般','LR-1.1.0','2026-04-01 09:00:00');

INSERT INTO warning_record (id, student_id, course_id, prediction_id, warning_type, warning_level, warning_content, status) VALUES
(901,102,203,701,'RISK_WARNING','MEDIUM','出勤与作业表现一般','OPEN'),
(902,103,201,702,'RISK_WARNING','HIGH','作业提交率低；GPA偏低','OPEN'),
(904,105,201,704,'RISK_WARNING','MEDIUM','出勤与作业表现一般','OPEN'),
(905,106,203,705,'RISK_WARNING','HIGH','作业提交率低；GPA偏低','OPEN'),
(907,108,203,707,'RISK_WARNING','MEDIUM','出勤与作业表现一般','OPEN'),
(908,109,201,708,'RISK_WARNING','HIGH','作业提交率低；GPA偏低','OPEN'),
(910,111,201,710,'RISK_WARNING','MEDIUM','出勤与作业表现一般','OPEN'),
(911,112,203,711,'RISK_WARNING','HIGH','作业提交率低；GPA偏低','OPEN'),
(913,114,203,713,'RISK_WARNING','MEDIUM','出勤与作业表现一般','OPEN'),
(914,115,201,714,'RISK_WARNING','HIGH','作业提交率低；GPA偏低','OPEN'),
(916,117,201,716,'RISK_WARNING','MEDIUM','出勤与作业表现一般','OPEN'),
(917,118,203,717,'RISK_WARNING','HIGH','作业提交率低；GPA偏低','OPEN'),
(919,120,203,719,'RISK_WARNING','MEDIUM','出勤与作业表现一般','OPEN');
