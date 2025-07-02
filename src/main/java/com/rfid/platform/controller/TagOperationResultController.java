package com.rfid.platform.controller;

import com.rfid.platform.annotation.InterfaceLog;
import com.rfid.platform.entity.TagStorageOperationResultBean;
import com.rfid.platform.persistence.InboundDTO;
import com.rfid.platform.persistence.InventoryDTO;
import com.rfid.platform.persistence.OutboundDTO;
import com.rfid.platform.persistence.RfidApiRequestDTO;
import com.rfid.platform.persistence.RfidApiResponseDTO;
import com.rfid.platform.service.TagStorageOperationResultService;
import com.rfid.platform.service.TagInfoService;
import com.rfid.platform.service.TagStorageOperationService;
import com.rfid.platform.entity.TagInfoBean;
import com.rfid.platform.entity.TagStorageOperationBean;
import com.rfid.platform.util.ParamUtil;
import com.rfid.platform.util.TimeUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;


import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RFID标签操作结果控制器
 * 实现RFID接口文档中定义的四个接口
 */
@Slf4j
@RestController
@RequestMapping("/rfid/tag/operation/result")
public class TagOperationResultController {

    @Autowired
    private TagStorageOperationResultService tagStorageOperationResultService;

    @Autowired
    private TagInfoService tagInfoService;

    @Autowired
    private TagStorageOperationService tagStorageOperationService;

    @Autowired
    private ParamUtil paramUtil;

    /**
     * 入库单实际明细回传
     *
     * @param request 请求参数
     * @return 响应结果
     */
    @PostMapping("/upInitem")
    @InterfaceLog(type = 4, description = "入库单明细回传")
    public RfidApiResponseDTO<String> uploadInboundItems(@RequestBody RfidApiRequestDTO request) {
        try {
            // 验证基础参数
            if (!paramUtil.validateBaseParams(request, "upInitem")) {
                return RfidApiResponseDTO.error("参数验证失败");
            }

            // 解析业务参数
            InboundDTO.InboundParam param = paramUtil.parseParam(request.getParam(), InboundDTO.InboundParam.class);
            if (param == null) {
                return RfidApiResponseDTO.error("业务参数解析失败");
            }

            // 处理入库单明细
            processInboundItems(param);

            log.info("入库单实际明细回传成功，单据编号：{}", param.getBillNo());
            RfidApiResponseDTO<String> response = RfidApiResponseDTO.success();
            response.setData("入库单实际明细回传成功，单据编号：" + param.getBillNo());
            return response;
        } catch (Exception e) {
            log.error("入库单实际明细回传失败", e);
            return RfidApiResponseDTO.error("系统异常：" + e.getMessage());
        }
    }

    /**
     * 出库单实际明细回传
     *
     * @param request 请求参数
     * @return 响应结果
     */
    @PostMapping("/upOutitem")
    @InterfaceLog(type = 6, description = "出库单明细回传")
    public RfidApiResponseDTO<String> uploadOutboundItems(@RequestBody RfidApiRequestDTO request) {
        try {
            // 验证基础参数
            if (!paramUtil.validateBaseParams(request, "upOutitem")) {
                return RfidApiResponseDTO.error("参数验证失败");
            }

            // 解析业务参数
            OutboundDTO.OutboundParam param = paramUtil.parseParam(request.getParam(), OutboundDTO.OutboundParam.class);
            if (param == null) {
                return RfidApiResponseDTO.error("业务参数解析失败");
            }

            // 处理出库单明细
            processOutboundItems(param);

            log.info("出库单实际明细回传成功，单据编号：{}", param.getBillNo());
            RfidApiResponseDTO<String> response = RfidApiResponseDTO.success();
            response.setData("出库单实际明细回传成功，单据编号：" + param.getBillNo());
            return response;
        } catch (Exception e) {
            log.error("出库单实际明细回传失败", e);
            return RfidApiResponseDTO.error("系统异常：" + e.getMessage());
        }
    }

