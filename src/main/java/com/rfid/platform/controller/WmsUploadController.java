package com.rfid.platform.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.rfid.platform.common.AccountContext;
import com.rfid.platform.common.BaseResult;
import com.rfid.platform.common.PlatformConstant;
import com.rfid.platform.config.PlatformRestProperties;
import com.rfid.platform.entity.AccountBean;
import com.rfid.platform.entity.TagStorageOrderBean;
import com.rfid.platform.entity.TagStorageOrderDetailBean;
import com.rfid.platform.entity.TagStorageOrderResultBean;
import com.rfid.platform.persistence.BoundUploadDTO;
import com.rfid.platform.persistence.RfidApiResponseDTO;
import com.rfid.platform.persistence.storage.StorageWmsUploadDTO;
import com.rfid.platform.persistence.storage.StorageWmsUploadDetailDTO;
import com.rfid.platform.service.AccountService;
import com.rfid.platform.service.TagRestService;
import com.rfid.platform.service.TagStorageOrderDetailService;
import com.rfid.platform.service.TagStorageOrderResultService;
import com.rfid.platform.service.TagStorageOrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "WMS上传操作", description = "将通知单结果推送到WMS")
@RestController
@RequestMapping(value = "/rfid")
public class WmsUploadController {

    @Autowired
    private TagStorageOrderService tagStorageOrderService;

    @Autowired
    private TagStorageOrderDetailService tagStorageOrderDetailService;

    @Autowired
    private TagStorageOrderResultService tagStorageOrderResultService;

    @Autowired
    private TagRestService tagRestService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PlatformRestProperties platformRestProperties;



    @PostMapping(value = "/bound/upload")
    public BaseResult<Boolean> boundUpload(@RequestBody BoundUploadDTO boundUploadDTO){
        BaseResult<Boolean> baseResult = new BaseResult<>();
        String orderNo = boundUploadDTO.getOrderNo();
        if (StringUtils.isBlank(orderNo)) {
            baseResult.setCode(PlatformConstant.RET_CODE.FAILED);
            baseResult.setMessage("通知单号不存在");
            return baseResult;
        }

        TagStorageOrderBean tagStorageOrderBean = tagStorageOrderService.queryTagStorageOrderByNo(orderNo);
        if (Objects.isNull(tagStorageOrderBean)) {
            baseResult.setCode(PlatformConstant.RET_CODE.FAILED);
            baseResult.setMessage("通知单不存在");
            return baseResult;
        }

        Integer orderState = tagStorageOrderBean.getState();
        if (!PlatformConstant.STORAGE_ORDER_STATUS.COMPLETED.equals(orderState)) {
            baseResult.setCode(PlatformConstant.RET_CODE.FAILED);
            baseResult.setMessage("通知单状态不正确");
            return baseResult;
        }

        String version = platformRestProperties.getVersion();
        String url = getString(tagStorageOrderBean);

        if (StringUtils.isBlank(url)) {
            baseResult.setCode(PlatformConstant.RET_CODE.FAILED);
            baseResult.setMessage("上传地址未配置");
            return baseResult;
        }

        StorageWmsUploadDTO storageWmsUploadDTO = createStorageWmsUpload(tagStorageOrderBean);
        JSONObject reqObject = JSONObject.parseObject(JSON.toJSONString(storageWmsUploadDTO));
        JSONObject respObject = tagRestService.executeRestPostOptions(version, url, reqObject);
        RfidApiResponseDTO<JSONObject> responseDTO = respObject.toJavaObject(RfidApiResponseDTO.class);

        if (responseDTO.getStatus()) {
            baseResult.setMessage("上传WMS成功");
            baseResult.setData(true);
        } else {
            baseResult.setCode(PlatformConstant.RET_CODE.FAILED);
            baseResult.setMessage(responseDTO.getMessage());
        }
        return baseResult;
    }

