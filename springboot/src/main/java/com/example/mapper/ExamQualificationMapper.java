package com.example.mapper;

import com.example.entity.ExamQualification;

import java.util.List;

public interface ExamQualificationMapper {
    int insert(ExamQualification entity);
    int updateById(ExamQualification entity);
    int deleteById(Long id);
    ExamQualification selectById(Long id);
    List<ExamQualification> selectAll(ExamQualification entity);
}