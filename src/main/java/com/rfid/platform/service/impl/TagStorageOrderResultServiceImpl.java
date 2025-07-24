package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rfid.platform.entity.TagStorageOrderResultBean;
import com.rfid.platform.mapper.TagStorageOrderResultMapper;
import com.rfid.platform.service.TagStorageOrderResultService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagStorageOrderResultServiceImpl extends ServiceImpl<TagStorageOrderResultMapper, TagStorageOrderResultBean> implements TagStorageOrderResultService {
    
    @Override
    public int countCompletedByOrderNoAndProductCode(String orderNoRms, String productCode) {
        LambdaQueryWrapper<TagStorageOrderResultBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(TagStorageOrderResultBean::getOrderNoRms, orderNoRms);
        queryWrapper.eq(TagStorageOrderResultBean::getProductCode, productCode);
        return Math.toIntExact(super.count(queryWrapper));
    }

    @Override
    public boolean saveStorageOrderResults(List<TagStorageOrderResultBean> resultBeans) {
        return super.saveBatch(resultBeans, 50);
    }

    @Override
    public Integer countCompletedBoxByOrderNo(String orderNo) {
        LambdaQueryWrapper<TagStorageOrderResultBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(TagStorageOrderResultBean::getOrderNoRms, orderNo)
                   .select(TagStorageOrderResultBean::getBoxCode)
                   .groupBy(TagStorageOrderResultBean::getBoxCode);
        
        List<TagStorageOrderResultBean> distinctBoxCodes = super.list(queryWrapper);
        return distinctBoxCodes.size();
    }

    @Override
    public Integer countCompletedRfidByOrderNo(String orderNo) {
        LambdaQueryWrapper<TagStorageOrderResultBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(TagStorageOrderResultBean::getOrderNoRms, orderNo);
        return Math.toIntExact(super.count(queryWrapper));
    }


    @Override
    public List<TagStorageOrderResultBean> listTagStorageOrderResults(String orderNoRms) {
        LambdaQueryWrapper<TagStorageOrderResultBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(TagStorageOrderResultBean::getOrderNoRms, orderNoRms);
        return super.list(queryWrapper);
    }

    @Override
    public List<TagStorageOrderResultBean> listTagStorageOrderResultsByOrderRmsAndProductCode(String orderNoRms, String productCode) {
        LambdaQueryWrapper<TagStorageOrderResultBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(TagStorageOrderResultBean::getOrderNoRms, orderNoRms);
        queryWrapper.eq(TagStorageOrderResultBean::getProductCode, productCode);
        return super.list(queryWrapper);
    }

    @Override
    public boolean existResultByBox(String boxCode) {
        LambdaQueryWrapper<TagStorageOrderResultBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(TagStorageOrderResultBean::getBoxCode, boxCode);
        return super.exists(queryWrapper);
    }

    @Override
    public boolean removeStorageOrderResults(String orderNoRms, String boxCode) {
        LambdaQueryWrapper<TagStorageOrderResultBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(TagStorageOrderResultBean::getOrderNoRms, orderNoRms);
        queryWrapper.eq(TagStorageOrderResultBean::getBoxCode, boxCode);
        return super.remove(queryWrapper);
    }

    @Override
    public List<TagStorageOrderResultBean> listTagStorageOrderResultsByOrderRmsAndBoxCode(String orderNoRms, String boxCode) {
        LambdaQueryWrapper<TagStorageOrderResultBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(TagStorageOrderResultBean::getOrderNoRms, orderNoRms);
        queryWrapper.eq(TagStorageOrderResultBean::getBoxCode, boxCode);
        return super.list(queryWrapper);
    }
}
