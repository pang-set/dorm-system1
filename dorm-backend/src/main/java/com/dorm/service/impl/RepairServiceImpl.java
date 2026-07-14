package com.dorm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dorm.entity.Repair;
import com.dorm.mapper.RepairMapper;
import com.dorm.service.RepairService;
import org.springframework.stereotype.Service;

@Service
public class RepairServiceImpl extends ServiceImpl<RepairMapper, Repair> implements RepairService {
}
