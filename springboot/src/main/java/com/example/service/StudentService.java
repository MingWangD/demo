package com.example.service;

import com.example.algorithm.FeatureExtractor;
import com.example.algorithm.RiskPredictor;
import com.example.entity.Course;
import com.example.entity.Exam;
import com.example.entity.ExamQualification;
import com.example.entity.ExamRecord;
import com.example.entity.Homework;
import com.example.entity.HomeworkSubmission;
import com.example.entity.InterventionRecord;
import com.example.entity.RiskPrediction;
import com.example.entity.StudentAcademic;
import com.example.entity.StudentAttendance;
import com.example.entity.StudentCourse;
import com.example.entity.StudentFeature;
import com.example.entity.SysUser;
import com.example.mapper.CourseMapper;
import com.example.mapper.ExamMapper;
import com.example.mapper.ExamQualificationMapper;
import com.example.mapper.ExamRecordMapper;
import com.example.mapper.HomeworkMapper;
import com.example.mapper.HomeworkSubmissionMapper;
import com.example.mapper.InterventionRecordMapper;
import com.example.mapper.RiskPredictionMapper;
import com.example.mapper.StudentAcademicMapper;
import com.example.mapper.StudentAttendanceMapper;
import com.example.mapper.StudentCourseMapper;
import com.example.mapper.SysUserMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    @Resource private InterventionRecordMapper interventionRecordMapper;
    @Resource private ExamService examService;
    @Resource private FeatureExtractor featureExtractor;
    @Resource private RiskPredictor riskPredictor;

    public Map<String, Object> overview(Long studentId) {
        Map<String, Object> result = new HashMap<>();
        SysUser user = sysUserMapper.selectById(studentId);
        StudentAcademic academicQuery = new StudentAcademic();
        academicQuery.setStudentId(studentId);
        StudentAcademic academic = studentAcademicMapper.selectAll(academicQuery).stream().findFirst().orElse(null);

        RiskPrediction riskQuery = new RiskPrediction();
        riskQuery.setStudentId(studentId);
        RiskPrediction latestRisk = riskPredictionMapper.selectAll(riskQuery).stream().findFirst().orElse(null);

        StudentAttendance attendanceQuery = new StudentAttendance();
        attendanceQuery.setStudentId(studentId);
        int attendanceCount = studentAttendanceMapper.selectAll(attendanceQuery).size();

        List<Long> courseIds = courses(studentId).stream().map(Course::getId).toList();
        List<Long> homeworkIds = courseIds.isEmpty()
                ? List.of()
                : homeworkMapper.selectAll(new Homework()).stream()
                .filter(homework -> courseIds.contains(homework.getCourseId()))
                .map(Homework::getId)
                .toList();

        HomeworkSubmission submissionQuery = new HomeworkSubmission();
        submissionQuery.setStudentId(studentId);
        List<HomeworkSubmission> submissions = homeworkSubmissionMapper.selectAll(submissionQuery).stream()
                .filter(submission -> homeworkIds.contains(submission.getHomeworkId()))
                .toList();
        long submittedCount = submissions.stream()
                .filter(submission -> "SUBMITTED".equals(submission.getStatus()) || "GRADED".equals(submission.getStatus()))
                .count();
        BigDecimal submitRate = homeworkIds.isEmpty()
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(submittedCount).divide(BigDecimal.valueOf(homeworkIds.size()), 4, RoundingMode.HALF_UP);

        List<BigDecimal> finalScores = courseIds.stream()
                .map(courseId -> examService.calculateCourseFinalScore(studentId, courseId))
                .filter(Objects::nonNull)
                .toList();
        BigDecimal finalScoreAvg = finalScores.isEmpty()
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(finalScores.stream().mapToDouble(BigDecimal::doubleValue).average().orElse(0D)).setScale(2, RoundingMode.HALF_UP);

        result.put("user", user);
        result.put("earnedCredit", academic == null ? BigDecimal.ZERO : academic.getEarnedCredit());
        result.put("gpa", academic == null ? BigDecimal.ZERO : academic.getGpa());
        result.put("gpaColor", academic == null ? "GREEN" : academic.getGpaColor());
        result.put("riskProbability", latestRisk == null ? BigDecimal.ZERO : latestRisk.getRiskProbability());
        result.put("riskLevel", latestRisk == null ? "LOW" : latestRisk.getRiskLevel());
        result.put("warningColor", latestRisk == null ? "GREEN" : latestRisk.getWarningColor());
        result.put("mainReason", latestRisk == null ? "当前学习状态稳定" : latestRisk.getMainReason());
        result.put("attendanceCount", attendanceCount);
        result.put("homeworkSubmitRate", submitRate);
        result.put("examAvgScore", finalScoreAvg);
        result.put("finalScoreAvg", finalScoreAvg);
        return result;
    }

    public List<Course> courses(Long studentId) {
        StudentCourse query = new StudentCourse();
        query.setStudentId(studentId);
        List<Long> courseIds = studentCourseMapper.selectAll(query).stream().map(StudentCourse::getCourseId).toList();
        if (courseIds.isEmpty()) {
            return Collections.emptyList();
        }
        return courseMapper.selectAll(new Course()).stream()
                .filter(course -> courseIds.contains(course.getId()))
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> courseHomework(Long studentId, Long courseId) {
        Homework query = new Homework();
        query.setCourseId(courseId);
        return homeworkMapper.selectAll(query).stream().map(homework -> {
            HomeworkSubmission submission = homeworkSubmissionMapper.selectByHomeworkAndStudent(homework.getId(), studentId);
            Map<String, Object> map = new HashMap<>();
            map.put("homework", homework);
            map.put("submission", submission);
            return map;
        }).toList();
    }

    public List<Map<String, Object>> courseExam(Long studentId, Long courseId) {
        Exam query = new Exam();
        query.setCourseId(courseId);
        List<Exam> exams = examMapper.selectAll(query).stream()
                .sorted((left, right) -> {
                    int leftSort = ExamService.EXAM_TYPE_MIDTERM.equals(left.getExamType()) ? 0 : 1;
                    int rightSort = ExamService.EXAM_TYPE_MIDTERM.equals(right.getExamType()) ? 0 : 1;
                    return Integer.compare(leftSort, rightSort);
                })
                .toList();

        return exams.stream().map(exam -> {
            examService.recomputeQualification(exam.getId(), studentId);
            ExamQualification qualification = examQualificationMapper.selectByExamAndStudent(exam.getId(), studentId);
            ExamRecord record = examRecordMapper.selectByExamAndStudent(exam.getId(), studentId);

            Map<String, Object> map = new HashMap<>();
            map.put("examId", exam.getId());
            map.put("examName", exam.getExamName());
            map.put("examType", exam.getExamType());
            map.put("examWeight", examService.examWeight(exam.getExamType()));
            map.put("examTime", exam.getExamTime());
            map.put("description", exam.getDescription());
            boolean qualified = qualification != null && Boolean.TRUE.equals(qualification.getIsQualified());
            map.put("isQualified", qualified);
            map.put("qualified", qualified);
            map.put("qualificationReason", qualification == null ? "未生成考试资格" : qualification.getReason());
            map.put("score", record == null ? null : record.getScore());
            map.put("status", record == null ? "NOT_JOINED" : record.getStatus());
            map.put("autoJudgeRemark", record == null ? null : parseExamRemark(record.getRemark()));
            map.put("submitTime", record == null ? null : record.getSubmitTime());
            return map;
        }).toList();
    }

    public Map<String, Object> riskDetail(Long studentId) {
        RiskPrediction riskQuery = new RiskPrediction();
        riskQuery.setStudentId(studentId);
        StudentAcademic academicQuery = new StudentAcademic();
        academicQuery.setStudentId(studentId);

        StudentAcademic academic = studentAcademicMapper.selectAll(academicQuery).stream().findFirst().orElse(null);
        RiskPrediction risk = riskPredictionMapper.selectAll(riskQuery).stream().findFirst().orElse(null);

        InterventionRecord interventionQuery = new InterventionRecord();
        interventionQuery.setStudentId(studentId);
        InterventionRecord latestIntervention = interventionRecordMapper.selectAll(interventionQuery).stream().findFirst().orElse(null);

        Map<String, Object> map = new HashMap<>();
        map.put("risk", risk);
        map.put("academic", academic);
        map.put("latestIntervention", latestIntervention);
        return map;
    }

    public Map<String, Object> trend(Long studentId) {
        RiskPrediction riskQuery = new RiskPrediction();
        riskQuery.setStudentId(studentId);
        List<RiskPrediction> predictions = riskPredictionMapper.selectAll(riskQuery).stream().limit(30).toList();

        StudentAcademic academicQuery = new StudentAcademic();
        academicQuery.setStudentId(studentId);
        StudentAcademic academic = studentAcademicMapper.selectAll(academicQuery).stream().findFirst().orElse(null);

        ExamRecord examRecordQuery = new ExamRecord();
        examRecordQuery.setStudentId(studentId);
        List<ExamRecord> examRecords = examRecordMapper.selectAll(examRecordQuery).stream().limit(30).toList();

        HomeworkSubmission homeworkSubmissionQuery = new HomeworkSubmission();
        homeworkSubmissionQuery.setStudentId(studentId);
        List<HomeworkSubmission> homeworkSubmissions = homeworkSubmissionMapper.selectAll(homeworkSubmissionQuery).stream().limit(30).toList();

        Map<String, Object> map = new HashMap<>();
        map.put("risk", predictions);
        map.put("gpa", academic == null ? List.of() : List.of(academic.getGpa()));
        map.put("examScores", examRecords.stream().map(ExamRecord::getScore).toList());
        map.put("homeworkScores", homeworkSubmissions.stream().map(HomeworkSubmission::getScore).toList());
        return map;
    }

    public void updateAcademic(Long studentId, BigDecimal earnedCredit, BigDecimal gpa, String gpaColor) {
        StudentAcademic query = new StudentAcademic();
        query.setStudentId(studentId);
        StudentAcademic academic = studentAcademicMapper.selectAll(query).stream().findFirst().orElse(null);
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

        for (Course course : courses(studentId)) {
            StudentFeature feature = featureExtractor.extractAndSave(studentId, course.getId());
            riskPredictor.predictAndSave(feature);
        }
    }

    private String parseExamRemark(String remark) {
        if (remark == null || remark.isEmpty()) {
            return "";
        }
        if (!remark.trim().startsWith("{")) {
            return remark;
        }
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            java.util.Map<String, Object> payload = mapper.readValue(remark, java.util.Map.class);
            Object message = payload.get("message");
            return message == null ? remark : String.valueOf(message);
        } catch (Exception e) {
            return remark;
        }
    }
}
