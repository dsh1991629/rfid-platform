package com.rfid.platform.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rfid.platform.entity.AccountBean;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AccountMapper extends BaseMapper<AccountBean> {
    Page<AccountBean> queryAccountPage(Page<AccountBean> page,
                                            @Param(Constants.WRAPPER) LambdaQueryWrapper<AccountBean> queryWrapper,
                                            @Param("departmentId") Long departmentId,
                                            @Param("roleId") Long roleId);
} 