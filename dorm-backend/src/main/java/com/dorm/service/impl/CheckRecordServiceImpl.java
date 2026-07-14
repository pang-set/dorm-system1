package com.dorm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dorm.entity.CheckRecord;
import com.dorm.mapper.CheckRecordMapper;
import com.dorm.service.CheckRecordService;
import org.springframework.stereotype.Service;

@Service
public class CheckRecordServiceImpl extends ServiceImpl<CheckRecordMapper, CheckRecord> implements CheckRecordService {
}
