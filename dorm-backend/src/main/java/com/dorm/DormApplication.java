package com.dorm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.dorm.mapper")
@EnableScheduling
public class DormApplication {
    public static void main(String[] args) {
        SpringApplication.run(DormApplication.class, args);
        System.out.println("===== 学生宿舍管理系统启动成功 =====");
    }
}
