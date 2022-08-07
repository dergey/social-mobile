package com.sergey.zhuravlev.mobile.social.util;

import android.content.Context;

public class StringUtils {

    public static String getString(String resourceName, Context context, String... args) {
        String packageName = context.getPackageName();
        int resId = context.getResources().getIdentifier(resourceName, "string", packageName);
        return context.getString(resId, args);
    }

    public static boolean isBlank(String source) {
        return source == null || source.trim().isEmpty();
    }
}
