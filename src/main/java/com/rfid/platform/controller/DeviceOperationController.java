package com.rfid.platform.controller;

import com.rfid.platform.common.PlatformConstant;
import com.rfid.platform.config.RfidPlatformProperties;
import com.rfid.platform.entity.AccountBean;
import com.rfid.platform.entity.DeviceAccountRelBean;
import com.rfid.platform.persistence.DeviceLoginReqDTO;
import com.rfid.platform.persistence.DeviceLoginRetDTO;
import com.rfid.platform.persistence.RfidApiRequestDTO;
import com.rfid.platform.persistence.RfidApiResponseDTO;
import com.rfid.platform.service.AccountService;
import com.rfid.platform.service.DeviceAccountRelService;
import com.rfid.platform.service.DeviceHeartbeatService;
import com.rfid.platform.service.LoginLogService;
import com.rfid.platform.util.JwtUtil;
import com.rfid.platform.util.RequestUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "设备操作", description = "提供给设备登录，登出，心跳功能")
@RestController
@RequestMapping(value = "/rfid/dev")
public class DeviceOperationController {

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

    @Autowired
    private DeviceAccountRelService deviceAccountRelService;

    @Autowired
    private DeviceHeartbeatService deviceHeartbeatService;




    @Operation(summary = "设备登录", description = "账号密码设备编码，登录成功后返回访问令牌")
    @PostMapping(value = "/login")
    public RfidApiResponseDTO<DeviceLoginRetDTO> deviceLogin(
            @Parameter(description = "设备登录请求参数", required = true)
            @RequestBody RfidApiRequestDTO<DeviceLoginReqDTO> request) {
        RfidApiResponseDTO<DeviceLoginRetDTO> response = RfidApiResponseDTO.success();
        String clientIp = RequestUtil.getClientIpAddress();

        try {
            // 验证参数
            if (Objects.isNull(request.getData())) {
                response.setStatus(false);
                response.setMessage("登录参数不能为空");
                return response;
            }

            DeviceLoginReqDTO deviceLoginReqDTO = request.getData();

            if (StringUtils.isBlank(deviceLoginReqDTO.getPassword())) {
                response.setStatus(false);
                response.setMessage("密码不能为空");
                return response;
            }

            if (StringUtils.isBlank(deviceLoginReqDTO.getDeviceCode())) {
                response.setStatus(false);
                response.setMessage("设备编码不能为空");
                return response;
            }

            DeviceAccountRelBean deviceAccountRelBean = deviceAccountRelService.queryBindingRel(deviceLoginReqDTO.getDeviceCode(), deviceLoginReqDTO.getAccount());
            if (Objects.isNull(deviceAccountRelBean)) {
                response.setStatus(false);
                response.setMessage("设备与账户没有绑定");
                return response;
            }

            // 查询登录数量
            Long loginNums = deviceHeartbeatService.queryLoginNums(deviceLoginReqDTO.getDeviceCode(), rfidPlatformProperties.getDeviceTimeout());
            if (loginNums.intValue() >= deviceAccountRelBean.getRepeatTimes()) {
                response.setStatus(false);
                response.setMessage("设备登录次数超限");
                return response;
            }

            String lockKey = PlatformConstant.CACHE_KEY.ACCOUNT_LOCK + deviceLoginReqDTO.getAccount();

            // 查询用户信息
            List<AccountBean> accounts = accountService.listAccountByCode(deviceLoginReqDTO.getAccount());

            if (CollectionUtils.isEmpty(accounts)) {
                response.setStatus(false);
                response.setMessage("用户不存在");
                // 异步记录登录日志
                loginLogService.recordLoginLogAsync(null, deviceLoginReqDTO.getAccount(), clientIp,
                        PlatformConstant.LOGIN_STATUS.FAILED, "用户不存在", null);
                return response;
            }
            AccountBean account = accounts.get(0);
            if (account.getState() != null && account.getState() == 0) {
                response.setStatus(false);
                response.setMessage("用户已被禁用");
                // 异步记录登录日志
                loginLogService.recordLoginLogAsync(account.getId(), deviceLoginReqDTO.getAccount(), clientIp,
                        PlatformConstant.LOGIN_STATUS.FAILED, "用户已被禁用", null);
                return response;
            }

            // 验证密码
            if (!passwordEncoder.matches(deviceLoginReqDTO.getPassword(), account.getPassword())) {
                // 记录登录失败次数
                String failCountKey = PlatformConstant.CACHE_KEY.LOGIN_FAIL_COUNT + deviceLoginReqDTO.getAccount();
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
                    loginLogService.recordLoginLogAsync(account.getId(), deviceLoginReqDTO.getAccount(), clientIp,
                            PlatformConstant.LOGIN_STATUS.LOCKED, "密码错误次数过多，账号被锁定", null);
                } else {
                    redisTemplate.opsForValue().set(failCountKey, failCount, 30, TimeUnit.MINUTES);
                    response.setStatus(false);;
                    response.setMessage("密码错误，还可尝试" + (PlatformConstant.LOGIN_CONFIG.MAX_LOGIN_FAIL_COUNT - failCount) + "次");
                    // 异步记录登录日志
                    loginLogService.recordLoginLogAsync(account.getId(), deviceLoginReqDTO.getAccount(), clientIp,
                            PlatformConstant.LOGIN_STATUS.FAILED, "密码错误", null);
                }
                return response;
            }

            // 登录成功，清除失败次数
            String failCountKey = PlatformConstant.CACHE_KEY.LOGIN_FAIL_COUNT + deviceLoginReqDTO.getAccount();
            redisTemplate.delete(failCountKey);

            // 生成JWT token, 默认失效一个月
            String accessToken = jwtUtil.generateTokenWithExpiration(account.getCode(), account.getId(), rfidPlatformProperties.getDeviceTimeout());

            // 将token存储到Redis中，用于后续验证
            String tokenKey = PlatformConstant.CACHE_KEY.TOKEN_KEY + accessToken;
            redisTemplate.opsForValue().set(tokenKey, account.getId(), rfidPlatformProperties.getDeviceTimeout(), TimeUnit.SECONDS);

            // 异步记录登录成功日志
            loginLogService.recordLoginLogAsync(account.getId(), account.getCode(), clientIp,
                    PlatformConstant.LOGIN_STATUS.SUCCESS, null, accessToken);

            deviceHeartbeatService.addLoginHeartBeat(deviceLoginReqDTO.getDeviceCode(), accessToken, request.getTimeStamp());

            // 构建返回结果
            DeviceLoginRetDTO loginRetDTO = new DeviceLoginRetDTO();
            loginRetDTO.setAccessToken(accessToken);
            response.setData(loginRetDTO);
            response.setMessage("登录成功");

        } catch (AuthenticationException e) {
            response.setStatus(false);
            response.setMessage("认证失败: " + e.getMessage());
            // 异步记录登录日志
            loginLogService.recordLoginLogAsync(null, Objects.nonNull(request) ? request.getData().getAccount() : "", clientIp,
                    PlatformConstant.LOGIN_STATUS.FAILED, "认证失败: " + e.getMessage(), null);
        } catch (Exception e) {
            response.setStatus(false);
            response.setMessage("登录失败: " + e.getMessage());
            // 异步记录登录日志
            loginLogService.recordLoginLogAsync(null, Objects.nonNull(request) ? request.getData().getAccount() : "", clientIp,
                    PlatformConstant.LOGIN_STATUS.FAILED, "登录失败: " + e.getMessage(), null);
        }

