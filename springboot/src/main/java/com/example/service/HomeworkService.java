package com.example.service;

import com.example.algorithm.FeatureExtractor;
import com.example.algorithm.RiskPredictor;
import com.example.dto.HomeworkSubmitRequest;
import com.example.entity.Homework;
import com.example.entity.HomeworkSubmission;
import com.example.entity.StudentFeature;
import com.example.mapper.HomeworkMapper;
import com.example.mapper.HomeworkSubmissionMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HomeworkService {
    @Resource private HomeworkMapper homeworkMapper;
    @Resource private HomeworkSubmissionMapper homeworkSubmissionMapper;
    @Resource private FeatureExtractor featureExtractor;
    @Resource private RiskPredictor riskPredictor;

    public List<Homework> listByCourse(Long courseId) {
        Homework q = new Homework(); q.setCourseId(courseId);
        return homeworkMapper.selectAll(q);
    }

    public void create(Homework homework) {
        homework.setCreateTime(LocalDateTime.now());
        homework.setUpdateTime(LocalDateTime.now());
        homeworkMapper.insert(homework);
    }

    public void submit(HomeworkSubmitRequest req) {
        HomeworkSubmission old = homeworkSubmissionMapper.selectByHomeworkAndStudent(req.getHomeworkId(), req.getStudentId());
        if (old == null) {
            old = new HomeworkSubmission();
            old.setHomeworkId(req.getHomeworkId());
            old.setStudentId(req.getStudentId());
            old.setCreateTime(LocalDateTime.now());
            homeworkSubmissionMapper.insert(old);
        }
        old.setSubmitContent(req.getSubmitContent());
        old.setSubmitTime(LocalDateTime.now());
        old.setStatus("SUBMITTED");
        old.setUpdateTime(LocalDateTime.now());
        homeworkSubmissionMapper.updateById(old);

        Homework hw = homeworkMapper.selectById(req.getHomeworkId());
        StudentFeature feature = featureExtractor.extractAndSave(req.getStudentId(), hw.getCourseId());
        riskPredictor.predictAndSave(feature);
    }
}
