package com.example.mapper;

import com.example.entity.ExamRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExamRecordMapper {
    int insert(ExamRecord entity);
    int updateById(ExamRecord entity);
    int deleteById(Long id);
    ExamRecord selectById(Long id);
    List<ExamRecord> selectAll(ExamRecord entity);

    ExamRecord selectByExamAndStudent(@Param("examId") Long examId, @Param("studentId") Long studentId);
}
