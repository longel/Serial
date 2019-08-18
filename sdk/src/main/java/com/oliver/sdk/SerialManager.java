package com.oliver.sdk;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.text.TextUtils;

import com.oliver.sdk.listener.IListener;
import com.oliver.sdk.model.SerialConfig;
import com.oliver.sdk.parser.Parser;
import com.oliver.sdk.service.SerialService;
import com.oliver.sdk.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * author : Oliver
 * date   : 2019/8/6
 * desc   : init
 */

@SuppressLint("StaticFieldLeak")
public class SerialManager {

    private static final String TAG = "SerialManager";


    private static Context sContext;
    private static SerialConfig mSerialConfig; // 全局串口配置

    private List<IListener> mCommandListeners;
    private SerialService.SerialBinder mSerialBinder;

//    public void


    private static final class HOLDER {
        private static final SerialManager sInstance = new SerialManager();
    }

    private SerialManager() {
        mCommandListeners = new ArrayList<>();
        Parser.parse(sContext, obj -> {
            mSerialConfig = (SerialConfig) obj;
            startService(); // 开启串口
        });
    }

    public static SerialManager getInstance() {
        check();
        return HOLDER.sInstance;
    }

    public static void init(Context context) {
        sContext = context.getApplicationContext();
    }

    private static void check() {
        if (sContext == null) {
            throw new RuntimeException("please call init() first!");
        }
    }

    public void addListener(IListener listener) {
        if (mSerialBinder != null) {
            mSerialBinder.addListener(listener);
        } else {
            mCommandListeners.add(listener);
        }
    }

    public void removeListaner(IListener listener) {
        if (mSerialBinder != null) {
            mSerialBinder.removeListener(listener);
        }
    }

    public void clearListaners() {
        if (mSerialBinder != null) {
            mSerialBinder.clearListeners();
        }
    }

    private void startService() {
        sContext.bindService(SerialService.getServiceIntent(sContext), mConn, Service.BIND_AUTO_CREATE);
    }

    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtils.d(TAG, "onServiceConnected " + service);
            onAfterServiceConnected(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtils.d(TAG, "onServiceDisconnected " + name);
        }
    };

    private void onAfterServiceConnected(IBinder service) {
        Observable.just(service)
                .filter(iBinder -> iBinder instanceof SerialService.SerialBinder)
                .map(iBinder -> (SerialService.SerialBinder) iBinder)
                .doOnNext(serialBinder -> mSerialBinder = serialBinder)
                .doOnNext(serialBinder -> serialBinder.setSerialConfig(mSerialConfig))
                .doOnNext(serialBinder -> {
                    if (mCommandListeners != null) {
                        for (IListener listener : mCommandListeners) {
                            mSerialBinder.addListener(listener);
                        }
                    }
                })
                .subscribe(serialBinder -> {
                    LogUtils.d(TAG, "onAfterServiceConnected finished");
                });
    }


    public void sendCommand(String dev) {
        Observable.just(true)
                .filter(aBoolean -> aBoolean && mSerialBinder != null)
                .subscribe(bytes1 -> mSerialBinder.sendCommand(dev));
    }

    public void sendCommand(String dev, byte[] bytes) {
        Observable.just(true)
                .filter(aBoolean -> aBoolean && mSerialBinder != null && bytes != null && bytes.length > 0)
                .map(aBoolean -> bytes)
                .subscribe(bytes1 -> mSerialBinder.sendCommand(dev, bytes1));
    }

    public void sendCommand(String dev, String ascii) {
        Observable.just(true)
                .filter(aBoolean -> aBoolean && mSerialBinder != null && !TextUtils.isEmpty(ascii))
                .map(aBoolean -> ascii)
                .subscribe(targetASCII -> mSerialBinder.sendCommand(dev, targetASCII));
    }

}
