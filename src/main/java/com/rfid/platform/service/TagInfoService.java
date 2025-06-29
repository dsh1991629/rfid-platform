package com.rfid.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.TagInfoBean;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface TagInfoService {

    boolean saveTagInfo(TagInfoBean entity);

    boolean updateTagInfoByPk(TagInfoBean entity);

    TagInfoBean getTagInfoByPk(Long id);

    IPage<TagInfoBean> pageTagInfo(Page<TagInfoBean> page, LambdaQueryWrapper<TagInfoBean> query);
} 