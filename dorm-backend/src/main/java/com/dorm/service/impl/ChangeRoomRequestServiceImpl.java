package com.dorm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dorm.entity.ChangeRoomRequest;
import com.dorm.mapper.ChangeRoomRequestMapper;
import com.dorm.service.ChangeRoomRequestService;
import org.springframework.stereotype.Service;

@Service
public class ChangeRoomRequestServiceImpl extends ServiceImpl<ChangeRoomRequestMapper, ChangeRoomRequest> implements ChangeRoomRequestService {
}
