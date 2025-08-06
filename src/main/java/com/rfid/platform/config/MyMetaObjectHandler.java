package com.rfid.platform.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.rfid.platform.common.AccountContext;
import com.rfid.platform.util.TimeUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author lihui
 * @date 2023年07月137日
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {


    // 插入时的填充策略
    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = TimeUtil.getSysDate();

        // 只有当createTime字段没有值时才设置
        if (this.getFieldValByName("createTime", metaObject) == null) {
            this.setFieldValByName("createTime", now, metaObject);
        }
        try {
            if (Objects.nonNull(AccountContext.getAccountId())) {
                this.setFieldValByName("createId", String.valueOf(AccountContext.getAccountId()), metaObject);
            }
        } catch (Exception ignored) {
        }
    }


    // 更新时的填充策略
    @Override
    public void updateFill(MetaObject metaObject) {
        LocalDateTime now = TimeUtil.getSysDate();
        if (this.getFieldValByName("updateTime", metaObject) == null) {
            this.setFieldValByName("updateTime", now, metaObject);
        }
        try {
            if (Objects.nonNull(AccountContext.getAccountId())) {
                this.setFieldValByName("updateId", String.valueOf(AccountContext.getAccountId()), metaObject);
            }
        } catch (Exception ignored) {
        }
    }
}
