package com.rfid.platform.config.handler;

import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SuffixTableNameHandler implements TableNameHandler {
    //用于记录哪些表可以使用该月份动态表名处理器
    private List<String> tableNames;
    //构造函数，构造动态表名处理器的时候，传递tableNames参数
    public SuffixTableNameHandler(String ...tableNames) {
        this.tableNames = Arrays.asList(tableNames).stream().map(String::toUpperCase).collect(Collectors.toList());
    }

    //每个请求线程维护一个suffix数据，避免多线程数据冲突。所以使用ThreadLocal
    private static final ThreadLocal<String> SUFFIX_DATA = new ThreadLocal<>();
    //设置请求线程的month数据
    public static void setData(String suffix) {
        SUFFIX_DATA.set(suffix);
    }
    //删除当前请求线程的month数据
    public static void removeData() {
        SUFFIX_DATA.remove();
    }

    //动态表名接口实现方法
    @Override
    public String dynamicTableName(String sql, String tableName) {
        if (this.tableNames.contains(tableName.toUpperCase()) && StringUtils.isNotEmpty(SUFFIX_DATA.get())){
            return tableName + "_" + SUFFIX_DATA.get();  //表名增加suffix
        }else{
            return tableName;   //表名原样返回
        }
    }
}

