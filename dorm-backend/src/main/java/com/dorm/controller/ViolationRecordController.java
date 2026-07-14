package com.dorm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.PageResult;
import com.dorm.common.Result;
import com.dorm.entity.ViolationRecord;
import com.dorm.service.ViolationRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/violation")
public class ViolationRecordController {

    @Autowired
    private ViolationRecordService violationRecordService;

    @GetMapping("/page")
    public Result<PageResult<ViolationRecord>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String violationType) {

        LambdaQueryWrapper<ViolationRecord> wrapper = new LambdaQueryWrapper<>();
        if (studentId != null) {
            wrapper.eq(ViolationRecord::getStudentId, studentId);
        }
        if (buildingId != null) {
            wrapper.eq(ViolationRecord::getBuildingId, buildingId);
        }
        if (status != null) {
            wrapper.eq(ViolationRecord::getStatus, status);
        }
        if (violationType != null && !violationType.isEmpty()) {
            wrapper.eq(ViolationRecord::getViolationType, violationType);
        }
        wrapper.orderByDesc(ViolationRecord::getCreateTime);

        Page<ViolationRecord> page = violationRecordService.page(new Page<>(pageNum, pageSize), wrapper);
        PageResult<ViolationRecord> pageResult = new PageResult<>(page.getTotal(), page.getRecords());
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    public Result<ViolationRecord> detail(@PathVariable Long id) {
        ViolationRecord violationRecord = violationRecordService.getById(id);
        return Result.success(violationRecord);
    }

    @PostMapping
    public Result<String> add(@RequestBody ViolationRecord violationRecord) {
        violationRecordService.save(violationRecord);
        return Result.success("新增成功");
    }

    @PutMapping
    public Result<String> update(@RequestBody ViolationRecord violationRecord) {
        violationRecordService.updateById(violationRecord);
        return Result.success("修改成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        violationRecordService.removeById(id);
        return Result.success("删除成功");
    }

    @PutMapping("/handle/{id}")
    public Result<String> handle(@PathVariable Long id, @RequestBody ViolationRecord violationRecord) {
        violationRecord.setId(id);
        violationRecordService.updateById(violationRecord);
        return Result.success("处理成功");
    }
}
