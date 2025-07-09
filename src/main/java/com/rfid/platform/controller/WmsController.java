package com.rfid.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.common.PlatformConstant;
import com.rfid.platform.config.RfidPlatformProperties;
import com.rfid.platform.entity.AccountBean;
import com.rfid.platform.persistence.RfidApiRequestDTO;
import com.rfid.platform.persistence.RfidApiResponseDTO;
import com.rfid.platform.persistence.WmsLoginReqDTO;
import com.rfid.platform.persistence.WmsLoginRetDTO;
import com.rfid.platform.service.AccountService;
import com.rfid.platform.service.LoginLogService;
import com.rfid.platform.util.JwtUtil;
import com.rfid.platform.util.RequestUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/rfid")
public class WmsController {

    @Autowired
    private RfidPlatformProperties rfidPlatformProperties;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private LoginLogService loginLogService;

    @Autowired
    private AccountService accountService;



    @Operation(summary = "WMS登录", description = "账号密码，登录成功后返回访问令牌")
    @PostMapping(value = "/wms-login")
    public RfidApiResponseDTO<WmsLoginRetDTO> login(
            @Parameter(description = "WMS登录请求参数", required = true)
            @RequestBody RfidApiRequestDTO<WmsLoginReqDTO> request) {
        RfidApiResponseDTO<WmsLoginRetDTO> response = RfidApiResponseDTO.success();
        String clientIp = RequestUtil.getClientIpAddress();

        try {
            // 验证参数
            if (Objects.isNull(request.getData())) {
                response.setStatus(false);
                response.setMessage("登录参数不能为空");
                return response;
            }

            WmsLoginReqDTO wmsLoginReqDTO = request.getData();
            // 验证参数
            if (StringUtils.isBlank(wmsLoginReqDTO.getAppId())) {
                response.setStatus(false);
                response.setMessage("账号不能为空");
                return response;
            }

            if (StringUtils.isBlank(wmsLoginReqDTO.getAppSecret())) {
                response.setStatus(false);
                response.setMessage("密码不能为空");
                return response;
            }

            // 检查账号是否被锁定
            String lockKey = PlatformConstant.CACHE_KEY.ACCOUNT_LOCK + wmsLoginReqDTO.getAppId();
            Object lockTime = redisTemplate.opsForValue().get(lockKey);
            if (lockTime != null) {
                response.setStatus(false);
                response.setMessage("账号已被锁定，请30分钟后再试");
                // 异步记录登录日志
                loginLogService.recordLoginLogAsync(null, wmsLoginReqDTO.getAppId(), clientIp,
                        PlatformConstant.LOGIN_STATUS.LOCKED, "账号已被锁定", null);
                return response;
            }



            // 查询用户信息
            LambdaQueryWrapper<AccountBean> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AccountBean::getCode, wmsLoginReqDTO.getAppId());
            List<AccountBean> accounts = accountService.listAccount(queryWrapper);

            if (CollectionUtils.isEmpty(accounts)) {
                response.setStatus(false);
                response.setMessage("用户不存在");
                // 异步记录登录日志
                loginLogService.recordLoginLogAsync(null, wmsLoginReqDTO.getAppId(), clientIp,
                        PlatformConstant.LOGIN_STATUS.FAILED, "用户不存在", null);
                return response;
            }
            AccountBean account = accounts.get(0);
            if (account.getState() != null && account.getState() == 0) {
                response.setStatus(false);
                response.setMessage("用户已被禁用");
                // 异步记录登录日志
                loginLogService.recordLoginLogAsync(account.getId(), wmsLoginReqDTO.getAppId(), clientIp,
                        PlatformConstant.LOGIN_STATUS.FAILED, "用户已被禁用", null);
                return response;
            }

            // 验证密码
            if (!passwordEncoder.matches(wmsLoginReqDTO.getAppSecret(), account.getPassword())) {
                // 记录登录失败次数
                String failCountKey = PlatformConstant.CACHE_KEY.LOGIN_FAIL_COUNT + wmsLoginReqDTO.getAppId();
                Integer failCount = (Integer) redisTemplate.opsForValue().get(failCountKey);
                failCount = failCount == null ? 1 : failCount + 1;

                if (failCount >= PlatformConstant.LOGIN_CONFIG.MAX_LOGIN_FAIL_COUNT) {
                    // 锁定账号
                    redisTemplate.opsForValue().set(lockKey, System.currentTimeMillis(),
                            PlatformConstant.LOGIN_CONFIG.LOCK_DURATION_MINUTES, TimeUnit.MINUTES);
                    redisTemplate.delete(failCountKey); // 清除失败次数

                    response.setStatus(false);
                    response.setMessage("密码错误次数过多，账号已被锁定30分钟");
                    // 异步记录登录日志
                    loginLogService.recordLoginLogAsync(account.getId(), wmsLoginReqDTO.getAppId(), clientIp,
                            PlatformConstant.LOGIN_STATUS.LOCKED, "密码错误次数过多，账号被锁定", null);
                } else {
                    redisTemplate.opsForValue().set(failCountKey, failCount, 30, TimeUnit.MINUTES);
                    response.setStatus(false);
                    response.setMessage("密码错误，还可尝试" + (PlatformConstant.LOGIN_CONFIG.MAX_LOGIN_FAIL_COUNT - failCount) + "次");
                    // 异步记录登录日志
                    loginLogService.recordLoginLogAsync(account.getId(), wmsLoginReqDTO.getAppId(), clientIp,
                            PlatformConstant.LOGIN_STATUS.FAILED, "密码错误", null);
                }
                return response;
            }

            // 登录成功，清除失败次数
            String failCountKey = PlatformConstant.CACHE_KEY.LOGIN_FAIL_COUNT + wmsLoginReqDTO.getAppId();
            redisTemplate.delete(failCountKey);

            // 生成JWT token
            String accessToken = jwtUtil.generateTokenWithExpiration(account.getCode(), account.getId(), rfidPlatformProperties.getWmsTimeout());

            // 将token存储到Redis中，用于后续验证
            String tokenKey = PlatformConstant.CACHE_KEY.TOKEN_KEY + accessToken;
            redisTemplate.opsForValue().set(tokenKey, account.getId(), rfidPlatformProperties.getWmsTimeout(), TimeUnit.SECONDS);

            // 异步记录登录成功日志
            loginLogService.recordLoginLogAsync(account.getId(), account.getCode(), clientIp,
                    PlatformConstant.LOGIN_STATUS.SUCCESS, null, accessToken);

            // 构建返回结果
            WmsLoginRetDTO loginRetDTO = new WmsLoginRetDTO();
            loginRetDTO.setAccessToken(accessToken);
            loginRetDTO.setExpiresIn(rfidPlatformProperties.getWmsTimeout()); // 24小时
            response.setData(loginRetDTO);
            response.setMessage("登录成功");

        } catch (AuthenticationException e) {
            response.setStatus(false);
            response.setMessage("认证失败: " + e.getMessage());
            // 异步记录登录日志
            loginLogService.recordLoginLogAsync(null, Objects.nonNull(request.getData()) ? request.getData().getAppId() : "", clientIp,
                    PlatformConstant.LOGIN_STATUS.FAILED, "认证失败: " + e.getMessage(), null);
        } catch (Exception e) {
            response.setStatus(false);
            response.setMessage("登录失败: " + e.getMessage());
            // 异步记录登录日志
            loginLogService.recordLoginLogAsync(null, Objects.nonNull(request.getData()) ? request.getData().getAppId() : "", clientIp,
                    PlatformConstant.LOGIN_STATUS.FAILED, "登录失败: " + e.getMessage(), null);
        }

