package com.rfid.platform.controller;

import com.alibaba.excel.EasyExcel;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/rfid/tag")
public class TagImportController {

    @Autowired
    private TagImportInfoService tagImportInfoService;

    @Autowired
    private TagInfoService tagInfoService;
    
    @PostMapping("/import")
    @InterfaceLog(type = 1, description = "标签导入")
    public BaseResult<String> importTags(@RequestParam("file") MultipartFile file) {
        BaseResult<String> baseResult = new BaseResult<>();
        if (file.isEmpty()) {
            baseResult.setCode(PlatformConstant.RET_CODE.FAILED);
            baseResult.setMessage("文件为空");
            return baseResult;
        }

        // 获取AOP中生成的execNo
        String execNo = ExecNoContext.getExecNo();
        log.info("当前执行编号: {}", execNo);
        
        try {
            // 使用EasyExcel读取文件，跳过第一行标题
            List<TagImportExcelDTO> dataList = EasyExcel.read(file.getInputStream())
                    .head(TagImportExcelDTO.class)
                    .sheet()
                    .headRowNumber(1) // 第一行是标题
                    .doReadSync();
            
            int successCount = 0;
            int failCount = 0;

            List<TagImportInfoBean> tagImportInfoBeans = new ArrayList<>();

            for (TagImportExcelDTO dto : dataList) {
                // 创建导入详细记录
                TagImportInfoBean importInfo = new TagImportInfoBean();
                importInfo.setEcpCode(dto.getEpcCode());
                importInfo.setSkuCode(dto.getSkuCode());
                importInfo.setImportType(1); // 1表示Excel导入
                importInfo.setExecNo(execNo);
                importInfo.setImportTime(LocalDateTime.now());

                try {
                    // 创建TagInfoBean对象
                    TagInfoBean tagInfo = new TagInfoBean();
                    tagInfo.setSkuCode(dto.getSkuCode());
                    tagInfo.setEpcCode(dto.getEpcCode());
                    tagInfo.setState(1); // 默认状态为1
                    tagInfo.setInTime(LocalDateTime.now());
                    
                    // 调用tagInfoService保存
                    boolean saved = tagInfoService.save(tagInfo);
                    if (saved) {
                        successCount++;
                        importInfo.setImportResult("S");
                    } else {
                        failCount++;
                        importInfo.setImportResult("F");
                    }
                } catch (Exception e) {
                    failCount++;
                    importInfo.setImportResult("F");
                }

                tagImportInfoBeans.add(importInfo);
            }

            // 保存导入详细记录
            try {
                tagImportInfoService.saveTagImportInfos(tagImportInfoBeans);
            } catch (Exception e) {
                log.error("[{}] 保存导入记录失败: {}", execNo, e.getMessage());
            }

            baseResult.setMessage("导入完成");
            baseResult.setData(String.format("成功：%d条，失败：%d条", successCount, failCount));
            return baseResult;
        } catch (IOException e) {
            baseResult.setCode(PlatformConstant.RET_CODE.FAILED);
            baseResult.setMessage("文件读取失败：" + e.getMessage());
            return baseResult;
        } catch (Exception e) {
            baseResult.setCode(PlatformConstant.RET_CODE.FAILED);
            baseResult.setMessage("导入失败：" + e.getMessage());
            return baseResult;
        }
    }
}
