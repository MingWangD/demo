package com.example.service;

import com.example.entity.Course;
import com.example.mapper.CourseMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherService {
    @Resource private CourseMapper courseMapper;

    public List<Course> courseList(Long teacherId) {
        Course q = new Course();
        q.setTeacherId(teacherId);
        return courseMapper.selectAll(q);
    }
}
