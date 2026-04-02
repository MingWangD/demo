package com.example.controller;

import com.example.common.GpaColorUtil;
import com.example.common.Result;
import com.example.security.AuthContext;
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

    private Long resolveStudentId(Long studentId) {
        return "STUDENT".equals(AuthContext.role()) ? AuthContext.userId() : studentId;
    }

    @GetMapping("/overview")
    public Result overview(@RequestParam Long studentId) { return Result.success(studentService.overview(resolveStudentId(studentId))); }

    @GetMapping("/courses")
    public Result courses(@RequestParam Long studentId) { return Result.success(studentService.courses(resolveStudentId(studentId))); }

    @GetMapping("/homework")
    public Result homework(@RequestParam Long studentId, @RequestParam Long courseId) { return Result.success(studentService.courseHomework(resolveStudentId(studentId), courseId)); }

    @GetMapping("/exams")
    public Result exams(@RequestParam Long studentId, @RequestParam Long courseId) { return Result.success(studentService.courseExam(resolveStudentId(studentId), courseId)); }

    @GetMapping("/risk-detail")
    public Result riskDetail(@RequestParam Long studentId) { return Result.success(studentService.riskDetail(resolveStudentId(studentId))); }

    @GetMapping("/trend")
    public Result trend(@RequestParam Long studentId) { return Result.success(studentService.trend(resolveStudentId(studentId))); }

    @PostMapping("/update-academic")
    public Result updateAcademic(@RequestParam Long studentId, @RequestParam BigDecimal earnedCredit, @RequestParam BigDecimal gpa) {
        studentService.updateAcademic(studentId, earnedCredit, gpa, GpaColorUtil.resolveColor(gpa));
        return Result.success();
    }
}
