package com.rfid.platform.controller;

import com.alibaba.excel.EasyExcel;
import com.rfid.platform.annotation.InterfaceLog;
import com.rfid.platform.common.BaseResult;
import com.rfid.platform.common.PlatformConstant;
import com.rfid.platform.entity.TagInfoBean;
import com.rfid.platform.persistence.TagImportExcelDTO;
import com.rfid.platform.service.TagImportInfoService;
import com.rfid.platform.service.TagInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

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
        
        try {
            // 使用EasyExcel读取文件，跳过第一行标题
            List<TagImportExcelDTO> dataList = EasyExcel.read(file.getInputStream())
                    .head(TagImportExcelDTO.class)
                    .sheet()
                    .headRowNumber(1) // 第一行是标题
                    .doReadSync();
            
            int successCount = 0;
            int failCount = 0;
            
            for (TagImportExcelDTO dto : dataList) {
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
                    } else {
                        failCount++;
                    }
                } catch (Exception e) {
                    failCount++;
                    // 可以记录具体的错误信息
                    System.err.println("保存数据失败: " + e.getMessage());
                }
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
