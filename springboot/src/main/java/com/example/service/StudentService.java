package com.example.service;

import com.example.algorithm.FeatureExtractor;
import com.example.algorithm.RiskPredictor;
import com.example.entity.*;
import com.example.mapper.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentService {
    @Resource private SysUserMapper sysUserMapper;
    @Resource private StudentAcademicMapper studentAcademicMapper;
    @Resource private RiskPredictionMapper riskPredictionMapper;
    @Resource private StudentAttendanceMapper studentAttendanceMapper;
    @Resource private StudentCourseMapper studentCourseMapper;
    @Resource private CourseMapper courseMapper;
    @Resource private HomeworkMapper homeworkMapper;
    @Resource private HomeworkSubmissionMapper homeworkSubmissionMapper;
    @Resource private ExamMapper examMapper;
    @Resource private ExamQualificationMapper examQualificationMapper;
    @Resource private ExamRecordMapper examRecordMapper;
    @Resource private ExamService examService;
    @Resource private FeatureExtractor featureExtractor;
    @Resource private RiskPredictor riskPredictor;

    public Map<String, Object> overview(Long studentId) {
        Map<String, Object> res = new HashMap<>();
        SysUser user = sysUserMapper.selectById(studentId);
        StudentAcademic q = new StudentAcademic(); q.setStudentId(studentId);
        StudentAcademic academic = studentAcademicMapper.selectAll(q).stream().findFirst().orElse(null);
        RiskPrediction rq = new RiskPrediction(); rq.setStudentId(studentId);
        RiskPrediction latestRisk = riskPredictionMapper.selectAll(rq).stream().findFirst().orElse(null);
        StudentAttendance aq = new StudentAttendance(); aq.setStudentId(studentId);
        int attendanceCount = studentAttendanceMapper.selectAll(aq).size();

        HomeworkSubmission hsq = new HomeworkSubmission(); hsq.setStudentId(studentId);
        List<HomeworkSubmission> submissions = homeworkSubmissionMapper.selectAll(hsq);
        long submitted = submissions.stream().filter(s -> "SUBMITTED".equals(s.getStatus()) || "GRADED".equals(s.getStatus())).count();
        BigDecimal submitRate = submissions.isEmpty() ? BigDecimal.ZERO : BigDecimal.valueOf(submitted).divide(BigDecimal.valueOf(submissions.size()), 4, java.math.RoundingMode.HALF_UP);

        ExamRecord eq = new ExamRecord(); eq.setStudentId(studentId);
        List<ExamRecord> records = examRecordMapper.selectAll(eq);
        BigDecimal examAvg = BigDecimal.valueOf(records.stream().map(ExamRecord::getScore).filter(Objects::nonNull).mapToDouble(BigDecimal::doubleValue).average().orElse(0));

        res.put("user", user);
        res.put("earnedCredit", academic == null ? BigDecimal.ZERO : academic.getEarnedCredit());
        res.put("gpa", academic == null ? BigDecimal.ZERO : academic.getGpa());
        res.put("gpaColor", academic == null ? "RED" : academic.getGpaColor());
        res.put("riskProbability", latestRisk == null ? BigDecimal.ZERO : latestRisk.getRiskProbability());
        res.put("riskLevel", latestRisk == null ? "LOW" : latestRisk.getRiskLevel());
        res.put("warningColor", latestRisk == null ? "GREEN" : latestRisk.getWarningColor());
        res.put("mainReason", latestRisk == null ? "暂无" : latestRisk.getMainReason());
        res.put("attendanceCount", attendanceCount);
        res.put("homeworkSubmitRate", submitRate);
        res.put("examAvgScore", examAvg);
        return res;
    }

    public List<Course> courses(Long studentId) {
        StudentCourse query = new StudentCourse(); query.setStudentId(studentId);
        List<Long> courseIds = studentCourseMapper.selectAll(query).stream().map(StudentCourse::getCourseId).toList();
        if (courseIds.isEmpty()) return Collections.emptyList();
        return courseMapper.selectAll(new Course()).stream().filter(c -> courseIds.contains(c.getId())).collect(Collectors.toList());
    }

    public List<Map<String, Object>> courseHomework(Long studentId, Long courseId) {
        Homework q = new Homework(); q.setCourseId(courseId);
        List<Homework> homeworkList = homeworkMapper.selectAll(q);
        return homeworkList.stream().map(h -> {
            HomeworkSubmission sub = homeworkSubmissionMapper.selectByHomeworkAndStudent(h.getId(), studentId);
            Map<String, Object> m = new HashMap<>();
            m.put("homework", h);
            m.put("submission", sub);
            return m;
        }).toList();
    }

    public List<Map<String, Object>> courseExam(Long studentId, Long courseId) {
        Exam q = new Exam(); q.setCourseId(courseId);
        List<Exam> exams = examMapper.selectAll(q);
        return exams.stream().map(exam -> {
            examService.recomputeQualification(exam.getId(), studentId);
            ExamQualification qualification = examQualificationMapper.selectByExamAndStudent(exam.getId(), studentId);
            ExamRecord record = examRecordMapper.selectByExamAndStudent(exam.getId(), studentId);
            Map<String, Object> m = new HashMap<>();
            m.put("examId", exam.getId());
            m.put("examName", exam.getExamName());
            m.put("examTime", exam.getExamTime());
            m.put("description", exam.getDescription());
            m.put("isQualified", qualification != null && Boolean.TRUE.equals(qualification.getIsQualified()));
            m.put("qualificationReason", qualification == null ? "未生成资格" : qualification.getReason());
            m.put("score", record == null ? null : record.getScore());
            m.put("status", record == null ? "NOT_JOINED" : record.getStatus());
            m.put("autoJudgeRemark", record == null ? null : parseExamRemark(record.getRemark()));
            m.put("submitTime", record == null ? null : record.getSubmitTime());
            return m;
        }).toList();
    }

    public Map<String, Object> riskDetail(Long studentId) {
        RiskPrediction q = new RiskPrediction(); q.setStudentId(studentId);
        StudentAcademic aq = new StudentAcademic(); aq.setStudentId(studentId);
        StudentAcademic academic = studentAcademicMapper.selectAll(aq).stream().findFirst().orElse(null);
        RiskPrediction rp = riskPredictionMapper.selectAll(q).stream().findFirst().orElse(null);

        Map<String, Object> m = new HashMap<>();
        m.put("risk", rp);
        m.put("academic", academic);
        return m;
    }

    public Map<String, Object> trend(Long studentId) {
        RiskPrediction rq = new RiskPrediction(); rq.setStudentId(studentId);
        List<RiskPrediction> predictions = riskPredictionMapper.selectAll(rq).stream().limit(30).toList();

        StudentAcademic aq = new StudentAcademic(); aq.setStudentId(studentId);
        StudentAcademic academic = studentAcademicMapper.selectAll(aq).stream().findFirst().orElse(null);

        ExamRecord eq = new ExamRecord(); eq.setStudentId(studentId);
        List<ExamRecord> exams = examRecordMapper.selectAll(eq).stream().limit(30).toList();

        HomeworkSubmission hq = new HomeworkSubmission(); hq.setStudentId(studentId);
        List<HomeworkSubmission> homeworks = homeworkSubmissionMapper.selectAll(hq).stream().limit(30).toList();

        Map<String, Object> m = new HashMap<>();
        m.put("risk", predictions);
        m.put("gpa", academic == null ? List.of() : List.of(academic.getGpa()));
        m.put("examScores", exams.stream().map(ExamRecord::getScore).toList());
        m.put("homeworkScores", homeworks.stream().map(HomeworkSubmission::getScore).toList());
        return m;
    }


    public void updateAcademic(Long studentId, BigDecimal earnedCredit, BigDecimal gpa, String gpaColor) {
        StudentAcademic q = new StudentAcademic(); q.setStudentId(studentId);
        StudentAcademic academic = studentAcademicMapper.selectAll(q).stream().findFirst().orElse(null);
        if (academic == null) {
            academic = new StudentAcademic();
            academic.setStudentId(studentId);
            academic.setTotalCredit(new BigDecimal("20"));
            academic.setUpdateTime(java.time.LocalDateTime.now());
            studentAcademicMapper.insert(academic);
        }
        academic.setEarnedCredit(earnedCredit);
        academic.setGpa(gpa);
        academic.setGpaColor(gpaColor);
        academic.setUpdateTime(java.time.LocalDateTime.now());
        studentAcademicMapper.updateById(academic);

        for (Course c : courses(studentId)) {
            StudentFeature f = featureExtractor.extractAndSave(studentId, c.getId());
            riskPredictor.predictAndSave(f);
        }
    }

    private String parseExamRemark(String remark) {
        if (remark == null || remark.isEmpty()) return "";
        if (!remark.trim().startsWith("{")) return remark;
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            java.util.Map<String, Object> m = mapper.readValue(remark, java.util.Map.class);
            Object message = m.get("message");
            return message == null ? remark : String.valueOf(message);
        } catch (Exception e) {
            return remark;
        }
    }

}
