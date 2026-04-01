package com.example.controller;

import com.example.common.Result;
import com.example.service.TeacherService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {
    @Resource private TeacherService teacherService;

    @GetMapping("/courses")
    public Result courses(@RequestParam Long teacherId) {
        return Result.success(teacherService.courseList(teacherId));
    }
}
