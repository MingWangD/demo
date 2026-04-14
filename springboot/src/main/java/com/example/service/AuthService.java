package com.example.service;

import com.example.dto.LoginRequest;
import com.example.dto.LoginResponse;
import com.example.entity.SysUser;
import com.example.exception.CustomException;
import com.example.mapper.SysUserMapper;
import com.example.security.JwtUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Resource
    private SysUserMapper sysUserMapper;

    public LoginResponse login(LoginRequest request) {
        SysUser user = sysUserMapper.selectByUsername(request.getUsername());
        if (user == null || !user.getPassword().equals(request.getPassword())) {
            throw new CustomException("账号或密码错误");
        }
        if (request.getRole() != null && !request.getRole().isBlank() && !request.getRole().equals(user.getRole())) {
            throw new CustomException("登录身份与账号角色不匹配，请重新选择");
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
