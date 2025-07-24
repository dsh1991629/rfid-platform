package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rfid.platform.entity.TagStorageBoxBean;
import com.rfid.platform.mapper.TagStorageBoxMapper;
import com.rfid.platform.service.TagStorageBoxService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TagStorageBoxServiceImpl extends ServiceImpl<TagStorageBoxMapper, TagStorageBoxBean> implements TagStorageBoxService {


    @Override
    public List<TagStorageBoxBean> queryTagStorageBoxByOrderRmsNo(String orderRmsNo) {
        LambdaQueryWrapper<TagStorageBoxBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(TagStorageBoxBean::getOrderNoRms, orderRmsNo);
        return super.list(queryWrapper);
    }


    @Override
    public boolean updateTagStorageBox(TagStorageBoxBean tagStorageBoxBean) {
        return super.updateById(tagStorageBoxBean);
    }

    @Override
    public boolean addTagStorageBoxes(List<TagStorageBoxBean> tagStorageBoxBeans) {
        return super.saveBatch(tagStorageBoxBeans);
    }

    @Override
    public boolean removeTagStorageBoxes(List<String> boxCodes) {
        LambdaQueryWrapper<TagStorageBoxBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.in(TagStorageBoxBean::getBoxCode, boxCodes);
        return super.remove(queryWrapper);
    }
}
