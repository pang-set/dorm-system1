package com.dorm.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("check_record")
public class CheckRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long buildingId;
    private Long roomId;
    private String roomNo;
    private Long inspectorId;
    private String inspectorName;
    private LocalDateTime checkTime;
    private Integer hygieneScore;
    private String hygieneLevel;
    private String remark;
    private String images;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
