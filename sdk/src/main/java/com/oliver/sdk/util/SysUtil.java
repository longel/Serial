package com.oliver.sdk.util;

import android.os.Build;

/**
 * author : Oliver
 * date   : 2018/9/26
 * desc   :
 */

public class SysUtil {

    /**
     * 获取手机型号
     *
     * @return
     */
    public static String getSystemModel() {
        return Build.MODEL;
    }

}