    /**
     * 查询盘点单账面明细
     *
     * @param request 请求参数
     * @return 响应结果
     */
    @PostMapping("/getInvbook")
    @InterfaceLog(type = 7, description = "盘点明细查询")
    public RfidApiResponseDTO<List<InventoryDTO.InventoryBookItem>> getInventoryBook(@RequestBody RfidApiRequestDTO request) {
        try {
            // 验证基础参数
            if (!paramUtil.validateBaseParams(request, "getInvbook")) {
                return RfidApiResponseDTO.error("参数验证失败");
            }

            // 解析业务参数
            InventoryDTO.InventoryQueryParam param = paramUtil.parseParam(request.getParam(),
                    InventoryDTO.InventoryQueryParam.class);
            if (param == null) {
                return RfidApiResponseDTO.error("业务参数解析失败");
            }

            // 查询盘点单账面明细
            List<InventoryDTO.InventoryBookItem> items = queryInventoryBookItems(param);

            log.info("查询盘点单账面明细成功，单据编号：{}，返回记录数：{}", param.getBillNo(), items.size());
            return RfidApiResponseDTO.success(items, items.size());
        } catch (Exception e) {
            log.error("查询盘点单账面明细失败", e);
            return RfidApiResponseDTO.error("系统异常：" + e.getMessage());
        }
    }

    /**
     * 盘点单实际明细回传
     *
     * @param request 请求参数
     * @return 响应结果
     */
    @PostMapping("/upInvitem")
    @InterfaceLog(type = 8, description = "盘点明细回传")
    public RfidApiResponseDTO<String> uploadInventoryItems(@RequestBody RfidApiRequestDTO request) {
        try {
            // 验证基础参数
            if (!paramUtil.validateBaseParams(request, "upInvitem")) {
                return RfidApiResponseDTO.error("参数验证失败");
            }

            // 解析业务参数
            InventoryDTO.InventoryParam param = paramUtil.parseParam(request.getParam(),
                    InventoryDTO.InventoryParam.class);
            if (param == null) {
                return RfidApiResponseDTO.error("业务参数解析失败");
            }

            // 处理盘点单明细
            processInventoryItems(param);

            log.info("盘点单实际明细回传成功，单据编号：{}", param.getBillNo());
            RfidApiResponseDTO response = RfidApiResponseDTO.success();
            response.setData("盘点单实际明细回传成功，单据编号：" + param.getBillNo());
            return response;
        } catch (Exception e) {
            log.error("盘点单实际明细回传失败", e);
            return RfidApiResponseDTO.error("系统异常：" + e.getMessage());
        }
    }


