package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rfid.platform.common.AccountContext;
import com.rfid.platform.common.PlatformConstant;
import com.rfid.platform.entity.TagStorageOrderBean;
import com.rfid.platform.mapper.TagStorageOrderMapper;
import com.rfid.platform.persistence.storage.StorageCheckQueryRequestDTO;
import com.rfid.platform.persistence.storage.StorageOrderItemRequestDTO;
import com.rfid.platform.service.TagStorageOrderDetailService;
import com.rfid.platform.service.TagStorageOrderService;
import com.rfid.platform.util.TimeUtil;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TagStorageOrderServiceImpl extends ServiceImpl<TagStorageOrderMapper, TagStorageOrderBean> implements TagStorageOrderService {

    @Autowired
    @Lazy
    private TagStorageOrderDetailService tagStorageOrderDetailService;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long saveInboundTagStorageOrder(String timeStamp, String orderNo, String orderType, List<StorageOrderItemRequestDTO> items) {
        TagStorageOrderBean tagStorageOrderBean = new TagStorageOrderBean();
        tagStorageOrderBean.setOrderNo(orderNo);
        tagStorageOrderBean.setType(PlatformConstant.STORAGE_ORDER_TYPE.IN_BOUND);
        tagStorageOrderBean.setOrderType(orderType);
        tagStorageOrderBean.setState(PlatformConstant.STORAGE_ORDER_STATUS.SEND);
        tagStorageOrderBean.setCreateUser(String.valueOf(AccountContext.getAccountId()));
        tagStorageOrderBean.setCreateDate(timeStamp);
        tagStorageOrderBean.setCreateTime(TimeUtil.localDateTimeToTimestamp(TimeUtil.parseDateFormatterString(timeStamp)));
        super.save(tagStorageOrderBean);

        tagStorageOrderDetailService.saveStorageOrderDetails(orderNo, items);

        return tagStorageOrderBean.getId();
    }



    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long saveOutboundTagStorageOrder(String timeStamp, String orderNo, String orderType, List<StorageOrderItemRequestDTO> items) {
        TagStorageOrderBean tagStorageOrderBean = new TagStorageOrderBean();
        tagStorageOrderBean.setOrderNo(orderNo);
        tagStorageOrderBean.setType(PlatformConstant.STORAGE_ORDER_TYPE.OUT_BOUND);
        tagStorageOrderBean.setOrderType(orderType);
        tagStorageOrderBean.setState(PlatformConstant.STORAGE_ORDER_STATUS.SEND);
        tagStorageOrderBean.setCreateUser(String.valueOf(AccountContext.getAccountId()));
        tagStorageOrderBean.setCreateDate(timeStamp);
        tagStorageOrderBean.setCreateTime(TimeUtil.localDateTimeToTimestamp(TimeUtil.parseDateFormatterString(timeStamp)));
        super.save(tagStorageOrderBean);

        tagStorageOrderDetailService.saveStorageOrderDetails(orderNo, items);

        return tagStorageOrderBean.getId();
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long saveInventoryTagStorageOrder(String timeStamp, String orderNo, List<StorageOrderItemRequestDTO> items) {
        TagStorageOrderBean tagStorageOrderBean = new TagStorageOrderBean();
        tagStorageOrderBean.setOrderNo(orderNo);
        tagStorageOrderBean.setType(PlatformConstant.STORAGE_ORDER_TYPE.INVENTORY_BOUND);
        tagStorageOrderBean.setOrderType("");
        tagStorageOrderBean.setState(PlatformConstant.STORAGE_ORDER_STATUS.SEND);
        tagStorageOrderBean.setCreateUser(String.valueOf(AccountContext.getAccountId()));
        tagStorageOrderBean.setCreateDate(timeStamp);
        tagStorageOrderBean.setCreateTime(TimeUtil.localDateTimeToTimestamp(TimeUtil.parseDateFormatterString(timeStamp)));
        super.save(tagStorageOrderBean);

        tagStorageOrderDetailService.saveStorageOrderDetails(orderNo, items);

        return tagStorageOrderBean.getId();
    }


    @Override
    public boolean checkStorageOrderCancelable(String orderNo, Integer orderType) {
        LambdaQueryWrapper<TagStorageOrderBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(TagStorageOrderBean::getOrderNo, orderNo);
        queryWrapper.eq(TagStorageOrderBean::getState, PlatformConstant.STORAGE_ORDER_STATUS.SEND);
        queryWrapper.eq(TagStorageOrderBean::getOrderType, orderType);
        return super.exists(queryWrapper);
    }


    @Override
    public Long cancelTagStorageOrder(String timeStamp, String orderNo, Integer orderType) {
        LambdaQueryWrapper<TagStorageOrderBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(TagStorageOrderBean::getOrderNo, orderNo);
        queryWrapper.eq(TagStorageOrderBean::getState, PlatformConstant.STORAGE_ORDER_STATUS.SEND);
        queryWrapper.eq(TagStorageOrderBean::getOrderType, orderType);
        TagStorageOrderBean tagStorageOrderBean = super.getOne(queryWrapper);
        if (Objects.nonNull(tagStorageOrderBean)) {
            tagStorageOrderBean.setState(PlatformConstant.STORAGE_ORDER_STATUS.CANCELED);
            tagStorageOrderBean.setUpdateDate(timeStamp);
            tagStorageOrderBean.setUpdateUser(String.valueOf(AccountContext.getAccountId()));
            tagStorageOrderBean.setUpdateTime(TimeUtil.localDateTimeToTimestamp(TimeUtil.parseDateFormatterString(timeStamp)));
            super.updateById(tagStorageOrderBean);
        }
        return tagStorageOrderBean.getId();
    }


    @Override
    public List<TagStorageOrderBean> queryActiveInBoundOrders(StorageCheckQueryRequestDTO data) {
        LambdaQueryWrapper<TagStorageOrderBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(TagStorageOrderBean::getOrderType, PlatformConstant.STORAGE_ORDER_TYPE.IN_BOUND);
        queryWrapper.eq(TagStorageOrderBean::getState, PlatformConstant.STORAGE_ORDER_STATUS.SEND)
                .or().eq(TagStorageOrderBean::getState, PlatformConstant.STORAGE_ORDER_STATUS.EXECUTING);
        if (Objects.nonNull(data)) {
            queryWrapper.like(TagStorageOrderBean::getOrderNo, data.getOrderNo());
            queryWrapper.ge(StringUtils.isNotBlank(data.getTimeBegin()), TagStorageOrderBean::getCreateTime, TimeUtil.getDayNoLineTimestamp(data.getTimeBegin()));
            queryWrapper.le(StringUtils.isNotBlank(data.getTimeEnd()), TagStorageOrderBean::getCreateTime, TimeUtil.getDayNoLineTimestamp(data.getTimeEnd()));
        }
        return super.list(queryWrapper);
    }


    @Override
    public List<TagStorageOrderBean> queryActiveOutBoundOrders(StorageCheckQueryRequestDTO data) {
        LambdaQueryWrapper<TagStorageOrderBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(TagStorageOrderBean::getOrderType, PlatformConstant.STORAGE_ORDER_TYPE.OUT_BOUND);
        queryWrapper.eq(TagStorageOrderBean::getState, PlatformConstant.STORAGE_ORDER_STATUS.SEND)
                .or().eq(TagStorageOrderBean::getState, PlatformConstant.STORAGE_ORDER_STATUS.EXECUTING);
        if (Objects.nonNull(data)) {
            queryWrapper.like(TagStorageOrderBean::getOrderNo, data.getOrderNo());
            queryWrapper.ge(StringUtils.isNotBlank(data.getTimeBegin()), TagStorageOrderBean::getCreateTime, TimeUtil.getDayNoLineTimestamp(data.getTimeBegin()));
            queryWrapper.le(StringUtils.isNotBlank(data.getTimeEnd()), TagStorageOrderBean::getCreateTime, TimeUtil.getDayNoLineTimestamp(data.getTimeEnd()));
        }
        return super.list(queryWrapper);
    }


    @Override
    public List<TagStorageOrderBean> queryActiveInventoryOrders(StorageCheckQueryRequestDTO data) {
        LambdaQueryWrapper<TagStorageOrderBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(TagStorageOrderBean::getOrderType, PlatformConstant.STORAGE_ORDER_TYPE.INVENTORY_BOUND);
        queryWrapper.eq(TagStorageOrderBean::getState, PlatformConstant.STORAGE_ORDER_STATUS.SEND)
                .or().eq(TagStorageOrderBean::getState, PlatformConstant.STORAGE_ORDER_STATUS.EXECUTING);
        if (Objects.nonNull(data)) {
            queryWrapper.like(TagStorageOrderBean::getOrderNo, data.getOrderNo());
            queryWrapper.ge(StringUtils.isNotBlank(data.getTimeBegin()), TagStorageOrderBean::getCreateTime, TimeUtil.getDayNoLineTimestamp(data.getTimeBegin()));
            queryWrapper.le(StringUtils.isNotBlank(data.getTimeEnd()), TagStorageOrderBean::getCreateTime, TimeUtil.getDayNoLineTimestamp(data.getTimeEnd()));
        }
        return super.list(queryWrapper);
    }

    @Override
    public boolean updateOrderStateByOrderNo(String orderNo, String timeStamp, Integer state) {
        if (StringUtils.isBlank(orderNo)  || Objects.isNull(state)) {
            return false;
        }
        
        LambdaQueryWrapper<TagStorageOrderBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(TagStorageOrderBean::getOrderNo, orderNo);
        TagStorageOrderBean existsBean = super.getOne(queryWrapper);
        
        if (Objects.isNull(existsBean)) {
            return false;
        }

        // 更新订单状态和相关字段
        existsBean.setState(state);
        existsBean.setUpdateDate(timeStamp);
        existsBean.setUpdateUser(String.valueOf(AccountContext.getAccountId()));
        existsBean.setUpdateTime(TimeUtil.localDateTimeToTimestamp(TimeUtil.parseDateFormatterString(timeStamp)));
        return super.updateById(existsBean);
    }


    @Override
    public TagStorageOrderBean queryTagStorageOrderByNo(String orderNo) {
        LambdaQueryWrapper<TagStorageOrderBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(TagStorageOrderBean::getOrderNo, orderNo);
        return super.getOne(queryWrapper);
    }
}
