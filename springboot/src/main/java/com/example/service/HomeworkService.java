package com.example.service;

import com.example.algorithm.FeatureExtractor;
import com.example.algorithm.RiskPredictor;
import com.example.dto.HomeworkSubmitRequest;
import com.example.entity.Homework;
import com.example.entity.HomeworkSubmission;
import com.example.entity.StudentFeature;
import com.example.mapper.HomeworkMapper;
import com.example.mapper.HomeworkSubmissionMapper;
import com.example.mapper.CourseMapper;
import com.example.entity.Course;
import com.example.exception.CustomException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.List;

@Service
public class HomeworkService {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    @Resource private HomeworkMapper homeworkMapper;
    @Resource private HomeworkSubmissionMapper homeworkSubmissionMapper;
    @Resource private FeatureExtractor featureExtractor;
    @Resource private RiskPredictor riskPredictor;
    @Resource private CourseMapper courseMapper;

    public List<Homework> listByCourse(Long courseId) {
        Homework q = new Homework(); q.setCourseId(courseId);
        return homeworkMapper.selectAll(q);
    }

    public void create(Homework homework, Long teacherId) {
        validateCourseOwner(homework.getCourseId(), teacherId);
        homework.setCreateTime(LocalDateTime.now());
        homework.setUpdateTime(LocalDateTime.now());
        homework.setTeacherId(teacherId);
        homeworkMapper.insert(homework);
    }

    public void submit(HomeworkSubmitRequest req) {
        HomeworkSubmission old = homeworkSubmissionMapper.selectByHomeworkAndStudent(req.getHomeworkId(), req.getStudentId());
        LocalDateTime now = LocalDateTime.now();
        if (old != null && "GRADED".equals(old.getStatus())) {
            throw new CustomException("该作业已批改，不能再次修改");
        }
        if (old == null) {
            old = new HomeworkSubmission();
            old.setHomeworkId(req.getHomeworkId());
            old.setStudentId(req.getStudentId());
            old.setCreateTime(now);
        }
        java.util.Map<String, Object> incoming = readJson(req.getSubmitContent());
        java.util.Map<String, Object> existing = readJson(old.getSubmitContent());
        boolean objectiveLocked = Boolean.TRUE.equals(existing.get("objectiveLocked"));
        int objectiveAnswered = num(objectiveLocked ? existing.get("objectiveAnswered") : incoming.get("objectiveAnswered"));
        int objectiveScoreInt = objectiveAnswered * 2;
        String subjectiveAnswer = String.valueOf(incoming.getOrDefault("subjectiveAnswer", existing.getOrDefault("subjectiveAnswer", "")));
        java.util.Map<String, Object> payload = new java.util.LinkedHashMap<>();
        payload.put("objectiveLocked", true);
        payload.put("objectiveAnswered", objectiveAnswered);
        payload.put("objectiveScore", objectiveScoreInt);
        payload.put("objectiveDetail", objectiveLocked ? existing.get("objectiveDetail") : incoming.get("objectiveDetail"));
        payload.put("subjectiveAnswer", subjectiveAnswer);
        old.setSubmitContent(writeJson(payload));
        old.setSubmitTime(now);
        old.setStatus("SUBMITTED");
        Homework hw = homeworkMapper.selectById(req.getHomeworkId());
        int subjectiveCount = count(hw.getContent(), "主观题");
        java.math.BigDecimal autoScore = java.math.BigDecimal.valueOf(Math.min(objectiveScoreInt, 100));
        old.setScore(autoScore);
        if (subjectiveCount > 0) {
            old.setTeacherComment("系统已自动判客观题得分：" + autoScore + "，主观题待教师批改");
        }
        old.setUpdateTime(now);
        if (old.getId() == null) {
            homeworkSubmissionMapper.insert(old);
        } else {
            homeworkSubmissionMapper.updateById(old);
        }

        StudentFeature feature = featureExtractor.extractAndSave(req.getStudentId(), hw.getCourseId());
        riskPredictor.predictAndSave(feature);
    }

    public void undo(Long teacherId, Long homeworkId) {
        Homework hw = homeworkMapper.selectById(homeworkId);
        validateCourseOwner(hw.getCourseId(), teacherId);
        HomeworkSubmission q = new HomeworkSubmission();
        q.setHomeworkId(homeworkId);
        homeworkSubmissionMapper.selectAll(q).stream().map(HomeworkSubmission::getId).filter(Objects::nonNull).forEach(homeworkSubmissionMapper::deleteById);
        homeworkMapper.deleteById(homeworkId);
    }

    private int count(String text, String keyword) {
        if (text == null || keyword == null || keyword.isEmpty()) return 0;
        int c = 0, idx = 0;
        while ((idx = text.indexOf(keyword, idx)) >= 0) { c++; idx += keyword.length(); }
        return c;
    }

    private java.util.Map<String, Object> readJson(String raw) {
        if (raw == null || raw.isEmpty()) return new java.util.HashMap<>();
        try {
            return MAPPER.readValue(raw, new TypeReference<java.util.Map<String, Object>>() {});
        } catch (Exception e) {
            return new java.util.HashMap<>();
        }
    }

    private String writeJson(java.util.Map<String, Object> map) {
        try {
            return MAPPER.writeValueAsString(map);
        } catch (Exception e) {
            return "{}";
        }
    }

    private int num(Object v) {
        if (v == null) return 0;
        if (v instanceof Number n) return n.intValue();
        try { return Integer.parseInt(String.valueOf(v)); } catch (Exception e) { return 0; }
    }


    private void validateCourseOwner(Long courseId, Long teacherId) {
        Course c = courseMapper.selectById(courseId);
        if (c == null || !teacherId.equals(c.getTeacherId())) {
            throw new CustomException("无权限操作该课程作业");
        }
    }
}
