package com.example.mapper;

import com.example.entity.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserMapper {
    int insert(SysUser entity);
    int updateById(SysUser entity);
    int deleteById(Long id);
    SysUser selectById(Long id);
    List<SysUser> selectAll(SysUser entity);

    SysUser selectByUsername(@Param("username") String username);
    List<SysUser> selectByRole(@Param("role") String role);
}