        return response;
    }


    /**
     * WMS退出登录
     */
    @Operation(summary = "WMS登出", description = "WMS登出")
    @PostMapping(value = "/wms-logout")
    public RfidApiResponseDTO<Boolean> wmsLogout() {
        RfidApiResponseDTO<Boolean> response = RfidApiResponseDTO.success();

        try {
            // 通过工具类获取token
            String accessToken = RequestUtil.getTokenFromHeader();

            // 验证token格式
            try {
                if (!jwtUtil.validateToken(accessToken)) {
                    response.setStatus(false);
                    response.setMessage("token无效");
                    response.setData(false);
                    return response;
                }
            } catch (Exception e) {
                response.setStatus(false);
                response.setMessage("token解析失败");
                response.setData(false);
                return response;
            }

            // 异步更新登出时间
            loginLogService.updateLogoutTimeAsync(accessToken);

            // 从Redis中删除token
            String tokenKey = PlatformConstant.CACHE_KEY.TOKEN_KEY + accessToken;
            redisTemplate.delete(tokenKey);

            response.setMessage("退出登录成功");
            response.setData(true);

        } catch (Exception e) {
            response.setStatus(false);
            response.setMessage("退出登录失败: " + e.getMessage());
            response.setData(false);
        }

        return response;
    }
}
