package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rfid.platform.entity.TagStorageOrderDetailBean;
import com.rfid.platform.mapper.TagStorageOrderDetailMapper;
import com.rfid.platform.persistence.storage.StorageOrderItemRequestDTO;
import com.rfid.platform.service.TagStorageOrderDetailService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

@Service
public class TagStorageOrderDetailServiceImpl extends ServiceImpl<TagStorageOrderDetailMapper, TagStorageOrderDetailBean> implements TagStorageOrderDetailService {


    @Override
    public boolean saveStorageOrderDetails(String orderNo, List<StorageOrderItemRequestDTO> items) {
        if (CollectionUtils.isEmpty(items)) {
            return false;
        }
        // 遍历items， 在遍历item中的boxCodes，按找一个boxCode生成一个TagStorageOrderDetailBean，返回最终的集合
        List<TagStorageOrderDetailBean> detailBeans = new ArrayList<>();
        
        for (StorageOrderItemRequestDTO item : items) {
            TagStorageOrderDetailBean detailBean = getTagStorageOrderDetailBean(orderNo, item);
            detailBeans.add(detailBean);
        }
        
        // 批量保存到数据库
        return super.saveBatch(detailBeans, 50);
    }

    private TagStorageOrderDetailBean getTagStorageOrderDetailBean(String orderNo, StorageOrderItemRequestDTO item) {
        TagStorageOrderDetailBean detailBean = new TagStorageOrderDetailBean();
        detailBean.setOrderNo(orderNo);
        detailBean.setProductCode(item.getProductCode());
        detailBean.setProductName(item.getProductName());
        detailBean.setProductSize(item.getProductSize());
        detailBean.setProductColor(item.getProductColor());
        detailBean.setSku(item.getSku());
        detailBean.setQuantity(item.getQuantity());
        detailBean.setBoxCnt(item.getBoxCnt());
        detailBean.setBoxCodes(String.join(",", item.getBoxCodes()));
        return detailBean;
    }

    @Override
    public List<TagStorageOrderDetailBean> listTagStorageOrderDetails(String orderNo) {
        LambdaQueryWrapper<TagStorageOrderDetailBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(TagStorageOrderDetailBean::getOrderNo, orderNo);
        return super.list(queryWrapper);
    }
    
    @Override
    public List<String> listDistinctProductCodes(String orderNo) {
        LambdaQueryWrapper<TagStorageOrderDetailBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(TagStorageOrderDetailBean::getOrderNo, orderNo);
        queryWrapper.select(TagStorageOrderDetailBean::getProductCode);
        
        List<TagStorageOrderDetailBean> details = super.list(queryWrapper);
        
        // 过滤重复的productCode
        return details.stream()
                .map(TagStorageOrderDetailBean::getProductCode)
                .distinct()
                .collect(Collectors.toList());
    }
}
