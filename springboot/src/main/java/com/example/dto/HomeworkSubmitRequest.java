package com.example.dto;

public class HomeworkSubmitRequest {
    private Long homeworkId;
    private Long studentId;
    private String submitContent;

    public Long getHomeworkId() { return homeworkId; }
    public void setHomeworkId(Long homeworkId) { this.homeworkId = homeworkId; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getSubmitContent() { return submitContent; }
    public void setSubmitContent(String submitContent) { this.submitContent = submitContent; }
}
