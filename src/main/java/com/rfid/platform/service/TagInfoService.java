package com.rfid.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rfid.platform.entity.TagInfoBean;

import java.util.List;
import java.util.Set;

public interface TagInfoService {

    boolean saveTagInfo(TagInfoBean entity);

    List<TagInfoBean> listTagInfoByEpcCodes(Set<String> requestEpcCodes);

    boolean updateTagInfoStorageStateByEpcs(List<String> epcs, int state);

    IPage<TagInfoBean> pageTagInfo(Page<TagInfoBean> page, LambdaQueryWrapper<TagInfoBean> queryWrapper);

    boolean updateTagInfo(TagInfoBean tagInfoBean);

    boolean existTagInfo(LambdaQueryWrapper<TagInfoBean> nameCheckWrapper);

    boolean deleteTagInfo(Long id);
}