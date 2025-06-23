package com.rfid.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.TagInterfaceLogBean;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface TagInterfaceLogService {

    boolean save(TagInterfaceLogBean entity);

    boolean removeById(Long id);

    boolean updateById(TagInterfaceLogBean entity);

    TagInterfaceLogBean getById(Long id);

    IPage<TagInterfaceLogBean> page(Page<TagInterfaceLogBean> page, LambdaQueryWrapper<TagInterfaceLogBean> query);
} 