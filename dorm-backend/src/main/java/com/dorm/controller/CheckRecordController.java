package com.dorm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.PageResult;
import com.dorm.common.Result;
import com.dorm.entity.CheckRecord;
import com.dorm.service.CheckRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/checkRecord")
public class CheckRecordController {

    @Autowired
    private CheckRecordService checkRecordService;

    @GetMapping("/page")
    public Result<PageResult<CheckRecord>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate) {

        LambdaQueryWrapper<CheckRecord> wrapper = new LambdaQueryWrapper<>();
        if (buildingId != null) {
            wrapper.eq(CheckRecord::getBuildingId, buildingId);
        }
        if (roomId != null) {
            wrapper.eq(CheckRecord::getRoomId, roomId);
        }
        if (startDate != null) {
            wrapper.ge(CheckRecord::getCheckTime, startDate);
        }
        if (endDate != null) {
            wrapper.le(CheckRecord::getCheckTime, endDate);
        }
        wrapper.orderByDesc(CheckRecord::getCreateTime);

        Page<CheckRecord> page = checkRecordService.page(new Page<>(pageNum, pageSize), wrapper);
        PageResult<CheckRecord> pageResult = new PageResult<>(page.getTotal(), page.getRecords());
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    public Result<CheckRecord> detail(@PathVariable Long id) {
        CheckRecord checkRecord = checkRecordService.getById(id);
        return Result.success(checkRecord);
    }

    @PostMapping
    public Result<String> add(@RequestBody CheckRecord checkRecord) {
        checkRecordService.save(checkRecord);
        return Result.success("新增成功");
    }

    @PutMapping
    public Result<String> update(@RequestBody CheckRecord checkRecord) {
        checkRecordService.updateById(checkRecord);
        return Result.success("修改成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        checkRecordService.removeById(id);
        return Result.success("删除成功");
    }
}
