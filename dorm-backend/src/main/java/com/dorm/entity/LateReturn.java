package com.dorm.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("late_return")
public class LateReturn {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentNo;
    private Long buildingId;
    private Long roomId;
    private LocalDateTime returnTime;
    private String reason;
    private Long registrarId;
    private String registrarName;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
