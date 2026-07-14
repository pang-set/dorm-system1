package com.dorm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dorm.entity.LeaveRequest;
import com.dorm.mapper.LeaveRequestMapper;
import com.dorm.service.LeaveRequestService;
import org.springframework.stereotype.Service;

@Service
public class LeaveRequestServiceImpl extends ServiceImpl<LeaveRequestMapper, LeaveRequest> implements LeaveRequestService {
}
