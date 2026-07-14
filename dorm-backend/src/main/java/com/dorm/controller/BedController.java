package com.dorm.controller;

import com.dorm.common.PageResult;
import com.dorm.common.Result;
import com.dorm.entity.Bed;
import com.dorm.entity.Student;
import com.dorm.service.BedService;
import com.dorm.service.StudentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bed")
public class BedController {

    @Autowired
    private BedService bedService;

    @Autowired
    private StudentService studentService;

    @GetMapping("/page")
    public Result<PageResult<Bed>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) Integer status) {

        Page<Bed> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Bed> wrapper = new LambdaQueryWrapper<>();

        if (buildingId != null) {
            wrapper.eq(Bed::getBuildingId, buildingId);
        }
        if (roomId != null) {
            wrapper.eq(Bed::getRoomId, roomId);
        }
        if (status != null) {
            wrapper.eq(Bed::getStatus, status);
        }
        wrapper.orderByAsc(Bed::getBedNo);

        bedService.page(page, wrapper);

        PageResult<Bed> result = new PageResult<>(page.getTotal(), page.getRecords());
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Bed> getById(@PathVariable Long id) {
        Bed bed = bedService.getById(id);
        return Result.success(bed);
    }

    @PostMapping
    public Result<String> add(@RequestBody Bed bed) {
        bedService.save(bed);
        return Result.success("新增成功");
    }

    @PutMapping
    public Result<String> update(@RequestBody Bed bed) {
        bedService.updateById(bed);
        return Result.success("修改成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        bedService.removeById(id);
        return Result.success("删除成功");
    }

    @GetMapping("/{id}/student")
    public Result<Student> getStudentByBedId(@PathVariable Long id) {
        Bed bed = bedService.getById(id);
        if (bed != null && bed.getStudentId() != null) {
            Student student = studentService.getById(bed.getStudentId());
            return Result.success(student);
        }
        return Result.success(null);
    }
}
