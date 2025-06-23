package com.rfid.platform.common;

public class LanguageContext {

    private static ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static void setLanguage(String lang) {
        threadLocal.set(lang);
    }

    public static String getLanguage() {
        return threadLocal.get();
    }

    public static void removeLanguage() {
        threadLocal.remove();
    }


}
