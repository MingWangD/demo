package com.example.controller;

import com.example.common.Result;
import com.example.service.TeacherService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {
    @Resource private TeacherService teacherService;

    @GetMapping("/courses")
    public Result courses(@RequestParam Long teacherId) {
        return Result.success(teacherService.courseList(teacherId));
    }

    @GetMapping("/student-detail")
    public Result studentDetail(@RequestParam Long studentId, @RequestParam(required = false) Long courseId) {
        return Result.success(teacherService.studentDetail(studentId, courseId));
    }

    @GetMapping("/high-risk")
    public Result highRisk(@RequestParam(required = false) Long courseId,
                           @RequestParam(required = false) String riskLevel,
                           @RequestParam(required = false) String gpaColor) {
        return Result.success(teacherService.highRisk(courseId, riskLevel, gpaColor));
    }

    @GetMapping("/homework-manage")
    public Result homeworkManage(@RequestParam Long courseId) { return Result.success(teacherService.homeworkManage(courseId)); }

    @PostMapping("/homework-grade")
    public Result grade(@RequestParam Long submissionId, @RequestParam BigDecimal score, @RequestParam(required = false) String comment) {
        teacherService.gradeHomework(submissionId, score, comment);
        return Result.success();
    }

    @GetMapping("/exam-manage")
    public Result examManage(@RequestParam Long courseId) { return Result.success(teacherService.examManage(courseId)); }
}
