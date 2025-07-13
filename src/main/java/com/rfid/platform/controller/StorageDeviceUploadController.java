package com.rfid.platform.controller;

import com.rfid.platform.config.RfidPlatformProperties;
import com.rfid.platform.entity.TagStorageOrderDetailBean;
import com.rfid.platform.entity.TagInfoBean;
import com.rfid.platform.entity.TagStorageOrderResultBean;
import com.rfid.platform.persistence.RfidApiRequestDTO;
import com.rfid.platform.persistence.RfidApiResponseDTO;
import com.rfid.platform.persistence.storage.StorageDeviceUploadRequestDTO;
import com.rfid.platform.persistence.storage.StorageDeviceUploadResponseDTO;
import com.rfid.platform.service.TagStorageOrderService;
import com.rfid.platform.service.TagStorageOrderDetailService;
import com.rfid.platform.service.TagStorageOrderResultService;
import com.rfid.platform.service.TagInfoService;
import com.rfid.platform.util.TimeUtil;
import com.rfid.platform.common.AccountContext;
import com.rfid.platform.common.PlatformConstant;
import java.time.LocalDateTime;

import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Tag(name = "设备通知单结果上传", description = "提供给设备入库、出库、盘点通知单结果上传功能")
@RestController
@RequestMapping(value = "/rfid/dev")
public class StorageDeviceUploadController {

    @Autowired
    private TagStorageOrderService tagStorageOrderService;

    @Autowired
    private TagStorageOrderDetailService tagStorageOrderDetailService;

    @Autowired
    private TagStorageOrderResultService tagStorageOrderResultService;

    @Autowired
    private TagInfoService tagInfoService;

    @Autowired
    private RfidPlatformProperties rfidPlatformProperties;


    @Operation(summary = "上传入库通知单扫描结果", description = "设备扫描EPC后上传入库结果")
    @PostMapping(value = "/uploadinbound")
    @Transactional(rollbackFor = Exception.class)
    public RfidApiResponseDTO<StorageDeviceUploadResponseDTO> uploadInbound(
            @ApiParam(value = "入库通知单扫描上传请求", required = true)
            @RequestBody RfidApiRequestDTO<StorageDeviceUploadRequestDTO> requestDTO) {
        RfidApiResponseDTO<StorageDeviceUploadResponseDTO> response = RfidApiResponseDTO.success();
        // 参数验证
        if (Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getData())) {
            response.setStatus(false);
            response.setMessage("扫描结果不能为空");
            return response;
        }

        StorageDeviceUploadRequestDTO storageDeviceUploadRequestDTO = requestDTO.getData();
        if (StringUtils.isBlank(storageDeviceUploadRequestDTO.getOrderNo())) {
            response.setStatus(false);
            response.setMessage("通知单号不能为空");
            return response;
        }

        if (StringUtils.isBlank(storageDeviceUploadRequestDTO.getProductCode())) {
            response.setStatus(false);
            response.setMessage("款式码不能为空");
            return response;
        }

        if (CollectionUtils.isEmpty(storageDeviceUploadRequestDTO.getEpcs())) {
            response.setStatus(false);
            response.setMessage("EPC不能为空");
            return response;
        }

        String orderNo = storageDeviceUploadRequestDTO.getOrderNo();
        String productCode = storageDeviceUploadRequestDTO.getProductCode();
        Integer rfidCnt = storageDeviceUploadRequestDTO.getRfidCnt();
        boolean oprtEnable = storageDeviceUploadRequestDTO.getOprtEnd();

        // 1. 根据orderNo查出tagStorageOrderDetail中所有的productCode，过滤重复的值
        List<String> distinctProductCodes = tagStorageOrderDetailService.listDistinctProductCodes(orderNo);
        if (!distinctProductCodes.contains(productCode)) {
            response.setStatus(false);
            response.setMessage("款式码与通知单中的款式码不一致");
            return response;
        }

        // 2. 根据orderNo和productCode查询tagStorageOrder中的quantity
        List<TagStorageOrderDetailBean> orderDetails = tagStorageOrderDetailService.listTagStorageOrderDetails(orderNo);
        Integer quantity = 0;
        for (TagStorageOrderDetailBean detail : orderDetails) {
            if (productCode.equals(detail.getProductCode())) {
                quantity = detail.getQuantity();
                break;
            }
        }

        // 根据orderNo和productCode查询已有的tagStorageOrderResult中的数量existQuantity
        int existQuantity = tagStorageOrderResultService.countCompletedByOrderNoAndProductCode(orderNo, productCode);

        // rfidCnt+existQuantity > quantity, 返回"盘点数量大于入库通知单数量"
        if (rfidCnt + existQuantity > quantity) {
            response.setStatus(false);
            response.setMessage("盘点数量大于入库通知单数量");
            return response;
        }

        // oprtEnable是true，并且rfidCnt+existQuantity < quantity, 返回 "盘点数量小于入库通知单数量"
        if (oprtEnable && rfidCnt + existQuantity < quantity) {
            response.setStatus(false);
            response.setMessage("盘点数量小于入库通知单数量");
            return response;
        }

