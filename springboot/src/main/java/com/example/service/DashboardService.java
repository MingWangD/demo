package com.example.service;

import com.example.entity.Course;
import com.example.entity.Homework;
import com.example.entity.HomeworkSubmission;
import com.example.entity.RiskPrediction;
import com.example.entity.StudentAcademic;
import com.example.entity.StudentCourse;
import com.example.entity.WarningRecord;
import com.example.mapper.CourseMapper;
import com.example.mapper.HomeworkMapper;
import com.example.mapper.HomeworkSubmissionMapper;
import com.example.mapper.RiskPredictionMapper;
import com.example.mapper.StudentAcademicMapper;
import com.example.mapper.StudentCourseMapper;
import com.example.mapper.WarningRecordMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DashboardService {
    @Resource private RiskPredictionMapper riskPredictionMapper;
    @Resource private StudentAcademicMapper studentAcademicMapper;
    @Resource private WarningRecordMapper warningRecordMapper;
    @Resource private CourseMapper courseMapper;
    @Resource private HomeworkMapper homeworkMapper;
    @Resource private HomeworkSubmissionMapper homeworkSubmissionMapper;
    @Resource private StudentCourseMapper studentCourseMapper;

    public Map<String, Object> teacherDashboard(Long teacherId) {
        Course courseQuery = new Course();
        courseQuery.setTeacherId(teacherId);
        List<Course> teacherCourses = courseMapper.selectAll(courseQuery);
        Set<Long> teacherCourseIds = teacherCourses.stream().map(Course::getId).collect(Collectors.toSet());

        Set<Long> teacherStudentIds = studentCourseMapper.selectAll(new StudentCourse()).stream()
                .filter(binding -> teacherCourseIds.contains(binding.getCourseId()))
                .map(StudentCourse::getStudentId)
                .collect(Collectors.toSet());

        List<RiskPrediction> allRisk = riskPredictionMapper.selectAll(new RiskPrediction()).stream()
                .filter(risk -> risk.getCourseId() == null || teacherCourseIds.contains(risk.getCourseId()))
                .toList();

        List<RiskPrediction> latestByStudentCourse = new ArrayList<>(allRisk.stream()
                .collect(Collectors.toMap(
                        risk -> risk.getStudentId() + "_" + (risk.getCourseId() == null ? "null" : risk.getCourseId()),
                        risk -> risk,
                        this::pickLatestPrediction
                ))
                .values());

        Map<Long, RiskPrediction> riskByStudent = latestByStudentCourse.stream()
                .collect(Collectors.toMap(
                        RiskPrediction::getStudentId,
                        risk -> risk,
                        this::pickMoreSeverePrediction
                ));

        for (Long studentId : teacherStudentIds) {
            riskByStudent.computeIfAbsent(studentId, this::buildDefaultLowRiskPrediction);
        }

        List<RiskPrediction> latestByStudent = riskByStudent.values().stream()
                .map(this::normalizeRiskPrediction)
                .sorted(this::compareRiskPrediction)
                .toList();

        Map<String, Long> riskCount = latestByStudent.stream()
                .collect(Collectors.groupingBy(risk -> Objects.requireNonNullElse(risk.getRiskLevel(), "LOW"), Collectors.counting()));

        List<StudentAcademic> academics = studentAcademicMapper.selectAll(new StudentAcademic()).stream()
                .filter(academic -> teacherStudentIds.contains(academic.getStudentId()))
                .toList();
        Map<String, Long> gpaColors = academics.stream()
                .collect(Collectors.groupingBy(StudentAcademic::getGpaColor, Collectors.counting()));

        List<WarningRecord> warnings = warningRecordMapper.selectAll(new WarningRecord()).stream()
                .filter(warning -> warning.getCourseId() == null || teacherCourseIds.contains(warning.getCourseId()))
                .sorted(Comparator.comparing(WarningRecord::getCreateTime, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        Map<String, Object> map = new HashMap<>();
        map.put("highRiskCount", riskCount.getOrDefault("HIGH", 0L));
        map.put("mediumRiskCount", riskCount.getOrDefault("MEDIUM", 0L));
        map.put("lowRiskCount", riskCount.getOrDefault("LOW", 0L));
        map.put("gpaColor", gpaColors);
        map.put("recentWarnings", warnings.stream().limit(10).toList());
        map.put("highRiskStudents", latestByStudent.stream().filter(risk -> "HIGH".equals(risk.getRiskLevel())).toList());
        map.put("homeworkTrend", buildHomeworkTrend(teacherCourseIds));
        return map;
    }

    private List<Map<String, Object>> buildHomeworkTrend(Set<Long> teacherCourseIds) {
        if (teacherCourseIds.isEmpty()) {
            return List.of();
        }

        List<Homework> homeworks = homeworkMapper.selectAll(new Homework()).stream()
                .filter(homework -> teacherCourseIds.contains(homework.getCourseId()))
                .filter(homework -> homework.getCreateTime() != null || homework.getDeadline() != null)
                .sorted(Comparator.comparing(this::trendDateTime))
                .toList();
        if (homeworks.isEmpty()) {
            return List.of();
        }

        Map<Long, Integer> expectedCountByCourse = studentCourseMapper.selectAll(new StudentCourse()).stream()
                .filter(binding -> teacherCourseIds.contains(binding.getCourseId()))
                .collect(Collectors.groupingBy(
                        StudentCourse::getCourseId,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));

        Map<Long, Long> completedByHomework = homeworkSubmissionMapper.selectAll(new HomeworkSubmission()).stream()
                .filter(submission -> "SUBMITTED".equals(submission.getStatus()) || "GRADED".equals(submission.getStatus()))
                .collect(Collectors.groupingBy(HomeworkSubmission::getHomeworkId, Collectors.counting()));

        Map<LocalDate, int[]> daily = new LinkedHashMap<>();
        for (Homework homework : homeworks) {
            LocalDate date = trendDateTime(homework).toLocalDate();
            int expectedCount = expectedCountByCourse.getOrDefault(homework.getCourseId(), 0);
            int completedCount = completedByHomework.getOrDefault(homework.getId(), 0L).intValue();
            int[] bucket = daily.computeIfAbsent(date, key -> new int[]{0, 0});
            bucket[0] += completedCount;
            bucket[1] += expectedCount;
        }

        return daily.entrySet().stream().map(entry -> {
            int completed = entry.getValue()[0];
            int expected = entry.getValue()[1];
            BigDecimal rate = expected == 0
                    ? BigDecimal.ZERO
                    : BigDecimal.valueOf(completed)
                    .divide(BigDecimal.valueOf(expected), 4, RoundingMode.HALF_UP);
            Map<String, Object> item = new HashMap<>();
            item.put("label", entry.getKey().toString());
            item.put("completionRate", rate);
            item.put("completedCount", completed);
            item.put("expectedCount", expected);
            return item;
        }).toList();
    }

    private int compareRiskPrediction(RiskPrediction left, RiskPrediction right) {
        int severityCompare = Integer.compare(riskPriority(right.getRiskLevel()), riskPriority(left.getRiskLevel()));
        if (severityCompare != 0) {
            return severityCompare;
        }
        BigDecimal leftProbability = left.getRiskProbability() == null ? BigDecimal.ZERO : left.getRiskProbability();
        BigDecimal rightProbability = right.getRiskProbability() == null ? BigDecimal.ZERO : right.getRiskProbability();
        int probabilityCompare = rightProbability.compareTo(leftProbability);
        if (probabilityCompare != 0) {
            return probabilityCompare;
        }
        LocalDateTime leftTime = effectiveTime(left);
        LocalDateTime rightTime = effectiveTime(right);
        if (leftTime == null && rightTime == null) {
            return 0;
        }
        if (leftTime == null) {
            return 1;
        }
        if (rightTime == null) {
            return -1;
        }
        return rightTime.compareTo(leftTime);
    }

    private RiskPrediction pickLatestPrediction(RiskPrediction left, RiskPrediction right) {
        LocalDateTime leftTime = effectiveTime(left);
        LocalDateTime rightTime = effectiveTime(right);
        if (leftTime == null) {
            return right;
        }
        if (rightTime == null) {
            return left;
        }
        return rightTime.isAfter(leftTime) ? right : left;
    }

    private RiskPrediction pickMoreSeverePrediction(RiskPrediction left, RiskPrediction right) {
        int leftPriority = riskPriority(left.getRiskLevel());
        int rightPriority = riskPriority(right.getRiskLevel());
        if (leftPriority != rightPriority) {
            return rightPriority > leftPriority ? right : left;
        }
        BigDecimal leftProbability = left.getRiskProbability() == null ? BigDecimal.ZERO : left.getRiskProbability();
        BigDecimal rightProbability = right.getRiskProbability() == null ? BigDecimal.ZERO : right.getRiskProbability();
        int probabilityCompare = rightProbability.compareTo(leftProbability);
        if (probabilityCompare != 0) {
            return probabilityCompare > 0 ? right : left;
        }
        return pickLatestPrediction(left, right);
    }

    private int riskPriority(String riskLevel) {
        if ("HIGH".equalsIgnoreCase(riskLevel)) {
            return 3;
        }
        if ("MEDIUM".equalsIgnoreCase(riskLevel)) {
            return 2;
        }
        return 1;
    }

    private LocalDateTime effectiveTime(RiskPrediction prediction) {
        return prediction.getPredictTime() == null ? prediction.getCreateTime() : prediction.getPredictTime();
    }

    private LocalDateTime trendDateTime(Homework homework) {
        if (homework.getCreateTime() != null) {
            return homework.getCreateTime();
        }
        if (homework.getDeadline() != null) {
            return homework.getDeadline();
        }
        return LocalDateTime.MIN;
    }

    private RiskPrediction buildDefaultLowRiskPrediction(Long studentId) {
        RiskPrediction prediction = new RiskPrediction();
        prediction.setStudentId(studentId);
        prediction.setRiskProbability(BigDecimal.ZERO);
        prediction.setRiskLabel("低风险");
        prediction.setRiskLevel("LOW");
        prediction.setWarningColor("GREEN");
        prediction.setMainReason("暂无风险预测记录");
        prediction.setModelVersion("SYSTEM-DEFAULT");
        prediction.setPredictTime(LocalDateTime.now());
        prediction.setCreateTime(LocalDateTime.now());
        return prediction;
    }

    private RiskPrediction normalizeRiskPrediction(RiskPrediction source) {
        BigDecimal probability = source.getRiskProbability() == null ? BigDecimal.ZERO : source.getRiskProbability();
        String existingLevel = source.getRiskLevel() == null ? "" : source.getRiskLevel().toUpperCase();

        if (probability.compareTo(new BigDecimal("0.10")) < 0) {
            source.setRiskLevel("LOW");
            source.setRiskLabel("低风险");
            source.setWarningColor("GREEN");
            return source;
        }

        if (existingLevel.isBlank()) {
            if (probability.compareTo(new BigDecimal("0.75")) >= 0) {
                source.setRiskLevel("HIGH");
                source.setRiskLabel("高风险");
                source.setWarningColor("RED");
            } else if (probability.compareTo(new BigDecimal("0.45")) >= 0) {
                source.setRiskLevel("MEDIUM");
                source.setRiskLabel("中风险");
                source.setWarningColor("ORANGE");
            } else {
                source.setRiskLevel("LOW");
                source.setRiskLabel("低风险");
                source.setWarningColor("GREEN");
            }
            return source;
        }

        if ("HIGH".equals(existingLevel)) {
            source.setRiskLevel("HIGH");
            source.setRiskLabel("高风险");
            source.setWarningColor("RED");
        } else if ("MEDIUM".equals(existingLevel)) {
            source.setRiskLevel("MEDIUM");
            source.setRiskLabel("中风险");
            source.setWarningColor("ORANGE");
        } else {
            source.setRiskLevel("LOW");
            source.setRiskLabel("低风险");
            source.setWarningColor("GREEN");
        }
        return source;
    }
}
