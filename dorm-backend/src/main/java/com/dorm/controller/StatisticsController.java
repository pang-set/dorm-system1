package com.dorm.controller;

import com.dorm.common.Result;
import com.dorm.entity.Building;
import com.dorm.entity.Repair;
import com.dorm.entity.Student;
import com.dorm.service.BuildingService;
import com.dorm.service.RepairService;
import com.dorm.service.StudentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private BuildingService buildingService;

    @Autowired
    private RepairService repairService;

    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview() {
        Map<String, Object> data = new HashMap<>();

        long totalStudents = studentService.count();
        long checkedInStudents = studentService.count(
            new LambdaQueryWrapper<Student>().eq(Student::getStatus, 2)
        );

        List<Building> buildings = buildingService.list();
        int totalBuildings = buildings.size();
        int totalRooms = buildings.stream().mapToInt(Building::getRoomCount).sum();
        int totalBeds = buildings.stream().mapToInt(Building::getBedCount).sum();
        int occupiedBeds = buildings.stream().mapToInt(Building::getOccupiedCount).sum();

        double occupancyRate = totalBeds > 0 ? (occupiedBeds * 100.0 / totalBeds) : 0;

        long pendingRepairs = repairService.count(
            new LambdaQueryWrapper<Repair>().eq(Repair::getStatus, 1)
        );
        long processingRepairs = repairService.count(
            new LambdaQueryWrapper<Repair>().eq(Repair::getStatus, 2)
        );
        long completedRepairs = repairService.count(
            new LambdaQueryWrapper<Repair>().eq(Repair::getStatus, 3)
        );

        data.put("totalStudents", totalStudents);
        data.put("checkedInStudents", checkedInStudents);
        data.put("totalBuildings", totalBuildings);
        data.put("totalRooms", totalRooms);
        data.put("totalBeds", totalBeds);
        data.put("occupiedBeds", occupiedBeds);
        data.put("occupancyRate", String.format("%.2f", occupancyRate));
        data.put("pendingRepairs", pendingRepairs);
        data.put("processingRepairs", processingRepairs);
        data.put("completedRepairs", completedRepairs);

        return Result.success(data);
    }

    @GetMapping("/buildingOccupancy")
    public Result<List<Map<String, Object>>> getBuildingOccupancy() {
        List<Building> buildings = buildingService.list();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Building building : buildings) {
            Map<String, Object> item = new HashMap<>();
            item.put("buildingName", building.getBuildingName());
            item.put("totalBeds", building.getBedCount());
            item.put("occupiedBeds", building.getOccupiedCount());
            double rate = building.getBedCount() > 0 ?
                (building.getOccupiedCount() * 100.0 / building.getBedCount()) : 0;
            item.put("occupancyRate", String.format("%.2f", rate));
            result.add(item);
        }

        return Result.success(result);
    }

    @GetMapping("/repairTrend")
    public Result<Map<String, Object>> getRepairTrend(@RequestParam(defaultValue = "7") Integer days) {
        List<String> dates = new ArrayList<>();
        List<Long> counts = new ArrayList<>();

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            dates.add(date.format(formatter));

            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(23, 59, 59);

            long count = repairService.count(
                new LambdaQueryWrapper<Repair>()
                    .ge(Repair::getCreateTime, startOfDay)
                    .le(Repair::getCreateTime, endOfDay)
            );
            counts.add(count);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("dates", dates);
        data.put("counts", counts);

        return Result.success(data);
    }

    @GetMapping("/collegeDistribution")
    public Result<List<Map<String, Object>>> getCollegeDistribution() {
        List<Student> students = studentService.list();
        Map<String, Long> collegeCount = students.stream()
            .filter(s -> s.getCollege() != null && !s.getCollege().isEmpty())
            .collect(Collectors.groupingBy(Student::getCollege, Collectors.counting()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : collegeCount.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", entry.getKey());
            item.put("value", entry.getValue());
            result.add(item);
        }

        result.sort((a, b) -> Long.compare((Long) b.get("value"), (Long) a.get("value")));

        return Result.success(result);
    }

    @GetMapping("/repairType")
    public Result<List<Map<String, Object>>> getRepairTypeDistribution() {
        List<Repair> repairs = repairService.list();
        Map<String, Long> typeCount = repairs.stream()
            .filter(r -> r.getRepairType() != null && !r.getRepairType().isEmpty())
            .collect(Collectors.groupingBy(Repair::getRepairType, Collectors.counting()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : typeCount.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", entry.getKey());
            item.put("value", entry.getValue());
            result.add(item);
        }

        return Result.success(result);
    }
}
