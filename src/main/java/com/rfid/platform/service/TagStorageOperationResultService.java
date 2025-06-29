package com.rfid.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.TagStorageOperationResultBean;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface TagStorageOperationResultService {

    boolean saveTagStorageOperationResult(TagStorageOperationResultBean entity);

    boolean removeTagStorageOperationResultByPk(Long id);

    boolean updateTagStorageOperationResultByPk(TagStorageOperationResultBean entity);

    TagStorageOperationResultBean getTagStorageOperationResultByPk(Long id);

    IPage<TagStorageOperationResultBean> pageTagStorageOperationResult(Page<TagStorageOperationResultBean> page, LambdaQueryWrapper<TagStorageOperationResultBean> query);
} 