        // 3. 判断EPC在不在数据库
        List<String> epcs = storageDeviceUploadRequestDTO.getEpcs();
        // 查询tag_info表中存在的EPC记录
        Set<String> epcSet = new HashSet<>(epcs);
        List<TagInfoBean> existingTagInfos = tagInfoService.listTagInfoByEpcCodes(epcSet);

        // 提取已存在的EPC码
        Set<String> existingEpcs = existingTagInfos.stream()
                .map(TagInfoBean::getEpc)
                .collect(Collectors.toSet());

        // 找出不在tag_info表中的EPC码
        List<String> invalidEpcs = epcs.stream()
                .filter(epc -> !existingEpcs.contains(epc))
                .collect(Collectors.toList());

        // 如果有无效的EPC码，返回错误信息
        if (CollectionUtils.isNotEmpty(invalidEpcs)) {
            // 根据rfidPlatformProperties中的epcPattern正则表达式，匹配所有invalidEpcs
            // 如果全部符合，返回不在数据库中的EPC符合规则; 否则返回不在数据库中的EPC不符合规则
            String epcPattern = rfidPlatformProperties.getEpcPattern();
            boolean allMatch = true;

            if (StringUtils.isNotBlank(epcPattern)) {
                // 检查所有无效EPC是否都符合正则表达式
                allMatch = invalidEpcs.stream()
                        .allMatch(epc -> epc.matches(epcPattern));
            }

            response.setStatus(false);
            if (allMatch) {
                response.setMessage("不在数据库中的EPC符合规则");
            } else {
                response.setMessage("不在数据库中的EPC不符合规则");
            }
            return response;
        }


        // 4. 数据库比对，productCode和epc绑定关系是不是全部正确，不正确返回 "款式码和EPC的绑定关系不正确"
        // 检查每个EPC对应的productCode是否与传入的productCode一致
        boolean bindingCorrect = existingTagInfos.stream()
                .allMatch(tagInfo -> productCode.equals(tagInfo.getProductCode()));

        if (!bindingCorrect) {
            response.setStatus(false);
            response.setMessage("款式码和EPC的绑定关系不正确");
            return response;
        }

        // 5.EPC状态校验，如果有不是1的，返回 "EPC状态不正确"
        boolean stateCorrect = existingTagInfos.stream()
                .allMatch(tagInfo -> tagInfo.getState() != null && tagInfo.getState() == 1);

        if (!stateCorrect) {
            response.setStatus(false);
            response.setMessage("EPC状态不正确");
            return response;
        }

        // 6. EPC库存状态校验，如果storageState有不是1的，返回 "EPC库存状态不正确"
        boolean storageStateCorrect = existingTagInfos.stream()
                .allMatch(tagInfo -> tagInfo.getStorageState() != null && tagInfo.getStorageState() == 1);

        if (!storageStateCorrect) {
            response.setStatus(false);
            response.setMessage("EPC库存状态不正确");
            return response;
        }

        // 将EPC的数据保存到表tag_storage_order_result表
        LocalDateTime now = TimeUtil.getSysDate();
        String createDate = TimeUtil.getDateFormatterString(now);
        Long createTime = TimeUtil.localDateTimeToTimestamp(now);
        String createUser = String.valueOf(AccountContext.getAccountId());

        List<TagStorageOrderResultBean> resultBeans = new ArrayList<>();
        for (String epc : epcs) {
            TagStorageOrderResultBean resultBean = new TagStorageOrderResultBean();
            resultBean.setOrderNo(orderNo);
            resultBean.setProductCode(productCode);
            resultBean.setEpc(epc);
            resultBean.setCreateDate(createDate);
            resultBean.setCreateUser(createUser);
            resultBean.setCreateTime(createTime);
            resultBeans.add(resultBean);
        }

        // 批量保存到数据库
        tagStorageOrderResultService.saveStorageOrderResults(resultBeans);

        // oprtEnable是false, 更新tag_storage_order表中对应orderNo和productCode记录的state为2; oprtEnable是true,更新tag_storage_order表中对应orderNo和productCode记录的state为3
        // oprtEnable是true，更新epc在tag_info表中记录的storageStatus为3；

        // 更新订单状态
        if (oprtEnable) {
            // 操作结束，更新订单状态为完成(3)
            tagStorageOrderService.updateOrderStateByOrderNo(orderNo, requestDTO.getTimeStamp(), PlatformConstant.STORAGE_ORDER_STATUS.COMPLETED);

            // 更新EPC的存储状态为3
            tagInfoService.updateTagInfoStorageStateByEpcs(epcs, 3);
        } else {
            // 操作未结束，更新订单状态为执行中(2)
            tagStorageOrderService.updateOrderStateByOrderNo(orderNo, requestDTO.getTimeStamp(), PlatformConstant.STORAGE_ORDER_STATUS.EXECUTING);
        }
        
        return response;
    }

}
