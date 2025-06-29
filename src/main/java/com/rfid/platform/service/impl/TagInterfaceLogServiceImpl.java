package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.TagInterfaceLogBean;
import com.rfid.platform.mapper.TagInterfaceLogMapper;
import com.rfid.platform.service.TagInterfaceLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

@Service
public class TagInterfaceLogServiceImpl extends ServiceImpl<TagInterfaceLogMapper, TagInterfaceLogBean> implements TagInterfaceLogService {

    @Override
    public boolean saveTagInterfaceLog(TagInterfaceLogBean entity) {
        return super.save(entity);
    }

    @Override
    public boolean removeTagInterfaceLogByPk(Long id) {
        return super.removeById(id);
    }

    @Override
    public boolean updateTagInterfaceLogByPk(TagInterfaceLogBean entity) {
        return super.updateById(entity);
    }

    @Override
    public TagInterfaceLogBean getTagInterfaceLogByPk(Long id) {
        return super.getById(id);
    }

    @Override
    public IPage<TagInterfaceLogBean> pageTagInterfaceLog(Page<TagInterfaceLogBean> page, LambdaQueryWrapper<TagInterfaceLogBean> query) {
        return super.page(page, query);
    }
} 