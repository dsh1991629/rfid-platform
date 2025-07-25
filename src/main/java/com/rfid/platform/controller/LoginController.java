package com.rfid.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.common.PlatformConstant;
import com.rfid.platform.config.RfidPlatformProperties;
import com.rfid.platform.entity.AccountBean;
import com.rfid.platform.persistence.CaptchaDTO;
import com.rfid.platform.persistence.DepartmentDTO;
import com.rfid.platform.persistence.ForgotPasswordReqDTO;
import com.rfid.platform.persistence.ResetPasswordReqDTO;
import com.rfid.platform.persistence.LoginReqDTO;
import com.rfid.platform.persistence.LoginRetDTO;
import com.rfid.platform.persistence.MenuDTO;
import com.rfid.platform.persistence.RfidApiRequestDTO;
import com.rfid.platform.persistence.RfidApiResponseDTO;
import com.rfid.platform.persistence.RoleDTO;
import com.rfid.platform.service.AccountService;
import com.rfid.platform.service.DepartmentService;
import com.rfid.platform.service.DeviceAccountRelService;
import com.rfid.platform.service.DeviceHeartbeatService;
import com.rfid.platform.service.LoginLogService;
import com.rfid.platform.service.MenuService;
import com.rfid.platform.service.RoleService;
import com.rfid.platform.util.JwtUtil;
import com.wf.captcha.SpecCaptcha;
import java.util.ArrayList;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.rfid.platform.persistence.ChangePasswordReqDTO;
import com.rfid.platform.util.RequestUtil;

/**
 * 登录控制器
 * 提供用户登录、注销、密码管理等功能
 */
@Tag(name = "登录管理", description = "用户登录、注销、密码管理相关接口")
@RestController
@RequestMapping(value = "/rfid")
public class LoginController {

    @Autowired
    private RfidPlatformProperties rfidPlatformProperties;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AccountService accountService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private LoginLogService loginLogService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DeviceAccountRelService deviceAccountRelService;

