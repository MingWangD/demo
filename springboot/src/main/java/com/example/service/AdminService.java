package com.example.service;

import com.example.entity.Account;
import com.example.entity.SysUser;
import com.example.exception.CustomException;
import com.example.mapper.SysUserMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminService {

    private static final String ROLE_STUDENT = "STUDENT";
    private static final String ROLE_TEACHER = "TEACHER";
    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String DEFAULT_PASSWORD = "123456";

    @Resource
    private SysUserMapper sysUserMapper;

    public PageInfo<SysUser> selectPage(String role, String name, String username, String status, Integer pageNum, Integer pageSize) {
        SysUser query = new SysUser();
        query.setRole(validateManagedRole(role));
        query.setName(emptyToNull(name));
        query.setUsername(emptyToNull(username));
        query.setStatus(emptyToNull(status));
        PageHelper.startPage(pageNum, pageSize);
        List<SysUser> list = sysUserMapper.selectAll(query);
        list.forEach(this::sanitizeForResponse);
        return PageInfo.of(list);
    }

    public SysUser selectById(Long id, String role) {
        String targetRole = validateManagedRole(role);
        SysUser user = requireUser(id);
        if (!targetRole.equals(user.getRole())) {
            throw new CustomException("用户角色不匹配");
        }
        return sanitizeForResponse(user);
    }

    public SysUser add(String role, SysUser user) {
        String targetRole = validateManagedRole(role);
        if (user == null) {
            throw new CustomException("请求参数不能为空");
        }
        user.setRole(targetRole);
        normalize(user);
        validateForCreate(user);
        LocalDateTime now = LocalDateTime.now();
        user.setCreateTime(now);
        user.setUpdateTime(now);
        sysUserMapper.insert(user);
        return sanitizeForResponse(user);
    }

    public SysUser update(Long id, String role, SysUser form) {
        String targetRole = validateManagedRole(role);
        SysUser existing = requireUser(id);
        if (!targetRole.equals(existing.getRole())) {
            throw new CustomException("用户角色不匹配");
        }
        if (form == null) {
            throw new CustomException("请求参数不能为空");
        }

        existing.setUsername(form.getUsername());
        if (form.getPassword() != null && !form.getPassword().isBlank()) {
            existing.setPassword(form.getPassword().trim());
        }
        existing.setName(form.getName());
        existing.setCollege(form.getCollege());
        existing.setMajor(form.getMajor());
        existing.setClassName(form.getClassName());
        existing.setStatus(form.getStatus());
        existing.setAvatar(form.getAvatar());
        existing.setStudentNo(form.getStudentNo());
        existing.setTeacherNo(form.getTeacherNo());
        existing.setRole(targetRole);

        normalize(existing);
        validateForUpdate(existing);
        existing.setUpdateTime(LocalDateTime.now());
        sysUserMapper.updateById(existing);
        return sanitizeForResponse(existing);
    }

    public void deleteById(Long id, String role, Long currentUserId) {
        String targetRole = validateManagedRole(role);
        SysUser user = requireUser(id);
        if (!targetRole.equals(user.getRole())) {
            throw new CustomException("用户角色不匹配");
        }
        if (currentUserId != null && currentUserId.equals(id)) {
            throw new CustomException("不能删除当前登录账号");
        }
        sysUserMapper.deleteById(id);
    }

    public Account login(Account account) {
        SysUser user = sysUserMapper.selectByUsername(account.getUsername());
        if (user == null || !"ADMIN".equals(user.getRole())) {
            throw new CustomException("管理员账号不存在");
        }
        if (!user.getPassword().equals(account.getPassword())) {
            throw new CustomException("账号或密码错误");
        }
        Account result = new Account();
        result.setId(user.getId().intValue());
        result.setUsername(user.getUsername());
        result.setName(user.getName());
        result.setRole(user.getRole());
        result.setAvatar(user.getAvatar());
        return result;
    }

    public void updatePassword(Account account) {
        SysUser user = sysUserMapper.selectByUsername(account.getUsername());
        if (user == null || !"ADMIN".equals(user.getRole())) {
            throw new CustomException("管理员账号不存在");
        }
        if (!user.getPassword().equals(account.getPassword())) {
            throw new CustomException("原密码错误");
        }
        SysUser update = new SysUser();
        update.setId(user.getId());
        update.setPassword(account.getNewPassword());
        update.setUpdateTime(LocalDateTime.now());
        sysUserMapper.updateById(update);
    }

    private void validateForCreate(SysUser user) {
        if (isBlank(user.getUsername())) {
            throw new CustomException("用户名不能为空");
        }
        if (isBlank(user.getName())) {
            throw new CustomException("姓名不能为空");
        }
        if (isBlank(user.getPassword())) {
            user.setPassword(DEFAULT_PASSWORD);
        }
        if (isBlank(user.getStatus())) {
            user.setStatus(STATUS_ACTIVE);
        }
        validateUniqueFields(user);
    }

    private void validateForUpdate(SysUser user) {
        if (isBlank(user.getUsername())) {
            throw new CustomException("用户名不能为空");
        }
        if (isBlank(user.getName())) {
            throw new CustomException("姓名不能为空");
        }
        if (isBlank(user.getStatus())) {
            user.setStatus(STATUS_ACTIVE);
        }
        validateUniqueFields(user);
    }

    private void validateUniqueFields(SysUser user) {
        SysUser sameUsername = sysUserMapper.selectByUsername(user.getUsername());
        if (sameUsername != null && !sameUsername.getId().equals(user.getId())) {
            throw new CustomException("用户名已存在");
        }

        if (ROLE_STUDENT.equals(user.getRole())) {
            if (isBlank(user.getStudentNo())) {
                throw new CustomException("学号不能为空");
            }
            SysUser sameStudentNo = sysUserMapper.selectByStudentNo(user.getStudentNo());
            if (sameStudentNo != null && !sameStudentNo.getId().equals(user.getId())) {
                throw new CustomException("学号已存在");
            }
        }

        if (ROLE_TEACHER.equals(user.getRole())) {
            if (isBlank(user.getTeacherNo())) {
                throw new CustomException("工号不能为空");
            }
            SysUser sameTeacherNo = sysUserMapper.selectByTeacherNo(user.getTeacherNo());
            if (sameTeacherNo != null && !sameTeacherNo.getId().equals(user.getId())) {
                throw new CustomException("工号已存在");
            }
        }
    }

    private SysUser requireUser(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new CustomException("用户不存在");
        }
        return user;
    }

    private String validateManagedRole(String role) {
        if (ROLE_STUDENT.equals(role) || ROLE_TEACHER.equals(role)) {
            return role;
        }
        throw new CustomException("仅支持管理学生和教师账号");
    }

    private void normalize(SysUser user) {
        user.setUsername(trimToNull(user.getUsername()));
        user.setPassword(trimToNull(user.getPassword()));
        user.setName(trimToNull(user.getName()));
        user.setCollege(trimToNull(user.getCollege()));
        user.setMajor(trimToNull(user.getMajor()));
        user.setClassName(trimToNull(user.getClassName()));
        user.setStatus(trimToNull(user.getStatus()));
        user.setAvatar(trimToNull(user.getAvatar()));
        user.setStudentNo(trimToNull(user.getStudentNo()));
        user.setTeacherNo(trimToNull(user.getTeacherNo()));

        if (ROLE_STUDENT.equals(user.getRole())) {
            user.setTeacherNo(null);
        } else if (ROLE_TEACHER.equals(user.getRole())) {
            user.setStudentNo(null);
            user.setMajor(null);
            user.setClassName(null);
        }
    }

    private SysUser sanitizeForResponse(SysUser user) {
        user.setPassword(null);
        return user;
    }

    private String emptyToNull(String value) {
        return trimToNull(value);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
