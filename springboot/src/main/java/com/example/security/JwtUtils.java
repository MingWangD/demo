package com.example.security;

import cn.hutool.core.date.DateUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {
    private static final byte[] KEY = "academic-warning-secret-2026".getBytes(StandardCharsets.UTF_8);

    private JwtUtils() {}

    public static String createToken(Long userId, String role) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        payload.put("role", role);
        payload.put("exp", DateUtil.offsetHour(new Date(), 12).getTime() / 1000);
        return JWTUtil.createToken(payload, KEY);
    }

    public static JWT verify(String token) {
        if (!JWTUtil.verify(token, KEY)) return null;
        JWT jwt = JWT.of(token);
        if (!jwt.setKey(KEY).verify()) return null;
        if (!jwt.validate(0)) return null;
        return jwt;
    }
}
