package com.rfid.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.TagStorageOperationBean;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

public interface TagStorageOperationService {

    boolean saveTagStorageOperation(TagStorageOperationBean entity);

    boolean removeTagStorageOperationByPk(Long id);

    boolean updateTagStorageOperationByPk(TagStorageOperationBean entity);

    TagStorageOperationBean getTagStorageOperationByPk(Long id);

    IPage<TagStorageOperationBean> pageTagStorageOperation(Page<TagStorageOperationBean> page, LambdaQueryWrapper<TagStorageOperationBean> query);

    List<TagStorageOperationBean> listTagStorageOperation(LambdaQueryWrapper<TagStorageOperationBean> query);
}