package com.dorm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.PageResult;
import com.dorm.common.Result;
import com.dorm.entity.LateReturn;
import com.dorm.service.LateReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/lateReturn")
public class LateReturnController {

    @Autowired
    private LateReturnService lateReturnService;

    @GetMapping("/page")
    public Result<PageResult<LateReturn>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate) {

        LambdaQueryWrapper<LateReturn> wrapper = new LambdaQueryWrapper<>();
        if (studentId != null) {
            wrapper.eq(LateReturn::getStudentId, studentId);
        }
        if (buildingId != null) {
            wrapper.eq(LateReturn::getBuildingId, buildingId);
        }
        if (startDate != null) {
            wrapper.ge(LateReturn::getReturnTime, startDate);
        }
        if (endDate != null) {
            wrapper.le(LateReturn::getReturnTime, endDate);
        }
        wrapper.orderByDesc(LateReturn::getCreateTime);

        Page<LateReturn> page = lateReturnService.page(new Page<>(pageNum, pageSize), wrapper);
        PageResult<LateReturn> pageResult = new PageResult<>(page.getTotal(), page.getRecords());
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    public Result<LateReturn> detail(@PathVariable Long id) {
        LateReturn lateReturn = lateReturnService.getById(id);
        return Result.success(lateReturn);
    }

    @PostMapping
    public Result<String> add(@RequestBody LateReturn lateReturn) {
        lateReturnService.save(lateReturn);
        return Result.success("新增成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        lateReturnService.removeById(id);
        return Result.success("删除成功");
    }
}
