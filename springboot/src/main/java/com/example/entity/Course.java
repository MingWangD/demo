package com.example.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Course {
    private Long id;
    private String courseName;
    private String courseCode;
    private Long teacherId;
    private BigDecimal credit;
    private String semester;
    private Integer totalWeeks;
    private Integer attendanceRequiredCount;
    private BigDecimal examQualificationRate;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public BigDecimal getCredit() {
        return credit;
    }

    public void setCredit(BigDecimal credit) {
        this.credit = credit;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public Integer getTotalWeeks() {
        return totalWeeks;
    }

    public void setTotalWeeks(Integer totalWeeks) {
        this.totalWeeks = totalWeeks;
    }

    public Integer getAttendanceRequiredCount() {
        return attendanceRequiredCount;
    }

    public void setAttendanceRequiredCount(Integer attendanceRequiredCount) {
        this.attendanceRequiredCount = attendanceRequiredCount;
    }

    public BigDecimal getExamQualificationRate() {
        return examQualificationRate;
    }

    public void setExamQualificationRate(BigDecimal examQualificationRate) {
        this.examQualificationRate = examQualificationRate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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