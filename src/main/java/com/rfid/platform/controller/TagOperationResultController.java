package com.rfid.platform.controller;

import com.rfid.platform.annotation.InterfaceLog;
import com.rfid.platform.entity.TagStorageOperationResultBean;
import com.rfid.platform.persistence.InboundDTO;
import com.rfid.platform.persistence.InventoryDTO;
import com.rfid.platform.persistence.OutboundDTO;
import com.rfid.platform.persistence.RfidApiRequestDTO;
import com.rfid.platform.persistence.RfidApiResponseDTO;
import com.rfid.platform.service.TagStorageOperationResultService;
import com.rfid.platform.util.ParamUtil;
import com.rfid.platform.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private ParamUtil paramUtil;

    /**
     * 入库单实际明细回传
     *
     * @param request 请求参数
     * @return 响应结果
     */
    @PostMapping("/upInitem")
    @InterfaceLog(type = 4, description = "入库单明细回传")
    public RfidApiResponseDTO<Void> uploadInboundItems(@RequestBody RfidApiRequestDTO request) {
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
            return RfidApiResponseDTO.success();
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
    public RfidApiResponseDTO<Void> uploadOutboundItems(@RequestBody RfidApiRequestDTO request) {
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
            return RfidApiResponseDTO.success();
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
    public RfidApiResponseDTO<Void> uploadInventoryItems(@RequestBody RfidApiRequestDTO request) {
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
            return RfidApiResponseDTO.success();
        } catch (Exception e) {
            log.error("盘点单实际明细回传失败", e);
            return RfidApiResponseDTO.error("系统异常：" + e.getMessage());
        }
    }


    /**
     * 处理入库单明细
     */
    private void processInboundItems(InboundDTO.InboundParam param) {
        if (param.getItemData() == null || param.getItemData().isEmpty()) {
            return;
        }

        LocalDateTime receiptTime = TimeUtil.parseDateFormatterString(param.getReceiptTime());

        for (InboundDTO.InboundItem item : param.getItemData()) {
            if (item.getMsitemData() != null) {
                for (InboundDTO.InboundMsItem msItem : item.getMsitemData()) {
                    TagStorageOperationResultBean entity = new TagStorageOperationResultBean();
                    entity.setEpcCode(msItem.getEpc());
                    entity.setSkuCode(msItem.getSkuCode());
                    entity.setBillNo(param.getBillNo());
                    entity.setNoticeNo(param.getUpstreamBillNo());
                    entity.setNoticeType(1); // 入库类型
                    entity.setNoticeTime(receiptTime);

                    tagStorageOperationResultService.saveTagStorageOperationResult(entity);
                }
            }
        }
    }

    /**
     * 处理出库单明细
     */
    private void processOutboundItems(OutboundDTO.OutboundParam param) {
        if (param.getItemData() == null || param.getItemData().isEmpty()) {
            return;
        }

        LocalDateTime receiptTime = TimeUtil.parseDateFormatterString(param.getReceiptTime());

        for (OutboundDTO.OutboundItem item : param.getItemData()) {
            if (item.getMsitemData() != null) {
                for (OutboundDTO.OutboundMsItem msItem : item.getMsitemData()) {
                    TagStorageOperationResultBean entity = new TagStorageOperationResultBean();
                    entity.setEpcCode(msItem.getEpc());
                    entity.setSkuCode(msItem.getSkuCode());
                    entity.setBillNo(param.getBillNo());
                    entity.setNoticeNo(param.getUpstreamBillNo());
                    entity.setNoticeType(2); // 出库类型
                    entity.setNoticeTime(receiptTime);

                    tagStorageOperationResultService.saveTagStorageOperationResult(entity);
                }
            }
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


}