    /**
     * 处理入库单明细
     */
    private void processInboundItems(InboundDTO.InboundParam param) throws Exception {
        if (param.getItemData() == null || param.getItemData().isEmpty()) {
            return;
        }

        LocalDateTime receiptTime = TimeUtil.parseDateFormatterString(param.getReceiptTime());

        List<TagStorageOperationResultBean> resultBeans = new ArrayList<>();
        for (InboundDTO.InboundItem item : param.getItemData()) {
            if (CollectionUtils.isNotEmpty(item.getMsitemData())) {
                for (InboundDTO.InboundMsItem msItem : item.getMsitemData()) {
                    TagStorageOperationResultBean entity = new TagStorageOperationResultBean();
                    entity.setEpcCode(msItem.getEpc());
                    entity.setSkuCode(msItem.getSkuCode());
                    entity.setBillNo(param.getBillNo());
                    entity.setNoticeNo(param.getUpstreamBillNo());
                    entity.setNoticeType(1); // 入库类型
                    entity.setNoticeTime(receiptTime);
                    resultBeans.add(entity);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(resultBeans)) {
            tagStorageOperationResultService.saveTagStorageOperationResults(resultBeans);
        }

        // 校验skuCode和epcCode是否都在tag_info表中, 根据noticeNo获取tagStoreOperation表中的quantity和总数是不是一样
        List<TagInfoBean> validatedTagInfoBeans = validateInTagInfoAndQuantity(param.getUpstreamBillNo(), resultBeans);

        // 校验成功，更新tag_info表中记录状态为2
        tagInfoService.updateTagInfos(validatedTagInfoBeans);
    }

    /**
     * 处理出库单明细
     */
    private void processOutboundItems(OutboundDTO.OutboundParam param) throws Exception {
        if (param.getItemData() == null || param.getItemData().isEmpty()) {
            return;
        }

        LocalDateTime receiptTime = TimeUtil.parseDateFormatterString(param.getReceiptTime());
        List<TagStorageOperationResultBean> resultBeans = new ArrayList<>();
        try {
            for (OutboundDTO.OutboundItem item : param.getItemData()) {
                if (CollectionUtils.isNotEmpty(item.getMsitemData())) {
                    for (OutboundDTO.OutboundMsItem msItem : item.getMsitemData()) {
                        TagStorageOperationResultBean entity = new TagStorageOperationResultBean();
                        entity.setEpcCode(msItem.getEpc());
                        entity.setSkuCode(msItem.getSkuCode());
                        entity.setBillNo(param.getBillNo());
                        entity.setNoticeNo(param.getUpstreamBillNo());
                        entity.setNoticeType(2); // 出库类型
                        entity.setNoticeTime(receiptTime);
                        resultBeans.add(entity);
                    }
                }
            }

            // 提取resultBeans中的skuCode和epcCode到两个集合中
            Set<String> skuCodes = resultBeans.stream()
                    .map(TagStorageOperationResultBean::getSkuCode)
                    .collect(Collectors.toSet());
            
            Set<String> epcCodes = resultBeans.stream()
                    .map(TagStorageOperationResultBean::getEpcCode)
                    .collect(Collectors.toSet());

            //  根据noticeNo获取tagStoreOperation表中的quantity和总数是不是一样, 校验skuCode和epcCode是否都在tag_info表中,
            List<TagInfoBean> validatedTagInfoBeans = validateOutTagInfoAndQuantity(param.getUpstreamBillNo(),
                    skuCodes, epcCodes);

            // 校验成功，更新tag_info表中记录状态为2
            tagInfoService.updateTagInfos(validatedTagInfoBeans);
        } catch (Exception e) {
            if (CollectionUtils.isNotEmpty(resultBeans)) {
                tagStorageOperationResultService.saveTagStorageOperationResults(resultBeans);
            }
            throw new Exception(e);
        }

    }

    /**
     * 查询盘点单账面明细
     */
    private List<InventoryDTO.InventoryBookItem> queryInventoryBookItems(InventoryDTO.InventoryQueryParam param) {
        // 这里应该根据实际业务逻辑查询数据库
        // 暂时返回模拟数据
        List<InventoryDTO.InventoryBookItem> items = new ArrayList<>();

        // 模拟数据
        InventoryDTO.InventoryBookItem item1 = new InventoryDTO.InventoryBookItem();
        item1.setSkuCode("SKU001");
        item1.setQty(10);
        items.add(item1);

        InventoryDTO.InventoryBookItem item2 = new InventoryDTO.InventoryBookItem();
        item2.setSkuCode("SKU002");
        item2.setQty(20);
        items.add(item2);

        return items;
    }

    /**
     * 处理盘点单明细
     */
    private void processInventoryItems(InventoryDTO.InventoryParam param) {
        if (param.getItemData() == null || param.getItemData().isEmpty()) {
            return;
        }

        LocalDateTime receiptTime = TimeUtil.parseDateFormatterString(param.getReceiptTime());

        for (InventoryDTO.InventoryItem item : param.getItemData()) {
            if (item.getMsitemData() != null) {
                for (InventoryDTO.InventoryMsItem msItem : item.getMsitemData()) {
                    TagStorageOperationResultBean entity = new TagStorageOperationResultBean();
                    entity.setEpcCode(msItem.getEpc());
                    entity.setSkuCode(msItem.getSkuCode());
                    entity.setBillNo(param.getBillNo());
                    entity.setNoticeNo(param.getUpstreamBillNo());
                    entity.setNoticeType(3); // 盘点类型
                    entity.setNoticeTime(receiptTime);

                    tagStorageOperationResultService.saveTagStorageOperationResult(entity);
                }
            }
        }
    }

    /**
     * 校验skuCode和epcCode是否都在tag_info表中，根据noticeNo获取tagStoreOperation表中的quantity和总数进行比较
     *
     * @param noticeNo 通知单号
     * @param resultBeans 操作结果数据
     * @return 校验成功的TagInfoBean集合
     */
    private List<TagInfoBean> validateInTagInfoAndQuantity(String noticeNo, List<TagStorageOperationResultBean> resultBeans) throws Exception {
        // 1. 根据noticeNo获取tagStoreOperation表中的quantity和实际总数进行比较
        LambdaQueryWrapper<TagStorageOperationBean> operationQuery = new LambdaQueryWrapper<>();
        operationQuery.eq(TagStorageOperationBean::getNoticeNo, noticeNo);

        List<TagStorageOperationBean> operationList = tagStorageOperationService.listTagStorageOperation(operationQuery);
        TagStorageOperationBean operation = CollectionUtils.isNotEmpty(operationList) ? operationList.get(0) : null;
        if (operation != null) {
            Long expectedQuantity = operation.getNoticeQuantity();
            int actualQuantity = resultBeans.size();

            if (expectedQuantity != null && !expectedQuantity.equals((long) actualQuantity)) {
                log.warn("入库数量不匹配: noticeNo={}, 预期数量={}, 实际数量={}",
                        noticeNo, expectedQuantity, actualQuantity);
                // 可以根据业务需求决定是否抛出异常或记录日志
                throw new Exception("入库数量不匹配: noticeNo="+noticeNo+", 预期数量="+expectedQuantity+", 实际数量=" + actualQuantity);
            } else {
                log.info("入库数量校验通过: noticeNo={}, 数量={}", noticeNo, actualQuantity);
            }
        } else {
            log.warn("入库未找到对应的存储操作记录: noticeNo={}", noticeNo);
        }


        // 用于收集所有校验成功的TagInfoBean
        List<TagInfoBean> validatedTagInfoBeans = new ArrayList<>();

        // 按skuCode分组
        Map<String, List<TagStorageOperationResultBean>> groupedBySkuCode = resultBeans.stream()
                .collect(Collectors.groupingBy(TagStorageOperationResultBean::getSkuCode));

        // 1. 校验skuCode和epcCode是否都在tag_info表中
        for (Map.Entry<String, List<TagStorageOperationResultBean>> entry : groupedBySkuCode.entrySet()) {
            String skuCode = entry.getKey();
            List<TagStorageOperationResultBean> skuBeans = entry.getValue();
            
            // 根据skuCode查询tag_info表中所有的epcCode
            LambdaQueryWrapper<TagInfoBean> tagInfoQuery = new LambdaQueryWrapper<>();
            tagInfoQuery.eq(TagInfoBean::getSkuCode, skuCode);
            
            List<TagInfoBean> tagInfoList = tagInfoService.listTagInfo(tagInfoQuery);
            Set<String> dbEpcCodes = tagInfoList.stream()
                    .map(TagInfoBean::getEpcCode)
                    .collect(Collectors.toSet());
            
            // 获取当前SKU下的所有epcCode
            Set<String> requestEpcCodes = skuBeans.stream()
                    .map(TagStorageOperationResultBean::getEpcCode)
                    .collect(Collectors.toSet());
            
            // 检查数据库中的epcCode集合是否完全包含请求中的epcCode集合
            if (!dbEpcCodes.containsAll(requestEpcCodes)) {
                Set<String> missingEpcCodes = requestEpcCodes.stream()
                        .filter(epc -> !dbEpcCodes.contains(epc))
                        .collect(Collectors.toSet());
                log.warn("入库SKU: {} 中存在未注册的EPC标签: {}", skuCode, missingEpcCodes);
                // 可以根据业务需求决定是否抛出异常或记录日志
                throw new Exception("SKU: "+skuCode+" 中存在未注册的EPC标签: " + missingEpcCodes);
            } else {
                log.info("入库SKU: {} 的所有EPC标签校验通过", skuCode);
                // 将校验成功的TagInfoBean添加到结果集合中，同时设置state为2
                List<TagInfoBean> matchedTagInfoBeans = tagInfoList.stream()
                        .filter(tagInfo -> requestEpcCodes.contains(tagInfo.getEpcCode()))
                        .peek(tagInfo -> tagInfo.setState(2))
                        .collect(Collectors.toList());
                validatedTagInfoBeans.addAll(matchedTagInfoBeans);
            }
        }

        return validatedTagInfoBeans;
    }


    /**
     * 校验skuCode和epcCode是否都在tag_info表中，根据noticeNo获取tagStoreOperation表中的quantity和总数进行比较
     *
     * @param noticeNo 通知单号
     * @return 校验成功的TagInfoBean集合
     */
    private List<TagInfoBean> validateOutTagInfoAndQuantity(String noticeNo, Set<String> skuCodes, Set<String> epcCodes) throws Exception {
        // 1. 根据noticeNo获取tagStoreOperation表中的quantity和实际总数进行比较
        LambdaQueryWrapper<TagStorageOperationBean> operationQuery = new LambdaQueryWrapper<>();
        operationQuery.eq(TagStorageOperationBean::getNoticeNo, noticeNo);

        List<TagStorageOperationBean> operationList = tagStorageOperationService.listTagStorageOperation(operationQuery);
        TagStorageOperationBean operation = CollectionUtils.isNotEmpty(operationList) ? operationList.get(0) : null;
        if (operation != null) {
            Long expectedQuantity = operation.getNoticeQuantity();
            int actualQuantity = epcCodes.size();

            if (expectedQuantity != null && !expectedQuantity.equals((long) actualQuantity)) {
                log.warn("出库数量不匹配: noticeNo={}, 预期数量={}, 实际数量={}",
                        noticeNo, expectedQuantity, actualQuantity);
                // 可以根据业务需求决定是否抛出异常或记录日志
                throw new Exception("出库数量不匹配: noticeNo="+noticeNo+", 预期数量="+expectedQuantity+", 实际数量=" + actualQuantity);
            } else {
                log.info("出库数量校验通过: noticeNo={}, 数量={}", noticeNo, actualQuantity);
            }
        } else {
            log.warn("出库未找到对应的存储操作记录: noticeNo={}", noticeNo);
        }


        // 用于收集所有校验成功的TagInfoBean
        List<TagInfoBean> validatedTagInfoBeans = new ArrayList<>();

        // 创建epcCodes的副本用于跟踪剩余的EPC
        List<String> remainingEpcCodes = new ArrayList<>(epcCodes);
        
        // 遍历skuCodes，获取每个skuCode包含的epcCode集合
        for (String skuCode : skuCodes) {
            // 根据skuCode查询tag_info表中所有的epcCode
            LambdaQueryWrapper<TagInfoBean> tagInfoQuery = new LambdaQueryWrapper<>();
            tagInfoQuery.eq(TagInfoBean::getSkuCode, skuCode);
            
            List<TagInfoBean> tagInfoList = tagInfoService.listTagInfo(tagInfoQuery);

            // 比较集合和epcCodes中相同的部分
            List<TagInfoBean> matchedTagInfoBeans = tagInfoList.stream()
                    .filter(tagInfo -> remainingEpcCodes.contains(tagInfo.getEpcCode()))
                    .peek(tagInfo -> {
                        tagInfo.setState(3); // 将state改成3
                        remainingEpcCodes.remove(tagInfo.getEpcCode()); // 从剩余列表中移除
                    })
                    .collect(Collectors.toList());
            
            // 相同的添加到validatedTagInfoBeans中
            validatedTagInfoBeans.addAll(matchedTagInfoBeans);
        }
        
        // 遍历完成后如果epcCodes中还有值，抛出异常
        if (!remainingEpcCodes.isEmpty()) {
            throw new Exception("EPC标签 " + remainingEpcCodes + " 未与SKU匹配");
        }

        return validatedTagInfoBeans;
    }



}
