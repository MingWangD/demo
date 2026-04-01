package com.example.entity;

import java.time.LocalDateTime;

public class InterventionRecord {
    private Long id;
    private Long studentId;
    private Long teacherId;
    private Long courseId;
    private Long warningId;
    private String interventionType;
    private String interventionContent;
    private String interventionResult;
    private LocalDateTime createTime;
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

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getWarningId() {
        return warningId;
    }

    public void setWarningId(Long warningId) {
        this.warningId = warningId;
    }

    public String getInterventionType() {
        return interventionType;
    }

    public void setInterventionType(String interventionType) {
        this.interventionType = interventionType;
    }

    public String getInterventionContent() {
        return interventionContent;
    }

    public void setInterventionContent(String interventionContent) {
        this.interventionContent = interventionContent;
    }

    public String getInterventionResult() {
        return interventionResult;
    }

    public void setInterventionResult(String interventionResult) {
        this.interventionResult = interventionResult;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

}