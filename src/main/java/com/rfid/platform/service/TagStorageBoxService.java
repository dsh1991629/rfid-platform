package com.rfid.platform.service;

import com.rfid.platform.entity.TagStorageBoxBean;
import java.util.List;

public interface TagStorageBoxService {

    List<TagStorageBoxBean> queryTagStorageBoxByOrderRmsNo(String orderRmsNo);

    boolean updateTagStorageBox(TagStorageBoxBean tagStorageBoxBean);

    boolean addTagStorageBoxes(List<TagStorageBoxBean> tagStorageBoxBeans);

    boolean removeTagStorageBoxes(List<String> boxCodes);
}
