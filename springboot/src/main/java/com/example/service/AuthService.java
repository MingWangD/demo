package com.example.service;

import cn.hutool.core.util.IdUtil;
import com.example.dto.LoginRequest;
import com.example.dto.LoginResponse;
import com.example.entity.StudentAttendance;
import com.example.entity.StudentCourse;
import com.example.entity.SysUser;
import com.example.exception.CustomException;
import com.example.mapper.StudentAttendanceMapper;
import com.example.mapper.StudentCourseMapper;
import com.example.mapper.SysUserMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuthService {
    @Resource private SysUserMapper sysUserMapper;
    @Resource private StudentCourseMapper studentCourseMapper;
    @Resource private StudentAttendanceMapper studentAttendanceMapper;

    public LoginResponse login(LoginRequest request) {
        SysUser user = sysUserMapper.selectByUsername(request.getUsername());
        if (user == null || !user.getPassword().equals(request.getPassword())) {
            throw new CustomException("账号或密码错误");
        }

        if ("STUDENT".equals(user.getRole())) {
            StudentCourse query = new StudentCourse();
            query.setStudentId(user.getId());
            List<StudentCourse> courses = studentCourseMapper.selectAll(query);
            for (StudentCourse c : courses) {
                StudentAttendance attendance = new StudentAttendance();
                attendance.setStudentId(user.getId());
                attendance.setCourseId(c.getCourseId());
                attendance.setAttendanceType("LOGIN");
                attendance.setAttendanceTime(LocalDateTime.now());
                attendance.setRemark("登录自动记录");
                studentAttendanceMapper.insert(attendance);
            }
        }

        LoginResponse response = new LoginResponse();
        response.setToken(IdUtil.fastSimpleUUID());
        response.setUserId(user.getId());
        response.setName(user.getName());
        response.setRole(user.getRole());
        response.setAvatar(user.getAvatar());
        return response;
    }
}
