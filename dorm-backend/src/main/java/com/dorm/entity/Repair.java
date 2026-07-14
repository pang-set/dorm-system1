package com.dorm.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("repair")
public class Repair {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String repairNo;
    private Long studentId;
    private String studentName;
    private String studentPhone;
    private Long buildingId;
    private Long roomId;
    private String roomNo;
    private String repairType;
    private String title;
    private String description;
    private String images;
    private Integer status;
    private Long repairerId;
    private String repairerName;
    private LocalDateTime repairTime;
    private String repairResult;
    private Integer rating;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
