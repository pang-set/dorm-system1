package com.dorm.controller;

import com.dorm.common.Result;
import com.dorm.dto.LoginDTO;
import com.dorm.dto.RegisterDTO;
import com.dorm.entity.SysUser;
import com.dorm.service.SysUserService;
import com.dorm.utils.JwtUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Validated @RequestBody LoginDTO loginDTO) {
        SysUser user = sysUserService.getOne(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, loginDTO.getUsername())
                .eq(SysUser::getStatus, 1)
        );

        if (user == null) {
            return Result.error("用户不存在或已禁用");
        }

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            return Result.error("密码错误");
        }

        String token = jwtUtils.generateToken(user.getUsername(), user.getId(), user.getRole());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("realName", user.getRealName());
        data.put("role", user.getRole());
        data.put("buildingId", user.getBuildingId());
        data.put("avatar", "");

        return Result.success("登录成功", data);
    }

    @PostMapping("/register")
    public Result<String> register(@Validated @RequestBody RegisterDTO registerDTO) {
        SysUser existUser = sysUserService.getOne(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, registerDTO.getUsername())
        );

        if (existUser != null) {
            return Result.error("用户名已存在");
        }

        SysUser user = new SysUser();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setRealName(registerDTO.getRealName());
        user.setPhone(registerDTO.getPhone());
        user.setRole(registerDTO.getRole() != null ? registerDTO.getRole() : "STUDENT");
        user.setStatus(1);

        sysUserService.save(user);

        return Result.success("注册成功");
    }

    @GetMapping("/userInfo")
    public Result<SysUser> getUserInfo(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        Long userId = jwtUtils.getUserIdFromToken(token);
        SysUser user = sysUserService.getById(userId);
        user.setPassword(null);
        return Result.success(user);
    }
}
