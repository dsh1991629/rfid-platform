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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * RFID标签导入控制器
 * 提供标签批量导入功能，支持Excel文件上传和数据解析
 */
@Slf4j
@RestController
@RequestMapping(value = "/rfid/tag")
@Tag(name = "标签导入管理", description = "RFID标签批量导入相关接口")
public class TagImportController {

    @Autowired
    private TagImportInfoService tagImportInfoService;

    @Autowired
    private TagInfoService tagInfoService;
    
    /**
     * 批量导入RFID标签信息
     * 通过上传Excel文件批量导入标签数据，支持EPC码和SKU码的关联
     * 
     * @param file Excel文件，包含标签信息数据
     * @return 导入结果，包含成功和失败的数量统计
     */
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @InterfaceLog(type = 1, description = "标签导入")
    @Operation(
        summary = "批量导入RFID标签",
        description = "通过上传Excel文件批量导入RFID标签信息，文件格式要求包含EPC码和SKU码列",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "包含标签信息的Excel文件",
            required = true,
            content = @Content(
                mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                schema = @Schema(type = "string", format = "binary")
            )
        )
    )
    public BaseResult<String> importTags(
        @Parameter(
            description = "Excel文件，包含EPC码和SKU码等标签信息",
            required = true,
            schema = @Schema(type = "string", format = "binary")
        )
        @RequestParam("file") MultipartFile file) {
        
        BaseResult<String> baseResult = new BaseResult<>();
        
        // 验证文件是否为空
        if (file.isEmpty()) {
            baseResult.setCode(PlatformConstant.RET_CODE.FAILED);
            baseResult.setMessage("文件为空");
            return baseResult;
        }

        // 获取AOP中生成的执行编号，用于追踪本次导入操作
        String execNo = ExecNoContext.getExecNo();
        log.info("当前执行编号: {}", execNo);
        
        try {
            // 使用EasyExcel读取文件，跳过第一行标题
            List<TagImportExcelDTO> dataList = EasyExcel.read(file.getInputStream())
                    .head(TagImportExcelDTO.class)
                    .sheet()
                    .headRowNumber(1) // 第一行是标题
                    .doReadSync();
            
            int successCount = 0; // 成功导入计数
            int failCount = 0;    // 失败导入计数

            List<TagImportInfoBean> tagImportInfoBeans = new ArrayList<>();

            // 遍历Excel数据，逐条处理标签信息
            for (TagImportExcelDTO dto : dataList) {
                // 创建导入详细记录
                TagImportInfoBean importInfo = new TagImportInfoBean();
                importInfo.setEpc(dto.getEpc());
                importInfo.setSku(dto.getSku());
                importInfo.setSkuIndex(dto.getEpc().substring(2, 10));
                importInfo.setProductCode(dto.getProductCode());
                importInfo.setImportType(2); // 2表示Excel导入
                importInfo.setExecNo(execNo);
                importInfo.setImportTime(LocalDateTime.now());

                try {
                    // 创建TagInfoBean对象
                    TagInfoBean tagInfo = new TagInfoBean();
                    tagInfo.setSku(dto.getSku());
                    tagInfo.setEpc(dto.getEpc());
                    tagInfo.setProductCode(dto.getProductCode());
                    tagInfo.setState(1); // 默认状态为1（激活状态）
                    tagInfo.setInTime(LocalDateTime.now());
                    
                    // 调用tagInfoService保存标签信息
                    boolean saved = tagInfoService.saveTagInfo(tagInfo);
                    if (saved) {
                        successCount++;
                        importInfo.setImportResult("S"); // S表示成功
                    } else {
                        failCount++;
                        importInfo.setImportResult("F"); // F表示失败
                    }
                } catch (Exception e) {
                    failCount++;
                    importInfo.setImportResult("F");
                    log.error("[{}] 保存标签信息失败，EPC: {}, SKU: {}, 款式ma: {}, 错误: {}",
                        execNo, dto.getEpc(), dto.getSku(), dto.getProductCode(), e.getMessage());
                }

                tagImportInfoBeans.add(importInfo);
            }

            // 批量保存导入详细记录
            try {
                tagImportInfoService.saveTagImportInfos(tagImportInfoBeans);
            } catch (Exception e) {
                log.error("[{}] 保存导入记录失败: {}", execNo, e.getMessage());
            }

            // 返回导入结果统计
            baseResult.setMessage("导入完成");
            baseResult.setData(String.format("成功：%d条，失败：%d条", successCount, failCount));
            return baseResult;
            
        } catch (IOException e) {
            log.error("[{}] 文件读取失败: {}", execNo, e.getMessage());
            baseResult.setCode(PlatformConstant.RET_CODE.FAILED);
            baseResult.setMessage("文件读取失败：" + e.getMessage());
            return baseResult;
        } catch (Exception e) {
            log.error("[{}] 导入过程发生异常: {}", execNo, e.getMessage());
            baseResult.setCode(PlatformConstant.RET_CODE.FAILED);
            baseResult.setMessage("导入失败：" + e.getMessage());
            return baseResult;
        }
    }

}
