package com.rfid.platform.controller;

import com.rfid.platform.persistence.RfidApiRequestDTO;
import com.rfid.platform.persistence.RfidApiResponseDTO;
import com.rfid.platform.persistence.storage.StorageDeviceValidateRequestDTO;
import com.rfid.platform.persistence.storage.StorageDeviceValidateResponseDTO;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "设备通知单结果校验", description = "提供给设备入库、出库、盘点通知单结果校验功能")
@RestController
@RequestMapping(value = "/rfid/dev")
public class StorageDeviceValidateController {

    @PostMapping(value = "/updateinbound")
    public RfidApiResponseDTO<StorageDeviceValidateResponseDTO> updateInbound(
            @ApiParam(value = "入库通知单扫描校验请求", required = true)
            @RequestBody RfidApiRequestDTO<StorageDeviceValidateRequestDTO> requestDTO) {
        return null;
    }

}
