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

}
