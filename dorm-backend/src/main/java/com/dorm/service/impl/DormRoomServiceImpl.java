package com.dorm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dorm.entity.DormRoom;
import com.dorm.mapper.DormRoomMapper;
import com.dorm.service.DormRoomService;
import org.springframework.stereotype.Service;

@Service
public class DormRoomServiceImpl extends ServiceImpl<DormRoomMapper, DormRoom> implements DormRoomService {
}
