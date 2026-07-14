package com.dorm.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("change_room_request")
public class ChangeRoomRequest {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentNo;
    private Long oldBuildingId;
    private Long oldRoomId;
    private String oldRoomNo;
    private Long newBuildingId;
    private Long newRoomId;
    private String newRoomNo;
    private Long newBedId;
    private String reason;
    private Integer status;
    private Long approverId;
    private String approverName;
    private String approveRemark;
    private LocalDateTime approveTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
