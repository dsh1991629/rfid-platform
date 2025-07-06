package com.rfid.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.TagStorageOperationBean;
import com.rfid.platform.persistence.StorageOperationDetailDTO;
import java.util.List;

public interface TagStorageOperationService {

    boolean updateTagStorageOperationByPk(TagStorageOperationBean entity);

    List<TagStorageOperationBean> listTagStorageOperation(LambdaQueryWrapper<TagStorageOperationBean> query);

    boolean updateTagStorageOperationPartiallyByNoticeNo(String noticeNo, Integer state);

    boolean saveTagStorageOperations(List<TagStorageOperationBean> tagStorageOperationBeans);

    boolean updateTagStorageOperationsByNoticeNoAndSku(String noticeNo);

    List<TagStorageOperationBean> listTagStorageOperationSkuByNotice(String noticeNo);
}