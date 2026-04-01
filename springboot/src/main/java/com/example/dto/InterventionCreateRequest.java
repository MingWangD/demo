package com.example.dto;

public class InterventionCreateRequest {
    private Long studentId;
    private Long teacherId;
    private Long courseId;
    private Long warningId;
    private String interventionType;
    private String interventionContent;
    private String interventionResult;

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public Long getWarningId() { return warningId; }
    public void setWarningId(Long warningId) { this.warningId = warningId; }
    public String getInterventionType() { return interventionType; }
    public void setInterventionType(String interventionType) { this.interventionType = interventionType; }
    public String getInterventionContent() { return interventionContent; }
    public void setInterventionContent(String interventionContent) { this.interventionContent = interventionContent; }
    public String getInterventionResult() { return interventionResult; }
    public void setInterventionResult(String interventionResult) { this.interventionResult = interventionResult; }
}
