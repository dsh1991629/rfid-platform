package com.rfid.platform.service.impl;

import com.rfid.platform.entity.TagImportInfoBean;
import com.rfid.platform.mapper.TagImportInfoMapper;
import com.rfid.platform.service.TagImportInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagImportInfoServiceImpl extends ServiceImpl<TagImportInfoMapper, TagImportInfoBean> implements TagImportInfoService {

    @Override
    public boolean saveTagImportInfo(TagImportInfoBean entity) {
        return super.save(entity);
    }


    @Override
    public boolean saveTagImportInfos(List<TagImportInfoBean> tagImportInfoBeans) {
        return super.saveBatch(tagImportInfoBeans, 100);
    }
}