    private StorageWmsUploadDTO createStorageWmsUpload(TagStorageOrderBean tagStorageOrderBean) {
        String orderNo = tagStorageOrderBean.getOrderNo();
        StorageWmsUploadDTO storageWmsUploadDTO = new StorageWmsUploadDTO();
        storageWmsUploadDTO.setOrderNo(orderNo);

        AccountBean accountBean = accountService.getAccountByPk(AccountContext.getAccountId());
        storageWmsUploadDTO.setUserNo(accountBean.getCode());

        List<TagStorageOrderResultBean> storageOrderResultBeans =
                tagStorageOrderResultService.listTagStorageOrderResults(orderNo);
        if (CollectionUtils.isNotEmpty(storageOrderResultBeans)) {
            // 按boxCode分组
            Map<String, List<TagStorageOrderResultBean>> groupedByBoxCode = storageOrderResultBeans.stream()
                    .collect(Collectors.groupingBy(TagStorageOrderResultBean::getBoxCode));
            
            List<StorageWmsUploadDetailDTO> storageWmsUploadDetails = new ArrayList<>();
            
            // 遍历groupedByBoxCode
            for (Map.Entry<String, List<TagStorageOrderResultBean>> boxEntry : groupedByBoxCode.entrySet()) {
                String boxCode = boxEntry.getKey();
                List<TagStorageOrderResultBean> boxResults = boxEntry.getValue();
                
                // 根据productCode分组
                Map<String, List<TagStorageOrderResultBean>> groupedByProductCode = boxResults.stream()
                        .collect(Collectors.groupingBy(TagStorageOrderResultBean::getProductCode));
                
                // 遍历productCode分组
                for (Map.Entry<String, List<TagStorageOrderResultBean>> productEntry : groupedByProductCode.entrySet()) {
                    String productCode = productEntry.getKey();
                    List<TagStorageOrderResultBean> productResults = productEntry.getValue();
                    
                    StorageWmsUploadDetailDTO detailDTO = new StorageWmsUploadDetailDTO();
                    detailDTO.setBoxCode(boxCode);
                    detailDTO.setProductCode(productCode);
                    
                    // 设置sku
                    if (CollectionUtils.isNotEmpty(productResults)) {
                        List<TagStorageOrderDetailBean> tagStorageOrderDetailBeans =
                                tagStorageOrderDetailService.listTagStorageOrderDetailsByNoAndProductCode(orderNo,
                                productCode);
                        if (CollectionUtils.isNotEmpty(tagStorageOrderDetailBeans)) {
                            detailDTO.setSku(tagStorageOrderDetailBeans.get(0).getSku());
                        }
                    }
                    
                    // 收集所有rfid
                    List<String> rfids = productResults.stream()
                            .map(TagStorageOrderResultBean::getEpc)
                            .collect(Collectors.toList());
                    detailDTO.setRfids(rfids);
                    
                    storageWmsUploadDetails.add(detailDTO);
                }
            }
            
            storageWmsUploadDTO.setBoxDetails(storageWmsUploadDetails);
            storageWmsUploadDTO.setQuantity(storageOrderResultBeans.size());
            storageWmsUploadDTO.setBoxCnt(groupedByBoxCode.size());
            storageWmsUploadDTO.setLvNo("");
        }

        return storageWmsUploadDTO;
    }

    private String getString(TagStorageOrderBean tagStorageOrderBean) {
        String url = "";
        Integer type = tagStorageOrderBean.getType();
        if (PlatformConstant.STORAGE_ORDER_TYPE.IN_BOUND.equals(type)) {
            url = platformRestProperties.getInBoundUploadUrl();
        }
        if (PlatformConstant.STORAGE_ORDER_TYPE.OUT_BOUND.equals(type)) {
            url = platformRestProperties.getOutBoundUploadUrl();
        }
        if (PlatformConstant.STORAGE_ORDER_TYPE.INVENTORY_BOUND.equals(type)) {
            url = platformRestProperties.getInventoryUploadUrl();
        }
        return url;
    }


}
