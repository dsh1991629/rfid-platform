package com.rfid.platform.service;

import com.rfid.platform.entity.TagInfoBean;

import java.util.List;
import java.util.Set;

public interface TagInfoService {

    boolean saveTagInfo(TagInfoBean entity);

    List<TagInfoBean> listTagInfoByEpcCodes(Set<String> requestEpcCodes);

    boolean updateTagInfoStorageStateByEpcs(List<String> epcs, int state);
}