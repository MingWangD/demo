package com.example.service;

import com.example.entity.*;
import com.example.exception.CustomException;
import com.example.mapper.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeacherService {
    private static final ObjectMapper MAPPER = new ObjectMapper();
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
    @Resource private ExamService examService;

    public List<Course> courseList(Long teacherId) {
        Course q = new Course();
        q.setTeacherId(teacherId);
        return courseMapper.selectAll(q);
    }

    public List<Map<String, Object>> courseStudents(Long teacherId, Long courseId) {
        validateCourseOwner(courseId, teacherId);
        StudentCourse q = new StudentCourse();
        q.setCourseId(courseId);
        return studentCourseMapper.selectAll(q).stream()
                .map(sc -> {
                    SysUser u = sysUserMapper.selectById(sc.getStudentId());
                    if (u == null) return null;
                    Map<String, Object> m = new HashMap<>();
                    m.put("studentId", u.getId());
                    m.put("studentName", u.getName());
                    return m;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Map<String, Object> studentDetail(Long teacherId, Long studentId, Long courseId) {
        if (courseId != null) validateCourseOwner(courseId, teacherId);
        Map<String, Object> m = new HashMap<>();
        m.put("student", sysUserMapper.selectById(studentId));

        StudentAttendance aq = new StudentAttendance(); aq.setStudentId(studentId); aq.setCourseId(courseId);
        int attendanceCount = studentAttendanceMapper.selectAll(aq).size();

        Homework hwQ = new Homework(); hwQ.setCourseId(courseId);
        List<Long> hwIds = homeworkMapper.selectAll(hwQ).stream().map(Homework::getId).toList();
        List<HomeworkSubmission> subs = homeworkSubmissionMapper.selectAll(new HomeworkSubmission()).stream().filter(s -> studentId.equals(s.getStudentId()) && hwIds.contains(s.getHomeworkId())).toList();

        List<ExamRecord> exams = examRecordMapper.selectAll(new ExamRecord()).stream().filter(e -> studentId.equals(e.getStudentId())).toList();

        StudentAcademic saQ = new StudentAcademic(); saQ.setStudentId(studentId);
        StudentAcademic academic = studentAcademicMapper.selectAll(saQ).stream().findFirst().orElse(null);
        RiskPrediction rpQ = new RiskPrediction(); rpQ.setStudentId(studentId);
        RiskPrediction latest = riskPredictionMapper.selectAll(rpQ).stream().findFirst().orElse(null);

        m.put("attendanceCount", attendanceCount);
        m.put("homeworkSubmitRate", hwIds.isEmpty()?0D:(double)subs.size()/hwIds.size());
        m.put("homeworkAvgScore", subs.stream().map(HomeworkSubmission::getScore).filter(Objects::nonNull).mapToDouble(v->v.doubleValue()).average().orElse(0));
        m.put("examAvgScore", exams.stream().map(ExamRecord::getScore).filter(Objects::nonNull).mapToDouble(v->v.doubleValue()).average().orElse(0));
        m.put("academic", academic);
        m.put("latestRisk", latest);
        return m;
    }

    public Map<String, Object> highRisk(Long teacherId, Long courseId, String riskLevel, String gpaColor, Integer pageNum, Integer pageSize) {
        if (courseId != null) validateCourseOwner(courseId, teacherId);
        Set<Long> allowedCourseIds = courseList(teacherId).stream().map(Course::getId).collect(Collectors.toSet());
        List<RiskPrediction> latestOnly = riskPredictionMapper.selectAll(new RiskPrediction()).stream()
                .collect(Collectors.toMap(
                        r -> r.getStudentId() + "_" + (r.getCourseId() == null ? "null" : r.getCourseId()),
                        r -> r,
                        (a, b) -> {
                            LocalDateTime at = a.getPredictTime() == null ? a.getCreateTime() : a.getPredictTime();
                            LocalDateTime bt = b.getPredictTime() == null ? b.getCreateTime() : b.getPredictTime();
                            if (at == null) return b;
                            if (bt == null) return a;
                            return bt.isAfter(at) ? b : a;
                        }
                ))
                .values().stream().toList();
        if (courseId == null) {
            latestOnly = latestOnly.stream().collect(Collectors.toMap(
                    RiskPrediction::getStudentId,
                    r -> r,
                    (a, b) -> {
                        if (a.getRiskProbability() == null) return b;
                        if (b.getRiskProbability() == null) return a;
                        return b.getRiskProbability().compareTo(a.getRiskProbability()) > 0 ? b : a;
                    }
            )).values().stream().toList();
        }
        List<Map<String, Object>> filtered = latestOnly.stream()
                .filter(r -> r.getCourseId() == null || allowedCourseIds.contains(r.getCourseId()))
                .filter(r -> courseId == null || courseId.equals(r.getCourseId()))
                .filter(r -> riskLevel == null || riskLevel.isEmpty() || riskLevel.equals(r.getRiskLevel()))
                .map(r -> {
                    StudentAcademic q = new StudentAcademic(); q.setStudentId(r.getStudentId());
                    StudentAcademic ac = studentAcademicMapper.selectAll(q).stream().findFirst().orElse(null);
                    if (gpaColor != null && !gpaColor.isEmpty() && (ac == null || !gpaColor.equals(ac.getGpaColor()))) return null;
                    Map<String,Object> m = new HashMap<>();
                    m.put("risk", r);
                    m.put("academic", ac);
                    m.put("student", sysUserMapper.selectById(r.getStudentId()));
                    Course c = r.getCourseId() == null ? null : courseMapper.selectById(r.getCourseId());
                    m.put("courseName", c == null ? "-" : c.getCourseName());
                    return m;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        int safePageNum = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 100);
        int total = filtered.size();
        int from = Math.min((safePageNum - 1) * safePageSize, total);
        int to = Math.min(from + safePageSize, total);
        List<Map<String, Object>> paged = filtered.subList(from, to);

        Map<String, Object> pageResult = new HashMap<>();
        pageResult.put("list", paged);
        pageResult.put("total", total);
        pageResult.put("pageNum", safePageNum);
        pageResult.put("pageSize", safePageSize);
        return pageResult;
    }

    public List<Map<String, Object>> homeworkManage(Long teacherId, Long courseId) {
        validateCourseOwner(courseId, teacherId);
        Homework q = new Homework(); q.setCourseId(courseId);
        List<Homework> homeworks = homeworkMapper.selectAll(q);
        return homeworks.stream().map(hw -> {
            Map<String, Object> m = new HashMap<>();
            HomeworkSubmission sQ = new HomeworkSubmission(); sQ.setHomeworkId(hw.getId());
            m.put("homework", hw);
            m.put("submissions", homeworkSubmissionMapper.selectAll(sQ));
            return m;
        }).toList();
    }

    public void gradeHomework(Long teacherId, Long submissionId, java.math.BigDecimal score, String comment) {
        HomeworkSubmission sub = homeworkSubmissionMapper.selectById(submissionId);
        Homework hw = homeworkMapper.selectById(sub.getHomeworkId());
        validateCourseOwner(hw.getCourseId(), teacherId);
        java.math.BigDecimal objective = sub.getScore() == null ? java.math.BigDecimal.ZERO : sub.getScore();
        java.math.BigDecimal finalScore = objective.add(score == null ? java.math.BigDecimal.ZERO : score);
        sub.setScore(finalScore.min(new java.math.BigDecimal("100")));
        sub.setTeacherComment(comment);
        sub.setStatus("GRADED");
        sub.setUpdateTime(java.time.LocalDateTime.now());
        homeworkSubmissionMapper.updateById(sub);
    }

    public void gradeExam(Long teacherId, Long recordId, java.math.BigDecimal subjectiveScore, String comment) {
        ExamRecord record = examRecordMapper.selectById(recordId);
        if (record == null) return;
        Exam ex = examMapper.selectById(record.getExamId());
        validateCourseOwner(ex.getCourseId(), teacherId);
        Map<String, Object> payload = readJson(record.getRemark());
        java.math.BigDecimal objective = java.math.BigDecimal.valueOf(num(payload.get("objectiveScore")));
        if (objective.compareTo(java.math.BigDecimal.ZERO) == 0) {
            objective = record.getScore() == null ? java.math.BigDecimal.ZERO : record.getScore();
        }
        java.math.BigDecimal finalScore = objective.add(subjectiveScore == null ? java.math.BigDecimal.ZERO : subjectiveScore);
        record.setScore(finalScore.min(new java.math.BigDecimal("100")));
        payload.put("message", "客观题自动判分：" + objective + "；主观题教师判分：" + (subjectiveScore == null ? 0 : subjectiveScore) + (comment == null || comment.isEmpty() ? "" : "；" + comment));
        record.setRemark(writeJson(payload));
        record.setUpdateTime(java.time.LocalDateTime.now());
        examRecordMapper.updateById(record);
    }

    public List<Map<String,Object>> examManage(Long teacherId, Long courseId) {
        validateCourseOwner(courseId, teacherId);
        Exam e = new Exam(); e.setCourseId(courseId);
        return examMapper.selectAll(e).stream().map(ex -> {
            examService.recomputeQualifications(ex.getId());
            Map<String,Object> m = new HashMap<>();
            m.put("exam", ex);
            m.put("details", examService.manageDetail(ex.getId()));
            return m;
        }).toList();
    }

    public Map<String, Object> homeworkSubmissionDetail(Long teacherId, Long submissionId) {
        HomeworkSubmission sub = homeworkSubmissionMapper.selectById(submissionId);
        if (sub == null) throw new CustomException("提交记录不存在");
        Homework hw = homeworkMapper.selectById(sub.getHomeworkId());
        validateCourseOwner(hw.getCourseId(), teacherId);
        Map<String, Object> payload = readJson(sub.getSubmitContent());
        Map<String, Object> res = new HashMap<>();
        res.put("submission", sub);
        res.put("homework", hw);
        res.put("student", sysUserMapper.selectById(sub.getStudentId()));
        List<String> objectiveQuestions = parseQuestions(hw.getContent(), 2);
        res.put("objectiveQuestions", objectiveQuestions);
        res.put("objectiveCorrectAnswers", parseObjectiveCorrectAnswers(objectiveQuestions));
        res.put("subjectiveQuestions", parseQuestions(hw.getContent(), 10));
        res.put("objectiveAnswers", list(payload.get("objectiveDetail")));
        res.put("subjectiveAnswers", list(payload.get("subjectiveAnswers")));
        res.put("subjectiveScores", list(payload.get("subjectiveScores")));
        res.put("objectiveScore", payload.getOrDefault("objectiveScore", sub.getScore()));
        return res;
    }

    public void gradeHomeworkByQuestion(Long teacherId, Long submissionId, String questionScores, String comment) {
        HomeworkSubmission sub = homeworkSubmissionMapper.selectById(submissionId);
        if (sub == null) throw new CustomException("提交记录不存在");
        Homework hw = homeworkMapper.selectById(sub.getHomeworkId());
        validateCourseOwner(hw.getCourseId(), teacherId);
        Map<String, Object> payload = readJson(sub.getSubmitContent());
        List<Object> scoresRaw = list(readJsonArray(questionScores));
        int subjectiveTotal = scoresRaw.stream().mapToInt(this::num).sum();
        int objective = num(payload.get("objectiveScore"));
        int finalScore = Math.min(objective + subjectiveTotal, 100);
        payload.put("subjectiveScores", scoresRaw);
        payload.put("message", "客观题自动判分：" + objective + "；主观题教师判分：" + subjectiveTotal);
        sub.setSubmitContent(writeJson(payload));
        sub.setScore(new java.math.BigDecimal(finalScore));
        sub.setStatus("GRADED");
        sub.setTeacherComment(comment == null || comment.isEmpty() ? String.valueOf(payload.get("message")) : comment);
        sub.setUpdateTime(LocalDateTime.now());
        homeworkSubmissionMapper.updateById(sub);
    }

    public Map<String, Object> examRecordDetail(Long teacherId, Long recordId) {
        ExamRecord record = examRecordMapper.selectById(recordId);
        if (record == null) throw new CustomException("考试记录不存在");
        Exam exam = examMapper.selectById(record.getExamId());
        validateCourseOwner(exam.getCourseId(), teacherId);
        Map<String, Object> payload = readJson(record.getRemark());
        Map<String, Object> res = new HashMap<>();
        res.put("record", record);
        res.put("exam", exam);
        res.put("student", sysUserMapper.selectById(record.getStudentId()));
        List<String> objectiveQuestions = parseQuestions(exam.getDescription(), 2);
        res.put("objectiveQuestions", objectiveQuestions);
        res.put("objectiveCorrectAnswers", parseObjectiveCorrectAnswers(objectiveQuestions));
        res.put("subjectiveQuestions", parseQuestions(exam.getDescription(), 10));
        res.put("objectiveAnswers", list(payload.get("objectiveDetail")));
        res.put("subjectiveAnswers", list(payload.get("subjectiveAnswers")));
        res.put("subjectiveScores", list(payload.get("subjectiveScores")));
        res.put("objectiveScore", payload.get("objectiveScore"));
        res.put("message", payload.getOrDefault("message", ""));
        return res;
    }

    public void gradeExamByQuestion(Long teacherId, Long recordId, String questionScores, String comment) {
        ExamRecord record = examRecordMapper.selectById(recordId);
        if (record == null) throw new CustomException("考试记录不存在");
        Exam exam = examMapper.selectById(record.getExamId());
        validateCourseOwner(exam.getCourseId(), teacherId);
        Map<String, Object> payload = readJson(record.getRemark());
        List<Object> scoresRaw = list(readJsonArray(questionScores));
        int subjectiveTotal = scoresRaw.stream().mapToInt(this::num).sum();
        int objective = num(payload.get("objectiveScore"));
        int finalScore = Math.min(objective + subjectiveTotal, 100);
        payload.put("subjectiveScores", scoresRaw);
        payload.put("message", "客观题自动判分：" + objective + "；主观题教师判分：" + subjectiveTotal + (comment == null || comment.isEmpty() ? "" : "；" + comment));
        record.setScore(new java.math.BigDecimal(finalScore));
        record.setRemark(writeJson(payload));
        record.setUpdateTime(LocalDateTime.now());
        examRecordMapper.updateById(record);
    }

    private void validateCourseOwner(Long courseId, Long teacherId) {
        Course c = courseMapper.selectById(courseId);
        if (c == null || !teacherId.equals(c.getTeacherId())) {
            throw new CustomException("无权限操作非本人课程");
        }
    }

    private List<String> parseQuestions(String content, int score) {
        if (content == null || content.isEmpty()) return List.of();
        return Arrays.stream(content.replace("\\\\n", "\n").split("\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .filter(s -> s.contains("（" + score + "分）") || s.contains("(" + score + "分)"))
                .toList();
    }

    private List<String> parseObjectiveCorrectAnswers(List<String> questions) {
        if (questions == null) return List.of();
        List<String> fallback = List.of("A", "B", "C", "D");
        return questions.stream().map(q -> {
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("答案\\s*[：:]\\s*([A-D])", java.util.regex.Pattern.CASE_INSENSITIVE).matcher(q);
            if (m.find()) return m.group(1).toUpperCase();
            int idx = Math.abs(q.hashCode()) % fallback.size();
            return fallback.get(idx);
        }).toList();
    }

    private Map<String, Object> readJson(String raw) {
        if (raw == null || raw.isEmpty()) return new HashMap<>();
        if (!raw.trim().startsWith("{")) return new HashMap<>();
        try {
            return MAPPER.readValue(raw, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private Object readJsonArray(String raw) {
        if (raw == null || raw.isEmpty()) return List.of();
        try {
            return MAPPER.readValue(raw, List.class);
        } catch (Exception e) {
            return List.of();
        }
    }

    private String writeJson(Map<String, Object> map) {
        try {
            return MAPPER.writeValueAsString(map);
        } catch (Exception e) {
            return "{}";
        }
    }

    private int num(Object v) {
        if (v == null) return 0;
        if (v instanceof Number n) return n.intValue();
        try { return Integer.parseInt(String.valueOf(v)); } catch (Exception e) { return 0; }
    }

    private List<Object> list(Object value) {
        if (value instanceof List<?> l) return new ArrayList<>(l);
        if (value == null) return new ArrayList<>();
        return new ArrayList<>(List.of(value));
    }
}
