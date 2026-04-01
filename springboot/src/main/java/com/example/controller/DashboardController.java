package com.example.controller;

import com.example.common.Result;
import com.example.service.DashboardService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    @Resource private DashboardService dashboardService;

    @GetMapping("/teacher")
    public Result teacher() { return Result.success(dashboardService.teacherDashboard()); }
}
