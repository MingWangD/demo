package com.example.scheduler;

import com.example.service.RiskService;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RiskScheduler {

    @Resource private RiskService riskService;

    @Scheduled(cron = "0 0 2 * * ?")
    public void recomputeDaily() {
        riskService.predictAllStudents();
    }
}
