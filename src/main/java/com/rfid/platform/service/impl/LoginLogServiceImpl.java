package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rfid.platform.entity.LoginLogBean;
import com.rfid.platform.mapper.LoginLogMapper;
import com.rfid.platform.service.LoginLogService;
import org.springframework.stereotype.Service;

@Service
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLogBean> implements LoginLogService {
}
