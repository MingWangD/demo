package com.example.controller;

import com.example.common.Result;
import com.example.dto.HomeworkSubmitRequest;
import com.example.entity.Homework;
import com.example.service.HomeworkService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/homework")
public class HomeworkController {
    @Resource private HomeworkService homeworkService;

    @GetMapping("/list")
    public Result list(@RequestParam Long courseId) { return Result.success(homeworkService.listByCourse(courseId)); }

    @PostMapping("/create")
    public Result create(@RequestBody Homework homework) { homeworkService.create(homework); return Result.success(); }

    @PostMapping("/submit")
    public Result submit(@RequestBody HomeworkSubmitRequest request) { homeworkService.submit(request); return Result.success(); }
}
