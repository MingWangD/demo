package com.example.mapper;

import com.example.entity.StudentAcademic;

import java.util.List;

public interface StudentAcademicMapper {
    int insert(StudentAcademic entity);
    int updateById(StudentAcademic entity);
    int deleteById(Long id);
    StudentAcademic selectById(Long id);
    List<StudentAcademic> selectAll(StudentAcademic entity);
}