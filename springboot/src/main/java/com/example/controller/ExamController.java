package com.example.controller;

import com.example.common.Result;
import com.example.dto.ExamSubmitRequest;
import com.example.entity.Exam;
import com.example.security.AuthContext;
import com.example.service.ExamService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exam")
public class ExamController {
    @Resource private ExamService examService;

    @GetMapping("/list")
    public Result list(@RequestParam Long courseId) { return Result.success(examService.listByCourse(courseId)); }

    @PostMapping("/create")
    public Result create(@RequestBody Exam exam) { examService.create(exam, AuthContext.userId()); return Result.success(); }

    @PostMapping("/submit")
    public Result submit(@RequestBody ExamSubmitRequest request) {
        if ("STUDENT".equals(AuthContext.role())) {
            request.setStudentId(AuthContext.userId());
        }
        examService.submit(request);
        return Result.success();
    }
}
