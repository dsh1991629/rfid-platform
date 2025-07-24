package com.rfid.platform.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.rfid.platform.common.PlatformConstant;
import com.rfid.platform.config.PlatformRestProperties;
import com.rfid.platform.config.RfidPlatformProperties;
import com.rfid.platform.entity.AccountBean;
import com.rfid.platform.entity.DeviceAccountRelBean;
import com.rfid.platform.entity.TagStorageBoxBean;
import com.rfid.platform.entity.TagStorageOrderBean;
import com.rfid.platform.entity.TagStorageOrderDetailBean;
import com.rfid.platform.entity.TagStorageOrderResultBean;
import com.rfid.platform.persistence.DeviceLoginReqDTO;
import com.rfid.platform.persistence.DeviceLoginRetDTO;
import com.rfid.platform.persistence.RfidApiRequestDTO;
import com.rfid.platform.persistence.RfidApiResponseDTO;
import com.rfid.platform.persistence.storage.DevAddBoxRequestDTO;
import com.rfid.platform.persistence.storage.DevAddBoxResponseDTO;
import com.rfid.platform.persistence.storage.DevDelBoxRequestDTO;
import com.rfid.platform.persistence.storage.DevDelBoxResponseDTO;
import com.rfid.platform.persistence.storage.DevInBoundOrderQueryDetailItemProgressResponseDTO;
import com.rfid.platform.persistence.storage.DevInventoryOrderQueryDetailItemProgressResponseDTO;
import com.rfid.platform.persistence.storage.DevInventoryOrderQueryDetailItemResponseDTO;
import com.rfid.platform.persistence.storage.DevInventoryOrderQueryDetailResponseDTO;
import com.rfid.platform.persistence.storage.DevInventoryOrderQueryRequestDTO;
import com.rfid.platform.persistence.storage.DevInventoryOrderQueryResponseDTO;
import com.rfid.platform.persistence.storage.DevOutBoundOrderQueryDetailItemProgressResponseDTO;
import com.rfid.platform.persistence.storage.DevOutBoundOrderQueryDetailItemResponseDTO;
import com.rfid.platform.persistence.storage.DevOutBoundOrderQueryDetailResponseDTO;
import com.rfid.platform.persistence.storage.DevOutBoundOrderQueryRequestDTO;
import com.rfid.platform.persistence.storage.DevOutBoundOrderQueryResponseDTO;
import com.rfid.platform.persistence.storage.DevPrintInfoRequestDTO;
import com.rfid.platform.persistence.storage.DevPrintInfoResponseDTO;
import com.rfid.platform.persistence.storage.HeartBeatDTO;
import com.rfid.platform.persistence.storage.DevInBoundOrderQueryDetailResponseDTO;
import com.rfid.platform.persistence.storage.DevInBoundOrderQueryDetailItemResponseDTO;
import com.rfid.platform.persistence.storage.DevInBoundOrderQueryRequestDTO;
import com.rfid.platform.persistence.storage.DevInBoundOrderQueryResponseDTO;
import com.rfid.platform.service.AccountService;
import com.rfid.platform.service.DeviceAccountRelService;
import com.rfid.platform.service.DeviceHeartbeatService;
import com.rfid.platform.service.LoginLogService;
import com.rfid.platform.service.TagRestService;
import com.rfid.platform.service.TagStorageBoxService;
import com.rfid.platform.service.TagStorageOrderDetailService;
import com.rfid.platform.service.TagStorageOrderResultService;
import com.rfid.platform.service.TagStorageOrderService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Tag(name = "设备到RMS接口", description = "设备与RMS系统之间的数据交互接口")
@RestController
@RequestMapping(value = "/rfid")
public class Dev2RmsController {

    @Autowired
    private RfidPlatformProperties rfidPlatformProperties;

    @Autowired
    private PlatformRestProperties platformRestProperties;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private DeviceAccountRelService deviceAccountRelService;

    @Autowired
    private DeviceHeartbeatService deviceHeartbeatService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private LoginLogService loginLogService;

    @Autowired
    private TagStorageOrderService tagStorageOrderService;

    @Autowired
    private TagStorageOrderDetailService tagStorageOrderDetailService;

