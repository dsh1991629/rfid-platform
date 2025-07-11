package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rfid.platform.entity.TagStorageOrderBean;
import com.rfid.platform.mapper.TagStorageOrderMapper;
import com.rfid.platform.service.TagStorageOrderService;
import org.springframework.stereotype.Service;

@Service
public class TagStorageOrderServiceImpl extends ServiceImpl<TagStorageOrderMapper, TagStorageOrderBean> implements TagStorageOrderService {
}
