package com.example.mapper;

import com.example.entity.Homework;

import java.util.List;

public interface HomeworkMapper {
    int insert(Homework entity);
    int updateById(Homework entity);
    int deleteById(Long id);
    Homework selectById(Long id);
    List<Homework> selectAll(Homework entity);
}