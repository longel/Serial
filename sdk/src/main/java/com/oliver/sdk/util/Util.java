package com.oliver.sdk.util;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import java.lang.reflect.Method;

/**
 * 获取Application
 */

public class Util {

    @SuppressLint("StaticFieldLeak")
    private static Context sContext = null;

    private static void getApplicationContext() {
        try {
            //获取当前的ActivityThread对象
            Class<?> aClass = Class.forName("android.app.ActivityThread");
            //获取ActivityThread的静态方法currentApplication()方法
            Method method = aClass.getDeclaredMethod("currentApplication");
            method.setAccessible(true);
            //调用ActivityThread的currentApplication()
            Application application = (Application) method.invoke(null);
            sContext = application.getApplicationContext();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Context getContext() {
        if (sContext != null) {
            return sContext;
        }
        try {
            Class<?> aClass = Class.forName("android.app.AppGlobals");
            //获取AppGlobals的getInitialApplication()
            Method method = aClass.getDeclaredMethod("getInitialApplication");
            method.setAccessible(true);
            //静态方法，不需要借助实例运行
            sContext = ((Application) method.invoke(null));
            if (sContext == null) {
                throw new IllegalStateException("Static initialization of Applications must be on main thread.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            getApplicationContext();
        }

        if (sContext == null) {
            throw new NullPointerException("Global application un-initialized");
        }
        return sContext;
    }
}
