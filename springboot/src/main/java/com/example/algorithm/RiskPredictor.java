package com.example.algorithm;

import com.example.entity.RiskPrediction;
import com.example.entity.StudentFeature;
import com.example.entity.WarningRecord;
import com.example.mapper.RiskPredictionMapper;
import com.example.mapper.WarningRecordMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class RiskPredictor {

    @Resource private LogisticRegression logisticRegression;
    @Resource private RiskPredictionMapper riskPredictionMapper;
    @Resource private WarningRecordMapper warningRecordMapper;

    public RiskPrediction predictAndSave(StudentFeature feature) {
        BigDecimal probability = logisticRegression.predictProbability(feature);
        boolean gpaHighRisk = feature.getGpa() != null && feature.getGpa().compareTo(new BigDecimal("1.5")) < 0;
        boolean gpaMediumRisk = feature.getGpa() != null && feature.getGpa().compareTo(new BigDecimal("2.0")) < 0;

        RiskPrediction prediction = new RiskPrediction();
        prediction.setStudentId(feature.getStudentId());
        prediction.setCourseId(feature.getCourseId());
        prediction.setRiskProbability(probability);

        if (probability.compareTo(new BigDecimal("0.8")) >= 0 || gpaHighRisk) {
            prediction.setRiskLevel("HIGH");
            prediction.setWarningColor("RED");
            prediction.setRiskLabel("高风险");
        } else if (probability.compareTo(new BigDecimal("0.5")) >= 0 || gpaMediumRisk) {
            prediction.setRiskLevel("MEDIUM");
            prediction.setWarningColor("ORANGE");
            prediction.setRiskLabel("中风险");
        } else {
            prediction.setRiskLevel("LOW");
            prediction.setWarningColor("GREEN");
            prediction.setRiskLabel("低风险");
        }

        prediction.setMainReason(buildReason(feature));
        prediction.setModelVersion("LR-1.2.0");
        prediction.setPredictTime(LocalDateTime.now());
        prediction.setCreateTime(LocalDateTime.now());
        riskPredictionMapper.insert(prediction);

        WarningRecord warning = new WarningRecord();
        warning.setStudentId(feature.getStudentId());
        warning.setCourseId(feature.getCourseId());
        warning.setPredictionId(prediction.getId());
        warning.setWarningType("RISK_WARNING");
        warning.setWarningLevel(prediction.getRiskLevel());
        warning.setWarningContent(prediction.getMainReason());
        warning.setStatus("OPEN");
        warning.setCreateTime(LocalDateTime.now());
        warning.setUpdateTime(LocalDateTime.now());
        warningRecordMapper.insert(warning);
        return prediction;
    }

    private String buildReason(StudentFeature feature) {
        List<String> reasons = new ArrayList<>();
        if (feature.getAttendanceRate() != null && feature.getAttendanceRate().compareTo(new BigDecimal("0.67")) < 0) {
            reasons.add("出勤次数偏低");
        }
        if (feature.getHomeworkSubmitRate() != null && feature.getHomeworkSubmitRate().compareTo(new BigDecimal("0.70")) < 0) {
            reasons.add("作业提交率低");
        }
        if (feature.getHomeworkAvgScore() != null && feature.getHomeworkAvgScore().compareTo(new BigDecimal("70")) < 0) {
            reasons.add("作业平均分偏低");
        }
        if (feature.getGpa() != null && feature.getGpa().compareTo(new BigDecimal("2.0")) < 0) {
            reasons.add("GPA低于学业预警区间");
        }
        return reasons.isEmpty() ? "当前学习状态稳定" : String.join("，", reasons);
    }
}
