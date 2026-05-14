package com.delivery.util;

import java.util.regex.Pattern;

public class SecurityUtil {

    private static final Pattern XSS_PATTERN = Pattern.compile(
            "<script[^>]*>.*?</script>|<iframe[^>]*>.*?</iframe>|javascript:|on\\w+\\s*=",
            Pattern.CASE_INSENSITIVE
    );

    public static String sanitizeInput(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        if (XSS_PATTERN.matcher(input).find()) {
            throw new IllegalArgumentException("输入包含非法字符");
        }
        return input.trim();
    }

    public static void validateUsername(String username) {
        if (username == null || username.length() < 3 || username.length() > 20) {
            throw new IllegalArgumentException("用户名长度必须在3-20个字符之间");
        }
        if (!username.matches("^[a-zA-Z0-9_\\u4e00-\\u9fa5]+$")) {
            throw new IllegalArgumentException("用户名只能包含字母、数字、下划线和中文");
        }
    }

    public static void validatePassword(String password) {
        if (password == null || password.length() < 6 || password.length() > 20) {
            throw new IllegalArgumentException("密码长度必须在6-20个字符之间");
        }
    }

    public static void validatePhone(String phone) {
        if (phone != null && !phone.isEmpty()) {
            if (!phone.matches("^1[3-9]\\d{9}$")) {
                throw new IllegalArgumentException("手机号格式不正确");
            }
        }
    }

    public static boolean containsXSS(String input) {
        return input != null && XSS_PATTERN.matcher(input).find();
    }
}
