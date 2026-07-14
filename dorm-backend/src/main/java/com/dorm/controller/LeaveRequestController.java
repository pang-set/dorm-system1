package com.dorm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.PageResult;
import com.dorm.common.Result;
import com.dorm.entity.LeaveRequest;
import com.dorm.service.LeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/leave")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @GetMapping("/page")
    public Result<PageResult<LeaveRequest>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) Integer status) {

        LambdaQueryWrapper<LeaveRequest> wrapper = new LambdaQueryWrapper<>();
        if (studentId != null) {
            wrapper.eq(LeaveRequest::getStudentId, studentId);
        }
        if (buildingId != null) {
            wrapper.eq(LeaveRequest::getBuildingId, buildingId);
        }
        if (status != null) {
            wrapper.eq(LeaveRequest::getStatus, status);
        }
        wrapper.orderByDesc(LeaveRequest::getCreateTime);

        Page<LeaveRequest> page = leaveRequestService.page(new Page<>(pageNum, pageSize), wrapper);
        PageResult<LeaveRequest> pageResult = new PageResult<>(page.getTotal(), page.getRecords());
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    public Result<LeaveRequest> detail(@PathVariable Long id) {
        LeaveRequest leaveRequest = leaveRequestService.getById(id);
        return Result.success(leaveRequest);
    }

    @PostMapping
    public Result<String> add(@RequestBody LeaveRequest leaveRequest) {
        leaveRequestService.save(leaveRequest);
        return Result.success("新增成功");
    }

    @PutMapping
    public Result<String> update(@RequestBody LeaveRequest leaveRequest) {
        leaveRequestService.updateById(leaveRequest);
        return Result.success("修改成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        leaveRequestService.removeById(id);
        return Result.success("删除成功");
    }

    @PutMapping("/approve/{id}")
    public Result<String> approve(@PathVariable Long id, @RequestBody LeaveRequest leaveRequest) {
        leaveRequest.setId(id);
        leaveRequest.setApproveTime(LocalDateTime.now());
        leaveRequestService.updateById(leaveRequest);
        return Result.success("审批成功");
    }
}
