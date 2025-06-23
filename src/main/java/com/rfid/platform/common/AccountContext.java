package com.rfid.platform.common;

public class AccountContext {

    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setAccountId(Long id) {
        threadLocal.set(id);
    }

    public static Long getAccountId() {
        return threadLocal.get();
    }

    public static void removeAccountId() {
        threadLocal.remove();
    }


}
