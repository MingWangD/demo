package com.example.controller;

import com.example.common.Result;
import com.example.dto.InterventionCreateRequest;
import com.example.service.InterventionService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/intervention")
public class InterventionController {
    @Resource private InterventionService interventionService;

    @PostMapping("/add")
    public Result add(@RequestBody InterventionCreateRequest request) { interventionService.add(request); return Result.success(); }

    @GetMapping("/list")
    public Result list(@RequestParam(required = false) Long studentId, @RequestParam(required = false) Long courseId) {
        return Result.success(interventionService.list(studentId, courseId));
    }
}
