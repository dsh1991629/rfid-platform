package com.rfid.platform.service;

import com.rfid.platform.entity.TagImportInfoBean;

import java.util.List;

public interface TagImportInfoService {

    boolean saveTagImportInfo(TagImportInfoBean entity);

    boolean saveTagImportInfos(List<TagImportInfoBean> tagImportInfoBeans);
} 