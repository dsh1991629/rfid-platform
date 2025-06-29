package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.TagImportInfoBean;
import com.rfid.platform.mapper.TagImportInfoMapper;
import com.rfid.platform.service.TagImportInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagImportInfoServiceImpl extends ServiceImpl<TagImportInfoMapper, TagImportInfoBean> implements TagImportInfoService {

    @Override
    public boolean save(TagImportInfoBean entity) {
        return super.save(entity);
    }

    @Override
    public boolean removeById(Long id) {
        return super.removeById(id);
    }

    @Override
    public boolean updateById(TagImportInfoBean entity) {
        return super.updateById(entity);
    }

    @Override
    public TagImportInfoBean getById(Long id) {
        return super.getById(id);
    }

    @Override
    public IPage<TagImportInfoBean> page(Page<TagImportInfoBean> page, LambdaQueryWrapper<TagImportInfoBean> query) {
        return super.page(page, query);
    }

    @Override
    public boolean saveTagImportInfos(List<TagImportInfoBean> tagImportInfoBeans) {
        return super.saveBatch(tagImportInfoBeans, 100);
    }
}