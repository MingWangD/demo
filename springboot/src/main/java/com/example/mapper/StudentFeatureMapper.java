package com.example.mapper;

import com.example.entity.StudentFeature;

import java.util.List;

public interface StudentFeatureMapper {
    int insert(StudentFeature entity);
    int updateById(StudentFeature entity);
    int deleteById(Long id);
    StudentFeature selectById(Long id);
    List<StudentFeature> selectAll(StudentFeature entity);
}