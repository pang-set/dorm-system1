package com.dorm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dorm.entity.Building;
import com.dorm.mapper.BuildingMapper;
import com.dorm.service.BuildingService;
import org.springframework.stereotype.Service;

@Service
public class BuildingServiceImpl extends ServiceImpl<BuildingMapper, Building> implements BuildingService {
}
