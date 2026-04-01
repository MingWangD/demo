package com.example.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class StudentAcademic {
    private Long id;
    private Long studentId;
    private BigDecimal totalCredit;
    private BigDecimal earnedCredit;
    private BigDecimal gpa;
    private String gpaColor;
    private String riskNote;
    private LocalDateTime updateTime;

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

    public BigDecimal getTotalCredit() {
        return totalCredit;
    }

    public void setTotalCredit(BigDecimal totalCredit) {
        this.totalCredit = totalCredit;
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

    public String getGpaColor() {
        return gpaColor;
    }

    public void setGpaColor(String gpaColor) {
        this.gpaColor = gpaColor;
    }

    public String getRiskNote() {
        return riskNote;
    }

    public void setRiskNote(String riskNote) {
        this.riskNote = riskNote;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

}