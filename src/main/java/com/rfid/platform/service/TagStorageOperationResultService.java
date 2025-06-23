package com.rfid.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.TagStorageOperationResultBean;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface TagStorageOperationResultService {

    boolean save(TagStorageOperationResultBean entity);

    boolean removeById(Long id);

    boolean updateById(TagStorageOperationResultBean entity);

    TagStorageOperationResultBean getById(Long id);

    IPage<TagStorageOperationResultBean> page(Page<TagStorageOperationResultBean> page, LambdaQueryWrapper<TagStorageOperationResultBean> query);
} 