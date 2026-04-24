package com.example.controller;

import com.example.common.Result;
import com.example.entity.StudentAttendance;
import com.example.security.AuthContext;
import com.example.service.TeacherService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {
    @Resource private TeacherService teacherService;

    @GetMapping("/courses")
    public Result courses() {
        return Result.success(teacherService.courseList(AuthContext.userId()));
    }

    @GetMapping("/student-detail")
    public Result studentDetail(@RequestParam Long studentId, @RequestParam(required = false) Long courseId) {
        return Result.success(teacherService.studentDetail(AuthContext.userId(), studentId, courseId));
    }

    @GetMapping("/attendance")
    public Result attendance(@RequestParam Long courseId, @RequestParam(required = false) Long studentId) {
        return Result.success(teacherService.attendanceList(AuthContext.userId(), courseId, studentId));
    }

    @GetMapping("/attendance/stats")
    public Result attendanceStats(@RequestParam Long courseId) {
        return Result.success(teacherService.attendanceStats(AuthContext.userId(), courseId));
    }

    @PostMapping("/attendance/required-count")
    public Result updateAttendanceRequiredCount(@RequestParam Long courseId,
                                                @RequestParam Integer attendanceRequiredCount) {
        return Result.success(teacherService.updateAttendanceRequiredCount(AuthContext.userId(), courseId, attendanceRequiredCount));
    }

    @PostMapping("/attendance/change")
    public Result changeAttendance(@RequestParam Long courseId,
                                   @RequestParam Long studentId,
                                   @RequestParam Integer delta) {
        return Result.success(teacherService.changeAttendanceCount(AuthContext.userId(), courseId, studentId, delta));
    }

    @PostMapping("/attendance/save")
    public Result saveAttendance(@RequestBody StudentAttendance attendance) {
        return Result.success(teacherService.saveAttendance(AuthContext.userId(), attendance));
    }

    @DeleteMapping("/attendance/{id}")
    public Result deleteAttendance(@PathVariable Long id) {
        teacherService.deleteAttendance(AuthContext.userId(), id);
        return Result.success();
    }

    @GetMapping("/course-students")
    public Result courseStudents(@RequestParam Long courseId) {
        return Result.success(teacherService.courseStudents(AuthContext.userId(), courseId));
    }

    @GetMapping("/high-risk")
    public Result highRisk(@RequestParam(required = false) Long courseId,
                           @RequestParam(required = false) String riskLevel,
                           @RequestParam(required = false) String gpaColor,
                           @RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(teacherService.highRisk(AuthContext.userId(), courseId, riskLevel, gpaColor, pageNum, pageSize));
    }

    @GetMapping("/homework-manage")
    public Result homeworkManage(@RequestParam Long courseId) { return Result.success(teacherService.homeworkManage(AuthContext.userId(), courseId)); }

    @PostMapping("/homework-grade")
    public Result grade(@RequestParam Long submissionId, @RequestParam BigDecimal score, @RequestParam(required = false) String comment) {
        teacherService.gradeHomework(AuthContext.userId(), submissionId, score, comment);
        return Result.success();
    }

    @GetMapping("/exam-manage")
    public Result examManage(@RequestParam Long courseId) { return Result.success(teacherService.examManage(AuthContext.userId(), courseId)); }

    @PostMapping("/exam-grade")
    public Result examGrade(@RequestParam Long recordId, @RequestParam BigDecimal subjectiveScore, @RequestParam(required = false) String comment) {
        teacherService.gradeExam(AuthContext.userId(), recordId, subjectiveScore, comment);
        return Result.success();
    }

    @GetMapping("/homework-submission-detail")
    public Result homeworkSubmissionDetail(@RequestParam Long submissionId) {
        return Result.success(teacherService.homeworkSubmissionDetail(AuthContext.userId(), submissionId));
    }

    @PostMapping("/homework-grade-detail")
    public Result homeworkGradeDetail(@RequestParam Long submissionId,
                                      @RequestParam String questionScores,
                                      @RequestParam(required = false) String comment) {
        teacherService.gradeHomeworkByQuestion(AuthContext.userId(), submissionId, questionScores, comment);
        return Result.success();
    }

    @GetMapping("/exam-record-detail")
    public Result examRecordDetail(@RequestParam Long recordId) {
        return Result.success(teacherService.examRecordDetail(AuthContext.userId(), recordId));
    }

    @PostMapping("/exam-grade-detail")
    public Result examGradeDetail(@RequestParam Long recordId,
                                  @RequestParam String questionScores,
                                  @RequestParam(required = false) String comment) {
        teacherService.gradeExamByQuestion(AuthContext.userId(), recordId, questionScores, comment);
        return Result.success();
    }
}