    @Autowired
    private TagStorageOrderResultService tagStorageOrderResultService;

    @Autowired
    private TagRestService tagRestService;

    @Autowired
    private TagStorageBoxService tagStorageBoxService;




    @Operation(summary = "设备登录", description = "账号密码设备编码，登录成功后返回访问令牌")
    @PostMapping(value = "/dev/login")
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

            if (StringUtils.isBlank(deviceLoginReqDTO.getDevCode())) {
                response.setStatus(false);
                response.setMessage("设备编码不能为空");
                return response;
            }

            DeviceAccountRelBean deviceAccountRelBean = deviceAccountRelService.queryBindingRel(deviceLoginReqDTO.getDevCode(), deviceLoginReqDTO.getUsername());
            if (Objects.isNull(deviceAccountRelBean)) {
                response.setStatus(false);
                response.setMessage("设备与账户没有绑定");
                return response;
            }

            // 查询登录数量
            Long loginNums = deviceHeartbeatService.queryLoginNums(deviceLoginReqDTO.getDevCode(), rfidPlatformProperties.getDeviceTimeout());
            if (loginNums.intValue() >= deviceAccountRelBean.getRepeatTimes()) {
                response.setStatus(false);
                response.setMessage("设备登录次数超限");
                return response;
            }

            String lockKey = PlatformConstant.CACHE_KEY.ACCOUNT_LOCK + deviceLoginReqDTO.getUsername();

            // 查询用户信息
            List<AccountBean> accounts = accountService.listAccountByCode(deviceLoginReqDTO.getUsername());

            if (CollectionUtils.isEmpty(accounts)) {
                response.setStatus(false);
                response.setMessage("用户不存在");
                // 异步记录登录日志
                loginLogService.recordLoginLogAsync(null, deviceLoginReqDTO.getUsername(), clientIp,
                        PlatformConstant.LOGIN_STATUS.FAILED, "用户不存在", null);
                return response;
            }
            AccountBean account = accounts.get(0);
            if (account.getState() != null && account.getState() == 0) {
                response.setStatus(false);
                response.setMessage("用户已被禁用");
                // 异步记录登录日志
                loginLogService.recordLoginLogAsync(account.getId(), deviceLoginReqDTO.getUsername(), clientIp,
                        PlatformConstant.LOGIN_STATUS.FAILED, "用户已被禁用", null);
                return response;
            }

