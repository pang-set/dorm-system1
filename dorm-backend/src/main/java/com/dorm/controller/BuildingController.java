package com.dorm.controller;

import com.dorm.common.PageResult;
import com.dorm.common.Result;
import com.dorm.entity.Building;
import com.dorm.service.BuildingService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/building")
public class BuildingController {

    @Autowired
    private BuildingService buildingService;

    @GetMapping("/page")
    public Result<PageResult<Building>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String buildingName,
            @RequestParam(required = false) String gender) {

        Page<Building> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Building> wrapper = new LambdaQueryWrapper<>();

        if (buildingName != null && !buildingName.isEmpty()) {
            wrapper.like(Building::getBuildingName, buildingName);
        }
        if (gender != null && !gender.isEmpty()) {
            wrapper.eq(Building::getGender, gender);
        }
        wrapper.orderByDesc(Building::getCreateTime);

        buildingService.page(page, wrapper);

        PageResult<Building> result = new PageResult<>(page.getTotal(), page.getRecords());
        return Result.success(result);
    }

    @GetMapping("/all")
    public Result<List<Building>> all() {
        List<Building> list = buildingService.list();
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Building> getById(@PathVariable Long id) {
        Building building = buildingService.getById(id);
        return Result.success(building);
    }

    @PostMapping
    public Result<String> add(@RequestBody Building building) {
        buildingService.save(building);
        return Result.success("新增成功");
    }

    @PutMapping
    public Result<String> update(@RequestBody Building building) {
        buildingService.updateById(building);
        return Result.success("修改成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        buildingService.removeById(id);
        return Result.success("删除成功");
    }
}