    @Autowired
    private DeviceHeartbeatService deviceHeartbeatService;



    
    @Operation(summary = "获取验证码", description = "生成图形验证码，用于登录时的安全验证")
    @PostMapping(value = "/captcha")
    public RfidApiResponseDTO<CaptchaDTO> captcha() {
        RfidApiResponseDTO<CaptchaDTO> response = RfidApiResponseDTO.success();
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


    @Operation(summary = "用户登录", description = "验证用户账号密码，登录成功后返回访问令牌和用户基本信息")
    @PostMapping(value = "/login")
    public RfidApiResponseDTO<LoginRetDTO> login(
            @Parameter(description = "登录请求参数", required = true)
            @RequestBody RfidApiRequestDTO<LoginReqDTO> requestDTO) {
        RfidApiResponseDTO<LoginRetDTO> response = RfidApiResponseDTO.success();
        String clientIp = RequestUtil.getClientIpAddress();
        if (Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getData())) {
            response.setStatus(false);
            response.setMessage("登录数据不能为空");
            return response;
        }

        try {
            LoginReqDTO loginReqDTO = requestDTO.getData();
            // 验证参数
            if (StringUtils.isBlank(loginReqDTO.getCaptchaCode())) {
                response.setStatus(false);
                response.setMessage("验证码不能为空");
                return response;
            }

            // 验证验证码
            String captchaKey = PlatformConstant.CACHE_KEY.CAPTCHA_KEY + loginReqDTO.getCaptchaKey();
            String cachedCaptcha = (String) redisTemplate.opsForValue().get(captchaKey);
            if (StringUtils.isBlank(cachedCaptcha)) {
                response.setStatus(false);
                response.setMessage("验证码已失效");
                return response;
            }

            if (!loginReqDTO.getCaptchaCode().equalsIgnoreCase(cachedCaptcha)) {
                response.setStatus(false);
                response.setMessage("验证码不匹配");
                return response;
            }

            // 删除已使用的验证码
            redisTemplate.delete(captchaKey);

            // 验证参数
            if (StringUtils.isBlank(loginReqDTO.getAccount())) {
                response.setStatus(false);
                response.setMessage("账号不能为空");
                return response;
            }

            if (StringUtils.isBlank(loginReqDTO.getPassword())) {
                response.setStatus(false);
                response.setMessage("密码不能为空");
                return response;
            }

            
            // 检查账号是否被锁定
            String lockKey = PlatformConstant.CACHE_KEY.ACCOUNT_LOCK + loginReqDTO.getAccount();
            Object lockTime = redisTemplate.opsForValue().get(lockKey);
            if (lockTime != null) {
                response.setStatus(false);
                response.setMessage("账号已被锁定，请30分钟后再试");
                // 异步记录登录日志
                loginLogService.recordLoginLogAsync(null, loginReqDTO.getAccount(), clientIp,
                    PlatformConstant.LOGIN_STATUS.LOCKED, "账号已被锁定", null);
                return response;
            }

            // 查询用户信息
            LambdaQueryWrapper<AccountBean> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AccountBean::getCode, loginReqDTO.getAccount());
            List<AccountBean> accounts = accountService.listAccount(queryWrapper);
            
            if (CollectionUtils.isEmpty(accounts)) {
                response.setStatus(false);
                response.setMessage("用户不存在");
                // 异步记录登录日志
                loginLogService.recordLoginLogAsync(null, loginReqDTO.getAccount(), clientIp,
                    PlatformConstant.LOGIN_STATUS.FAILED, "用户不存在", null);
                return response;
            }
            AccountBean account = accounts.get(0);
            if (account.getState() != null && account.getState() == 0) {
                response.setStatus(false);
                response.setMessage("用户已被禁用");
                // 异步记录登录日志
                loginLogService.recordLoginLogAsync(account.getId(), loginReqDTO.getAccount(), clientIp,
                    PlatformConstant.LOGIN_STATUS.FAILED, "用户已被禁用", null);
                return response;
            }
            
            // 验证密码
            if (!passwordEncoder.matches(loginReqDTO.getPassword(), account.getPassword())) {
                // 记录登录失败次数
                String failCountKey = PlatformConstant.CACHE_KEY.LOGIN_FAIL_COUNT + loginReqDTO.getAccount();
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
                    loginLogService.recordLoginLogAsync(account.getId(), loginReqDTO.getAccount(), clientIp,
                        PlatformConstant.LOGIN_STATUS.LOCKED, "密码错误次数过多，账号被锁定", null);
                } else {
                    redisTemplate.opsForValue().set(failCountKey, failCount, 30, TimeUnit.MINUTES);
                    response.setStatus(false);
                    response.setMessage("密码错误，还可尝试" + (PlatformConstant.LOGIN_CONFIG.MAX_LOGIN_FAIL_COUNT - failCount) + "次");
                    // 异步记录登录日志
                    loginLogService.recordLoginLogAsync(account.getId(), loginReqDTO.getAccount(), clientIp,
                        PlatformConstant.LOGIN_STATUS.FAILED, "密码错误", null);
                }
                return response;
            }
            
            // 登录成功，清除失败次数
            String failCountKey = PlatformConstant.CACHE_KEY.LOGIN_FAIL_COUNT + loginReqDTO.getAccount();
            redisTemplate.delete(failCountKey);

            // 生成JWT token
            String accessToken = jwtUtil.generateToken(account.getCode(), account.getId());
            String refreshToken = jwtUtil.generateRefreshToken(account.getCode(), account.getId());
            
            // 将token存储到Redis中，用于后续验证
            String tokenKey = PlatformConstant.CACHE_KEY.TOKEN_KEY + accessToken;
            redisTemplate.opsForValue().set(tokenKey, account.getId(), 24, TimeUnit.HOURS);
            
            // 异步记录登录成功日志
            loginLogService.recordLoginLogAsync(account.getId(), account.getCode(), clientIp,
                PlatformConstant.LOGIN_STATUS.SUCCESS, null, accessToken);
            
            // 构建返回结果
            LoginRetDTO loginRetDTO = new LoginRetDTO();
            loginRetDTO.setAccount(account.getCode());
            loginRetDTO.setAccessToken(accessToken);
            loginRetDTO.setRefreshToken(refreshToken);
            loginRetDTO.setExpiresIn(86400L); // 24小时

