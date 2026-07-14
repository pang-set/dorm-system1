package com.dorm.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("building")
public class Building {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String buildingName;
    private String buildingNo;
    private String gender;
    private Integer floorCount;
    private Integer roomCount;
    private Integer bedCount;
    private Integer occupiedCount;
    private Long managerId;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
