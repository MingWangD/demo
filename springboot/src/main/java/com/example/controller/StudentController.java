package com.example.controller;

import com.example.common.GpaColorUtil;
import com.example.common.Result;
import com.example.service.StudentService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    @Resource private StudentService studentService;

    @GetMapping("/overview")
    public Result overview(@RequestParam Long studentId) { return Result.success(studentService.overview(studentId)); }

    @GetMapping("/courses")
    public Result courses(@RequestParam Long studentId) { return Result.success(studentService.courses(studentId)); }

    @GetMapping("/homework")
    public Result homework(@RequestParam Long studentId, @RequestParam Long courseId) { return Result.success(studentService.courseHomework(studentId, courseId)); }

    @GetMapping("/exams")
    public Result exams(@RequestParam Long studentId, @RequestParam Long courseId) { return Result.success(studentService.courseExam(studentId, courseId)); }

    @GetMapping("/risk-detail")
    public Result riskDetail(@RequestParam Long studentId) { return Result.success(studentService.riskDetail(studentId)); }

    @GetMapping("/trend")
    public Result trend(@RequestParam Long studentId) { return Result.success(studentService.trend(studentId)); }

    @PostMapping("/update-academic")
    public Result updateAcademic(@RequestParam Long studentId, @RequestParam BigDecimal earnedCredit, @RequestParam BigDecimal gpa) {
        studentService.updateAcademic(studentId, earnedCredit, gpa, GpaColorUtil.resolveColor(gpa));
        return Result.success();
    }
}