            RoleDTO roleDTO = roleService.queryRoleByAccountId(account.getId());
            if (Objects.nonNull(roleDTO)) {
                loginRetDTO.setRole(roleDTO);
                List<MenuDTO> menuDTOS = new ArrayList<>();
                if (PlatformConstant.ROLE_ALIAS.SUPERADMIN.equalsIgnoreCase(roleDTO.getAlias())) {
                    menuDTOS = menuService.queryAdminMenus();
                } else {
                    menuDTOS = menuService.queryMenusByRole(roleDTO.getId());
                }
                roleDTO.setMenus(menuDTOS);
            }

            // 获取用户部门信息
            DepartmentDTO departmentDTO = departmentService.queryDepartmentByAccountId(account.getId());
            loginRetDTO.setDepartment(departmentDTO);
            
            response.setData(loginRetDTO);
            response.setMessage("登录成功");
            
        } catch (AuthenticationException e) {
            response.setStatus(false);
            response.setMessage("认证失败: " + e.getMessage());
            // 异步记录登录日志
            loginLogService.recordLoginLogAsync(null, requestDTO.getData().getAccount(), clientIp,
                PlatformConstant.LOGIN_STATUS.FAILED, "认证失败: " + e.getMessage(), null);
        } catch (Exception e) {
            response.setStatus(false);
            response.setMessage("登录失败: " + e.getMessage());
            // 异步记录登录日志
            loginLogService.recordLoginLogAsync(null, requestDTO.getData().getAccount(), clientIp,
                PlatformConstant.LOGIN_STATUS.FAILED, "登录失败: " + e.getMessage(), null);
        }
        
        return response;
    }

    /**
     * 忘记密码 - 发送重置密码邮件
     * 用户忘记密码时，通过邮箱发送重置密码链接
     * 
     * @param requestDTO 忘记密码请求参数
     * @return 重置密码token
     */
    @Operation(summary = "忘记密码", description = "用户忘记密码时，验证身份后生成重置密码token")
    @PostMapping(value = "/forgotPassword")
    public RfidApiResponseDTO<String> forgotPassword(
            @Parameter(description = "忘记密码请求参数", required = true)
            @RequestBody RfidApiRequestDTO<ForgotPasswordReqDTO> requestDTO) {
        RfidApiResponseDTO<String> response = RfidApiResponseDTO.success();
        if (Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getData())) {
            response.setStatus(false);
            response.setMessage("请求数据不能为空");
            return response;
        }

        try {
            ForgotPasswordReqDTO forgotPasswordReqDTO = requestDTO.getData();
            // 验证参数
            if (StringUtils.isBlank(forgotPasswordReqDTO.getAccount())) {
                response.setStatus(false);
                response.setMessage("账号不能为空");
                return response;
            }
            
            if (StringUtils.isBlank(forgotPasswordReqDTO.getCaptchaCode())) {
                response.setStatus(false);
                response.setMessage("验证码不能为空");
                return response;
            }
            
            if (StringUtils.isBlank(forgotPasswordReqDTO.getCaptchaKey())) {
                response.setStatus(false);
                response.setMessage("验证码key不能为空");
                return response;
            }
            
            // 验证验证码
            String captchaKey = PlatformConstant.CACHE_KEY.CAPTCHA_KEY + forgotPasswordReqDTO.getCaptchaKey();
            String cachedCaptcha = (String) redisTemplate.opsForValue().get(captchaKey);
            
            if (StringUtils.isBlank(cachedCaptcha)) {
                response.setStatus(false);
                response.setMessage("验证码已过期");
                return response;
            }

            if (!cachedCaptcha.equalsIgnoreCase(forgotPasswordReqDTO.getCaptchaCode())) {
                response.setStatus(false);
                response.setMessage("验证码错误");
                return response;
            }
            
            // 删除已使用的验证码
            redisTemplate.delete(captchaKey);
            
            // 查询用户信息（支持用户名或邮箱）
            LambdaQueryWrapper<AccountBean> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AccountBean::getCode, forgotPasswordReqDTO.getAccount())
                       .or()
                       .eq(AccountBean::getEmail, forgotPasswordReqDTO.getAccount());
            List<AccountBean> accounts = accountService.listAccount(queryWrapper);
            
            if (CollectionUtils.isEmpty(accounts)) {
                response.setStatus(false);
                response.setMessage("用户不存在");
                return response;
            }
            
            AccountBean account = accounts.get(0);
            if (account.getState() != null && account.getState() == 0) {
                response.setStatus(false);
                response.setMessage("用户已被禁用");
                return response;
            }

            // 生成重置密码token
            String resetToken = UUID.randomUUID().toString().replace("-", "");
            String resetTokenKey = PlatformConstant.CACHE_KEY.RESET_PASSWORD + resetToken;
            
            // 将重置token存储到Redis中，有效期30分钟
            redisTemplate.opsForValue().set(resetTokenKey, account.getId(), 30, TimeUnit.MINUTES);

            response.setMessage("重置密码邮件已发送");
            response.setData(resetToken);
            
        } catch (Exception e) {
            response.setStatus(false);
            response.setMessage("操作失败: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 忘记密码重置密码
     * 通过重置token重新设置用户密码
     * 
     * @param requestDTO 重置密码请求参数
     * @return 重置结果
     */
    @Operation(summary = "重置密码", description = "通过重置token重新设置用户密码")
    @PostMapping(value = "/forgetResetPassword")
    public RfidApiResponseDTO<Boolean> resetPassword(
            @Parameter(description = "重置密码请求参数", required = true)
            @RequestBody RfidApiRequestDTO<ResetPasswordReqDTO> requestDTO) {
        RfidApiResponseDTO<Boolean> response = RfidApiResponseDTO.success();
        if (Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getData())) {
            response.setStatus(false);
            response.setMessage("请求数据不能为空");
            return response;
        }
        try {
            ResetPasswordReqDTO resetPasswordReqDTO = requestDTO.getData();
            // 验证参数
            if (StringUtils.isBlank(resetPasswordReqDTO.getResetToken())) {
                response.setStatus(false);
                response.setMessage("重置token不能为空");
                return response;
            }
            
            if (StringUtils.isBlank(resetPasswordReqDTO.getNewPassword())) {
                response.setStatus(false);
                response.setMessage("新密码不能为空");
                return response;
            }
            
            if (StringUtils.isBlank(resetPasswordReqDTO.getConfirmPassword())) {
                response.setStatus(false);
                response.setMessage("确认密码不能为空");
                return response;
            }
            
            if (!resetPasswordReqDTO.getNewPassword().equals(resetPasswordReqDTO.getConfirmPassword())) {
                response.setStatus(false);
                response.setMessage("两次输入的密码不一致");
                return response;
            }
            
            // 验证密码强度（可选）
            if (resetPasswordReqDTO.getNewPassword().length() < 6) {
                response.setStatus(false);
                response.setMessage("密码长度不能少于6位");
                return response;
            }
            
            // 验证重置token
            String resetTokenKey = PlatformConstant.CACHE_KEY.RESET_PASSWORD + resetPasswordReqDTO.getResetToken();
            Object accountIdObj = redisTemplate.opsForValue().get(resetTokenKey);
            
            if (accountIdObj == null) {
                response.setStatus(false);
                response.setMessage("重置token已过期或无效");
                return response;
            }
            
            Long accountId = (Long) accountIdObj;
            
            // 获取用户信息
            AccountBean account = accountService.getAccountByPk(accountId);
            if (account == null) {
                response.setStatus(false);
                response.setMessage("用户不存在");
                return response;
            }
            
            if (account.getState() != null && account.getState() == 0) {
                response.setStatus(false);
                response.setMessage("用户已被禁用");
                return response;
            }
            
            // 加密新密码
            String encodedPassword = passwordEncoder.encode(resetPasswordReqDTO.getNewPassword());
            
            // 更新密码
            account.setPassword(encodedPassword);
            boolean updateResult = accountService.updateAccountByPk(account, null, null);
            
            if (!updateResult) {
                response.setStatus(false);
                response.setMessage("密码重置失败");
                return response;
            }
            
            // 删除重置token
            redisTemplate.delete(resetTokenKey);
            
            response.setMessage("密码重置成功");
            response.setData(true);
            
        } catch (Exception e) {
            response.setStatus(false);
            response.setMessage("操作失败: " + e.getMessage());
        }
        
        return response;
    }

    /**
     * 已登录状态下修改密码
     * 用户在已登录状态下修改自己的密码
     * 
     * @param requestDTO 修改密码请求参数
     * @return 修改结果
     */
    @Operation(summary = "修改密码", description = "用户在已登录状态下修改自己的密码")
    @PostMapping(value = "/changePassword")
    public RfidApiResponseDTO<String> changePassword(
            @Parameter(description = "修改密码请求参数", required = true)
            @RequestBody RfidApiRequestDTO<ChangePasswordReqDTO> requestDTO) {
        RfidApiResponseDTO<String> response = RfidApiResponseDTO.success();
        if (Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getData())) {
            response.setStatus(false);
            response.setMessage("请求数据不能为空");
            return response;
        }
        try {
            ChangePasswordReqDTO changePasswordReqDTO = requestDTO.getData();
            // 验证参数
            if (StringUtils.isBlank(changePasswordReqDTO.getOldPassword())) {
                response.setStatus(false);
                response.setMessage("原密码不能为空");
                return response;
            }
            
            if (StringUtils.isBlank(changePasswordReqDTO.getNewPassword())) {
                response.setStatus(false);
                response.setMessage("新密码不能为空");
                return response;
            }
            
            if (StringUtils.isBlank(changePasswordReqDTO.getConfirmPassword())) {
                response.setStatus(false);
                response.setMessage("确认密码不能为空");
                return response;
            }
            
            if (!changePasswordReqDTO.getNewPassword().equals(changePasswordReqDTO.getConfirmPassword())) {
                response.setStatus(false);
                response.setMessage("两次输入的新密码不一致");
                return response;
            }
            
            if (changePasswordReqDTO.getOldPassword().equals(changePasswordReqDTO.getNewPassword())) {
                response.setStatus(false);
                response.setMessage("新密码不能与原密码相同");
                return response;
            }
            
            // 验证新密码强度
            if (changePasswordReqDTO.getNewPassword().length() < 6) {
                response.setStatus(false);
                response.setMessage("新密码长度不能少于6位");
                return response;
            }
            
            // 通过工具类获取token
            String accessToken = RequestUtil.getTokenFromHeader();
            if (StringUtils.isBlank(accessToken)) {
                response.setStatus(false);
                response.setMessage("未登录或登录已过期");
                return response;
            }
            
            // 验证token并获取用户ID
            Long accountId = null;
            try {
                accountId = jwtUtil.getUserIdFromToken(accessToken);
            } catch (Exception e) {
                response.setStatus(false);
                response.setMessage("token解析失败，请重新登录");
                return response;
            }
            
            // 获取用户信息
            AccountBean account = accountService.getAccountByPk(accountId);
            if (account == null) {
                response.setStatus(false);
                response.setMessage("用户不存在");
                return response;
            }
            
            if (account.getState() != null && account.getState() == 0) {
                response.setStatus(false);
                response.setMessage("用户已被禁用");
                return response;
            }
            
            // 验证原密码
            if (!passwordEncoder.matches(changePasswordReqDTO.getOldPassword(), account.getPassword())) {
                response.setStatus(false);
                response.setMessage("原密码错误");
                return response;
            }
            
            // 加密新密码
            String encodedNewPassword = passwordEncoder.encode(changePasswordReqDTO.getNewPassword());
            
            // 更新密码
            account.setPassword(encodedNewPassword);
            boolean updateResult = accountService.updateAccountByPk(account, null, null);
            
            if (!updateResult) {
                response.setStatus(false);
                response.setMessage("密码修改失败");
                return response;
            }
            
            response.setMessage("密码修改成功");
            response.setData("密码修改成功");
            
        } catch (Exception e) {
            response.setStatus(false);
            response.setMessage("操作失败: " + e.getMessage());
        }
        
        return response;
    }


    /**
     * 退出登录
     */
    @Operation(summary = "用户登出", description = "用户登出")
    @PostMapping(value = "/logout")
    public RfidApiResponseDTO<Boolean> logout() {
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
