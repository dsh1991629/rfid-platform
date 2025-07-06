package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rfid.platform.common.PlatformConstant;
import com.rfid.platform.entity.TagStorageOperationBean;
import com.rfid.platform.mapper.TagStorageOperationMapper;
import com.rfid.platform.service.TagStorageOperationService;
import com.rfid.platform.util.TimeUtil;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

@Service
public class TagStorageOperationServiceImpl extends ServiceImpl<TagStorageOperationMapper, TagStorageOperationBean> implements TagStorageOperationService {


    @Override
    public boolean updateTagStorageOperationByPk(TagStorageOperationBean entity) {
        return super.updateById(entity);
    }

    @Override
    public java.util.List<TagStorageOperationBean> listTagStorageOperation(LambdaQueryWrapper<TagStorageOperationBean> query) {
        return super.list(query);
    }

    @Override
    public boolean updateTagStorageOperationPartiallyByNoticeNo(String noticeNo, Integer state) {
        LambdaQueryWrapper<TagStorageOperationBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(TagStorageOperationBean::getNoticeNo, noticeNo);
        TagStorageOperationBean tagStorageOperationBean = super.getOne(queryWrapper);
        if (Objects.nonNull(tagStorageOperationBean)) {
            tagStorageOperationBean.setState(state);
            return super.updateById(tagStorageOperationBean);
        }
        return false;
    }

    @Override
    public boolean saveTagStorageOperations(List<TagStorageOperationBean> tagStorageOperationBeans) {
        return super.saveBatch(tagStorageOperationBeans);
    }

    @Override
    public boolean updateTagStorageOperationsByNoticeNoAndSku(String noticeNo) {
        LambdaUpdateWrapper<TagStorageOperationBean> queryWrapper = Wrappers.lambdaUpdate();
        queryWrapper.eq(TagStorageOperationBean::getNoticeNo, noticeNo);
        queryWrapper.set(TagStorageOperationBean::getState, PlatformConstant.STORAGE_TASK_STATE.RUNNING);
        queryWrapper.set(TagStorageOperationBean::getStartTime, TimeUtil.getSysDate());
        return super.update(queryWrapper);
    }

    @Override
    public List<TagStorageOperationBean> listTagStorageOperationSkuByNotice(String noticeNo) {
        LambdaQueryWrapper<TagStorageOperationBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(TagStorageOperationBean::getNoticeNo, noticeNo);
        List<TagStorageOperationBean> tagStorageOperationBeans = super.list(queryWrapper);
        if (CollectionUtils.isEmpty(tagStorageOperationBeans)) {
            return List.of();
        }
        return tagStorageOperationBeans;
    }
}
