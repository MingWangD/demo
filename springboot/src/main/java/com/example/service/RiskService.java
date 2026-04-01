package com.example.service;

import com.example.algorithm.FeatureExtractor;
import com.example.algorithm.RiskPredictor;
import com.example.entity.Course;
import com.example.entity.RiskPrediction;
import com.example.entity.StudentFeature;
import com.example.entity.SysUser;
import com.example.mapper.CourseMapper;
import com.example.mapper.StudentCourseMapper;
import com.example.mapper.SysUserMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RiskService {
    @Resource private FeatureExtractor featureExtractor;
    @Resource private RiskPredictor riskPredictor;
    @Resource private SysUserMapper sysUserMapper;
    @Resource private StudentService studentService;

    public RiskPrediction predictSingle(Long studentId, Long courseId) {
        StudentFeature feature = featureExtractor.extractAndSave(studentId, courseId);
        return riskPredictor.predictAndSave(feature);
    }

    public List<RiskPrediction> predictAllStudents() {
        List<RiskPrediction> list = new ArrayList<>();
        for (SysUser user : sysUserMapper.selectByRole("STUDENT")) {
            List<Course> courses = studentService.courses(user.getId());
            if (courses.isEmpty()) {
                list.add(predictSingle(user.getId(), null));
            } else {
                for (Course c : courses) {
                    list.add(predictSingle(user.getId(), c.getId()));
                }
            }
        }
        return list;
    }
}
