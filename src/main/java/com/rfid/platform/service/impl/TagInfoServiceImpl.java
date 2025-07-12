package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.rfid.platform.entity.TagInfoBean;
import com.rfid.platform.mapper.TagInfoMapper;
import com.rfid.platform.service.TagInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class TagInfoServiceImpl extends ServiceImpl<TagInfoMapper, TagInfoBean> implements TagInfoService {

    @Override
    public boolean saveTagInfo(TagInfoBean entity) {
        return super.save(entity);
    }

    @Override
    public List<TagInfoBean> listTagInfoByEpcCodes(Set<String> requestEpcCodes) {
        LambdaQueryWrapper<TagInfoBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.in(TagInfoBean::getEpc, requestEpcCodes);
        return super.list(queryWrapper);
    }

    @Override
    public boolean updateTagInfoStorageStateByEpcs(List<String> epcs, int state) {
        LambdaUpdateWrapper<TagInfoBean> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(TagInfoBean::getStorageState, state);
        updateWrapper.in(TagInfoBean::getEpc, epcs);
        return super.update(updateWrapper);
    }
}