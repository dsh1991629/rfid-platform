package com.rfid.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.TagInterfaceLogBean;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface TagInterfaceLogService {

    boolean saveTagInterfaceLog(TagInterfaceLogBean entity);

    boolean removeTagInterfaceLogByPk(Long id);

    boolean updateTagInterfaceLogByPk(TagInterfaceLogBean entity);

    TagInterfaceLogBean getTagInterfaceLogByPk(Long id);

    IPage<TagInterfaceLogBean> pageTagInterfaceLog(Page<TagInterfaceLogBean> page, LambdaQueryWrapper<TagInterfaceLogBean> query);
} 