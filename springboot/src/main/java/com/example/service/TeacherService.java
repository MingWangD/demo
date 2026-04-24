package com.example.service;

import com.example.algorithm.FeatureExtractor;
import com.example.algorithm.RiskPredictor;
import com.example.entity.Course;
import com.example.entity.Exam;
import com.example.entity.ExamRecord;
import com.example.entity.Homework;
import com.example.entity.HomeworkSubmission;
import com.example.entity.RiskPrediction;
import com.example.entity.StudentAcademic;
import com.example.entity.StudentAttendance;
import com.example.entity.StudentCourse;
import com.example.entity.StudentFeature;
import com.example.entity.SysUser;
import com.example.exception.CustomException;
import com.example.mapper.CourseMapper;
import com.example.mapper.ExamMapper;
import com.example.mapper.ExamRecordMapper;
import com.example.mapper.HomeworkMapper;
import com.example.mapper.HomeworkSubmissionMapper;
import com.example.mapper.RiskPredictionMapper;
import com.example.mapper.StudentAcademicMapper;
import com.example.mapper.StudentAttendanceMapper;
import com.example.mapper.StudentCourseMapper;
import com.example.mapper.SysUserMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TeacherService {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Resource private CourseMapper courseMapper;
    @Resource private StudentCourseMapper studentCourseMapper;
    @Resource private SysUserMapper sysUserMapper;
    @Resource private StudentAcademicMapper studentAcademicMapper;
    @Resource private RiskPredictionMapper riskPredictionMapper;
    @Resource private StudentAttendanceMapper studentAttendanceMapper;
    @Resource private HomeworkMapper homeworkMapper;
    @Resource private HomeworkSubmissionMapper homeworkSubmissionMapper;
    @Resource private ExamMapper examMapper;
    @Resource private ExamRecordMapper examRecordMapper;
    @Resource private FeatureExtractor featureExtractor;
    @Resource private RiskPredictor riskPredictor;
    @Resource private ExamService examService;

    public List<Course> courseList(Long teacherId) {
        Course query = new Course();
        query.setTeacherId(teacherId);
        return courseMapper.selectAll(query);
    }

    public List<Map<String, Object>> courseStudents(Long teacherId, Long courseId) {
        validateCourseOwner(courseId, teacherId);
        StudentCourse query = new StudentCourse();
        query.setCourseId(courseId);
        return studentCourseMapper.selectAll(query).stream()
                .map(item -> {
                    SysUser student = sysUserMapper.selectById(item.getStudentId());
                    if (student == null) {
                        return null;
                    }
                    Map<String, Object> map = new HashMap<>();
                    map.put("studentId", student.getId());
                    map.put("studentName", student.getName());
                    map.put("studentNo", student.getStudentNo());
                    return map;
                })
                .filter(Objects::nonNull)
                .toList();
    }

    public Map<String, Object> studentDetail(Long teacherId, Long studentId, Long courseId) {
        if (courseId != null) {
            validateCourseOwner(courseId, teacherId);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("student", sysUserMapper.selectById(studentId));

        StudentAttendance attendanceQuery = new StudentAttendance();
        attendanceQuery.setStudentId(studentId);
        attendanceQuery.setCourseId(courseId);
        int attendanceCount = studentAttendanceMapper.selectAll(attendanceQuery).size();

        Homework homeworkQuery = new Homework();
        homeworkQuery.setCourseId(courseId);
        List<Long> homeworkIds = homeworkMapper.selectAll(homeworkQuery).stream().map(Homework::getId).toList();
        List<HomeworkSubmission> submissions = homeworkSubmissionMapper.selectAll(new HomeworkSubmission()).stream()
                .filter(item -> studentId.equals(item.getStudentId()) && homeworkIds.contains(item.getHomeworkId()))
                .toList();

        StudentAcademic academicQuery = new StudentAcademic();
        academicQuery.setStudentId(studentId);
        StudentAcademic academic = studentAcademicMapper.selectAll(academicQuery).stream().findFirst().orElse(null);

        RiskPrediction riskQuery = new RiskPrediction();
        riskQuery.setStudentId(studentId);
        if (courseId != null) {
            riskQuery.setCourseId(courseId);
        }
        RiskPrediction latestRisk = riskPredictionMapper.selectAll(riskQuery).stream().findFirst().orElse(null);
        if (latestRisk == null && courseId != null) {
            RiskPrediction fallbackQuery = new RiskPrediction();
            fallbackQuery.setStudentId(studentId);
            latestRisk = riskPredictionMapper.selectAll(fallbackQuery).stream().findFirst().orElse(null);
        }

        BigDecimal courseFinalScore = courseId == null ? BigDecimal.ZERO : examService.calculateCourseFinalScore(studentId, courseId);
        result.put("attendanceCount", attendanceCount);
        result.put("homeworkSubmitRate", homeworkIds.isEmpty() ? 0D : (double) submissions.size() / homeworkIds.size());
        result.put("homeworkAvgScore", submissions.stream().map(HomeworkSubmission::getScore).filter(Objects::nonNull).mapToDouble(BigDecimal::doubleValue).average().orElse(0D));
        result.put("examAvgScore", courseFinalScore == null ? BigDecimal.ZERO : courseFinalScore);
        result.put("academic", academic);
        result.put("latestRisk", latestRisk);
        return result;
    }

    public List<StudentAttendance> attendanceList(Long teacherId, Long courseId, Long studentId) {
        validateCourseOwner(courseId, teacherId);
        StudentAttendance query = new StudentAttendance();
        query.setCourseId(courseId);
        query.setStudentId(studentId);
        return studentAttendanceMapper.selectAll(query);
    }

    public Map<String, Object> attendanceStats(Long teacherId, Long courseId) {
        Course course = validateCourseOwner(courseId, teacherId);
        int requiredCount = Optional.ofNullable(course.getAttendanceRequiredCount()).orElse(0);
        int minQualifiedCount = (int) Math.ceil(requiredCount * 2D / 3D);

        StudentCourse query = new StudentCourse();
        query.setCourseId(courseId);
        List<Map<String, Object>> students = studentCourseMapper.selectAll(query).stream()
                .map(item -> {
                    SysUser student = sysUserMapper.selectById(item.getStudentId());
                    if (student == null) {
                        return null;
                    }
                    StudentAttendance attendanceQuery = new StudentAttendance();
                    attendanceQuery.setCourseId(courseId);
                    attendanceQuery.setStudentId(item.getStudentId());
                    int attendanceCount = studentAttendanceMapper.selectAll(attendanceQuery).size();

                    Map<String, Object> map = new HashMap<>();
                    map.put("studentId", student.getId());
                    map.put("studentName", student.getName());
                    map.put("studentNo", student.getStudentNo());
                    map.put("attendanceCount", attendanceCount);
                    map.put("requiredCount", requiredCount);
                    map.put("minQualifiedCount", minQualifiedCount);
                    map.put("qualified", attendanceCount >= minQualifiedCount);
                    map.put("canTakeExam", attendanceCount >= minQualifiedCount);
                    return map;
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(item -> String.valueOf(item.get("studentNo") == null ? "" : item.get("studentNo"))))
                .toList();

        Map<String, Object> result = new HashMap<>();
        result.put("courseId", course.getId());
        result.put("courseName", course.getCourseName());
        result.put("requiredCount", requiredCount);
        result.put("minQualifiedCount", minQualifiedCount);
        result.put("students", students);
        return result;
    }

    public Map<String, Object> updateAttendanceRequiredCount(Long teacherId, Long courseId, Integer attendanceRequiredCount) {
        if (attendanceRequiredCount == null || attendanceRequiredCount < 0) {
            throw new CustomException("课程总出勤次数不能小于0");
        }
        Course course = validateCourseOwner(courseId, teacherId);
        course.setAttendanceRequiredCount(attendanceRequiredCount);
        course.setUpdateTime(LocalDateTime.now());
        courseMapper.updateById(course);

        StudentCourse query = new StudentCourse();
        query.setCourseId(courseId);
        for (StudentCourse studentCourse : studentCourseMapper.selectAll(query)) {
            refreshAttendanceDerivedData(studentCourse.getStudentId(), courseId);
        }
        return attendanceStats(teacherId, courseId);
    }

    public Map<String, Object> changeAttendanceCount(Long teacherId, Long courseId, Long studentId, Integer delta) {
        if (delta == null || delta == 0) {
            throw new CustomException("出勤次数调整值不能为空");
        }
        validateCourseOwner(courseId, teacherId);
        if (sysUserMapper.selectById(studentId) == null) {
            throw new CustomException("学生不存在");
        }

        StudentAttendance query = new StudentAttendance();
        query.setCourseId(courseId);
        query.setStudentId(studentId);
        List<StudentAttendance> currentRecords = studentAttendanceMapper.selectAll(query);

        if (delta > 0) {
            for (int i = 0; i < delta; i++) {
                StudentAttendance attendance = new StudentAttendance();
                attendance.setCourseId(courseId);
                attendance.setStudentId(studentId);
                attendance.setAttendanceType("MANUAL");
                attendance.setAttendanceTime(LocalDateTime.now().plusSeconds(i));
                attendance.setWeekNo(currentRecords.size() + i + 1);
                attendance.setRemark("教师统计出勤 +1");
                studentAttendanceMapper.insert(attendance);
            }
        } else {
            int removeCount = Math.min(currentRecords.size(), Math.abs(delta));
            for (int i = 0; i < removeCount; i++) {
                studentAttendanceMapper.deleteById(currentRecords.get(i).getId());
            }
        }

        refreshAttendanceDerivedData(studentId, courseId);
        return attendanceStats(teacherId, courseId);
    }

    public StudentAttendance saveAttendance(Long teacherId, StudentAttendance attendance) {
        if (attendance.getCourseId() == null || attendance.getStudentId() == null) {
            throw new CustomException("课程和学生不能为空");
        }
        validateCourseOwner(attendance.getCourseId(), teacherId);
        if (sysUserMapper.selectById(attendance.getStudentId()) == null) {
            throw new CustomException("学生不存在");
        }
        if (attendance.getAttendanceTime() == null) {
            attendance.setAttendanceTime(LocalDateTime.now());
        }
        if (attendance.getAttendanceType() == null || attendance.getAttendanceType().isBlank()) {
            attendance.setAttendanceType("MANUAL");
        }
        if (attendance.getId() == null) {
            studentAttendanceMapper.insert(attendance);
        } else {
            StudentAttendance db = studentAttendanceMapper.selectById(attendance.getId());
            if (db == null) {
                throw new CustomException("出勤记录不存在");
            }
            validateCourseOwner(db.getCourseId(), teacherId);
            studentAttendanceMapper.updateById(attendance);
        }
        refreshAttendanceDerivedData(attendance.getStudentId(), attendance.getCourseId());
        return attendance.getId() == null ? attendance : studentAttendanceMapper.selectById(attendance.getId());
    }

    public void deleteAttendance(Long teacherId, Long attendanceId) {
        StudentAttendance db = studentAttendanceMapper.selectById(attendanceId);
        if (db == null) {
            return;
        }
        validateCourseOwner(db.getCourseId(), teacherId);
        studentAttendanceMapper.deleteById(attendanceId);
        refreshAttendanceDerivedData(db.getStudentId(), db.getCourseId());
    }

    public Map<String, Object> highRisk(Long teacherId, Long courseId, String riskLevel, String gpaColor, Integer pageNum, Integer pageSize) {
        if (courseId != null) {
            validateCourseOwner(courseId, teacherId);
        }
        Set<Long> allowedCourseIds = courseList(teacherId).stream().map(Course::getId).collect(Collectors.toSet());
        List<RiskPrediction> latestOnly = riskPredictionMapper.selectAll(new RiskPrediction()).stream()
                .collect(Collectors.toMap(
                        item -> item.getStudentId() + "_" + (item.getCourseId() == null ? "null" : item.getCourseId()),
                        item -> item,
                        (left, right) -> {
                            LocalDateTime leftTime = left.getPredictTime() == null ? left.getCreateTime() : left.getPredictTime();
                            LocalDateTime rightTime = right.getPredictTime() == null ? right.getCreateTime() : right.getPredictTime();
                            if (leftTime == null) return right;
                            if (rightTime == null) return left;
                            return rightTime.isAfter(leftTime) ? right : left;
                        }
                ))
                .values().stream().toList();

        if (courseId == null) {
            latestOnly = latestOnly.stream()
                    .collect(Collectors.toMap(
                            RiskPrediction::getStudentId,
                            item -> item,
                            (left, right) -> {
                                if (left.getRiskProbability() == null) return right;
                                if (right.getRiskProbability() == null) return left;
                                return right.getRiskProbability().compareTo(left.getRiskProbability()) > 0 ? right : left;
                            }
                    ))
                    .values().stream().toList();
        }

        List<Map<String, Object>> filtered = latestOnly.stream()
                .filter(item -> item.getCourseId() == null || allowedCourseIds.contains(item.getCourseId()))
                .filter(item -> courseId == null || courseId.equals(item.getCourseId()))
                .filter(item -> riskLevel == null || riskLevel.isEmpty() || riskLevel.equals(item.getRiskLevel()))
                .map(item -> {
                    StudentAcademic academicQuery = new StudentAcademic();
                    academicQuery.setStudentId(item.getStudentId());
                    StudentAcademic academic = studentAcademicMapper.selectAll(academicQuery).stream().findFirst().orElse(null);
                    if (gpaColor != null && !gpaColor.isEmpty() && (academic == null || !gpaColor.equals(academic.getGpaColor()))) {
                        return null;
                    }
                    Map<String, Object> map = new HashMap<>();
                    map.put("risk", item);
                    map.put("academic", academic);
                    map.put("student", sysUserMapper.selectById(item.getStudentId()));
                    Course course = item.getCourseId() == null ? null : courseMapper.selectById(item.getCourseId());
                    map.put("courseName", course == null ? "-" : course.getCourseName());
                    return map;
                })
                .filter(Objects::nonNull)
                .toList();

        int safePageNum = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 100);
        int total = filtered.size();
        int from = Math.min((safePageNum - 1) * safePageSize, total);
        int to = Math.min(from + safePageSize, total);

        Map<String, Object> result = new HashMap<>();
        result.put("list", filtered.subList(from, to));
        result.put("total", total);
        result.put("pageNum", safePageNum);
        result.put("pageSize", safePageSize);
        return result;
    }

    public List<Map<String, Object>> homeworkManage(Long teacherId, Long courseId) {
        validateCourseOwner(courseId, teacherId);
        Homework query = new Homework();
        query.setCourseId(courseId);
        return homeworkMapper.selectAll(query).stream()
                .sorted((left, right) -> {
                    LocalDateTime leftTime = left.getCreateTime() == null ? LocalDateTime.MIN : left.getCreateTime();
                    LocalDateTime rightTime = right.getCreateTime() == null ? LocalDateTime.MIN : right.getCreateTime();
                    int timeCompare = rightTime.compareTo(leftTime);
                    if (timeCompare != 0) {
                        return timeCompare;
                    }
                    Long leftId = left.getId() == null ? 0L : left.getId();
                    Long rightId = right.getId() == null ? 0L : right.getId();
                    return rightId.compareTo(leftId);
                })
                .map(homework -> {
            Map<String, Object> map = new HashMap<>();
            HomeworkSubmission submissionQuery = new HomeworkSubmission();
            submissionQuery.setHomeworkId(homework.getId());
            map.put("homework", homework);
            map.put("publishedAt", formatDateTime(homework.getCreateTime()));
            map.put("deadlineAt", formatDateTime(homework.getDeadline()));
            map.put("submissions", homeworkSubmissionMapper.selectAll(submissionQuery));
            return map;
        }).toList();
    }

    public void gradeHomework(Long teacherId, Long submissionId, BigDecimal score, String comment) {
        HomeworkSubmission submission = homeworkSubmissionMapper.selectById(submissionId);
        Homework homework = homeworkMapper.selectById(submission.getHomeworkId());
        validateCourseOwner(homework.getCourseId(), teacherId);
        BigDecimal objective = submission.getScore() == null ? BigDecimal.ZERO : submission.getScore();
        BigDecimal finalScore = objective.add(score == null ? BigDecimal.ZERO : score);
        submission.setScore(finalScore.min(new BigDecimal("100")));
        submission.setTeacherComment(comment);
        submission.setStatus("GRADED");
        submission.setUpdateTime(LocalDateTime.now());
        homeworkSubmissionMapper.updateById(submission);
    }

    public void gradeExam(Long teacherId, Long recordId, BigDecimal subjectiveScore, String comment) {
        ExamRecord record = examRecordMapper.selectById(recordId);
        if (record == null) {
            return;
        }
        Long studentId = record.getStudentId();
        Exam exam = examMapper.selectById(record.getExamId());
        validateCourseOwner(exam.getCourseId(), teacherId);
        Map<String, Object> payload = readJson(record.getRemark());
        BigDecimal objective = BigDecimal.valueOf(num(payload.get("objectiveScore")));
        if (objective.compareTo(BigDecimal.ZERO) == 0) {
            objective = record.getScore() == null ? BigDecimal.ZERO : record.getScore();
        }
        BigDecimal finalScore = objective.add(subjectiveScore == null ? BigDecimal.ZERO : subjectiveScore);
        record.setScore(finalScore.min(new BigDecimal("100")));
        payload.put("subjectiveScores", List.of(subjectiveScore == null ? BigDecimal.ZERO : subjectiveScore));
        payload.put("message", "客观题自动判分：" + objective + "；主观题教师判分：" + (subjectiveScore == null ? 0 : subjectiveScore) + (comment == null || comment.isEmpty() ? "" : "；" + comment));
        record.setRemark(writeJson(payload));
        record.setUpdateTime(LocalDateTime.now());
        examRecordMapper.updateById(record);
        if (studentId != null) {
            examService.refreshAcademic(studentId);
        }
    }

    public List<Map<String, Object>> examManage(Long teacherId, Long courseId) {
        validateCourseOwner(courseId, teacherId);
        Exam query = new Exam();
        query.setCourseId(courseId);
        return examMapper.selectAll(query).stream()
                .sorted((left, right) -> {
                    int leftSort = ExamService.EXAM_TYPE_MIDTERM.equals(left.getExamType()) ? 0 : 1;
                    int rightSort = ExamService.EXAM_TYPE_MIDTERM.equals(right.getExamType()) ? 0 : 1;
                    return Integer.compare(leftSort, rightSort);
                })
                .map(exam -> {
                    examService.recomputeQualifications(exam.getId());
                    Map<String, Object> map = new HashMap<>();
                    map.put("exam", exam);
                    map.put("details", examService.manageDetail(exam.getId()));
                    return map;
                })
                .toList();
    }

    public Map<String, Object> homeworkSubmissionDetail(Long teacherId, Long submissionId) {
        HomeworkSubmission submission = homeworkSubmissionMapper.selectById(submissionId);
        if (submission == null) {
            throw new CustomException("提交记录不存在");
        }
        Homework homework = homeworkMapper.selectById(submission.getHomeworkId());
        validateCourseOwner(homework.getCourseId(), teacherId);
        Map<String, Object> payload = readJson(submission.getSubmitContent());

        Map<String, Object> result = new HashMap<>();
        result.put("submission", submission);
        result.put("homework", homework);
        result.put("student", sysUserMapper.selectById(submission.getStudentId()));
        List<String> objectiveQuestions = parseQuestions(homework.getContent(), 2);
        result.put("objectiveQuestions", objectiveQuestions);
        result.put("objectiveCorrectAnswers", parseObjectiveCorrectAnswers(objectiveQuestions));
        result.put("subjectiveQuestions", parseQuestions(homework.getContent(), 10));
        result.put("objectiveAnswers", list(payload.get("objectiveDetail")));
        result.put("subjectiveAnswers", list(payload.get("subjectiveAnswers")));
        result.put("subjectiveScores", list(payload.get("subjectiveScores")));
        result.put("objectiveScore", payload.getOrDefault("objectiveScore", submission.getScore()));
        return result;
    }

    public void gradeHomeworkByQuestion(Long teacherId, Long submissionId, String questionScores, String comment) {
        HomeworkSubmission submission = homeworkSubmissionMapper.selectById(submissionId);
        if (submission == null) {
            throw new CustomException("提交记录不存在");
        }
        Homework homework = homeworkMapper.selectById(submission.getHomeworkId());
        validateCourseOwner(homework.getCourseId(), teacherId);

        Map<String, Object> payload = readJson(submission.getSubmitContent());
        List<Object> scoresRaw = list(readJsonArray(questionScores));
        int subjectiveTotal = scoresRaw.stream().mapToInt(this::num).sum();
        int objective = num(payload.get("objectiveScore"));
        int finalScore = Math.min(objective + subjectiveTotal, 100);
        payload.put("subjectiveScores", scoresRaw);
        payload.put("message", "客观题自动判分：" + objective + "；主观题教师判分：" + subjectiveTotal);
        submission.setSubmitContent(writeJson(payload));
        submission.setScore(new BigDecimal(finalScore));
        submission.setStatus("GRADED");
        submission.setTeacherComment(comment == null || comment.isEmpty() ? String.valueOf(payload.get("message")) : comment);
        submission.setUpdateTime(LocalDateTime.now());
        homeworkSubmissionMapper.updateById(submission);
    }

    public Map<String, Object> examRecordDetail(Long teacherId, Long recordId) {
        ExamRecord record = examRecordMapper.selectById(recordId);
        if (record == null) {
            throw new CustomException("考试记录不存在");
        }
        Exam exam = examMapper.selectById(record.getExamId());
        validateCourseOwner(exam.getCourseId(), teacherId);
        Map<String, Object> payload = readJson(record.getRemark());

        Map<String, Object> result = new HashMap<>();
        result.put("record", record);
        result.put("exam", exam);
        result.put("student", sysUserMapper.selectById(record.getStudentId()));
        List<String> objectiveQuestions = parseQuestions(exam.getDescription(), 2);
        result.put("objectiveQuestions", objectiveQuestions);
        result.put("objectiveCorrectAnswers", parseObjectiveCorrectAnswers(objectiveQuestions));
        result.put("subjectiveQuestions", parseQuestions(exam.getDescription(), 10));
        result.put("objectiveAnswers", list(payload.get("objectiveDetail")));
        result.put("subjectiveAnswers", list(payload.get("subjectiveAnswers")));
        result.put("subjectiveScores", list(payload.get("subjectiveScores")));
        result.put("objectiveScore", payload.get("objectiveScore"));
        result.put("message", payload.getOrDefault("message", ""));
        return result;
    }

    public void gradeExamByQuestion(Long teacherId, Long recordId, String questionScores, String comment) {
        ExamRecord record = examRecordMapper.selectById(recordId);
        if (record == null) {
            throw new CustomException("考试记录不存在");
        }
        Long studentId = record.getStudentId();
        Exam exam = examMapper.selectById(record.getExamId());
        validateCourseOwner(exam.getCourseId(), teacherId);

        Map<String, Object> payload = readJson(record.getRemark());
        List<Object> scoresRaw = list(readJsonArray(questionScores));
        int subjectiveTotal = scoresRaw.stream().mapToInt(this::num).sum();
        int objective = num(payload.get("objectiveScore"));
        int finalScore = Math.min(objective + subjectiveTotal, 100);
        payload.put("subjectiveScores", scoresRaw);
        payload.put("message", "客观题自动判分：" + objective + "；主观题教师判分：" + subjectiveTotal + (comment == null || comment.isEmpty() ? "" : "；" + comment));
        record.setScore(new BigDecimal(finalScore));
        record.setRemark(writeJson(payload));
        record.setUpdateTime(LocalDateTime.now());
        examRecordMapper.updateById(record);
        if (studentId != null) {
            examService.refreshAcademic(studentId);
        }
    }

    private void refreshAttendanceDerivedData(Long studentId, Long courseId) {
        StudentFeature feature = featureExtractor.extractAndSave(studentId, courseId);
        riskPredictor.predictAndSave(feature);
        Exam examQuery = new Exam();
        examQuery.setCourseId(courseId);
        for (Exam exam : examMapper.selectAll(examQuery)) {
            examService.recomputeQualification(exam.getId(), studentId);
        }
    }

    private Course validateCourseOwner(Long courseId, Long teacherId) {
        Course course = courseMapper.selectById(courseId);
        if (course == null || !teacherId.equals(course.getTeacherId())) {
            throw new CustomException("无权限操作非本人课程");
        }
        return course;
    }

    private List<String> parseQuestions(String content, int score) {
        if (content == null || content.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(content.replace("\\n", "\n").split("\n"))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .filter(item -> item.contains("（" + score + "分）") || item.contains("(" + score + ")") || item.contains("(" + score + "分)"))
                .toList();
    }

    private List<String> parseObjectiveCorrectAnswers(List<String> questions) {
        if (questions == null) {
            return List.of();
        }
        List<String> fallback = List.of("A", "B", "C", "D");
        return questions.stream().map(question -> {
            java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("答案\\s*[：:]\\s*([A-D])", java.util.regex.Pattern.CASE_INSENSITIVE).matcher(question);
            if (matcher.find()) {
                return matcher.group(1).toUpperCase();
            }
            return fallback.get(Math.abs(question.hashCode()) % fallback.size());
        }).toList();
    }

    private Map<String, Object> readJson(String raw) {
        if (raw == null || raw.isEmpty() || !raw.trim().startsWith("{")) {
            return new HashMap<>();
        }
        try {
            return MAPPER.readValue(raw, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private Object readJsonArray(String raw) {
        if (raw == null || raw.isEmpty()) {
            return List.of();
        }
        try {
            return MAPPER.readValue(raw, Object.class);
        } catch (Exception e) {
            return List.of();
        }
    }

    private List<Object> list(Object value) {
        if (value instanceof List<?> list) {
            return new java.util.ArrayList<>(list);
        }
        return new java.util.ArrayList<>();
    }

    private int num(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (Exception e) {
            return 0;
        }
    }

    private String writeJson(Map<String, Object> map) {
        try {
            return MAPPER.writeValueAsString(map);
        } catch (Exception e) {
            return "{}";
        }
    }

    private String formatDateTime(LocalDateTime value) {
        if (value == null) {
            return "-";
        }
        return value.format(DATE_TIME_FORMATTER);
    }
}
