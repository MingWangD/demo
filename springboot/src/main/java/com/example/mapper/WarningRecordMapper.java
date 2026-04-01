package com.example.mapper;

import com.example.entity.WarningRecord;

import java.util.List;

public interface WarningRecordMapper {
    int insert(WarningRecord entity);
    int updateById(WarningRecord entity);
    int deleteById(Long id);
    WarningRecord selectById(Long id);
    List<WarningRecord> selectAll(WarningRecord entity);
}