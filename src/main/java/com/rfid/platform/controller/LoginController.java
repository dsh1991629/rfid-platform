package com.rfid.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.common.BaseResult;
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
import com.rfid.platform.persistence.RoleDTO;
import com.rfid.platform.service.AccountService;
import com.rfid.platform.service.DepartmentService;
import com.rfid.platform.service.MenuService;
import com.rfid.platform.service.RoleService;
import com.rfid.platform.util.JwtUtil;
import com.wf.captcha.SpecCaptcha;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
    private PasswordEncoder passwordEncoder;


    
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
        
        try {
            // 验证参数
            if (StringUtils.isBlank(loginReqDTO.getAccount())) {
                response.setCode(PlatformConstant.RET_CODE.FAILED);
                response.setMessage("账号不能为空");
                return response;
            }
            
            if (StringUtils.isBlank(loginReqDTO.getPassword())) {
                response.setCode(PlatformConstant.RET_CODE.FAILED);
                response.setMessage("密码不能为空");
                return response;
            }
            
            if (StringUtils.isBlank(loginReqDTO.getCaptchaCode())) {
                response.setCode(PlatformConstant.RET_CODE.FAILED);
                response.setMessage("验证码不能为空");
                return response;
            }
            
            if (StringUtils.isBlank(loginReqDTO.getCaptchaKey())) {
                response.setCode(PlatformConstant.RET_CODE.FAILED);
                response.setMessage("验证码key不能为空");
                return response;
            }
            
            // 验证验证码
            String captchaKey = PlatformConstant.CACHE_KEY.CAPTCHA_KEY + loginReqDTO.getCaptchaKey();
            String cachedCaptcha = (String) redisTemplate.opsForValue().get(captchaKey);
            
            if (StringUtils.isBlank(cachedCaptcha)) {
                response.setCode(PlatformConstant.RET_CODE.FAILED);
                response.setMessage("验证码已过期");
                return response;
            }

            if (!cachedCaptcha.equalsIgnoreCase(loginReqDTO.getCaptchaCode())) {
                response.setCode(PlatformConstant.RET_CODE.FAILED);
                response.setMessage("验证码错误");
                return response;
            }
            
            // 删除已使用的验证码
            redisTemplate.delete(captchaKey);
            
            // 查询用户信息
            LambdaQueryWrapper<AccountBean> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AccountBean::getCode, loginReqDTO.getAccount());
            List<AccountBean> accounts = accountService.listAccount(queryWrapper);
            
            if (CollectionUtils.isEmpty(accounts)) {
                response.setCode(PlatformConstant.RET_CODE.FAILED);
                response.setMessage("用户不存在");
                return response;
            }
            AccountBean account = accounts.get(0);
            if (account.getState() != null && account.getState() == 0) {
                response.setCode(PlatformConstant.RET_CODE.FAILED);
                response.setMessage("用户已被禁用");
                return response;
            }
            
            // 验证密码
            if (!passwordEncoder.matches(loginReqDTO.getPassword(), account.getPassword())) {
                response.setCode(PlatformConstant.RET_CODE.FAILED);
                response.setMessage("密码错误");
                return response;
            }
            
            // 使用Spring Security进行认证
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginReqDTO.getAccount(), loginReqDTO.getPassword())
            );
            
            // 生成JWT token
            String accessToken = jwtUtil.generateToken(account.getCode(), account.getId());
            String refreshToken = jwtUtil.generateRefreshToken(account.getCode(), account.getId());
            
            // 将token存储到Redis中，用于后续验证
            String tokenKey = PlatformConstant.CACHE_KEY.TOKEN_KEY + accessToken;
            redisTemplate.opsForValue().set(tokenKey, account.getId(), 24, TimeUnit.HOURS);
            
            // 构建返回结果
            LoginRetDTO loginRetDTO = new LoginRetDTO();
            loginRetDTO.setAccount(account.getCode());
            loginRetDTO.setAccessToken(accessToken);
            loginRetDTO.setRefreshToken(refreshToken);
            loginRetDTO.setExpiresIn(86400L); // 24小时

            RoleDTO roleDTO = roleService.queryRoleByAccountId(account.getId());
            if (Objects.nonNull(roleDTO)) {
                loginRetDTO.setRole(roleDTO);
                List<MenuDTO> menuDTOS = menuService.queryMenusByRole(roleDTO.getId());
                loginRetDTO.setMenus(menuDTOS);
            }

            // 获取用户部门信息
            DepartmentDTO departmentDTO = departmentService.queryDepartmentByAccountId(account.getId());
            loginRetDTO.setDepartment(departmentDTO);
            
            response.setData(loginRetDTO);
            response.setMessage("登录成功");
            
        } catch (AuthenticationException e) {
            response.setCode(PlatformConstant.RET_CODE.FAILED);
            response.setMessage("认证失败: " + e.getMessage());
        } catch (Exception e) {
            response.setCode(PlatformConstant.RET_CODE.FAILED);
            response.setMessage("登录失败: " + e.getMessage());
        }
        
        return response;
    }

    /**
     * 忘记密码 - 发送重置密码邮件
     */
    @PostMapping(value = "/forgotPassword")
    public BaseResult<String> forgotPassword(@RequestBody ForgotPasswordReqDTO forgotPasswordReqDTO) {
        BaseResult<String> response = new BaseResult<>();
        
        try {
            // 验证参数
            if (StringUtils.isBlank(forgotPasswordReqDTO.getAccount())) {
                response.setCode(PlatformConstant.RET_CODE.FAILED);
                response.setMessage("账号不能为空");
                return response;
            }
            
            if (StringUtils.isBlank(forgotPasswordReqDTO.getCaptchaCode())) {
                response.setCode(PlatformConstant.RET_CODE.FAILED);
                response.setMessage("验证码不能为空");
                return response;
            }
            
            if (StringUtils.isBlank(forgotPasswordReqDTO.getCaptchaKey())) {
                response.setCode(PlatformConstant.RET_CODE.FAILED);
                response.setMessage("验证码key不能为空");
                return response;
            }
            
            // 验证验证码
            String captchaKey = PlatformConstant.CACHE_KEY.CAPTCHA_KEY + forgotPasswordReqDTO.getCaptchaKey();
            String cachedCaptcha = (String) redisTemplate.opsForValue().get(captchaKey);
            
            if (StringUtils.isBlank(cachedCaptcha)) {
                response.setCode(PlatformConstant.RET_CODE.FAILED);
                response.setMessage("验证码已过期");
                return response;
            }

            if (!cachedCaptcha.equalsIgnoreCase(forgotPasswordReqDTO.getCaptchaCode())) {
                response.setCode(PlatformConstant.RET_CODE.FAILED);
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
                response.setCode(PlatformConstant.RET_CODE.FAILED);
                response.setMessage("用户不存在");
                return response;
            }
            
            AccountBean account = accounts.get(0);
            if (account.getState() != null && account.getState() == 0) {
                response.setCode(PlatformConstant.RET_CODE.FAILED);
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
            response.setCode(PlatformConstant.RET_CODE.FAILED);
            response.setMessage("操作失败: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 忘记密码重置密码
     */
    @PostMapping(value = "/forgetResetPassword")
    public BaseResult<String> resetPassword(@RequestBody ResetPasswordReqDTO resetPasswordReqDTO) {
        BaseResult<String> response = new BaseResult<>();
        
        try {
            // 验证参数
            if (StringUtils.isBlank(resetPasswordReqDTO.getResetToken())) {
                response.setCode(PlatformConstant.RET_CODE.FAILED);
                response.setMessage("重置token不能为空");
                return response;
            }
            
            if (StringUtils.isBlank(resetPasswordReqDTO.getNewPassword())) {
                response.setCode(PlatformConstant.RET_CODE.FAILED);
                response.setMessage("新密码不能为空");
                return response;
            }
            
            if (StringUtils.isBlank(resetPasswordReqDTO.getConfirmPassword())) {
                response.setCode(PlatformConstant.RET_CODE.FAILED);
                response.setMessage("确认密码不能为空");
                return response;
            }
            
            if (!resetPasswordReqDTO.getNewPassword().equals(resetPasswordReqDTO.getConfirmPassword())) {
                response.setCode(PlatformConstant.RET_CODE.FAILED);
                response.setMessage("两次输入的密码不一致");
                return response;
            }
            
            // 验证密码强度（可选）
            if (resetPasswordReqDTO.getNewPassword().length() < 6) {
                response.setCode(PlatformConstant.RET_CODE.FAILED);
                response.setMessage("密码长度不能少于6位");
                return response;
            }
            
            // 验证重置token
            String resetTokenKey = PlatformConstant.CACHE_KEY.RESET_PASSWORD + resetPasswordReqDTO.getResetToken();
            Object accountIdObj = redisTemplate.opsForValue().get(resetTokenKey);
            
            if (accountIdObj == null) {
                response.setCode(PlatformConstant.RET_CODE.FAILED);
                response.setMessage("重置token已过期或无效");
                return response;
            }
            
            Long accountId = (Long) accountIdObj;
            
            // 获取用户信息
            AccountBean account = accountService.getAccountByPk(accountId);
            if (account == null) {
                response.setCode(PlatformConstant.RET_CODE.FAILED);
                response.setMessage("用户不存在");
                return response;
            }
            
            if (account.getState() != null && account.getState() == 0) {
                response.setCode(PlatformConstant.RET_CODE.FAILED);
                response.setMessage("用户已被禁用");
                return response;
            }
            
            // 加密新密码
            String encodedPassword = passwordEncoder.encode(resetPasswordReqDTO.getNewPassword());
            
            // 更新密码
            account.setPassword(encodedPassword);
            boolean updateResult = accountService.updateAccountByPk(account, null, null);
            
            if (!updateResult) {
                response.setCode(PlatformConstant.RET_CODE.FAILED);
                response.setMessage("密码重置失败");
                return response;
            }
            
            // 删除重置token
            redisTemplate.delete(resetTokenKey);
            
            response.setMessage("密码重置成功");
            response.setData("密码重置成功");
            
        } catch (Exception e) {
            response.setCode(PlatformConstant.RET_CODE.FAILED);
            response.setMessage("操作失败: " + e.getMessage());
        }
        
        return response;
    }
}
