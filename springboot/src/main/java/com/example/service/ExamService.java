package com.example.service;

import com.example.algorithm.FeatureExtractor;
import com.example.algorithm.RiskPredictor;
import com.example.common.GpaColorUtil;
import com.example.dto.ExamSubmitRequest;
import com.example.entity.Course;
import com.example.entity.Exam;
import com.example.entity.ExamQualification;
import com.example.entity.ExamRecord;
import com.example.entity.StudentAcademic;
import com.example.entity.StudentAttendance;
import com.example.entity.StudentCourse;
import com.example.entity.StudentFeature;
import com.example.exception.CustomException;
import com.example.mapper.CourseMapper;
import com.example.mapper.ExamMapper;
import com.example.mapper.ExamQualificationMapper;
import com.example.mapper.ExamRecordMapper;
import com.example.mapper.StudentAcademicMapper;
import com.example.mapper.StudentAttendanceMapper;
import com.example.mapper.StudentCourseMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ExamService {
    public static final String EXAM_TYPE_MIDTERM = "MIDTERM";
    public static final String EXAM_TYPE_FINAL = "FINAL";
    public static final BigDecimal MIDTERM_WEIGHT = new BigDecimal("0.30");
    public static final BigDecimal FINAL_WEIGHT = new BigDecimal("0.70");
    public static final String GPA_PENDING_NOTE = "PENDING_FINAL_GPA";

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Pattern TWO_POINT_QUESTION_PATTERN = Pattern.compile("\\(\\s*2\\s*\\)|（\\s*2\\s*分\\s*）");
    private static final Pattern TEN_POINT_QUESTION_PATTERN = Pattern.compile("\\(\\s*10\\s*\\)|（\\s*10\\s*分\\s*）");

    @Resource private ExamMapper examMapper;
    @Resource private ExamRecordMapper examRecordMapper;
    @Resource private ExamQualificationMapper examQualificationMapper;
    @Resource private FeatureExtractor featureExtractor;
    @Resource private RiskPredictor riskPredictor;
    @Resource private StudentAttendanceMapper studentAttendanceMapper;
    @Resource private CourseMapper courseMapper;
    @Resource private StudentCourseMapper studentCourseMapper;
    @Resource private StudentAcademicMapper studentAcademicMapper;

    public List<Exam> listByCourse(Long courseId) {
        Exam query = new Exam();
        query.setCourseId(courseId);
        List<Exam> exams = examMapper.selectAll(query).stream()
                .sorted((a, b) -> {
                    int left = examTypeSort(a.getExamType());
                    int right = examTypeSort(b.getExamType());
                    if (left != right) return Integer.compare(left, right);
                    LocalDateTime at = a.getExamTime();
                    LocalDateTime bt = b.getExamTime();
                    if (at == null && bt == null) return Long.compare(Optional.ofNullable(a.getId()).orElse(0L), Optional.ofNullable(b.getId()).orElse(0L));
                    if (at == null) return 1;
                    if (bt == null) return -1;
                    return at.compareTo(bt);
                })
                .toList();
        exams.forEach(exam -> recomputeQualifications(exam.getId()));
        return exams;
    }

    public void create(Exam exam, Long teacherId) {
        Course course = validateCourseOwner(exam.getCourseId(), teacherId);
        validateExamType(exam.getExamType());
        ensureTypeUniquePerCourse(exam.getCourseId(), exam.getExamType(), null);
        if (exam.getTotalScore() == null || exam.getTotalScore().compareTo(new BigDecimal("100")) != 0) {
            throw new CustomException("考试总分必须为100分");
        }
        exam.setCreateTime(LocalDateTime.now());
        exam.setUpdateTime(LocalDateTime.now());
        exam.setTeacherId(teacherId);
        exam.setExamName(defaultExamName(course, exam.getExamType(), exam.getExamName()));
        examMapper.insert(exam);
        recomputeQualifications(exam.getId());
    }

    public void submit(ExamSubmitRequest request) {
        recomputeQualification(request.getExamId(), request.getStudentId());
        ExamQualification qualification = examQualificationMapper.selectByExamAndStudent(request.getExamId(), request.getStudentId());
        if (qualification == null || !Boolean.TRUE.equals(qualification.getIsQualified())) {
            throw new CustomException("当前考试资格不满足，不能参加考试");
        }

        Exam exam = examMapper.selectById(request.getExamId());
        ExamRecord record = examRecordMapper.selectByExamAndStudent(request.getExamId(), request.getStudentId());
        LocalDateTime now = LocalDateTime.now();
        if (record != null && hasSubmitted(record)) {
            throw new CustomException("考试已提交，不能再次修改");
        }
        if (record == null) {
            record = new ExamRecord();
            record.setExamId(request.getExamId());
            record.setStudentId(request.getStudentId());
            record.setCreateTime(now);
        }

        Map<String, Object> payload = readJson(request.getAnswerContent());
        List<String> objectiveQuestions = parseQuestions(exam.getDescription(), TWO_POINT_QUESTION_PATTERN);
        List<String> objectiveCorrectAnswers = parseObjectiveCorrectAnswers(objectiveQuestions);
        List<Object> objectiveDetail = list(payload.get("objectiveDetail"));
        int objectiveAnswered = (int) objectiveDetail.stream().filter(Objects::nonNull).map(String::valueOf).map(String::trim).filter(s -> !s.isEmpty()).count();
        int objectiveCorrect = countObjectiveCorrectAnswers(objectiveDetail, objectiveCorrectAnswers);
        int subjectiveCount = countScoreTaggedQuestions(exam.getDescription(), TEN_POINT_QUESTION_PATTERN);
        BigDecimal autoScore = BigDecimal.valueOf(Math.min(objectiveCorrect * 2, 100));

        record.setScore(autoScore);
        payload.put("objectiveLocked", true);
        payload.put("objectiveAnswered", objectiveAnswered);
        payload.put("objectiveCorrect", objectiveCorrect);
        payload.put("objectiveScore", autoScore);
        String remark = "系统已自动判客观题得分：" + autoScore;
        if (subjectiveCount > 0) {
            remark += "，主观题待教师批改";
        }
        payload.put("message", remark);
        record.setRemark(writeJson(payload));
        record.setStatus("FINISHED");
        record.setSubmitTime(now);
        record.setUpdateTime(now);
        if (record.getId() == null) {
            examRecordMapper.insert(record);
        } else {
            examRecordMapper.updateById(record);
        }

        refreshAcademic(request.getStudentId());
        StudentFeature feature = featureExtractor.extractAndSave(request.getStudentId(), exam.getCourseId());
        riskPredictor.predictAndSave(feature);
    }

    public List<Map<String, Object>> manageDetail(Long examId) {
        recomputeQualifications(examId);
        ExamQualification query = new ExamQualification();
        query.setExamId(examId);
        return examQualificationMapper.selectAll(query).stream().map(item -> {
            Map<String, Object> map = new HashMap<>();
            map.put("qualification", item);
            map.put("record", examRecordMapper.selectByExamAndStudent(item.getExamId(), item.getStudentId()));
            return map;
        }).toList();
    }

    public void recomputeQualifications(Long examId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            return;
        }
        StudentCourse query = new StudentCourse();
        query.setCourseId(exam.getCourseId());
        for (StudentCourse studentCourse : studentCourseMapper.selectAll(query)) {
            recomputeQualification(examId, studentCourse.getStudentId());
        }
    }

    public void recomputeQualification(Long examId, Long studentId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            return;
        }
        Course course = courseMapper.selectById(exam.getCourseId());

        StudentAttendance attendanceQuery = new StudentAttendance();
        attendanceQuery.setStudentId(studentId);
        attendanceQuery.setCourseId(course.getId());
        int attendanceCount = studentAttendanceMapper.selectAll(attendanceQuery).size();
        int requiredCount = Optional.ofNullable(course.getAttendanceRequiredCount()).orElse(0);
        int base = Math.max(requiredCount, 1);
        BigDecimal qualificationRate = BigDecimal.valueOf(attendanceCount)
                .divide(BigDecimal.valueOf(base), 4, RoundingMode.HALF_UP);
        boolean qualified = requiredCount == 0 || qualificationRate.compareTo(new BigDecimal("0.6667")) >= 0;

        ExamQualification row = examQualificationMapper.selectByExamAndStudent(examId, studentId);
        if (row == null) {
            row = new ExamQualification();
            row.setExamId(examId);
            row.setStudentId(studentId);
            row.setCreateTime(LocalDateTime.now());
        }
        row.setAttendanceCount(attendanceCount);
        row.setRequiredCount(requiredCount);
        row.setQualificationRate(qualificationRate);
        row.setIsQualified(qualified);
        row.setReason(qualified ? "出勤达到课程要求，可参加考试" : "出勤次数不足课程总次数的三分之二，不能参加考试");
        if (row.getId() == null) {
            examQualificationMapper.insert(row);
        } else {
            examQualificationMapper.updateById(row);
        }

        ExamRecord record = examRecordMapper.selectByExamAndStudent(examId, studentId);
        if (!qualified && record != null && !hasSubmitted(record)) {
            record.setScore(null);
            record.setSubmitTime(null);
            record.setStatus("NOT_JOINED");
            record.setRemark("出勤次数不足课程总次数的三分之二，成绩无效");
            record.setUpdateTime(LocalDateTime.now());
            examRecordMapper.updateById(record);
        }
    }

    public BigDecimal calculateCourseFinalScore(Long studentId, Long courseId) {
        BigDecimal finalScore = resolveExamScoreForCourse(studentId, courseId, EXAM_TYPE_FINAL);
        if (finalScore == null) {
            return null;
        }
        BigDecimal midtermScore = Optional.ofNullable(resolveExamScoreForCourse(studentId, courseId, EXAM_TYPE_MIDTERM))
                .orElse(BigDecimal.ZERO);
        return midtermScore.multiply(MIDTERM_WEIGHT)
                .add(finalScore.multiply(FINAL_WEIGHT))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public Map<String, BigDecimal> calculateAllCourseFinalScores(Long studentId) {
        StudentCourse query = new StudentCourse();
        query.setStudentId(studentId);
        Map<String, BigDecimal> result = new HashMap<>();
        for (StudentCourse studentCourse : studentCourseMapper.selectAll(query)) {
            BigDecimal score = calculateCourseFinalScore(studentId, studentCourse.getCourseId());
            if (score != null) {
                result.put(String.valueOf(studentCourse.getCourseId()), score);
            }
        }
        return result;
    }

    public void refreshAcademic(Long studentId) {
        StudentCourse query = new StudentCourse();
        query.setStudentId(studentId);
        List<StudentCourse> studentCourses = studentCourseMapper.selectAll(query);

        BigDecimal totalCredit = BigDecimal.ZERO;
        BigDecimal earnedCredit = BigDecimal.ZERO;
        BigDecimal scoreSum = BigDecimal.ZERO;
        int completedCourseCount = 0;

        for (StudentCourse studentCourse : studentCourses) {
            Course course = courseMapper.selectById(studentCourse.getCourseId());
            if (course == null) {
                continue;
            }
            BigDecimal credit = Optional.ofNullable(course.getCredit()).orElse(BigDecimal.ZERO);
            totalCredit = totalCredit.add(credit);

            BigDecimal finalScore = calculateCourseFinalScore(studentId, course.getId());
            if (finalScore == null) {
                continue;
            }
            completedCourseCount++;
            scoreSum = scoreSum.add(finalScore);
            if (finalScore.compareTo(new BigDecimal("60")) >= 0) {
                earnedCredit = earnedCredit.add(credit);
            }
        }

        BigDecimal gpa = completedCourseCount == 0
                ? BigDecimal.ZERO
                : scoreSum.divide(BigDecimal.valueOf(completedCourseCount), 2, RoundingMode.HALF_UP)
                .divide(new BigDecimal("25"), 2, RoundingMode.HALF_UP);

        StudentAcademic academicQuery = new StudentAcademic();
        academicQuery.setStudentId(studentId);
        StudentAcademic academic = studentAcademicMapper.selectAll(academicQuery).stream().findFirst().orElse(null);
        boolean isNewAcademic = academic == null;
        if (isNewAcademic) {
            academic = new StudentAcademic();
            academic.setStudentId(studentId);
        }
        academic.setTotalCredit(totalCredit);
        academic.setEarnedCredit(earnedCredit);
        academic.setGpa(gpa);
        academic.setGpaColor(GpaColorUtil.resolveColor(gpa));
        academic.setRiskNote(completedCourseCount == 0 ? GPA_PENDING_NOTE : "WEIGHTED_TERM_GPA");
        academic.setUpdateTime(LocalDateTime.now());
        if (isNewAcademic) {
            studentAcademicMapper.insert(academic);
        } else {
            studentAcademicMapper.updateById(academic);
        }
    }

    public BigDecimal examWeight(String examType) {
        if (EXAM_TYPE_MIDTERM.equals(examType)) {
            return MIDTERM_WEIGHT;
        }
        if (EXAM_TYPE_FINAL.equals(examType)) {
            return FINAL_WEIGHT;
        }
        return BigDecimal.ZERO;
    }

    public void undo(Long teacherId, Long examId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            return;
        }
        validateCourseOwner(exam.getCourseId(), teacherId);

        ExamQualification qualificationQuery = new ExamQualification();
        qualificationQuery.setExamId(examId);
        List<Long> affectedStudents = examQualificationMapper.selectAll(qualificationQuery).stream()
                .map(ExamQualification::getStudentId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        examQualificationMapper.selectAll(qualificationQuery).stream()
                .map(ExamQualification::getId)
                .forEach(examQualificationMapper::deleteById);

        ExamRecord recordQuery = new ExamRecord();
        recordQuery.setExamId(examId);
        examRecordMapper.selectAll(recordQuery).stream()
                .map(ExamRecord::getId)
                .forEach(examRecordMapper::deleteById);

        examMapper.deleteById(examId);
        for (Long studentId : affectedStudents) {
            refreshAcademic(studentId);
            StudentFeature feature = featureExtractor.extractAndSave(studentId, exam.getCourseId());
            riskPredictor.predictAndSave(feature);
        }
    }

    private BigDecimal resolveExamScoreForCourse(Long studentId, Long courseId, String examType) {
        Exam examQuery = new Exam();
        examQuery.setCourseId(courseId);
        examQuery.setExamType(examType);
        Exam exam = examMapper.selectAll(examQuery).stream().findFirst().orElse(null);
        if (exam == null) {
            return EXAM_TYPE_FINAL.equals(examType) ? null : BigDecimal.ZERO;
        }

        ExamRecord record = examRecordMapper.selectByExamAndStudent(exam.getId(), studentId);
        if (record != null && "FINISHED".equals(record.getStatus()) && record.getScore() != null) {
            return record.getScore();
        }

        if (exam.getExamTime() != null && !LocalDateTime.now().isBefore(exam.getExamTime())) {
            return BigDecimal.ZERO;
        }
        return EXAM_TYPE_FINAL.equals(examType) ? null : BigDecimal.ZERO;
    }

    private void validateExamType(String examType) {
        if (!EXAM_TYPE_MIDTERM.equals(examType) && !EXAM_TYPE_FINAL.equals(examType)) {
            throw new CustomException("考试类型只能是期中考试或期末考试");
        }
    }

    private void ensureTypeUniquePerCourse(Long courseId, String examType, Long currentExamId) {
        Exam query = new Exam();
        query.setCourseId(courseId);
        query.setExamType(examType);
        boolean exists = examMapper.selectAll(query).stream()
                .anyMatch(item -> !Objects.equals(item.getId(), currentExamId));
        if (exists) {
            throw new CustomException(EXAM_TYPE_MIDTERM.equals(examType) ? "当前课程已经存在期中考试" : "当前课程已经存在期末考试");
        }
    }

    private String defaultExamName(Course course, String examType, String incomingName) {
        if (incomingName != null && !incomingName.isBlank()) {
            return incomingName;
        }
        String suffix = EXAM_TYPE_MIDTERM.equals(examType) ? "期中考试" : "期末考试";
        return (course == null ? "课程" : course.getCourseName()) + suffix;
    }

    private Course validateCourseOwner(Long courseId, Long teacherId) {
        Course course = courseMapper.selectById(courseId);
        if (course == null || !teacherId.equals(course.getTeacherId())) {
            throw new CustomException("无权限操作该课程考试");
        }
        return course;
    }

    private int examTypeSort(String examType) {
        if (EXAM_TYPE_MIDTERM.equals(examType)) {
            return 0;
        }
        if (EXAM_TYPE_FINAL.equals(examType)) {
            return 1;
        }
        return 9;
    }

    private int countObjectiveQuestions(String text) {
        return countScoreTaggedQuestions(text, TWO_POINT_QUESTION_PATTERN);
    }

    private List<String> parseQuestions(String content, Pattern pattern) {
        if (content == null || content.isEmpty()) {
            return List.of();
        }
        return java.util.Arrays.stream(content.replace("\\n", "\n").split("\n"))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .filter(item -> pattern.matcher(item).find())
                .toList();
    }

    private int countScoreTaggedQuestions(String text, Pattern pattern) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        int count = 0;
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    private List<String> parseObjectiveCorrectAnswers(List<String> questions) {
        if (questions == null) {
            return List.of();
        }
        List<String> fallback = List.of("A", "B", "C", "D");
        return questions.stream().map(question -> {
            Matcher matcher = Pattern.compile("答案\\s*[:：]?\\s*([A-D])", Pattern.CASE_INSENSITIVE).matcher(question);
            if (matcher.find()) {
                return matcher.group(1).toUpperCase(Locale.ROOT);
            }
            return fallback.get(Math.abs(question.hashCode()) % fallback.size());
        }).toList();
    }

    private int countObjectiveCorrectAnswers(List<Object> objectiveDetail, List<String> objectiveCorrectAnswers) {
        int size = Math.min(objectiveDetail.size(), objectiveCorrectAnswers.size());
        int count = 0;
        for (int i = 0; i < size; i++) {
            String answer = String.valueOf(objectiveDetail.get(i) == null ? "" : objectiveDetail.get(i)).trim().toUpperCase(Locale.ROOT);
            String correct = String.valueOf(objectiveCorrectAnswers.get(i) == null ? "" : objectiveCorrectAnswers.get(i)).trim().toUpperCase(Locale.ROOT);
            if (!answer.isEmpty() && answer.equals(correct)) {
                count++;
            }
        }
        return count;
    }

    private Map<String, Object> readJson(String raw) {
        if (raw == null || raw.isEmpty()) {
            return new HashMap<>();
        }
        try {
            return MAPPER.readValue(raw, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return new HashMap<>();
        }
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

    private List<Object> list(Object value) {
        if (value instanceof List<?> list) {
            return new java.util.ArrayList<>(list);
        }
        return new java.util.ArrayList<>();
    }

    private boolean hasSubmitted(ExamRecord record) {
        if (record == null) {
            return false;
        }
        if ("FINISHED".equals(record.getStatus())) {
            return true;
        }
        if (record.getSubmitTime() != null) {
            return true;
        }
        String remark = record.getRemark();
        return remark != null && remark.trim().startsWith("{");
    }
}
