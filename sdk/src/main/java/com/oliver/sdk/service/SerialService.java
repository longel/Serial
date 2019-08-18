package com.oliver.sdk.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.serialport.SerialPort;
import android.text.TextUtils;
import android.util.Log;

import com.oliver.sdk.listener.IListener;
import com.oliver.sdk.listener.cmd.IOnReceiveCommandListener;
import com.oliver.sdk.listener.cmd.IOnSendCommandListener;
import com.oliver.sdk.model.Device;
import com.oliver.sdk.model.Product;
import com.oliver.sdk.model.SerialConfig;
import com.oliver.sdk.model.Uart;
import com.oliver.sdk.policy.IPolicy;
import com.oliver.sdk.util.ByteUtils;
import com.oliver.sdk.util.LogUtils;
import com.oliver.sdk.util.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;


public class SerialService extends Service {

    public static final String TAG = "SerialService";
    public static final String UNIQUE_ACTION = "com.oliver.sdk.SerialService";
    public static final String POLICY_PACKAGE_NAME = "com.oliver.sdk.policy.";
    private static final int DEFAULT_QUEUE_SIZE = 128;

    private IPolicy mPolicy;

    private SerialBinder mSerialBinder;
    private boolean mServiceRunning;
    private boolean mInitialized = false;

    private SerialConfig mSerialConfig;
    private List<IListener> mListeners;
    private List<ReadThread> mReadThreadList;
    private List<SendThread> mSendThreadList;

    public SerialService() {
    }

    public static Intent getServiceIntent(Context context) {
        Intent intent = new Intent(UNIQUE_ACTION);
        intent.setPackage(context.getPackageName());
        return intent;
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.d(TAG, "onBind");
        return mSerialBinder = new SerialBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d(TAG, "onCreate");
        initialize();
    }

    private void initialize() {
        mServiceRunning = true;
        Observable.just(mInitialized)
                .filter(aBoolean -> {
                    LogUtils.d(TAG, "filter: " + Thread.currentThread().getName());
                    return !aBoolean;
                })
                .doOnNext(aBoolean -> {
                    LogUtils.d(TAG, "doAfterNext: " + Thread.currentThread().getName());
                    mInitialized = true;
                })
                .doOnNext(aBoolean -> initListeners())
                .subscribeOn(Schedulers.io())
                .subscribe(aBoolean -> LogUtils.d(TAG, "初始化成功"));
    }

    private void initListeners() {
        LogUtils.d(TAG, "initListeners");
        mListeners = new CopyOnWriteArrayList<>();
        mReadThreadList = new CopyOnWriteArrayList<>();
        mSendThreadList = new CopyOnWriteArrayList<>();
    }

    private void initThreads() {
        LogUtils.d(TAG, "initThreads");
    }

    private void initSerials() {
        LogUtils.d(TAG, "initSerials");
        Observable.just(mSerialConfig)
                .filter(serialConfig -> mSerialConfig != null)
                .map(serialConfig -> {
                    Device device = serialConfig.getDevice();
                    if (device == null) {
                        LogUtils.e(TAG, "没有搜索到设备信息！");
                    }
                    return device;
                })
                .map(device -> {
                    List<Product> products = device.getProduct();
                    if (products == null || products.size() == 0) {
                        LogUtils.e(TAG, "没有搜索到产品信息");
                    }
                    return products;
                })
                .map(products -> {
                    if (products == null || products.size() == 0) {
                        return null;
                    }
                    Product current = null;
                    for (Product product : products) {
                        if (product.getCurrent()) {
                            current = product;
                            break;
                        }
                    }
                    if (current == null) {
                        LogUtils.e(TAG, "请在配置文件中设置当前产品的current属性！");
                    }
                    return current;
                })
                .map(product -> {
                    initProductByReflection(product);
                    List<Uart> uarts = product.getUart();
                    if (uarts == null || uarts.size() == 0) {
                        LogUtils.e(TAG, "无可用串口");
                    }
                    return uarts;
                })
                .doAfterNext((uarts) -> release())
                .doAfterNext(this::startUart)
                .subscribeOn(Schedulers.io())
                .subscribe(uarts -> LogUtils.d(TAG, "initSerials finished!"));
    }

