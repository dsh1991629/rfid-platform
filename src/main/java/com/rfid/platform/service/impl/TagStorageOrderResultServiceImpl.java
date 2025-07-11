package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rfid.platform.entity.TagStorageOrderResultBean;
import com.rfid.platform.mapper.TagStorageOrderResultMapper;
import com.rfid.platform.service.TagStorageOrderResultService;
import org.springframework.stereotype.Service;

@Service
public class TagStorageOrderResultServiceImpl extends ServiceImpl<TagStorageOrderResultMapper, TagStorageOrderResultBean> implements TagStorageOrderResultService {
}
