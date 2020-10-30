package com.fuzy.find.util;

public class StringUtils {

    public static String trimToEmpty(String s) {
        if (s == null) {
            return "";
        }

        return s.trim();
    }

    public static String trimToNull(String s) {
        if (s == null) {
            return null;
        }

        String trim = s.trim();
        return trim.length() == 0 ? null : trim;
    }

}
