package com.example.entity;

import java.time.LocalDateTime;

public class StudentCourse {
    private Long id;
    private Long studentId;
    private Long courseId;
    private LocalDateTime selectTime;
    private String status;

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

    public LocalDateTime getSelectTime() {
        return selectTime;
    }

    public void setSelectTime(LocalDateTime selectTime) {
        this.selectTime = selectTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}