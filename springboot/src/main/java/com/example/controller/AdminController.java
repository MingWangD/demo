package com.example.controller;

import com.example.common.Result;
import com.example.entity.SysUser;
import com.example.security.AuthContext;
import com.example.service.AdminService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
public class AdminController {

    @Resource
    private AdminService adminService;

    @GetMapping
    public Result selectPage(@RequestParam String role,
                             @RequestParam(required = false) String name,
                             @RequestParam(required = false) String username,
                             @RequestParam(required = false) String status,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<SysUser> page = adminService.selectPage(role, name, username, status, pageNum, pageSize);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result selectById(@PathVariable Long id, @RequestParam String role) {
        return Result.success(adminService.selectById(id, role));
    }

    @PostMapping
    public Result add(@RequestParam String role, @RequestBody SysUser user) {
        return Result.success(adminService.add(role, user));
    }

    @PutMapping("/{id}")
    public Result update(@PathVariable Long id,
                         @RequestParam String role,
                         @RequestBody SysUser user) {
        return Result.success(adminService.update(id, role, user));
    }

    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable Long id, @RequestParam String role) {
        adminService.deleteById(id, role, AuthContext.userId());
        return Result.success();
    }
}
