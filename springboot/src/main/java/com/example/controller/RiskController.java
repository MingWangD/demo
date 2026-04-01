package com.example.controller;

import com.example.common.Result;
import com.example.service.RiskService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/risk")
public class RiskController {
    @Resource private RiskService riskService;

    @PostMapping("/predict")
    public Result predict(@RequestParam Long studentId, @RequestParam(required = false) Long courseId) {
        return Result.success(riskService.predictSingle(studentId, courseId));
    }

    @PostMapping("/predict-all")
    public Result predictAll() {
        return Result.success(riskService.predictAllStudents());
    }
}
