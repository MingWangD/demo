package com.example.mapper;

import com.example.entity.StudentAttendance;

import java.util.List;

public interface StudentAttendanceMapper {
    int insert(StudentAttendance entity);
    int updateById(StudentAttendance entity);
    int deleteById(Long id);
    StudentAttendance selectById(Long id);
    List<StudentAttendance> selectAll(StudentAttendance entity);
}