    private void initProductByReflection(Product product) {
        LogUtils.d(TAG, "initProductByReflection");
        String policy = product.getPolicy();
        if (TextUtils.isEmpty(policy)) {
            return;
        }
        LogUtils.d(TAG, "-----------------------------------------------------------");
        LogUtils.d(TAG, "initProductByReflection policy：" + policy);
        policy = policy.substring(0, 1).toUpperCase() + policy.substring(1);
        LogUtils.d(TAG, "initProductByReflection policy：" + policy);
        LogUtils.d(TAG, "-----------------------------------------------------------");
        try {
            Class<?> policyClass = Class.forName(POLICY_PACKAGE_NAME + policy + "Policy");
            Constructor<?> constructor = policyClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            mPolicy = (IPolicy) constructor.newInstance();
            mPolicy.onStart(mSerialBinder);
            LogUtils.d(TAG, "mPolicy.getClass():" + mPolicy.getClass());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void startUart(List<Uart> uarts) {
        release();
        for (Uart uart : uarts) {
            startUart(uart);
        }
    }

    private void startUart(Uart uart) {
        if (TextUtils.isEmpty(uart.getPath())) {
            LogUtils.e(TAG, "请配置串口驱动路径，例如：dev/ttys1");
            return;
        }
        if (TextUtils.isEmpty(uart.getBaudrate())) {
            LogUtils.e(TAG, "请配置串口波特率，例如：9600");
            return;
        }
        //在这里try-catch，避免在ScanService中跳出了循环
        try {
            SerialPort serialPort = new SerialPort(new File(uart.getPath()), Integer.parseInt(uart.getBaudrate()), 0);
            SendThread sendThread = new SendThread(uart, serialPort);
            sendThread.start();
            mSendThreadList.add(sendThread);

            ReadThread readThread = new ReadThread(uart, serialPort);
            sendThread.start();
            mReadThreadList.add(readThread);


        } catch (Exception e) {
            LogUtils.e(TAG, "=========================================================");
            LogUtils.e(TAG, "openSerial failed：" + uart.getPath());
            LogUtils.e(TAG, "=========================================================");
        }
    }

    private void release() {
        releaseThreads();
    }

    private void releaseThreads() {
        if (mReadThreadList != null && mReadThreadList.size() > 0) {
            for (ReadThread thread : mReadThreadList) {
                thread.interrupt();
            }
            mReadThreadList.clear();
        }
        if (mSendThreadList != null && mSendThreadList.size() > 0) {
            for (SendThread thread : mSendThreadList) {
                thread.interrupt();
            }
            mSendThreadList.clear();
        }
    }

    public class SerialBinder extends Binder {

        public void setSerialConfig(SerialConfig serialConfig) {
            ObjectUtils.assertNull(serialConfig);
            mSerialConfig = serialConfig;
            initSerials();
        }

        public void addListener(IListener listener) {
            if (mListeners.contains(listener)) {
                return;
            }
            mListeners.add(listener);
        }

        public void removeListener(IListener listener) {
            if (mListeners.contains(listener)) {
                mListeners.remove(listener);
            }
        }

        public void clearListeners() {
            mListeners.clear();
        }

        public void sendCommand(String dev) {
            byte[] bytes = mPolicy.get(dev);
            sendCommand(dev, bytes);
        }

        /**
         * @param dev   串口文件驱动
         * @param aciss 字符串，需要转成 byte[] 下发到串口
         */
        public void sendCommand(String dev, String aciss) {
            byte[] bytes = ByteUtils.string2Hex(aciss);
            sendCommand(dev, bytes);
        }

        //发送指令
        public void sendCommand(String dev, byte[] bytes) {
            LogUtils.d(TAG, "sendCommand -------"
                    + "\n"
                    + "dev：" + dev
                    + "\n"
                    + "bytes："
                    + ByteUtils.bytes2HEX(bytes));
            if (TextUtils.isEmpty(dev)) {
                LogUtils.e(TAG, "please assign the serialPort driver path,just like dev/ttys1");
                return;
            }
            if (bytes == null || bytes.length <= 0) {
                LogUtils.d(TAG, "Don't send empty-data to serialPort");
                return;
            }
            if (!mServiceRunning) {
                LogUtils.d(TAG, "SerialService has interrupted!");
                return;
            }

            if (mSendThreadList == null || mSendThreadList.size() < 0) {
                LogUtils.d(TAG, "No SendThread to send data!");
                return;
            }
            for (SendThread sendThread : mSendThreadList) {
                if (TextUtils.equals(sendThread.getPath(), dev)) {
                    sendThread.sendCommand(bytes);
                    break;
                }
            }
        }
    }

    private class SendThread extends Thread {

        private Uart uart;
        private SerialPort serialPort;
        private OutputStream outputStream;

        private BlockingQueue<byte[]> mSendQueues;

        public String getPath() {
            return uart == null ? "" : uart.getPath();
        }

        public SendThread(Uart uart, SerialPort serialPort) {
            this.uart = uart;
            this.serialPort = serialPort;
            outputStream = serialPort.getOutputStream();
            mSendQueues = new ArrayBlockingQueue<>(DEFAULT_QUEUE_SIZE);
        }

        public void sendCommand(byte[] bytes) {
            if (mSendQueues.size() <= DEFAULT_QUEUE_SIZE - 1) {
                mSendQueues.add(bytes);
            }
        }

        @Override
        public void run() {
            super.run();
            LogUtils.d(TAG, "start SendThread!");
            while (mServiceRunning) {
                try {
                    byte[] bytes = mSendQueues.take();
                    if (bytes != null && bytes.length > 0) {
                        onSendCommandCallback(uart, bytes);
                        outputStream.write(bytes);
                    }
                    sleep(100);
                } catch (IOException e) {
                    e.printStackTrace();
                    LogUtils.e(TAG, e.getMessage());
                    release();
                    initThreads();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    LogUtils.e(TAG, e.getMessage());
                }
            }
        }


        @Override
        public void interrupt() {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (serialPort != null) {
                    serialPort.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            super.interrupt();
        }
    }

    private void onSendCommandCallback(Uart uart, byte[] bytes) {
        LogUtils.d(TAG, "onSendCommandCallback：" + uart
                + "\n"
                + "，send data：" + ByteUtils.bytes2HEX(bytes));
        if (uart != null && uart.getSendCommandCallback()) {
            for (IListener listener : mListeners) {
                if (listener instanceof IOnSendCommandListener) {
                    if (uart.getFormatRule() == Uart.RULE_ASCII) {
                        String ascii = ByteUtils.bytes2ASCII(bytes);
                        ((IOnSendCommandListener) listener).onSendCommand(uart.getPath(), ascii);
                    } else if (uart.getFormatRule() == Uart.RULE_HEX) {
                        String hex = ByteUtils.bytes2HEX(bytes);
                        ((IOnSendCommandListener) listener).onSendCommand(uart.getPath(), hex);
                    } else {
                        ((IOnSendCommandListener) listener).onSendCommand(uart.getPath(), Arrays.toString(bytes));
                    }
                }
            }
        }
    }

    private class ReadThread extends Thread {

        private Uart uart;
        private SerialPort serialPort;
        private InputStream inputStream;

        public ReadThread(Uart uart, SerialPort serialPort) {
            this.uart = uart;
            this.serialPort = serialPort;
            inputStream = serialPort.getInputStream();
        }


        @Override
        public void run() {
            LogUtils.d(TAG, "read thread run .");
            while (mServiceRunning) {
                try {
                    if (inputStream != null) {
                        byte[] buffer = new byte[128];
                        int size = inputStream.read(buffer);
                        if (size > 0) {
                            String parseCommand = mPolicy.parseCommand(uart, buffer, size);
                            if (TextUtils.isEmpty(parseCommand)) {
                                onReceiveCommandCallback(uart, parseCommand);
                            }
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    initSerials();
                }
            }
        }

        @Override
        public void interrupt() {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (serialPort != null) {
                    serialPort.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            super.interrupt();
        }
    }

    private void onReceiveCommandCallback(Uart uart, String msg) {
        LogUtils.d(TAG, "onReceiveCommandCallback：" + uart
                + "\n"
                + "，send data：" + msg);
        if (uart != null && uart.getSendCommandCallback()) {
            for (IListener listener : mListeners) {
                if (listener instanceof IOnReceiveCommandListener) {
                    ((IOnReceiveCommandListener) listener).onReceiveCommand(uart.getPath(), msg);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPolicy != null) {
            mPolicy.onDestroy();
        }
        release();
    }
}
