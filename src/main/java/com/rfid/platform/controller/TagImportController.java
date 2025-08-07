package com.rfid.platform.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rfid.platform.annotation.InterfaceLog;
import com.rfid.platform.common.ExecNoContext;
import com.rfid.platform.common.PageResult;
import com.rfid.platform.entity.TagImportInfoBean;
import com.rfid.platform.entity.TagInfoBean;
import com.rfid.platform.persistence.RfidApiRequestDTO;
import com.rfid.platform.persistence.RfidApiResponseDTO;
import com.rfid.platform.persistence.TagAddDTO;
import com.rfid.platform.persistence.TagDTO;
import com.rfid.platform.persistence.TagImportExcelDTO;
import com.rfid.platform.persistence.TagInfoDeleteDTO;
import com.rfid.platform.persistence.TagPageQueryDTO;
import com.rfid.platform.persistence.TagUpdateDTO;
import com.rfid.platform.service.TagImportInfoService;
import com.rfid.platform.service.TagInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
@Tag(name = "标签Rfid管理", description = "RFID标签相关接口")
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
        description = "通过上传Excel文件批量导入RFID标签信息，文件格式要求包含EPC码和SKU码列"
    )
    public RfidApiResponseDTO<String> importTags(
        @Parameter(
            description = "Excel文件，包含EPC码和SKU码等标签信息",
            required = true,
            schema = @Schema(type = "string", format = "binary")
        )
        @RequestParam("file") MultipartFile file) {

        RfidApiResponseDTO<String> baseResult = RfidApiResponseDTO.success();
        
        // 验证文件是否为空
        if (file.isEmpty()) {
            baseResult.setStatus(false);
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
                importInfo.setProductName(dto.getProductName());
                importInfo.setProductSize(dto.getProductSize());
                importInfo.setProductColor(dto.getProductColor());
                importInfo.setImportType(2); // 2表示Excel导入
                importInfo.setExecNo(execNo);
                importInfo.setImportTime(LocalDateTime.now());

                try {
                    // 创建TagInfoBean对象
                    TagInfoBean tagInfo = new TagInfoBean();
                    tagInfo.setSku(dto.getSku());
                    tagInfo.setEpc(dto.getEpc());
                    tagInfo.setProductCode(dto.getProductCode());
                    tagInfo.setProductName(dto.getProductName());
                    tagInfo.setProductSize(dto.getProductSize());
                    tagInfo.setProductColor(dto.getProductSize());
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
            baseResult.setStatus(false);
            baseResult.setMessage("文件读取失败：" + e.getMessage());
            return baseResult;
        } catch (Exception e) {
            log.error("[{}] 导入过程发生异常: {}", execNo, e.getMessage());
            baseResult.setStatus(false);
            baseResult.setMessage("导入失败：" + e.getMessage());
            return baseResult;
        }
    }


    /**
     * 创建RFID标签信息
     *
     * @param requestDTO 包含标签信息数据
     * @return 导入结果，包含成功和失败的数量统计
     */
    @PostMapping(value = "/add")
    @InterfaceLog(type = 2, description = "标签创建")
    @Operation(
            summary = "创建RFID标签",
            description = "创建RFID标签"
    )
    public RfidApiResponseDTO<String> createTags(
            @Parameter(
                    description = "包含EPC码和SKU码等标签信息",
                    required = true
            )
            @RequestBody RfidApiRequestDTO<TagAddDTO> requestDTO) {

        RfidApiResponseDTO<String> baseResult = RfidApiResponseDTO.success();

        // 验证文件是否为空
        if (Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getData())) {
            baseResult.setStatus(false);
            baseResult.setMessage("请求参数不存在");
            return baseResult;
        }

        // 获取AOP中生成的执行编号，用于追踪本次导入操作
        String execNo = ExecNoContext.getExecNo();
        log.info("当前创建执行编号: {}", execNo);

        try {
            TagAddDTO tagAddDTO = requestDTO.getData();

            List<TagImportInfoBean> tagImportInfoBeans = new ArrayList<>();
            // 创建导入详细记录
            TagImportInfoBean importInfo = new TagImportInfoBean();
            importInfo.setEpc(tagAddDTO.getEpc());
            importInfo.setSku(tagAddDTO.getSku());
            importInfo.setSkuIndex(tagAddDTO.getEpc().substring(2, 10));
            importInfo.setProductCode(tagAddDTO.getProductCode());
            importInfo.setProductName(tagAddDTO.getProductName());
            importInfo.setProductSize(tagAddDTO.getProductSize());
            importInfo.setProductColor(tagAddDTO.getProductColor());
            importInfo.setImportType(1); // 2表示Excel导入
            importInfo.setExecNo(execNo);
            importInfo.setImportTime(LocalDateTime.now());

            try {
                // 创建TagInfoBean对象
                TagInfoBean tagInfo = new TagInfoBean();
                tagInfo.setSku(tagAddDTO.getSku());
                tagInfo.setEpc(tagAddDTO.getEpc());
                tagInfo.setProductCode(tagAddDTO.getProductCode());
                tagInfo.setProductName(tagAddDTO.getProductName());
                tagInfo.setProductSize(tagAddDTO.getProductSize());
                tagInfo.setProductColor(tagAddDTO.getProductSize());
                tagInfo.setState(1); // 默认状态为1（激活状态）
                tagInfo.setInTime(LocalDateTime.now());

                // 调用tagInfoService保存标签信息
                boolean saved = tagInfoService.saveTagInfo(tagInfo);
                if (saved) {
                    importInfo.setImportResult("S"); // S表示成功
                } else {
                    importInfo.setImportResult("F"); // F表示失败
                }
            } catch (Exception e) {
                importInfo.setImportResult("F");
                log.error("[{}] 保存标签信息失败，EPC: {}, SKU: {}, 款式ma: {}, 错误: {}",
                        execNo, tagAddDTO.getEpc(), tagAddDTO.getSku(), tagAddDTO.getProductCode(), e.getMessage());
            }

            // 批量保存导入详细记录
            try {
                tagImportInfoService.saveTagImportInfos(tagImportInfoBeans);
            } catch (Exception e) {
                log.error("[{}] 保存创建记录失败: {}", execNo, e.getMessage());
            }

            // 返回导入结果统计
            baseResult.setMessage("导入完成");
            baseResult.setData(String.format("成功创建标签"));
            return baseResult;

        } catch (Exception e) {
            log.error("[{}] 创建过程发生异常: {}", execNo, e.getMessage());
            baseResult.setStatus(false);
            baseResult.setMessage("创建失败：" + e.getMessage());
            return baseResult;
        }
    }


    @Operation(
            summary = "分页查询标签",
            description = "根据查询条件分页获取标签列表，支持按SKU、款式码"
    )
    @PostMapping(value = "/page")
    public RfidApiResponseDTO<PageResult<TagDTO>> accountPage(
            @Parameter(description = "标签分页查询条件", required = true)
            @RequestBody RfidApiRequestDTO<TagPageQueryDTO> requestDTO,
            @Parameter(description = "页码，从1开始", example = "1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize) {
        RfidApiResponseDTO<PageResult<TagDTO>> result = RfidApiResponseDTO.success();
        try {
            Page<TagInfoBean> page = new Page<>(pageNum, pageSize);
            LambdaQueryWrapper<TagInfoBean> queryWrapper = new LambdaQueryWrapper<>();

            if (Objects.nonNull(requestDTO.getData())) {
                TagPageQueryDTO tagPageQueryDTO = requestDTO.getData();
                // 构建查询条件
                if (StringUtils.isNotBlank(tagPageQueryDTO.getSku())) {
                    queryWrapper.like(TagInfoBean::getSku, tagPageQueryDTO.getSku());
                }
                if (StringUtils.isNotBlank(tagPageQueryDTO.getProductCode())) {
                    queryWrapper.like(TagInfoBean::getProductCode, tagPageQueryDTO.getProductCode());
                }
            }

            queryWrapper.orderByDesc(TagInfoBean::getCreateTime);

            IPage<TagInfoBean> pageResult = tagInfoService.pageTagInfo(page, queryWrapper);

            // 转换结果
            PageResult<TagDTO> pageResultDTO = new PageResult<>();
            pageResultDTO.setPageNum(pageNum);
            pageResultDTO.setPageSize(pageSize);
            pageResultDTO.setTotal(pageResult.getTotal());
            pageResultDTO.setPages(pageResult.getPages());

            List<TagDTO> dtoList = pageResult.getRecords().stream()
                    .map(bean -> {
                        TagDTO dto = BeanUtil.copyProperties(bean, TagDTO.class);
                        String storageStateName = convertStorageStateName(bean.getStorageState());
                        dto.setStorageStateName(storageStateName);
                        return dto;
                    })
                    .collect(Collectors.toList());

            pageResultDTO.setData(dtoList);
            result.setData(pageResultDTO);
            result.setMessage("查询成功");
        } catch (Exception e) {
            result.setStatus(false);
            result.setMessage("分页查询异常: " + e.getMessage());
        }
        return result;
    }

    private String convertStorageStateName(Integer storageState) {
        String storageStateName = "";
        switch (storageState) {
            case 1:
                storageStateName = "待入库";
                break;
            case 3:
                storageStateName = "入库";
                break;
            case 5:
                storageStateName = "出库";
                break;
            default:
                break;
        }
        return storageStateName;
    }


    @Operation(summary = "更新标签", description = "更新标签")
    @PostMapping(value = "/update")
    public RfidApiResponseDTO<Boolean> updateDevice(@Parameter(description = "标签更新参数", required = true)
                                                    @RequestBody RfidApiRequestDTO<TagUpdateDTO> requestDTO) {
        RfidApiResponseDTO<Boolean> result = RfidApiResponseDTO.success();
        try {
            if (Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getData())) {
                result.setStatus(false);
                result.setMessage("标签数据不能为空");
                return result;
            }

            TagUpdateDTO tagUpdateDTO = requestDTO.getData();
            // 参数校验
            if (Objects.isNull(tagUpdateDTO.getId())) {
                result.setStatus(false);
                result.setMessage("标签ID不能为空");
                return result;
            }

            // 检查SKU是否已存在
            if (StringUtils.isNotBlank(tagUpdateDTO.getSku())) {
                LambdaQueryWrapper<TagInfoBean> nameCheckWrapper = new LambdaQueryWrapper<>();
                nameCheckWrapper.eq(TagInfoBean::getSku, tagUpdateDTO.getSku());
                nameCheckWrapper.ne(TagInfoBean::getId, tagUpdateDTO.getId());
                Boolean existingTags = tagInfoService.existTagInfo(nameCheckWrapper);
                if (existingTags) {
                    result.setStatus(false);
                    result.setMessage("SKU编码已存在，不能重复");
                    return result;
                }
            }

            // 检查PRODUCT_CODE是否已存在
            if (StringUtils.isNotBlank(tagUpdateDTO.getProductCode())) {
                LambdaQueryWrapper<TagInfoBean> nameCheckWrapper = new LambdaQueryWrapper<>();
                nameCheckWrapper.eq(TagInfoBean::getProductCode, tagUpdateDTO.getProductCode());
                nameCheckWrapper.ne(TagInfoBean::getId, tagUpdateDTO.getId());
                Boolean existingTags = tagInfoService.existTagInfo(nameCheckWrapper);
                if (existingTags) {
                    result.setStatus(false);
                    result.setMessage("款式码已存在，不能重复");
                    return result;
                }
            }

            // 检查EPC是否已存在
            if (StringUtils.isNotBlank(tagUpdateDTO.getEpc())) {
                LambdaQueryWrapper<TagInfoBean> nameCheckWrapper = new LambdaQueryWrapper<>();
                nameCheckWrapper.eq(TagInfoBean::getEpc, tagUpdateDTO.getEpc());
                nameCheckWrapper.ne(TagInfoBean::getId, tagUpdateDTO.getId());
                Boolean existingTags = tagInfoService.existTagInfo(nameCheckWrapper);
                if (existingTags) {
                    result.setStatus(false);
                    result.setMessage("Rfid码已存在，不能重复");
                    return result;
                }
            }


            TagInfoBean tagInfoBean = BeanUtil.copyProperties(tagUpdateDTO, TagInfoBean.class);
            // 保存设备
            boolean success = tagInfoService.updateTagInfo(tagInfoBean);
            if (success) {
                result.setData(true);
                result.setMessage("更新成功");
            } else {
                result.setData(false);
                result.setStatus(false);
                result.setMessage("更新失败");
            }
        } catch (Exception e) {
            result.setStatus(false);
            result.setMessage("系统异常：" + e.getMessage());
        }

        return result;
    }


    @Operation(summary = "删除标签", description = "删除标签")
    @PostMapping(value = "/delete")
    public RfidApiResponseDTO<Boolean> deleteDevice(@Parameter(description = "标签删除参数", required = true)
                                                    @RequestBody RfidApiRequestDTO<TagInfoDeleteDTO> requestDTO) {
        RfidApiResponseDTO<Boolean> result = RfidApiResponseDTO.success();
        try {
            if (Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getData())) {
                result.setStatus(false);
                result.setMessage("标签数据不能为空");
                return result;
            }

            TagInfoDeleteDTO tagInfoDeleteDTO = requestDTO.getData();
            // 参数校验
            if (Objects.isNull(tagInfoDeleteDTO.getId())) {
                result.setStatus(false);
                result.setMessage("标签ID不能为空");
                return result;
            }

            // 保存设备
            boolean success = tagInfoService.deleteTagInfo(tagInfoDeleteDTO.getId());
            if (success) {
                result.setData(true);
                result.setMessage("删除成功");
            } else {
                result.setData(false);
                result.setStatus(false);
                result.setMessage("删除失败");
            }
        } catch (Exception e) {
            result.setStatus(false);
            result.setMessage("系统异常：" + e.getMessage());
        }

        return result;
    }

}
