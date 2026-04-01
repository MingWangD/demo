package com.example.service;

import com.example.entity.*;
import com.example.mapper.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

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
    @Resource private ExamRecordMapper examRecordMapper;
    @Resource private ExamMapper examMapper;
    @Resource private ExamService examService;

    public List<Course> courseList(Long teacherId) {
        Course q = new Course();
        q.setTeacherId(teacherId);
        return courseMapper.selectAll(q);
    }

    public Map<String, Object> studentDetail(Long studentId, Long courseId) {
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

    public List<Map<String, Object>> highRisk(Long courseId, String riskLevel, String gpaColor) {
        List<RiskPrediction> list = riskPredictionMapper.selectAll(new RiskPrediction());
        return list.stream()
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

    public List<Map<String, Object>> homeworkManage(Long courseId) {
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

    public void gradeHomework(Long submissionId, java.math.BigDecimal score, String comment) {
        HomeworkSubmission sub = homeworkSubmissionMapper.selectById(submissionId);
        sub.setScore(score);
        sub.setTeacherComment(comment);
        sub.setStatus("GRADED");
        sub.setUpdateTime(java.time.LocalDateTime.now());
        homeworkSubmissionMapper.updateById(sub);
    }

    public List<Map<String,Object>> examManage(Long courseId) {
        Exam e = new Exam(); e.setCourseId(courseId);
        return examMapper.selectAll(e).stream().map(ex -> {
            examService.recomputeQualifications(ex.getId());
            Map<String,Object> m = new HashMap<>();
            m.put("exam", ex);
            m.put("details", examService.manageDetail(ex.getId()));
            return m;
        }).toList();
    }
}
