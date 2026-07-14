package com.dorm.controller;

import com.dorm.common.PageResult;
import com.dorm.common.Result;
import com.dorm.entity.Bed;
import com.dorm.entity.DormRoom;
import com.dorm.entity.Student;
import com.dorm.service.BedService;
import com.dorm.service.DormRoomService;
import com.dorm.service.StudentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private BedService bedService;

    @Autowired
    private DormRoomService dormRoomService;

    @GetMapping("/page")
    public Result<PageResult<Student>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String studentNo,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) String college,
            @RequestParam(required = false) Integer status) {

        Page<Student> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();

        if (studentNo != null && !studentNo.isEmpty()) {
            wrapper.like(Student::getStudentNo, studentNo);
        }
        if (name != null && !name.isEmpty()) {
            wrapper.like(Student::getName, name);
        }
        if (buildingId != null) {
            wrapper.eq(Student::getBuildingId, buildingId);
        }
        if (roomId != null) {
            wrapper.eq(Student::getRoomId, roomId);
        }
        if (college != null && !college.isEmpty()) {
            wrapper.like(Student::getCollege, college);
        }
        if (status != null) {
            wrapper.eq(Student::getStatus, status);
        }
        wrapper.orderByDesc(Student::getCreateTime);

        studentService.page(page, wrapper);

        PageResult<Student> result = new PageResult<>(page.getTotal(), page.getRecords());
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Student> getById(@PathVariable Long id) {
        Student student = studentService.getById(id);
        return Result.success(student);
    }

    @GetMapping("/no/{studentNo}")
    public Result<Student> getByStudentNo(@PathVariable String studentNo) {
        Student student = studentService.getOne(
            new LambdaQueryWrapper<Student>().eq(Student::getStudentNo, studentNo)
        );
        return Result.success(student);
    }

    @PostMapping
    public Result<String> add(@RequestBody Student student) {
        Student exist = studentService.getOne(
            new LambdaQueryWrapper<Student>().eq(Student::getStudentNo, student.getStudentNo())
        );
        if (exist != null) {
            return Result.error("学号已存在");
        }
        if (student.getStatus() == null) {
            student.setStatus(1);
        }
        studentService.save(student);
        return Result.success("新增成功");
    }

    @PutMapping
    public Result<String> update(@RequestBody Student student) {
        studentService.updateById(student);
        return Result.success("修改成功");
    }

    @DeleteMapping("/{id}")
    @Transactional
    public Result<String> delete(@PathVariable Long id) {
        Student student = studentService.getById(id);
        if (student != null && student.getBedId() != null) {
            Bed bed = bedService.getById(student.getBedId());
            if (bed != null) {
                bed.setStatus(0);
                bed.setStudentId(null);
                bedService.updateById(bed);

                DormRoom room = dormRoomService.getById(student.getRoomId());
                if (room != null) {
                    room.setBedUsed(room.getBedUsed() - 1);
                    dormRoomService.updateById(room);
                }
            }
        }
        studentService.removeById(id);
        return Result.success("删除成功");
    }

    @PostMapping("/checkIn")
    @Transactional
    public Result<String> checkIn(@RequestParam Long studentId, @RequestParam Long bedId) {
        Student student = studentService.getById(studentId);
        Bed bed = bedService.getById(bedId);

        if (student == null) {
            return Result.error("学生不存在");
        }
        if (bed == null) {
            return Result.error("床位不存在");
        }
        if (bed.getStatus() == 1) {
            return Result.error("床位已被占用");
        }

        if (student.getBedId() != null) {
            Bed oldBed = bedService.getById(student.getBedId());
            if (oldBed != null) {
                oldBed.setStatus(0);
                oldBed.setStudentId(null);
                bedService.updateById(oldBed);

                DormRoom oldRoom = dormRoomService.getById(student.getRoomId());
                if (oldRoom != null) {
                    oldRoom.setBedUsed(oldRoom.getBedUsed() - 1);
                    dormRoomService.updateById(oldRoom);
                }
            }
        }

        bed.setStatus(1);
        bed.setStudentId(studentId);
        bedService.updateById(bed);

        student.setBuildingId(bed.getBuildingId());
        student.setRoomId(bed.getRoomId());
        student.setBedId(bedId);
        student.setStatus(2);
        student.setCheckInDate(LocalDate.now());
        studentService.updateById(student);

        DormRoom room = dormRoomService.getById(bed.getRoomId());
        if (room != null) {
            room.setBedUsed(room.getBedUsed() + 1);
            dormRoomService.updateById(room);
        }

        return Result.success("入住成功");
    }

    @PostMapping("/checkOut")
    @Transactional
    public Result<String> checkOut(@RequestParam Long studentId) {
        Student student = studentService.getById(studentId);
        if (student == null) {
            return Result.error("学生不存在");
        }
        if (student.getBedId() == null) {
            return Result.error("学生未入住");
        }

        Bed bed = bedService.getById(student.getBedId());
        if (bed != null) {
            bed.setStatus(0);
            bed.setStudentId(null);
            bedService.updateById(bed);
        }

        DormRoom room = dormRoomService.getById(student.getRoomId());
        if (room != null) {
            room.setBedUsed(room.getBedUsed() - 1);
            dormRoomService.updateById(room);
        }

        student.setBedId(null);
        student.setRoomId(null);
        student.setBuildingId(null);
        student.setStatus(3);
        student.setCheckOutDate(LocalDate.now());
        studentService.updateById(student);

        return Result.success("退宿成功");
    }
}
