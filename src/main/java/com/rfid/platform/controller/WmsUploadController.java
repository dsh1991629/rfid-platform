package com.rfid.platform.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "WMS上传操作", description = "将通知单结果推送到WMS")
@RestController
@RequestMapping(value = "/rfid")
public class WmsUploadController {



}
