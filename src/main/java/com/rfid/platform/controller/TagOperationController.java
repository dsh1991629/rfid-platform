package com.rfid.platform.controller;

import com.rfid.platform.annotation.InterfaceLog;
import com.rfid.platform.common.ExecNoContext;
import com.rfid.platform.common.PlatformConstant;
import com.rfid.platform.entity.TagImportInfoBean;
import com.rfid.platform.entity.TagInfoBean;
import com.rfid.platform.entity.TagStorageOperationBean;
import com.rfid.platform.persistence.RfidApiRequestDTO;
import com.rfid.platform.persistence.RfidApiResponseDTO;
import com.rfid.platform.persistence.StorageOperationDTO;
import com.rfid.platform.persistence.TagImportExcelDTO;
import com.rfid.platform.service.TagImportInfoService;
import com.rfid.platform.service.TagInfoService;
import com.rfid.platform.service.TagStorageOperationService;
import com.rfid.platform.util.ParamUtil;
import com.rfid.platform.util.TimeUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * RFID标签操作控制器
 * 提供标签推送、入库通知、出库通知等功能
 */
@Slf4j
@RestController
@RequestMapping(value = "/rfid/tag/operation")
@Tag(name = "标签操作管理", description = "RFID标签相关操作接口，包括标签推送、入库出库通知等功能")
public class TagOperationController {

    @Autowired
    private TagInfoService tagInfoService;

    @Autowired
    private TagImportInfoService tagImportInfoService;

    @Autowired
    private TagStorageOperationService tagStorageOperationService;

    @Autowired
    private ParamUtil paramUtil;

    /**
     * 标签推送接口
     * 接收外部系统推送的标签信息，保存到系统中
     * 
     * @param request 包含标签信息的请求对象
     * @return 推送结果
     */
    @PostMapping(value = "/push")
    @InterfaceLog(type = 2, description = "标签推送")
    @Operation(summary = "标签推送", description = "接收外部系统推送的RFID标签信息，包括EPC编码和SKU编码等")
    public RfidApiResponseDTO<Boolean> tagPush(
            @Parameter(description = "标签推送请求参数，包含EPC编码和SKU编码", required = true)
            @RequestBody RfidApiRequestDTO request){
        RfidApiResponseDTO<Boolean> baseResult = new RfidApiResponseDTO<>();

        // 验证基础参数
        if (!paramUtil.validateBaseParams(request, "push", baseResult)) {
            return baseResult;
        }

        // 解析业务参数
        TagImportExcelDTO param = paramUtil.parseParam(request.getParam(), TagImportExcelDTO.class);
        if (param == null) {
            baseResult.setCode("310");
            baseResult.setMessage("业务参数解析失败");
            return baseResult;
        }

        String execNo = ExecNoContext.getExecNo();

        // 创建导入详细记录
        TagImportInfoBean importInfo = new TagImportInfoBean();
        importInfo.setEcpCode(param.getEpcCode());
        importInfo.setSkuCode(param.getSkuCode());
        importInfo.setImportType(1); // 1表示接口推送
        importInfo.setExecNo(execNo);
        importInfo.setImportTime(LocalDateTime.now());

        try {
            // 创建TagInfoBean对象
            TagInfoBean tagInfo = new TagInfoBean();
            tagInfo.setSkuCode(param.getSkuCode());
            tagInfo.setEpcCode(param.getEpcCode());
            tagInfo.setState(1); // 默认状态为1
            tagInfo.setInTime(LocalDateTime.now());
            // 调用tagInfoService保存
            boolean saved = tagInfoService.saveTagInfo(tagInfo);
            if (saved) {
                importInfo.setImportResult("S");
            } else {
                importInfo.setImportResult("F");
            }
            baseResult.setData(saved);
        } catch (Exception e) {
            importInfo.setImportResult("F");
            baseResult.setCode("400");
            baseResult.setMessage("接口保存失败：" + e.getMessage());
        }

        // 保存导入详细记录
        try {
            tagImportInfoService.saveTagImportInfo(importInfo);
        } catch (Exception e) {
            log.error("[{}] 保存接口记录失败: {}", execNo, e.getMessage());
        }

        return baseResult;
    }

