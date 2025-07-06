package com.rfid.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.rfid.platform.entity.TagInfoBean;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import java.util.Set;

public interface TagInfoService {

    boolean saveTagInfo(TagInfoBean entity);

    boolean updateTagInfoByPk(TagInfoBean entity);

    TagInfoBean getTagInfoByPk(Long id);

    IPage<TagInfoBean> pageTagInfo(Page<TagInfoBean> page, LambdaQueryWrapper<TagInfoBean> query);

    List<TagInfoBean> listTagInfo(LambdaQueryWrapper<TagInfoBean> query);

    boolean updateTagInfos(List<TagInfoBean> validatedTagInfoBeans);

    List<TagInfoBean> listTagInfoByEpcCodes(Set<String> requestEpcCodes);

    Long countTagInfosBySkuAndStorageState(String skuCode, int storageState);

    boolean updateTagInfoStorageStateBySkuCodes(List<String> skuCodeUpdates, int beforeState,  int afterState);
}