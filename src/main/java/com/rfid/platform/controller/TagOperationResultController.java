package com.rfid.platform.controller;

import com.rfid.platform.annotation.InterfaceLog;
import com.rfid.platform.common.PlatformConstant;
import com.rfid.platform.common.StorageOperationResultEnum;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.regex.Pattern;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RFID标签操作结果控制器
 * 实现RFID接口文档中定义的四个接口：入库单明细回传、出库单明细回传、盘点单账面明细查询、盘点单明细回传
 * 
 * @author RFID Platform Team
 * @version 1.0
 * @since 2024
 */
@Slf4j
@RestController
@RequestMapping("/rfid/tag/operation/result")
@Tag(name = "出入库结果管理", description = "提供RFID标签操作结果相关的API接口，包括入库、出库、盘点等操作的数据回传和查询功能")
public class TagOperationResultController {

    // 通知类型常量
    private static final int NOTICE_TYPE_INBOUND = 1;  // 入库类型
    private static final int NOTICE_TYPE_OUTBOUND = 2; // 出库类型
    private static final int NOTICE_TYPE_INVENTORY = 3; // 盘点类型

    @Autowired
    private TagStorageOperationResultService tagStorageOperationResultService;

    @Autowired
    private TagInfoService tagInfoService;

    @Autowired
    private TagStorageOperationService tagStorageOperationService;

    @Autowired
    private ParamUtil paramUtil;

    /**
     * 入库单实际明细回传接口
     * 用于接收RFID设备扫描到的入库单实际明细数据，包括EPC标签信息和SKU信息
     *
     * @param request 入库单明细请求参数，包含单据编号、上游单据编号、接收时间和明细数据
     * @return 响应结果，包含处理状态和结果信息
     */
    @PostMapping("/upInitem")
    @InterfaceLog(type = 4, description = "入库单明细回传")
    @Operation(
        summary = "入库单实际明细回传",
        description = "接收RFID设备扫描到的入库单实际明细数据，验证EPC标签和SKU信息的有效性，并更新标签状态",
        tags = {"出入库结果管理"}
    )
    public RfidApiResponseDTO<String> uploadInboundItems(
            @Parameter(description = "入库单明细请求参数", required = true, 
                      content = @Content(schema = @Schema(implementation = RfidApiRequestDTO.class)))
            @RequestBody RfidApiRequestDTO<InboundDTO.InboundParam> request) {
        RfidApiResponseDTO<String> response = RfidApiResponseDTO.success();
        try {
            // 验证基础参数
            if (!paramUtil.validateBaseParams(request, "upInitem", response)) {
                return response;
            }

            // 解析业务参数
            InboundDTO.InboundParam param = request.getParam();
            if (param == null) {
                response.setCode("310");
                response.setMessage("业务参数解析失败");
                return response;
            }

            if (CollectionUtils.isEmpty(param.getItemData())) {
                response.setCode(StorageOperationResultEnum.DETAIL_NOT_EXIST.getCode());
                response.setMessage(StorageOperationResultEnum.DETAIL_NOT_EXIST.getMessage());
                return response;
            }

            // 处理入库单明细
            processInboundItems(param, response);
            if (!response.getSuccess()) {
                return response;
            }



            log.info("入库单实际明细回传成功，单据编号：{}", param.getBillNo());

            response.setData("入库单实际明细回传成功，单据编号：" + param.getBillNo());
            return response;
        } catch (Exception e) {
            log.error("入库单实际明细回传失败", e);
            return RfidApiResponseDTO.error("系统异常：" + e.getMessage());
        }
    }

