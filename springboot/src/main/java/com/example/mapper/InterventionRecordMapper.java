package com.example.mapper;

import com.example.entity.InterventionRecord;

import java.util.List;

public interface InterventionRecordMapper {
    int insert(InterventionRecord entity);
    int updateById(InterventionRecord entity);
    int deleteById(Long id);
    InterventionRecord selectById(Long id);
    List<InterventionRecord> selectAll(InterventionRecord entity);
}