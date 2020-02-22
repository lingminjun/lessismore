package com.lessismore.xauto.ast;

public final class StringUtils {

    public static boolean isLowerCase(String string, int index){
        if (string == null) {
            return false;
        }
        if (index >= string.length()) {
            return false;
        }

        char c = string.charAt(index);
        return c >= 'a' && c <= 'z';
    }

    public static boolean isUpperCase(String string, int index){
        if (string == null) {
            return false;
        }
        if (index >= string.length()) {
            return false;
        }

        char c = string.charAt(index);
        return c >= 'A' && c <= 'Z';
    }

    public static boolean notEmpty(String string) {
        return string != null && string.length() > 0;
    }

    public static boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }

    public static String convertFieldMatchName(String string, String type) {
        if (isEmpty(string)) {
            return string;
        }

        // is XXX 的情况，比较特殊，必须是bool的才会被处理
        if (("boolean".equals(type) || "java.lang.Boolean".equals(type)) && string.startsWith("is")) {
            if (StringUtils.isUpperCase(string, 2)) {
                return string.substring(2);
            }
        }

        // 小字母开头，如aXxx，保留
        if (StringUtils.isLowerCase(string, 0) && StringUtils.isUpperCase(string, 1)) {
           return string;
        }

        // 首字母大写
        if (string.length() > 1) {
            return string.substring(0,1).toUpperCase() +  string.substring(1);
        } else {
            return string.substring(0,1).toUpperCase();
        }
    }

    public static String valueString(String value) {
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1,value.length() - 1);
        } else if (value.startsWith("\'") && value.endsWith("\'")) {
            return value.substring(1,value.length() - 1);
        } else {
            return value;
        }
    }
}