    /**
     * 出库单实际明细回传接口
     * 用于接收RFID设备扫描到的出库单实际明细数据，验证出库标签的有效性
     *
     * @param request 出库单明细请求参数，包含单据编号、上游单据编号、接收时间和明细数据
     * @return 响应结果，包含处理状态和结果信息
     */
    @PostMapping("/upOutitem")
    @InterfaceLog(type = 6, description = "出库单明细回传")
    @Operation(
        summary = "出库单实际明细回传",
        description = "接收RFID设备扫描到的出库单实际明细数据，验证出库标签的有效性并更新标签状态为已出库",
        tags = {"出入库结果管理"}
    )
    public RfidApiResponseDTO<String> uploadOutboundItems(
            @Parameter(description = "出库单明细请求参数", required = true,
                      content = @Content(schema = @Schema(implementation = RfidApiRequestDTO.class)))
            @RequestBody RfidApiRequestDTO<OutboundDTO.OutboundParam> request) {
        RfidApiResponseDTO<String> response = RfidApiResponseDTO.success();
        try {
            // 验证基础参数
            if (!paramUtil.validateBaseParams(request, "upOutitem", response)) {
                return RfidApiResponseDTO.error("参数验证失败");
            }

            // 解析业务参数
            OutboundDTO.OutboundParam param = request.getParam();
            if (param == null) {
                response.setCode("310");
                response.setMessage("业务参数解析失败");
                return response;
            }

            if (CollectionUtils.isEmpty(param.getItemData())) {
                response.setCode(StorageOperationResultEnum.DETAIL_NOT_EXIST.getCode());
                response.setMessage(StorageOperationResultEnum.DETAIL_NOT_EXIST.getMessage());
                return response;
            }

            // 处理出库单明细
            processOutboundItems(param);

            log.info("出库单实际明细回传成功，单据编号：{}", param.getBillNo());

            response.setData("出库单实际明细回传成功，单据编号：" + param.getBillNo());
            return response;
        } catch (Exception e) {
            log.error("出库单实际明细回传失败", e);
            return RfidApiResponseDTO.error("系统异常：" + e.getMessage());
        }
    }

