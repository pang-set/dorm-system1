package com.dorm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.PageResult;
import com.dorm.common.Result;
import com.dorm.entity.Repair;
import com.dorm.service.RepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/repair")
public class RepairController {

    @Autowired
    private RepairService repairService;

    @GetMapping("/page")
    public Result<PageResult<Repair>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String repairType) {

        LambdaQueryWrapper<Repair> wrapper = new LambdaQueryWrapper<>();
        if (studentId != null) {
            wrapper.eq(Repair::getStudentId, studentId);
        }
        if (buildingId != null) {
            wrapper.eq(Repair::getBuildingId, buildingId);
        }
        if (status != null) {
            wrapper.eq(Repair::getStatus, status);
        }
        if (repairType != null && !repairType.isEmpty()) {
            wrapper.eq(Repair::getRepairType, repairType);
        }
        wrapper.orderByDesc(Repair::getCreateTime);

        Page<Repair> page = repairService.page(new Page<>(pageNum, pageSize), wrapper);
        PageResult<Repair> pageResult = new PageResult<>(page.getTotal(), page.getRecords());
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    public Result<Repair> detail(@PathVariable Long id) {
        Repair repair = repairService.getById(id);
        return Result.success(repair);
    }

    @PostMapping
    public Result<String> add(@RequestBody Repair repair) {
        String repairNo = "BX" + System.currentTimeMillis();
        repair.setRepairNo(repairNo);
        repairService.save(repair);
        return Result.success("新增成功");
    }

    @PutMapping
    public Result<String> update(@RequestBody Repair repair) {
        repairService.updateById(repair);
        return Result.success("修改成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        repairService.removeById(id);
        return Result.success("删除成功");
    }

    @PutMapping("/assign/{id}")
    public Result<String> assign(@PathVariable Long id, @RequestBody Repair repair) {
        repair.setId(id);
        repairService.updateById(repair);
        return Result.success("派单成功");
    }

    @PutMapping("/complete/{id}")
    public Result<String> complete(@PathVariable Long id, @RequestBody Repair repair) {
        repair.setId(id);
        repairService.updateById(repair);
        return Result.success("完成成功");
    }

    @PutMapping("/rate/{id}")
    public Result<String> rate(@PathVariable Long id, @RequestBody Repair repair) {
        repair.setId(id);
        repairService.updateById(repair);
        return Result.success("评价成功");
    }
}
