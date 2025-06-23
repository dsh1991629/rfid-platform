package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.TagInfoBean;
import com.rfid.platform.mapper.TagInfoMapper;
import com.rfid.platform.service.TagInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

@Service
public class TagInfoServiceImpl extends ServiceImpl<TagInfoMapper, TagInfoBean> implements TagInfoService {

    @Override
    public boolean save(TagInfoBean entity) {
        return super.save(entity);
    }

    @Override
    public boolean removeById(Long id) {
        return super.removeById(id);
    }

    @Override
    public boolean updateById(TagInfoBean entity) {
        return super.updateById(entity);
    }

    @Override
    public TagInfoBean getById(Long id) {
        return super.getById(id);
    }

    @Override
    public IPage<TagInfoBean> page(Page<TagInfoBean> page, LambdaQueryWrapper<TagInfoBean> query) {
        return super.page(page, query);
    }
} 