CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `username` VARCHAR(64) NOT NULL UNIQUE,
  `password` VARCHAR(128) NOT NULL,
  `name` VARCHAR(64) NOT NULL,
  `role` VARCHAR(20) NOT NULL COMMENT 'STUDENT/TEACHER/ADMIN',
  `student_no` VARCHAR(32) NULL,
  `teacher_no` VARCHAR(32) NULL,
  `college` VARCHAR(100) NULL,
  `major` VARCHAR(100) NULL,
  `class_name` VARCHAR(100) NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  `avatar` VARCHAR(255) NULL,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `course` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `course_name` VARCHAR(120) NOT NULL,
  `course_code` VARCHAR(64) NOT NULL UNIQUE,
  `teacher_id` BIGINT NOT NULL,
  `credit` DECIMAL(4,1) NOT NULL DEFAULT 0,
  `semester` VARCHAR(32) NOT NULL,
  `total_weeks` INT NOT NULL DEFAULT 16,
  `attendance_required_count` INT NOT NULL DEFAULT 10,
  `exam_qualification_rate` DECIMAL(5,2) NOT NULL DEFAULT 0.67,
  `description` VARCHAR(500) NULL,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `fk_course_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `student_course` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `student_id` BIGINT NOT NULL,
  `course_id` BIGINT NOT NULL,
  `select_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` VARCHAR(20) NOT NULL DEFAULT 'BOUND',
  UNIQUE KEY `uk_student_course` (`student_id`, `course_id`),
  CONSTRAINT `fk_student_course_student` FOREIGN KEY (`student_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `fk_student_course_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `student_attendance` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `student_id` BIGINT NOT NULL,
  `course_id` BIGINT NOT NULL,
  `attendance_type` VARCHAR(20) NOT NULL COMMENT 'MANUAL/IMPORT',
  `attendance_time` DATETIME NOT NULL,
  `week_no` INT NULL,
  `remark` TEXT NULL,
  CONSTRAINT `fk_attendance_student` FOREIGN KEY (`student_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `fk_attendance_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `homework` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `course_id` BIGINT NOT NULL,
  `teacher_id` BIGINT NOT NULL,
  `title` VARCHAR(200) NOT NULL,
  `content` TEXT NULL,
  `deadline` DATETIME NOT NULL,
  `total_score` DECIMAL(5,2) NOT NULL DEFAULT 100,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `fk_homework_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`),
  CONSTRAINT `fk_homework_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `homework_submission` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `homework_id` BIGINT NOT NULL,
  `student_id` BIGINT NOT NULL,
  `submit_content` TEXT NULL,
  `submit_time` DATETIME NULL,
  `score` DECIMAL(5,2) NULL,
  `is_late` TINYINT(1) NOT NULL DEFAULT 0,
  `status` VARCHAR(20) NOT NULL COMMENT 'SUBMITTED/GRADED/MISSING',
  `teacher_comment` VARCHAR(500) NULL,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_homework_student` (`homework_id`, `student_id`),
  CONSTRAINT `fk_hw_sub_homework` FOREIGN KEY (`homework_id`) REFERENCES `homework` (`id`),
  CONSTRAINT `fk_hw_sub_student` FOREIGN KEY (`student_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `exam` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `course_id` BIGINT NOT NULL,
  `teacher_id` BIGINT NOT NULL,
  `exam_name` VARCHAR(120) NOT NULL,
  `exam_type` VARCHAR(20) NOT NULL COMMENT 'MIDTERM/FINAL',
  `exam_time` DATETIME NOT NULL,
  `total_score` DECIMAL(5,2) NOT NULL DEFAULT 100,
  `duration_minutes` INT NOT NULL DEFAULT 120,
  `description` TEXT NULL,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_exam_course_type` (`course_id`, `exam_type`),
  CONSTRAINT `fk_exam_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`),
  CONSTRAINT `fk_exam_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `exam_qualification` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `exam_id` BIGINT NOT NULL,
  `student_id` BIGINT NOT NULL,
  `attendance_count` INT NOT NULL DEFAULT 0,
  `required_count` INT NOT NULL DEFAULT 0,
  `qualification_rate` DECIMAL(6,4) NOT NULL DEFAULT 0,
  `is_qualified` TINYINT(1) NOT NULL DEFAULT 0,
  `reason` VARCHAR(500) NULL,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_exam_qualification` (`exam_id`, `student_id`),
  CONSTRAINT `fk_exam_qual_exam` FOREIGN KEY (`exam_id`) REFERENCES `exam` (`id`),
  CONSTRAINT `fk_exam_qual_student` FOREIGN KEY (`student_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `exam_record` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `exam_id` BIGINT NOT NULL,
  `student_id` BIGINT NOT NULL,
  `score` DECIMAL(5,2) NULL,
  `submit_time` DATETIME NULL,
  `status` VARCHAR(20) NOT NULL COMMENT 'NOT_JOINED/JOINED/FINISHED/ABSENT',
  `remark` VARCHAR(255) NULL,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_exam_student` (`exam_id`, `student_id`),
  CONSTRAINT `fk_exam_record_exam` FOREIGN KEY (`exam_id`) REFERENCES `exam` (`id`),
  CONSTRAINT `fk_exam_record_student` FOREIGN KEY (`student_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `student_academic` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `student_id` BIGINT NOT NULL UNIQUE,
  `total_credit` DECIMAL(6,2) NOT NULL DEFAULT 0,
  `earned_credit` DECIMAL(6,2) NOT NULL DEFAULT 0,
  `gpa` DECIMAL(3,2) NOT NULL DEFAULT 0,
  `gpa_color` VARCHAR(20) NOT NULL COMMENT 'RED/ORANGE/YELLOW/GREEN',
  `risk_note` VARCHAR(500) NULL,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `fk_student_academic_student` FOREIGN KEY (`student_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `student_feature` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `student_id` BIGINT NOT NULL,
  `course_id` BIGINT NULL,
  `attendance_count` INT NOT NULL DEFAULT 0,
  `attendance_rate` DECIMAL(6,4) NOT NULL DEFAULT 0,
  `homework_submit_rate` DECIMAL(6,4) NOT NULL DEFAULT 0,
  `homework_avg_score` DECIMAL(5,2) NOT NULL DEFAULT 0,
  `exam_avg_score` DECIMAL(5,2) NOT NULL DEFAULT 0,
  `earned_credit` DECIMAL(6,2) NOT NULL DEFAULT 0,
  `gpa` DECIMAL(3,2) NOT NULL DEFAULT 0,
  `missing_homework_count` INT NOT NULL DEFAULT 0,
  `absent_exam_count` INT NOT NULL DEFAULT 0,
  `recent_risk_trend` VARCHAR(30) NULL,
  `feature_date` DATE NOT NULL,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `fk_student_feature_student` FOREIGN KEY (`student_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `fk_student_feature_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `risk_prediction` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `student_id` BIGINT NOT NULL,
  `course_id` BIGINT NULL,
  `risk_probability` DECIMAL(6,4) NOT NULL,
  `risk_label` VARCHAR(100) NOT NULL,
  `risk_level` VARCHAR(20) NOT NULL COMMENT 'HIGH/MEDIUM/LOW',
  `warning_color` VARCHAR(20) NOT NULL COMMENT 'RED/ORANGE/GREEN',
  `main_reason` VARCHAR(500) NULL,
  `model_version` VARCHAR(40) NOT NULL,
  `predict_time` DATETIME NOT NULL,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `fk_prediction_student` FOREIGN KEY (`student_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `fk_prediction_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `warning_record` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `student_id` BIGINT NOT NULL,
  `course_id` BIGINT NULL,
  `prediction_id` BIGINT NULL,
  `warning_type` VARCHAR(30) NOT NULL COMMENT 'GPA_WARNING/RISK_WARNING/ATTENDANCE_WARNING/EXAM_WARNING',
  `warning_level` VARCHAR(20) NOT NULL,
  `warning_content` VARCHAR(500) NOT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'OPEN',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `fk_warning_student` FOREIGN KEY (`student_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `fk_warning_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`),
  CONSTRAINT `fk_warning_prediction` FOREIGN KEY (`prediction_id`) REFERENCES `risk_prediction` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `intervention_record` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `student_id` BIGINT NOT NULL,
  `teacher_id` BIGINT NOT NULL,
  `course_id` BIGINT NULL,
  `warning_id` BIGINT NOT NULL,
  `intervention_type` VARCHAR(50) NOT NULL,
  `intervention_content` VARCHAR(1000) NOT NULL,
  `intervention_result` VARCHAR(500) NULL,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `fk_intervention_student` FOREIGN KEY (`student_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `fk_intervention_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `fk_intervention_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`),
  CONSTRAINT `fk_intervention_warning` FOREIGN KEY (`warning_id`) REFERENCES `warning_record` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
