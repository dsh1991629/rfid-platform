package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rfid.platform.entity.TagStorageOperationBean;
import com.rfid.platform.mapper.TagStorageOperationMapper;
import com.rfid.platform.service.TagStorageOperationService;
import org.springframework.stereotype.Service;

@Service
public class TagStorageOperationServiceImpl extends ServiceImpl<TagStorageOperationMapper, TagStorageOperationBean> implements TagStorageOperationService {

    @Override
    public boolean save(TagStorageOperationBean entity) {
        return super.save(entity);
    }

    @Override
    public boolean removeById(Long id) {
        return super.removeById(id);
    }

    @Override
    public boolean updateById(TagStorageOperationBean entity) {
        return super.updateById(entity);
    }

    @Override
    public TagStorageOperationBean getById(Long id) {
        return super.getById(id);
    }

    @Override
    public IPage<TagStorageOperationBean> page(Page<TagStorageOperationBean> page, LambdaQueryWrapper<TagStorageOperationBean> query) {
        return super.page(page, query);
    }
}
