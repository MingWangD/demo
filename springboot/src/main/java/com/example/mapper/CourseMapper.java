package com.example.mapper;

import com.example.entity.Course;

import java.util.List;

public interface CourseMapper {
    int insert(Course entity);
    int updateById(Course entity);
    int deleteById(Long id);
    Course selectById(Long id);
    List<Course> selectAll(Course entity);
}