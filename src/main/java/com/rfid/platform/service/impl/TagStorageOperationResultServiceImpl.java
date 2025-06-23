package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.TagStorageOperationResultBean;
import com.rfid.platform.mapper.TagStorageOperationResultMapper;
import com.rfid.platform.service.TagStorageOperationResultService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

@Service
public class TagStorageOperationResultServiceImpl extends ServiceImpl<TagStorageOperationResultMapper, TagStorageOperationResultBean> implements TagStorageOperationResultService {

    @Override
    public boolean save(TagStorageOperationResultBean entity) {
        return super.save(entity);
    }

    @Override
    public boolean removeById(Long id) {
        return super.removeById(id);
    }

    @Override
    public boolean updateById(TagStorageOperationResultBean entity) {
        return super.updateById(entity);
    }

    @Override
    public TagStorageOperationResultBean getById(Long id) {
        return super.getById(id);
    }

    @Override
    public IPage<TagStorageOperationResultBean> page(Page<TagStorageOperationResultBean> page, LambdaQueryWrapper<TagStorageOperationResultBean> query) {
        return super.page(page, query);
    }
} 