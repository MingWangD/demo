package com.example.mapper;

import com.example.entity.StudentCourse;

import java.util.List;

public interface StudentCourseMapper {
    int insert(StudentCourse entity);
    int updateById(StudentCourse entity);
    int deleteById(Long id);
    StudentCourse selectById(Long id);
    List<StudentCourse> selectAll(StudentCourse entity);
}