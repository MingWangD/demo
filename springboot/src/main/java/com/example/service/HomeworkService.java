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
        if (old == null) {
            old = new HomeworkSubmission();
            old.setHomeworkId(req.getHomeworkId());
            old.setStudentId(req.getStudentId());
            old.setCreateTime(now);
        }
        old.setSubmitContent(req.getSubmitContent());
        old.setSubmitTime(now);
        old.setStatus("SUBMITTED");
        old.setUpdateTime(now);
        if (old.getId() == null) {
            homeworkSubmissionMapper.insert(old);
        } else {
            homeworkSubmissionMapper.updateById(old);
        }

        Homework hw = homeworkMapper.selectById(req.getHomeworkId());
        StudentFeature feature = featureExtractor.extractAndSave(req.getStudentId(), hw.getCourseId());
        riskPredictor.predictAndSave(feature);
    }


    private void validateCourseOwner(Long courseId, Long teacherId) {
        Course c = courseMapper.selectById(courseId);
        if (c == null || !teacherId.equals(c.getTeacherId())) {
            throw new CustomException("无权限操作该课程作业");
        }
    }
}
