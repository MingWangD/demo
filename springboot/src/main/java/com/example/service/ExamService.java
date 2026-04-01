package com.example.service;

import com.example.algorithm.FeatureExtractor;
import com.example.algorithm.RiskPredictor;
import com.example.dto.ExamSubmitRequest;
import com.example.entity.*;
import com.example.exception.CustomException;
import com.example.mapper.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExamService {
    @Resource private ExamMapper examMapper;
    @Resource private ExamRecordMapper examRecordMapper;
    @Resource private ExamQualificationMapper examQualificationMapper;
    @Resource private FeatureExtractor featureExtractor;
    @Resource private RiskPredictor riskPredictor;

    public List<Exam> listByCourse(Long courseId) {
        Exam q = new Exam(); q.setCourseId(courseId);
        return examMapper.selectAll(q);
    }

    public void create(Exam exam) {
        exam.setCreateTime(LocalDateTime.now());
        exam.setUpdateTime(LocalDateTime.now());
        examMapper.insert(exam);
    }

    public void submit(ExamSubmitRequest req) {
        ExamQualification qualification = examQualificationMapper.selectByExamAndStudent(req.getExamId(), req.getStudentId());
        if (qualification == null || !Boolean.TRUE.equals(qualification.getIsQualified())) {
            throw new CustomException("无考试资格，后端拒绝提交");
        }
        ExamRecord record = examRecordMapper.selectByExamAndStudent(req.getExamId(), req.getStudentId());
        if (record == null) {
            record = new ExamRecord();
            record.setExamId(req.getExamId());
            record.setStudentId(req.getStudentId());
            record.setCreateTime(LocalDateTime.now());
            examRecordMapper.insert(record);
        }
        record.setScore(req.getScore());
        record.setStatus("FINISHED");
        record.setSubmitTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        examRecordMapper.updateById(record);

        Exam exam = examMapper.selectById(req.getExamId());
        StudentFeature feature = featureExtractor.extractAndSave(req.getStudentId(), exam.getCourseId());
        riskPredictor.predictAndSave(feature);
    }
}
