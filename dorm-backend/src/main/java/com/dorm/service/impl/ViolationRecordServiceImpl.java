package com.dorm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dorm.entity.ViolationRecord;
import com.dorm.mapper.ViolationRecordMapper;
import com.dorm.service.ViolationRecordService;
import org.springframework.stereotype.Service;

@Service
public class ViolationRecordServiceImpl extends ServiceImpl<ViolationRecordMapper, ViolationRecord> implements ViolationRecordService {
}
