package com.example.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class StudentFeature {
    private Long id;
    private Long studentId;
    private Long courseId;
    private Integer attendanceCount;
    private BigDecimal attendanceRate;
    private BigDecimal homeworkSubmitRate;
    private BigDecimal homeworkAvgScore;
    private BigDecimal examAvgScore;
    private BigDecimal earnedCredit;
    private BigDecimal gpa;
    private Integer missingHomeworkCount;
    private Integer absentExamCount;
    private String recentRiskTrend;
    private LocalDate featureDate;
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

    public Integer getAttendanceCount() {
        return attendanceCount;
    }

    public void setAttendanceCount(Integer attendanceCount) {
        this.attendanceCount = attendanceCount;
    }

    public BigDecimal getAttendanceRate() {
        return attendanceRate;
    }

    public void setAttendanceRate(BigDecimal attendanceRate) {
        this.attendanceRate = attendanceRate;
    }

    public BigDecimal getHomeworkSubmitRate() {
        return homeworkSubmitRate;
    }

    public void setHomeworkSubmitRate(BigDecimal homeworkSubmitRate) {
        this.homeworkSubmitRate = homeworkSubmitRate;
    }

    public BigDecimal getHomeworkAvgScore() {
        return homeworkAvgScore;
    }

    public void setHomeworkAvgScore(BigDecimal homeworkAvgScore) {
        this.homeworkAvgScore = homeworkAvgScore;
    }

    public BigDecimal getExamAvgScore() {
        return examAvgScore;
    }

    public void setExamAvgScore(BigDecimal examAvgScore) {
        this.examAvgScore = examAvgScore;
    }

    public BigDecimal getEarnedCredit() {
        return earnedCredit;
    }

    public void setEarnedCredit(BigDecimal earnedCredit) {
        this.earnedCredit = earnedCredit;
    }

    public BigDecimal getGpa() {
        return gpa;
    }

    public void setGpa(BigDecimal gpa) {
        this.gpa = gpa;
    }

    public Integer getMissingHomeworkCount() {
        return missingHomeworkCount;
    }

    public void setMissingHomeworkCount(Integer missingHomeworkCount) {
        this.missingHomeworkCount = missingHomeworkCount;
    }

    public Integer getAbsentExamCount() {
        return absentExamCount;
    }

    public void setAbsentExamCount(Integer absentExamCount) {
        this.absentExamCount = absentExamCount;
    }

    public String getRecentRiskTrend() {
        return recentRiskTrend;
    }

    public void setRecentRiskTrend(String recentRiskTrend) {
        this.recentRiskTrend = recentRiskTrend;
    }

    public LocalDate getFeatureDate() {
        return featureDate;
    }

    public void setFeatureDate(LocalDate featureDate) {
        this.featureDate = featureDate;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

}