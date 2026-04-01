package com.example.mapper;

import com.example.entity.HomeworkSubmission;

import java.util.List;

public interface HomeworkSubmissionMapper {
    int insert(HomeworkSubmission entity);
    int updateById(HomeworkSubmission entity);
    int deleteById(Long id);
    HomeworkSubmission selectById(Long id);
    List<HomeworkSubmission> selectAll(HomeworkSubmission entity);
}