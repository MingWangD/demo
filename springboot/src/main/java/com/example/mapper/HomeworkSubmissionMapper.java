package com.example.mapper;

import com.example.entity.HomeworkSubmission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HomeworkSubmissionMapper {
    int insert(HomeworkSubmission entity);
    int updateById(HomeworkSubmission entity);
    int deleteById(Long id);
    HomeworkSubmission selectById(Long id);
    List<HomeworkSubmission> selectAll(HomeworkSubmission entity);

    HomeworkSubmission selectByHomeworkAndStudent(@Param("homeworkId") Long homeworkId, @Param("studentId") Long studentId);
}
