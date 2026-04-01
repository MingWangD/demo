package com.example.mapper;

import com.example.entity.Exam;

import java.util.List;

public interface ExamMapper {
    int insert(Exam entity);
    int updateById(Exam entity);
    int deleteById(Long id);
    Exam selectById(Long id);
    List<Exam> selectAll(Exam entity);
}