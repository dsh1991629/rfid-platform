package com.rfid.platform.controller;

import com.rfid.platform.common.PlatformConstant;
import com.rfid.platform.config.RfidPlatformProperties;
import com.rfid.platform.entity.AccountBean;
import com.rfid.platform.persistence.RfidApiRequestDTO;
import com.rfid.platform.persistence.RfidApiResponseDTO;
import com.rfid.platform.persistence.WmsLoginReqDTO;
import com.rfid.platform.persistence.WmsLoginRetDTO;
import com.rfid.platform.persistence.storage.CancelInBoundOrderRequestDTO;
import com.rfid.platform.persistence.storage.CancelInBoundOrderResponseDTO;
import com.rfid.platform.persistence.storage.CancelInventoryOrderRequestDTO;
import com.rfid.platform.persistence.storage.CancelInventoryOrderResponseDTO;
import com.rfid.platform.persistence.storage.CancelOutBoundOrderRequestDTO;
import com.rfid.platform.persistence.storage.CancelOutBoundOrderResponseDTO;
import com.rfid.platform.persistence.storage.InBoundOrderRequestDTO;
import com.rfid.platform.persistence.storage.InBoundOrderResponseDTO;
import com.rfid.platform.persistence.storage.InventoryOrderRequestDTO;
import com.rfid.platform.persistence.storage.InventoryOrderResponseDTO;
import com.rfid.platform.persistence.storage.OutBoundOrderRequestDTO;
import com.rfid.platform.persistence.storage.OutBoundOrderResponseDTO;
import com.rfid.platform.service.AccountService;
import com.rfid.platform.service.LoginLogService;
import com.rfid.platform.service.TagStorageOrderService;
import com.rfid.platform.util.JwtUtil;
import com.rfid.platform.util.RequestUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
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

@RestController
@RequestMapping(value = "/rfid")
@Tag(name = "WMS到RMS接口", description = "WMS系统与RMS系统之间的数据交互接口")
public class Wms2RmsController {

    @Autowired
    private RfidPlatformProperties rfidPlatformProperties;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private LoginLogService loginLogService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TagStorageOrderService tagStorageOrderService;


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
            List<AccountBean> accounts = accountService.listAccountByCode(wmsLoginReqDTO.getAppId());

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


    @Operation(summary = "发送入库通知单", description = "WMS发送入库通知单到RMS系统")
    @PostMapping(value = "/send-inboundorder")
    public RfidApiResponseDTO<InBoundOrderResponseDTO> sendInBoundOrder(
            @Parameter(description = "入库通知单请求数据", required = true)
            @RequestBody RfidApiRequestDTO<InBoundOrderRequestDTO> requestDTO) {
        RfidApiResponseDTO<InBoundOrderResponseDTO> response = RfidApiResponseDTO.success();

        if (Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getData())) {
            response.setStatus(false);
            response.setMessage("入库通知单不存在");
            return response;
        }

        InBoundOrderRequestDTO inBoundOrderRequestDTO = requestDTO.getData();
        if (StringUtils.isBlank(inBoundOrderRequestDTO.getOrderNoWMS())) {
            response.setStatus(false);
            response.setMessage("WMS入库通知单号不存在");
            return response;
        }

        if (StringUtils.isBlank(inBoundOrderRequestDTO.getOrderNoERP())) {
            response.setStatus(false);
            response.setMessage("ERP入库通知单号不存在");
            return response;
        }

        if (StringUtils.isBlank(inBoundOrderRequestDTO.getOrderType())) {
            response.setStatus(false);
            response.setMessage("入库通知单类型不存在");
            return response;
        }

        if (StringUtils.isBlank(inBoundOrderRequestDTO.getWh())) {
            response.setStatus(false);
            response.setMessage("收获仓库不存在");
            return response;
        }

