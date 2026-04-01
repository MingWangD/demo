package com.example.dto;

import java.math.BigDecimal;

public class ExamSubmitRequest {
    private Long examId;
    private Long studentId;
    private BigDecimal score;

    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }
}
