package com.rfid.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.TagImportInfoBean;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface TagImportInfoService {

    boolean save(TagImportInfoBean entity);

    boolean removeById(Long id);

    boolean updateById(TagImportInfoBean entity);

    TagImportInfoBean getById(Long id);

    IPage<TagImportInfoBean> page(Page<TagImportInfoBean> page, LambdaQueryWrapper<TagImportInfoBean> query);
} 