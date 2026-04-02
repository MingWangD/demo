package com.example.service;

import com.example.algorithm.FeatureExtractor;
import com.example.algorithm.RiskPredictor;
import com.example.common.GpaColorUtil;
import com.example.dto.ExamSubmitRequest;
import com.example.entity.*;
import com.example.exception.CustomException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.mapper.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExamService {
    private static final ObjectMapper MAPPER = new ObjectMapper();
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
        Exam q = new Exam(); q.setCourseId(courseId);
        List<Exam> exams = examMapper.selectAll(q);
        exams.forEach(e -> recomputeQualifications(e.getId()));
        return exams;
    }

    public void create(Exam exam, Long teacherId) {
        validateCourseOwner(exam.getCourseId(), teacherId);
        if (exam.getTotalScore() == null || exam.getTotalScore().compareTo(new BigDecimal("100")) != 0) {
            throw new CustomException("考试总分必须为100分");
        }
        exam.setCreateTime(LocalDateTime.now());
        exam.setUpdateTime(LocalDateTime.now());
        exam.setTeacherId(teacherId);
        examMapper.insert(exam);
        recomputeQualifications(exam.getId());
    }

    public void submit(ExamSubmitRequest req) {
        recomputeQualification(req.getExamId(), req.getStudentId());
        ExamQualification qualification = examQualificationMapper.selectByExamAndStudent(req.getExamId(), req.getStudentId());
        if (qualification == null || !Boolean.TRUE.equals(qualification.getIsQualified())) {
            throw new CustomException("无考试资格，后端拒绝提交");
        }
        Exam exam = examMapper.selectById(req.getExamId());
        ExamRecord record = examRecordMapper.selectByExamAndStudent(req.getExamId(), req.getStudentId());
        LocalDateTime now = LocalDateTime.now();
        if (record != null && "FINISHED".equals(record.getStatus())) {
            throw new CustomException("考试已提交，不可修改");
        }
        if (record == null) {
            record = new ExamRecord();
            record.setExamId(req.getExamId());
            record.setStudentId(req.getStudentId());
            record.setCreateTime(now);
        }
        Map<String, Object> payload = readJson(req.getAnswerContent());
        int objectiveAnswered = num(payload.get("objectiveAnswered"));
        int subjectiveCount = count(exam.getDescription(), "主观题");
        BigDecimal autoScore = BigDecimal.valueOf(Math.min(objectiveAnswered * 2, 100));
        record.setScore(autoScore);
        payload.put("objectiveLocked", true);
        payload.put("objectiveScore", autoScore);
        String remark = "系统已自动判客观题得分：" + autoScore;
        if (subjectiveCount > 0) remark += "，主观题待教师批改";
        payload.put("message", remark);
        record.setRemark(remark);
        record.setStatus("FINISHED");
        record.setSubmitTime(now);
        record.setUpdateTime(now);
        if (record.getId() == null) {
            examRecordMapper.insert(record);
        } else {
            examRecordMapper.updateById(record);
        }

        refreshAcademic(req.getStudentId());
        StudentFeature feature = featureExtractor.extractAndSave(req.getStudentId(), exam.getCourseId());
        riskPredictor.predictAndSave(feature);
    }

    public List<Map<String, Object>> manageDetail(Long examId) {
        recomputeQualifications(examId);
        ExamQualification q = new ExamQualification();
        q.setExamId(examId);
        List<ExamQualification> quals = examQualificationMapper.selectAll(q);
        return quals.stream().map(it -> {
            Map<String, Object> m = new HashMap<>();
            m.put("qualification", it);
            m.put("record", examRecordMapper.selectByExamAndStudent(it.getExamId(), it.getStudentId()));
            return m;
        }).toList();
    }

    public void recomputeQualifications(Long examId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) return;
        StudentCourse sc = new StudentCourse();
        sc.setCourseId(exam.getCourseId());
        for (StudentCourse c : studentCourseMapper.selectAll(sc)) {
            recomputeQualification(examId, c.getStudentId());
        }
    }

    public void recomputeQualification(Long examId, Long studentId) {
        Exam exam = examMapper.selectById(examId);
        Course course = courseMapper.selectById(exam.getCourseId());

        StudentAttendance aq = new StudentAttendance();
        aq.setStudentId(studentId);
        aq.setCourseId(course.getId());
        int attendanceCount = studentAttendanceMapper.selectAll(aq).size();
        int required = Optional.ofNullable(course.getAttendanceRequiredCount()).orElse(1);
        BigDecimal rate = BigDecimal.valueOf(attendanceCount).divide(BigDecimal.valueOf(required), 4, RoundingMode.HALF_UP);
        boolean qualified = attendanceCount >= required || rate.compareTo(Optional.ofNullable(course.getExamQualificationRate()).orElse(new BigDecimal("0.67"))) >= 0;

        ExamQualification row = examQualificationMapper.selectByExamAndStudent(examId, studentId);
        if (row == null) {
            row = new ExamQualification();
            row.setExamId(examId);
            row.setStudentId(studentId);
            row.setCreateTime(LocalDateTime.now());
        }
        row.setAttendanceCount(attendanceCount);
        row.setRequiredCount(required);
        row.setQualificationRate(rate);
        row.setIsQualified(qualified);
        row.setReason(qualified ? "出勤达标，可参加考试" : "出勤未达标，暂不具备考试资格");
        if (row.getId() == null) {
            examQualificationMapper.insert(row);
        } else {
            examQualificationMapper.updateById(row);
        }

        ExamRecord record = examRecordMapper.selectByExamAndStudent(examId, studentId);
        if (!qualified && record != null) {
            record.setScore(null);
            record.setSubmitTime(null);
            record.setStatus("NOT_JOINED");
            record.setRemark("出勤未达标，成绩无效");
            record.setUpdateTime(LocalDateTime.now());
            examRecordMapper.updateById(record);
        }
    }

    private void refreshAcademic(Long studentId) {
        ExamRecord eq = new ExamRecord();
        eq.setStudentId(studentId);
        List<ExamRecord> records = examRecordMapper.selectAll(eq);
        double avg = records.stream().map(ExamRecord::getScore).filter(Objects::nonNull).mapToDouble(BigDecimal::doubleValue).average().orElse(0);
        BigDecimal gpa = BigDecimal.valueOf(avg / 25.0).setScale(2, RoundingMode.HALF_UP);

        StudentAcademic q = new StudentAcademic();
        q.setStudentId(studentId);
        StudentAcademic academic = studentAcademicMapper.selectAll(q).stream().findFirst().orElse(null);
        if (academic == null) {
            academic = new StudentAcademic();
            academic.setStudentId(studentId);
            academic.setTotalCredit(new BigDecimal("20"));
            academic.setEarnedCredit(new BigDecimal("0"));
            academic.setUpdateTime(LocalDateTime.now());
            studentAcademicMapper.insert(academic);
        }
        academic.setGpa(gpa);
        academic.setGpaColor(GpaColorUtil.resolveColor(gpa));
        academic.setUpdateTime(LocalDateTime.now());
        studentAcademicMapper.updateById(academic);
    }


    private void validateCourseOwner(Long courseId, Long teacherId) {
        Course c = courseMapper.selectById(courseId);
        if (c == null || !teacherId.equals(c.getTeacherId())) {
            throw new CustomException("无权限操作该课程考试");
        }
    }

    public void undo(Long teacherId, Long examId) {
        Exam exam = examMapper.selectById(examId);
        validateCourseOwner(exam.getCourseId(), teacherId);
        ExamQualification qq = new ExamQualification();
        qq.setExamId(examId);
        examQualificationMapper.selectAll(qq).stream().map(ExamQualification::getId).forEach(examQualificationMapper::deleteById);
        ExamRecord rq = new ExamRecord();
        rq.setExamId(examId);
        examRecordMapper.selectAll(rq).stream().map(ExamRecord::getId).forEach(examRecordMapper::deleteById);
        examMapper.deleteById(examId);
    }

    private int count(String text, String keyword) {
        if (text == null || keyword == null || keyword.isEmpty()) return 0;
        int c = 0, idx = 0;
        while ((idx = text.indexOf(keyword, idx)) >= 0) { c++; idx += keyword.length(); }
        return c;
    }

    private Map<String, Object> readJson(String raw) {
        if (raw == null || raw.isEmpty()) return new HashMap<>();
        try {
            return MAPPER.readValue(raw, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private int num(Object v) {
        if (v == null) return 0;
        if (v instanceof Number n) return n.intValue();
        try { return Integer.parseInt(String.valueOf(v)); } catch (Exception e) { return 0; }
    }
}
