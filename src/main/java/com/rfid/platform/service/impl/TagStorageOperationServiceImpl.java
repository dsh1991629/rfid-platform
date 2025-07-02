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
    public boolean saveTagStorageOperation(TagStorageOperationBean entity) {
        return super.save(entity);
    }

    @Override
    public boolean removeTagStorageOperationByPk(Long id) {
        return super.removeById(id);
    }

    @Override
    public boolean updateTagStorageOperationByPk(TagStorageOperationBean entity) {
        return super.updateById(entity);
    }

    @Override
    public TagStorageOperationBean getTagStorageOperationByPk(Long id) {
        return super.getById(id);
    }

    @Override
    public IPage<TagStorageOperationBean> pageTagStorageOperation(Page<TagStorageOperationBean> page, LambdaQueryWrapper<TagStorageOperationBean> query) {
        return super.page(page, query);
    }

    @Override
    public java.util.List<TagStorageOperationBean> listTagStorageOperation(LambdaQueryWrapper<TagStorageOperationBean> query) {
        return super.list(query);
    }
}
