package com.dorm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.PageResult;
import com.dorm.common.Result;
import com.dorm.entity.ChangeRoomRequest;
import com.dorm.entity.DormRoom;
import com.dorm.entity.Student;
import com.dorm.service.BedService;
import com.dorm.service.ChangeRoomRequestService;
import com.dorm.service.DormRoomService;
import com.dorm.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/changeRoom")
public class ChangeRoomRequestController {

    @Autowired
    private ChangeRoomRequestService changeRoomRequestService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private DormRoomService dormRoomService;

    @Autowired
    private BedService bedService;

    @GetMapping("/page")
    public Result<PageResult<ChangeRoomRequest>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Integer status) {

        LambdaQueryWrapper<ChangeRoomRequest> wrapper = new LambdaQueryWrapper<>();
        if (studentId != null) {
            wrapper.eq(ChangeRoomRequest::getStudentId, studentId);
        }
        if (status != null) {
            wrapper.eq(ChangeRoomRequest::getStatus, status);
        }
        wrapper.orderByDesc(ChangeRoomRequest::getCreateTime);

        Page<ChangeRoomRequest> page = changeRoomRequestService.page(new Page<>(pageNum, pageSize), wrapper);
        PageResult<ChangeRoomRequest> pageResult = new PageResult<>(page.getTotal(), page.getRecords());

        // 回填冗余名称字段（学生、原宿舍、新宿舍）
        for (ChangeRoomRequest req : pageResult.getRecords()) {
            if (req.getStudentId() != null) {
                Student student = studentService.getById(req.getStudentId());
                if (student != null) {
                    if (req.getStudentName() == null) req.setStudentName(student.getName());
                    if (req.getStudentNo() == null) req.setStudentNo(student.getStudentNo());
                    if (req.getOldBuildingId() == null) req.setOldBuildingId(student.getBuildingId());
                    if (req.getOldRoomId() == null) req.setOldRoomId(student.getRoomId());
                }
            }
            if (req.getOldRoomId() != null && req.getOldRoomNo() == null) {
                DormRoom oldRoom = dormRoomService.getById(req.getOldRoomId());
                if (oldRoom != null) {
                    req.setOldRoomNo(oldRoom.getRoomNo());
                    if (req.getOldBuildingId() == null) req.setOldBuildingId(oldRoom.getBuildingId());
                }
            }
            if (req.getNewRoomId() != null && req.getNewRoomNo() == null) {
                DormRoom newRoom = dormRoomService.getById(req.getNewRoomId());
                if (newRoom != null) {
                    req.setNewRoomNo(newRoom.getRoomNo());
                    if (req.getNewBuildingId() == null) req.setNewBuildingId(newRoom.getBuildingId());
                }
            }
        }

        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    public Result<ChangeRoomRequest> detail(@PathVariable Long id) {
        ChangeRoomRequest changeRoomRequest = changeRoomRequestService.getById(id);
        if (changeRoomRequest == null) {
            return Result.error("记录不存在");
        }
        // 补齐冗余字段
        if (changeRoomRequest.getStudentId() != null) {
            Student student = studentService.getById(changeRoomRequest.getStudentId());
            if (student != null) {
                if (changeRoomRequest.getStudentName() == null) changeRoomRequest.setStudentName(student.getName());
                if (changeRoomRequest.getStudentNo() == null) changeRoomRequest.setStudentNo(student.getStudentNo());
                if (changeRoomRequest.getOldBuildingId() == null) changeRoomRequest.setOldBuildingId(student.getBuildingId());
                if (changeRoomRequest.getOldRoomId() == null) changeRoomRequest.setOldRoomId(student.getRoomId());
            }
        }
        if (changeRoomRequest.getOldRoomId() != null && changeRoomRequest.getOldRoomNo() == null) {
            DormRoom oldRoom = dormRoomService.getById(changeRoomRequest.getOldRoomId());
            if (oldRoom != null) {
                changeRoomRequest.setOldRoomNo(oldRoom.getRoomNo());
                if (changeRoomRequest.getOldBuildingId() == null) changeRoomRequest.setOldBuildingId(oldRoom.getBuildingId());
            }
        }
        if (changeRoomRequest.getNewRoomId() != null && changeRoomRequest.getNewRoomNo() == null) {
            DormRoom newRoom = dormRoomService.getById(changeRoomRequest.getNewRoomId());
            if (newRoom != null) {
                changeRoomRequest.setNewRoomNo(newRoom.getRoomNo());
                if (changeRoomRequest.getNewBuildingId() == null) changeRoomRequest.setNewBuildingId(newRoom.getBuildingId());
            }
        }
        return Result.success(changeRoomRequest);
    }

    @PostMapping
    public Result<String> add(@RequestBody ChangeRoomRequest changeRoomRequest) {
        // 自动填充冗余字段
        if (changeRoomRequest.getStudentId() != null) {
            Student student = studentService.getById(changeRoomRequest.getStudentId());
            if (student != null) {
                changeRoomRequest.setStudentName(student.getName());
                changeRoomRequest.setStudentNo(student.getStudentNo());
                changeRoomRequest.setOldBuildingId(student.getBuildingId());
                changeRoomRequest.setOldRoomId(student.getRoomId());
                if (student.getRoomId() != null) {
                    DormRoom oldRoom = dormRoomService.getById(student.getRoomId());
                    if (oldRoom != null) {
                        changeRoomRequest.setOldRoomNo(oldRoom.getRoomNo());
                    }
                }
            }
        }
        if (changeRoomRequest.getNewRoomId() != null) {
            DormRoom newRoom = dormRoomService.getById(changeRoomRequest.getNewRoomId());
            if (newRoom != null) {
                changeRoomRequest.setNewRoomNo(newRoom.getRoomNo());
                changeRoomRequest.setNewBuildingId(newRoom.getBuildingId());
            }
        }
        changeRoomRequestService.save(changeRoomRequest);
        return Result.success("新增成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        changeRoomRequestService.removeById(id);
        return Result.success("删除成功");
    }

    @PutMapping("/approve/{id}")
    @Transactional
    public Result<String> approve(@PathVariable Long id, @RequestBody ChangeRoomRequest changeRoomRequest) {
        changeRoomRequest.setId(id);
        changeRoomRequest.setApproveTime(LocalDateTime.now());
        changeRoomRequestService.updateById(changeRoomRequest);

        if (changeRoomRequest.getStatus() != null && changeRoomRequest.getStatus() == 1) {
            ChangeRoomRequest request = changeRoomRequestService.getById(id);
            Student student = studentService.getById(request.getStudentId());
            if (student != null && request.getNewRoomId() != null) {
                // 释放原床位
                if (student.getBedId() != null) {
                    com.dorm.entity.Bed oldBed = bedService.getById(student.getBedId());
                    if (oldBed != null) {
                        oldBed.setStatus(0);
                        bedService.updateById(oldBed);
                    }
                }
                // 占用新床位
                if (request.getNewBedId() != null) {
                    com.dorm.entity.Bed newBed = bedService.getById(request.getNewBedId());
                    if (newBed != null) {
                        newBed.setStatus(1);
                        bedService.updateById(newBed);
                    }
                }
                student.setBuildingId(request.getNewBuildingId());
                student.setRoomId(request.getNewRoomId());
                student.setBedId(request.getNewBedId());
                studentService.updateById(student);
            }
        }

        return Result.success("审批成功");
    }
}