            // 验证密码
            if (!passwordEncoder.matches(deviceLoginReqDTO.getPassword(), account.getPassword())) {
                // 记录登录失败次数
                String failCountKey = PlatformConstant.CACHE_KEY.LOGIN_FAIL_COUNT + deviceLoginReqDTO.getUsername();
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
                    loginLogService.recordLoginLogAsync(account.getId(), deviceLoginReqDTO.getUsername(), clientIp,
                            PlatformConstant.LOGIN_STATUS.LOCKED, "密码错误次数过多，账号被锁定", null);
                } else {
                    redisTemplate.opsForValue().set(failCountKey, failCount, 30, TimeUnit.MINUTES);
                    response.setStatus(false);
                    ;
                    response.setMessage("密码错误，还可尝试" + (PlatformConstant.LOGIN_CONFIG.MAX_LOGIN_FAIL_COUNT - failCount) + "次");
                    // 异步记录登录日志
                    loginLogService.recordLoginLogAsync(account.getId(), deviceLoginReqDTO.getUsername(), clientIp,
                            PlatformConstant.LOGIN_STATUS.FAILED, "密码错误", null);
                }
                return response;
            }

            // 登录成功，清除失败次数
            String failCountKey = PlatformConstant.CACHE_KEY.LOGIN_FAIL_COUNT + deviceLoginReqDTO.getUsername();
            redisTemplate.delete(failCountKey);

            // 生成JWT token, 默认失效一个月
            String accessToken = jwtUtil.generateTokenWithExpiration(account.getCode(), account.getId(), rfidPlatformProperties.getDeviceTimeout());

            // 将token存储到Redis中，用于后续验证
            String tokenKey = PlatformConstant.CACHE_KEY.TOKEN_KEY + accessToken;
            redisTemplate.opsForValue().set(tokenKey, account.getId(), rfidPlatformProperties.getDeviceTimeout(), TimeUnit.SECONDS);

            // 异步记录登录成功日志
            loginLogService.recordLoginLogAsync(account.getId(), account.getCode(), clientIp,
                    PlatformConstant.LOGIN_STATUS.SUCCESS, null, accessToken);

            deviceHeartbeatService.addLoginHeartBeat(deviceLoginReqDTO.getDevCode(), accessToken, request.getTimeStamp());

            // 构建返回结果
            DeviceLoginRetDTO loginRetDTO = new DeviceLoginRetDTO();
            loginRetDTO.setAccessToken(accessToken);
            loginRetDTO.setExpiresIn(rfidPlatformProperties.getDeviceTimeout()); // 2小时
            response.setData(loginRetDTO);
            response.setMessage("登录成功");

        } catch (AuthenticationException e) {
            response.setStatus(false);
            response.setMessage("认证失败: " + e.getMessage());
            // 异步记录登录日志
            loginLogService.recordLoginLogAsync(null, Objects.nonNull(request) ? request.getData().getUsername() : "", clientIp,
                    PlatformConstant.LOGIN_STATUS.FAILED, "认证失败: " + e.getMessage(), null);
        } catch (Exception e) {
            response.setStatus(false);
            response.setMessage("登录失败: " + e.getMessage());
            // 异步记录登录日志
            loginLogService.recordLoginLogAsync(null, Objects.nonNull(request) ? request.getData().getUsername() : "", clientIp,
                    PlatformConstant.LOGIN_STATUS.FAILED, "登录失败: " + e.getMessage(), null);
        }

        return response;
    }

    @Operation(summary = "设备心跳接口", description = "更新设备心跳接口")
    @PostMapping(value = "/dev/heartbeat")
    public RfidApiResponseDTO<Boolean> deviceHeartbeat(
            @Parameter(description = "设备心跳录请求参数", required = true)
            @RequestBody RfidApiRequestDTO<HeartBeatDTO> requestDTO) {
        RfidApiResponseDTO<Boolean> result = RfidApiResponseDTO.success();

        // 通过工具类获取token
        String accessToken = RequestUtil.getTokenFromHeader();
        boolean success = deviceHeartbeatService.addDeviceHeartbeat(accessToken, requestDTO.getTimeStamp(), requestDTO.getData());
        result.setData(success);
        return result;
    }


    @Operation(summary = "查询入库通知单", description = "设备查询入库通知单及其详情")
    @PostMapping(value = "/dev/getinboundorder")
    public RfidApiResponseDTO<DevInBoundOrderQueryResponseDTO> getInBoundOrder(
            @Parameter(description = "入库通知单查询请求", required = true)
            @RequestBody RfidApiRequestDTO<DevInBoundOrderQueryRequestDTO> requestDTO) {
        RfidApiResponseDTO<DevInBoundOrderQueryResponseDTO> response = RfidApiResponseDTO.success();

        if (Objects.isNull(requestDTO)) {
            response.setStatus(false);
            response.setMessage("入库通知单查询对象不存在");
            return response;
        }
        DevInBoundOrderQueryResponseDTO devInBoundOrderQueryResponseDTO = new DevInBoundOrderQueryResponseDTO();

        List<TagStorageOrderBean> tagStorageOrderBeans = tagStorageOrderService.queryActiveInBoundOrders(requestDTO.getData());
        if (CollectionUtils.isNotEmpty(tagStorageOrderBeans)) {
            List<DevInBoundOrderQueryDetailResponseDTO> details = tagStorageOrderBeans.stream().map(e -> {
                DevInBoundOrderQueryDetailResponseDTO detailDTO = new DevInBoundOrderQueryDetailResponseDTO();
                detailDTO.setOrderType(e.getOrderType());
                detailDTO.setOrderID_ERP(e.getOrderNoErp());
                detailDTO.setOrderID_WMS(e.getOrderNoWms());
                detailDTO.setOrderID_RMS(e.getOrderNoRms());

                List<TagStorageOrderDetailBean> tagStorageOrderDetailBeans = tagStorageOrderDetailService.listTagStorageOrderDetails(e.getOrderNoRms());
                // 按照productCode分组，遍历productCode，生成StorageCheckQueryOrderDetailItemDTO集合
                if (CollectionUtils.isNotEmpty(tagStorageOrderDetailBeans)) {
                    List<DevInBoundOrderQueryDetailItemResponseDTO> detailItemDTOS = new ArrayList<>();

                    for (TagStorageOrderDetailBean tagStorageOrderDetailBean : tagStorageOrderDetailBeans) {
                        DevInBoundOrderQueryDetailItemResponseDTO detailItemResponseDTO = new DevInBoundOrderQueryDetailItemResponseDTO();
                        detailItemResponseDTO.setProductCode(tagStorageOrderDetailBean.getProductCode());
                        detailItemResponseDTO.setSku(tagStorageOrderDetailBean.getSku());
                        detailItemResponseDTO.setQty(tagStorageOrderDetailBean.getQuantity());

                        List<TagStorageOrderResultBean> tagStorageOrderResultBeans = tagStorageOrderResultService.listTagStorageOrderResultsByOrderRmsAndProductCode(e.getOrderNoRms(), tagStorageOrderDetailBean.getProductCode());
                        DevInBoundOrderQueryDetailItemProgressResponseDTO detailItemProgressResponseDTO = new DevInBoundOrderQueryDetailItemProgressResponseDTO();
                        detailItemProgressResponseDTO.setQty(tagStorageOrderResultBeans.size());

                        // 从tagStorageOrderResultBeans中提取所有不重复的boxCode组成集合
                        List<String> boxCodes = tagStorageOrderResultBeans.stream()
                                .map(TagStorageOrderResultBean::getBoxCode)
                                .filter(Objects::nonNull)
                                .distinct()
                                .collect(Collectors.toList());

                        detailItemProgressResponseDTO.setBoxCnt(boxCodes.size());
                        detailItemProgressResponseDTO.setBoxCodes(boxCodes);
                        detailItemResponseDTO.setProgress(detailItemProgressResponseDTO);

                        detailItemDTOS.add(detailItemResponseDTO);
                    }

                    detailDTO.setDetails(detailItemDTOS);
                }

                return detailDTO;
            }).collect(Collectors.toUnmodifiableList());
            devInBoundOrderQueryResponseDTO.setOrders(details);
        }
        response.setData(devInBoundOrderQueryResponseDTO);
        return response;
    }


    @Operation(summary = "获取出库通知单", description = "设备查询出库通知单及其详情")
    @PostMapping(value = "/dev/getoutboundorder")
    public RfidApiResponseDTO<DevOutBoundOrderQueryResponseDTO> getOutBoundOrder(
            @Parameter(description = "出库通知单查询请求", required = true)
            @RequestBody RfidApiRequestDTO<DevOutBoundOrderQueryRequestDTO> requestDTO) {
        RfidApiResponseDTO<DevOutBoundOrderQueryResponseDTO> response = RfidApiResponseDTO.success();

        if (Objects.isNull(requestDTO)) {
            response.setStatus(false);
            response.setMessage("出库通知单查询对象不存在");
            return response;
        }
        DevOutBoundOrderQueryResponseDTO devOutBoundOrderQueryResponseDTO = new DevOutBoundOrderQueryResponseDTO();

        List<TagStorageOrderBean> tagStorageOrderBeans = tagStorageOrderService.queryActiveOutBoundOrders(requestDTO.getData());
        if (CollectionUtils.isNotEmpty(tagStorageOrderBeans)) {
            List<DevOutBoundOrderQueryDetailResponseDTO> details = tagStorageOrderBeans.stream().map(e -> {
                DevOutBoundOrderQueryDetailResponseDTO detailDTO = new DevOutBoundOrderQueryDetailResponseDTO();
                detailDTO.setOrderType(e.getOrderType());
                detailDTO.setOrderID_ERP(e.getOrderNoErp());
                detailDTO.setOrderID_WMS(e.getOrderNoWms());
                detailDTO.setOrderID_RMS(e.getOrderNoRms());

                List<TagStorageOrderDetailBean> tagStorageOrderDetailBeans = tagStorageOrderDetailService.listTagStorageOrderDetails(e.getOrderNoRms());
                // 按照productCode分组，遍历productCode，生成StorageCheckQueryOrderDetailItemDTO集合
                if (CollectionUtils.isNotEmpty(tagStorageOrderDetailBeans)) {
                    List<DevOutBoundOrderQueryDetailItemResponseDTO> detailItemDTOS = new ArrayList<>();

                    for (TagStorageOrderDetailBean tagStorageOrderDetailBean : tagStorageOrderDetailBeans) {
                        DevOutBoundOrderQueryDetailItemResponseDTO detailItemResponseDTO = new DevOutBoundOrderQueryDetailItemResponseDTO();
                        detailItemResponseDTO.setProductCode(tagStorageOrderDetailBean.getProductCode());
                        detailItemResponseDTO.setSku(tagStorageOrderDetailBean.getSku());
                        detailItemResponseDTO.setQty(tagStorageOrderDetailBean.getQuantity());

                        List<TagStorageOrderResultBean> tagStorageOrderResultBeans = tagStorageOrderResultService.listTagStorageOrderResultsByOrderRmsAndProductCode(e.getOrderNoRms(), tagStorageOrderDetailBean.getProductCode());
                        DevOutBoundOrderQueryDetailItemProgressResponseDTO detailItemProgressResponseDTO = new DevOutBoundOrderQueryDetailItemProgressResponseDTO();
                        detailItemProgressResponseDTO.setQty(tagStorageOrderResultBeans.size());

                        // 从tagStorageOrderResultBeans中提取所有不重复的boxCode组成集合
                        List<String> boxCodes = tagStorageOrderResultBeans.stream()
                                .map(TagStorageOrderResultBean::getBoxCode)
                                .filter(Objects::nonNull)
                                .distinct()
                                .collect(Collectors.toList());

                        detailItemProgressResponseDTO.setBoxCnt(boxCodes.size());
                        detailItemProgressResponseDTO.setBoxCodes(boxCodes);
                        detailItemResponseDTO.setProgress(detailItemProgressResponseDTO);

                        detailItemDTOS.add(detailItemResponseDTO);
                    }

                    detailDTO.setDetails(detailItemDTOS);
                }

                return detailDTO;
            }).collect(Collectors.toUnmodifiableList());
            devOutBoundOrderQueryResponseDTO.setOrders(details);
        }
        response.setData(devOutBoundOrderQueryResponseDTO);
        return response;
    }


    @Operation(summary = "获取盘点通知单", description = "设备查询活跃的盘点通知单及其详情")
    @PostMapping(value = "/dev/getInventoryorder")
    public RfidApiResponseDTO<DevInventoryOrderQueryResponseDTO> getInventoryOrder(
            @Parameter(description = "盘点通知单查询请求")
            @RequestBody RfidApiRequestDTO<DevInventoryOrderQueryRequestDTO> requestDTO) {
        RfidApiResponseDTO<DevInventoryOrderQueryResponseDTO> response = RfidApiResponseDTO.success();

        if (Objects.isNull(requestDTO)) {
            response.setStatus(false);
            response.setMessage("盘点通知单查询对象不存在");
            return response;
        }

        DevInventoryOrderQueryResponseDTO devInBoundOrderQueryResponseDTO = new DevInventoryOrderQueryResponseDTO();

        List<TagStorageOrderBean> tagStorageOrderBeans = tagStorageOrderService.queryActiveInventoryOrders(requestDTO.getData());
        if (CollectionUtils.isNotEmpty(tagStorageOrderBeans)) {
            List<DevInventoryOrderQueryDetailResponseDTO> details = tagStorageOrderBeans.stream().map(e -> {
                DevInventoryOrderQueryDetailResponseDTO detailDTO = new DevInventoryOrderQueryDetailResponseDTO();
                detailDTO.setOrderID_ERP(e.getOrderNoErp());
                detailDTO.setOrderID_WMS(e.getOrderNoWms());
                detailDTO.setOrderID_RMS(e.getOrderNoRms());

                List<TagStorageOrderDetailBean> tagStorageOrderDetailBeans = tagStorageOrderDetailService.listTagStorageOrderDetails(e.getOrderNoRms());
                // 按照productCode分组，遍历productCode，生成StorageCheckQueryOrderDetailItemDTO集合
                if (CollectionUtils.isNotEmpty(tagStorageOrderDetailBeans)) {
                    List<DevInventoryOrderQueryDetailItemResponseDTO> detailItemDTOS = new ArrayList<>();

                    for (TagStorageOrderDetailBean tagStorageOrderDetailBean : tagStorageOrderDetailBeans) {
                        DevInventoryOrderQueryDetailItemResponseDTO detailItemResponseDTO = new DevInventoryOrderQueryDetailItemResponseDTO();
                        detailItemResponseDTO.setProductCode(tagStorageOrderDetailBean.getProductCode());
                        detailItemResponseDTO.setSku(tagStorageOrderDetailBean.getSku());
                        detailItemResponseDTO.setQty(tagStorageOrderDetailBean.getQuantity());
                        detailItemResponseDTO.setBoxCnt(tagStorageOrderDetailBean.getBoxCnt());

                        List<TagStorageOrderResultBean> tagStorageOrderResultBeans = tagStorageOrderResultService.listTagStorageOrderResultsByOrderRmsAndProductCode(e.getOrderNoRms(), tagStorageOrderDetailBean.getProductCode());
                        DevInventoryOrderQueryDetailItemProgressResponseDTO detailItemProgressResponseDTO = new DevInventoryOrderQueryDetailItemProgressResponseDTO();
                        detailItemProgressResponseDTO.setQty(tagStorageOrderResultBeans.size());

                        // 从tagStorageOrderResultBeans中提取所有不重复的boxCode组成集合
                        List<String> boxCodes = tagStorageOrderResultBeans.stream()
                                .map(TagStorageOrderResultBean::getBoxCode)
                                .filter(Objects::nonNull)
                                .distinct()
                                .collect(Collectors.toList());

                        detailItemProgressResponseDTO.setBoxCnt(boxCodes.size());
                        detailItemResponseDTO.setProgress(detailItemProgressResponseDTO);

                        detailItemDTOS.add(detailItemResponseDTO);
                    }

                    detailDTO.setDetails(detailItemDTOS);
                }

                return detailDTO;
            }).collect(Collectors.toUnmodifiableList());

            devInBoundOrderQueryResponseDTO.setOrders(details);
        }
        response.setData(devInBoundOrderQueryResponseDTO);
        return response;
    }


    @Operation(summary = "查询打印信息", description = "查询打印信息")
    @PostMapping(value = "/dev/getprintinfo")
    public RfidApiResponseDTO<DevPrintInfoResponseDTO> getPrintInfo(
            @Parameter(description = "查询打印信息请求", required = true)
            @RequestBody RfidApiRequestDTO<DevPrintInfoRequestDTO> requestDTO) {

        RfidApiResponseDTO<DevPrintInfoResponseDTO> responseDTO = RfidApiResponseDTO.success();

        if (Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getData())) {
            responseDTO.setStatus(false);
            responseDTO.setMessage("打印信息对象不存在");
            return responseDTO;
        }

        DevPrintInfoRequestDTO devPrintInfoRequestDTO = requestDTO.getData();
        if (StringUtils.isBlank(devPrintInfoRequestDTO.getSku())) {
            responseDTO.setStatus(false);
            responseDTO.setMessage("SKU码不存在");
            return responseDTO;
        }

        String url = platformRestProperties.getGetPrintInfoUrl();

        responseDTO = tagRestService.executeRestPostOptions(url, requestDTO,
                new TypeReference<RfidApiResponseDTO<DevPrintInfoResponseDTO>>() {
                }
        );

        return responseDTO;
    }



    @Operation(summary = "创建箱", description = "创建箱")
    @PostMapping(value = "/dev/addbox")
    public RfidApiResponseDTO<DevAddBoxResponseDTO> addBox(
            @Parameter(description = "创建箱请求", required = true)
            @RequestBody RfidApiRequestDTO<DevAddBoxRequestDTO> requestDTO) {

        RfidApiResponseDTO<DevAddBoxResponseDTO> responseDTO = RfidApiResponseDTO.success();

        if (Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getData())) {
            responseDTO.setStatus(false);
            responseDTO.setMessage("创建箱对象不存在");
            return responseDTO;
        }

        DevAddBoxRequestDTO devAddBoxRequestDTO = requestDTO.getData();
        String orderNoRms = devAddBoxRequestDTO.getOrderID_RMS();
        if (StringUtils.isBlank(orderNoRms)) {
            responseDTO.setStatus(false);
            responseDTO.setMessage("RMS单号不存在");
            return responseDTO;
        }

        Integer boxCnt = devAddBoxRequestDTO.getBoxCnt();
        if (Objects.isNull(boxCnt)) {
            responseDTO.setStatus(false);
            responseDTO.setMessage("箱数量不存在");
            return responseDTO;
        }

        TagStorageOrderBean tagStorageOrderBean = tagStorageOrderService.queryTagStorageOrderByNo(orderNoRms);
        if (Objects.isNull(tagStorageOrderBean)) {
            responseDTO.setStatus(false);
            responseDTO.setMessage("RMS通知单不存在");
            return responseDTO;
        }

        int start = 1;
        List<String> boxCodes = new ArrayList<>();
        List<TagStorageBoxBean> tagStorageBoxBeans = tagStorageBoxService.queryTagStorageBoxByOrderRmsNo(orderNoRms);
        if (CollectionUtils.isNotEmpty(tagStorageBoxBeans)) {
            start = tagStorageBoxBeans.stream().mapToInt(TagStorageBoxBean::getBoxIdx).max().orElse(0) + 1;
        }

        List<TagStorageBoxBean> addBeans = new ArrayList<>();

        for (int i = start; i < start + boxCnt; i++) {
            String boxCode = orderNoRms + "_" + i;
            boxCodes.add(boxCode);

            TagStorageBoxBean addBean = new TagStorageBoxBean();
            addBean.setOrderNoRms(orderNoRms);
            addBean.setBoxCode(boxCode);
            addBean.setBoxIdx(i);
            addBeans.add(addBean);
        }
        tagStorageBoxService.addTagStorageBoxes(addBeans);

        DevAddBoxResponseDTO devAddBoxResponseDTO = new DevAddBoxResponseDTO();
        devAddBoxResponseDTO.setBoxCodes(boxCodes);
        responseDTO.setData(devAddBoxResponseDTO);
        return responseDTO;
    }


    @Operation(summary = "删除箱", description = "删除箱")
    @PostMapping(value = "/dev/delbox")
    public RfidApiResponseDTO<DevDelBoxResponseDTO> delBox(
            @Parameter(description = "删除箱请求", required = true)
            @RequestBody RfidApiRequestDTO<DevDelBoxRequestDTO> requestDTO) {

        RfidApiResponseDTO<DevDelBoxResponseDTO> responseDTO = RfidApiResponseDTO.success();

        if (Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getData())) {
            responseDTO.setStatus(false);
            responseDTO.setMessage("删除箱对象不存在");
            return responseDTO;
        }

        DevDelBoxRequestDTO devDelBoxRequestDTO = requestDTO.getData();
        List<String> boxCodes = devDelBoxRequestDTO.getBoxCodes();
        if (CollectionUtils.isEmpty(boxCodes)) {
            responseDTO.setStatus(false);
            responseDTO.setMessage("箱码不存在");
            return responseDTO;
        }

        for (String boxCode : boxCodes) {
            boolean existResult = tagStorageOrderResultService.existResultByBox(boxCode);
            if (existResult) {
                responseDTO.setStatus(false);
                responseDTO.setMessage("箱码: " +boxCode+ " 有上传记录，不能删除");
                return responseDTO;
            }
        }

        boolean ret = tagStorageBoxService.removeTagStorageBoxes(boxCodes);

        DevDelBoxResponseDTO devDelBoxResponseDTO = new DevDelBoxResponseDTO();
        responseDTO.setData(devDelBoxResponseDTO);
        return responseDTO;
    }



}
