package com.rfid.platform.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.rfid.platform.entity.AccountDepartmentRelBean;
import com.rfid.platform.entity.DepartmentBean;
import com.rfid.platform.mapper.DepartmentMapper;
import com.rfid.platform.persistence.DepartmentDTO;
import com.rfid.platform.service.DepartmentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, DepartmentBean> implements DepartmentService {

    @Autowired
    @Lazy
    private AccountDepartRelServiceImpl accountDepartRelService;


    @Override
    public boolean saveDepartment(DepartmentBean entity) {
        return super.save(entity);
    }

    @Override
    public boolean removeDepartmentByPk(Long id) {
        return super.removeById(id);
    }

    @Override
    public boolean updateDepartmentByPk(DepartmentBean entity) {
        return super.updateById(entity);
    }

    @Override
    public DepartmentBean getDepartmentByPk(Long id) {
        return super.getById(id);
    }

    @Override
    public IPage<DepartmentBean> pageDepartment(Page<DepartmentBean> page, LambdaQueryWrapper<DepartmentBean> query) {
        return super.page(page, query);
    }

    @Override
    public List<DepartmentBean> listDepartment(LambdaQueryWrapper<DepartmentBean> queryWrapper) {
        return super.list(queryWrapper);
    }

    @Override
    public Boolean existDepartment(LambdaQueryWrapper<DepartmentBean> nameCheckWrapper) {
        return super.exists(nameCheckWrapper);
    }

    @Override
    public DepartmentDTO queryDepartmentByAccountId(Long accountId) {
        LambdaQueryWrapper<AccountDepartmentRelBean> relWrapper = Wrappers.lambdaQuery();
        relWrapper.eq(AccountDepartmentRelBean::getAccountId, accountId);
        List<AccountDepartmentRelBean> accountDepartmentRelBeans = accountDepartRelService.listAccountDepartRel(relWrapper);
        if (CollectionUtils.isEmpty(accountDepartmentRelBeans)) {
            return null;
        }
        AccountDepartmentRelBean accountDepartmentRelBean = accountDepartmentRelBeans.get(0);
        Long departmentId = accountDepartmentRelBean.getDepartmentId();
        DepartmentBean departmentBean = super.getById(departmentId);
        if (Objects.nonNull(departmentBean)) {
            DepartmentDTO departmentDTO = BeanUtil.copyProperties(departmentBean, DepartmentDTO.class);
            return departmentDTO;
        }
        return null;
    }
}