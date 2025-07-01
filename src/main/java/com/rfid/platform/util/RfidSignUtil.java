package com.rfid.platform.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * RFID接口签名验证工具类
 */
public class RfidSignUtil {

    /**
     * 测试环境appSecret
     */
    private static final String APP_SECRET = "tbe-8100";

    /**
     * 生成签名
     * @param appId 应用ID
     * @param method 方法名
     * @param version 版本号
     * @param timestamp 时间戳
     * @return 签名字符串
     */
    public static String generateSign(String appId, String method, String version, String timestamp) {
        try {
            // Base64编码各个参数
            String encodedAppId = Base64.getEncoder().encodeToString(appId.getBytes(StandardCharsets.UTF_8));
            String encodedAppSecret = Base64.getEncoder().encodeToString(APP_SECRET.getBytes(StandardCharsets.UTF_8));
            String encodedVersion = Base64.getEncoder().encodeToString(version.getBytes(StandardCharsets.UTF_8));
            String encodedMethod = Base64.getEncoder().encodeToString(method.getBytes(StandardCharsets.UTF_8));
            String encodedTimestamp = Base64.getEncoder().encodeToString(timestamp.getBytes(StandardCharsets.UTF_8));

            // 拼接参数字符串
            String paramStr = String.format("appId=%s&appSecret=%s&version=%s&method=%s&timestamp=%s",
                    encodedAppId, encodedAppSecret, encodedVersion, encodedMethod, encodedTimestamp);

            // MD5加密并转大写
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(paramStr.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5算法不可用", e);
        }
    }

    /**
     * 验证签名
     * @param appId 应用ID
     * @param method 方法名
     * @param version 版本号
     * @param timestamp 时间戳
     * @param sign 待验证的签名
     * @return 验证结果
     */
    public static boolean verifySign(String appId, String method, String version, String timestamp, String sign) {
        String expectedSign = generateSign(appId, method, version, timestamp);
        return expectedSign.equals(sign);
    }
}