package com.example.lampeMagique;

import android.util.Log;

public class Dbg {
    public static String getMethodName(final int depth)
    {
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        return ste[3 + depth].getMethodName();
    }

    public static String getMethodName()
    {
        return getMethodName(1);
    }

    public static String getClassName(final int depth)
    {
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        return ste[3 + depth].getClassName();
    }

    public static String getClassName()
    {
        return getClassName(1);
    }

    public static void logMethod() {
        String fullClazz = getClassName(1);

        String[] names = fullClazz.split("\\.");
        String clazz = names[names.length-1];

        String method = getMethodName(1);

        Log.i(clazz, method+"() \tof \t"+fullClazz);
    }
}
