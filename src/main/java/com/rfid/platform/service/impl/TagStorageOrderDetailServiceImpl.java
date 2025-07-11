package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rfid.platform.entity.TagStorageOrderDetailBean;
import com.rfid.platform.mapper.TagStorageOrderDetailMapper;
import com.rfid.platform.service.TagStorageOrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class TagStorageOrderDetailServiceImpl extends ServiceImpl<TagStorageOrderDetailMapper, TagStorageOrderDetailBean> implements TagStorageOrderDetailService {
}
