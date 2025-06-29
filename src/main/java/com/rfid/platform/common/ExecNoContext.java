package com.rfid.platform.common;

public class ExecNoContext {
    
    private static final ThreadLocal<String> EXEC_NO_HOLDER = new ThreadLocal<>();
    
    /**
     * 设置执行编号
     */
    public static void setExecNo(String execNo) {
        EXEC_NO_HOLDER.set(execNo);
    }
    
    /**
     * 获取执行编号
     */
    public static String getExecNo() {
        return EXEC_NO_HOLDER.get();
    }
    
    /**
     * 清除执行编号
     */
    public static void clear() {
        EXEC_NO_HOLDER.remove();
    }
}