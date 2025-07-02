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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping(value = "/rfid/tag/operation")
public class TagOperationController {

    @Autowired
    private TagInfoService tagInfoService;

    @Autowired
    private TagImportInfoService tagImportInfoService;

    @Autowired
    private TagStorageOperationService tagStorageOperationService;

    @Autowired
    private ParamUtil paramUtil;


    @PostMapping(value = "/push")
    @InterfaceLog(type = 2, description = "标签推送")
    public RfidApiResponseDTO<Boolean> tagPush(@RequestBody RfidApiRequestDTO request){
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


    @PostMapping(value = "/storage/in")
    @InterfaceLog(type = 3, description = "入库通知")
    public RfidApiResponseDTO<Boolean> storageIn(@RequestBody RfidApiRequestDTO request){
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


    @PostMapping(value = "/storage/out")
    @InterfaceLog(type = 5, description = "出库通知")
    public RfidApiResponseDTO<Boolean> storageOut(@RequestBody RfidApiRequestDTO request){
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