        return response;
    }



    /**
     * 设备退出登录
     */
    @Operation(summary = "设备登出", description = "设备登出")
    @PostMapping(value = "/logout")
    public RfidApiResponseDTO<Boolean> deviceLogout(@RequestBody RfidApiRequestDTO requestDTO) {
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

            // 更新心跳表记录
            deviceHeartbeatService.addLogoutHeartBeat(accessToken, requestDTO.getTimeStamp());

            response.setMessage("退出登录成功");
            response.setData(true);

        } catch (Exception e) {
            response.setStatus(false);
            response.setMessage("退出登录失败: " + e.getMessage());
            response.setData(false);
        }

        return response;
    }



    @Operation(summary = "设备心跳接口", description = "更新设备心跳接口")
    @PostMapping(value = "/heartbeat")
    public RfidApiResponseDTO<Boolean> deviceHeartbeat(@RequestBody RfidApiRequestDTO requestDTO) {
        RfidApiResponseDTO<Boolean> result = RfidApiResponseDTO.success();

        // 通过工具类获取token
        String accessToken = RequestUtil.getTokenFromHeader();
        boolean success = deviceHeartbeatService.addDeviceHeartbeat(accessToken, requestDTO.getTimeStamp());
        result.setData(success);
        return result;
    }

}
