package com.rfid.platform.controller;

import com.rfid.platform.common.BaseResult;
import com.rfid.platform.common.PlatformConstant;
import com.rfid.platform.config.RfidPlatformProperties;
import com.rfid.platform.persistence.CaptchaDTO;
import com.rfid.platform.persistence.LoginReqDTO;
import com.rfid.platform.persistence.LoginRetDTO;
import com.wf.captcha.SpecCaptcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/rfid")
public class LoginController {

    @Autowired
    private RfidPlatformProperties rfidPlatformProperties;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @PostMapping(value = "/captcha")
    public BaseResult<CaptchaDTO> captcha() {
        BaseResult<CaptchaDTO> response = new BaseResult<>();
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, rfidPlatformProperties.getCaptchaBit());
        String captcha = specCaptcha.text().toLowerCase();
        String key = UUID.randomUUID().toString();
        String image = specCaptcha.toBase64();

        redisTemplate.opsForValue().set(PlatformConstant.CACHE_KEY.CAPTCHA_KEY + key, captcha, 30, TimeUnit.SECONDS);

        CaptchaDTO captchaDTO = new CaptchaDTO();
        captchaDTO.setImage(image);
        captchaDTO.setKey(key);
        response.setData(captchaDTO);

        return response;
    }



    @PostMapping(value = "/login")
    public BaseResult<LoginRetDTO> login(@RequestBody LoginReqDTO loginReqDTO) {
        BaseResult<LoginRetDTO> response = new BaseResult<>();



        return response;
    }

}
