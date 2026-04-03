package com.example.service;

import com.example.algorithm.FeatureExtractor;
import com.example.algorithm.RiskPredictor;
import com.example.dto.LoginRequest;
import com.example.dto.LoginResponse;
import com.example.entity.StudentAttendance;
import com.example.entity.StudentCourse;
import com.example.entity.StudentFeature;
import com.example.entity.SysUser;
import com.example.exception.CustomException;
import com.example.mapper.StudentAttendanceMapper;
import com.example.mapper.StudentCourseMapper;
import com.example.mapper.SysUserMapper;
import com.example.security.JwtUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuthService {
    @Resource private SysUserMapper sysUserMapper;
    @Resource private StudentCourseMapper studentCourseMapper;
    @Resource private StudentAttendanceMapper studentAttendanceMapper;
    @Resource private FeatureExtractor featureExtractor;
    @Resource private RiskPredictor riskPredictor;

    public LoginResponse login(LoginRequest request) {
        SysUser user = sysUserMapper.selectByUsername(request.getUsername());
        if (user == null || !user.getPassword().equals(request.getPassword())) {
            throw new CustomException("账号或密码错误");
        }
        if (request.getRole() != null && !request.getRole().isBlank() && !request.getRole().equals(user.getRole())) {
            throw new CustomException("登录身份与账号角色不匹配，请重新选择");
        }

        if ("STUDENT".equals(user.getRole())) {
            StudentCourse query = new StudentCourse();
            query.setStudentId(user.getId());
            List<StudentCourse> courses = studentCourseMapper.selectAll(query);
            for (StudentCourse c : courses) {
                StudentAttendance attendanceQuery = new StudentAttendance();
                attendanceQuery.setStudentId(user.getId());
                attendanceQuery.setCourseId(c.getCourseId());
                boolean alreadyLoggedToday = studentAttendanceMapper.selectAll(attendanceQuery).stream()
                        .anyMatch(a -> "LOGIN".equals(a.getAttendanceType())
                                && a.getAttendanceTime() != null
                                && LocalDate.now().equals(a.getAttendanceTime().toLocalDate()));
                if (!alreadyLoggedToday) {
                    StudentAttendance attendance = new StudentAttendance();
                    attendance.setStudentId(user.getId());
                    attendance.setCourseId(c.getCourseId());
                    attendance.setAttendanceType("LOGIN");
                    attendance.setAttendanceTime(LocalDateTime.now());
                    attendance.setRemark("登录自动记录");
                    studentAttendanceMapper.insert(attendance);
                }

                StudentFeature feature = featureExtractor.extractAndSave(user.getId(), c.getCourseId());
                riskPredictor.predictAndSave(feature);
            }
        }

        LoginResponse response = new LoginResponse();
        response.setToken(JwtUtils.createToken(user.getId(), user.getRole()));
        response.setUserId(user.getId());
        response.setName(user.getName());
        response.setRole(user.getRole());
        response.setAvatar(user.getAvatar());
        return response;
    }
}
