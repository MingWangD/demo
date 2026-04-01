package com.example.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RiskPrediction {
    private Long id;
    private Long studentId;
    private Long courseId;
    private BigDecimal riskProbability;
    private String riskLabel;
    private String riskLevel;
    private String warningColor;
    private String mainReason;
    private String modelVersion;
    private LocalDateTime predictTime;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public BigDecimal getRiskProbability() {
        return riskProbability;
    }

    public void setRiskProbability(BigDecimal riskProbability) {
        this.riskProbability = riskProbability;
    }

    public String getRiskLabel() {
        return riskLabel;
    }

    public void setRiskLabel(String riskLabel) {
        this.riskLabel = riskLabel;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getWarningColor() {
        return warningColor;
    }

    public void setWarningColor(String warningColor) {
        this.warningColor = warningColor;
    }

    public String getMainReason() {
        return mainReason;
    }

    public void setMainReason(String mainReason) {
        this.mainReason = mainReason;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public LocalDateTime getPredictTime() {
        return predictTime;
    }

    public void setPredictTime(LocalDateTime predictTime) {
        this.predictTime = predictTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

}