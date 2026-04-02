package com.example.service;

import com.example.dto.InterventionCreateRequest;
import com.example.entity.Course;
import com.example.entity.InterventionRecord;
import com.example.entity.SysUser;
import com.example.entity.WarningRecord;
import com.example.mapper.CourseMapper;
import com.example.mapper.InterventionRecordMapper;
import com.example.mapper.SysUserMapper;
import com.example.mapper.WarningRecordMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class InterventionService {
    @Resource private InterventionRecordMapper interventionRecordMapper;
    @Resource private WarningRecordMapper warningRecordMapper;
    @Resource private CourseMapper courseMapper;
    @Resource private SysUserMapper sysUserMapper;

    public void add(InterventionCreateRequest req, Long teacherId) {
        InterventionRecord r = new InterventionRecord();
        r.setStudentId(req.getStudentId());
        r.setTeacherId(teacherId);
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

    public List<Map<String, Object>> warningOptions(Long teacherId) {
        Course cq = new Course();
        cq.setTeacherId(teacherId);
        Set<Long> courseIds = courseMapper.selectAll(cq).stream().map(Course::getId).collect(Collectors.toSet());
        WarningRecord q = new WarningRecord();
        q.setStatus("OPEN");
        return warningRecordMapper.selectAll(q).stream()
                .filter(w -> w.getCourseId() != null && courseIds.contains(w.getCourseId()))
                .collect(Collectors.toMap(
                        w -> w.getStudentId() + "_" + w.getWarningType() + "_" + w.getCourseId(),
                        w -> w,
                        (a, b) -> b
                ))
                .values().stream()
                .map(w -> {
                    Map<String, Object> m = new HashMap<>();
                    SysUser student = sysUserMapper.selectById(w.getStudentId());
                    m.put("warningId", w.getId());
                    m.put("studentId", w.getStudentId());
                    m.put("studentName", student == null ? ("学生" + w.getStudentId()) : student.getName());
                    m.put("courseId", w.getCourseId());
                    m.put("warningType", w.getWarningType());
                    m.put("warningLevel", w.getWarningLevel());
                    return m;
                }).toList();
    }

    public void undo(Long interventionId, Long teacherId) {
        InterventionRecord record = interventionRecordMapper.selectById(interventionId);
        if (record == null) {
            return;
        }
        interventionRecordMapper.deleteById(interventionId);
    }
}
