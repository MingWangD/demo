package com.example.mapper;

import com.example.entity.SysUser;

import java.util.List;

public interface SysUserMapper {
    int insert(SysUser entity);
    int updateById(SysUser entity);
    int deleteById(Long id);
    SysUser selectById(Long id);
    List<SysUser> selectAll(SysUser entity);
}