        String orderNoRMS = tagStorageOrderService.saveInboundTagStorageOrder(requestDTO.getTimeStamp(), inBoundOrderRequestDTO);
        InBoundOrderResponseDTO inBoundOrderResponseDTO = new InBoundOrderResponseDTO();
        inBoundOrderResponseDTO.setOrderNoRMS(orderNoRMS);
        response.setData(inBoundOrderResponseDTO);
        return response;
    }


    @Operation(summary = "取消入库通知单", description = "取消已创建的入库通知单")
    @PostMapping(value = "/cancel-inboundorder")
    public RfidApiResponseDTO<CancelInBoundOrderResponseDTO> cancelInBoundOrder(
            @Parameter(description = "取消入库通知单请求数据", required = true)
            @RequestBody RfidApiRequestDTO<CancelInBoundOrderRequestDTO> requestDTO) {
        RfidApiResponseDTO<CancelInBoundOrderResponseDTO> response = RfidApiResponseDTO.success();

        if (Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getData())) {
            response.setStatus(false);
            response.setMessage("入库通知单不存在");
            return response;
        }

        CancelInBoundOrderRequestDTO cancelInBoundOrderRequestDTO = requestDTO.getData();
        if (StringUtils.isBlank(cancelInBoundOrderRequestDTO.getOrderNoWMS())) {
            response.setStatus(false);
            response.setMessage("WMS入库通知单号不存在");
            return response;
        }

        String orderNoWms = cancelInBoundOrderRequestDTO.getOrderNoWMS();
        boolean canCancel = tagStorageOrderService.checkStorageOrderCancelable(orderNoWms, PlatformConstant.STORAGE_ORDER_TYPE.IN_BOUND);
        if (!canCancel) {
            response.setStatus(false);
            response.setMessage("入库通知单不能被取消");
            return response;
        }

        String orderNoRms = tagStorageOrderService.cancelTagStorageOrder(requestDTO.getTimeStamp(), orderNoWms,
                PlatformConstant.STORAGE_ORDER_TYPE.IN_BOUND);

        CancelInBoundOrderResponseDTO storageOrderResponseDTO = new CancelInBoundOrderResponseDTO();
        storageOrderResponseDTO.setOrderNoRms(orderNoRms);
        response.setData(storageOrderResponseDTO);
        return response;
    }


    @Operation(summary = "发送出库通知单", description = "WMS发送出库通知单到RMS系统")
    @PostMapping(value = "/send-outboundorder")
    public RfidApiResponseDTO<OutBoundOrderResponseDTO> sendOutBoundOrder(
            @Parameter(description = "出库通知单请求数据", required = true)
            @RequestBody RfidApiRequestDTO<OutBoundOrderRequestDTO> requestDTO) {
        RfidApiResponseDTO<OutBoundOrderResponseDTO> response = RfidApiResponseDTO.success();

        if (Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getData())) {
            response.setStatus(false);
            response.setMessage("出库通知单不存在");
            return response;
        }

        OutBoundOrderRequestDTO outBoundOrderRequestDTO = requestDTO.getData();
        if (StringUtils.isBlank(outBoundOrderRequestDTO.getOrderNoWMS())) {
            response.setStatus(false);
            response.setMessage("WMS出库通知单号不存在");
            return response;
        }

        if (StringUtils.isBlank(outBoundOrderRequestDTO.getOrderNoERP())) {
            response.setStatus(false);
            response.setMessage("ERP出库通知单号不存在");
            return response;
        }

        if (StringUtils.isBlank(outBoundOrderRequestDTO.getOrderType())) {
            response.setStatus(false);
            response.setMessage("出库通知单类型不存在");
            return response;
        }

        if (StringUtils.isBlank(outBoundOrderRequestDTO.getWh())) {
            response.setStatus(false);
            response.setMessage("出库仓库不存在");
            return response;
        }

        String orderNoRms = tagStorageOrderService.saveOutboundTagStorageOrder(requestDTO.getTimeStamp(), outBoundOrderRequestDTO);
        OutBoundOrderResponseDTO outBoundOrderResponseDTO = new OutBoundOrderResponseDTO();
        outBoundOrderResponseDTO.setOrderNoRMS(orderNoRms);
        response.setData(outBoundOrderResponseDTO);
        return response;
    }


    @Operation(summary = "取消出库通知单", description = "取消已创建的出库通知单")
    @PostMapping(value = "/cancel-outboundorder")
    public RfidApiResponseDTO<CancelOutBoundOrderResponseDTO> cancelOutBoundOrder(
            @Parameter(description = "取消出库通知单请求数据", required = true)
            @RequestBody RfidApiRequestDTO<CancelOutBoundOrderRequestDTO> requestDTO) {
        RfidApiResponseDTO<CancelOutBoundOrderResponseDTO> response = RfidApiResponseDTO.success();

        if (Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getData())) {
            response.setStatus(false);
            response.setMessage("出库通知单不存在");
            return response;
        }

        CancelOutBoundOrderRequestDTO cancelOutBoundOrderRequestDTO = requestDTO.getData();
        if (StringUtils.isBlank(cancelOutBoundOrderRequestDTO.getOrderNoWMS())) {
            response.setStatus(false);
            response.setMessage("出库通知单号不存在");
            return response;
        }

        String orderNoWMS = cancelOutBoundOrderRequestDTO.getOrderNoWMS();
        boolean canCancel = tagStorageOrderService.checkStorageOrderCancelable(orderNoWMS, PlatformConstant.STORAGE_ORDER_TYPE.OUT_BOUND);
        if (!canCancel) {
            response.setStatus(false);
            response.setMessage("出库通知单不能被取消");
            return response;
        }

        String orderNoRms = tagStorageOrderService.cancelTagStorageOrder(requestDTO.getTimeStamp(), orderNoWMS, PlatformConstant.STORAGE_ORDER_TYPE.OUT_BOUND);

        CancelOutBoundOrderResponseDTO storageOrderResponseDTO = new CancelOutBoundOrderResponseDTO();
        storageOrderResponseDTO.setOrderNoRms(orderNoRms);
        response.setData(storageOrderResponseDTO);
        return response;
    }


    @Operation(summary = "发送盘点通知单", description = "创建并发送盘点通知单到RMS系统")
    @PostMapping(value = "/send-inventoryorder")
    public RfidApiResponseDTO<InventoryOrderResponseDTO> sendInventoryOrder(
            @Parameter(description = "盘点通知单请求数据", required = true)
            @RequestBody RfidApiRequestDTO<InventoryOrderRequestDTO> requestDTO) {
        RfidApiResponseDTO<InventoryOrderResponseDTO> response = RfidApiResponseDTO.success();

        if (Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getData())) {
            response.setStatus(false);
            response.setMessage("盘点通知单不存在");
            return response;
        }

        String orderNoRms = tagStorageOrderService.saveInventoryTagStorageOrder(requestDTO.getTimeStamp(), requestDTO.getData());
        InventoryOrderResponseDTO inventoryOrderResponseDTO = new InventoryOrderResponseDTO();
        inventoryOrderResponseDTO.setOrderNoRMS(orderNoRms);
        response.setData(inventoryOrderResponseDTO);
        return response;
    }


    @Operation(summary = "取消盘点通知单", description = "取消已创建的盘点通知单")
    @PostMapping(value = "/cancel-inventoryorder")
    public RfidApiResponseDTO<CancelInventoryOrderResponseDTO> cancelInventoryOrder(
            @Parameter(description = "取消盘点通知单请求数据", required = true)
            @RequestBody RfidApiRequestDTO<CancelInventoryOrderRequestDTO> requestDTO) {
        RfidApiResponseDTO<CancelInventoryOrderResponseDTO> response = RfidApiResponseDTO.success();

        if (Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getData())) {
            response.setStatus(false);
            response.setMessage("盘点通知单不存在");
            return response;
        }

        CancelInventoryOrderRequestDTO cancelInventoryOrderRequestDTO = requestDTO.getData();
        if (StringUtils.isBlank(cancelInventoryOrderRequestDTO.getOrderNoWMS()) && StringUtils.isBlank(cancelInventoryOrderRequestDTO.getOrderNoRMS())) {
            response.setStatus(false);
            response.setMessage("盘点通知单号不存在");
            return response;
        }

        String orderNoWms = cancelInventoryOrderRequestDTO.getOrderNoWMS();
        String orderNoRms = cancelInventoryOrderRequestDTO.getOrderNoRMS();
        boolean canCancel = tagStorageOrderService.checkInventoryOrderCancelable(orderNoWms, orderNoRms, PlatformConstant.STORAGE_ORDER_TYPE.INVENTORY_BOUND);
        if (!canCancel) {
            response.setStatus(false);
            response.setMessage("盘点通知单不能被取消");
            return response;
        }

        String retOrderNoRms = tagStorageOrderService.cancelInventoryTagStorageOrder(requestDTO.getTimeStamp(),
                orderNoWms, orderNoRms, PlatformConstant.STORAGE_ORDER_TYPE.INVENTORY_BOUND);

        CancelInventoryOrderResponseDTO storageOrderResponseDTO = new CancelInventoryOrderResponseDTO();
        storageOrderResponseDTO.setOrderNoRms(retOrderNoRms);
        response.setData(storageOrderResponseDTO);
        return response;
    }

}
