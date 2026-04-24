package com.example.config;

import cn.hutool.jwt.JWT;
import com.example.common.Result;
import com.example.security.AuthContext;
import com.example.security.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        if (!uri.startsWith("/api") || uri.startsWith("/api/auth/login")) {
            return true;
        }

        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            write401(response, "未登录或 token 缺失");
            return false;
        }
        String token = auth.substring(7);
        JWT jwt = JwtUtils.verify(token);
        if (jwt == null) {
            write401(response, "token 无效或已过期");
            return false;
        }

        Long userId = Long.valueOf(String.valueOf(jwt.getPayload("userId")));
        String role = String.valueOf(jwt.getPayload("role"));
        AuthContext.set(userId, role);

        if (uri.startsWith("/api/student") && !"STUDENT".equals(role)) {
            write401(response, "无学生端权限");
            return false;
        }
        if (uri.startsWith("/api/teacher") && !"TEACHER".equals(role)) {
            write401(response, "无教师端权限");
            return false;
        }
        if (uri.startsWith("/api/admin") && !"ADMIN".equals(role)) {
            write401(response, "无管理员权限");
            return false;
        }
        if ((uri.startsWith("/api/homework/create") || uri.startsWith("/api/exam/create") || uri.startsWith("/api/dashboard")) && !"TEACHER".equals(role)) {
            write401(response, "无教师操作权限");
            return false;
        }
        if (uri.startsWith("/api/intervention")) {
            boolean studentCanReadOwnList = "STUDENT".equals(role) && uri.startsWith("/api/intervention/list");
            boolean teacherAllowed = "TEACHER".equals(role);
            if (!teacherAllowed && !studentCanReadOwnList) {
                write401(response, "无教师操作权限");
                return false;
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthContext.clear();
    }

    private void write401(HttpServletResponse response, String msg) throws Exception {
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(build401(msg)));
    }

    private Result build401(String msg) {
        Result r = Result.error(msg);
        r.setCode("401");
        return r;
    }
}
