package com.dorm.config;

import com.dorm.entity.SysUser;
import com.dorm.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initAdminUser();
    }

    private void initAdminUser() {
        SysUser admin = sysUserService.getOne(
            new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, "admin")
        );
        if (admin == null) {
            admin = new SysUser();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setRealName("管理员");
            admin.setPhone("13800138000");
            admin.setRole("ADMIN");
            admin.setStatus(1);
            sysUserService.save(admin);
            System.out.println("===== 管理员账号初始化成功：admin / 123456 =====");
        }

        SysUser housemaster = sysUserService.getOne(
            new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, "housemaster")
        );
        if (housemaster == null) {
            housemaster = new SysUser();
            housemaster.setUsername("housemaster");
            housemaster.setPassword(passwordEncoder.encode("123456"));
            housemaster.setRealName("王宿管");
            housemaster.setPhone("13800138001");
            housemaster.setRole("HOUSEMASTER");
            housemaster.setBuildingId(1L);
            housemaster.setStatus(1);
            sysUserService.save(housemaster);
            System.out.println("===== 宿管账号初始化成功：housemaster / 123456 =====");
        }

        SysUser student = sysUserService.getOne(
            new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, "student")
        );
        if (student == null) {
            student = new SysUser();
            student.setUsername("student");
            student.setPassword(passwordEncoder.encode("123456"));
            student.setRealName("张三");
            student.setPhone("13800138002");
            student.setRole("STUDENT");
            student.setStatus(1);
            sysUserService.save(student);
            System.out.println("===== 学生账号初始化成功：student / 123456 =====");
        }
    }
}
