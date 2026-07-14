package com.dorm.controller;

import com.dorm.common.PageResult;
import com.dorm.common.Result;
import com.dorm.entity.SysUser;
import com.dorm.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/page")
    public Result<PageResult<SysUser>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Integer status) {

        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();

        if (username != null && !username.isEmpty()) {
            wrapper.like(SysUser::getUsername, username);
        }
        if (realName != null && !realName.isEmpty()) {
            wrapper.like(SysUser::getRealName, realName);
        }
        if (role != null && !role.isEmpty()) {
            wrapper.eq(SysUser::getRole, role);
        }
        if (status != null) {
            wrapper.eq(SysUser::getStatus, status);
        }
        wrapper.orderByDesc(SysUser::getCreateTime);

        sysUserService.page(page, wrapper);

        page.getRecords().forEach(u -> u.setPassword(null));

        PageResult<SysUser> result = new PageResult<>(page.getTotal(), page.getRecords());
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<SysUser> getById(@PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        user.setPassword(null);
        return Result.success(user);
    }

    @PostMapping
    public Result<String> add(@RequestBody SysUser sysUser) {
        SysUser exist = sysUserService.getOne(
            new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, sysUser.getUsername())
        );
        if (exist != null) {
            return Result.error("用户名已存在");
        }

        if (sysUser.getPassword() == null || sysUser.getPassword().isEmpty()) {
            sysUser.setPassword("123456");
        }
        sysUser.setPassword(passwordEncoder.encode(sysUser.getPassword()));
        if (sysUser.getStatus() == null) {
            sysUser.setStatus(1);
        }

        sysUserService.save(sysUser);
        return Result.success("新增成功");
    }

    @PutMapping
    public Result<String> update(@RequestBody SysUser sysUser) {
        SysUser oldUser = sysUserService.getById(sysUser.getId());
        if (oldUser == null) {
            return Result.error("用户不存在");
        }

        if (sysUser.getPassword() != null && !sysUser.getPassword().isEmpty()) {
            sysUser.setPassword(passwordEncoder.encode(sysUser.getPassword()));
        } else {
            sysUser.setPassword(null);
        }

        sysUserService.updateById(sysUser);
        return Result.success("修改成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        sysUserService.removeById(id);
        return Result.success("删除成功");
    }

    @PostMapping("/resetPassword/{id}")
    public Result<String> resetPassword(@PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(passwordEncoder.encode("123456"));
        sysUserService.updateById(user);
        return Result.success("密码已重置为123456");
    }
}
