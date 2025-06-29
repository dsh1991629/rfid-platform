package com.rfid.platform.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InterfaceLog {
    
    /**
     * 接口类型
     */
    int type() default 1;
    
    /**
     * 接口描述
     */
    String description() default "";
}