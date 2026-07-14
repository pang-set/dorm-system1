package com.dorm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dorm.entity.LateReturn;
import com.dorm.mapper.LateReturnMapper;
import com.dorm.service.LateReturnService;
import org.springframework.stereotype.Service;

@Service
public class LateReturnServiceImpl extends ServiceImpl<LateReturnMapper, LateReturn> implements LateReturnService {
}
