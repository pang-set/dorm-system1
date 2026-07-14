package com.dorm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dorm.entity.Bed;
import com.dorm.mapper.BedMapper;
import com.dorm.service.BedService;
import org.springframework.stereotype.Service;

@Service
public class BedServiceImpl extends ServiceImpl<BedMapper, Bed> implements BedService {
}
