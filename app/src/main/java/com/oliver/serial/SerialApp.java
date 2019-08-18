package com.oliver.serial;

import android.app.Application;

import com.oliver.sdk.SerialManager;

/**
 * author : Oliver
 * date   : 2019/8/18
 * desc   :
 */

public class SerialApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SerialManager.init(this);
        SerialManager.getInstance();
    }
}