    /**
     * 查询盘点单账面明细接口
     * 根据盘点单号查询系统中的账面库存明细，为盘点作业提供基础数据
     *
     * @param request 盘点单查询请求参数，包含盘点单号等查询条件
     * @return 盘点单账面明细列表，包含SKU编码和账面数量信息
     */
    @PostMapping("/getInvbook")
    @InterfaceLog(type = 7, description = "盘点明细查询")
    @Operation(
        summary = "查询盘点单账面明细",
        description = "根据盘点单号查询系统中的账面库存明细，返回SKU编码和对应的账面数量，为盘点作业提供基础数据",
        tags = {"出入库结果管理"}
    )
    public RfidApiResponseDTO<List<InventoryDTO.InventoryBookItem>> getInventoryBook(
            @Parameter(description = "盘点单查询请求参数", required = true,
                      content = @Content(schema = @Schema(implementation = RfidApiRequestDTO.class)))
            @RequestBody RfidApiRequestDTO<InventoryDTO.InventoryQueryParam> request) {
        RfidApiResponseDTO<List<InventoryDTO.InventoryBookItem>> response = RfidApiResponseDTO.success();
        try {
            // 验证基础参数
            if (!paramUtil.validateBaseParams(request, "getInvbook", response)) {
                return RfidApiResponseDTO.error("参数验证失败");
            }

            // 解析业务参数
            InventoryDTO.InventoryQueryParam param = request.getParam();
            if (param == null) {
                response.setCode("310");
                response.setMessage("业务参数解析失败");
                return response;
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
     * 盘点单实际明细回传接口
     * 用于接收RFID设备盘点扫描到的实际明细数据，记录盘点结果
     *
     * @param request 盘点单明细请求参数，包含盘点单号、实际扫描到的EPC标签和SKU信息
     * @return 响应结果，包含处理状态和结果信息
     */
    @PostMapping("/upInvitem")
    @InterfaceLog(type = 8, description = "盘点明细回传")
    @Operation(
        summary = "盘点单实际明细回传",
        description = "接收RFID设备盘点扫描到的实际明细数据，记录盘点结果，用于后续的盘点差异分析",
        tags = {"出入库结果管理"}
    )
    public RfidApiResponseDTO<String> uploadInventoryItems(
            @Parameter(description = "盘点单明细请求参数", required = true,
                      content = @Content(schema = @Schema(implementation = RfidApiRequestDTO.class)))
            @RequestBody RfidApiRequestDTO<InventoryDTO.InventoryParam> request) {
        RfidApiResponseDTO<String> response = RfidApiResponseDTO.success();
        try {
            // 验证基础参数
            if (!paramUtil.validateBaseParams(request, "upInvitem", response)) {
                return RfidApiResponseDTO.error("参数验证失败");
            }

            // 解析业务参数
            InventoryDTO.InventoryParam param = request.getParam();
            if (param == null) {
                response.setCode("310");
                response.setMessage("业务参数解析失败");
                return response;
            }

            // 参数校验
            if (CollectionUtils.isNotEmpty(param.getItemData())) {
                response.setSuccess(false);
                response.setCode(StorageOperationResultEnum.DETAIL_NOT_EXIST.getCode());
                response.setMessage(StorageOperationResultEnum.DETAIL_NOT_EXIST.getMessage());
                return response;
            }

            // 处理盘点单明细
            processInventoryItems(param);

            if (!response.getSuccess()) {
                return response;
            }

            log.info("盘点单实际明细回传成功，单据编号：{}", param.getBillNo());
            response.setData("盘点单实际明细回传成功，单据编号：" + param.getBillNo());
            return response;
        } catch (Exception e) {
            log.error("盘点单实际明细回传失败", e);
            return RfidApiResponseDTO.error("系统异常：" + e.getMessage());
        }
    }



    
    /**
     * 处理入库单明细数据
     * 解析入库单明细数据，验证EPC标签和SKU的有效性，并更新标签状态
     * 
     * @param param 入库单参数对象，包含入库明细数据
     * @throws Exception 当数据验证失败或处理过程中出现异常时抛出
     */
    @Transactional(rollbackFor = Exception.class)
    public void processInboundItems(InboundDTO.InboundParam param, RfidApiResponseDTO<String> response) throws Exception {

        LocalDateTime receiptTime = TimeUtil.parseDateFormatterString(param.getReceiptTime());
        String noticeNo = param.getUpstreamBillNo();
        List<TagStorageOperationBean> tagStorageOperationBeans = tagStorageOperationService.listTagStorageOperationSkuByNotice(noticeNo);
        List<String> skuCodes = tagStorageOperationBeans.stream().map(TagStorageOperationBean::getSkuCode).collect(Collectors.toUnmodifiableList());
        
        // 构建入库明细数据
        List<TagStorageOperationResultBean> resultBeans = buildInboundResultBeans(param, receiptTime);

        // 按skuCode分组
        Map<String, List<TagStorageOperationResultBean>> groupedBySkuCode = resultBeans.stream()
                .collect(Collectors.groupingBy(TagStorageOperationResultBean::getSkuCode));

        List<TagInfoBean> validatedTagInfoBeans = new ArrayList<>();

        // 遍历groupedBySkuCode进行校验
        for (Map.Entry<String, List<TagStorageOperationResultBean>> entry : groupedBySkuCode.entrySet()) {
            String skuCode = entry.getKey();
            // 第一步，先校验key是不是包含在skuCodes里，不包含response的success设置成false,code和message使用SKU_NOT_CONTAINED_BY_IN_NOTICE，同时结束方法
            if (!skuCodes.contains(skuCode)) {
                response.setSuccess(false);
                response.setCode(StorageOperationResultEnum.SKU_NOT_CONTAINED_BY_IN_NOTICE.getCode());
                response.setMessage(StorageOperationResultEnum.SKU_NOT_CONTAINED_BY_IN_NOTICE.getMessage());
                return;
            }

            List<TagStorageOperationResultBean> tagStorageOperationResultBeans = entry.getValue();
            // 第二步， 判断tagStorageOperationResultBeans中的epcCode是不是全部都在数据库中，不是response的success设置成fals；使用正则表达式校验不包含的epcCode,全部符合规则使用EPC_CODE_MATCHED_RULE，否则使用EPC_CODE_NOT_MATCHED_RULE
            
            // 提取当前SKU下的所有epcCode
            Set<String> requestEpcCodes = tagStorageOperationResultBeans.stream()
                    .map(TagStorageOperationResultBean::getEpcCode)
                    .collect(Collectors.toSet());
            
            // 通过requestEpcCodes统计tagInfo表的总数，如果总数和requestEpcCodes的总数不一致
            Integer epcSize = requestEpcCodes.size();
            List<TagInfoBean> tagInfoBeans = tagInfoService.listTagInfoByEpcCodes(requestEpcCodes);

            if (epcSize.intValue() != tagInfoBeans.size()) {
                // 从数据库查询结果中提取EPC编码集合
                Set<String> dbEpcCodes = tagInfoBeans.stream()
                        .map(TagInfoBean::getEpcCode)
                        .collect(Collectors.toSet());
                
                // EPC编码格式验证正则表达式：支持两种格式
                // 1. 3000开头的28位数字格式
                // 2. E开头的32位十六进制格式
                String epcPattern = "^(3000[0-9]{24}|E[0-9A-F]{31})$";
                Pattern pattern = Pattern.compile(epcPattern);

                // 过滤出requestEpcCodes中不在tagInfoBeans中的
                Set<String> filteredEpcCodes = requestEpcCodes.stream()
                        .filter(epc -> !dbEpcCodes.contains(epc))
                        .collect(Collectors.toSet());

                // 检查所有缺失的EPC编码是否都符合格式规则
                boolean allMatched = filteredEpcCodes.stream()
                        .allMatch(epc -> pattern.matcher(epc).matches());

                response.setSuccess(false);
                if (allMatched) {
                    // 全部符合规则使用EPC_CODE_MATCHED_RULE
                    response.setCode(StorageOperationResultEnum.EPC_CODE_MATCHED_RULE.getCode());
                    response.setMessage(StorageOperationResultEnum.EPC_CODE_MATCHED_RULE.getMessage());
                } else {
                    // 否则使用EPC_CODE_NOT_MATCHED_RULE
                    response.setCode(StorageOperationResultEnum.EPC_CODE_NOT_MATCHED_RULE.getCode());
                    response.setMessage(StorageOperationResultEnum.EPC_CODE_NOT_MATCHED_RULE.getMessage());
                }
                return;
            }

            // 第3步，根据tagInfoBeans和tagStorageOperationResultBeans， 判断tagStorageOperationResultBeans中的所有绑定关系都正确；不正确使用SKU_EPC_BIND_NOT_MATCH
            // 所有epc的state是1，storageState是1；否则使用EPC_STATE_ABNORMAL
            
            // 创建tagInfoBeans的映射，便于快速查找
            Map<String, TagInfoBean> tagInfoMap = tagInfoBeans.stream()
                    .collect(Collectors.toMap(TagInfoBean::getEpcCode, bean -> bean));
            
            // 检查SKU和EPC的绑定关系
            for (TagStorageOperationResultBean resultBean : tagStorageOperationResultBeans) {
                TagInfoBean tagInfo = tagInfoMap.get(resultBean.getEpcCode());
                if (tagInfo != null) {
                    // 检查SKU绑定关系
                    if (!Objects.equals(tagInfo.getSkuCode(), resultBean.getSkuCode())) {
                        response.setSuccess(false);
                        response.setCode(StorageOperationResultEnum.SKU_EPC_BIND_NOT_MATCH.getCode());
                        response.setMessage(StorageOperationResultEnum.SKU_EPC_BIND_NOT_MATCH.getMessage());
                        return;
                    }
                    
                    // 检查EPC状态
                    if (!Objects.equals(tagInfo.getState(), 1) || !Objects.equals(tagInfo.getStorageState(), 1)) {
                        response.setSuccess(false);
                        response.setCode(StorageOperationResultEnum.EPC_STATE_ABNORMAL.getCode());
                        response.setMessage(StorageOperationResultEnum.EPC_STATE_ABNORMAL.getMessage());
                        return;
                    }
                }
            }
            
            // 生成校验成功的TagInfoBeans，即tagInfoBeans中和tagStorageOperationResultBeans中有交集的记录
            validatedTagInfoBeans = tagStorageOperationResultBeans.stream()
                    .map(resultBean -> {
                        TagInfoBean tagInfo = tagInfoMap.get(resultBean.getEpcCode());
                        // 要求skuCode和epcCode都一样
                        if (tagInfo != null && Objects.equals(tagInfo.getSkuCode(), resultBean.getSkuCode())) {
                            tagInfo.setStorageState(2);
                            return tagInfo;
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        }

        if (!response.getSuccess()) {
            return;
        }

        // 明细数据添加
        tagStorageOperationResultService.saveTagStorageOperationResults(resultBeans);

        // 更新存储操作状态为部分完成
        tagStorageOperationService.updateTagStorageOperationPartiallyByNoticeNo(param.getUpstreamBillNo(), PlatformConstant.STORAGE_TASK_STATE.PARTIAL);

        // 校验成功，更新tag_info表中记录状态为2
        tagInfoService.updateTagInfos(validatedTagInfoBeans);

        
        // 检查所有SKU的数量是否一致
        boolean allMatched = true;
        List<String> skuCodeUpdates = new ArrayList<>();
        for (TagStorageOperationBean tagStorageOperationBean : tagStorageOperationBeans) {
            String skuCode = tagStorageOperationBean.getSkuCode();
            int expectedQuantity = tagStorageOperationBean.getNoticeQuantity();
            int count = tagInfoService.countTagInfosBySkuAndStorageState(skuCode, 2).intValue();
            allMatched = expectedQuantity == count;
            skuCodeUpdates.add(skuCode);
        }
        
        // 如果全部一样，更新所有相关TagInfo的storageState为3
        if (allMatched) {
            tagInfoService.updateTagInfoStorageStateBySkuCodes(skuCodeUpdates, 2, 3);
            // TODO 发送给WMS入库成功
        }

    }
    
    /**
     * 构建入库明细数据
     * 
     * @param param 入库单参数
     * @param receiptTime 接收时间
     * @return 入库明细数据列表
     */
    private List<TagStorageOperationResultBean> buildInboundResultBeans(InboundDTO.InboundParam param, LocalDateTime receiptTime) {
        List<TagStorageOperationResultBean> resultBeans = new ArrayList<>();
        
        for (InboundDTO.InboundItem item : param.getItemData()) {
            if (CollectionUtils.isNotEmpty(item.getMsitemData())) {
                for (InboundDTO.InboundMsItem msItem : item.getMsitemData()) {
                    TagStorageOperationResultBean entity = new TagStorageOperationResultBean();
                    entity.setEpcCode(msItem.getEpc());
                    entity.setSkuCode(msItem.getSkuCode());
                    entity.setBillNo(param.getBillNo());
                    entity.setNoticeNo(param.getUpstreamBillNo());
                    entity.setNoticeType(NOTICE_TYPE_INBOUND);
                    entity.setNoticeTime(receiptTime);
                    resultBeans.add(entity);
                }
            }
        }
        
        return resultBeans;
     }
     
    /**
     * 构建出库明细数据
     * 
     * @param param 出库单参数
     * @param receiptTime 接收时间
     * @return 出库明细数据列表
     */
    private List<TagStorageOperationResultBean> buildOutboundResultBeans(OutboundDTO.OutboundParam param, LocalDateTime receiptTime) {
        List<TagStorageOperationResultBean> resultBeans = new ArrayList<>();
        
        for (OutboundDTO.OutboundItem item : param.getItemData()) {
            if (CollectionUtils.isNotEmpty(item.getMsitemData())) {
                for (OutboundDTO.OutboundMsItem msItem : item.getMsitemData()) {
                    TagStorageOperationResultBean entity = new TagStorageOperationResultBean();
                    entity.setEpcCode(msItem.getEpc());
                    entity.setSkuCode(msItem.getSkuCode());
                    entity.setBillNo(param.getBillNo());
                    entity.setNoticeNo(param.getUpstreamBillNo());
                    entity.setNoticeType(NOTICE_TYPE_OUTBOUND);
                    entity.setNoticeTime(receiptTime);
                    resultBeans.add(entity);
                }
            }
        }
        
        return resultBeans;
    }

    /**
     * 处理出库单明细数据
     * 解析出库单明细数据，验证出库标签的有效性，并更新标签状态为已出库
     * 
     * @param param 出库单参数对象，包含出库明细数据
     * @throws Exception 当数据验证失败或处理过程中出现异常时抛出
     */
    private void processOutboundItems(OutboundDTO.OutboundParam param) throws Exception {

        LocalDateTime receiptTime = TimeUtil.parseDateFormatterString(param.getReceiptTime());
        
        // 构建出库明细数据
        List<TagStorageOperationResultBean> resultBeans = buildOutboundResultBeans(param, receiptTime);
        
        try {

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
     * 查询盘点单账面明细数据
     * 根据盘点查询参数获取系统中的账面库存明细信息
     * 
     * @param param 盘点查询参数对象
     * @return 盘点账面明细列表
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
     * 处理盘点单明细数据
     * 解析盘点明细数据，记录实际盘点结果到数据库
     * 
     * @param param 盘点单参数对象，包含盘点明细数据
     */
    private void processInventoryItems(InventoryDTO.InventoryParam param) {
        // 参数校验
        if (param == null || param.getItemData() == null || param.getItemData().isEmpty()) {
            log.warn("盘点单明细数据为空，跳过处理");
            return;
        }

        LocalDateTime receiptTime = TimeUtil.parseDateFormatterString(param.getReceiptTime());

        for (InventoryDTO.InventoryItem item : param.getItemData()) {
            if (CollectionUtils.isNotEmpty(item.getMsitemData())) {
                for (InventoryDTO.InventoryMsItem msItem : item.getMsitemData()) {
                    TagStorageOperationResultBean entity = new TagStorageOperationResultBean();
                    entity.setEpcCode(msItem.getEpc());
                    entity.setSkuCode(msItem.getSkuCode());
                    entity.setBillNo(param.getBillNo());
                    entity.setNoticeNo(param.getUpstreamBillNo());
                    entity.setNoticeType(NOTICE_TYPE_INVENTORY);
                    entity.setNoticeTime(receiptTime);

                    tagStorageOperationResultService.saveTagStorageOperationResult(entity);
                }
            }
        }

        tagStorageOperationService.updateTagStorageOperationPartiallyByNoticeNo(param.getUpstreamBillNo(), PlatformConstant.STORAGE_TASK_STATE.PARTIAL);
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
            Integer expectedQuantity = operation.getNoticeQuantity();
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
            Integer expectedQuantity = operation.getNoticeQuantity();
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