    /**
     * 入库通知接口
     * 接收到库通知信息，记录入库操作
     * 
     * @param request 包含入库信息的请求对象
     * @return 入库通知处理结果
     */
    @PostMapping(value = "/storage/in")
    @InterfaceLog(type = 3, description = "入库通知")
    @Operation(summary = "入库通知", description = "接收货物入库通知，记录入库操作信息并通知硬件设备")
    public RfidApiResponseDTO<Boolean> storageIn(
            @Parameter(description = "入库通知请求参数，包含单据号和数量信息", required = true)
            @RequestBody RfidApiRequestDTO request){
        RfidApiResponseDTO<Boolean> baseResult = new RfidApiResponseDTO<>();
        String execNo = ExecNoContext.getExecNo();

        // 验证基础参数
        if (!paramUtil.validateBaseParams(request, "storageIn", baseResult)) {
            return baseResult;
        }

        // 解析业务参数
        StorageOperationDTO param = paramUtil.parseParam(request.getParam(), StorageOperationDTO.class);
        if (param == null) {
            baseResult.setCode("310");
            baseResult.setMessage("业务参数解析失败");
            return baseResult;
        }

        try {
            LocalDateTime current = TimeUtil.getSysDate();
            TagStorageOperationBean tagStorageOperationBean = new TagStorageOperationBean();
            tagStorageOperationBean.setExecNo(execNo);
            tagStorageOperationBean.setNoticeNo(param.getTicketNo());
            tagStorageOperationBean.setNoticeQuantity(param.getQuantity());
            tagStorageOperationBean.setNoticeTime(current);
            tagStorageOperationBean.setNoticeType(PlatformConstant.STORAGE_OPERATION_TYPE.STORAGE_IN);
            boolean saved = tagStorageOperationService.saveTagStorageOperation(tagStorageOperationBean);
            baseResult.setData(saved);

            // TODO 调用接口发送给硬件设备

        } catch (Exception e) {
            baseResult.setData(false);
            baseResult.setCode("400");
            baseResult.setMessage("接口保存失败：" + e.getMessage());
        }
        return baseResult;
    }

    /**
     * 出库通知接口
     * 接收出库通知信息，记录出库操作
     * 
     * @param request 包含出库信息的请求对象
     * @return 出库通知处理结果
     */
    @PostMapping(value = "/storage/out")
    @InterfaceLog(type = 5, description = "出库通知")
    @Operation(summary = "出库通知", description = "接收货物出库通知，记录出库操作信息并通知硬件设备")
    public RfidApiResponseDTO<Boolean> storageOut(
            @Parameter(description = "出库通知请求参数，包含单据号和数量信息", required = true)
            @RequestBody RfidApiRequestDTO request){
        RfidApiResponseDTO<Boolean> baseResult = new RfidApiResponseDTO<>();

        // 验证基础参数
        if (!paramUtil.validateBaseParams(request, "storageOut", baseResult)) {
            return baseResult;
        }

        // 解析业务参数
        StorageOperationDTO param = paramUtil.parseParam(request.getParam(), StorageOperationDTO.class);
        if (param == null) {
            baseResult.setCode("310");
            baseResult.setMessage("业务参数解析失败");
            return baseResult;
        }

        String execNo = ExecNoContext.getExecNo();

        try {
            LocalDateTime current = TimeUtil.getSysDate();
            TagStorageOperationBean tagStorageOperationBean = new TagStorageOperationBean();
            tagStorageOperationBean.setExecNo(execNo);
            tagStorageOperationBean.setNoticeNo(param.getTicketNo());
            tagStorageOperationBean.setNoticeQuantity(param.getQuantity());
            tagStorageOperationBean.setNoticeTime(current);
            tagStorageOperationBean.setNoticeType(PlatformConstant.STORAGE_OPERATION_TYPE.STORAGE_OUT);
            boolean saved = tagStorageOperationService.saveTagStorageOperation(tagStorageOperationBean);
            baseResult.setData(saved);

            // TODO 调用接口发送给硬件设备

        } catch (Exception e) {
            baseResult.setData(false);
            baseResult.setCode("400");
            baseResult.setMessage("接口保存失败：" + e.getMessage());
        }
        return baseResult;
    }
}
