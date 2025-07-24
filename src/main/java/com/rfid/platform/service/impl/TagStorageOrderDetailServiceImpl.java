package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.rfid.platform.config.PlatformRestProperties;
import com.rfid.platform.entity.TagStorageOrderDetailBean;
import com.rfid.platform.mapper.TagStorageOrderDetailMapper;
import com.rfid.platform.persistence.RfidApiRequestDTO;
import com.rfid.platform.persistence.RfidApiResponseDTO;
import com.rfid.platform.persistence.storage.InBoundOrderItemRequestDTO;
import com.rfid.platform.persistence.storage.InventoryOrderItemRequestDTO;
import com.rfid.platform.persistence.storage.OutBoundOrderItemRequestDTO;
import com.rfid.platform.persistence.storage.Rms2ErpSkuDetailReqDTO;
import com.rfid.platform.persistence.storage.Rms2ErpSkuDetailRespDTO;
import com.rfid.platform.service.TagRestService;
import com.rfid.platform.service.TagStorageOrderDetailService;
import com.rfid.platform.util.TimeUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class TagStorageOrderDetailServiceImpl extends ServiceImpl<TagStorageOrderDetailMapper, TagStorageOrderDetailBean> implements TagStorageOrderDetailService {

    @Autowired
    private PlatformRestProperties platformRestProperties;


    @Autowired
    @Lazy
    private TagRestService tagRestService;



    @Override
    public boolean saveInBoundOrderDetails(String orderNoRms, List<InBoundOrderItemRequestDTO> items) {
        if (CollectionUtils.isEmpty(items)) {
            return false;
        }
        // 遍历items， 在遍历item中的boxCodes，按找一个boxCode生成一个TagStorageOrderDetailBean，返回最终的集合
        List<TagStorageOrderDetailBean> detailBeans = new ArrayList<>();
        
        for (InBoundOrderItemRequestDTO item : items) {
            TagStorageOrderDetailBean detailBean = getInBoundTagStorageOrderDetailBean(orderNoRms, item);
            detailBeans.add(detailBean);
        }
        
        // 批量保存到数据库
        return super.saveBatch(detailBeans, 50);
    }

    private TagStorageOrderDetailBean getInBoundTagStorageOrderDetailBean(String orderNoRms, InBoundOrderItemRequestDTO item) {
        TagStorageOrderDetailBean detailBean = new TagStorageOrderDetailBean();
        detailBean.setOrderNoRms(orderNoRms);
        detailBean.setSku(item.getSku());
        detailBean.setQuantity(item.getQty());
        detailBean.setBinLocation(item.getBinLocation());
        // 去erp查productCode
        RfidApiRequestDTO<Rms2ErpSkuDetailReqDTO> requestDTO = new RfidApiRequestDTO<>();
        requestDTO.setTimeStamp(TimeUtil.getDateFormatterString(TimeUtil.getSysDate()));
        requestDTO.setVersion(platformRestProperties.getVersion());
        RfidApiResponseDTO<Rms2ErpSkuDetailRespDTO> responseDTO = tagRestService.executeRestPostOptions(
                platformRestProperties.getGetSkuDetailUrl(),
                requestDTO,
                new TypeReference<RfidApiResponseDTO<Rms2ErpSkuDetailRespDTO>>() {}
        );
        if (Objects.nonNull(responseDTO) && Objects.nonNull(responseDTO.getData())) {
            String productCode = responseDTO.getData().getProductCode();
            detailBean.setProductCode(productCode);
        }
        return detailBean;
    }

    @Override
    public boolean saveOutBoundOrderDetails(String orderNoRms, List<OutBoundOrderItemRequestDTO> items) {
        if (CollectionUtils.isEmpty(items)) {
            return false;
        }
        // 遍历items， 在遍历item中的boxCodes，按找一个boxCode生成一个TagStorageOrderDetailBean，返回最终的集合
        List<TagStorageOrderDetailBean> detailBeans = new ArrayList<>();

        for (OutBoundOrderItemRequestDTO item : items) {
            TagStorageOrderDetailBean detailBean = getOutBoundTagStorageOrderDetailBean(orderNoRms, item);
            detailBeans.add(detailBean);
        }

        // 批量保存到数据库
        return super.saveBatch(detailBeans, 50);
    }

    private TagStorageOrderDetailBean getOutBoundTagStorageOrderDetailBean(String orderNoRms, OutBoundOrderItemRequestDTO item) {
        TagStorageOrderDetailBean detailBean = new TagStorageOrderDetailBean();
        detailBean.setOrderNoRms(orderNoRms);
        detailBean.setProductCode(item.getProductCode());
        detailBean.setSku(item.getSku());
        detailBean.setBinLocation(item.getBinLocation());
        detailBean.setQuantity(item.getQty());
        if (StringUtils.isNotBlank(item.getSku()) && StringUtils.isBlank(item.getProductCode())) {
            // 去erp查productCode
            RfidApiRequestDTO<Rms2ErpSkuDetailReqDTO> requestDTO = new RfidApiRequestDTO<>();
            requestDTO.setTimeStamp(TimeUtil.getDateFormatterString(TimeUtil.getSysDate()));
            requestDTO.setVersion(platformRestProperties.getVersion());
            RfidApiResponseDTO<Rms2ErpSkuDetailRespDTO> responseDTO = tagRestService.executeRestPostOptions(
                    platformRestProperties.getGetSkuDetailUrl(),
                    requestDTO,
                    new TypeReference<RfidApiResponseDTO<Rms2ErpSkuDetailRespDTO>>() {}
            );
            if (Objects.nonNull(responseDTO) && Objects.nonNull(responseDTO.getData())) {
                String productCode = responseDTO.getData().getProductCode();
                detailBean.setProductCode(productCode);
            }
        }
        return detailBean;
    }


    @Override
    public boolean saveInventoryOrderDetails(String orderNoRms, List<InventoryOrderItemRequestDTO> items) {
        if (CollectionUtils.isEmpty(items)) {
            return false;
        }
        // 遍历items， 在遍历item中的boxCodes，按找一个boxCode生成一个TagStorageOrderDetailBean，返回最终的集合
        List<TagStorageOrderDetailBean> detailBeans = new ArrayList<>();

        for (InventoryOrderItemRequestDTO item : items) {
            TagStorageOrderDetailBean detailBean = getInventoryTagStorageOrderDetailBean(orderNoRms, item);
            detailBeans.add(detailBean);
        }

        // 批量保存到数据库
        return super.saveBatch(detailBeans, 50);
    }

    private TagStorageOrderDetailBean getInventoryTagStorageOrderDetailBean(String orderNoRms, InventoryOrderItemRequestDTO item) {
        TagStorageOrderDetailBean detailBean = new TagStorageOrderDetailBean();
        detailBean.setOrderNoRms(orderNoRms);
        detailBean.setProductCode(item.getProductCode());
        detailBean.setSku(item.getSku());
        detailBean.setQuantity(item.getQty());
        detailBean.setBinLocation(item.getBinLocation());
        detailBean.setBoxCnt(item.getBoxCnt());
        if (StringUtils.isNotBlank(item.getSku()) && StringUtils.isBlank(item.getProductCode())) {
            // 去erp查productCode
            RfidApiRequestDTO<Rms2ErpSkuDetailReqDTO> requestDTO = new RfidApiRequestDTO<>();
            requestDTO.setTimeStamp(TimeUtil.getDateFormatterString(TimeUtil.getSysDate()));
            requestDTO.setVersion(platformRestProperties.getVersion());
            RfidApiResponseDTO<Rms2ErpSkuDetailRespDTO> responseDTO = tagRestService.executeRestPostOptions(
                    platformRestProperties.getGetSkuDetailUrl(),
                    requestDTO,
                    new TypeReference<RfidApiResponseDTO<Rms2ErpSkuDetailRespDTO>>() {}
            );
            if (Objects.nonNull(responseDTO) && Objects.nonNull(responseDTO.getData())) {
                String productCode = responseDTO.getData().getProductCode();
                detailBean.setProductCode(productCode);
            }
        }
        return detailBean;
    }

    @Override
    public List<TagStorageOrderDetailBean> listTagStorageOrderDetails(String orderNoRms) {
        LambdaQueryWrapper<TagStorageOrderDetailBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(TagStorageOrderDetailBean::getOrderNoRms, orderNoRms);
        return super.list(queryWrapper);
    }


    @Override
    public boolean productCodeExistInOrderNo(String orderNoRms, String productCode) {
        LambdaQueryWrapper<TagStorageOrderDetailBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(TagStorageOrderDetailBean::getOrderNoRms, orderNoRms);
        queryWrapper.eq(TagStorageOrderDetailBean::getProductCode, productCode);
        return super.exists(queryWrapper);
    }

    @Override
    public TagStorageOrderDetailBean getSkuByOrderNoRmsAndProductCode(String orderNoRms, String productCode) {
        LambdaQueryWrapper<TagStorageOrderDetailBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(TagStorageOrderDetailBean::getOrderNoRms, orderNoRms);
        queryWrapper.eq(TagStorageOrderDetailBean::getProductCode, productCode);
        Page<TagStorageOrderDetailBean> page = new Page(1, 1, false);
        super.page(page, queryWrapper);
        if (CollectionUtils.isNotEmpty(page.getRecords())) {
            TagStorageOrderDetailBean tagStorageOrderDetailBean = page.getRecords().get(0);
            return tagStorageOrderDetailBean;
        }
        return null;
    }

    @Override
    public Integer getQuantityFromTagStorageOrderDetails(String orderNoRms, String productCode) {
        LambdaQueryWrapper<TagStorageOrderDetailBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(TagStorageOrderDetailBean::getOrderNoRms, orderNoRms);
        queryWrapper.eq(TagStorageOrderDetailBean::getProductCode, productCode);
        TagStorageOrderDetailBean tagStorageOrderDetailBean = super.getOne(queryWrapper);
        return Objects.nonNull(tagStorageOrderDetailBean) ? tagStorageOrderDetailBean.getQuantity() : 0;
    }


    @Override
    public List<TagStorageOrderDetailBean> listTagStorageOrderDetailsByNoAndProductCode(String orderNoRms, String productCode) {
        LambdaQueryWrapper<TagStorageOrderDetailBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(TagStorageOrderDetailBean::getOrderNoRms, orderNoRms);
        queryWrapper.eq(TagStorageOrderDetailBean::getProductCode, productCode);
        return super.list(queryWrapper);
    }
}
