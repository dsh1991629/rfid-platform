package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.AccountBean;
import com.rfid.platform.service.AccountService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AccountService accountService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LambdaQueryWrapper<AccountBean> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountBean::getCode, username);
        List<AccountBean> accounts = accountService.listAccount(queryWrapper);
        
        if (CollectionUtils.isEmpty(accounts)) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        AccountBean account = accounts.get(0);
        if (account.getState() != null && account.getState() == 0) {
            throw new UsernameNotFoundException("用户已被禁用: " + username);
        }
        
        return User.builder()
                .username(account.getCode())
                .password(account.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();
    }
}