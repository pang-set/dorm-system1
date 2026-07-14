package com.dorm.controller;

import com.dorm.common.PageResult;
import com.dorm.common.Result;
import com.dorm.entity.Bed;
import com.dorm.entity.DormRoom;
import com.dorm.service.BedService;
import com.dorm.service.DormRoomService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/room")
public class DormRoomController {

    @Autowired
    private DormRoomService dormRoomService;

    @Autowired
    private BedService bedService;

    @GetMapping("/page")
    public Result<PageResult<DormRoom>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) String roomNo,
            @RequestParam(required = false) Integer floor,
            @RequestParam(required = false) Integer status) {

        Page<DormRoom> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<DormRoom> wrapper = new LambdaQueryWrapper<>();

        if (buildingId != null) {
            wrapper.eq(DormRoom::getBuildingId, buildingId);
        }
        if (roomNo != null && !roomNo.isEmpty()) {
            wrapper.like(DormRoom::getRoomNo, roomNo);
        }
        if (floor != null) {
            wrapper.eq(DormRoom::getFloor, floor);
        }
        if (status != null) {
            wrapper.eq(DormRoom::getStatus, status);
        }
        wrapper.orderByAsc(DormRoom::getFloor).orderByAsc(DormRoom::getRoomNo);

        dormRoomService.page(page, wrapper);

        PageResult<DormRoom> result = new PageResult<>(page.getTotal(), page.getRecords());
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<DormRoom> getById(@PathVariable Long id) {
        DormRoom room = dormRoomService.getById(id);
        return Result.success(room);
    }

    @GetMapping("/{id}/beds")
    public Result<List<Bed>> getBedsByRoomId(@PathVariable Long id) {
        List<Bed> beds = bedService.list(
            new LambdaQueryWrapper<Bed>()
                .eq(Bed::getRoomId, id)
                .orderByAsc(Bed::getBedNo)
        );
        return Result.success(beds);
    }

    @PostMapping
    @Transactional
    public Result<String> add(@RequestBody DormRoom dormRoom) {
        dormRoom.setBedUsed(0);
        dormRoom.setStatus(1);
        dormRoomService.save(dormRoom);

        List<Bed> beds = new ArrayList<>();
        for (int i = 1; i <= dormRoom.getBedTotal(); i++) {
            Bed bed = new Bed();
            bed.setRoomId(dormRoom.getId());
            bed.setBuildingId(dormRoom.getBuildingId());
            bed.setBedNo(dormRoom.getRoomNo() + "-" + i);
            bed.setStatus(0);
            beds.add(bed);
        }
        bedService.saveBatch(beds);

        return Result.success("新增成功");
    }

    @PutMapping
    public Result<String> update(@RequestBody DormRoom dormRoom) {
        dormRoomService.updateById(dormRoom);
        return Result.success("修改成功");
    }

    @DeleteMapping("/{id}")
    @Transactional
    public Result<String> delete(@PathVariable Long id) {
        dormRoomService.removeById(id);
        bedService.remove(new LambdaQueryWrapper<Bed>().eq(Bed::getRoomId, id));
        return Result.success("删除成功");
    }

    @GetMapping("/available")
    public Result<List<DormRoom>> getAvailableRooms(
            @RequestParam Long buildingId,
            @RequestParam(required = false) String gender) {
        List<DormRoom> rooms = dormRoomService.list(
            new LambdaQueryWrapper<DormRoom>()
                .eq(DormRoom::getBuildingId, buildingId)
                .eq(DormRoom::getStatus, 1)
                .apply("bed_used < bed_total")
                .orderByAsc(DormRoom::getRoomNo)
        );
        return Result.success(rooms);
    }
}
