package com.example.algorithm;

import com.example.entity.Course;
import com.example.entity.Exam;
import com.example.entity.ExamRecord;
import com.example.entity.Homework;
import com.example.entity.HomeworkSubmission;
import com.example.entity.RiskPrediction;
import com.example.entity.StudentAcademic;
import com.example.entity.StudentAttendance;
import com.example.entity.StudentFeature;
import com.example.mapper.CourseMapper;
import com.example.mapper.ExamMapper;
import com.example.mapper.ExamRecordMapper;
import com.example.mapper.HomeworkMapper;
import com.example.mapper.HomeworkSubmissionMapper;
import com.example.mapper.RiskPredictionMapper;
import com.example.mapper.StudentAcademicMapper;
import com.example.mapper.StudentAttendanceMapper;
import com.example.mapper.StudentFeatureMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FeatureExtractor {

    @Resource
    private StudentAttendanceMapper attendanceMapper;
    @Resource
    private StudentAcademicMapper academicMapper;
    @Resource
    private HomeworkSubmissionMapper homeworkSubmissionMapper;
    @Resource
    private HomeworkMapper homeworkMapper;
    @Resource
    private ExamRecordMapper examRecordMapper;
    @Resource
    private ExamMapper examMapper;
    @Resource
    private RiskPredictionMapper riskPredictionMapper;
    @Resource
    private StudentFeatureMapper studentFeatureMapper;
    @Resource
    private CourseMapper courseMapper;

    public StudentFeature extractAndSave(Long studentId, Long courseId) {
        StudentFeature feature = extract(studentId, courseId);
        studentFeatureMapper.insert(feature);
        return feature;
    }

    public StudentFeature extract(Long studentId, Long courseId) {
        StudentAttendance attendanceQuery = new StudentAttendance();
        attendanceQuery.setStudentId(studentId);
        attendanceQuery.setCourseId(courseId);
        int attendanceCount = attendanceMapper.selectAll(attendanceQuery).size();

        int attendanceRequired = 10;
        if (courseId != null) {
            Course course = courseMapper.selectById(courseId);
            if (course != null && course.getAttendanceRequiredCount() != null) {
                attendanceRequired = course.getAttendanceRequiredCount();
            }
        }

        Homework hwQuery = new Homework();
        hwQuery.setCourseId(courseId);
        List<Long> homeworkIds = homeworkMapper.selectAll(hwQuery).stream().map(Homework::getId).collect(Collectors.toList());
        int totalHomework = homeworkIds.size();

        int submittedHomework = 0;
        BigDecimal homeworkAvg = BigDecimal.ZERO;
        if (!homeworkIds.isEmpty()) {
            List<HomeworkSubmission> submissions = homeworkSubmissionMapper.selectAll(new HomeworkSubmission()).stream()
                    .filter(s -> studentId.equals(s.getStudentId()) && homeworkIds.contains(s.getHomeworkId()))
                    .collect(Collectors.toList());
            submittedHomework = (int) submissions.stream()
                    .filter(s -> "SUBMITTED".equals(s.getStatus()) || "GRADED".equals(s.getStatus()))
                    .count();
            List<BigDecimal> scores = submissions.stream()
                    .filter(s -> s.getScore() != null)
                    .map(s -> {
                        Homework hw = homeworkMapper.selectById(s.getHomeworkId());
                        if (hw != null && hw.getTotalScore() != null && hw.getTotalScore().compareTo(BigDecimal.ZERO) > 0) {
                            return s.getScore().multiply(new BigDecimal("100")).divide(hw.getTotalScore(), 2, RoundingMode.HALF_UP);
                        }
                        return s.getScore();
                    })
                    .collect(Collectors.toList());
            if (!scores.isEmpty()) {
                homeworkAvg = BigDecimal.valueOf(scores.stream().mapToDouble(BigDecimal::doubleValue).average().orElse(0));
            }
        }

        Exam exQuery = new Exam();
        exQuery.setCourseId(courseId);
        List<Long> examIds = examMapper.selectAll(exQuery).stream().map(Exam::getId).collect(Collectors.toList());
        List<ExamRecord> examRecords = examRecordMapper.selectAll(new ExamRecord()).stream()
                .filter(e -> studentId.equals(e.getStudentId()) && (courseId == null || examIds.contains(e.getExamId())))
                .collect(Collectors.toList());
        int absentExamCount = (int) examRecords.stream().filter(e -> "ABSENT".equals(e.getStatus())).count();
        BigDecimal examAvg = BigDecimal.valueOf(examRecords.stream()
                .map(ExamRecord::getScore)
                .filter(s -> s != null)
                .mapToDouble(BigDecimal::doubleValue)
                .average().orElse(0));

        StudentAcademic academicQuery = new StudentAcademic();
        academicQuery.setStudentId(studentId);
        StudentAcademic academic = academicMapper.selectAll(academicQuery).stream().findFirst().orElse(null);

        RiskPrediction riskQuery = new RiskPrediction();
        riskQuery.setStudentId(studentId);
        String trend = riskPredictionMapper.selectAll(riskQuery).stream()
                .limit(2)
                .sorted(Comparator.comparing(RiskPrediction::getPredictTime, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
                    if (list.size() < 2 || list.get(0).getRiskProbability() == null || list.get(1).getRiskProbability() == null) {
                        return "FLAT";
                    }
                    return list.get(0).getRiskProbability().compareTo(list.get(1).getRiskProbability()) >= 0 ? "UP" : "DOWN";
                }));

        StudentFeature feature = new StudentFeature();
        feature.setStudentId(studentId);
        feature.setCourseId(courseId);
        feature.setAttendanceCount(attendanceCount);
        feature.setAttendanceRate(div(attendanceCount, Math.max(attendanceRequired, 1)));
        feature.setHomeworkSubmitRate(div(submittedHomework, Math.max(totalHomework, 1)));
        feature.setHomeworkAvgScore(homeworkAvg.setScale(2, RoundingMode.HALF_UP));
        feature.setExamAvgScore(examAvg.setScale(2, RoundingMode.HALF_UP));
        feature.setEarnedCredit(academic == null ? BigDecimal.ZERO : academic.getEarnedCredit());
        feature.setGpa(academic == null ? BigDecimal.ZERO : academic.getGpa());
        feature.setMissingHomeworkCount(Math.max(totalHomework - submittedHomework, 0));
        feature.setAbsentExamCount(absentExamCount);
        feature.setRecentRiskTrend(trend);
        feature.setFeatureDate(LocalDate.now());
        feature.setCreateTime(LocalDateTime.now());
        return feature;
    }

    private BigDecimal div(int a, int b) {
        return BigDecimal.valueOf(a).divide(BigDecimal.valueOf(b), 4, RoundingMode.HALF_UP);
    }
}
