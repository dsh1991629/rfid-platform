package com.rfid.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.TagStorageOperationBean;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface TagStorageOperationService {

    boolean save(TagStorageOperationBean entity);

    boolean removeById(Long id);

    boolean updateById(TagStorageOperationBean entity);

    TagStorageOperationBean getById(Long id);

    IPage<TagStorageOperationBean> page(Page<TagStorageOperationBean> page, LambdaQueryWrapper<TagStorageOperationBean> query);
} 