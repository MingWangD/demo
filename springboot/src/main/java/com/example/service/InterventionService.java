package com.example.service;

import com.example.dto.InterventionCreateRequest;
import com.example.entity.InterventionRecord;
import com.example.mapper.InterventionRecordMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InterventionService {
    @Resource private InterventionRecordMapper interventionRecordMapper;

    public void add(InterventionCreateRequest req) {
        InterventionRecord r = new InterventionRecord();
        r.setStudentId(req.getStudentId());
        r.setTeacherId(req.getTeacherId());
        r.setCourseId(req.getCourseId());
        r.setWarningId(req.getWarningId());
        r.setInterventionType(req.getInterventionType());
        r.setInterventionContent(req.getInterventionContent());
        r.setInterventionResult(req.getInterventionResult());
        r.setCreateTime(LocalDateTime.now());
        r.setUpdateTime(LocalDateTime.now());
        interventionRecordMapper.insert(r);
    }

    public List<InterventionRecord> list(Long studentId, Long courseId) {
        InterventionRecord q = new InterventionRecord();
        q.setStudentId(studentId);
        q.setCourseId(courseId);
        return interventionRecordMapper.selectAll(q);
    }
}
