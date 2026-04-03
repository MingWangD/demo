package com.example.service;

import com.example.entity.RiskPrediction;
import com.example.entity.StudentAcademic;
import com.example.entity.WarningRecord;
import com.example.mapper.RiskPredictionMapper;
import com.example.mapper.StudentAcademicMapper;
import com.example.mapper.WarningRecordMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {
    @Resource private RiskPredictionMapper riskPredictionMapper;
    @Resource private StudentAcademicMapper studentAcademicMapper;
    @Resource private WarningRecordMapper warningRecordMapper;

    public Map<String, Object> teacherDashboard() {
        List<RiskPrediction> allRisk = riskPredictionMapper.selectAll(new RiskPrediction());
        List<RiskPrediction> latestRisk = allRisk.stream()
                .collect(Collectors.toMap(
                        r -> r.getStudentId() + "_" + (r.getCourseId() == null ? "null" : r.getCourseId()),
                        r -> r,
                        (a, b) -> {
                            java.time.LocalDateTime at = a.getPredictTime() == null ? a.getCreateTime() : a.getPredictTime();
                            java.time.LocalDateTime bt = b.getPredictTime() == null ? b.getCreateTime() : b.getPredictTime();
                            if (at == null) return b;
                            if (bt == null) return a;
                            return bt.isAfter(at) ? b : a;
                        }
                ))
                .values().stream().toList();
        Map<String, Long> riskCount = latestRisk.stream().collect(Collectors.groupingBy(RiskPrediction::getRiskLevel, Collectors.counting()));

        List<StudentAcademic> academics = studentAcademicMapper.selectAll(new StudentAcademic());
        Map<String, Long> gpaColors = academics.stream().collect(Collectors.groupingBy(StudentAcademic::getGpaColor, Collectors.counting()));

        List<WarningRecord> warnings = warningRecordMapper.selectAll(new WarningRecord());
        List<Map<String, Object>> classTrend = allRisk.stream()
                .filter(r -> r.getRiskProbability() != null)
                .sorted(Comparator.comparing(RiskPrediction::getPredictTime, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.groupingBy(
                        r -> r.getPredictTime() == null ? "未知时间" : r.getPredictTime().toLocalDate().toString(),
                        LinkedHashMap::new,
                        Collectors.mapping(RiskPrediction::getRiskProbability, Collectors.toList())
                ))
                .entrySet().stream()
                .map(e -> {
                    double avg = e.getValue().stream().mapToDouble(v -> v.doubleValue()).average().orElse(0D);
                    Map<String, Object> m = new HashMap<>();
                    m.put("label", e.getKey());
                    m.put("riskProbability", avg);
                    return m;
                })
                .toList();

        Map<String, Object> map = new HashMap<>();
        map.put("highRiskCount", riskCount.getOrDefault("HIGH", 0L));
        map.put("mediumRiskCount", riskCount.getOrDefault("MEDIUM", 0L));
        map.put("lowRiskCount", riskCount.getOrDefault("LOW", 0L));
        map.put("gpaColor", gpaColors);
        map.put("recentWarnings", warnings.stream().limit(10).toList());
        map.put("highRiskStudents", latestRisk.stream().filter(r -> "HIGH".equals(r.getRiskLevel())).toList());
        map.put("riskTrend", classTrend);
        return map;
    }
}
