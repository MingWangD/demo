package com.example.mapper;

import com.example.entity.RiskPrediction;

import java.util.List;

public interface RiskPredictionMapper {
    int insert(RiskPrediction entity);
    int updateById(RiskPrediction entity);
    int deleteById(Long id);
    RiskPrediction selectById(Long id);
    List<RiskPrediction> selectAll(RiskPrediction entity);
}