package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rfid.platform.entity.TagStorageOrderResultBean;
import com.rfid.platform.mapper.TagStorageOrderResultMapper;
import com.rfid.platform.service.TagStorageOrderResultService;
import org.springframework.stereotype.Service;

@Service
public class TagStorageOrderResultServiceImpl extends ServiceImpl<TagStorageOrderResultMapper, TagStorageOrderResultBean> implements TagStorageOrderResultService {
    
    @Override
    public int countCompletedByOrderNoAndProductCode(String orderNo, String productCode) {
        LambdaQueryWrapper<TagStorageOrderResultBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(TagStorageOrderResultBean::getOrderNo, orderNo);
        queryWrapper.eq(TagStorageOrderResultBean::getProductCode, productCode);
        return Math.toIntExact(super.count(queryWrapper));
    }
}
