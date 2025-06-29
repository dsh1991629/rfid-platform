package com.rfid.platform.controller;

import com.rfid.platform.annotation.InterfaceLog;
import com.rfid.platform.common.BaseResult;
import com.rfid.platform.common.ExecNoContext;
import com.rfid.platform.common.PlatformConstant;
import com.rfid.platform.entity.TagImportInfoBean;
import com.rfid.platform.entity.TagInfoBean;
import com.rfid.platform.persistence.TagImportExcelDTO;
import com.rfid.platform.service.TagImportInfoService;
import com.rfid.platform.service.TagInfoService;
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


    @PostMapping(value = "/push")
    @InterfaceLog(type = 2, description = "标签推送")
    public BaseResult<Boolean> tagPush(@RequestBody TagImportExcelDTO tagImportExcelDTO){
        BaseResult<Boolean> baseResult = new BaseResult<>();

        String execNo = ExecNoContext.getExecNo();

        // 创建导入详细记录
        TagImportInfoBean importInfo = new TagImportInfoBean();
        importInfo.setEcpCode(tagImportExcelDTO.getEpcCode());
        importInfo.setSkuCode(tagImportExcelDTO.getSkuCode());
        importInfo.setImportType(1); // 1表示接口推送
        importInfo.setExecNo(execNo);
        importInfo.setImportTime(LocalDateTime.now());

        try {
            // 创建TagInfoBean对象
            TagInfoBean tagInfo = new TagInfoBean();
            tagInfo.setSkuCode(tagImportExcelDTO.getSkuCode());
            tagInfo.setEpcCode(tagImportExcelDTO.getEpcCode());
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
            baseResult.setCode(PlatformConstant.RET_CODE.FAILED);
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

}
