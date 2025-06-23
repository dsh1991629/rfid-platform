package com.rfid.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.TagInfoBean;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface TagInfoService {

    boolean save(TagInfoBean entity);

    boolean removeById(Long id);

    boolean updateById(TagInfoBean entity);

    TagInfoBean getById(Long id);

    IPage<TagInfoBean> page(Page<TagInfoBean> page, LambdaQueryWrapper<TagInfoBean> query);
} 