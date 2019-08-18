package com.oliver.sdk.parser;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.oliver.sdk.helper.CallbackHelper;
import com.oliver.sdk.listener.ISuccessListener;
import com.oliver.sdk.model.SerialConfig;
import com.oliver.sdk.util.FileUtils;
import com.oliver.sdk.util.LogUtils;
import com.oliver.sdk.util.SysUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.annotations.NonNull;


/**
 * author : Oliver
 * date   : 2019/8/6
 * desc   :
 */

public class Parser {

    public static final String TAG = "Parser";

    private static final String PATH_CHANNEL = "channel/";
    private static final String CHANNEL = "channel";
    private static final String PATH_CONFIG = "configuration";

    public static void parse(@NonNull final Context context, @NonNull final ISuccessListener listener) {
        new Thread(() -> {
            try {
                String config = getConfig(context.getApplicationContext());
                Gson gson = new Gson();
                SerialConfig serialConfig = gson.fromJson(config, SerialConfig.class);
                CallbackHelper.onSuccess(listener, serialConfig);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    /**
     * 通过assets目录的文件进行初始化配置
     */
    private static String getConfig(Context context) {
        InputStream is = null;
        try {
            String device = getAdaptChannel(context);
            String configFilePath = device + File.separator + PATH_CONFIG;
            LogUtils.d(TAG, "try to get mcu config from channel : " + configFilePath);
            is = context.getAssets().open(configFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (is == null) {
                LogUtils.d(TAG, "use default config " + PATH_CONFIG);
                is = context.getAssets().open(PATH_CONFIG);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (is != null) {
                String config = FileUtils.readFile(is);
                if (TextUtils.isEmpty(config)) {
                    throw new NullPointerException("mcu-config file isn't exist!!!");
                }
                LogUtils.d(TAG, "mcu config content: " + config);
                return config;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    private static String getAdaptProduct(Context context, String device) {
//        if (TextUtils.isEmpty(device)) {
//            return null;
//        }
//        try {
//            String[] products = context.getAssets().list(device);
//            for (String product : products) {
//                if (product.equalsIgnoreCase(Constants.PRODUCT_DEFAULT)) {
//                    return PATH_CHANNEL + product;
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return null;
    }


    private static String getAdaptChannel(Context context) {
        try {
            String[] channels = context.getAssets().list(CHANNEL);
            for (String channel : channels) {
                LogUtils.d(TAG,"channel: " + channel);
                if (channel.equalsIgnoreCase(SysUtil.getSystemModel())) {
                    return PATH_CHANNEL + channel;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
