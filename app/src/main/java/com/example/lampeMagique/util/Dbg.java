package com.example.lampeMagique.util;

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
        return getClassName(2);
    }

    public static String getSimpleClassName(final int depth) {
        String fullClazz = getClassName(depth);
        String[] names = fullClazz.split("\\.");
        return names[names.length-1];
    }
    public static String getSimpleClassName() {
        return getSimpleClassName(2);
    }

    public static void logMethod() {
        String fullClazz = getClassName();
        String clazz = getSimpleClassName(2);
        String method = getMethodName(1);

        Log.i(clazz, method+"() \tof \t"+fullClazz);
    }

    public static void logInMethod(String message) {
        String clazz = getSimpleClassName(2);
        String method = getMethodName(1);

        Log.i(clazz+"."+method+"()", "msg: "+message);
    }
}
