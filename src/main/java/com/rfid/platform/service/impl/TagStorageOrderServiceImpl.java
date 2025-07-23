package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rfid.platform.common.AccountContext;
import com.rfid.platform.common.PlatformConstant;
import com.rfid.platform.entity.TagStorageOrderBean;
import com.rfid.platform.mapper.TagStorageOrderMapper;
import com.rfid.platform.persistence.storage.DevInventoryOrderQueryRequestDTO;
import com.rfid.platform.persistence.storage.DevOutBoundOrderQueryRequestDTO;
import com.rfid.platform.persistence.storage.InBoundOrderRequestDTO;
import com.rfid.platform.persistence.storage.InventoryOrderRequestDTO;
import com.rfid.platform.persistence.storage.OutBoundOrderRequestDTO;
import com.rfid.platform.persistence.storage.DevInBoundOrderQueryRequestDTO;
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
    public String saveInboundTagStorageOrder(String timeStamp, InBoundOrderRequestDTO inBoundOrderRequestDTO) {
        String orderNoRms = createOrderNoRms(PlatformConstant.STORAGE_ORDER_TYPE.IN_BOUND, inBoundOrderRequestDTO.getOrderType());
        TagStorageOrderBean tagStorageOrderBean = new TagStorageOrderBean();
        tagStorageOrderBean.setOrderNoRms(orderNoRms);
        tagStorageOrderBean.setOrderNoErp(inBoundOrderRequestDTO.getOrderNoERP());
        tagStorageOrderBean.setOrderNoWms(inBoundOrderRequestDTO.getOrderNoWMS());
        tagStorageOrderBean.setType(PlatformConstant.STORAGE_ORDER_TYPE.IN_BOUND);
        tagStorageOrderBean.setOrderType(inBoundOrderRequestDTO.getOrderType());
        tagStorageOrderBean.setWh(inBoundOrderRequestDTO.getWh());
        tagStorageOrderBean.setState(PlatformConstant.STORAGE_ORDER_STATUS.SEND);
        tagStorageOrderBean.setCreateUser(String.valueOf(AccountContext.getAccountId()));
        tagStorageOrderBean.setCreateDate(timeStamp);
        tagStorageOrderBean.setCreateTime(TimeUtil.localDateTimeToTimestamp(TimeUtil.parseDateFormatterString(timeStamp)));
        super.save(tagStorageOrderBean);

        tagStorageOrderDetailService.saveInBoundOrderDetails(orderNoRms, inBoundOrderRequestDTO.getItems());

        return orderNoRms;
    }

    private String createOrderNoRms(Integer type, String orderType) {
        StringBuilder orderNoRms = new StringBuilder();
        if (PlatformConstant.STORAGE_ORDER_TYPE.IN_BOUND.equals(type)) {
            orderNoRms.append("INBOUND_ORDER");
        }
        if (PlatformConstant.STORAGE_ORDER_TYPE.IN_BOUND.equals(type)) {
            orderNoRms.append("OUTBOUND_ORDER");
        }
        if (PlatformConstant.STORAGE_ORDER_TYPE.IN_BOUND.equals(type)) {
            orderNoRms.append("INVENTORY_ORDER");
        }
        orderNoRms.append("_");
        if (StringUtils.isNotBlank(orderNoRms)) {
            orderNoRms.append(orderType.toUpperCase());
            orderNoRms.append("_");
        }
        orderNoRms.append(TimeUtil.getSecondNoLineString(TimeUtil.getSysDate()));
        return orderNoRms.toString();
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public String saveOutboundTagStorageOrder(String timeStamp, OutBoundOrderRequestDTO outBoundOrderRequestDTO) {
        String orderNoRms = createOrderNoRms(PlatformConstant.STORAGE_ORDER_TYPE.OUT_BOUND, outBoundOrderRequestDTO.getOrderType());
        TagStorageOrderBean tagStorageOrderBean = new TagStorageOrderBean();
        tagStorageOrderBean.setOrderNoRms(orderNoRms);
        tagStorageOrderBean.setOrderNoWms(outBoundOrderRequestDTO.getOrderNoWMS());
        tagStorageOrderBean.setOrderNoErp(outBoundOrderRequestDTO.getOrderNoERP());
        tagStorageOrderBean.setType(PlatformConstant.STORAGE_ORDER_TYPE.OUT_BOUND);
        tagStorageOrderBean.setOrderType(outBoundOrderRequestDTO.getOrderType());
        tagStorageOrderBean.setWh(outBoundOrderRequestDTO.getWh());
        tagStorageOrderBean.setState(PlatformConstant.STORAGE_ORDER_STATUS.SEND);
        tagStorageOrderBean.setCreateUser(String.valueOf(AccountContext.getAccountId()));
        tagStorageOrderBean.setCreateDate(timeStamp);
        tagStorageOrderBean.setCreateTime(TimeUtil.localDateTimeToTimestamp(TimeUtil.parseDateFormatterString(timeStamp)));
        super.save(tagStorageOrderBean);

        tagStorageOrderDetailService.saveOutBoundOrderDetails(orderNoRms, outBoundOrderRequestDTO.getItems());

        return orderNoRms;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public String saveInventoryTagStorageOrder(String timeStamp, InventoryOrderRequestDTO inventoryOrderRequestDTO) {
        String orderNoRms = createOrderNoRms(PlatformConstant.STORAGE_ORDER_TYPE.INVENTORY_BOUND, "");
        TagStorageOrderBean tagStorageOrderBean = new TagStorageOrderBean();
        tagStorageOrderBean.setOrderNoRms(orderNoRms);
        tagStorageOrderBean.setOrderNoWms(inventoryOrderRequestDTO.getOrderNoWMS());
        tagStorageOrderBean.setOrderNoErp(inventoryOrderRequestDTO.getOrderNoERP());
        tagStorageOrderBean.setType(PlatformConstant.STORAGE_ORDER_TYPE.INVENTORY_BOUND);
        tagStorageOrderBean.setOrderType("");
        tagStorageOrderBean.setState(PlatformConstant.STORAGE_ORDER_STATUS.SEND);
        tagStorageOrderBean.setCreateUser(String.valueOf(AccountContext.getAccountId()));
        tagStorageOrderBean.setCreateDate(timeStamp);
        tagStorageOrderBean.setCreateTime(TimeUtil.localDateTimeToTimestamp(TimeUtil.parseDateFormatterString(timeStamp)));
        super.save(tagStorageOrderBean);

        tagStorageOrderDetailService.saveInventoryOrderDetails(orderNoRms, inventoryOrderRequestDTO.getItems());

        return orderNoRms;
    }


    @Override
    public boolean checkStorageOrderCancelable(String orderNoWms, Integer type) {
        LambdaQueryWrapper<TagStorageOrderBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(TagStorageOrderBean::getOrderNoWms, orderNoWms);
        queryWrapper.eq(TagStorageOrderBean::getState, PlatformConstant.STORAGE_ORDER_STATUS.SEND);
        queryWrapper.eq(TagStorageOrderBean::getType, type);
        return super.exists(queryWrapper);
    }

    @Override
    public boolean checkInventoryOrderCancelable(String orderNoWms, String orderNoRms, Integer type) {
        LambdaQueryWrapper<TagStorageOrderBean> queryWrapper = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(orderNoRms)) {
            queryWrapper.eq(TagStorageOrderBean::getOrderNoWms, orderNoWms);
        }
        if (StringUtils.isNotBlank(orderNoRms)) {
            queryWrapper.eq(TagStorageOrderBean::getOrderNoRms, orderNoRms);
        }
        queryWrapper.eq(TagStorageOrderBean::getState, PlatformConstant.STORAGE_ORDER_STATUS.SEND);
        queryWrapper.eq(TagStorageOrderBean::getType, type);
        return super.exists(queryWrapper);
    }


    @Override
    public String cancelTagStorageOrder(String timeStamp, String orderNoWms, Integer type) {
        LambdaQueryWrapper<TagStorageOrderBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(TagStorageOrderBean::getOrderNoWms, orderNoWms);
        queryWrapper.eq(TagStorageOrderBean::getState, PlatformConstant.STORAGE_ORDER_STATUS.SEND);
        queryWrapper.eq(TagStorageOrderBean::getType, type);
        TagStorageOrderBean tagStorageOrderBean = super.getOne(queryWrapper);
        if (Objects.nonNull(tagStorageOrderBean)) {
            tagStorageOrderBean.setState(PlatformConstant.STORAGE_ORDER_STATUS.CANCELED);
            tagStorageOrderBean.setUpdateDate(timeStamp);
            tagStorageOrderBean.setUpdateUser(String.valueOf(AccountContext.getAccountId()));
            tagStorageOrderBean.setUpdateTime(TimeUtil.localDateTimeToTimestamp(TimeUtil.parseDateFormatterString(timeStamp)));
            super.updateById(tagStorageOrderBean);
        }
        return tagStorageOrderBean.getOrderNoRms();
    }

    @Override
    public String cancelInventoryTagStorageOrder(String timeStamp, String orderNoWms, String orderNoRms, Integer type) {
        LambdaQueryWrapper<TagStorageOrderBean> queryWrapper = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(orderNoWms)) {
            queryWrapper.eq(TagStorageOrderBean::getOrderNoWms, orderNoWms);
        }
        if (StringUtils.isNotBlank(orderNoRms)) {
            queryWrapper.eq(TagStorageOrderBean::getOrderNoRms, orderNoRms);
        }
        queryWrapper.eq(TagStorageOrderBean::getState, PlatformConstant.STORAGE_ORDER_STATUS.SEND);
        queryWrapper.eq(TagStorageOrderBean::getType, type);
        TagStorageOrderBean tagStorageOrderBean = super.getOne(queryWrapper);
        if (Objects.nonNull(tagStorageOrderBean)) {
            tagStorageOrderBean.setState(PlatformConstant.STORAGE_ORDER_STATUS.CANCELED);
            tagStorageOrderBean.setUpdateDate(timeStamp);
            tagStorageOrderBean.setUpdateUser(String.valueOf(AccountContext.getAccountId()));
            tagStorageOrderBean.setUpdateTime(TimeUtil.localDateTimeToTimestamp(TimeUtil.parseDateFormatterString(timeStamp)));
            super.updateById(tagStorageOrderBean);
        }
        return tagStorageOrderBean.getOrderNoRms();
    }

    @Override
    public List<TagStorageOrderBean> queryActiveInBoundOrders(DevInBoundOrderQueryRequestDTO data) {
        LambdaQueryWrapper<TagStorageOrderBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(TagStorageOrderBean::getType, PlatformConstant.STORAGE_ORDER_TYPE.IN_BOUND);
        queryWrapper.eq(TagStorageOrderBean::getState, PlatformConstant.STORAGE_ORDER_STATUS.SEND)
                .or().eq(TagStorageOrderBean::getState, PlatformConstant.STORAGE_ORDER_STATUS.EXECUTING);
        if (Objects.nonNull(data)) {
            queryWrapper.like(TagStorageOrderBean::getOrderNoRms, data.getOrderID_RMS());
            queryWrapper.ge(StringUtils.isNotBlank(data.getTimeBegin()), TagStorageOrderBean::getCreateTime, TimeUtil.getDayNoLineTimestamp(data.getTimeBegin()));
            queryWrapper.le(StringUtils.isNotBlank(data.getTimeEnd()), TagStorageOrderBean::getCreateTime, TimeUtil.getDayNoLineTimestamp(data.getTimeEnd()));
        }
        return super.list(queryWrapper);
    }


    @Override
    public List<TagStorageOrderBean> queryActiveOutBoundOrders(DevOutBoundOrderQueryRequestDTO data) {
        LambdaQueryWrapper<TagStorageOrderBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(TagStorageOrderBean::getType, PlatformConstant.STORAGE_ORDER_TYPE.OUT_BOUND);
        queryWrapper.eq(TagStorageOrderBean::getState, PlatformConstant.STORAGE_ORDER_STATUS.SEND)
                .or().eq(TagStorageOrderBean::getState, PlatformConstant.STORAGE_ORDER_STATUS.EXECUTING);
        if (Objects.nonNull(data)) {
            queryWrapper.like(TagStorageOrderBean::getOrderNoRms, data.getOrderID_RMS());
            queryWrapper.ge(StringUtils.isNotBlank(data.getTimeBegin()), TagStorageOrderBean::getCreateTime, TimeUtil.getDayNoLineTimestamp(data.getTimeBegin()));
            queryWrapper.le(StringUtils.isNotBlank(data.getTimeEnd()), TagStorageOrderBean::getCreateTime, TimeUtil.getDayNoLineTimestamp(data.getTimeEnd()));
        }
        return super.list(queryWrapper);
    }


    @Override
    public List<TagStorageOrderBean> queryActiveInventoryOrders(DevInventoryOrderQueryRequestDTO data) {
        LambdaQueryWrapper<TagStorageOrderBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(TagStorageOrderBean::getType, PlatformConstant.STORAGE_ORDER_TYPE.INVENTORY_BOUND);
        queryWrapper.eq(TagStorageOrderBean::getState, PlatformConstant.STORAGE_ORDER_STATUS.SEND)
                .or().eq(TagStorageOrderBean::getState, PlatformConstant.STORAGE_ORDER_STATUS.EXECUTING);
        if (Objects.nonNull(data)) {
            queryWrapper.like(TagStorageOrderBean::getOrderNoRms, data.getOrderID_RMS());
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
        queryWrapper.eq(TagStorageOrderBean::getOrderNoRms, orderNo);
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
    public TagStorageOrderBean queryTagStorageOrderByNo(String orderNoRms) {
        LambdaQueryWrapper<TagStorageOrderBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(TagStorageOrderBean::getOrderNoRms, orderNoRms);
        return super.getOne(queryWrapper);
    }
}
