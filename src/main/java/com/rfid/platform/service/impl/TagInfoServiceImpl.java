package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.TagInfoBean;
import com.rfid.platform.mapper.TagInfoMapper;
import com.rfid.platform.service.TagInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TagInfoServiceImpl extends ServiceImpl<TagInfoMapper, TagInfoBean> implements TagInfoService {

    @Override
    public boolean saveTagInfo(TagInfoBean entity) {
        return super.save(entity);
    }

    @Override
    public boolean updateTagInfoByPk(TagInfoBean entity) {
        return super.updateById(entity);
    }

    @Override
    public TagInfoBean getTagInfoByPk(Long id) {
        return super.getById(id);
    }

    @Override
    public IPage<TagInfoBean> pageTagInfo(Page<TagInfoBean> page, LambdaQueryWrapper<TagInfoBean> query) {
        return super.page(page, query);
    }

    @Override
    public java.util.List<TagInfoBean> listTagInfo(LambdaQueryWrapper<TagInfoBean> query) {
        return super.list(query);
    }

    @Override
    public boolean updateTagInfos(List<TagInfoBean> validatedTagInfoBeans) {
        return super.updateBatchById(validatedTagInfoBeans, 100);
    }
}