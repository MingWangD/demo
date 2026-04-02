package com.example.service;

import com.example.entity.*;
import com.example.exception.CustomException;
import com.example.mapper.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeacherService {
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
    @Resource private ExamMapper examMapper;
    @Resource private ExamService examService;

    public List<Course> courseList(Long teacherId) {
        Course q = new Course();
        q.setTeacherId(teacherId);
        return courseMapper.selectAll(q);
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

    public List<Map<String, Object>> highRisk(Long teacherId, Long courseId, String riskLevel, String gpaColor) {
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
        return latestOnly.stream()
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
                    return m;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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
        java.math.BigDecimal objective = record.getScore() == null ? java.math.BigDecimal.ZERO : record.getScore();
        java.math.BigDecimal finalScore = objective.add(subjectiveScore == null ? java.math.BigDecimal.ZERO : subjectiveScore);
        record.setScore(finalScore.min(new java.math.BigDecimal("100")));
        record.setRemark((record.getRemark() == null ? "" : record.getRemark() + "；") + "教师主观题评分：" + (subjectiveScore == null ? 0 : subjectiveScore) + "；" + (comment == null ? "" : comment));
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

    private void validateCourseOwner(Long courseId, Long teacherId) {
        Course c = courseMapper.selectById(courseId);
        if (c == null || !teacherId.equals(c.getTeacherId())) {
            throw new CustomException("无权限操作非本人课程");
        }
    }
}
